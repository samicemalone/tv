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
package tv;

import java.io.File;
import java.util.List;
import tv.exception.SeasonNotFoundException;
import tv.matcher.TVMatcher;
import tv.model.Season;
import uk.co.samicemalone.libtv.VideoFilter;

/**
 *
 * @author Sam Malone
 */
public class TVScan {

    public List<String> sources;
    
    public TVScan(List<String> sources) {
        this.sources = sources;
    }
    
    /**
     * Gets the integer season number for the given episode string
     * @param ep Episode string e.g. s01e04
     * @return int season number e.g. 1
     */
    public static int getSeasonNo(String ep) {
        return Integer.valueOf(ep.substring(1, 3));
    }
    
    /**
     * Gets the episode number in string format for the given episode string
     * @param ep Episode string e.g. s01e04
     * @return String episode number e.g. "04"
     */
    public static String getEpisodeNo(String ep) {
        return ep.substring(4, 6);
    }
    
    /**
     * List the video files in the given season
     * @param season season to list
     * @return list of files in season directory. empty array if none or error
     */
    public File[] listFiles(Season season) {
        File[] list = season.getDir().listFiles(new VideoFilter());
        return list == null ? new File[] {} : list;
    }
    
    /**
     * List the video files in the given season of show
     * @param show tv show
     * @param seasonNo season to list
     * @return list of files in season directory. empty array if none or error
     * @throws tv.exception.SeasonNotFoundException if unable to find season
     */
    public File[] listFiles(String show, int seasonNo) throws SeasonNotFoundException {
        Season season = getSeason(show, seasonNo);
        if(season.getDir() == null || !season.getDir().exists()) {
            throw new SeasonNotFoundException(String.format("Season %s could not be found for %s", seasonNo, show));
        }
        return listFiles(season);
    }
    
    /**
     * Get the Season information for the given show and season.
     * The season directory will be null if the season doesn't exist
     * @param show show
     * @param season season
     * @return season
     */
    public Season getSeason(String show, int season) {
        return new Season(season, getSeasonDirectory(show, season));
    }
    
    /**
     * Gets the season directory File for the given show and season
     * @param show TV Show
     * @param season Season number
     * @return Season directory file or null if not found
     */
    public File getSeasonDirectory(String show, int season) {
        for(String source : sources) {
            if(new File(source, show).exists()) {
                for(String seasonPrefix : TVMatcher.SEASON_DIRECTORY_PREFIXES) {
                    StringBuilder dir = new StringBuilder();
                    dir.append(source).append(File.separator);
                    dir.append(show).append(File.separator);
                    dir.append(seasonPrefix).append(' ').append(season);
                    File f;
                    if((f = new File(dir.toString())).exists()) {
                        return f;
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * Get the show directory from the tv sources
     * @param show tv show
     * @return tv show directory File or null if not found
     */
    public File getShowDirectory(String show) {
        for(String source : sources) {
            if(directoryExists(source, show)) {
                return new File(source, show);
            }
        }
        return null;
    }
    
    /**
     * Checks whether the show exists in the given folder
     * @param folder Parent folder for TV show e.g. /media/TV/
     * @param show TV show
     * @return true if show exists in the given folder, false otherwise
     */
    public static boolean directoryExists(String folder, String show) {
        return new File(folder, show).exists();
    }
    
    /**
     * Checks whether the given show exists
     * @param show TV Show
     * @return true if show exists in at least one source folder, otherwise false
     */
    public boolean showExists(String show) {
        if(show.trim().equals("") || show.trim().equals("..")) {
            return false;
        }
        for(String sourceDir : sources) {
            if(directoryExists(sourceDir, show)) {
                return true;
            }
        }
        return false;
    }
    
}
