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
 * @author Ice
 */
public class Episode {

    private String show;
    private String seasonNo;
    private String episodeNo;
    private String user;

    /**
     * Creates an instance of Episode
     * @param show TV Show
     * @param seasonNo Zero padded season number
     * @param episodeNo Zero padded episode number
     */
    public Episode(String show, String seasonNo, String episodeNo) {
        this.show = show;
        this.seasonNo = seasonNo;
        this.episodeNo = episodeNo;
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
     * Sets the TV Show
     * @return 
     */
    public String getShow() {
        return show;
    }

    /**
     * Gets the TV Show
     * @param show 
     */
    public void setShow(String show) {
        this.show = show;
    }
}
