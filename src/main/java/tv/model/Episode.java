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
package tv.model;

/**
 *
 * @author Sam Malone
 */
public class Episode {

    private String show;
    private String seasonNo;
    private String episodeNo;
    private String user;
    private int date;

    /**
     * Creates an instance of Episode
     * @param show TV Show
     * @param user TV user
     * @param seasonNo Zero padded season number
     * @param episodeNo Zero padded episode number
     */
    public Episode(String show, String user, String seasonNo, String episodeNo) {
        this.show = show;
        this.user = user;
        this.seasonNo = seasonNo;
        this.episodeNo = episodeNo;
    }
    /**
     * Creates an instance of Episode
     * @param show TV Show
     * @param user TV user
     * @param seasonNo Season number
     * @param episodeNo Episode number
     */
    public Episode(String show, String user, int seasonNo, int episodeNo) {
        this.show = show;
        this.user = user;
        this.seasonNo = String.format("%02d", seasonNo);
        this.episodeNo = String.format("%02d", episodeNo);
    }

    /**
     * Copies the Episode e to a new instance
     * @param e Episode to copy
     */
    public Episode(Episode e) {
        show = e.getShow();
        user = e.getUser();
        seasonNo = e.getSeasonNo();
        episodeNo = e.getEpisodeNo();
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
     * Gets the episode number
     * @return Zero padded episode number
     */
    public String getEpisodeNo() {
        return episodeNo;
    }

    /**
     * Sets the episode number
     * @param episodeNo Zero padded episode number
     */
    public void setEpisodeNo(String episodeNo) {
        this.episodeNo = episodeNo;
    }

    /**
     * Gets the season number
     * @return Zero padded season number
     */
    public String getSeasonNo() {
        return seasonNo;
    }

    /**
     * Sets the season number
     * @param seasonNo Zero padded season number
     */
    public void setSeasonNo(String seasonNo) {
        this.seasonNo = seasonNo;
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
        return date;
    }

    /**
     * Sets the date that the episode was played/stored
     * @param date unix timestamp
     */
    public void setPlayedDate(int date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return String.format("%s - s%se%s", show, seasonNo, episodeNo);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (obj == this) {
            return true;
        } else if (!(obj instanceof Episode)) {
            return false;
        }
        Episode o2 = (Episode) obj;
        return show.equals(o2.getShow()) && seasonNo.equals(o2.getSeasonNo()) && episodeNo.equals(o2.getEpisodeNo());
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.show != null ? this.show.hashCode() : 0);
        hash = 37 * hash + (this.seasonNo != null ? this.seasonNo.hashCode() : 0);
        hash = 37 * hash + (this.episodeNo != null ? this.episodeNo.hashCode() : 0);
        hash = 37 * hash + (this.user != null ? this.user.hashCode() : 0);
        return hash;
    }
    
}
