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
package uk.co.samicemalone.tv.util;

import uk.co.samicemalone.libtv.model.EpisodeNavigator;
import uk.co.samicemalone.libtv.model.EpisodeRange;
import uk.co.samicemalone.libtv.model.Range;

/**
 *
 * @author Sam Malone
 */
public class TVUtil {
    
    private TVUtil() {
        
    }
    
    /**
     * Gets the integer season number for the given episode string
     * @param ep Episode string e.g. s01e04
     * @return int season number e.g. 1
     */
    public static int getSeasonNo(String ep) {
        return Integer.valueOf(ep.substring(1, 3));
    }
    
    /**
     * Gets the episode number in string format for the given episode string
     * @param ep Episode string e.g. s01e04
     * @return String episode number e.g. "04"
     */
    public static String getEpisodeNo(String ep) {
        return ep.substring(4, 6);
    }
    
    /**
     * Get the season range for the given episode string
     * @param ep Episode string e.g. s01-s03
     * @return season range
     */
    public static Range getSeasonRange(String ep) {
        String[] seasonRange = ep.split("-", 2);
        return new Range(TVUtil.getSeasonNo(seasonRange[0]), TVUtil.getSeasonNo(seasonRange[1]));
    }
    
    /**
     * Get the episode range for the given episode string
     * @param ep Episode string e.g. s01e12-s02e04
     * @return episode range
     */
    public static EpisodeRange getEpisodeRange(String ep) {
        String[] episodesRange = ep.split("-", 2);
        return new EpisodeRange(
            TVUtil.getSeasonNo(episodesRange[0]),
            Integer.valueOf(TVUtil.getEpisodeNo(episodesRange[0])),
            TVUtil.getSeasonNo(episodesRange[1]),
            Integer.valueOf(TVUtil.getEpisodeNo(episodesRange[1]))
        );
    }
    
    /**
     * Get the Pointer for navigating an episode from the given episode string
     * @param ep Episode string as pointer e.g. prev, cur, next
     * @return Pointer or {@link EpisodeNavigator.Pointer#CUR} if unknown string
     */
    public static EpisodeNavigator.Pointer getNavigationPointer(String ep) {
        switch(ep.toLowerCase()) {
            case "prev":
            case "previous":
                return EpisodeNavigator.Pointer.PREV;
            case "next":
                return EpisodeNavigator.Pointer.NEXT;
            default:
                return EpisodeNavigator.Pointer.CUR;
        }
    }
    
}
