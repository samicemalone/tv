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
package uk.co.samicemalone.tv.model;

import java.util.List;
import uk.co.samicemalone.libtv.model.EpisodeMatch;

/**
 *
 * @author Sam Malone
 */
public class Episode extends EpisodeMatch {
    
    private int playedDate;
    private String user = "";
    
    public Episode(EpisodeMatch episodeMatch, String show, String user) {
        super(episodeMatch);
        this.user = user;
        setShow(show);
    }
   
    public Episode(String show, String user, int seasonNo, int episode) {
        super(show, seasonNo, episode);
        this.user = user;
    }

    /**
     * Copies the Episode e to a new instance
     * @param e Episode to copy
     */
    public Episode(Episode e) {
        super(e);
        user = e.getUser();
    }
    
    /**
     * Gets the date that the episode was played/stored
     * @return unix timestamp or 0 if not set
     */
    public int getPlayedDate() {
        return playedDate;
    }

    /**
     * Sets the date that the episode was played/stored
     * @param date unix timestamp
     */
    public void setPlayedDate(int date) {
        this.playedDate = date;
    }

    /**
     * Gets the user to which this Episode refers
     * @return User if exists, empty string otherwise
     */
    public String getUser() {
        return user;
    }

    /**
     * Sets the user to which this Episode refers
     * @param user
     */
    public void setUser(String user) {
        this.user = user;
    }
    
    @Override
    public String toString() {
        return String.format("%s - %s", getShow(), super.toString());
    }
    
}
