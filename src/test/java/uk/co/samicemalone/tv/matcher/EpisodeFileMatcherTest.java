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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import uk.co.samicemalone.tv.FileSystemEnvironment;
import uk.co.samicemalone.tv.MockFileSystem;
import static uk.co.samicemalone.tv.matcher.TVMatcherTest.assertEpisodeMatchEquals;
import uk.co.samicemalone.tv.model.EpisodeMatch;
import uk.co.samicemalone.tv.model.Range;

/**
 *
 * @author Sam Malone
 */
public class EpisodeFileMatcherTest extends FileSystemEnvironment {
    
    private EpisodeFileMatcher episodeMatcher;
    
    @Before
    public void setUp() {
        episodeMatcher = new EpisodeFileMatcher();
    }
    
    @After
    public void tearDown() {
        episodeMatcher = null;
    }
    
    /**
     * Test of stripCommonTags method, of class EpisodeFileMatcher.
     */
    @Test
    public void testStripCommonTags() {
        String fileName = "Modern.Family.S05E17.720p.DD5.1.AAC2.0.H.264.mkv";
        String expResult = "Modern.Family.S05E17.....mkv";
        assertEquals(expResult, EpisodeFileMatcher.stripCommonTags(fileName));
        fileName = "the.walking.dead.s03e01.720p.bluray.x264.mkv";
        expResult = "the.walking.dead.s03e01..bluray..mkv";
        assertEquals(expResult, EpisodeFileMatcher.stripCommonTags(fileName));
    }

    /**
     * Test of match method, of class EpisodeFileMatcher.
     */
    @Test
    public void testMatchFile() {
        File file = MockFileSystem.getEpisodeFile("Scrubs", 1, 2);
        EpisodeMatch expResult = new EpisodeMatch(file, 1, 2);
        EpisodeMatch result = episodeMatcher.match(file);
        assertEpisodeMatchEquals(expResult, result);
    }

    /**
     * Test of match method, of class EpisodeFileMatcher.
     */
    @Test
    public void testMatchFileInSeason() {
        File[] files = MockFileSystem.getFullSeasonEpisodes("Scrubs", 1, 1);
        int episodeNo = 4;
        File file = MockFileSystem.getEpisodeFile("Scrubs", 1, episodeNo);
        EpisodeMatch expResult = new EpisodeMatch(file, 1, episodeNo);
        EpisodeMatch result = episodeMatcher.match(files, episodeNo);
        assertEpisodeMatchEquals(expResult, result);
    }

    /**
     * Test of match method, of class EpisodeFileMatcher.
     */
    @Test
    public void testMatchFilesInSeason() {
        File[] files = MockFileSystem.getFullSeasonEpisodes("Scrubs", 1, 1);
        List<EpisodeMatch> expResult = MockFileSystem.getFullSeasonEpisodeMatches("Scrubs", 1, 1);
        List<EpisodeMatch> result = episodeMatcher.match(files);
        assertEquals(expResult.size(), result.size());
        for(int i = 0; i < result.size(); i++) {
            assertEpisodeMatchEquals(expResult.get(i), result.get(i));
        }
    }

    /**
     * Test of matchRange method, of class EpisodeFileMatcher.
     */
    @Test
    public void testMatchRange() {
        String show = "Scrubs";
        File[] files = MockFileSystem.getFullSeasonEpisodes(show, 1, 1);
        List<EpisodeMatch> expResult = new ArrayList<>();
        expResult.add(MockFileSystem.getEpisodeMatch(show, 1, 4));
        expResult.add(MockFileSystem.getEpisodeMatch(show, 1, 5));
        expResult.add(MockFileSystem.getEpisodeMatch(show, 1, 6));
        expResult.add(MockFileSystem.getEpisodeMatch(show, 1, 7));
        expResult.add(MockFileSystem.getEpisodeMatch(show, 1, 8));
        expResult.add(MockFileSystem.getEpisodeMatch(show, 1, 9));
        List<EpisodeMatch> result = episodeMatcher.matchRange(files, new Range(4, 9));
        assertEquals(expResult.size(), result.size());
        for(int i = 0; i < result.size(); i++) {
            assertEpisodeMatchEquals(expResult.get(i), result.get(i));
        }
    }

    /**
     * Test of matchFrom method, of class EpisodeFileMatcher.
     */
    @Test
    public void testMatchFrom() {
        String show = "Scrubs";
        File[] files = MockFileSystem.getFullSeasonEpisodes(show, 1, 1);
        List<EpisodeMatch> expResult = new ArrayList<>();
        expResult.add(MockFileSystem.getEpisodeMatch(show, 1, 10));
        expResult.add(MockFileSystem.getEpisodeMatch(show, 1, 11));
        expResult.add(MockFileSystem.getEpisodeMatch(show, 1, 12));
        List<EpisodeMatch> result = episodeMatcher.matchFrom(files, 10);
        assertEquals(expResult.size(), result.size());
        for(int i = 0; i < result.size(); i++) {
            assertEpisodeMatchEquals(expResult.get(i), result.get(i));
        }
    }

    /**
     * Test of matchLargest method, of class EpisodeFileMatcher.
     */
    @Test
    public void testMatchLargest() {
        String show = "Scrubs";
        File[] files = MockFileSystem.getFullSeasonEpisodes(show, 1, 1);
        File file = MockFileSystem.getEpisodeFile(show, 1, MockFileSystem.NUM_EPISODES);
        EpisodeMatch expResult = new EpisodeMatch(file, 1, MockFileSystem.NUM_EPISODES);
        EpisodeMatch result = episodeMatcher.matchLargest(files);
        assertEpisodeMatchEquals(expResult, result);
    }
    
//    @Test
//    public void testing() throws IOException {
//        BufferedReader br = null;
//        File source = new File("C:\\TV\\Show Name\\Season 1");
//        File predb = new File("J:\\Downloads\\", "predb.csv");
//        EpisodeFileMatcher m = new EpisodeFileMatcher();
//        try {
//            FileWriter yes = new FileWriter(new File("J:\\Downloads\\matched.txt"));
//            FileWriter no = new FileWriter(new File("J:\\Downloads\\nomatch.txt"));
//            br = new BufferedReader(new InputStreamReader(new FileInputStream(predb), "UTF-8"));
//            String line;
//            while((line = br.readLine()) != null) {
//                System.out.println("Matching: " + line);
//                EpisodeMatch match = m.match(new File(source, line.concat(".mkv")));
//                if(match != null) {
//                    //String filter = EpisodeFileMatcher.stripCommonTags(line.concat(".mkv"));
//                    yes.write(match.toString() + "#" + line + "\n");
//                    //System.out.println("Matched: " + match.toString() + " :: " + filter + " :: " + line);
//                } else {
//                    no.write(line + "\n");
//                }
//            }
//            yes.close();
//            no.close();
//        } catch (IOException ex) {
//            
//        } finally {
//            if(br != null) {
//                br.close();
//            }
//        }
//    }
    
}
