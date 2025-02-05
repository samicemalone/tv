package uk.co.samicemalone.tv.selector;

import com.j256.ormlite.support.ConnectionSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.co.samicemalone.libtv.matcher.path.StandardTVLibrary;
import uk.co.samicemalone.libtv.matcher.path.TVPath;
import uk.co.samicemalone.libtv.model.EpisodeMatch;
import uk.co.samicemalone.tv.FileSystemEnvironment;
import uk.co.samicemalone.tv.MockFileSystem;
import uk.co.samicemalone.tv.TV;
import uk.co.samicemalone.tv.exception.ExitException;
import uk.co.samicemalone.tv.model.Arguments;
import uk.co.samicemalone.tv.options.ArgsParser;
import uk.co.samicemalone.tv.options.UnixEnvironment;
import uk.co.samicemalone.tv.options.WindowsEnvironment;
import uk.co.samicemalone.tv.tvdb.TVDatabase;
import uk.co.samicemalone.tv.tvdb.model.Show;
import uk.co.samicemalone.tv.tvdb.model.ShowProgress;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EpisodeNavigatorProgressSelectorTest extends FileSystemEnvironment {

    private EpisodeNavigatorProgressSelector selector;

    private TVDatabase tvdb;
    private ConnectionSource source;
    private Show show;
    private ShowProgress showProgress;

    @Before
    public void setUp() throws Exception {
        Arguments args = ArgsParser.parse(arg("Friends", "next"));
        TV.ENV = WindowsEnvironment.isWindows() ? new WindowsEnvironment() : new UnixEnvironment();
        TV.ENV.setArguments(args);
        TVPath tvPath = new StandardTVLibrary(MockFileSystem.getSourceFolders());
        tvdb = new TVDatabase();
        selector = new EpisodeNavigatorProgressSelector(tvPath, tvdb);
        source = tvdb.connect(TVDatabase.IN_MEMORY_DATABASE);

        show = new Show("Friends");
        tvdb.createOrUpdateShow(show);
        showProgress = new ShowProgress(show, "", 1, 1);
        showProgress.setWatchedAt(Instant.now());
        tvdb.setShowProgress(null, showProgress.toEpisode());
    }

    @After
    public void tearDown() throws Exception {
        source.closeQuietly();
    }

    @Test
    public void getSelector() {
        List<String> episodeSelectors = Arrays.asList("next", "cur", "prev");
        for (String episodeSelector : episodeSelectors) {
            assertTrue(episodeSelector.matches(selector.getSelector()));
        }
    }

    @Test
    public void findMatches() throws IOException, ExitException {
        List<EpisodeMatch> actualMatches = selector.findMatches();
        int episodeNo = 2;
        assertEquals(1, actualMatches.size());
        EpisodeMatch expectedMatch = MockFileSystem.getEpisodeMatch("Friends", 1, episodeNo);
        EpisodeMatch actualMatch = actualMatches.get(0);
        assertEquals(expectedMatch.getSeason(), actualMatch.getSeason());
        assertEquals(expectedMatch.getEpisode(), actualMatch.getEpisode());
    }
}
