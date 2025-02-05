package uk.co.samicemalone.tv;

import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.support.ConnectionSource;
import uk.co.samicemalone.libtv.matcher.path.StandardTVLibrary;
import uk.co.samicemalone.libtv.matcher.path.TVPath;
import uk.co.samicemalone.libtv.model.EpisodeMatch;
import uk.co.samicemalone.tv.action.Action;
import uk.co.samicemalone.tv.action.ActionListener;
import uk.co.samicemalone.tv.exception.ExitException;
import uk.co.samicemalone.tv.filter.RandomFilter;
import uk.co.samicemalone.tv.model.Arguments;
import uk.co.samicemalone.tv.model.Episode;
import uk.co.samicemalone.tv.plugin.Plugin;
import uk.co.samicemalone.tv.plugin.PointerPlugin;
import uk.co.samicemalone.tv.plugin.TraktPlugin;
import uk.co.samicemalone.tv.selector.CurrentProgressProvider;
import uk.co.samicemalone.tv.selector.EpisodeSelector;
import uk.co.samicemalone.tv.tvdb.TVDatabase;
import uk.co.samicemalone.tv.tvdb.model.Show;
import uk.co.samicemalone.tv.tvdb.model.ShowProgress;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class Application implements Lifecycle {

    private final static Logger logger = LoggerFactory.getLogger(Application.class.getName());

    private List<Plugin> pluginList;
    private List<ActionListener> actionListeners;
    private List<Action> actions;
    private Queue<EpisodeSelector> episodeSelectors;
    private CurrentProgressProvider currentProgressProvider;
    private TVDatabase tvDatabase;
    private Arguments args;
    private TVPath tvPath;

    public Application(TVDatabase tvDatabase) {
        this.pluginList = new ArrayList<>();
        this.actionListeners = new ArrayList<>();
        this.actions = new ArrayList<>();
        this.episodeSelectors = new PriorityQueue<>();
        this.tvDatabase = tvDatabase;
        this.currentProgressProvider = null;
    }

    private Show getShow() throws SQLException {
        Show show = tvDatabase.getShowByName(args.getShow());
        if(show == null) {
            show = new Show(args.getShow());
            tvDatabase.createOrUpdateShow(show);
        }
        return show;
    }

    public EpisodeSelector getEpisodeSelector() throws ExitException {
        EpisodeSelector episodeSelector = EpisodeSelector.findEpisodeSelector(episodeSelectors, args.getEpisodes());
        if(episodeSelector == null) {
            throw new ExitException("Episode Selector Not Found", ExitCode.PARSE_EPISODES_FAILED);
        }
        return episodeSelector;
    }

    public TVPath getTVPath() {
        return tvPath;
    }

    public Arguments getArguments() {
        return args;
    }

    private ShowProgress getCurrentProgress(EpisodeSelector selector, Show show, String tag) throws ExitException {
        if(currentProgressProvider != null) {
            return currentProgressProvider.getCurrentProgress(show, tag);
        }
        return selector.getCurrentProgress(show, tag);
    }

    private Action getMediaAction() throws ExitException {
        for(Action action : actions) {
            if(action.isAction(args.getMediaAction())) {
                return action;
            }
        }
        throw new ExitException("[action] invalid action type", ExitCode.UNEXPECTED_ARGUMENT);
    }

    @Override
    public void onLoad() {
        tvPath = new StandardTVLibrary(args.getSourceFolders());

        registerPlugin(new PointerPlugin(tvDatabase));
        if(TV.ENV.isTraktEnabled()) {
            registerPlugin(new TraktPlugin(tvDatabase));
        }

        EpisodeSelector.defaultSelectors(tvPath).forEach(this::registerEpisodeSelector);
        Action.defaultActions().forEach(this::registerAction);

        pluginList.forEach(plugin -> plugin.onLoad(this));
    }

    public void registerPlugin(Plugin plugin) {
        pluginList.add(plugin);
    }

    public void registerEpisodeSelector(EpisodeSelector episodeSelector) {
        episodeSelectors.add(episodeSelector);
    }

    public void registerAction(Action action) {
        actions.add(action);
    }

    public void registerActionListener(ActionListener listener) {
        actionListeners.add(listener);
    }

    public void setCurrentProgressProvider(CurrentProgressProvider provider) {
        currentProgressProvider = provider;
    }

    public void run(Arguments args) throws Exception {
        this.args = args;
        try (ConnectionSource connection = tvDatabase.connect(TV.ENV.getTVDB())) {
            onLoad();

            Show show = getShow();
            EpisodeSelector selector = getEpisodeSelector();
            logger.debug("[app] episode selector = {}", selector.getClass().getName());

            ShowProgress currentProgress = getCurrentProgress(selector, show, args.getUser());
            Episode currentProgressEpisode = currentProgress == null ? null : currentProgress.toEpisode();
            logger.debug("[app] current progress = {}", currentProgressEpisode);

            List<EpisodeMatch> matches = selector.findMatchesOrThrow();
            if(args.getRandomCount() > 0) {
                matches = RandomFilter.filter(matches);
            }

            logger.debug("[app] executing media action");
            getMediaAction().execute(show, matches);

            for(ActionListener listener : actionListeners) {
                logger.debug("[app] calling action listener {}", listener.getClass().getName());
                listener.onActionExecuted(show, selector, matches, currentProgress);
            }

            for (Plugin plugin : pluginList) {
                logger.debug("[app] unloading plugin {}", plugin.getClass().getName());
                plugin.onUnload(this);
            }
            logger.debug("[app] exiting");
        }
    }
}
