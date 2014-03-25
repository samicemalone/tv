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

import com.jakewharton.trakt.entities.TvShowProgress;
import java.io.File;
import tv.ExitCode;
import tv.NavigableEpisode;
import tv.TV;
import tv.TVScan;
import tv.TraktClient;
import tv.exception.CancellationException;
import tv.exception.ExitException;
import tv.exception.FileNotFoundException;
import tv.exception.SeasonNotFoundException;
import tv.exception.TraktException;
import tv.io.TVDBManager;
import tv.model.Arguments;
import tv.model.Episode;
import tv.model.Season;

/**
 *
 * @author Sam Malone
 */
public class PointerMode extends EpisodeMode {
    
    private Episode currentPointer;
    private Episode newPointer;

    public PointerMode(int mode, TVScan scanner) {
        super(mode, scanner);
    }
    
    public PointerMode readCurrentPointer() throws ExitException {
        boolean useTraktPointer = TV.ENV.isTraktEnabled() && TV.ENV.getArguments().isTraktPointerSet();
        currentPointer = useTraktPointer ? getCurrentTraktPointer() : getCurrentTVDBPointer();
        setNewPointer();
        return this;
    }

    /**
     * Get the season the episode that the episode pointer points to
     * @return Season
     */
    private Season getSeason() {
        int season = Integer.valueOf(newPointer.getSeasonNo());
        return getTvScanner().getSeason(TV.ENV.getArguments().getShow(), season);
    }
    
    /**
     * Copies the current pointer to {@link #newPointer} and modifies the new pointer
     * by the offset specified by the episode string.
     */
    private void setNewPointer() throws SeasonNotFoundException {
        if(newPointer == null) {
            newPointer = new Episode(currentPointer);
            new NavigableEpisode(getTvScanner()).navigate(newPointer, TV.ENV.getArguments().getEpisodes());
        }
    }
    
    @Override
    public File[] buildFileList() throws ExitException {
        Season season = getSeason();
        assertStartingSeasonValid(season);
        switch(getMode()) {
            case EpisodeModes.POINTER: return new File[] { episodePointer(season) };
            case EpisodeModes.SEASONFROMPOINTER: return seasonFromEpisode(season, newPointer.getEpisodeNo());
        }
        return new File[] {};
    }
    
    private Episode getCurrentTraktPointer() throws ExitException {
        TraktClient trakt = new TraktClient(TV.ENV.getTraktCredentials());
        Arguments args = TV.ENV.getArguments();
        try {
            TvShowProgress.NextEpisode next = trakt.getNextEpisode(args.getShow());
            if(next != null) {
                Episode curPointer = new Episode(args.getShow(), args.getUser(), next.season, next.number);
                return new NavigableEpisode(getTvScanner()).navigate(curPointer, "prev");
            }
        } catch (TraktException e) {
            throw new ExitException(e.getMessage(), ExitCode.TRAKT_ERROR);
        } catch (CancellationException e) {
            throw new ExitException("exiting: user cancelled whilst choosing the matching show search result.", ExitCode.SHOW_INPUT_REQUIRED);
        }
        throw new ExitException("trakt: there is no next episode for this show", ExitCode.EPISODES_NOT_FOUND);
    }

    
    /**
     * Read the TVDB file to get the current pointer for the given show
     * @return Current episode pointer
     * @throws ExitException if unable to find the TVDB file or the current 
     * episode file or if there is stored episode data for the show
     */
    private static Episode getCurrentTVDBPointer() throws ExitException {
        TVDBManager io = new TVDBManager(TV.ENV.getTVDB());
        Episode curEp;
        try {
            io.readStorage(TV.ENV.getArguments().getUser());
            if(!io.containsEpisodeData(TV.ENV.getArguments().getShow())) {
                throw new ExitException("There is no episode data stored for this show", ExitCode.NO_STORED_EPISODE_DATA);
            }
            if((curEp = io.getEpisode(TV.ENV.getArguments().getShow())) == null) {
                throw new FileNotFoundException("Could not find the episode stored as the current pointer", ExitCode.EPISODES_NOT_FOUND);
            }
        } catch(java.io.FileNotFoundException e) {
            throw new FileNotFoundException("The TVDB could not be found", ExitCode.FILE_NOT_FOUND);
        }
        return curEp;
    }
    
    /**
     * Get the new episode pointer to be set
     * @return new episode pointer or null if one should be not be set
     * @throws ExitException if there is no episode data stored for the show
     */
    @Override
    public Episode getNewPointer() throws ExitException {
        switch(getMode()) {
            case EpisodeModes.POINTER:
                if(newPointer == null) {
                    throw new ExitException("There is no episode data stored for this show", ExitCode.NO_STORED_EPISODE_DATA);
                }
                return newPointer;
        }
        return null;
    }

    /**
     * Get the requested episode File as from the given episode pointer
     * @param season Season that the episode pointer points to
     * @return episode File the episode pointer points to
     * @throws ExitException if unable to find the episode at the pointer offset
     * given.
     */
    public File episodePointer(Season season) throws ExitException {
        File ep = TVScan.getEpisode(season, newPointer.getEpisodeNo());
        if(ep == null) {
            throw new ExitException("Unable to find the episode offset given", ExitCode.EPISODE_POINTER_INVALID);
        }
        return ep;
    }
    
}
