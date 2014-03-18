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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import tv.filter.ExtensionFilter;
import tv.filter.RangeFilter;
import tv.filter.SeasonDirectoryFilter;
import tv.filter.SeasonSubsetFilter;
import tv.model.Season;

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
     * Gets a single episode file from the given season directory and episode string
     * @param season Season directory
     * @param epNo Episode number e.g. "03"
     * @return single episode or null if not found
     */
    public static File getEpisode(Season season, String epNo) {
        if(!season.getSeasonDir().exists()) {
            return null;
        }
        File[] list = season.getSeasonDir().listFiles(new ExtensionFilter());
        Pattern p = Pattern.compile(buildEpisodeRegex(season, epNo), Pattern.CASE_INSENSITIVE);
        for(File episode : list) {
            if(p.matcher(filterFileName(episode.getName())).find()) {
                return episode;
            }
        }
        return null;
    }
    
    private static String buildEpisodeRegex(Season s, String epNo) {
        StringBuilder sb = new StringBuilder();
        sb.append('s').append(s.getSeasonString());
        sb.append("(e").append(epNo).append("|(e\\d\\d)+e").append(epNo).append(")");
        sb.append('|').append(s.getSeasonNo()).append("(x").append(epNo).append("|(x\\d\\d)+x").append(epNo).append(')');
        sb.append('|').append(s.getSeasonNo()).append(epNo);
        return sb.toString();
    }
    
    /**
     * Gets the last episode number in the given season
     * @param season Season
     * @return Last Zero padded episode number or null if error occurred
     */
    public static String getLastEpisodeNo(Season season) {
        File[] list = season.getSeasonDir().listFiles(new ExtensionFilter());
        Pattern p = Pattern.compile("[sS][0-9][0-9][eE]([0-9][0-9])|[0-9]+x([0-9][0-9])");
        Matcher m;
        int lastEpisode = -1;
        for(File episode : list) {
            m = p.matcher(filterFileName(episode.getName()));
            if(m.find() && m.groupCount() == 2) {
                int curEp;
                if(m.group(1) == null && m.group(2) != null) {
                    curEp = Integer.valueOf(m.group(2));
                } else {
                    curEp = Integer.valueOf(m.group(1));
                }
                if(curEp > lastEpisode) {
                    lastEpisode = curEp;
                }
            }
        }
        if(lastEpisode == -1) {
            return null;
        }
        return String.format("%02d", lastEpisode);
    }
    
    /**
     * Gets the last (largest) season number for the given show
     * @param show TV Show
     * @return Zero padded season number or null if error
     */
    public String getLastSeasonNo(String show) {
        File showFolder = null;
        for(String source : sources) {
            if(directoryExists(source, show)) {
                showFolder = new File(source, show);
                break;
            }
        }
        if(showFolder == null) {
            return null;
        }
        File[] seasonDirs = showFolder.listFiles(new SeasonDirectoryFilter());
        if(seasonDirs == null) {
            return null;
        }
        int maxSeason = -1;
        int tmpSeason;
        Pattern p = Pattern.compile(SeasonDirectoryFilter.REGEX);
        Matcher m;
        for(File season : seasonDirs) {
            m = p.matcher(season.getName());
            if(m.find()) {
                tmpSeason = Integer.valueOf(m.group(1));
                if(tmpSeason > maxSeason) {
                    maxSeason = tmpSeason;
                }
            }
        }
        if(maxSeason == -1) {
            return null;
        }
        return String.format("%02d", maxSeason);
    }
    
    /**
     * Filters the given file name to remove possible ambiguities such
     * as 720p and 1080p strings.
     * @param fileName
     * @return Filtered fileName
     */
    private static String filterFileName(String fileName) {
        return fileName.replaceAll("720p", "").replaceAll("1080p", "");
    }
    
    /**
     * Gets all the episodes from the given show
     * @param show TV Show
     * @return Episode List or empty array
     */
    public File[] getAllEpisodes(String show) {
        return getSeasonsFrom(getSeason(show, 1), show);
    }
    
    /**
     * Gets the list of files in the given episode range
     * @param season Starting Season
     * @param show TV Show
     * @param startEp Start episode in range e.g. s02e04
     * @param endEp End episode in range e.g. s04e02
     * @return List of files in the given range or empty array
     */
    public File[] getEpisodeRange(Season season, String show, String startEp, String endEp) {
        int i = getSeasonNo(startEp);
        int endSeason = getSeasonNo(endEp);
        if(i > endSeason) {
            return new File[] {};
        }
        if(i == endSeason) {
            return season.getSeasonDir().listFiles(new RangeFilter(getEpisodeNo(startEp), getEpisodeNo(endEp)));
        }
        File[] tmpSeason = season.getSeasonDir().listFiles(new SeasonSubsetFilter(getEpisodeNo(startEp)));
        List<File> list = new ArrayList<File>();
        // add episodes from starting season in range
        list.addAll(Arrays.asList(tmpSeason));
        // add full seasons in between range if applicable
        File seasonDir = season.getSeasonDir();
        while(i < endSeason - 1 && (seasonDir = getSeasonDirectory(show, ++i)) != null) {
            tmpSeason = seasonDir.listFiles(new ExtensionFilter());
            list.addAll(Arrays.asList(tmpSeason));
        }
        // add remaining episode of end season in range
        seasonDir = getSeasonDirectory(show, endSeason);
        if(seasonDir != null) {
            tmpSeason = seasonDir.listFiles(new RangeFilter("00", getEpisodeNo(endEp)));
            list.addAll(Arrays.asList(tmpSeason));
        }
        return list.toArray(new File[0]);
    }
    
    /**
     * Gets a list of files from the episode given in the season directory given
     * @param season Season directory
     * @param startEp Episode number to start from e.g. "05"
     * @return List of files matched or empty array if none found
     */
    public File[] getEpisodesFrom(Season season, String startEp) {
        return season.getSeasonDir().listFiles(new SeasonSubsetFilter(startEp));
    }
    
    /**
     * Get a list of files from the startSeason to the endSeason
     * @param season Starting Season
     * @param show TV Show
     * @param startSeason Starting season e.e. s03
     * @param endSeason End season e.g. s04
     * @return list of files matched or empty array if none found
     */
    public File[] getSeasonRange(Season season, String show, String startSeason, String endSeason) {
        File[] tmpSeason = season.getSeasonDir().listFiles(new ExtensionFilter());
        int i = getSeasonNo(startSeason);
        int maxSeason = getSeasonNo(endSeason);
        if(i > maxSeason) {
            return new File[] {};
        }
        List<File> list = new ArrayList<File>(tmpSeason.length*(Math.abs(maxSeason-i)));
        list.addAll(Arrays.asList(tmpSeason));
        File seasonDir = season.getSeasonDir();
        while(i < maxSeason && (seasonDir = getSeasonDirectory(show, ++i)) != null) {
            tmpSeason = seasonDir.listFiles(new ExtensionFilter());
            list.addAll(Arrays.asList(tmpSeason));
        }
        return list.toArray(new File[0]);
    }
    
    /**
     * Get a list of all the episodes for the given show starting from seasonNo
     * @param season Starting Season
     * @param show TV Show
     * @return list of files matched or empty array if none found
     */
    public File[] getSeasonsFrom(Season season, String show) {
        File seasonDir = season.getSeasonDir();
        File[] tmpSeason = seasonDir.listFiles(new ExtensionFilter());
        List<File> list = new ArrayList<File>(tmpSeason.length*3);
        list.addAll(Arrays.asList(tmpSeason));
        int i = season.getSeasonNo();
        while((seasonDir = getSeasonDirectory(show, ++i)) != null) {
            tmpSeason = seasonDir.listFiles(new ExtensionFilter());
            list.addAll(Arrays.asList(tmpSeason));
        }
        return list.toArray(new File[0]);
    }
    
    /**
     * Get the Season information for the given show and season
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
                for(String seasonPrefix : SeasonDirectoryFilter.SEASON_DIRECTORY_PREFIX) {
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
