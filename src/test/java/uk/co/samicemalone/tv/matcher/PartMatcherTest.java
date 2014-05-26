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

import uk.co.samicemalone.tv.matcher.PartMatcher;
import uk.co.samicemalone.tv.matcher.EpisodeFileMatcher;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import uk.co.samicemalone.tv.model.EpisodeMatch;

/**
 *
 * @author Sam Malone
 */
public class PartMatcherTest {
    
    private final String path = "C:\\TV\\The Pacific\\Season 1\\";

    /**
     * Test of match method, of class PartMatcher.
     */
    @Test
    public void testMatch() {
        String[] fileNames = new String[] {
            "the pacific part ii name.mkv",
            "the.pacific.pt.ii.name.mkv",
            "the_pacific_part_ii_name.mkv",
            "the pacific part 2 name.mkv",
            "the pacific part2 name.mkv",
            "the.pacific.pt.2.name.mkv",
            "the_pacific_pt2_name.mkv",
        };
        PartMatcher instance = new PartMatcher();
        EpisodeMatch expResult = new EpisodeMatch(1, 2);
        for(String fileName : fileNames) {
            EpisodeMatch result = instance.match(path + fileName, EpisodeFileMatcher.stripCommonTags(fileName));
            assertEquals(expResult.getEpisodes(), result.getEpisodes());
            assertEquals(expResult.getSeason(), result.getSeason());
        }
    }

    /**
     * Test of match method, of class PartMatcher.
     */
    @Test
    public void testMatchDouble() {
        String[] fileNames = new String[] {
            "the-pacific-part-i-part-ii-name.mkv",
            "the pacific part iii part iv name.mkv",
            "the.pacific.pt.v.pt.vi.name.mkv",
            "the_pacific_part_vii_part_viii_name.mkv",
            "the pacific part ix name part x name.mkv",
            "the-pacific-part-11-part-12-name.mkv",
            "the pacific part 13 part 14 name.mkv",
            "the.pacific.pt.15.pt.16.name.mkv",
            "the_pacific_part_17_part_18_name.mkv",
            "the pacific part 19 name part 20 name.mkv",
            "the-pacific-part21-part22-name.mkv",
            "the pacific pt23-pt24 name.mkv",
            "the.pacific.pt.25.name.pt.26.name.mkv",
            "the_pacific_part27_name_part28_name.mkv",
        };
        PartMatcher instance = new PartMatcher();
        for(int i = 0; i < fileNames.length; i++) {
            EpisodeMatch expResult = new EpisodeMatch(1, (i * 2) + 1);
            expResult.getEpisodes().add((i * 2) + 2);
            EpisodeMatch result = instance.match(path + fileNames[i], EpisodeFileMatcher.stripCommonTags(fileNames[i]));
            assertEquals(expResult.getEpisodes(), result.getEpisodes());
            assertEquals(expResult.getSeason(), result.getSeason());
        }
    }

    /**
     * Test of match method, of class PartMatcher.
     */
    @Test
    public void testMatchQuad() {
        String[] fileNames = new String[] {
            "the-pacific-part-i-part-ii-part-iii-part-iv-name.mkv",
            "the pacific part v part vi part vii name part viii name.mkv",
            "the.pacific.pt.ix.pt.x.pt.xi.pt.xii.name.mkv",
            "the_pacific_part_xiii_part_xiv_part_xv_part_xvi_name.mkv",
            "the pacific part xvii name part xviii name part xix name part xx name.mkv",
            "the-pacific-part-21-part-22-part-23-part-24-name.mkv",
            "the pacific part 25 part 26 part 27 part 28 name.mkv",
            "the.pacific.pt.29.pt.30.pt.31.pt.32.name.mkv",
            "the_pacific_part_33_part_34_part_35_part_36_name.mkv",
            "the pacific part 37 name part 38 name part 39 name part 40 name.mkv",
            "the-pacific-part41-part42-part43-part44-name.mkv",
            "the pacific pt45-pt46-pt47-pt48 name.mkv",
            "the.pacific.pt.49.name.pt.50.name.pt.51.name.pt.52.name.mkv",
            "the_pacific_part53_name_part54_name_part55_name_part56_name.mkv",
        };
        PartMatcher instance = new PartMatcher();
        for(int i = 0; i < fileNames.length; i++) {
            EpisodeMatch expResult = new EpisodeMatch(1, (i * 4) + 1);
            expResult.getEpisodes().add((i * 4) + 2);
            expResult.getEpisodes().add((i * 4) + 3);
            expResult.getEpisodes().add((i * 4) + 4);
            EpisodeMatch result = instance.match(path + fileNames[i], EpisodeFileMatcher.stripCommonTags(fileNames[i]));
            assertEquals(expResult.getEpisodes(), result.getEpisodes());
            assertEquals(expResult.getSeason(), result.getSeason());
        }
    }
    
}
