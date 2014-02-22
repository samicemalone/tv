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

package tv.action;

import java.io.File;
import tv.TV;
import tv.TraktClient;
import tv.exception.CancellationException;
import tv.exception.TraktException;
import tv.io.TVDBManager;
import tv.model.Episode;
import tv.player.MediaPlayer;
import tv.player.MediaPlayerFactory;

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
    public void execute(File[] list) {
        MediaPlayer player = MediaPlayerFactory.parsePlayer(TV.ENV.getArguments().getPlayerInfo());
        switch(action) {
            case Action.ENQUEUE:
                player.enqueue(list);
                break;
            default:
                player.play(list);
        }
    }

    @Override
    public void execute(File list, Episode pointerEpisode) {
        if(!TV.ENV.getArguments().isSetOnly()) {
            execute(new File[] { list });
        }
        if(pointerEpisode != null && !TV.ENV.getArguments().isIgnoreSet()) {
            new TVDBManager(TV.ENV.getTVDB()).writeStorage(pointerEpisode);
            if(TV.ENV.isTraktEnabled()) {
                TraktClient trakt = new TraktClient(TV.ENV.getTraktCredentials());
                trakt.processJournal();
                try {
                    trakt.markEpisodeAsSeen(pointerEpisode);
                } catch (TraktException ex) {
                    System.err.println(ex.getMessage());
                    trakt.addEpisodeToJournal(pointerEpisode);
                } catch (CancellationException ex) {
                    
                }
            }
        }
    }
    
}
