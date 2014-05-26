/*
 * Copyright (c) 2014, Sam Malone. All rights reserved.
 *
 * Redistribution and use of this software in source and binary forms, with or
 * without modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *  - Neither the name of Sam Malone nor the names of its contributors may be
 *    used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package tv.matcher;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import tv.FileSystemEnvironment;
import tv.MockFileSystem;
import tv.TVScan;
import tv.exception.SeasonNotFoundException;
import tv.model.EpisodeMatch;
import tv.model.EpisodeRange;
import tv.model.Range;
import tv.model.Season;

/**
 *
 * @author Sam Malone
 */
public class TVMatcherTest extends FileSystemEnvironment {
    
    private TVMatcher tvMatcher;
    
    @Before
    public void setUp() {
        tvMatcher = new TVMatcher(new TVScan(MockFileSystem.getSourceFolders()));
    }
    
    @After
    public void tearDown() {
        tvMatcher = null;
    }
    
    public static void assertEpisodeMatchEquals(EpisodeMatch expResult, EpisodeMatch result) {
        assertEquals(expResult.getSeason(), result.getSeason());
        assertEquals(expResult.getEpisodes(), result.getEpisodes());
        assertEquals(expResult.getEpisodeFile(), result.getEpisodeFile());
    }
    
    public static void assertSeasonEquals(Season expResult, Season result) {
        assertEquals(expResult.asInt(), result.asInt());
        assertEquals(expResult.getDir(), result.getDir());
    }

    /**
     * Test of matchEpisode method, of class TVMatcher.
     * @throws tv.exception.SeasonNotFoundException
     */
    @Test(expected = SeasonNotFoundException.class)
    public void testMatchEpisodeThrow() throws SeasonNotFoundException {
        tvMatcher.matchEpisode("Scrubs", 0, 1);
    }

    /**
     * Test of matchEpisode method, of class TVMatcher.
     */
    @Test
    public void testMatchEpisode() {
        String show = "Scrubs";
        int s = 1;
        Season season = new Season(s, MockFileSystem.getSeasonDir(show, s));
        File expFile = MockFileSystem.getEpisodeFile(show, s, 4);
        EpisodeMatch expResult = new EpisodeMatch(expFile, s, 4);
        EpisodeMatch result = tvMatcher.matchEpisode(season, 4);
        assertEpisodeMatchEquals(expResult, result);
    }

    /**
     * Test of matchLargestSeasonEpisode method, of class TVMatcher.
     * @throws tv.exception.SeasonNotFoundException
     */
    @Test(expected = SeasonNotFoundException.class)
    public void testMatchLargestEpisodeThrow() throws SeasonNotFoundException {
        tvMatcher.matchLargestEpisode("Scrubs", 0);
    }

    /**
     * Test of matchLargestSeasonEpisode method, of class TVMatcher.
     */
    @Test
    public void testMatchLargestEpisode() {
        String show = "Scrubs";
        int s = 1;
        Season season = new Season(s, MockFileSystem.getSeasonDir(show, s));
        File expFile = MockFileSystem.getEpisodeFile(show, s, MockFileSystem.NUM_EPISODES);
        EpisodeMatch expResult = new EpisodeMatch(expFile, s, MockFileSystem.NUM_EPISODES);
        EpisodeMatch result = tvMatcher.matchLargestEpisode(season);
        assertEpisodeMatchEquals(expResult, result);
    }

    /**
     * Test of matchLatestEpisode method, of class TVMatcher.
     * @throws tv.exception.SeasonNotFoundException
     */
    @Test
    public void testMatchLatestEpisode() throws SeasonNotFoundException {
        String show = "Scrubs";
        int season = MockFileSystem.NUM_SEASONS;
        int episode = MockFileSystem.NUM_EPISODES;
        File expFile = MockFileSystem.getEpisodeFile(show, season, episode);
        EpisodeMatch result = tvMatcher.matchLatestEpisode(show);
        assertEquals(season, result.getSeason());
        assertEquals(expFile, result.getEpisodeFile());
        assertTrue(result.isEpisodeNo(episode));
    }

    /**
     * Test of matchSeason method, of class TVMatcher.
     * @throws tv.exception.SeasonNotFoundException
     */
    @Test
    public void testMatchSeason() throws SeasonNotFoundException {
        String show = "Scrubs";
        int season = 1;
        List<EpisodeMatch> result = tvMatcher.matchSeason(show, season);
        assertEquals(MockFileSystem.NUM_EPISODES, result.size());
        for(int i = 0; i < MockFileSystem.NUM_EPISODES; i++) {
            assertEquals(season, result.get(i).getSeason());
            assertTrue(result.get(i).isEpisodeNo(i+1));
            assertEquals(MockFileSystem.getEpisodeFile(show, season, i+1), result.get(i).getEpisodeFile());
        }
    }

    /**
     * Test of getSeason method, of class TVMatcher.
     */
    @Test
    public void testGetSeason() {
        String show = "Scrubs";
        int season = 1;
        Season result = tvMatcher.getSeason(show, season);
        assertSeasonEquals(new Season(season, MockFileSystem.getSeasonDir(show, season)), result);
    }

    /**
     * Test of matchSeasonRange method, of class TVMatcher.
     */
    @Test
    public void testMatchSeasonRange() {
        String show = "Scrubs";
        List<EpisodeMatch> expResult = MockFileSystem.getFullSeasonEpisodeMatches(show, 2, 3);
        List<EpisodeMatch> result = tvMatcher.matchSeasonRange(show, new Range(2, 3));
        assertEquals(expResult.size(), result.size());
        for(int i = 0; i < result.size(); i++) {
            assertEpisodeMatchEquals(expResult.get(i), result.get(i));
        }
    }

    /**
     * Test of matchSeasonsFrom method, of class TVMatcher.
     */
    @Test
    public void testMatchSeasonsFrom() {
        String show = "Scrubs";
        List<EpisodeMatch> expResult = MockFileSystem.getFullSeasonEpisodeMatches(show, 2, MockFileSystem.NUM_SEASONS);
        List<EpisodeMatch> result = tvMatcher.matchSeasonsFrom(show, 2);
        assertEquals(expResult.size(), result.size());
        for(int i = 0; i < result.size(); i++) {
            assertEpisodeMatchEquals(expResult.get(i), result.get(i));
        }
    }

    /**
     * Test of matchSeasons method, of class TVMatcher.
     */
    @Test
    public void testMatchSeasons() {
        String show = "Scrubs";
        File showDir = MockFileSystem.getShowDir(show);
        List<Season> expResult = new ArrayList<>(MockFileSystem.NUM_SEASONS);
        for(int i = 1; i <= MockFileSystem.NUM_SEASONS; i++) {
            expResult.add(new Season(i, MockFileSystem.getSeasonDir(show, i)));
        }
        List<Season> result = tvMatcher.matchSeasons(showDir);
        assertEquals(expResult.size(), result.size());
        for(int i = 0; i < result.size(); i++) {
            assertSeasonEquals(expResult.get(i), result.get(i));
        }
    }

    /**
     * Test of matchLargestSeasonSeason method, of class TVMatcher.
     */
    @Test
    public void testMatchLargestSeason() {
        String show = "Scrubs";
        int s = MockFileSystem.NUM_SEASONS;
        List<EpisodeMatch> expResult = MockFileSystem.getFullSeasonEpisodeMatches(show, s, s);
        List<EpisodeMatch> result = tvMatcher.matchLargestSeason(show);
        assertEquals(expResult.size(), result.size());
        for(int i = 0; i < result.size(); i++) {
            assertEpisodeMatchEquals(expResult.get(i), result.get(i));
        }
    }

    /**
     * Test of matchLargestSeason method, of class TVMatcher.
     */
    @Test
    public void testMatchLargest() {
        String show = "Scrubs";
        File showDir = MockFileSystem.getShowDir(show);
        int season = MockFileSystem.NUM_SEASONS;
        Season expResult = new Season(season, MockFileSystem.getSeasonDir(show, season));
        Season result = tvMatcher.matchLargestSeason(tvMatcher.matchSeasons(showDir));
        assertSeasonEquals(expResult, result);
    }

    /**
     * Test of matchEpisodesFrom method, of class TVMatcher.
     * @throws tv.exception.SeasonNotFoundException
     */
    @Test
    public void testMatchEpisodesFrom() throws SeasonNotFoundException {
        String show = "Scrubs";
        int season = 1;
        int episode = 9;
        List<EpisodeMatch> expResult = new ArrayList<>();
        expResult.add(MockFileSystem.getEpisodeMatch(show, 1, 9));
        expResult.add(MockFileSystem.getEpisodeMatch(show, 1, 10));
        expResult.add(MockFileSystem.getEpisodeMatch(show, 1, 11));
        expResult.add(MockFileSystem.getEpisodeMatch(show, 1, 12));
        List<EpisodeMatch> result = tvMatcher.matchEpisodesFrom(show, season, episode);
        assertEquals(expResult.size(), result.size());
        for(int i = 0; i < result.size(); i++) {
            assertEpisodeMatchEquals(expResult.get(i), result.get(i));
        }
    }

    /**
     * Test of matchEpisodeRange method, of class TVMatcher.
     * @throws tv.exception.SeasonNotFoundException
     */
    @Test
    public void testMatchEpisodeRange() throws SeasonNotFoundException {
        String show = "Scrubs";
        EpisodeRange range = new EpisodeRange(1, 10, 2, 2);
        List<EpisodeMatch> expResult = new ArrayList<>();
        expResult.add(MockFileSystem.getEpisodeMatch(show, 1, 10));
        expResult.add(MockFileSystem.getEpisodeMatch(show, 1, 11));
        expResult.add(MockFileSystem.getEpisodeMatch(show, 1, 12));
        expResult.add(MockFileSystem.getEpisodeMatch(show, 2, 1));
        expResult.add(MockFileSystem.getEpisodeMatch(show, 2, 2));
        List<EpisodeMatch> result = tvMatcher.matchEpisodeRange(show, range);
        assertEquals(expResult.size(), result.size());
        for(int i = 0; i < result.size(); i++) {
            assertEpisodeMatchEquals(expResult.get(i), result.get(i));
        }
    }

    /**
     * Test of matchAllEpisodes method, of class TVMatcher.
     * @throws tv.exception.SeasonNotFoundException
     */
    @Test
    public void testMatchAllEpisodes() throws SeasonNotFoundException {
        String show = "Scrubs";
        List<EpisodeMatch> expResult = MockFileSystem.getFullSeasonEpisodeMatches(show, 1, MockFileSystem.NUM_SEASONS);
        List<EpisodeMatch> result = tvMatcher.matchAllEpisodes(show);
        assertEquals(expResult.size(), result.size());
        for(int i = 0; i < result.size(); i++) {
            assertEpisodeMatchEquals(expResult.get(i), result.get(i));
        }
    }
    
    /**
     * Test of SEASON_PATTERN field, of class TVMatch.
     */
    @Test
    public void testAccept() {
        String[] seasonDirs = new String[] {
            "Season 0",
            "Season 1",
            "Season 01",
            "Series 1",
            "Series 01",
        };
        for(String seasonDir : seasonDirs) {
            assertTrue(TVMatcher.SEASON_PATTERN.matcher(seasonDir).find());
        }
    }
    
}
