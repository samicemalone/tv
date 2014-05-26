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
import java.util.ArrayList;
import java.util.List;
import tv.ExitCode;
import tv.NavigableEpisode;
import tv.TV;
import tv.TVScan;
import tv.TraktClient;
import tv.exception.CancellationException;
import tv.exception.ExitException;
import tv.exception.FileNotFoundException;
import tv.exception.TraktException;
import tv.io.TVDBManager;
import tv.model.Arguments;
import tv.model.Episode;
import tv.model.EpisodeMatch;

/**
 *
 * @author Sam Malone
 */
public class PointerMode extends EpisodeMode {
    
    private Episode currentPointer;

    public PointerMode(int mode, TVScan scanner) {
        super(mode, scanner);
    }
    
    public PointerMode readCurrentPointer() throws ExitException {
        boolean useTraktPointer = TV.ENV.isTraktEnabled() && TV.ENV.getArguments().isTraktPointerSet();
        currentPointer = useTraktPointer ? getCurrentTraktPointer() : getCurrentTVDBPointer();
        return this;
    }
    
    @Override
    public List<EpisodeMatch> findMatches() throws ExitException {
        String offset = TV.ENV.getArguments().getEpisodes();
        EpisodeMatch m = new NavigableEpisode(getTvMatcher()).navigate(currentPointer, offset);
        if(m == null) {
            String message = String.format("Unable to find the episode to navigate to. (current = %s)", currentPointer);
            throw new ExitException(message, ExitCode.EPISODES_NOT_FOUND);
        }
        String show = TV.ENV.getArguments().getShow();
        List<EpisodeMatch> matches = new ArrayList<>();
        switch(getMode()) {
            case EpisodeModes.POINTER:
                matches.add(m);
                break;
            case EpisodeModes.SEASONFROMPOINTER:
                int episode = m.getEpisodesAsRange().getStart();
                matches.addAll(getTvMatcher().matchEpisodesFrom(show, m.getSeason(), episode));
                break;
        }
        return matches;
    }
    
    @Override
    public List<EpisodeMatch> findMatchesOrThrow() throws ExitException {
        List<EpisodeMatch> matches = findMatches();
        if(matches.isEmpty()) {
            if(getMode() == EpisodeModes.SEASONFROMPOINTER) {
                throw new ExitException("Unable to match the episodes from the given pointer", ExitCode.EPISODES_NOT_FOUND);
            }
            throw new ExitException("Unable to match the episode given", ExitCode.EPISODES_NOT_FOUND);
        }
        return matches;
    }
    
    @Override
    public Episode getNewPointer(EpisodeMatch match) throws ExitException {
        Arguments args = TV.ENV.getArguments();
        return new Episode(args.getShow(), args.getUser(), match);
    }
    
    private Episode getCurrentTraktPointer() throws ExitException {
        TraktClient trakt = new TraktClient(TV.ENV.getTraktCredentials());
        Arguments args = TV.ENV.getArguments();
        try {
            TvShowProgress.NextEpisode next = trakt.getNextEpisode(args.getShow());
            if(next != null) {
                Episode nextEp = new Episode(args.getShow(), args.getUser(), next.season, next.number);
                EpisodeMatch m = new NavigableEpisode(getTvMatcher()).navigate(nextEp, "prev");
                if(m == null) {
                    String message = String.format("unable to find the current episode after fetching the trakt pointer (current = %s)", nextEp);
                    throw new ExitException(message, ExitCode.EPISODES_NOT_FOUND);
                }
                return new Episode(args.getShow(), args.getUser(), m);
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
    
}
