/*
 * Copyright (c) 2015, Ice. All rights reserved.
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

import com.uwetrottmann.trakt5.entities.EpisodeCheckin;
import com.uwetrottmann.trakt5.entities.Show;
import com.uwetrottmann.trakt5.entities.ShowIds;
import com.uwetrottmann.trakt5.entities.SyncEpisode;
import com.uwetrottmann.trakt5.entities.SyncItems;
import com.uwetrottmann.trakt5.entities.SyncSeason;
import com.uwetrottmann.trakt5.entities.SyncShow;
import org.threeten.bp.Instant;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneId;
import uk.co.samicemalone.libtv.model.EpisodeMatch;
import uk.co.samicemalone.libtv.model.TVMap;
import uk.co.samicemalone.tv.Version;
import uk.co.samicemalone.tv.model.Episode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Ice
 */
public class TraktSyncBuilder {
    
    public static SyncItems buildSyncItems(Collection<uk.co.samicemalone.tv.tvdb.model.Show> shows, List<Episode> episodes) {
        Map<String, uk.co.samicemalone.tv.tvdb.model.Show> showMap = new HashMap<>();
        shows.forEach(show -> showMap.put(show.getName(), show));
        SyncItems sync = new SyncItems();
        TVMap tvMap = buildMap(episodes);
        List<SyncShow> syncShows = new ArrayList<>();

        for (String show : tvMap.getShows()) {
            SyncShow syncShow = new SyncShow().id(ShowIds.tvdb(showMap.get(show).getTVDBId()));
            List<SyncSeason> syncSeasons = new ArrayList<>();

            for (Integer season : tvMap.getSeasons(show)) {
                Set<EpisodeMatch> seasonMap = tvMap.getSeasonEpisodes(show, season);
                SyncSeason syncSeason = new SyncSeason()
                    .number(season)
                    .episodes(getEpisodes(seasonMap));

                syncSeasons.add(syncSeason);
            }

            syncShow.seasons = syncSeasons;
            syncShows.add(syncShow);
        }
        sync.shows = syncShows;
        return sync;
    }
    
    public static EpisodeCheckin buildEpisodeCheckin(int showId, Episode episode) {
        Show show = new Show();
        show.ids = ShowIds.tvdb(showId);
        show.title = episode.getShow();
        SyncEpisode se = new SyncEpisode()
            .season(episode.getSeason())
            .number(episode.getEpisode())
            .watchedAt(OffsetDateTime.ofInstant(Instant.ofEpochMilli(episode.getWatchedAt().toEpochMilli()), ZoneId.systemDefault()));
        return new EpisodeCheckin.Builder(se, Version.VERSION, Version.BUILD_DATE)
            .message("Checked in with tv " + Version.VERSION)
            .show(show)
            .build();
    }

    private static List<SyncEpisode> getEpisodes(Set<EpisodeMatch> episodes) {
        List<SyncEpisode> syncEpisodes = new ArrayList<>();
        for (EpisodeMatch episode : episodes) {
            for (Integer episodeNo : episode.getEpisodes()) {
                java.time.Instant watchedAtTimestamp = ((Episode) episode).getWatchedAt();
                OffsetDateTime watchedAt = OffsetDateTime.ofInstant(Instant.ofEpochMilli(watchedAtTimestamp.toEpochMilli()), ZoneId.systemDefault());
                syncEpisodes.add(new SyncEpisode().number(episodeNo).watchedAt(watchedAt));
            }
        }
        return syncEpisodes;
    }
    
    private static TVMap buildMap(Collection<Episode> episodes) {
        TVMap map = new TVMap();
        episodes.forEach(map::addEpisode);
        return map;
    }
    
}
