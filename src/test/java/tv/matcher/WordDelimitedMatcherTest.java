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

import tv.matcher.WordDelimitedMatcher;
import tv.matcher.EpisodeFileMatcher;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import tv.model.EpisodeMatch;

/**
 *
 * @author Sam Malone
 */
public class WordDelimitedMatcherTest {
    
    private final String path = "C:\\TV\\The League\\Season 1\\";

    /**
     * Test of match method, of class XDelimitedMatcher.
     */
    @Test
    public void testMatch() {
        String[] fileNames = new String[] {
            "the.league ep2 name.mkv",
            "the.league ep02 name.mkv",
            "the.league.ep.02.name.mkv",
        };
        WordDelimitedMatcher instance = new WordDelimitedMatcher();
        for(String fileName : fileNames) {
            EpisodeMatch expResult = new EpisodeMatch(1, 2);
            EpisodeMatch result = instance.match(path + fileName, EpisodeFileMatcher.stripCommonTags(fileName));
            assertEquals(expResult.getSeason(), result.getSeason());
            assertEquals(expResult.getEpisodes(), result.getEpisodes());
        }
    }

    /**
     * Test of match method, of class XDelimitedMatcher.
     */
    @Test
    public void testMatchWithSeason() {
        String[] fileNames = new String[] {
            "the league season 1 ep 2 name.mkv",
            "the.league.season.1.ep02.name.mkv",
            "the league season 1 episode 2 name.mkv",
        };
        WordDelimitedMatcher instance = new WordDelimitedMatcher();
        for(String fileName : fileNames) {
            EpisodeMatch expResult = new EpisodeMatch(1, 2);
            EpisodeMatch result = instance.match(path + fileName, EpisodeFileMatcher.stripCommonTags(fileName));
            assertEquals(expResult.getSeason(), result.getSeason());
            assertEquals(expResult.getEpisodes(), result.getEpisodes());
        }
    }

    /**
     * Test of match method, of class XDelimitedMatcher.
     */
    @Test
    public void testMatchDouble() {
        String[] fileNames = new String[] {
            "the.league ep01ep02 pilot.mkv",
            "the.league ep1ep2 pilot.mkv",
            "the_league_ep01_ep02_pilot.mkv",
            "the_league_ep1_ep2_pilot.mkv",
            "the.league.ep_01_ep_02.pilot.mkv",
            "the.league.ep_1_ep_2.pilot.mkv",
            "the.league.ep_01_name_ep_02_name.mkv",
        };
        WordDelimitedMatcher instance = new WordDelimitedMatcher();
        for(String fileName : fileNames) {
            EpisodeMatch expResult = new EpisodeMatch(1, 1);
            expResult.getEpisodes().add(2);
            EpisodeMatch result = instance.match(path + fileName, EpisodeFileMatcher.stripCommonTags(fileName));
            assertEquals(expResult.getSeason(), result.getSeason());
            assertEquals(expResult.getEpisodes(), result.getEpisodes());
        }
    }

    /**
     * Test of match method, of class XDelimitedMatcher.
     */
    @Test
    public void testMatchDoubleWithSeason() {
        String[] fileNames = new String[] {
            "the league season 1 ep 2 ep 3 name.mkv",
            "the league season1 ep2 ep3 name.mkv",
            "the league season1ep2ep3 name.mkv",
            "the.league.season.1.ep02.ep03.name.mkv",
            "the league season 1 episode 2 episode 3 name.mkv",
            "the league season 01 episode 02 episode 03 name.mkv",
            "the_league_season_1_episode_02_name_episode_03_name.mkv",
            "the_league_season_1_ep_02_name_ep_03_name.mkv",
            "the league season 1 ep 2 name ep 3 name.mkv",
        };
        WordDelimitedMatcher instance = new WordDelimitedMatcher();
        for(String fileName : fileNames) {
            EpisodeMatch expResult = new EpisodeMatch(1, 2);
            expResult.getEpisodes().add(3);
            EpisodeMatch result = instance.match(path + fileName, EpisodeFileMatcher.stripCommonTags(fileName));
            assertEquals(expResult.getSeason(), result.getSeason());
            assertEquals(expResult.getEpisodes(), result.getEpisodes());
        }
    }

    /**
     * Test of match method, of class XDelimitedMatcher.
     */
    @Test
    public void testMatchQuad() {
        String[] fileNames = new String[] {
            "the.league ep01ep02ep03ep04 pilot.mkv",
            "the.league ep1ep2ep3ep4 pilot.mkv",
            "the_league_ep01_ep02_ep03_ep04_pilot.mkv",
            "the_league_ep1_ep2_ep3_ep4_pilot.mkv",
            "the.league.ep_01_ep_02_ep_03_ep_04.pilot.mkv",
            "the.league.ep_01_name_ep_02_name_ep_03_name_ep_04_name.mkv",
            "the.league.ep_1_ep_2_ep_3_ep_4.pilot.mkv",
        };
        WordDelimitedMatcher instance = new WordDelimitedMatcher();
        for(String fileName : fileNames) {
            EpisodeMatch expResult = new EpisodeMatch(1, 1);
            expResult.getEpisodes().add(2);
            expResult.getEpisodes().add(3);
            expResult.getEpisodes().add(4);
            EpisodeMatch result = instance.match(path + fileName, EpisodeFileMatcher.stripCommonTags(fileName));
            assertEquals(expResult.getSeason(), result.getSeason());
            assertEquals(expResult.getEpisodes(), result.getEpisodes());
        }
    }

    /**
     * Test of match method, of class XDelimitedMatcher.
     */
    @Test
    public void testMatchQuadWithSeason() {
        String[] fileNames = new String[] {
            "the league season 1 ep 2 ep 3 ep 4 ep 5 name.mkv",
            "the league season1 ep2 ep3 ep4 ep5 name.mkv",
            "the league season1ep2ep3ep4ep5 name.mkv",
            "the.league.season.1.ep02.ep03.ep04.ep05.name.mkv",
            "the league season 1 episode 2 episode 3 episode 4 episode 5 name.mkv",
            "the league season 01 episode 02 episode 03 episode 04 episode 05 name.mkv",
            "the_league_season_1_episode_02_name_episode_03_name_episode_04_name_episode_05_name.mkv",
            "the_league_season_1_ep_02_name_ep_03_name_ep_04_name_ep_05_name.mkv",
            "the league season 1 ep 2 name ep 3 name ep 4 name ep5 name.mkv",
        };
        WordDelimitedMatcher instance = new WordDelimitedMatcher();
        for(String fileName : fileNames) {
            EpisodeMatch expResult = new EpisodeMatch(1, 2);
            expResult.getEpisodes().add(3);
            expResult.getEpisodes().add(4);
            expResult.getEpisodes().add(5);
            EpisodeMatch result = instance.match(path + fileName, EpisodeFileMatcher.stripCommonTags(fileName));
            assertEquals(expResult.getSeason(), result.getSeason());
            assertEquals(expResult.getEpisodes(), result.getEpisodes());
        }
    }
    
}
