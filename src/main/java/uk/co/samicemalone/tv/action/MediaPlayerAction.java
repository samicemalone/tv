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

package uk.co.samicemalone.tv.action;

import com.uwetrottmann.trakt.v2.exceptions.OAuthUnauthorizedException;
import java.io.File;
import java.util.List;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import retrofit.RetrofitError;
import uk.co.samicemalone.libtv.model.EpisodeMatch;
import uk.co.samicemalone.tv.TV;
import uk.co.samicemalone.tv.exception.CancellationException;
import uk.co.samicemalone.tv.exception.ExitException;
import uk.co.samicemalone.tv.exception.TraktException;
import uk.co.samicemalone.tv.io.TVDBManager;
import uk.co.samicemalone.tv.model.Episode;
import uk.co.samicemalone.tv.player.MediaPlayer;
import uk.co.samicemalone.tv.player.MediaPlayerFactory;
import uk.co.samicemalone.tv.trakt.TraktClient;

/**
 *
 * @author Sam Malone
 */
public class MediaPlayerAction implements Action {
    
    private int action = Action.PLAY;

    public MediaPlayerAction(int action) {
        this.action = action;
    }

    @Override
    public int hashCode() {
        return 47 * 7 + this.action;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final MediaPlayerAction other = (MediaPlayerAction) obj;
        return this.action == other.action;
    }
    
    private void execute(File[] files) {
        MediaPlayer player = MediaPlayerFactory.parsePlayer(TV.ENV.getArguments().getPlayerInfo());
        switch(action) {
            case Action.ENQUEUE:
                player.enqueue(files);
                break;
            default:
                player.play(files);
        }
    }

    @Override
    public void execute(List<EpisodeMatch> list, Episode pointerEpisode) throws ExitException {
        if(!TV.ENV.getArguments().isSetOnly()) {
            File[] files = new File[list.size()];
            for(int i = 0; i < list.size(); i++) {
                files[i] = list.get(i).getEpisodeFile();
            }
            execute(files);
        }
        if(pointerEpisode != null && !TV.ENV.getArguments().isIgnoreSet()) {
            new TVDBManager(TV.ENV.getTVDB()).writeStorage(pointerEpisode);
            if(TV.ENV.isTraktEnabled()) {
                TraktClient trakt = new TraktClient();
                try {
                    trakt.authenticate(TV.ENV.getTraktAuthFile());
                    if(TV.ENV.isTraktUseCheckins()) {
                        trakt.checkinEpisode(pointerEpisode);
                    } else {
                        trakt.markEpisodeAs(TraktClient.SEEN, pointerEpisode);
                    }
                } catch (
                    TraktException | OAuthUnauthorizedException | OAuthSystemException | 
                    OAuthProblemException | RetrofitError ex
                ) {
                    System.err.println(ex.getMessage());
                    trakt.addEpisodeToJournal(pointerEpisode);
                } catch (CancellationException ex) {
                    
                }
            }
        }
    }

    @Override
    public void execute(File file) throws ExitException {
        execute(new File[] { file });
    }
    
}
