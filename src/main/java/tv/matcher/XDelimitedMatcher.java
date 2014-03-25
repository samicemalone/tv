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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import tv.model.EpisodeMatch;

/**
 * Matches: (with variants of separators)
 * the.league - 1x01 - pilot.mkv",
 * the.league - 1x01x02 - pilot.mkv",
 * the.league - 1x01x02x03x04 - pilot.mkv",
 * the.league 1x1x2x3x4 pilot.mkv",
 * the.league.1x01_1x02.pilot.mkv",
 * the.league.1x01.pilot.1x02.pilot.cont.mkv",
 * the league - 1x01+1x02 - pilot.mkv"
 * etc...
 * @author Sam Malone
 */
public class XDelimitedMatcher implements EpisodeMatcher.Matcher {
    
    private final static String separator = "[_\\-. +]*";

    private final static Pattern pattern = Pattern.compile(
        new StringBuilder().append("(\\d+)").append(separator).append("x(\\d+)").append(separator)
          .append("((?:(?:(?:.*?)(?:\\d+)").append(separator)
          .append(")?x\\d+").append(separator).append(")*)").toString(),
        Pattern.CASE_INSENSITIVE
    );

    @Override
    public EpisodeMatch match(String fileName) {
        Matcher m = pattern.matcher(fileName);
        if(m.find()) {
            int season = Integer.valueOf(m.group(1));
            int episode = Integer.valueOf(m.group(2));
            EpisodeMatch em = new EpisodeMatch(season, episode);
            em.getEpisodes().addAll(parseMultiEpisodes(m.group(3)));
            return em;
        }
        return null;
    }
        
    private List<Integer> parseMultiEpisodes(String multiEpisodes) {
        List<Integer> list = new ArrayList<Integer>();
        if(multiEpisodes == null || multiEpisodes.isEmpty()) {
            return list;
        }
        String[] matches = multiEpisodes.split("[_\\-. +]+");
        Pattern p = Pattern.compile("(?:\\d+)?x(\\d+)", Pattern.CASE_INSENSITIVE);
        for(String match : matches) {
            Matcher m = p.matcher(match);
            while(m.find()) {
                list.add(Integer.valueOf(m.group(1)));
            }
        }
        return list;
    }
    
}
