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

import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.uwetrottmann.trakt5.TraktV2;
import com.uwetrottmann.trakt5.entities.AccessToken;
import com.uwetrottmann.trakt5.entities.BaseShow;
import com.uwetrottmann.trakt5.entities.DeviceCode;
import com.uwetrottmann.trakt5.entities.EpisodeCheckin;
import com.uwetrottmann.trakt5.entities.EpisodeCheckinResponse;
import com.uwetrottmann.trakt5.entities.SearchResult;
import com.uwetrottmann.trakt5.entities.SyncItems;
import com.uwetrottmann.trakt5.entities.SyncResponse;
import com.uwetrottmann.trakt5.entities.TraktError;
import com.uwetrottmann.trakt5.enums.ProgressLastActivity;
import retrofit2.Call;
import retrofit2.Response;
import uk.co.samicemalone.tv.exception.TraktException;
import uk.co.samicemalone.tv.io.TraktAuthTokenReader;
import uk.co.samicemalone.tv.model.Episode;
import uk.co.samicemalone.tv.model.TraktAuthToken;
import uk.co.samicemalone.tv.tvdb.model.Show;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Sam Malone
 */
public class TraktClient {

    private final static Logger logger = LoggerFactory.getLogger(TraktClient.class.getName());
    
    public static final int SEEN = 0;
    public static final int UNSEEN = 1;
    
    private static final String CLIENT_ID = "c9513988f293fcea34f80aa61f9bf7114965fbdf7d7c70ba1be0b3b2b720c245";
    private static final String CLIENT_SECRET = "841b510a2402a009e17b65693d384a2c43b82392ef4be76337f20ac9d3da5314";
    
    private static final String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";
        
    private final TraktV2 trakt;

    public TraktClient(TraktAuthToken token) {
        trakt = new TraktV2Http11(CLIENT_ID, CLIENT_SECRET, REDIRECT_URI);
        setAuthToken(token);
    }

    public TraktClient() {
        this(null);
    }
    
    private void setAuthToken(TraktAuthToken token) {
        if(token != null) {
           trakt.accessToken(token.getAccessToken());
           trakt.refreshToken(token.getRefreshToken());
        }
    }
    
    public TraktAuthToken authenticate(File traktAuthFile) {
        logger.debug("[trakt] reading auth token file");
        TraktAuthToken token = TraktAuthTokenReader.read(traktAuthFile);
        Response<AccessToken> response = null;
        try {
            if(token == null || token.hasExpired()) {
                logger.debug("[trakt] token missing or expired - generating new device code...");
                Response<DeviceCode> deviceCodeResponse = trakt.generateDeviceCode();
                DeviceCode code = deviceCodeResponse.body();
                if(deviceCodeResponse.isSuccessful() && code != null) {
                    TraktUI.promptForDeviceCodeConfirmation(code);

                    logger.debug("[trakt] exchanging device code for access token");
                    response = trakt.exchangeDeviceCodeForAccessToken(code.device_code);
                } else {
                    logger.error("[trakt] failed to generate device code");
                }
            } else if(token.isRefreshRequired(60)) {
                logger.info("[trakt] refreshing access token");
                response = trakt.refreshAccessToken(token.getRefreshToken());
            }

            if(response != null) {
                AccessToken at = response.body();
                if (response.isSuccessful() && at != null) {
                    token = new TraktAuthToken(at);
                    logger.debug("[trakt] writing auth token file");
                    TraktAuthTokenReader.write(token, traktAuthFile);
                } else {
                    logger.error(response.message());
                }
            }
        } catch(IOException ex) {
            logger.error("[trakt] unable to store the trakt auth file");
        }
        setAuthToken(token);
        return token;
    }
    
    /**
     * Marks an episode as seen or unseen.
     * This method will block for user input if show search results have to be
     * retrieved.
     * @param show show
     * @param episode episode to mark as seen/unseen
     * @param markType {@link #SEEN} or {@link #UNSEEN}
     * @throws TraktException if an error occurs whilst searching/marking
     */
    public void markEpisodeAs(Show show, Episode episode, int markType) throws TraktException {
        episode.setWatchedAt(Instant.now());
        SyncItems syncItems = TraktSyncBuilder.buildSyncItems(
            Collections.singletonList(show),
            Collections.singletonList(episode)
        );
        markEpisodesAs(syncItems, markType);
    }
    
    public void markEpisodesAs(SyncItems episodes, int markType) throws TraktException {
        SyncResponse response = null;
        try {
            switch (markType) {
                case SEEN:
                    response = executeCall(trakt.sync().addItemsToWatchedHistory(episodes));
                    break;
                case UNSEEN:
                    response = executeCall(trakt.sync().deleteItemsFromWatchedHistory(episodes));
                    break;
                default:
                    throw new TraktException("warning: unknown marking type. must be SEEN or UNSEEN");
            }
        } catch (IOException ex) {
            //
        }
        if (response == null) {
            String type = markType == SEEN ? "seen" : "unseen";
            throw new TraktException("[trakt] error whilst marking episode as " + type);
        }
    }
    
    /**
     * Check in the given episode to trakt. If a checkin already exists, it will
     * be cancelled and this episode will be checked in instead.
     * @param episode episode to check in
     * @throws TraktException 
     */
    public void checkinEpisode(Show show, Episode episode) throws TraktException {
        episode.setWatchedAt(Instant.now());
        int showId = show.getTVDBId();
        try {
            checkin(showId, episode);

            if (episode.isMultiEpisode()) {
                Episode copy = new Episode(episode);
                // start episode in range is checked in, so remove it before marking the rest seen
                copy.getEpisodes().remove(0);
                markEpisodeAs(show, copy, SEEN);
            }
        } catch (IOException ex) {
            throw new TraktException("[trakt] unable to checkin the episode");
        }
    }

    /**
     * Get the watched progress for the given show
     * @param show show
     * @return watched progress
     * @throws TraktException if unable to fetch the watched episodes progress
     */
    public BaseShow getWatchedProgress(Show show) throws TraktException {
        try {
            return executeCall(trakt.shows().watchedProgress(
                String.valueOf(show.getTVDBId()),
                false,
                false,
                false,
                ProgressLastActivity.WATCHED,
                null
            ));
        } catch (IOException e) {
            throw new TraktException("[trakt] unable to fetch the watched episode progress");
        }
    }

    /**
     * Check in the given episode with the given show id and episode information
     * @param showId trakt tvdb show id
     * @param episode episode to checkin
     * @throws TraktException if no response is received from trakt or if an
     * error occurs whilst checking in
     */
    private void checkin(int showId, Episode episode) throws TraktException, IOException {
        EpisodeCheckin checkin = TraktSyncBuilder.buildEpisodeCheckin(showId, episode);
        Response<EpisodeCheckinResponse> response;
        response = trakt.checkin().checkin(checkin).execute();

        if(response.code() == 409) {
            throw new TraktException("[trakt] there is already a checkin in progress");
        } else if(!response.isSuccessful()) {
            logger.debug("[trakt] [" + response.code() + "] " + response.message());
            throw new TraktException("[trakt] no response from checkin request");
        }
    }

    /**
     * Sync the episodes in the queue
     */
    public void syncWatchedProgress(Collection<Show> shows, List<Episode> queueEpisodes) throws TraktException, IOException {
        SyncItems sync = TraktSyncBuilder.buildSyncItems(shows, queueEpisodes);
        executeCall(trakt.sync().addItemsToWatchedHistory(sync));
    }
    
    public List<SearchResult> searchShow(String showName) throws TraktException {
        try {
            return executeCall(trakt.search().textQueryShow(
                showName, null, null, null, null, null,
                null, null, null, null, null, 1, 15
            ));
        } catch(IOException e) {
            throw new TraktException("warning: trakt: error whilst searching: " + showName);
        }
    }

    private <T> T executeCall(Call<T> call) throws IOException, TraktException {
        Response<T> r = call.execute();
        if (r.isSuccessful()) {
            return r.body();
        } else {
            String baseMessage = "[trakt] [" + r.code() + "] ";
            if (r.code() == 401) {
                throw new TraktException(baseMessage + r.message());
            } else {
                TraktError error = trakt.checkForTraktError(r);
                String message = error != null && error.message != null ? error.message : r.message();
                throw new TraktException(baseMessage + message);
            }
        }
    }
    
}
