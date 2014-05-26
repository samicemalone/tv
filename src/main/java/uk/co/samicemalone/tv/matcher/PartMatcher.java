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

package uk.co.samicemalone.tv.matcher;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import uk.co.samicemalone.tv.model.EpisodeMatch;
import uk.co.samicemalone.tv.model.RomanNumeral;

/**
 * Matches: (with variants of separators)
 * the pacific part i pilot.mkv
 * the.pacific.pt.i.pt.ii.pilot.mkv
 * @author Sam Malone
 */
public class PartMatcher implements EpisodeFileMatcher.Matcher {
    
    private static final String romanNumeralsStrict = "^M{0,4}(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})$";
    private static final String romanNumerals = "[MDCLXVI]+";
    private static final String separator = "[_\\-. +]";
    private static final String epSeparator = "(?:pt|part)";
    
    private final static Pattern pattern = Pattern.compile(
        new StringBuilder().append(epSeparator).append("(?:").append(separator).append("+(")
          .append(romanNumerals).append(")|").append(separator).append("*(\\d++))").toString(),
        Pattern.CASE_INSENSITIVE
    );

    @Override
    public EpisodeMatch match(String absolutePath, String filteredFileName) {
        Matcher m = pattern.matcher(filteredFileName);
        EpisodeMatch em = new EpisodeMatch();
        boolean hasMatched = false;
        while(m.find()) {
            hasMatched = true;
            if(m.group(1) == null && m.group(2) != null) {
                em.getEpisodes().add(Integer.valueOf(m.group(2)));
            } else if (m.group(1) != null && m.group(2) == null) {
                int romanDec = fromRomanNumeral(m.group(1));
                if(romanDec > 0) {
                    em.getEpisodes().add(romanDec);
                }
            }
        }
        if(hasMatched) {
            Matcher seasonMatcher = TVMatcher.SEASON_PATTERN.matcher(absolutePath);
            if(!seasonMatcher.find()) {
                return null;
            }
            em.setSeason(Integer.valueOf(seasonMatcher.group(1)));
        }
        return hasMatched ? em : null;
    }
    
    private int fromRomanNumeral(String roman) {
        Pattern p = Pattern.compile(romanNumeralsStrict, Pattern.CASE_INSENSITIVE);
        return p.matcher(roman).matches() ? RomanNumeral.valueOf(roman) : 0;
    }
    
}
