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

package uk.co.samicemalone.tv.util;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import uk.co.samicemalone.libtv.model.EpisodeNavigator;
import uk.co.samicemalone.libtv.model.EpisodeRange;
import uk.co.samicemalone.libtv.model.Range;
import uk.co.samicemalone.tv.FileSystemEnvironment;

/**
 *
 * @author Sam Malone
 */
public class TVUtilTest extends FileSystemEnvironment {

    /**
     * Test of asInt method, of class TVUtil.
     */
    @Test
    public void testGetSeasonNo() {
        String ep = "s02e01";
        int expResult = 2;
        int result = TVUtil.getSeasonNo(ep);
        assertEquals(expResult, result);
    }

    /**
     * Test of getEpisodeNo method, of class TVUtil.
     */
    @Test
    public void testGetEpisodeNo() {
        String ep = "s02e01";
        String expResult = "01";
        String result = TVUtil.getEpisodeNo(ep);
        assertEquals(expResult, result);
    }

    /**
     * Test of getSeasonRange method, of class TVUtil.
     */
    @Test
    public void testGetSeasonRange() {
        String ep = "s01-s03";
        Range expResult = new Range(1, 3);
        Range result = TVUtil.getSeasonRange(ep);
        assertEquals(expResult.getStart(), result.getStart());
        assertEquals(expResult.getEnd(), result.getEnd());
    }

    /**
     * Test of getEpisodeRange method, of class TVUtil.
     */
    @Test
    public void testGetEpisodeRange() {
        String ep = "s01e12-s02e04";
        EpisodeRange expResult = new EpisodeRange(1, 12, 2, 4);
        EpisodeRange result = TVUtil.getEpisodeRange(ep);
        assertEquals(expResult.getStartSeason(), result.getStartSeason());
        assertEquals(expResult.getStartEpisode(), result.getStartEpisode());
        assertEquals(expResult.getEndSeason(), result.getEndSeason());
        assertEquals(expResult.getEndEpisode(), result.getEndEpisode());
    }

    /**
     * Test of getNavigationPointer method, of class TVUtil.
     */
    @Test
    public void testGetNavigationPointer() {
        assertEquals(EpisodeNavigator.Pointer.PREV, TVUtil.getNavigationPointer("prev"));
        assertEquals(EpisodeNavigator.Pointer.CUR, TVUtil.getNavigationPointer("cur"));
        assertEquals(EpisodeNavigator.Pointer.NEXT, TVUtil.getNavigationPointer("next"));
    }
    
}
