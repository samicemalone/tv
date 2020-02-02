package uk.co.samicemalone.tv.action;

import uk.co.samicemalone.libtv.model.EpisodeMatch;
import uk.co.samicemalone.tv.exception.ExitException;
import uk.co.samicemalone.tv.selector.EpisodeSelector;
import uk.co.samicemalone.tv.tvdb.model.Show;
import uk.co.samicemalone.tv.tvdb.model.ShowProgress;

import java.util.List;

public interface ActionListener {
    void onActionExecuted(Show show, EpisodeSelector selector, List<EpisodeMatch> matches, ShowProgress currentProgress) throws ExitException;
}
