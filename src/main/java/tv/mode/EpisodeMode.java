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
import tv.model.Episode;
import tv.model.Season;

/**
 * EpisodeMode should be used for modes that do not involve using the pointer.
 * For episode modes that can read and write to a pointer, see {@link PointerMode}.
 * For episode modes that only need to write to a pointer, see {@link WriteOnlyPointerMode}.
 * @author Sam Malone
 */
public class EpisodeMode {
    
    private final int mode;
    
    /**
     * Creates a new EpisodeMode instance
     * @param mode EpisodeModes Episode Mode
     * @see EpisodeModes
     */
    public EpisodeMode(int mode) {
        this.mode = mode;
    }
    
    /**
     * Get the starting season for the current episode mode
     * @return Starting Season
     */
    private Season getStartingSeason() {
        switch(mode) {
            case EpisodeModes.ALL:
                return new Season(TV.ENV.getArguments().getShow(), "01");
            case EpisodeModes.LATEST_SEASON:
                return new Season(TV.ENV.getArguments().getShow(), TVScan.getLastSeasonNo(TV.ENV.getArguments().getShow()));
        }
        return new Season(TV.ENV.getArguments().getShow(), TVScan.getSeasonNo(TV.ENV.getArguments().getEpisodes()));
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
     * Build the list of episode Files as specified by the episode mode
     * @return List of episode Files or empty File array if none found
     * @throws ExitException if unable to determine the list of files for the given mode
     */
    public File[] buildFileList() throws ExitException {
        Season season = getStartingSeason();
        assertStartingSeasonValid(season);
        switch(mode) {
            case EpisodeModes.SEASON:
                return seasonFromEpisode(season, "00");
            case EpisodeModes.SEASONFROMEP:
                return seasonFromEpisode(season, TVScan.getEpisodeNo(TV.ENV.getArguments().getEpisodes()));
            case EpisodeModes.EPRANGE:
                return episodeRange(season, TV.ENV.getArguments().getEpisodes().split("-"));
            case EpisodeModes.ALL:
                return allEpisodes(season);
            case EpisodeModes.SEASONRANGE:
                return seasonRange(season, TV.ENV.getArguments().getEpisodes().split("-"));
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
     * @throws FileNotFoundException if the season cannot be found
     */
    public static void assertStartingSeasonValid(Season season) throws FileNotFoundException {
        if(season.getSeasonDir() == null || !season.getSeasonDir().exists()) {
            throw new FileNotFoundException("Season could not be found", ExitCode.SEASON_NOT_FOUND);
        }
    }
    
    /**
     * Get the list of all episode Files
     * @param season Starting season (usually 1)
     * @return list of all episode Files
     * @throws ExitException if unable to find any episodes
     */
    public static File[] allEpisodes(Season season) throws ExitException {
        File[] eps = TVScan.getAllEpisodes(season, TV.ENV.getArguments().getShow());
        if(eps == null || eps.length == 0) {
            throw new ExitException("Unable to find any episodes", ExitCode.EPISODES_NOT_FOUND);
        }
        return eps;
    }
    
    /**
     * Gets the episodes Files in given episode range
     * @param season Starting season in the range i.e. the season for rangeArray[0]
     * @param rangeArray rangeArray[0]: start episode, rangeArray[1]: end episode
     * @return List of episode Files in the given range
     * @throws ExitException if unable to find any episodes in the given range
     */
    public static File[] episodeRange(Season season, String[] rangeArray) throws ExitException {
        File[] eprange = TVScan.getEpisodeRange(season, TV.ENV.getArguments().getShow(), rangeArray[0], rangeArray[1]);
        if(eprange == null || eprange.length == 0) {
            throw new ExitException("Unable to find any episodes in the given range", ExitCode.EPISODES_RANGE_NOT_FOUND);
        }
        return eprange;
    }
    
    /**
     * Gets the episodes including and greater than epString in the same season
     * @param season Season
     * @param epString Episode No e.g. 02
     * @return List of episode Files
     * @throws ExitException if unable to find any episodes in the given range
     */
    public static File[] seasonFromEpisode(Season season, String epString) throws ExitException {
        File[] eps = TVScan.getEpisodesFrom(season, epString);
        if(eps == null || eps.length == 0) {
            throw new ExitException("Unable to find any episodes in the given range", ExitCode.EPISODES_RANGE_NOT_FOUND);
        }
        return eps;
    }

    /**
     * Get all the episodes from the given season range
     * @param season Starting season
     * @param rangeArray rangeArray[0]: start season, rangeArray[1]: end season e.g. s03
     * @return List of episode Files in the season range
     * @throws ExitException if unable to fid the seasons in the given range
     */
    public static File[] seasonRange(Season season, String[] rangeArray) throws ExitException {
        File[] range = TVScan.getSeasonRange(season, TV.ENV.getArguments().getShow(), rangeArray[0], rangeArray[1]);
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
    public static File[] allFromSeason(Season season) throws ExitException {
        File[] eps = TVScan.getSeasonsFrom(season, TV.ENV.getArguments().getShow());
        if(eps == null || eps.length == 0) {
            throw new ExitException("Unable to find the episodes in the given range", ExitCode.SEASON_RANGE_NOT_FOUND);
        }
        return eps;
    }
    
    /**
     * Get the new episode pointer to be set
     * @return new episode pointer or null if one should be not be set
     */
    public Episode getNewPointer() throws ExitException {
        return null;
    }
    
}
