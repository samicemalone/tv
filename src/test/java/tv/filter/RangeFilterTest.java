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

import static org.junit.Assert.assertTrue;
import org.junit.Test;
import tv.matcher.EpisodeMatcher;
import tv.model.Range;

/**
 *
 * @author Sam Malone
 */
public class RangeFilterTest {

    /**
     * Test of accept method, of class RangeFilter.
     */
    @Test
    public void testAccept() {
        Range range = new Range(1, 12);
        EpisodeMatcher m = new EpisodeMatcher();
        RangeFilter instance = new RangeFilter(m, range);
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
            "the league - 111 - hyphon spaced.mkv",
            "the league s01e12 - spaced name hyphon.mkv",
        };
        for(String name : fileNames) {
            boolean result = instance.accept(null, name);
            assertTrue(result);
        }
    }
    
}
