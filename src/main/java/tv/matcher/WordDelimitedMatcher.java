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

package tv.matcher;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import tv.model.EpisodeMatch;

/**
 * Matches: (with variants of separators)
 * the.league ep01 pilot.mkv
 * the.league.ep.01.pilot.mkv
 * the.league e02 name.mkv
 * the_league - e2 - name.mkv
 * the.league ep01ep02 pilot.mkv
 * the.league.ep_01_ep_02.pilot.mkv
 * the league season 1 episode 1 pilot.mkv
 * the league season 1 ep 1 ep 2 pilot.mkv
 * etc...
 * @author Sam Malone
 */
public class WordDelimitedMatcher implements EpisodeFileMatcher.Matcher {
    
    private final static String separator = "[_\\-. +]*";
    private final static String epSeparator = "(?:e|ep|episode)";

    private final static Pattern pattern = Pattern.compile(
        new StringBuilder().append("(?:season").append(separator).append("(\\d+)").append(separator)
          .append(")?").append(epSeparator).append(separator).append("(\\d+)").toString(),
        Pattern.CASE_INSENSITIVE
    );

    @Override
    public EpisodeMatch match(String absolutePath, String filteredFileName) {
        Matcher m = pattern.matcher(filteredFileName);
        if(m.find()) {
            int season;
            if(m.group(1) == null) {
                Matcher seasonMatcher = TVMatcher.SEASON_PATTERN.matcher(absolutePath);
                if(!seasonMatcher.find()) {
                    return null;
                }
                season = Integer.valueOf(seasonMatcher.group(1));
            } else {
                season = Integer.valueOf(m.group(1));
            }
            int episode = Integer.valueOf(m.group(2));
            EpisodeMatch em = new EpisodeMatch(season, episode);
            while(m.find()) {
                em.getEpisodes().add(Integer.valueOf(m.group(2)));
            }
            return em;
        }
        return null;
    }
    
}
