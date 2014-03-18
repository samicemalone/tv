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
package tv.model;

import java.io.File;
import tv.TVScan;

/**
 *
 * @author Sam Malone
 */
public class Season {
    
    public static Season fromEpisode(TVScan scanner, Episode ep) {
        int season = Integer.valueOf(ep.getSeasonNo());
        return new Season(season, scanner.getSeasonDirectory(ep.getShow(), season));
    }
    
    private final File seasonDir;
    private final String seasonNoString;
    private final int seasonNo;
    
    public Season(int season, File dir) {
        seasonNo = season;
        seasonNoString = String.format("%02d", season);
        seasonDir = dir;
    }
    
    public Season(String season, File dir) {
        seasonNo = Integer.valueOf(season);
        seasonNoString = season;
        seasonDir = dir;
    }

    /**
     * Gets the directory of the season location
     * @return 
     */
    public File getSeasonDir() {
        return seasonDir;
    }
    
    /**
     * Get season number
     * @return 
     */
    public int getSeasonNo() {
        return seasonNo;
    }
    
    /**
     * Gets the season as a string, zero padded to at least 2 characters
     * @return Season String
     */
    public String getSeasonString() {
        return seasonNoString;
    }
}
