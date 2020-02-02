package uk.co.samicemalone.tv.action;

import uk.co.samicemalone.tv.exception.ExitException;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public interface FileAction {
    static List<FileAction> defaultFileActions() {
        return Arrays.asList(
            new MediaPlayerAction(Action.PLAY),
            new MediaPlayerAction(Action.ENQUEUE),
            new ListAction(),
            new ListAction(Action.LIST_PATH),
            new CountAction(),
            new SizeAction(),
            new LengthAction()
        );
    }

    boolean isAction(int action);

    void execute(File file) throws ExitException;
}
