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

import java.util.regex.Pattern;
import tv.model.EpisodeMatch;

/**
 *
 * @author Sam Malone
 */
public class EpisodeMatcher {
    
    public interface Matcher {
        public EpisodeMatch match(String fileName);
    }
    
    /**
     * Strip common tags from an episode filename that may interfere with
     * matching. Tags removed:
     *   Qualities: e.g. 720p, 1080i, 480p
     *   Codecs: e.g. ac3, dd5.1, aac2.0, dd 7.1, h.264, x264
     * @param fileName fileName to strip of tags
     * @return stripped filename
     */
    public static String stripCommonTags(String fileName) {
        String regex = "(?:720|480|1080)[ip]|([hx][_\\-. +]*264)|dd[_\\-. +]?[257][_\\-. +]*[01]|ac3|aac[_\\-. +]*(?:[257][_\\-. +]*[01])*";
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        return p.matcher(fileName).replaceAll("");
    }
    
    public EpisodeMatch match(String fileName) {
        return match(stripCommonTags(fileName),
            new SEDelimitedMatcher(),
            new XDelimitedMatcher(),
            new WordDelimitedMatcher(),
            new NoDelimiterMatcher(),
            new PartMatcher()
        );
    }
    
    private EpisodeMatch match(String fileName, Matcher... parsers) {
        for(Matcher matcher : parsers) {
            EpisodeMatch m = matcher.match(fileName);
            if(m != null) {
                return m;
            }
        }
        return null;
    }
    
}
