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

import java.util.ArrayList;
import java.util.List;
import uk.co.samicemalone.tv.ExitCode;
import uk.co.samicemalone.tv.TV;
import uk.co.samicemalone.tv.TVScan;
import uk.co.samicemalone.tv.exception.ExitException;
import uk.co.samicemalone.tv.model.Arguments;
import uk.co.samicemalone.tv.model.Episode;
import uk.co.samicemalone.tv.model.EpisodeMatch;

/**
 * WriteOnlyPointerMode should be used for modes that write an pointer but do
 * not read from it i.e. Setting a new pointer only, not using the existing pointer
 * with an offset.
 * @author Sam Malone
 */
public class WriteOnlyPointerMode extends EpisodeMode {
    
    public WriteOnlyPointerMode(int mode, TVScan scanner) {
        super(mode, scanner);
    }

    @Override
    public List<EpisodeMatch> findMatches() throws ExitException {
        String show = TV.ENV.getArguments().getShow();
        List<EpisodeMatch> matches = new ArrayList<>();
        switch(getMode()) {
            case EpisodeModes.EPSINGLE:
                int season = TVScan.getSeasonNo(TV.ENV.getArguments().getEpisodes());
                int episode = Integer.valueOf(TVScan.getEpisodeNo(TV.ENV.getArguments().getEpisodes()));
                matches.add(getTvMatcher().matchEpisode(show, season, episode));
                break;
            case EpisodeModes.PILOT:
                matches.add(getTvMatcher().matchEpisode(show, 1, 1));
                break;
            case EpisodeModes.LATEST: 
                matches.add(getTvMatcher().matchLatestEpisode(show));
                break;
        }
        return matches;
    }
    
    @Override
    public List<EpisodeMatch> findMatchesOrThrow() throws ExitException {
        List<EpisodeMatch> matches = findMatches();
        if(matches.isEmpty()) {
            throw new ExitException("Unable to match the episode given", ExitCode.EPISODES_NOT_FOUND);
        }
        return matches;
    }

    @Override
    public Episode getNewPointer(EpisodeMatch match) throws ExitException {
        Arguments args = TV.ENV.getArguments();
        return new Episode(args.getShow(), args.getUser(), match);
    }
    
}
