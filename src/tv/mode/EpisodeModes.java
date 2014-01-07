/*
 * Copyright (c) 2013, Sam Malone
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package tv.mode;

/**
 *
 * @author Sam Malone
 */
public class EpisodeModes {
    
    /*
     * Episode Modes
     */
    public static final int SEASON = 0;
    public static final int EPSINGLE = 1;
    public static final int SEASONFROMEP = 2;
    public static final int EPRANGE = 3;
    public static final int POINTER = 4;
    public static final int SEASONFROMPOINTER = 5;
    public static final int ALL = 6;
    public static final int SEASONRANGE = 7;
    public static final int ALLFROMSEASON = 8;
    public static final int PILOT = 9;
    public static final int LATEST = 10;
    public static final int LATEST_SEASON = 11;
    
    public static final int INVALID_MODE = -1;
    
    /**
     * Gets the string array of valid regex patterns.
     * The indices use the episode mode constants e.g. SEASON, EPSINGLE etc...
     * @return valid array of regex patterns
     */
    private static String[] getValidRegex() {
        String[] regex = new String[12];
        String season = "s[0-9][0-9]"; 
        String ep = season + "e[0-9][0-9]";
        regex[SEASON] = season + "$";
        regex[EPSINGLE] = ep + "$";
        regex[SEASONFROMEP] = ep + "-$";
        regex[EPRANGE] = ep + "-" + ep + "$";
        regex[POINTER] = "prev|current|cur|next";
        regex[SEASONFROMPOINTER] = "(" + regex[POINTER] + ")-$";
        regex[ALL] = "all";
        regex[SEASONRANGE] = season + '-' + season + '$';
        regex[ALLFROMSEASON] = season + "-$";
        regex[PILOT] = "pilot";
        regex[LATEST] = "latest";
        regex[LATEST_SEASON] = "s\\$$";
        return regex;
    }
    
    /**
     * Checks whether the episode string is valid
     * @param ep Episode string e.g. s01e03, next, s02
     * @return true if valid, false otherwise
     */
    public static boolean episodesValid(String ep) {
        return getEpisodesMode(ep) != INVALID_MODE;
    }
    
    /**
     * Gets the episode mode for the given episode string
     * @param ep Episode string e.g. s01e03, next, s02
     * @return Episode mode or {@link #INVALID_MODE} if not valid
     */
    public static int getEpisodesMode(String ep) {
        String[] regex = getValidRegex();
        for(int i = 0; i < regex.length; i++) {
            if(ep.matches(regex[i])) {
                return i;
            }
        }
        return INVALID_MODE;
    }
    
}
