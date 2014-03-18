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

import java.io.File;
import tv.model.Episode;
import tv.model.Season;

/**
 *
 * @author Sam Malone
 */
public class NavigableEpisode {
    
    private static class Offset {
        
        private final String offsetEpisodeNo;
        private final File offsetEpisode;

        public Offset(Season season, String episodeNo, int offset) {
            offsetEpisodeNo = addIntString(episodeNo, offset);
            offsetEpisode = TVScan.getEpisode(season, offsetEpisodeNo);
        }
    }
    
    /*
     * Offset Values
     */
    public static final int PREV = -1;
    public static final int CUR = 0;
    public static final int NEXT = 1;

    private final TVScan tvScanner;
    
    public NavigableEpisode(TVScan scanner) {
        tvScanner = scanner;
    }
    
    /**
     * Navigate from the episode by the offset given.
     * @param toNavigate episode to navigate from
     * @param strOffset offset string e.g. next, cur, prev
     * @return modified Episode toNavigate that represents the episode with the given offset
     */
    public Episode navigate(Episode toNavigate, String strOffset) {
        int offset = parseStringOffset(strOffset);
        if(offset == CUR) {
            return toNavigate;
        }
        Season season = Season.fromEpisode(tvScanner, toNavigate);
        Offset o = new Offset(season, toNavigate.getEpisodeNo(), offset);
        if(o.offsetEpisode != null) {
            // check for multi part episodes to skip over if they in the same file
            File currentFile = TVScan.getEpisode(season, toNavigate.getEpisodeNo());
            while(o.offsetEpisode.equals(currentFile)) {
                Offset tmpOffset = new Offset(season, o.offsetEpisodeNo, offset);
                if(tmpOffset.offsetEpisode == null) {
                    return navigateSeason(toNavigate, offset);
                }
                o = tmpOffset;
            }
            toNavigate.setEpisodeNo(o.offsetEpisodeNo);
            return toNavigate;
        }
        return navigateSeason(toNavigate, offset);
    }
    
    /**
     * Navigate to the start of the next season or the end of the previous
     * season.
     * @param toNavigate episode to navigate from
     * @param offset {@link #PREV} or {@link #NEXT}
     * @return modified Episode toNavigate. if unable to find previous/next season
     * directory, toNavigate will not be modified.
     */
    public Episode navigateSeason(Episode toNavigate, int offset) {
        int season = Integer.valueOf(toNavigate.getSeasonNo()) + offset;
        Season offsetSeason = tvScanner.getSeason(toNavigate.getShow(), season);
        if(offsetSeason.getSeasonDir() != null) {
            if(offset == PREV) {
                String lastEpisode = TVScan.getLastEpisodeNo(offsetSeason);
                toNavigate.setSeasonNo(addIntString(toNavigate.getSeasonNo(), PREV));
                toNavigate.setEpisodeNo(addIntString(lastEpisode, 0));
            } else if(offset == NEXT) {
                File prequel = TVScan.getEpisode(offsetSeason, "00");
                toNavigate.setEpisodeNo(prequel == null ? "01" : "00");
                toNavigate.setSeasonNo(addIntString(toNavigate.getSeasonNo(), NEXT));
            }
        }
        return toNavigate;
    }
    
    /**
     * Perform addition on a string interpreted as an integer
     * @param str Base string to perform addition on e.g. "03"
     * @param num Amount to add to the string
     * @return Zero padded string of addition result
     */
    public static String addIntString(String str, int num) {
        int val = Integer.valueOf(str) + num;
        if(val < 10) {
            return "0" + String.valueOf(val);
        }
        return String.valueOf(val);
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
