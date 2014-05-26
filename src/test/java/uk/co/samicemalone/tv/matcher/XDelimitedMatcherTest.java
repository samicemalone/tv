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

package uk.co.samicemalone.tv.matcher;

import uk.co.samicemalone.tv.matcher.EpisodeFileMatcher;
import uk.co.samicemalone.tv.matcher.XDelimitedMatcher;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import uk.co.samicemalone.tv.model.EpisodeMatch;

/**
 *
 * @author Sam Malone
 */
public class XDelimitedMatcherTest {
    
    private final String path = "C:\\TV\\The League\\Season 1\\";

    /**
     * Test of match method, of class XDelimitedMatcher.
     */
    @Test
    public void testMatch() {
        String[] fileNames = new String[] {
            "the.league.1x02.name.mkv",
            "the.league - 01x02 - name.mkv",
            "the league 1x2 name.mkv",
            "the_league_1x02_name.mkv",
        };
        XDelimitedMatcher instance = new XDelimitedMatcher();
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
            "the.league.1x02x03.name.mkv",
            "the.league - 01x02x03 - name.mkv",
            "the league 1x2x3 name.mkv",
            "the_league_1x02x03_name.mkv",
            "the_league_1x02_1x03_name.mkv",
            "the league - 1x02+1x03 - name.mkv",
            "the_league_1x2_1x3_name.mkv",
            "the_league_1x02.name.1x03_name.mkv",
            "the_league_1x2.name.1x3_name.mkv",
        };
        XDelimitedMatcher instance = new XDelimitedMatcher();
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
            "the.league.1x02x03x04x05.name.mkv",
            "the.league - 01x02x03x04x05 - name.mkv",
            "the league 1x2x3x4x5 name.mkv",
            "the_league_1x02x03x04x05_name.mkv",
            "the_league_1x02_1x03_1x04_1x05_name.mkv",
            "the league - 1x02+1x03+1x04+1x05 - name.mkv",
            "the_league_1x2_1x3_1x4_1x5_name.mkv",
            "the_league_1x02.name.1x03_name.1x04.name.1x05.name.mkv",
            "the_league_1x2.name.1x3_name.1x4.name.1x5.name.mkv",
        };
        XDelimitedMatcher instance = new XDelimitedMatcher();
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
