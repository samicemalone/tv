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

package tv.matcher;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import tv.TVScan;
import tv.exception.SeasonNotFoundException;
import tv.filter.DirectoryFilter;
import tv.model.EpisodeMatch;
import tv.model.EpisodeRange;
import tv.model.Range;
import tv.model.Season;

/**
 *
 * @author Sam Malone
 */
public class TVMatcher {
    
    public static final Pattern SEASON_PATTERN = Pattern.compile("(?:Season|Series) ([0-9]+)", Pattern.CASE_INSENSITIVE);
    
    public static final String[] SEASON_DIRECTORY_PREFIXES = new String[] { "Season", "Series" };

    private final TVScan tvScanner;
    private final EpisodeFileMatcher episodeMatcher;
    
    public TVMatcher(TVScan scanner) {
        tvScanner = scanner;
        episodeMatcher = new EpisodeFileMatcher();
    }
    
    public TVMatcher(TVScan scanner, EpisodeFileMatcher matcher) {
        tvScanner = scanner;
        episodeMatcher = matcher;
    }
    
    public EpisodeMatch matchEpisode(String show, int season, int episode) throws SeasonNotFoundException {
        return episodeMatcher.match(tvScanner.listFiles(show, season), episode);
    }
    
    public EpisodeMatch matchEpisode(Season season, int episode) {
        return episodeMatcher.match(tvScanner.listFiles(season), episode);
    }
    
    public EpisodeMatch matchLargestEpisode(String show, int season) throws SeasonNotFoundException {
        return episodeMatcher.matchLargest(tvScanner.listFiles(show, season));
    }
    
    public EpisodeMatch matchLargestEpisode(Season season) {
        return episodeMatcher.matchLargest(tvScanner.listFiles(season));
    }
    
    public EpisodeMatch matchLatestEpisode(String show) throws SeasonNotFoundException {
        Season season = matchLargestSeason(matchSeasons(tvScanner.getShowDirectory(show)));
        return episodeMatcher.matchLargest(tvScanner.listFiles(season));
    }
    
    public List<EpisodeMatch> matchSeason(String show, int season) throws SeasonNotFoundException {
        return matchSeasonRange(show, new Range(season, season));
    }
    
    /**
     * Get the Season information for the given show and season.
     * The season directory will be null if the season doesn't exist
     * @param show show
     * @param season season
     * @return season
     */
    public Season getSeason(String show, int season) {
        return tvScanner.getSeason(show, season);
    }
    
    /**
     * Match the episodes in the given range for show
     * @param show TV Show
     * @param range Range of seasons
     * @return list of files matched or empty array if none found
     */
    public List<EpisodeMatch> matchSeasonRange(String show, Range range) {
        List<EpisodeMatch> matches = new ArrayList<EpisodeMatch>();
        for(Season season : matchSeasons(tvScanner.getShowDirectory(show))) {
            if(season.asInt() >= range.getStart() && season.asInt() <= range.getEnd()) {
                matches.addAll(episodeMatcher.match(tvScanner.listFiles(season)));
            }
        }
        return matches;
    }
    
    /**
     * Get a list of all the episodes for the given show starting from season
     * @param season Starting Season
     * @param show TV Show
     * @return list of files matched or empty array if none found
     */
    public List<EpisodeMatch> matchSeasonsFrom(String show, int season) {
        List<EpisodeMatch> list = new ArrayList<EpisodeMatch>();
        list.addAll(matchSeasonRange(show, Range.maxRange(season)));
        return list;
    }
    
    /**
     * Match a list of seasons in the given show directory
     * @param showDir show directory
     * @return list of seasons or empty list
     */
    public List<Season> matchSeasons(File showDir) {
        List<Season> seasons = new ArrayList<Season>();
        for(File season : showDir.listFiles(new DirectoryFilter())) {
            Matcher m = SEASON_PATTERN.matcher(season.getName());
            if(m.find()) {
                seasons.add(new Season(Integer.valueOf(m.group(1)), season));
            }
        }
        Collections.sort(seasons);
        return seasons;
    }
    
    /**
     * Matches the largest season for the given show
     * @param show tv show
     * @return largest season or null if seasons is empty
     */
    public List<EpisodeMatch> matchLargestSeason(String show) {
        Season largest = matchLargestSeason(matchSeasons(tvScanner.getShowDirectory(show)));
        return episodeMatcher.match(tvScanner.listFiles(largest));
    }
    
    /**
     * Matches the largest season from the list of seasons
     * @param seasons list of seasons
     * @return largest season or null if seasons is empty
     */
    public Season matchLargestSeason(List<Season> seasons) {
        Season largest = null;
        int max = -1;
        for(Season season : seasons) {
            if(season.asInt() > max) {
                max = season.asInt();
                largest = season;
            }
        }
        return largest;
    }
    
    /**
     * Match episodes in season from the given start episode. In other words,
     * match each file to an episode which is greater than or equal to startEp.
     * @param show tv show
     * @param season season to match episodes
     * @param episode episode to start from in season
     * @return episode matches or empty list
     * @throws tv.exception.SeasonNotFoundException if unable to find a season
     */
    public List<EpisodeMatch> matchEpisodesFrom(String show, int season, int episode) throws SeasonNotFoundException {
        return episodeMatcher.matchFrom(tvScanner.listFiles(show, season), episode);
    }
    
    /**
     * Match the episodes in the given episode range
     * @param show TV Show
     * @param range EpisodeRange
     * @return List of episode matches in the given range or empty list
     * @throws tv.exception.SeasonNotFoundException if unable to find a season
     */
    public List<EpisodeMatch> matchEpisodeRange(String show, EpisodeRange range) throws SeasonNotFoundException {
        if(range.getStartSeason() > range.getEndSeason()) {
            return new ArrayList<EpisodeMatch>();
        } else if(range.getStartSeason() == range.getEndSeason()) {
            return episodeMatcher.matchRange(tvScanner.listFiles(show, range.getStartSeason()), range.toRange());
        }
        List<EpisodeMatch> list = new ArrayList<EpisodeMatch>();
        // add episodes from starting season in range
        list.addAll(episodeMatcher.matchFrom(tvScanner.listFiles(show, range.getStartSeason()), range.getStartEpisode()));
        // add full seasons in between range if applicable
        list.addAll(matchSeasonRange(show, new Range(range.getStartSeason() + 1, range.getEndSeason() - 1)));
        // add remaining episodes of the end season in range
        File[] tmpList = tvScanner.listFiles(show, range.getEndSeason());
        list.addAll(episodeMatcher.matchRange(tmpList, new Range(0, range.getEndEpisode())));
        return list;
    }
    
    /**
     * Match all the episodes from all the seasons of the given show
     * @param show TV Show
     * @return Episode List or empty array
     * @throws tv.exception.SeasonNotFoundException if unable to find a season
     */
    public List<EpisodeMatch> matchAllEpisodes(String show) throws SeasonNotFoundException {
        return matchSeasonsFrom(show, 1);
    }
    
    public TVScan getTvScanner() {
        return tvScanner;
    }
    
}
