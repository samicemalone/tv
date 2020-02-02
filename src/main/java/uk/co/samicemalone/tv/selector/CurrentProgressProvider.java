package uk.co.samicemalone.tv.selector;

import uk.co.samicemalone.tv.exception.ExitException;
import uk.co.samicemalone.tv.tvdb.model.Show;
import uk.co.samicemalone.tv.tvdb.model.ShowProgress;

public interface CurrentProgressProvider {
    ShowProgress getCurrentProgress(Show show, String tag) throws ExitException;
}
