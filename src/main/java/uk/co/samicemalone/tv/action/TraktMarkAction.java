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

package uk.co.samicemalone.tv.action;

import com.jakewharton.trakt.services.ShowService;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import uk.co.samicemalone.libtv.matcher.TVMatcher;
import uk.co.samicemalone.libtv.model.EpisodeMatch;
import uk.co.samicemalone.libtv.model.SeasonsMap;
import uk.co.samicemalone.tv.ExitCode;
import uk.co.samicemalone.tv.TV;
import uk.co.samicemalone.tv.exception.CancellationException;
import uk.co.samicemalone.tv.exception.ExitException;
import uk.co.samicemalone.tv.exception.InvalidArgumentException;
import uk.co.samicemalone.tv.exception.TraktException;
import uk.co.samicemalone.tv.model.Episode;
import uk.co.samicemalone.tv.trakt.TraktBuilder;
import uk.co.samicemalone.tv.trakt.TraktClient;

/**
 *
 * @author Sam Malone
 */
public class TraktMarkAction implements Action {
    
    /** if true, mark as seen, otherwise mark unseen **/
    private final boolean isMarkAsSeen;

    public TraktMarkAction(boolean isMarkAsSeen) {
        this.isMarkAsSeen = isMarkAsSeen;
    }

    @Override
    public void execute(List<EpisodeMatch> list, Episode pointerEpisode) throws ExitException {
        if(isMarkAsSeen && TV.ENV.getArguments().isIgnoreSet()) {
            throw new InvalidArgumentException("--seen action and -i flag cannot be set together", ExitCode.UNEXPECTED_ARGUMENT);
        } else if(!isMarkAsSeen && TV.ENV.getArguments().isSetOnly()) {
            throw new InvalidArgumentException("--unseen action and -s flag cannot be set together", ExitCode.UNEXPECTED_ARGUMENT);
        } else if(!TV.ENV.isTraktEnabled()) {
            throw new InvalidArgumentException("Trakt is disabled. Enable it via config before you can mark as seen/unseen", ExitCode.TRAKT_ERROR);
        }
        TraktClient trakt = new TraktClient(TV.ENV.getTraktCredentials());
        SeasonsMap seasons = new SeasonsMap(list);
        int lastPlayed = (int) (System.currentTimeMillis() / 1000L);
        int markType = isMarkAsSeen ? TraktClient.SEEN : TraktClient.UNSEEN;
        EpisodeMatch cur = null;
        try {
            int showId = trakt.getShowId(TV.ENV.getArguments().getShow());
            for(int season : seasons.getSeasons()) {
                List<ShowService.Episodes.Episode> apiList = new ArrayList<>();
                for(EpisodeMatch m : seasons.getSeasonEpisodes(season)) {
                    cur = m;
                    TraktBuilder.buildMarkableEpisodes(apiList, m, lastPlayed);
                    trakt.markEpisodesAs(markType, new ShowService.Episodes(showId, apiList));
                }
            }
        } catch(TraktException ex) {
            System.err.println(ex.getMessage());
            if(isMarkAsSeen && cur != null) {
                Episode e = new Episode(cur, TV.ENV.getArguments().getShow(), "");
                e.setPlayedDate(lastPlayed);
                trakt.addEpisodeToJournal(e);
            }
        } catch(CancellationException ex) {
            System.err.println(ex.getMessage());
        }
    }

    @Override
    public void execute(File file) throws ExitException {
        EpisodeMatch m = new TVMatcher().matchElement(file.toPath(), TVMatcher.MatchElement.ALL);
        if(m == null) {
            throw new ExitException("Unable to match show, season or episodes from " + file, ExitCode.PARSE_EPISODES_FAILED);
        } else if(!TV.ENV.isTraktEnabled()) {
            throw new InvalidArgumentException("Trakt is disabled. Enable it via config before you can mark as seen/unseen", ExitCode.TRAKT_ERROR);
        }
        TraktClient trakt = new TraktClient(TV.ENV.getTraktCredentials());
        Episode episode = new Episode(m, m.getShow(), TV.ENV.getArguments().getUser());
        try {
            trakt.markEpisodeAs(isMarkAsSeen ? TraktClient.SEEN : TraktClient.UNSEEN, episode);
        } catch (TraktException ex) {
            System.err.println(ex.getMessage());
            if(isMarkAsSeen) {
                trakt.addEpisodeToJournal(episode);
            }
        } catch (CancellationException ex) {

        }
    }    
    
}
