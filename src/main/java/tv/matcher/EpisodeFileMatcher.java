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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import tv.comparator.EpisodeNoComparator;
import tv.model.EpisodeMatch;
import tv.model.MatchCondition;
import tv.model.Range;

/**
 *
 * @author Sam Malone
 */
public class EpisodeFileMatcher {
    
    public interface Matcher {
        public EpisodeMatch match(String absolutePath, String filteredFileName);
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
    
    /**
     * Match file to an episode to determine the season and episode number(s)
     * @param file file to match
     * @return EpisodeMatch if found, otherwise null.
     */
    public EpisodeMatch match(File file) {
        return match(file,
            new SEDelimitedMatcher(),
            new XDelimitedMatcher(),
            new WordDelimitedMatcher(),
            new NoDelimiterMatcher(),
            new PartMatcher()
        );
    }
    
    /**
     * Match files to the episode given by episodeNo
     * @param files files to search for episode match
     * @param episodeNo episode number
     * @return episode match or null if not found
     */
    public EpisodeMatch match(File[] files, int episodeNo) {
        for(File file : files) {
            EpisodeMatch m = match(file);
            if(m != null && m.isEpisodeNo(episodeNo)) {
                return m;
            }
        }
        return null;
    }
    
    /**
     * Match file to an episode to determine the season and episode number(s).
     * Each Matcher is checked in array order and returns as soon as a match is found
     * @param file file to match
     * @param matchers Matchers to check in the order given
     * @return EpisodeMatch if found, otherwise null.
     */
    private EpisodeMatch match(File file, Matcher... matchers) {
        String filteredName = stripCommonTags(file.getName());
        for(Matcher matcher : matchers) {
            EpisodeMatch m = matcher.match(file.getAbsolutePath(), filteredName);
            if(m != null) {
                m.setEpisodeFile(file);
                return m;
            }
        }
        return null;
    }
    
    /**
     * Match each file to an episode to determine the season and episode number(s).
     * Wrapper for match(file, null)
     * @param files files in same season to search for episode matches
     * @return EpisodeMatch list in episode order or empty list if no matches.
     */
    public List<EpisodeMatch> match(File[] files) {
        return match(files, null);
    }
    
    /**
     * Match each file to an episode to determine the season and episode number(s).
     * In addition, the given MatchCondition must be satisfied in order for the episode
     * to be matched.
     * The file list is assumed to only contain episodes within the same season
     * @param files files in same season to search for episode matches
     * @param condition additional condition to be satisfied before accepting a match
     * or null to place no extra conditions on the match
     * @return EpisodeMatch list in episode order or empty list if no matches.
     */
    public List<EpisodeMatch> match(File[] files, MatchCondition<EpisodeMatch> condition) {
        List<EpisodeMatch> matches = new ArrayList<EpisodeMatch>();
        for(File file : files) {
            EpisodeMatch match = match(file);
            if(match != null && (condition == null || condition.matches(match))) {
                matches.add(match);
            }
        }
        Collections.sort(matches, new EpisodeNoComparator());
        return matches;
    }
    
    /**
     * Match each file name to an episode in the Range given.
     * The file list is assumed to only contain episodes within the same season
     * @param files files in same season to search for episode matches
     * @param range range of episodes to match
     * @return EpisodeMatch list in episode order or empty list if no matches.
     */
    public List<EpisodeMatch> matchRange(File[] files, final Range range) {
        return match(files, new MatchCondition<EpisodeMatch>() {
            @Override
            public boolean matches(EpisodeMatch match) {
                return range.isInRange(match.getEpisodesAsRange());
            }
        });
    }
    
    /**
     * Match files to episodes from the given start episode. In other words,
     * match each file to an episode which is greater than or equal to startEp.
     * @param files files in same season to search for episode matches
     * @param startEp starting episode or higher to match
     * @return EpisodeMatch list in episode order or empty list if no matches.
     */
    public List<EpisodeMatch> matchFrom(File[] files, final int startEp) {
        return match(files, new MatchCondition<EpisodeMatch>() {
            @Override
            public boolean matches(EpisodeMatch match) {
                return match.isEpisodeInRange(Range.maxRange(startEp));
            }
        });
    }
    
    /**
     * Match each file name to an episode and return the largest match
     * @param files files to search for episode matches
     * @return largest episode match or null if no episode matches
     */
    public EpisodeMatch matchLargest(File[] files) {
        List<EpisodeMatch> matches = match(files);
        EpisodeMatch largest = null;
        int max = -1;
        for(EpisodeMatch match : matches) {
            int matchMax = match.getEpisodesAsRange().getEnd();
            if(matchMax > max) {
                max = matchMax;
                largest = match;
            }
        }
        return largest;
    }
    
    /**
     * Get the episode file from the from list of episode matches given
     * and return an array of the files.
     * @param list list of matched episodes
     * @return episode file array
     */
    public static File[] toFileArray(List<EpisodeMatch> list) {
        List<File> files = new ArrayList<File>();
        for(EpisodeMatch match : list) {
            files.add(match.getEpisodeFile());
        }
        return files.toArray(new File[files.size()]);
    }
    
}
