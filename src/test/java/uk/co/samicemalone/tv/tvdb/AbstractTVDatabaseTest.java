package uk.co.samicemalone.tv.tvdb;

import com.j256.ormlite.support.ConnectionSource;
import org.junit.After;
import org.junit.Before;
import uk.co.samicemalone.tv.TV;
import uk.co.samicemalone.tv.options.UnixEnvironment;
import uk.co.samicemalone.tv.options.WindowsEnvironment;

public abstract class AbstractTVDatabaseTest {
    protected TVDatabase tvdb;
    protected ConnectionSource source;

    @Before
    public void setUp() throws Exception {
        tvdb = new TVDatabase();
        source = tvdb.connect(TVDatabase.IN_MEMORY_DATABASE);

        TV.ENV = WindowsEnvironment.isWindows() ? new WindowsEnvironment() : new UnixEnvironment();
        TV.ENV.setTVDB(TVDatabase.IN_MEMORY_DATABASE);
    }

    @After
    public void tearDown() throws Exception {
        source.closeQuietly();
    }
}
