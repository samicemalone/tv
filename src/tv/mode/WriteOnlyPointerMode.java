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
import tv.model.Arguments;
import tv.model.Episode;
import tv.model.Season;

/**
 * WriteOnlyPointerMode should be used for modes that write an pointer but do
 * not read from it i.e. Setting a new pointer only, not using the existing pointer
 * with an offset.
 * @author Sam Malone
 */
public class WriteOnlyPointerMode extends EpisodeMode {
    
    /**
     * Create a new WriteOnlyPointerMode instance.
     * @param mode EpisodeModes Episode Mode
     * @see EpisodeModes
     */
    public WriteOnlyPointerMode(int mode) {
        super(mode);
    }
    
    /**
     * Get the Season that the specified write-only pointer refers to
     * @return Season
     */
    private Season getSeason() {
        Arguments args = TV.ENV.getArguments();
        switch(getMode()) {
            case EpisodeModes.PILOT: return new Season(args.getShow(), 1);
            case EpisodeModes.LATEST: return new Season(args.getShow(), TVScan.getLastSeasonNo(args.getShow()));
        }
        return new Season(args.getShow(), TVScan.getSeasonNo(args.getEpisodes()));
    }

    @Override
    public File[] buildFileList() throws ExitException {
        Season season = getSeason();
        assertStartingSeasonValid(season);
        switch(getMode()) {
            case EpisodeModes.EPSINGLE: return new File[] { singleEpisode(season) }; 
            case EpisodeModes.PILOT: return new File[] { singleEpisode(season, "01") };
            case EpisodeModes.LATEST: return new File[] { singleEpisode(season, TVScan.getLastEpisodeNo(season)) };
        }
        return new File[] {};
    }

    /**
     * Get the new episode pointer to be set
     * @return new episode pointer or null if one should be not be set
     * @throws ExitException if the season given isn't valid
     */
    @Override
    public Episode getNewPointer() throws ExitException {
        Arguments args = TV.ENV.getArguments();
        Season season = getSeason();
        assertStartingSeasonValid(season);
        switch(getMode()) {
            case EpisodeModes.PILOT: 
                return new Episode(args.getShow(), args.getUser(), season.getSeasonString(), "01");
            case EpisodeModes.LATEST: 
                return new Episode(args.getShow(), args.getUser(), season.getSeasonString(), TVScan.getLastEpisodeNo(season));
        }
        return new Episode(args.getShow(), args.getUser(), season.getSeasonString(), TVScan.getEpisodeNo(args.getEpisodes())); 
    }
    
    
    /**
     * Get the episode File given by the season and episode number
     * @param season Season Season the episode is in
     * @param episodeNo Episode No
     * @return episode File
     * @throws ExitException if unable to find the episode given
     */
    public File singleEpisode(Season season, String episodeNo) throws ExitException {
        File ep = TVScan.getEpisode(season, episodeNo);
        if(ep == null) {
            throw new ExitException("Unable to find the episode given", ExitCode.EPISODES_NOT_FOUND);
        }
        return ep;
    }
    
    /**
     * Wrapper method for singleEpisode(Season, episodeNo) using the users episode string
     * to determine episode number
     * @param season Season Season the episode is in
     * @return episode File
     * @throws ExitException if unable to find the episode given
     */
    public File singleEpisode(Season season) throws ExitException {
        return singleEpisode(season, TVScan.getEpisodeNo(TV.ENV.getArguments().getEpisodes()));
    }
    
}
