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
import com.jakewharton.trakt.entities.Response;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.enumerations.Status;
import com.jakewharton.trakt.services.ShowService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import tv.exception.CancellationException;
import tv.exception.TraktException;
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
    private final TraktDBManager dbManager;

    public TraktClient(TraktCredentials credentials) {
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
        if(episode.getPlayedDate() == 0) {
            episode.setPlayedDate((int) (System.currentTimeMillis() / 1000));
        }
        int showId = findShowId(episode.getShow());
        if(showId == NOT_FOUND) {
            throw new TraktException("notice: trakt - unable to find any shows that match: " + episode.getShow());
        } else if (showId == CANCELLED) {
            throw new CancellationException("notice: trakt - user has cancelled show choice");
        }
        try {
            Response response = trakt.showService().episodeSeen(buildEpisodes(showId, episode));
            if(response != null && Status.SUCCESS.equals(response.status)) {
                return;
            }
        } catch (Exception ex) {}
        throw new TraktException("warning: trakt: error whilst marking episode as seen");
    }
    
    /**
     * Build an API compatible episodes object from the given episode information 
     * @param showId trakt tvdb show id
     * @param episode episode to build
     * @return episodes
     */
    private ShowService.Episodes buildEpisodes(int showId, Episode episode) {
        ShowService.Episodes episodes = new ShowService.Episodes(                
            showId,
            Integer.valueOf(episode.getSeasonNo()),
            Integer.valueOf(episode.getEpisodeNo())
        );
        episodes.episodes.get(0).last_played = String.valueOf(episode.getPlayedDate());
        return episodes;
    }
    
    /**
     * Add (append) the given episode to the journal file
     * @param ep Episode to add
     */
    public void addEpisodeToJournal(Episode ep) {
        List<Episode> eps = new ArrayList<Episode>(1);
        eps.add(ep);
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
     */
    public void processJournal() {
        List<Episode> eps = dbManager.readJournal();
        int size = eps.size();
        for(Iterator<Episode> it = eps.iterator(); it.hasNext(); ) {
            try {
                markEpisodeAsSeen(it.next());
                it.remove();
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
    
    private List<TvShow> searchShow(String showName) throws TraktException {
        try {
            return trakt.searchService().shows(showName, 10);
        } catch(Exception e) {
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