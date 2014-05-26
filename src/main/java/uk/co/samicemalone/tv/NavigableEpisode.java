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

package uk.co.samicemalone.tv;

import uk.co.samicemalone.tv.matcher.TVMatcher;
import uk.co.samicemalone.tv.model.Episode;
import uk.co.samicemalone.tv.model.EpisodeMatch;
import uk.co.samicemalone.tv.model.Range;
import uk.co.samicemalone.tv.model.Season;

/**
 *
 * @author Sam Malone
 */
public class NavigableEpisode {
    
    /*
     * Offset Values
     */
    public static final int PREV = -1;
    public static final int CUR = 0;
    public static final int NEXT = 1;

    private final TVMatcher tvMatcher;
    
    public NavigableEpisode(TVMatcher matcher) {
        tvMatcher = matcher;
    }
    
    /**
     * Navigate from the episode by the offset given. The episode given will
     * be checked for a multi episode match before navigating. If the episode
     * given cannot be found, it is assumed to be a single episode and will be
     * navigated as normal.
     * @param episode episode to navigate from
     * @param strOffset offset string e.g. next, cur, prev
     * @return navigated Episode with the given offset or null if navigated 
     * episode is not found
     */
    public EpisodeMatch navigate(Episode episode, String strOffset) {
        int offset = parseStringOffset(strOffset);
        Season season = tvMatcher.getSeason(episode.getShow(), episode.getSeason());
        Range episodeRange = episode.getEpisodesAsRange();
        if(season.getDir() == null) {
            boolean isNavigateSeason = offset == PREV && (episodeRange.getStart() - 1) < 1;
            return isNavigateSeason ? navigateSeason(episode, offset) : null;
        }
        int episodeNoBound = (offset == NEXT) ? episodeRange.getEnd() : episodeRange.getStart();
        EpisodeMatch curMatch = tvMatcher.matchEpisode(season, episodeNoBound);
        if(curMatch != null) {
            episodeRange = curMatch.getEpisodesAsRange();
            episodeNoBound = (offset == NEXT) ? episodeRange.getEnd() : episodeRange.getStart();
        }
        EpisodeMatch m = tvMatcher.matchEpisode(season, episodeNoBound + offset);
        if(m != null || offset == CUR) { 
            return m;
        }
        // check another offset episode to see if it's missing or likely to be end of season
        if(tvMatcher.matchEpisode(season, episodeNoBound + offset + offset) != null) {
            return null; // found extra offset episode so dont skip to offset season
        }
        return navigateSeason(episode, offset);
    }
    
    /**
     * Navigate to the start of the next season or the end of the previous
     * season.
     * @param toNavigate episode to navigate from
     * @param offset {@link #PREV} or {@link #NEXT}
     * @return navigated episode at the start of the next season or the end of
     * the previous season or null if no offset episode is found
     */
    public EpisodeMatch navigateSeason(Episode toNavigate, int offset) {
        int season = toNavigate.getSeason() + offset;
        Season offsetSeason = tvMatcher.getTvScanner().getSeason(toNavigate.getShow(), season);
        if(offsetSeason.getDir() == null) {
            return null;
        }
        EpisodeMatch match = null;
        if(offset == PREV) {
            if((match = tvMatcher.matchLargestEpisode(offsetSeason)) == null) {
                return null;
            }
        } else if(offset == NEXT) {
            if((match = tvMatcher.matchEpisode(offsetSeason, 0)) == null) {
                if((match = tvMatcher.matchEpisode(offsetSeason, 1)) == null) {
                    return null;
                }
            }
        }
        return match;
    }
    
    /**
     * Parses the given string to determine the offset
     * @param str Offset string e.g. next, cur, prev
     * @return either PREV, CUR or NEXT depending on offset string
     */
    private static int parseStringOffset(String str) {
        if(str.startsWith("n")) {
            return NEXT;
        } else if(str.startsWith("p")) {
            return PREV;
        }
        return CUR;
    }
    
}
