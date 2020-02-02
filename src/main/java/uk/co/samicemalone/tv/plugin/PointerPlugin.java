package uk.co.samicemalone.tv.plugin;

import uk.co.samicemalone.libtv.model.EpisodeMatch;
import uk.co.samicemalone.tv.Application;
import uk.co.samicemalone.tv.action.Action;
import uk.co.samicemalone.tv.action.ActionListener;
import uk.co.samicemalone.tv.exception.ExitException;
import uk.co.samicemalone.tv.model.Arguments;
import uk.co.samicemalone.tv.model.Episode;
import uk.co.samicemalone.tv.selector.EpisodeNavigatorProgressSelector;
import uk.co.samicemalone.tv.selector.EpisodeProgressSelector;
import uk.co.samicemalone.tv.selector.EpisodeSelector;
import uk.co.samicemalone.tv.selector.LatestEpisodeProgressSelector;
import uk.co.samicemalone.tv.selector.PilotEpisodeProgressSelector;
import uk.co.samicemalone.tv.selector.RemainingEpisodesInSeasonFromProgressSelector;
import uk.co.samicemalone.tv.selector.SingleEpisodeProgressSelector;
import uk.co.samicemalone.tv.tvdb.TVDatabase;
import uk.co.samicemalone.tv.tvdb.model.Show;
import uk.co.samicemalone.tv.tvdb.model.ShowProgress;

import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

public class PointerPlugin implements Plugin, ActionListener {
    private TVDatabase tvDatabase;

    public PointerPlugin(TVDatabase tvDatabase) {
        this.tvDatabase = tvDatabase;
    }

    @Override
    public void onLoad(Application app) {
        app.registerEpisodeSelector(new SingleEpisodeProgressSelector(app.getTVPath(), tvDatabase));
        app.registerEpisodeSelector(new PilotEpisodeProgressSelector(app.getTVPath(), tvDatabase));
        app.registerEpisodeSelector(new LatestEpisodeProgressSelector(app.getTVPath(), tvDatabase));
        app.registerEpisodeSelector(new EpisodeNavigatorProgressSelector(app.getTVPath(), tvDatabase));
        app.registerEpisodeSelector(new RemainingEpisodesInSeasonFromProgressSelector(app.getTVPath(), tvDatabase));

        Arguments args = app.getArguments();
        if(args.getMediaAction() == Action.PLAY && !args.isIgnoreSet()) {
            app.registerActionListener(this);
        }
    }

    @Override
    public void onActionExecuted(Show show, EpisodeSelector selector, List<EpisodeMatch> matches, ShowProgress currentProgress) throws ExitException {
        if(!matches.isEmpty() && selector instanceof EpisodeProgressSelector) {
            EpisodeProgressSelector episodeSelector = (EpisodeProgressSelector) selector;
            Episode pointer = episodeSelector.getNewProgress(matches.get(0));
            if(pointer != null) {
                try {
                    pointer.setWatchedAt(Instant.now());
                    tvDatabase.setShowProgress(currentProgress, pointer);
                } catch (SQLException ex) {
                    System.err.format("[tvdb] unable to set current progress (%s)\n", pointer);
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onUnload(Application app) {

    }
}
