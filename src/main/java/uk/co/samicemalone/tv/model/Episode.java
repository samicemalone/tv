/*
 * Copyright (c) 2013, Sam Malone. All rights reserved.
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sam Malone
 */
public class Episode extends AbstractEpisode {

    private List<Integer> episodes;
    private File episodeFile;
    private String show;
    private String user;
    private int season;
    private int playedDate;
   
    public Episode(String show, String user, EpisodeMatch episodeMatch) {
        this.show = show;
        this.user = user;
        this.season = episodeMatch.getSeason();
        this.episodes = episodeMatch.getEpisodes();
        this.episodeFile = episodeMatch.getEpisodeFile();
    }
   
    public Episode(String show, String user, int seasonNo, int episode) {
        this.show = show;
        this.user = user;
        this.season = seasonNo;
        this.episodes = new ArrayList<>();
        this.episodes.add(episode);
    }

    /**
     * Copies the Episode e to a new instance
     * @param e Episode to copy
     */
    public Episode(Episode e) {
        show = e.getShow();
        user = e.getUser();
        season = e.getSeason();
        episodes = e.getEpisodes();
        episodeFile = e.getEpisodeFile();
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

    /**
     * Sets the episode numbers
     * @param episodes episode numbers
     */
    public void setEpisodes(List<Integer> episodes) {
        this.episodes = episodes;
    }

    /**
     * Gets the season number
     * @return season number
     */
    public int getSeason() {
        return season;
    }

    /**
     * Sets the season number
     * @param season season number
     */
    public void setSeason(int season) {
        this.season = season;
    }

    /**
     * Gets the TV Show
     * @return 
     */
    public String getShow() {
        return show;
    }

    /**
     * Sets the TV Show
     * @param show 
     */
    public void setShow(String show) {
        this.show = show;
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

    @Override
    public String toString() {
        return String.format("%s - s%02d%s", show, season, super.toString());
    }

    @Override
    public List<Integer> getEpisodes() {
        return episodes;
    }

    public File getEpisodeFile() {
        return episodeFile;
    }
    
    /**
     * Check if the episode file contains more than one episode
     * @return true if multi-episode, false otherwise
     */
    public boolean isMultiEpisode() {
        return getEpisodes().size() > 1;
    }
    
}
