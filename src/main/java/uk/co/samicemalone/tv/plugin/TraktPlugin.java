package uk.co.samicemalone.tv.plugin;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.uwetrottmann.trakt5.entities.BaseShow;
import com.uwetrottmann.trakt5.entities.SearchResult;
import uk.co.samicemalone.libtv.model.EpisodeMatch;
import uk.co.samicemalone.tv.Application;
import uk.co.samicemalone.tv.ExitCode;
import uk.co.samicemalone.tv.action.Action;
import uk.co.samicemalone.tv.action.ActionListener;
import uk.co.samicemalone.tv.action.TraktMarkAction;
import uk.co.samicemalone.tv.exception.ExitException;
import uk.co.samicemalone.tv.exception.TraktException;
import uk.co.samicemalone.tv.model.Arguments;
import uk.co.samicemalone.tv.model.Episode;
import uk.co.samicemalone.tv.model.TraktAuthToken;
import uk.co.samicemalone.tv.selector.CurrentProgressProvider;
import uk.co.samicemalone.tv.selector.EpisodeProgressSelector;
import uk.co.samicemalone.tv.selector.EpisodeSelector;
import uk.co.samicemalone.tv.trakt.TraktClient;
import uk.co.samicemalone.tv.trakt.TraktUI;
import uk.co.samicemalone.tv.tvdb.TVDatabase;
import uk.co.samicemalone.tv.tvdb.model.Show;
import uk.co.samicemalone.tv.tvdb.model.ShowProgress;
import uk.co.samicemalone.tv.tvdb.model.TraktShowProgressQueue;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static uk.co.samicemalone.tv.TV.ENV;

public class TraktPlugin implements Plugin, CurrentProgressProvider, ActionListener {

    private final static Logger logger = LoggerFactory.getLogger(Application.class.getName());

    private TraktClient trakt;
    private TraktAuthToken authToken;
    private TVDatabase tvDatabase;
    private Dao<TraktShowProgressQueue, Integer> dao;

    public TraktPlugin(TVDatabase tvDatabase) {
        this.trakt = new TraktClient();
        this.tvDatabase = tvDatabase;
    }

    public void addEpisodeToQueue(Show show, Episode ep, String markType) throws TraktException {
        TraktShowProgressQueue queue = new TraktShowProgressQueue();
        queue.setShow(show);
        queue.setSeason(ep.getSeason());
        queue.setEpisode(ep.getEpisode());
        queue.setWatchedAt(new Date(ep.getWatchedAt().toEpochMilli()));
        queue.setMarkType(markType);
        try {
            dao.create(queue);
        } catch (SQLException e) {
            throw new TraktException("[tvdb] unable to create trakt show progress queue");
        }
    }

    private List<List<TraktShowProgressQueue>> groupQueueByContinuousType(List<TraktShowProgressQueue> queue) {
        List<List<TraktShowProgressQueue>> batchQueue = new ArrayList<>();
        List<TraktShowProgressQueue> batchQueueItem = new ArrayList<>();

        for (int i = 0; i < queue.size(); i++) {
            TraktShowProgressQueue queueItem = queue.get(i);
            if(i != 0) {
                TraktShowProgressQueue lastQueueItem = queue.get(i - 1);
                if(!queueItem.getMarkType().equals(lastQueueItem.getMarkType())) {
                    batchQueue.add(batchQueueItem);
                    batchQueueItem = new ArrayList<>();
                }
            }

            batchQueueItem.add(queueItem);

            // Add remaining items on the last run
            if(i == queue.size() - 1 && !batchQueueItem.isEmpty()) {
                batchQueue.add(batchQueueItem);
            }
        }
        return batchQueue;
    }

    private void processQueue() throws TraktException, IOException {
        try {
            Map<Integer, Show> showMapById = new HashMap<>();
            List<TraktShowProgressQueue> queue = dao.query(
                dao.queryBuilder().orderBy("watchedAt", true).prepare()
            );
            List<List<TraktShowProgressQueue>> batchQueue = groupQueueByContinuousType(queue);
            logger.debug("[trakt] progress queue batches = " + batchQueue.size());

            for (List<TraktShowProgressQueue> batchQueueItem : batchQueue) {
                logger.debug("[trakt] progress queue items = " + batchQueueItem.size());
                List<Episode> queueEpisodes = new ArrayList<>();

                for (TraktShowProgressQueue item : batchQueueItem) {
                    Show show = item.getShow();
                    showMapById.put(show.getId(), show);
                    Episode episode = item.toEpisode();
                    queueEpisodes.add(episode);
                    logger.info("[trakt] marking {} as seen {}", episode, item.getMarkType());
                    trakt.syncWatchedProgress(showMapById.values(), queueEpisodes);
                    logger.debug("[tvdb] deleting {} from the progress queue", episode);
                    dao.delete(queue);
                }
            }
        } catch (SQLException e) {
            logger.error("[trakt] unable to process the progress queue");
            logger.debug(e, "[trakt] [SQLException]");
        }
    }

    public TVDatabase getTVDatabase() {
        return tvDatabase;
    }

    @Override
    public void onLoad(Application app) {
        ConnectionSource source = tvDatabase.getConnectionSource();
        try {
            dao = DaoManager.createDao(source, TraktShowProgressQueue.class);

            TableUtils.createTableIfNotExists(source, TraktShowProgressQueue.class);
        } catch (SQLException e) {
            logger.error("[tvdb] unable to create trakt show progress queue table");
            ENV.setTraktEnabled(false);
            return;
        }

        authToken = trakt.authenticate(ENV.getTraktAuthFile());
        if(authToken == null) {
            logger.info("[trakt] auth token is null, disabling trakt...");
            ENV.setTraktEnabled(false);
            return;
        }

        if(app.getArguments().isTraktPointerSet()) {
            app.setCurrentProgressProvider(this);
        }

        Arguments args = app.getArguments();
        if(args.getMediaAction() == Action.PLAY && !args.isIgnoreSet()) {
            app.registerActionListener(this);
        }

        app.registerAction(new TraktMarkAction(this, Action.SEEN));
        app.registerAction(new TraktMarkAction(this, Action.UNSEEN));
    }

    @Override
    public void onActionExecuted(Show show, EpisodeSelector selector, List<EpisodeMatch> matches, ShowProgress currentProgress) throws ExitException {
        if(show.getTVDBId() == 0) {
            try {
                logger.info("[trakt] show tvdb id not found, searching trakt");
                SearchResult result = getShowSearchResult(show.getName());
                if(result == null) {
                    return;
                }
                show.setTraktIds(result.show.ids);
                show.setYear(result.show.year);
                logger.info("[tvdb] updating tvdb with trakt show ids");
                tvDatabase.createOrUpdateShow(show);
            } catch (TraktException e) {
                throw new ExitException(e.getMessage(), ExitCode.TRAKT_ERROR);
            } catch (SQLException e) {
                throw new ExitException("[tvdb] unable to update show with trakt ids", ExitCode.TVDB_ERROR);
            }
        }

        if(!matches.isEmpty() && selector instanceof EpisodeProgressSelector) {
            EpisodeProgressSelector progressSelector = (EpisodeProgressSelector) selector;
            Episode progressEpisode = progressSelector.getNewProgress(matches.get(0));
            try {
                if (ENV.isTraktUseCheckins()) {
                    logger.debug("[trakt] checking in " + progressEpisode);
                    trakt.checkinEpisode(show, progressEpisode);
                } else {
                    logger.debug("[trakt] marking {} as {}", progressEpisode, TraktShowProgressQueue.SEEN);
                    trakt.markEpisodeAs(show, progressEpisode, TraktClient.SEEN);
                }
            } catch (TraktException ex) {
                try {
                    logger.info("[trakt] unable to set progress, adding episode to queue");
                    logger.debug("[trakt] [exception] " + ex.getMessage());
                    addEpisodeToQueue(show, progressEpisode, TraktShowProgressQueue.SEEN);
                } catch (TraktException e) {
                    logger.error("[trakt] unable to add trakt watched progress to the queue");
                }
            }
        }
    }

    @Override
    public void onUnload(Application app) {
        if(ENV.isTraktEnabled()) {
            try {
                processQueue();
            } catch (TraktException e) {
                logger.error(e.getMessage());
            } catch (IOException e) {
                logger.error("[trakt] [IOException] unable to process the progress queue");
            }
        }
    }

    private SearchResult getShowSearchResult(String showName) throws TraktException {
        Pattern showYearInTitle = Pattern.compile("(.*) \\(\\d{4}\\)");
        Matcher m = showYearInTitle.matcher(showName);
        if(m.find()) {
            showName = m.group(1);
        }
        List<SearchResult> searchResults = trakt.searchShow(showName);
        if(searchResults.size() == 1) {
            return searchResults.get(0);
        }
        return TraktUI.readShowSearchResult(showName, searchResults);
    }

    @Override
    public ShowProgress getCurrentProgress(Show show, String tag) throws ExitException {
        try {
            logger.info("[trakt] get watched progress");
            BaseShow watchedProgress = trakt.getWatchedProgress(show);
            com.uwetrottmann.trakt5.entities.Episode currentEpisode = watchedProgress.last_episode;
            if(currentEpisode == null) {
                throw new ExitException("[trakt] there is no next episode for this show", ExitCode.EPISODES_NOT_FOUND);
            }
            return new ShowProgress(show, tag, currentEpisode.season, currentEpisode.number);
        } catch (TraktException e) {
            throw new ExitException(e.getMessage(), ExitCode.TRAKT_ERROR);
        }
    }
}
