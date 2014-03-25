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

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import tv.model.EpisodeMatch;

/**
 *
 * @author Sam Malone
 */
public class SEDelimitedMatcherTest {    

    /**
     * Test of match method, of class SEDelimitedMatcher.
     */
    @Test
    public void testMatch() {
        String[] fileNames = new String[] {
            "the.league.s01e01.pilot.mkv",
            "the.league.s1e1.pilot.mkv",
            "the league s1e1 pilot.mkv",
            "the league - s01e01 - pilot.mkv",
            "the league.s01.e01.pilot.mkv",
            "the.league.S01xE01.pilot.mkv",
            "the_league_S01_E01_pilot.mkv",
        };
        SEDelimitedMatcher instance = new SEDelimitedMatcher();
        for(String fileName : fileNames) {
            EpisodeMatch expResult = new EpisodeMatch(1, 1);
            EpisodeMatch result = instance.match(fileName);
            assertEquals(expResult.getSeason(), result.getSeason());
            assertEquals(expResult.getEpisodes(), result.getEpisodes());
        }
    }
    
    /**
     * Test of match method, of class SEDelimitedMatcher.
     */
    @Test
    public void testMatchDouble() {
        String[] fileNames = new String[] {
            "the league.s01e01e02.pilot.mkv",
            "the.league - s01e01e02 - pilot.mkv",
            "the_league_s1e1e2_pilot.mkv",
            "the.league.S01xE01xE02.pilot.mkv",
            "the.league.S1xE1xE2.pilot.mkv",
            "the.league_s01_e01_e02.pilot.mkv",
            "the_league-s01e01-s01e02-pilot.mkv",
            "the_league_s01e01+s01e02_pilot.mkv",
            "the.league.s1e1.pilot.s1e2.pilot.cont.mkv",
            "the.league.s01e01.pilot.s01e02.pilot.cont.mkv",
            "the.league.s01.e01.pilot.s01.e02.pilot.cont.mkv",
        };
        SEDelimitedMatcher instance = new SEDelimitedMatcher();
        for(String fileName : fileNames) {
            EpisodeMatch expResult = new EpisodeMatch(1, 1);
            expResult.getEpisodes().add(2);
            EpisodeMatch result = instance.match(fileName);
            assertEquals(expResult.getSeason(), result.getSeason());
            assertEquals(expResult.getEpisodes(), result.getEpisodes());
        }
    }
    
    /**
     * Test of match method, of class SEDelimitedMatcher.
     */
    @Test
    public void testMatchQuad() {
        String[] fileNames = new String[] {
            "the league.s01e01e02e03e04.pilot.mkv",
            "the.league - s01e01e02e03e04 - pilot.mkv",
            "the_league_s1e1e2e3e4_pilot.mkv",
            "the.league.S01xE01xE02xE03xE04.pilot.mkv",
            "the.league.S1xE1xE2xE3xE4.pilot.mkv",
            "the.league_s01_e01_e02_e03_e04.pilot.mkv",
            "the_league-s01e01-s01e02-s01e03-s01e04-pilot.mkv",
            "the_league_s01e01+s01e02+s01e03+s01e04_pilot.mkv",
            "the.league.s1e1.pilot.s1e2.pilot.cont.s1e3.cont.again.s1e4.cont.mkv",
            "the.league.s01e01.pilot.s01e02.pilot.cont.s01e03.cont.again.s01e04.cont.mkv",
            "the.league.s01.e01.pilot.s01.e02.pilot.cont.s01e03.cont.again.s01e04.cont.mkv",
        };
        SEDelimitedMatcher instance = new SEDelimitedMatcher();
        for(String fileName : fileNames) {
            EpisodeMatch expResult = new EpisodeMatch(1, 1);
            expResult.getEpisodes().add(2);
            expResult.getEpisodes().add(3);
            expResult.getEpisodes().add(4);
            EpisodeMatch result = instance.match(fileName);
            assertEquals(expResult.getSeason(), result.getSeason());
            assertEquals(expResult.getEpisodes(), result.getEpisodes());
        }
    }
    
}