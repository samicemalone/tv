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
import uk.co.samicemalone.tv.util.AssertionUtil;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RemainingEpisodesInSeasonFromProgressSelectorTest extends FileSystemEnvironment {

    private RemainingEpisodesInSeasonFromProgressSelector selector;

    private TVDatabase tvdb;
    private ConnectionSource source;
    private Show show;
    private ShowProgress showProgress;

    @Before
    public void setUp() throws Exception {
        Arguments args = ArgsParser.parse(arg("Friends", "next-"));
        TV.ENV = WindowsEnvironment.isWindows() ? new WindowsEnvironment() : new UnixEnvironment();
        TV.ENV.setArguments(args);
        TVPath tvPath = new StandardTVLibrary(MockFileSystem.getSourceFolders());
        tvdb = new TVDatabase();
        selector = new RemainingEpisodesInSeasonFromProgressSelector(tvPath, tvdb);
        source = tvdb.connect(TVDatabase.IN_MEMORY_DATABASE);

        show = new Show("Friends");
        tvdb.createOrUpdateShow(show);
        showProgress = new ShowProgress(show, "", 2, 8);
        showProgress.setWatchedAt(Instant.now());
        tvdb.setShowProgress(null, showProgress.toEpisode());
    }

    @After
    public void tearDown() throws Exception {
        source.closeQuietly();
    }

    @Test
    public void getSelector() {
        List<String> episodeSelectors = Arrays.asList("next-", "cur-", "prev-");
        for (String episodeSelector : episodeSelectors) {
            assertTrue(episodeSelector.matches(selector.getSelector()));
        }
    }

    @Test
    public void findMatches() throws IOException, ExitException {
        List<EpisodeMatch> actualMatches = selector.findMatches();
        assertEquals(4, actualMatches.size());
        List<EpisodeMatch> expectedMatches = MockFileSystem.getRemainingSeasonEpisodeMatches("Friends", 2, 9);
        for (int i = 0; i < expectedMatches.size(); i++) {
            AssertionUtil.assertEpisodeMatchEqual(expectedMatches.get(i), actualMatches.get(i));
        }
    }

    @Test
    public void testPrevPointer() throws IOException, ExitException {
        TV.ENV.setArguments(ArgsParser.parse(arg("Friends", "prev-")));
        List<EpisodeMatch> actualMatches = selector.findMatches();
        assertEquals(6, actualMatches.size());
        List<EpisodeMatch> expectedMatches = MockFileSystem.getRemainingSeasonEpisodeMatches("Friends", 2, 7);
        for (int i = 0; i < expectedMatches.size(); i++) {
            AssertionUtil.assertEpisodeMatchEqual(expectedMatches.get(i), actualMatches.get(i));
        }
    }
}
