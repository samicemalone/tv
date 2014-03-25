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

package tv.filter;

import org.junit.After;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import tv.matcher.EpisodeMatcher;

/**
 *
 * @author Sam Malone
 */
public class EpisodeFilterTest {

    /**
     * Test of accept method, of class EpisodeFilter.
     */
    @Test
    public void testAccept() {
        EpisodeFilter instance;
        String[] fileNames = new String[] {
            "the.league.s01e01.pilot.mkv",
            "the.league.S01E02.720p.mkv",
            "the_league_s01e03_1080p.mkv",
            "the-league-s01e04_480p.mkv",
            "the league s01e05 spaced.mkv",
            "the league - s01e06 - hyphon spaced.mkv",
            "the.league.s01.e07.dot.gap.mkv",
            "the.league.s01-e08.dash.gap.mkv",
            "the league - S01xE09 - x separator.mkv",
            "the.league.110.no.ep.separator.mkv",
            "the league s01e11 - spaced name hyphon.mkv",
            "the_league_s1e12.name.mkv"
        };
        EpisodeMatcher matcher = new EpisodeMatcher();
        for(int i = 0; i < fileNames.length; i++) {
            instance = new EpisodeFilter(matcher, i+1);
            String name = fileNames[i];
            boolean result = instance.accept(null, name);
            assertTrue(result);
        }
    }

    /**
     * Test of accept method, of class EpisodeFilter.
     */
    @Test
    public void testNotAccept() {
        EpisodeFilter instance;
        String[] fileNames = new String[] {
            "the.league.s01e01.pilot.nfo",
            "the.league.S01E02.720p.jpg",
        };
        EpisodeMatcher matcher = new EpisodeMatcher();
        for(int i = 0; i < fileNames.length; i++) {
            instance = new EpisodeFilter(matcher, i+1);
            String name = fileNames[i];
            boolean result = instance.accept(null, name);
            assertFalse(result);
        }
    }

    /**
     * Test of accept method, of class EpisodeFilter.
     */
    @Test
    public void testAcceptMultiEpisodes() {
        EpisodeFilter instance;
        String[] fileNames = new String[] {
            "the.league.s01e01e02.pilot.mkv",
            "the.league - 1x01x02 - pilot.mkv",
            "the.league.s01e02e03e04.pilot.mkv",
            "the.league - 1x01x02x03x04 - pilot.mkv",
            "the.league 1x01x02x03x04 pilot.mkv",
            "the.league.S01xE01xE02.pilot.mkv",
            "the.league_s01_e01_e02.pilot.mkv",
            "the_league_s01e01-s01e02_pilot.mkv",
            "the.league.1x01_1x02.pilot.mkv",
            "the.league.1x01.pilot.1x02.pilot.cont.mkv",
            "the.league.s01e01.pilot.s01e02.pilot.cont.mkv",
            "the league.s01e01+s01e02.pilot.mkv",
            "the league - 1x01+1x02 - pilot.mkv"
        };
        EpisodeMatcher matcher = new EpisodeMatcher();
        for(String fileName : fileNames) {
            instance = new EpisodeFilter(matcher, 2);
            assertTrue(instance.accept(null, fileName));
            instance = new EpisodeFilter(matcher, 2);
            assertTrue(instance.accept(null, fileName));
        }
    }
    
}
