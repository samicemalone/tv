/*
 * Copyright (c) 2013, Sam Malone
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package tv.mode;

import java.io.File;
import tv.ExitCode;
import tv.TV;
import tv.TVScan;
import tv.exception.ExitException;
import tv.exception.FileNotFoundException;
import tv.exception.SeasonNotFoundException;
import tv.model.Arguments;
import tv.model.Episode;
import tv.model.EpisodeRange;
import tv.model.Range;
import tv.model.Season;

/**
 * EpisodeMode should be used for modes that do not involve using the pointer.
 * For episode modes that can read and write to a pointer, see {@link PointerMode}.
 * For episode modes that only need to write to a pointer, see {@link WriteOnlyPointerMode}.
 * @author Sam Malone
 */
public class EpisodeMode {
    
    private final int mode;
    private final TVScan tvScanner;
    
    /**
     * Creates a new EpisodeMode instance
     * @param mode EpisodeModes Episode Mode
     * @param scanner TV Scanner
     * @see EpisodeModes
     */
    public EpisodeMode(int mode, TVScan scanner) {
        this.mode = mode;
        this.tvScanner = scanner;
    }
    
    /**
     * Get the starting season for the current episode mode
     * @return Starting Season
     */
    private Season getStartingSeason() {
        Arguments args = TV.ENV.getArguments();
        switch(mode) {
            case EpisodeModes.ALL:
                return getTvScanner().getSeason(args.getShow(), 1);
            case EpisodeModes.LATEST_SEASON:
                int season = Integer.valueOf(getTvScanner().getLastSeasonNo(args.getShow()));
                return getTvScanner().getSeason(args.getShow(), season);
        }
        return getTvScanner().getSeason(args.getShow(), TVScan.getSeasonNo(args.getEpisodes()));
    }
    
    /**
     * Get the EpisodesMode that represents this EpisodeMode.
     * @see EpisodeModes
     * @return Episode Mode
     */
    public int getMode() {
        return mode;
    }

    /**
     * Get the TvScanner to be used to scan for content
     * @return TvScanner
     */
    public TVScan getTvScanner() {
        return tvScanner;
    }
    
    /**
     * Build the list of episode Files as specified by the episode mode
     * @return List of episode Files or empty File array if none found
     * @throws ExitException if unable to determine the list of files for the given mode
     */
    public File[] buildFileList() throws ExitException {
        Season season = getStartingSeason();
        assertStartingSeasonValid(season);
        String episodeString = TV.ENV.getArguments().getEpisodes();
        switch(mode) {
            case EpisodeModes.SEASON:
                return seasonFromEpisode(season, "00");
            case EpisodeModes.SEASONFROMEP:
                return seasonFromEpisode(season, TVScan.getEpisodeNo(episodeString));
            case EpisodeModes.EPRANGE:
                return episodeRange(season, EpisodeRange.fromArray(episodeString.split("-")));
            case EpisodeModes.ALL:
                return allEpisodes();
            case EpisodeModes.SEASONRANGE:
                return seasonRange(season, Range.fromSeason(episodeString.split("-")));
            case EpisodeModes.ALLFROMSEASON:
                return allFromSeason(season);
            case EpisodeModes.LATEST_SEASON:
                return seasonFromEpisode(season, "00");
        }
        return new File[] {};
    }
    
    /**
     * Assert that the given season is valid
     * @param season season
     * @throws SeasonNotFoundException if the season cannot be found
     */
    public static void assertStartingSeasonValid(Season season) throws SeasonNotFoundException {
        if(season.getSeasonDir() == null || !season.getSeasonDir().exists()) {
            throw new SeasonNotFoundException("Season " + season.getSeasonNo() + " could not be found");
        }
    }
    
    /**
     * Get the list of all episode Files
     * @return list of all episode Files
     * @throws ExitException if unable to find any episodes
     */
    public File[] allEpisodes() throws ExitException {
        File[] eps = getTvScanner().getAllEpisodes(TV.ENV.getArguments().getShow());
        if(eps == null || eps.length == 0) {
            throw new ExitException("Unable to find any episodes", ExitCode.EPISODES_NOT_FOUND);
        }
        return eps;
    }
    
    /**
     * Gets the episodes Files in given episode range
     * @param season Starting season in the range i.e. the season for rangeArray[0]
     * @param range Range of episodes
     * @return List of episode Files in the given range
     * @throws ExitException if unable to find any episodes in the given range
     */
    public File[] episodeRange(Season season, EpisodeRange range) throws ExitException {
        File[] list = getTvScanner().getEpisodeRange(season, TV.ENV.getArguments().getShow(), range);
        if(list == null || list.length == 0) {
            throw new ExitException("Unable to find any episodes in the given range", ExitCode.EPISODES_RANGE_NOT_FOUND);
        }
        return list;
    }
    
    /**
     * Gets the episodes including and greater than epString in the same season
     * @param season Season
     * @param epString Episode No e.g. 02
     * @return List of episode Files
     * @throws ExitException if unable to find any episodes in the given range
     */
    public File[] seasonFromEpisode(Season season, String epString) throws ExitException {
        File[] eps = getTvScanner().getEpisodesFrom(season, Integer.valueOf(epString));
        if(eps == null || eps.length == 0) {
            throw new ExitException("Unable to find any episodes in the given range", ExitCode.EPISODES_RANGE_NOT_FOUND);
        }
        return eps;
    }

    /**
     * Get all the episodes from the given season range
     * @param season Starting season
     * @param seasonRange Range of seasons inclusive
     * @return List of episode Files in the season range
     * @throws ExitException if unable to fid the seasons in the given range
     */
    public File[] seasonRange(Season season, Range seasonRange) throws ExitException {
        File[] range = getTvScanner().getSeasonRange(season, TV.ENV.getArguments().getShow(), seasonRange);
        if(range == null || range.length == 0) {
            throw new ExitException("Unable to find the seasons in the given range", ExitCode.SEASON_RANGE_NOT_FOUND);
        }
        return range;
    }

    /**
     * Get all the episodes from the given season, and any remaining seasons
     * @param season Starting season
     * @return List of episode Files
     * @throws ExitException if unable to find the episodes in the given range
     */
    public File[] allFromSeason(Season season) throws ExitException {
        File[] eps = getTvScanner().getSeasonsFrom(season, TV.ENV.getArguments().getShow());
        if(eps == null || eps.length == 0) {
            throw new ExitException("Unable to find the episodes in the given range", ExitCode.SEASON_RANGE_NOT_FOUND);
        }
        return eps;
    }
    
    /**
     * Get the new episode pointer to be set
     * @return new episode pointer or null if one should be not be set
     * @throws tv.exception.ExitException
     */
    public Episode getNewPointer() throws ExitException {
        return null;
    }
    
}
