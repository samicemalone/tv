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

package uk.co.samicemalone.tv.selector;

import uk.co.samicemalone.libtv.matcher.path.TVPath;
import uk.co.samicemalone.libtv.model.EpisodeMatch;
import uk.co.samicemalone.libtv.model.EpisodeNavigator;
import uk.co.samicemalone.tv.ExitCode;
import uk.co.samicemalone.tv.TV;
import uk.co.samicemalone.tv.exception.ExitException;
import uk.co.samicemalone.tv.model.Arguments;
import uk.co.samicemalone.tv.model.Episode;
import uk.co.samicemalone.tv.tvdb.TVDatabase;
import uk.co.samicemalone.tv.tvdb.model.Show;
import uk.co.samicemalone.tv.tvdb.model.ShowProgress;
import uk.co.samicemalone.tv.util.TVUtil;

import java.io.IOException;
import java.util.List;

/**
 * @author Sam Malone
 */
public class RemainingEpisodesInSeasonFromProgressSelector extends EpisodeProgressSelector {
    private EpisodeNavigator episodeNavigator;

    public RemainingEpisodesInSeasonFromProgressSelector(TVPath tvPath, TVDatabase tvDatabase) {
        super(tvPath, tvDatabase);
        episodeNavigator = new EpisodeNavigator(getTVEpisodeMatcher(), tvPath);
    }

    @Override
    public String getMode() {
        return "REMAINING_EPISODES_IN_SEASON_FROM_POINTER";
    }

    @Override
    public String getSelector() {
        return "(prev|current|cur|next)-$";
    }

    @Override
    public List<EpisodeMatch> findMatches() throws IOException, ExitException {
        Arguments args = TV.ENV.getArguments();
        String episodePointer = args.getEpisodes().substring(0, args.getEpisodes().length() - 1);
        EpisodeNavigator.Pointer offset = TVUtil.getNavigationPointer(episodePointer);
        ShowProgress currentProgress = getCurrentProgress(new Show(args.getShow()), args.getUser());
        Episode currentEpisode = currentProgress.toEpisode();
        EpisodeMatch m = episodeNavigator.navigate(currentEpisode, offset);
        if(m == null) {
            String message = String.format("Unable to find the episode to navigate to. (current = %s)", currentEpisode);
            throw new ExitException(message, ExitCode.EPISODES_NOT_FOUND);
        }
        int episode = m.getEpisodesAsRange().getStart();
        return getTVEpisodeMatcher().matchEpisodesFrom(args.getShow(), m.getSeason(), episode);
    }
    
    @Override
    public List<EpisodeMatch> findMatchesOrThrow() throws IOException, ExitException {
        List<EpisodeMatch> matches = findMatches();
        if(matches.isEmpty()) {
            throw new ExitException("Unable to match the episode given", ExitCode.EPISODES_NOT_FOUND);
        }
        return matches;
    }
}
