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

package uk.co.samicemalone.tv.trakt;

import com.jakewharton.trakt.services.ShowService;
import java.util.Collection;
import uk.co.samicemalone.libtv.model.EpisodeMatch;
import uk.co.samicemalone.tv.Version;
import uk.co.samicemalone.tv.model.Episode;

/**
 *
 * @author Sam Malone
 */
public class TraktBuilder {
    
    /**
     * Build an API compatible episode object from the given episode information 
     * used for marking episodes as seen/unseen, and add it to the list given.
     * If episode contains multiple episode numbers, each one will be added to list
     * @param list list of api compatible episodes to add {@code episode} to
     * @param episode episode to add
     */
    public static void buildMarkableEpisodes(Collection<ShowService.Episodes.Episode> list, Episode episode) {
        buildMarkableEpisodes(list, episode, episode.getPlayedDate());
    }
    
    /**
     * Build an API compatible episode object from the given episode information 
     * used for marking episodes as seen/unseen, and add it to the list given.
     * If episode contains multiple episode numbers, each one will be added to list
     * @param list list of api compatible episodes to add {@code episode} to
     * @param episode episode to add
     * @param lastPlayed unix timestamp of the last played date
     */
    public static void buildMarkableEpisodes(Collection<ShowService.Episodes.Episode> list, EpisodeMatch episode, int lastPlayed) {
        for(int curEp : episode.getEpisodes()) {
            ShowService.Episodes.Episode tmpEp = new ShowService.Episodes.Episode(episode.getSeason(), curEp);
            tmpEp.last_played = String.valueOf(lastPlayed);
            list.add(tmpEp);
        }
    }
    
    /**
     * Build an API compatible ShowCheckin object from the given episode information.
     * If the episode file contains multiple episodes, only the first episode will
     * be checked in.
     * @param showId trakt tvdb show id
     * @param episode episode to build
     * @return ShowCheckin
     */
    public static ShowService.ShowCheckin buildEpisodeCheckin(int showId, Episode episode) {
        ShowService.ShowCheckin checkin = new ShowService.ShowCheckin(
            showId,
            episode.getSeason(),
            episode.getEpisodesAsRange().getStart(),
            "Checked in with tv " + Version.VERSION,
            Version.VERSION,
            Version.BUILD_DATE
        );
        checkin.duration = 5;
        return checkin;
    }
    
}
