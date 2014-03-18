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

package tv;

import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import tv.model.Episode;

/**
 *
 * @author Sam Malone
 */
public class NavigableEpisodeTest {
    
    private NavigableEpisode episode;
    
    @BeforeClass
    public static void setUpClass() throws IOException {
        MockFileSystem.create();
    }
    
    @AfterClass
    public static void tearDownClass() {
        MockFileSystem.delete();
    }
    
    @Before
    public void setUp() {
        episode = new NavigableEpisode(new TVScan(MockFileSystem.getSourceFolders()));
    }
    
    @After
    public void tearDown() {
        episode = null;
    }

    /**
     * Test of navigate method, of class NavigableEpisode.
     */
    @Test
    public void testNavigateCurrent() {
        Episode toNavigate = new Episode("Scrubs", "", 1, 5);
        Episode expResult = toNavigate;
        Episode result = episode.navigate(toNavigate, "next");
        assertSame(expResult, result);
    }

    /**
     * Test of navigate method, of class NavigableEpisode.
     */
    @Test
    public void testNavigateNext() {
        Episode toNavigate = new Episode("Scrubs", "", 1, 5);
        Episode expResult = new Episode("Scrubs", "", 1, 6);
        Episode result = episode.navigate(toNavigate, "next");
        assertEquals(expResult, result);
    }

    /**
     * Test of navigate method, of class NavigableEpisode.
     */
    @Test
    public void testNavigateNextDoubleEp() {
        String show = "The Walking Dead";
        Episode toNavigate = new Episode(show, "", 1, 2);
        Episode expResult = new Episode(show, "", 1, 4);
        Episode result = episode.navigate(toNavigate, "next");
        assertEquals(expResult, result);
    }

    /**
     * Test of navigate method, of class NavigableEpisode.
     */
    @Test
    public void testNavigatePrevious() {
        Episode toNavigate = new Episode("Scrubs", "", 1, 6);
        Episode expResult = new Episode("Scrubs", "", 1, 5);
        Episode result = episode.navigate(toNavigate, "prev");
        assertEquals(expResult, result);
    }
    

    /**
     * Test of navigate method, of class NavigableEpisode.
     */
    @Test
    public void testNavigatePreviousDoubleEp() {
        String show = "The Walking Dead";
        // the mock episode 1x02 is a double (ep 2 and 3).
        Episode toNavigate = new Episode(show, "", 1, 4);
        Episode expResult = new Episode(show, "", 1, 3);
        Episode result = episode.navigate(toNavigate, "prev");
        assertEquals(expResult, result);
        toNavigate = expResult;
        expResult = new Episode(show, "", 1, 1);
        result = episode.navigate(toNavigate, "prev");
        assertEquals(expResult, result);
    }

    /**
     * Test of navigateSeason method, of class NavigableEpisode.
     */
    @Test
    public void testNavigateSeasonPrevious() {
        Episode toNavigate = new Episode("Scrubs", "", 3, 1);
        Episode expResult = new Episode("Scrubs", "", 2, 12);
        Episode result = episode.navigateSeason(toNavigate, NavigableEpisode.PREV);
        assertEquals(expResult, result);
    }
    
    /**
     * Test of navigateSeason method, of class NavigableEpisode.
     */
    @Test
    public void testNavigateSeasonNext() {
        Episode toNavigate = new Episode("Scrubs", "", 2, 12);
        Episode expResult = new Episode("Scrubs", "", 3, 1);
        Episode result = episode.navigateSeason(toNavigate, NavigableEpisode.NEXT);
        assertEquals(expResult, result);
    }
    
}
