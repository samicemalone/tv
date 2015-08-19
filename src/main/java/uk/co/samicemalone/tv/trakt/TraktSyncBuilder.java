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

import com.uwetrottmann.trakt.v2.entities.EpisodeCheckin;
import com.uwetrottmann.trakt.v2.entities.Show;
import com.uwetrottmann.trakt.v2.entities.ShowIds;
import com.uwetrottmann.trakt.v2.entities.SyncEpisode;
import com.uwetrottmann.trakt.v2.entities.SyncItems;
import com.uwetrottmann.trakt.v2.entities.SyncSeason;
import com.uwetrottmann.trakt.v2.entities.SyncShow;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.joda.time.DateTime;
import uk.co.samicemalone.libtv.model.EpisodeMatch;
import uk.co.samicemalone.libtv.model.TVMap;
import uk.co.samicemalone.tv.TV;
import uk.co.samicemalone.tv.Version;
import uk.co.samicemalone.tv.model.Episode;

/**
 *
 * @author Ice
 */
public class TraktSyncBuilder {
    
    public static SyncItems buildSyncItems(List<Episode> episodes) {
        SyncItems sync = new SyncItems();
        TVMap tvMap = buildMap(episodes);
        List<SyncShow> syncShows = new ArrayList<>();
        for (String show : tvMap.getShows()) {
            SyncShow syncShow = new SyncShow().id(ShowIds.tvdb(getShowId(tvMap, show)));
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
    
    public static SyncItems buildSyncItemsForShow(List<EpisodeMatch> matches, int showId) {
        List<Episode> episodes = new ArrayList<>();
        for (EpisodeMatch match : matches) {
            Episode e = new Episode(match, match.getShow(), TV.ENV.getArguments().getUser());
            e.setPlayedDate((int) (System.currentTimeMillis() / 1000L));
            e.setShowId(showId);
            episodes.add(e);
        }
        return buildSyncItems(episodes);
    }
    
    public static EpisodeCheckin buildEpisodeCheckin(int showId, Episode episode) {
        Show show = new Show();
        show.ids = ShowIds.tvdb(showId);
        show.title = episode.getShow();
        SyncEpisode se = new SyncEpisode()
            .season(episode.getSeason())
            .number(episode.getEpisode())
            .watchedAt(new DateTime(episode.getPlayedDate() * 1000L));
        return new EpisodeCheckin.Builder(se, Version.VERSION, Version.BUILD_DATE)
            .message("Checked in with tv " + Version.VERSION)
            .show(show)
            .build();
    }
    
    private static int getShowId(TVMap tvMap, String show) {
        int season = tvMap.getSeasons(show).stream().findAny().get();
        int episode = tvMap.getSeasonEpisodes(show, season).stream().findAny().get().getEpisode();
        return ((Episode) tvMap.getEpisode(show, season, episode)).getShowId();
    }
    
    private static List<SyncEpisode> getEpisodes(Set<EpisodeMatch> episodes) {
        List<SyncEpisode> syncEpisodes = new ArrayList<>();
        for (EpisodeMatch episode : episodes) {
            for (Integer episodeNo : episode.getEpisodes()) {
                DateTime watchedAt = new DateTime(((Episode) episode).getPlayedDate() * 1000L);
                syncEpisodes.add(new SyncEpisode().number(episodeNo).watchedAt(watchedAt));
            }
        }
        return syncEpisodes;
    }
    
    private static TVMap buildMap(Collection<Episode> episodes) {
        TVMap map = new TVMap();
        for (Episode episode : episodes) {
            map.addEpisode(episode);
        }
        return map;
    }
    
}
