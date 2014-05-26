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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import tv.model.EpisodeMatch;

/**
 * 
 * @author Sam Malone
 */
public class MockFileSystem {
    
    private final static String[] shows = new String[] {
        "Scrubs", "Friends", "Modern Family"
    };
    
    private final static File MOCK_ROOT = new File("mockroot_tv");
    
    public final static int NUM_SEASONS = 3;
    public final static int NUM_EPISODES = 12;
    
    public static List<String> getSourceFolders() {
        return Arrays.asList(MOCK_ROOT.getAbsolutePath());
    }
    
    public static void create() throws IOException {
        if(!MOCK_ROOT.exists()) {
            MOCK_ROOT.mkdir();
        }
        createStandardShows();
        createShowExceptions();
    }
    
    public static File getShowDir(String show) {
        return new File(MOCK_ROOT, show).getAbsoluteFile();
    }
    
    public static File getSeasonDir(String show, int season) {
        return new File(getShowDir(show), "Season " + season);
    }
    
    public static File getEpisodeFile(String show, int season, int episode) {
        return new File(getSeasonDir(show, season), genFileName(show, season, episode));
    }
    
    public static EpisodeMatch getEpisodeMatch(String show, int season, int episode) {
        return new EpisodeMatch(getEpisodeFile(show, season, episode), season, episode);
    }
    
    public static List<EpisodeMatch> getFullSeasonEpisodeMatches(String show, int startSeason, int endSeason) {
        List<EpisodeMatch> list = new ArrayList<>(NUM_EPISODES * (endSeason - startSeason + 1));
        while(startSeason <= endSeason) {
            for(int i = 1; i <= NUM_EPISODES; i++) {
                list.add(getEpisodeMatch(show, startSeason, i));
            }
            startSeason++; 
       }
        return list;
    }
    
    public static File[] getFullSeasonEpisodes(String show, int startSeason, int endSeason) {
        List<File> list = new ArrayList<>(NUM_EPISODES * (endSeason - startSeason + 1));
        while(startSeason <= endSeason) {
            list.addAll(Arrays.asList(getSeasonDir(show, startSeason++).listFiles()));
        }
        return list.toArray(new File[0]);
    }
    
    private static void createStandardShows() throws IOException {
         for(String show : shows) {
            File showDir = new File(MOCK_ROOT, show);
            showDir.mkdir();
            createSeasons(show, showDir);
        }
    }
    
    private static void createShowExceptions() throws IOException {
        String show = "The Walking Dead";
        int season = 1;
        File showDir = new File(MOCK_ROOT, show);
        showDir.mkdir();
        File seasonDir = new File(showDir, "Season 1");
        seasonDir.mkdir();
        new File(seasonDir, genFileName(show, season, 1)).createNewFile();
        new File(seasonDir, genFileName(show, season, 2, 2)).createNewFile();
        new File(seasonDir, genFileName(show, season, 4)).createNewFile();
        new File(seasonDir, genFileName(show, season, 5, 2)).createNewFile();
        createEpisodes(show, season, seasonDir, 7);
    }
    
    private static void createSeasons(String show, File showDir) throws IOException {
        for(int i = 1; i <= NUM_SEASONS; i++) {
            File seasonDir = new File(showDir, "Season " + i);
            seasonDir.mkdir();
            createEpisodes(show, i, seasonDir);
        }
    }
    
    private static void createEpisodes(String show, int season, File seasonDir) throws IOException {
        createEpisodes(show, season, seasonDir, 1);
    }
    
    private static void createEpisodes(String show, int season, File seasonDir, int start) throws IOException {
        for(int i = start; i <= NUM_EPISODES; i++) {
            new File(seasonDir, genFileName(show, season, i)).createNewFile();
        }
    }
    
    public static String genFileName(String show, int season, int episode) {
        return genFileName(show, season, episode, 1);
    }
    
    private static String genFileName(String show, int season, int episode, int numEpisodes) {
        StringBuilder sb = new StringBuilder();
        sb.append(show).append(" - ").append(season);
        for(int i = 0; i < numEpisodes; i++) {
            sb.append('x').append(String.format("%02d", episode + i));
        }
        return sb.append(".mkv").toString();
    }
    
    public static void delete() {
        delete(MOCK_ROOT);
    }
    
    private static void delete(File dir) {
        File[] files = dir.listFiles();
        if(files != null) {
            for(File f : files) {
                if(f.isDirectory()) {
                    delete(f);
                } else {
                    f.delete();
                }
            }
        }
        dir.delete();
    }
    
}
