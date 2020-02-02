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

import uk.co.samicemalone.libtv.model.EpisodeMatch;
import uk.co.samicemalone.tv.ExitCode;
import uk.co.samicemalone.tv.exception.ExitException;
import uk.co.samicemalone.tv.exception.InvalidArgumentException;
import uk.co.samicemalone.tv.exception.TraktException;
import uk.co.samicemalone.tv.model.Episode;
import uk.co.samicemalone.tv.plugin.TraktPlugin;
import uk.co.samicemalone.tv.tvdb.model.Show;
import uk.co.samicemalone.tv.tvdb.model.TraktShowProgressQueue;

import java.time.Instant;
import java.util.List;

import static uk.co.samicemalone.tv.TV.ENV;

/**
 *
 * @author Sam Malone
 */
public class TraktMarkAction implements Action {
    
    /** if true, mark as seen, otherwise mark unseen **/
    private final boolean isMarkAsSeen;
    private final TraktPlugin traktPlugin;

    public TraktMarkAction(TraktPlugin traktPlugin, int markAction) {
        this.traktPlugin = traktPlugin;
        this.isMarkAsSeen = markAction == Action.SEEN;
    }

    @Override
    public boolean isAction(int action) {
        return action == Action.SEEN || action == Action.UNSEEN;
    }

    @Override
    public void execute(Show show, List<EpisodeMatch> list) throws ExitException {
        if(isMarkAsSeen && ENV.getArguments().isIgnoreSet()) {
            throw new InvalidArgumentException("--seen action and -i flag cannot be set together", ExitCode.UNEXPECTED_ARGUMENT);
        } else if(!isMarkAsSeen && ENV.getArguments().isSetOnly()) {
            throw new InvalidArgumentException("--unseen action and -s flag cannot be set together", ExitCode.UNEXPECTED_ARGUMENT);
        } else if(!ENV.isTraktEnabled()) {
            throw new InvalidArgumentException("Trakt is disabled. Enable it via config before you can mark as seen/unseen", ExitCode.TRAKT_ERROR);
        }

        try {
            String tag = ENV.getArguments().getUser();
            for (EpisodeMatch episodeMatch : list) {
                Episode e = new Episode(episodeMatch, show.getName(), tag).setWatchedAt(Instant.now());
                String markType = isMarkAsSeen ? TraktShowProgressQueue.SEEN : TraktShowProgressQueue.UNSEEN;
                traktPlugin.addEpisodeToQueue(show, e, markType);
            }
        } catch (TraktException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
