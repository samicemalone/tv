package uk.co.samicemalone.tv.selector;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.co.samicemalone.libtv.matcher.path.StandardTVLibrary;
import uk.co.samicemalone.libtv.matcher.path.TVPath;
import uk.co.samicemalone.libtv.model.EpisodeMatch;
import uk.co.samicemalone.tv.FileSystemEnvironment;
import uk.co.samicemalone.tv.MockFileSystem;
import uk.co.samicemalone.tv.TV;
import uk.co.samicemalone.tv.model.Arguments;
import uk.co.samicemalone.tv.options.ArgsParser;
import uk.co.samicemalone.tv.options.UnixEnvironment;
import uk.co.samicemalone.tv.options.WindowsEnvironment;
import uk.co.samicemalone.tv.util.AssertionUtil;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RemainingEpisodesInSeasonSelectorTest extends FileSystemEnvironment {

    private RemainingEpisodesInSeasonSelector selector;

    @Before
    public void setUp() throws Exception {
        Arguments args = ArgsParser.parse(arg("Scrubs", "s02e06-"));
        TVPath tvPath = new StandardTVLibrary(MockFileSystem.getSourceFolders());
        selector = new RemainingEpisodesInSeasonSelector(tvPath);

        TV.ENV = WindowsEnvironment.isWindows() ? new WindowsEnvironment() : new UnixEnvironment();
        TV.ENV.setArguments(args);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void getSelector() {
        assertTrue("s02e06-".matches(selector.getSelector()));
    }

    @Test
    public void findMatches() throws IOException {
        List<EpisodeMatch> actualMatches = selector.findMatches();
        assertEquals(7, actualMatches.size());
        List<EpisodeMatch> expectedMatches = MockFileSystem.getRemainingSeasonEpisodeMatches("Scrubs", 2, 6);
        for(int i = 0; i < expectedMatches.size(); i++) {
            EpisodeMatch expectedMatch = expectedMatches.get(i);
            EpisodeMatch actualMatch = actualMatches.get(i);
            AssertionUtil.assertEpisodeMatchEqual(expectedMatch, actualMatch);
        }
    }
}
