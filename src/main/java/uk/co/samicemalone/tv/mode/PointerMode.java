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

package uk.co.samicemalone.tv.mode;

import com.uwetrottmann.trakt.v2.exceptions.OAuthUnauthorizedException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import retrofit.RetrofitError;
import uk.co.samicemalone.libtv.matcher.path.TVPath;
import uk.co.samicemalone.libtv.model.EpisodeMatch;
import uk.co.samicemalone.libtv.model.EpisodeNavigator;
import uk.co.samicemalone.tv.ExitCode;
import uk.co.samicemalone.tv.TV;
import uk.co.samicemalone.tv.exception.CancellationException;
import uk.co.samicemalone.tv.exception.ExitException;
import uk.co.samicemalone.tv.exception.FileNotFoundException;
import uk.co.samicemalone.tv.exception.TraktException;
import uk.co.samicemalone.tv.io.TVDBManager;
import uk.co.samicemalone.tv.model.Arguments;
import uk.co.samicemalone.tv.model.Episode;
import uk.co.samicemalone.tv.trakt.TraktClient;
import uk.co.samicemalone.tv.util.TVUtil;

/**
 *
 * @author Sam Malone
 */
public class PointerMode extends EpisodeMode {
    
    private final EpisodeNavigator episodeNav;
    
    private Episode currentPointer;

    public PointerMode(int mode, TVPath tvPath) {
        super(mode, tvPath);
        episodeNav = new EpisodeNavigator(getTVEpisodeMatcher(), tvPath);
    }
    
    public PointerMode readCurrentPointer() throws ExitException {
        boolean useTraktPointer = TV.ENV.isTraktEnabled() && TV.ENV.getArguments().isTraktPointerSet();
        currentPointer = useTraktPointer ? getCurrentTraktPointer() : getCurrentTVDBPointer();
        return this;
    }
    
    @Override
    public List<EpisodeMatch> findMatches() throws IOException, ExitException {
        EpisodeNavigator.Pointer offset = TVUtil.getNavigationPointer(TV.ENV.getArguments().getEpisodes());
        EpisodeMatch m = episodeNav.navigate(currentPointer, offset);
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
                matches.addAll(getTVEpisodeMatcher().matchEpisodesFrom(show, m.getSeason(), episode));
                break;
        }
        return matches;
    }
    
    @Override
    public List<EpisodeMatch> findMatchesOrThrow() throws IOException, ExitException {
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
        return new Episode(match, args.getShow(), args.getUser());
    }
    
    private Episode getCurrentTraktPointer() throws ExitException {
        TraktClient trakt = new TraktClient();
        Arguments args = TV.ENV.getArguments();
        try {
            trakt.authenticate(TV.ENV.getTraktAuthFile());
            Episode next = trakt.getNextEpisode(args.getShow());
            if(next != null) {
                EpisodeMatch m = episodeNav.navigate(next, EpisodeNavigator.Pointer.PREV);
                if(m == null) {
                    String message = String.format("unable to find the current episode after fetching the trakt pointer (current = %s)", next);
                    throw new ExitException(message, ExitCode.EPISODES_NOT_FOUND);
                }
                return new Episode(m, args.getShow(), args.getUser());
            }
        } catch (TraktException | OAuthSystemException | OAuthProblemException | OAuthUnauthorizedException | RetrofitError e) {
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
