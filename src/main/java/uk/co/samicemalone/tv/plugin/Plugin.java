package uk.co.samicemalone.tv.plugin;

import uk.co.samicemalone.tv.Application;

public interface Plugin {

    void onLoad(Application app);

    void onUnload(Application app);

}
