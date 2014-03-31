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

import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import tv.exception.SeasonNotFoundException;
import tv.matcher.TVMatcher;
import tv.model.Episode;
import tv.model.EpisodeMatch;

/**
 *
 * @author Sam Malone
 */
public class NavigableEpisodeTest extends FileSystemEnvironment {
    
    private NavigableEpisode episode;
    
    @Before
    public void setUp() {
        TVScan scanner = new TVScan(MockFileSystem.getSourceFolders());
        episode = new NavigableEpisode(new TVMatcher(scanner));
    }
    
    @After
    public void tearDown() {
        episode = null;
    }

    /**
     * Test of navigate method, of class NavigableEpisode.
     * @throws tv.exception.SeasonNotFoundException
     */
    @Test
    public void testNavigateCurrent() throws SeasonNotFoundException {
        Episode toNavigate = new Episode("Scrubs", "", 1, 5);
        EpisodeMatch expResult = new EpisodeMatch(1, 5);
        EpisodeMatch result = episode.navigate(toNavigate, "cur");
        assertEquals(expResult.getSeason(), result.getSeason());        
        assertEquals(expResult.getEpisodes(), result.getEpisodes());        
    }

    /**
     * Test of navigate method, of class NavigableEpisode.
     * @throws tv.exception.SeasonNotFoundException
     */
    @Test
    public void testNavigateNext() throws SeasonNotFoundException {
        Episode toNavigate = new Episode("Scrubs", "", 1, 5);
        EpisodeMatch expResult = new EpisodeMatch(1, 6);
        EpisodeMatch result = episode.navigate(toNavigate, "next");
        assertEquals(expResult.getSeason(), result.getSeason());        
        assertEquals(expResult.getEpisodes(), result.getEpisodes());  
    }

    /**
     * Test of navigate method, of class NavigableEpisode.
     * @throws tv.exception.SeasonNotFoundException
     */
    @Test
    public void testNavigateNextDoubleEp() throws SeasonNotFoundException {
        Episode toNavigate = new Episode("The Walking Dead", "", 1, 2);
        EpisodeMatch expResult = new EpisodeMatch(1, 4);
        EpisodeMatch result = episode.navigate(toNavigate, "next");
        assertEquals(expResult.getSeason(), result.getSeason());        
        assertEquals(expResult.getEpisodes(), result.getEpisodes());  
    }

    /**
     * Test of navigate method, of class NavigableEpisode.
     * @throws tv.exception.SeasonNotFoundException
     */
    @Test
    public void testNavigatePrevious() throws SeasonNotFoundException {
        Episode toNavigate = new Episode("Scrubs", "", 1, 6);
        EpisodeMatch expResult = new EpisodeMatch(1, 5);
        EpisodeMatch result = episode.navigate(toNavigate, "prev");
        assertEquals(expResult.getSeason(), result.getSeason());        
        assertEquals(expResult.getEpisodes(), result.getEpisodes());  
    }
    

    /**
     * Test of navigate method, of class NavigableEpisode.
     * @throws tv.exception.SeasonNotFoundException
     */
    @Test
    public void testNavigatePreviousDoubleEp() throws SeasonNotFoundException {
        String show = "The Walking Dead";
        // the mock episode 1x02 is a double (ep 2 and 3).
        Episode toNavigate = new Episode(show, "", 1, 4);
        EpisodeMatch expResult = new EpisodeMatch(1, 2);
        expResult.getEpisodes().add(3);
        EpisodeMatch result = episode.navigate(toNavigate, "prev");
        assertEquals(expResult.getSeason(), result.getSeason());        
        assertEquals(expResult.getEpisodes(), result.getEpisodes());
        toNavigate = new Episode(show, "", expResult);
        expResult = new EpisodeMatch(1, 1);
        result = episode.navigate(toNavigate, "prev");
        assertEquals(expResult.getSeason(), result.getSeason());        
        assertEquals(expResult.getEpisodes(), result.getEpisodes());
    }

    /**
     * Test of navigateSeason method, of class NavigableEpisode.
     */
    @Test
    public void testNavigateSeasonPrevious() {
        Episode toNavigate = new Episode("Scrubs", "", 3, 1);
        EpisodeMatch expResult = new EpisodeMatch(2, 12);
        EpisodeMatch result = episode.navigateSeason(toNavigate, NavigableEpisode.PREV);
        assertEquals(expResult.getSeason(), result.getSeason());        
        assertEquals(expResult.getEpisodes(), result.getEpisodes());
    }
    
    /**
     * Test of navigateSeason method, of class NavigableEpisode.
     */
    @Test
    public void testNavigateSeasonNext() {
        Episode toNavigate = new Episode("Scrubs", "", 2, 12);
        EpisodeMatch expResult = new EpisodeMatch(3, 1);
        EpisodeMatch result = episode.navigateSeason(toNavigate, NavigableEpisode.NEXT);
        assertEquals(expResult.getSeason(), result.getSeason());        
        assertEquals(expResult.getEpisodes(), result.getEpisodes());
    }
    
    /**
     * Test of navigateSeason method, of class NavigableEpisode.
     */
    @Test
    public void testNavigateShowAscending() {
        for(int i = 1; i <= MockFileSystem.NUM_SEASONS; i++) {
            for(int j = 1; j <= MockFileSystem.NUM_EPISODES; j++) {
                int season = (j == MockFileSystem.NUM_EPISODES) ? i + 1 : i;
                int expEp = (j == MockFileSystem.NUM_EPISODES) ? 1 : j + 1;
                Episode toNavigate = new Episode("Scrubs", "", i, j);
                EpisodeMatch expResult = new EpisodeMatch(season, expEp);
                EpisodeMatch result = episode.navigate(toNavigate, "next");
                if(result == null && i == MockFileSystem.NUM_SEASONS && j == MockFileSystem.NUM_EPISODES) {
                    return;
                }
                System.out.println("Navigating: " + toNavigate + " : to : " + expResult);
                assertEquals(expResult.getSeason(), result.getSeason());
                assertEquals(expResult.getEpisodes(), result.getEpisodes());
            }
        }
    }
    
    /**
     * Test of navigateSeason method, of class NavigableEpisode.
     */
    @Test
    public void testNavigateShowDescending() {
        for(int i = MockFileSystem.NUM_SEASONS; i > 0; i--) {
            for(int j = MockFileSystem.NUM_EPISODES; j > 0; j--) {
                int season = (j == 1) ? i - 1 : i;
                int expEp = (j == 1) ? MockFileSystem.NUM_EPISODES : j - 1;
                Episode toNavigate = new Episode("Scrubs", "", i, j);
                EpisodeMatch expResult = new EpisodeMatch(season, expEp);
                EpisodeMatch result = episode.navigate(toNavigate, "prev");
                if(result == null && i == 1 && j == 1) {
                    return;
                }
                System.out.println("Navigating: " + toNavigate + " : to : " + expResult);
                assertEquals(expResult.getSeason(), result.getSeason());
                assertEquals(expResult.getEpisodes(), result.getEpisodes());
            }
        }
    }
    
}
