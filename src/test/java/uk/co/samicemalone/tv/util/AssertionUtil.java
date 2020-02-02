package uk.co.samicemalone.tv.util;

import uk.co.samicemalone.libtv.model.EpisodeMatch;

import static org.junit.Assert.assertEquals;

public class AssertionUtil {
    public static void assertEpisodeMatchEqual(EpisodeMatch expectedMatch, EpisodeMatch actualMatch) {
        assertEquals(expectedMatch.getShow(), actualMatch.getShow());
        assertEquals(expectedMatch.getSeason(), actualMatch.getSeason());
        assertEquals(expectedMatch.getEpisode(), actualMatch.getEpisode());
        assertEquals(expectedMatch.getEpisodeFile(), actualMatch.getEpisodeFile());
    }
}
