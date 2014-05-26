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

package tv;

import com.jakewharton.trakt.Trakt;
import com.jakewharton.trakt.entities.CheckinResponse;
import com.jakewharton.trakt.entities.Response;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowProgress;
import com.jakewharton.trakt.enumerations.Extended2;
import com.jakewharton.trakt.enumerations.SortType;
import com.jakewharton.trakt.enumerations.Status;
import com.jakewharton.trakt.services.ShowService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import retrofit.RetrofitError;
import tv.exception.CancellationException;
import tv.exception.TraktException;
import tv.exception.TraktUnauthorizedException;
import tv.io.TraktDBManager;
import tv.model.Episode;
import tv.model.TraktCredentials;

/**
 *
 * @author Sam Malone
 */
public class TraktClient {
    
    private static final int NOT_FOUND = -1;
    private static final int CANCELLED = -2;
    
    private final Trakt trakt;
    private final TraktCredentials credentials;
    private final TraktDBManager dbManager;

    public TraktClient(TraktCredentials credentials) {
        this.credentials = credentials;
        trakt = new Trakt();
        trakt.setAuthentication(credentials.getUsername(), credentials.getPasswordSha1());
        trakt.setApiKey(credentials.getApiKey());
        dbManager = new TraktDBManager();
    }
    
    /**
     * Marks an episode as seen.
     * This method will block for user input if show search results have to be
     * retrieved.
     * @param episode episode to mark as seen
     * @throws TraktException if an error occurs whilst searching/marking seen
     * @throws CancellationException if the user cancels the show search results
     */
    public void markEpisodeAsSeen(Episode episode) throws TraktException, CancellationException {
        setEpisodePlayedDate(episode);
        int showId = getShowId(episode.getShow());
        try {
            Response response = trakt.showService().episodeSeen(buildEpisodes(showId, episode));
            if(response != null && Status.SUCCESS.equals(response.status)) {
                return;
            }
        } catch (RetrofitError ex) {
            assertAuthorized(ex);
        }
        throw new TraktException("warning: trakt: error whilst marking episode as seen");
    }
    
    /**
     * Check in the given episode to trakt. If a checkin already exists, it will
     * be cancelled and this episode will be checked in instead.
     * @param episode episode to check in
     * @throws TraktException 
     * @throws CancellationException if an error occurs whilst checking in
     */
    public void checkinEpisode(Episode episode) throws TraktException, CancellationException {
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
            markEpisodeAsSeen(copy);
        }
    }
    
    /**
     * Get the next episode to watch for the given show
     * @param show show name
     * @return next episode or null if there is no next episode
     * @throws TraktException if unable to fetch the watched episodes progress
     * @throws CancellationException if the user cancels if prompted for a show choice
     */
    public TvShowProgress.NextEpisode getNextEpisode(String show) throws TraktException, CancellationException {
        int showId = getShowId(show);
        try {
            List<TvShowProgress> progress = trakt.userService().progressWatched(
                credentials.getUsername(), String.valueOf(showId), SortType.TITLE, Extended2.NORMAL
            );
            if(progress != null) {
                return progress.get(0).next_episode;
            }
        } catch (RetrofitError re) {
            assertAuthorized(re);
        }
        throw new TraktException("warning: trakt: unable to fetch the watched episodes progress");
    }
    
    private void setEpisodePlayedDate(Episode episode) {
        if(episode.getPlayedDate() == 0) {
            episode.setPlayedDate((int) (System.currentTimeMillis() / 1000));
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
    private boolean checkin(int showId, Episode episode) throws TraktException {
        try {
            CheckinResponse response = trakt.showService().checkin(buildEpisodeCheckin(showId, episode));
            if(response == null) {
                throw new TraktException("warning: trakt: no response from checkin request");
            }
            return Status.SUCCESS.equals(response.status);
        } catch (RetrofitError ex) {
            assertAuthorized(ex);
        }
        throw new TraktException("warning: trakt: error whilst checking in episode");
    }
    
    /**
     * Cancel the current trakt checkin
     * @throws TraktException if an error occurs cancelling a checkin
     */
    public void cancelCheckin() throws TraktException {
        try {
            Response response = trakt.showService().cancelcheckin();
            if(response != null && Status.SUCCESS.equals(response.status)) {
                return;
            }
        } catch (RetrofitError ex) {
            assertAuthorized(ex);
        }
        throw new TraktException("warning: trakt: error cancelling checkin");
    }

    /**
     * Build an API compatible ShowCheckin object from the given episode information.
     * If the episode file contains multiple episodes, only the first episode will
     * be checked in.
     * @param showId trakt tvdb show id
     * @param episode episode to build
     * @return ShowCheckin
     */
    private ShowService.ShowCheckin buildEpisodeCheckin(int showId, Episode episode) {
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
    
    /**
     * Build an API compatible episodes object from the given episode information 
     * @param showId trakt tvdb show id
     * @param episode episode to build
     * @return episodes
     */
    private ShowService.Episodes buildEpisodes(int showId, Episode episode) {
        List<ShowService.Episodes.Episode> list = new ArrayList<>();
        for(int curEp : episode.getEpisodes()) {
            ShowService.Episodes.Episode tmpEp = new ShowService.Episodes.Episode(episode.getSeason(), curEp);
            tmpEp.last_played = String.valueOf(episode.getPlayedDate());
            list.add(tmpEp);
        }
        return new ShowService.Episodes(showId, list);
    }
    
    /**
     * Add (append) the given episode to the journal file
     * @param ep Episode to add
     */
    public void addEpisodeToJournal(Episode ep) {
        List<Episode> eps = Arrays.asList(ep);
        try {
            dbManager.appendJournal(eps);
        } catch (IOException ex) {
            System.err.println("warning: trakt - unable to append to journal");
        }
    }
    
    /**
     * Process the journaled episodes that have previously had errors when 
     * marking as seen. The journal will be read, and an attempt will be made
     * to mark the episodes as seen. Any successfully marked episodes will be 
     * removed from the journal
     * @throws tv.exception.TraktUnauthorizedException if credentials unauthorized
     */
    public void processJournal() throws TraktUnauthorizedException {
        List<Episode> eps = dbManager.readJournal();
        int size = eps.size();
        for(Iterator<Episode> it = eps.iterator(); it.hasNext(); ) {
            try {
                markEpisodeAsSeen(it.next());
                it.remove();
            } catch (TraktUnauthorizedException ex) {
                throw ex;
            } catch (TraktException ex) {
                System.err.println("warning: trakt: unable to mark journaled episodes as seen");
                break;
            } catch (CancellationException ex) {
                
            }
        }
        if(eps.isEmpty()) {
            dbManager.removeJournal();
        } else if(eps.size() != size) {
            try {
                dbManager.writeJournal(eps, false);
            } catch (IOException ex) {
                System.err.println("warning: trakt: unable to write new journal");
            }
        }
    }
    
    /**
     * Get the trakt tvdb show id for the given show name.
     * @param showName
     * @return trakt tvdb show id
     * @throws TraktException if an error occurs retrieving search results or 
     * a show id cannot be found
     * @throws CancellationException if the user cancelled
     */
    private int getShowId(String showName) throws TraktException, CancellationException {
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
        List<TvShow> shows = searchShow(showName);
        if(shows != null && !shows.isEmpty()) {
            displayShowSearchResults(showName, shows);
            int choice = readShowChoiceFromStdin(1, shows.size(), 0);
            if(choice == 0) {
                return CANCELLED;
            }
            int tvdbId = shows.get(choice - 1).tvdb_id;
            try {
                dbManager.appendTVDB(tvdbId, showName);
            } catch (IOException ex) {
                System.err.println("warning: trakt: could not append to trakt tvdb file");
            }
            return tvdbId;
        }
        return NOT_FOUND;
    }
    
    /**
     * Assert that the retrofit error given was not caused by unauthorized credentials
     * @param re retrofit error
     * @throws TraktUnauthorizedException if credentials are not authorized
     */
    private void assertAuthorized(RetrofitError re) throws TraktUnauthorizedException {
        retrofit.client.Response r = re.getResponse();
        if(r != null && r.getStatus() == 401) {
            throw new TraktUnauthorizedException(r.getReason(), re);
        }
    }
    
    private List<TvShow> searchShow(String showName) throws TraktException {
        try {
            return trakt.searchService().shows(showName, 10);
        } catch(RetrofitError e) {
            assertAuthorized(e);
            throw new TraktException("warning: trakt: error whilst searching: " + showName);
        }
    }
    
    /**
     * Display the list of tv show search results with a 1 based index id 
     * displayed with it.
     * @param query search query
     * @param shows list of shows to display
     */
    private void displayShowSearchResults(String query, List<TvShow> shows) {
        if(shows.isEmpty()) {
            System.out.println("No results found for " + query);
        } else {
            System.out.println("Search results for " + query);
            for(int i = 0; i < shows.size(); i++) {
                System.out.format(" %1$2s) %2$s\n", String.valueOf(i+1), shows.get(i).title);
            }
        }
    }
    
    /**
     * Reads the users show choice from stdin.
     * @param minValue Minimum value to accept
     * @param maxValue Maximum value to accept
     * @param cancel Value to cancel/abort
     * @return users input between minValue and maxValue or cancel if the user
     * cancelled
     */
    private int readShowChoiceFromStdin(int minValue, int maxValue, int cancel) {
        System.out.format("Enter the id that matches the show or %d to cancel: \n", cancel);
        Scanner s = new Scanner(System.in);
        while (true) {
            try {
                int i = s.nextInt();
                if(i == cancel) {
                    return cancel;
                } else if(i >= minValue && i <= maxValue) {
                    return i;
                }
            } catch (InputMismatchException ex) {}
            System.out.format("Enter a value between %d and %d: \n", minValue, maxValue);
        }
    }
    
}
