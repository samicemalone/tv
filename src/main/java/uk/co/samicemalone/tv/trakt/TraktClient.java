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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.uwetrottmann.trakt.v2.TraktV2;
import com.uwetrottmann.trakt.v2.entities.BaseShow;
import com.uwetrottmann.trakt.v2.entities.EpisodeCheckin;
import com.uwetrottmann.trakt.v2.entities.EpisodeCheckinResponse;
import com.uwetrottmann.trakt.v2.entities.SearchResult;
import com.uwetrottmann.trakt.v2.entities.SyncItems;
import com.uwetrottmann.trakt.v2.entities.SyncResponse;
import com.uwetrottmann.trakt.v2.enums.Extended;
import com.uwetrottmann.trakt.v2.enums.Type;
import com.uwetrottmann.trakt.v2.exceptions.CheckinInProgressException;
import com.uwetrottmann.trakt.v2.exceptions.OAuthUnauthorizedException;
import java.io.File;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import retrofit.RetrofitError;
import retrofit.client.Response;
import uk.co.samicemalone.tv.TV;
import uk.co.samicemalone.tv.exception.CancellationException;
import uk.co.samicemalone.tv.exception.TraktException;
import uk.co.samicemalone.tv.io.TraktAuthTokenReader;
import uk.co.samicemalone.tv.io.TraktDBManager;
import uk.co.samicemalone.tv.model.Episode;
import uk.co.samicemalone.tv.model.TraktAuthToken;

/**
 *
 * @author Sam Malone
 */
public class TraktClient {
    
    /**
     * Length the token is valid for in milliseconds (3 months / 90 days)
     */
    public static final long TOKEN_VALID_FOR = 1000 * 60 * 60 * 24 * 90;
    
    public static final int SEEN = 0;
    public static final int UNSEEN = 1;
    
    private static final int NOT_FOUND = -1;
    private static final int CANCELLED = -2;
    
    private static final String CLIENT_ID = "c9513988f293fcea34f80aa61f9bf7114965fbdf7d7c70ba1be0b3b2b720c245";
    private static final String CLIENT_SECRET = "841b510a2402a009e17b65693d384a2c43b82392ef4be76337f20ac9d3da5314";
    
    private static final String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";
        
    private final TraktV2 trakt;
    private final TraktDBManager dbManager;
    
    private TraktAuthToken token;

    public TraktClient(TraktAuthToken token) {
        trakt = new TraktV2();
//        trakt.setIsDebug(true);
        trakt.setApiKey(CLIENT_ID);
        dbManager = new TraktDBManager();
        setAuthToken(token);
    }

    public TraktClient() {
        this(null);
    }
    
    public final void setAuthToken(TraktAuthToken token) {
        this.token = token;
        if(token != null) {
           trakt.setAccessToken(token.getAccessToken());
        }
    }
    
    public TraktAuthToken authenticate(File traktAuthFile) throws OAuthSystemException, OAuthProblemException {
        TraktAuthToken token = TraktAuthTokenReader.read(traktAuthFile);
        try {
            if(token != null) {
                if(!token.isValid(TOKEN_VALID_FOR)) {
                    token = new TraktAuthToken(TraktV2.refreshAccessToken(CLIENT_ID, CLIENT_SECRET, REDIRECT_URI, token.getRefreshToken()));
                    TraktAuthTokenReader.write(token, traktAuthFile);
                }
            } else {
                String authCode = TraktUI.promptForPINCode();
                if(authCode != null) {
                    token = new TraktAuthToken(TraktV2.getAccessToken(CLIENT_ID, CLIENT_SECRET, REDIRECT_URI, authCode));
                    TraktAuthTokenReader.write(token, traktAuthFile);
                }
            }
        } catch(IOException ex) {
            System.err.println("Unable to store the trakt auth file");
        }
        setAuthToken(token);
        return token;
    }
    
    /**
     * Marks an episode as seen or unseen.
     * This method will block for user input if show search results have to be
     * retrieved.
     * @param markType {@link #SEEN} or {@link #UNSEEN}
     * @param episode episode to mark as seen/unseen
     * @throws TraktException if an error occurs whilst searching/marking
     * @throws CancellationException if the user cancels the show search results
     * @throws com.uwetrottmann.trakt.v2.exceptions.OAuthUnauthorizedException
     */
    public void markEpisodeAs(int markType, Episode episode) throws TraktException, CancellationException, OAuthUnauthorizedException {
        setEpisodePlayedDate(episode);
        episode.setShowId(getShowId(episode.getShow()));
        markEpisodesAs(markType, TraktSyncBuilder.buildSyncItems(Arrays.asList(episode)));
    }
    
    public void markEpisodesAs(int markType, SyncItems episodes) throws TraktException, OAuthUnauthorizedException {
        SyncResponse response;
        switch(markType) {
            case SEEN:
                response = trakt.sync().addItemsToWatchedHistory(episodes);
                break;
            case UNSEEN:
                response = trakt.sync().deleteItemsFromWatchedHistory(episodes);
                break;
            default:
                throw new TraktException("warning: unknown marking type. must be SEEN or UNSEEN");
        }
        if(response == null) {
            String type = markType == SEEN ? "seen" : "unseen";
            throw new TraktException("warning: trakt: error whilst marking episode as " + type);
        }
    }
    
    /**
     * Check in the given episode to trakt. If a checkin already exists, it will
     * be cancelled and this episode will be checked in instead.
     * @param episode episode to check in
     * @throws TraktException 
     * @throws CancellationException if an error occurs whilst checking in
     * @throws com.uwetrottmann.trakt.v2.exceptions.OAuthUnauthorizedException
     */
    public void checkinEpisode(Episode episode) throws TraktException, CancellationException, OAuthUnauthorizedException {
        setEpisodePlayedDate(episode);
        int showId = getShowId(episode.getShow());
        if(!checkin(showId, episode)) {
            cancelCheckin();
            checkin(showId, episode);
        }
        if(episode.isMultiEpisode()) {
            Episode copy = new Episode(episode);
            // start episode in range is checked in, so remove it before marking the rest seen
            copy.getEpisodes().remove(0);
            markEpisodeAs(SEEN, copy);
        }
    }
    
    /**
     * Get the next episode to watch for the given show
     * @param show show name
     * @return next episode or null if there is no next episode
     * @throws TraktException if unable to fetch the watched episodes progress
     * @throws CancellationException if the user cancels if prompted for a show choice
     * @throws com.uwetrottmann.trakt.v2.exceptions.OAuthUnauthorizedException
     */
    public Episode getNextEpisode(String show) throws TraktException, CancellationException, OAuthUnauthorizedException {
        BaseShow progress = trakt.shows().watchedProgress(
            String.valueOf(getShowId(show)),
            false,
            false,
            Extended.DEFAULT_MIN
        );
        if(progress != null && progress.next_episode != null) {
            com.uwetrottmann.trakt.v2.entities.Episode e = progress.next_episode;
            return new Episode(show, TV.ENV.getArguments().getUser(), e.season, e.number);
        }
        throw new TraktException("warning: trakt: unable to fetch the watched episodes progress");
    }
    
    private void setEpisodePlayedDate(Episode episode) {
        if(episode.getPlayedDate() == 0) {
            episode.setPlayedDate((int) (System.currentTimeMillis() / 1000L));
        }
    }
    
    /**
     * Check in the given episode with the given show id and episode information
     * @param showId trakt tvdb show id
     * @param episode episode to checkin
     * @return true if successfully checked in, false otherwise
     * @throws TraktException if no response is received from trakt or if an
     * error occurs whilst checking in
     */
    private boolean checkin(int showId, Episode episode) throws TraktException, OAuthUnauthorizedException {
        EpisodeCheckin checkin = TraktSyncBuilder.buildEpisodeCheckin(showId, episode);
        EpisodeCheckinResponse response;
        try {
            response = trakt.checkin().checkin(checkin);
            if(response == null) {
                throw new TraktException("warning: trakt: no response from checkin request");
            }
        } catch (CheckinInProgressException ex) {
            System.err.println("trakt: warning: unable to check in as another item is already checked in");
            return false;
        }
        return true;
    }
    
    /**
     * Cancel the current trakt checkin
     * @throws TraktException if an error occurs cancelling a checkin
     * @throws com.uwetrottmann.trakt.v2.exceptions.OAuthUnauthorizedException
     */
    public void cancelCheckin() throws TraktException, OAuthUnauthorizedException {
        Response response = trakt.checkin().deleteActiveCheckin();
        if(response == null || response.getStatus() != 204) {
           throw new TraktException("warning: trakt: error cancelling checkin");
        }
    }
    
    /**
     * Add (append) the given episode to the journal file
     * @param ep Episode to add
     */
    public void addEpisodeToJournal(Episode ep) {
        try {
            dbManager.appendJournal(Arrays.asList(ep));
        } catch (IOException ex) {
            System.err.println("warning: trakt - unable to append to journal");
        }
    }
    
    /**
     * Process the journaled episodes that have previously had errors when 
     * marking as seen. The journal will be read, and an attempt will be made
     * to mark the episodes as seen. Any successfully marked episodes will be 
     * removed from the journal
     * @throws OAuthUnauthorizedException if credentials unauthorized
     * @throws uk.co.samicemalone.tv.exception.CancellationException
     */
    public void processJournal() throws OAuthUnauthorizedException, CancellationException {
        List<Episode> eps = dbManager.readJournal();
        try {
            for (Episode ep : eps) {
                ep.setShowId(getShowId(ep.getShow()));
            }
            SyncItems sync = TraktSyncBuilder.buildSyncItems(eps);
            trakt.sync().addItemsToWatchedHistory(sync);
            dbManager.removeJournal();
        } catch(TraktException ex) {
            
        }
    }
    
    /**
     * Get the trakt tvdb show id for the given show name.
     * @param showName show name
     * @return trakt tvdb show id
     * @throws TraktException if an error occurs retrieving search results or 
     * a show id cannot be found
     * @throws CancellationException if the user cancelled
     */
    public int getShowId(String showName) throws TraktException, CancellationException {
        int showId = findShowId(showName);
        if(showId == NOT_FOUND) {
            throw new TraktException("notice: trakt - unable to find any shows that match: " + showName);
        } else if (showId == CANCELLED) {
            throw new CancellationException("notice: trakt - user has cancelled show choice");
        }
        return showId;
    }
    
    /**
     * Find the trakt tvdb show id for the given show name. The local trakt db
     * file (mapping names to id's) will be searched. If the id is not found
     * locally, the trakt api will be used to display search results to the
     * user and wait for the users confirmation of the show before returning the
     * id. 
     * @param showName show name
     * @return trakt tvdb show id if found, {@link #NOT_FOUND} if not found, or
     * {@link #CANCELLED} if the user cancelled
     * @throws TraktException if an error occurs retrieving search results
     */
    private int findShowId(String showName) throws TraktException {
        int showId = dbManager.findShowId(showName);
        if(showId != -1) {
            return showId;
        }
        List<SearchResult> shows = searchShow(showName);
        if(shows != null && !shows.isEmpty()) {
            TraktUI.displayShowSearchResults(showName, shows);
            int choice = TraktUI.readShowChoiceFromStdin(1, shows.size(), 0);
            if(choice == 0) {
                return CANCELLED;
            }
            int tvdbId = shows.get(choice - 1).show.ids.tvdb;
            try {
                dbManager.appendTVDB(tvdbId, showName);
            } catch (IOException ex) {
                System.err.println("warning: trakt: could not append to trakt tvdb file");
            }
            return tvdbId;
        }
        return NOT_FOUND;
    }
    
    private List<SearchResult> searchShow(String showName) throws TraktException {
        try {
            return trakt.search().textQuery(showName, Type.SHOW, null, 1, 10);
        } catch(RetrofitError e) {
            throw new TraktException("warning: trakt: error whilst searching: " + showName);
        }
    }
    
}
