/*
 * Copyright (c) 2013, Sam Malone. All rights reserved.
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
package tv.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import tv.TV;
import tv.model.Episode;

/**
 *
 * @author Sam Malone
 */
public class TVDBManager extends CSV_IO {
    
    private Map<String, Episode> list;
    private List<Episode> episodeList;
    private String filteredUser;
    private boolean isFilterUser;
    
    private final static int CSV_COLUMNS = 5;
    
    public TVDBManager(File tvdb) {
        super(tvdb);
    }
    
    /**
     * Checks if the tv database contains the episode information given show
     * @param show TV Show
     * @return true if the tv database contains episode data, false otherwise
     */
    public boolean containsEpisodeData(String show) {
        return list.containsKey(show);
    }
    
    /**
     * Gets the episode information for the given show
     * @param show TV Show
     * @return Episode data for the given show, or null if doesn't exist
     */
    public Episode getEpisode(String show) {
        return list.get(show);
    }
    
    /**
     * Gets the list of shows from the TV Database as a CSV formatted string
     * @return CSV formatted show list
     */
    public String getCSVShows() {
        StringBuilder sb = new StringBuilder();
        for(String showFolder : TV.ENV.getArguments().getSourceFolders()) {
            for(String show : new File(showFolder).list()) {
                appendCSVLine(sb, show);
            }
        }
        return sb.substring(0, sb.length()-1);
    }
    
    /**
     * Gets the list of episodes from the TV Database as a CSV formatted string
     * @param eps CSV formatted episode list
     * @return 
     */
    public String getCSVEpisodes(List<Episode> eps) {
        StringBuilder sb = new StringBuilder();
        for(Episode e : eps) {
            appendCSVEpisodeLine(sb, e);
        }
        return sb.toString();
    }
    
    /**
     * Wrapper around {@link #appendCSVLine(java.lang.StringBuilder, java.lang.String...)}.
     * Appends a CSV formatted line with the values from the episode given in the order:
     * Show, User, Season No, Episode No, Played Date
     * @param sb StringBuilder to append to
     * @param e Episode to append as a CSV line
     */
    private void appendCSVEpisodeLine(StringBuilder sb, Episode e) {
        String season = String.valueOf(e.getSeason());
        String episode = String.valueOf(e.getEpisodesAsRange().getEnd());
        String playedDate = String.valueOf(e.getPlayedDate());
        appendCSVLine(sb, e.getShow(), e.getUser(), season, episode, playedDate);
    }
    
    /**
     * Find an episode in the episode list that matches the show and user given. 
     * If a match is found, the episode will be updated with the new season,
     * episode number and play date set as the current time.
     * @param episodes List of episodes to search
     * @param episode Episode to write to the TVDB file
     * @return true if an episode was found and updated. false otherwise
     */
    private boolean findAndUpdateEpisode(List<Episode> episodes, Episode episode) {
        for(Episode ep : episodes) {
            if(ep.getShow().equals(episode.getShow()) && ep.getUser().equals(episode.getUser())) {
                ep.setSeason(episode.getSeason());
                ep.setEpisodes(episode.getEpisodes());
                ep.setPlayedDate((int) (System.currentTimeMillis() / 1000));
                return true;
            }
        }
        return false;
    }
    
    /**
     * Writes the given episode information to the TV Database.
     * Will attempt to create the parent directory for the TVDB if
     * it doesn't exist.
     * @param episode Episode to write to the TVDB file
     */
    public void writeStorage(Episode episode) {
        if(!CSV_FILE.getParentFile().exists() && !CSV_FILE.getParentFile().mkdir()) {
            System.out.println("Unable to create a directory at " + CSV_FILE.getParentFile().getAbsolutePath());
            return;
        }
        StringBuilder sb = new StringBuilder();
        boolean found = false;
        try {
            List<Episode> eps = readAllStorage();
            found = findAndUpdateEpisode(eps, episode);
            for(Episode e : eps) {
                appendCSVEpisodeLine(sb, e);
            }
        } catch(FileNotFoundException ex) {} // ignored because file will be created
        if(!found) {
            episode.setPlayedDate((int) (System.currentTimeMillis() / 1000));
            appendCSVEpisodeLine(sb, episode);
        }
        writeFile(sb.toString());
    }
    
    /**
     * Reads the TV Database information for the given user
     * @param user User
     * @throws FileNotFoundException If the TV database doesn't exist
     */
    public void readStorage(String user) throws FileNotFoundException {
        list = new HashMap<String, Episode>();
        isFilterUser = true;
        filteredUser = user;
        readFile(CSV_COLUMNS);
        isFilterUser = false;
    }
    
    /**
     * Reads the TV Database information for all users
     * @return List of episode information, empty list if exists but no eps
     * @throws FileNotFoundException If the TV database doesn't exist
     */
    public List<Episode> readAllStorage() throws FileNotFoundException {
        episodeList = new ArrayList<Episode>();
        readFile(CSV_COLUMNS);
        return episodeList;
    }

    @Override
    protected void handleLine(Matcher m) {
        if(m.find()) {
            int season = Integer.valueOf(m.group(3));
            int episode = Integer.valueOf(m.group(4));
            Episode ep = new Episode(m.group(1), m.group(2), season, episode);
            ep.setPlayedDate(Integer.valueOf(m.group(5)));
            if(isFilterUser) {
                if(ep.getUser().equals(filteredUser)) {
                    list.put(m.group(1), ep);
                }
            } else {
                episodeList.add(ep);
            }
        }
    }
    
}
