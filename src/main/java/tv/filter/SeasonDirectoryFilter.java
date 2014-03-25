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
package tv.filter;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Sam Malone
 */
public class SeasonDirectoryFilter implements FilenameFilter {
    
    public static final String REGEX = "(?:Season|Series) ([0-9]+)";
    
    public static final String[] SEASON_DIRECTORY_PREFIX = new String[] { "Season", "Series" };
    
    @Override
    public boolean accept(File dir, String name) {
        return name.matches(REGEX);
    }
    
    /**
     * Matches the same season directories as SeasonDirectoryFilter but will
     * also record the maximum season number found which can be accessed via
     * {@link #getMax()} after the filter has been used.
     */
    public static final class Max extends SeasonDirectoryFilter {

        private final Pattern pattern;
                
        private int maxSeason = -1;

        public Max() {
            this.pattern = Pattern.compile(SeasonDirectoryFilter.REGEX);
        }

        /**
         * Get maximum season
         * @return maximum season or -1 if seasons found
         */
        public int getMax() {
            return maxSeason;
        }
        
        @Override
        public boolean accept(File dir, String name) {
            Matcher m = pattern.matcher(name);
            if(m.find()) {
                int tmpSeason = Integer.valueOf(m.group(1));
                if(tmpSeason > maxSeason) {
                    maxSeason = tmpSeason;
                }
            }
            return super.accept(dir, name);
        }
        
    }
}
