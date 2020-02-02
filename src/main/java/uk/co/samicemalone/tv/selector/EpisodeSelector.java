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

package uk.co.samicemalone.tv.selector;

import uk.co.samicemalone.libtv.matcher.TVEpisodeMatcher;
import uk.co.samicemalone.libtv.matcher.path.TVPath;
import uk.co.samicemalone.libtv.model.EpisodeMatch;
import uk.co.samicemalone.tv.exception.ExitException;
import uk.co.samicemalone.tv.tvdb.model.Show;
import uk.co.samicemalone.tv.tvdb.model.ShowProgress;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author Sam Malone
 */
public abstract class EpisodeSelector implements Comparable<EpisodeSelector>, CurrentProgressProvider {
    public static List<EpisodeSelector> defaultSelectors(TVPath tvPath) {
        return Arrays.asList(
            new SeasonSelector(tvPath),
            new AllEpisodesSelector(tvPath),
            new RemainingEpisodesInSeasonSelector(tvPath),
            new EpisodeRangeSelector(tvPath),
            new RemainingEpisodesInShowSelector(tvPath),
            new SeasonRangeSelector(tvPath),
            new LatestSeasonSelector(tvPath)
        );
    }

    public static EpisodeSelector findEpisodeSelector(Collection<EpisodeSelector> selectors, String selector) {
        for (EpisodeSelector episodeSelector : selectors) {
            if(selector.matches(episodeSelector.getSelector())) {
                return episodeSelector;
            }
        }
        return null;
    }


    private final TVEpisodeMatcher tvEpisodeMatcher;

    /**
     * Creates a new EpisodeMode instance
     * @param tvPath TVPath
     */
    public EpisodeSelector(TVPath tvPath) {
        this.tvEpisodeMatcher = new TVEpisodeMatcher(tvPath);
    }

    /**
     * Get the EpisodesMode that represents this EpisodeMode.
     * @return Episode Mode
     */
    public abstract String getMode();

    public abstract String getSelector();

    @Override
    public ShowProgress getCurrentProgress(Show show, String tag) throws ExitException {
        return null;
    }

    public int getPriority() {
        return 50;
    }

    /**
     * Get the TVEpisodeMatcher
     * @return TVEpisodeMatcher
     */
    public TVEpisodeMatcher getTVEpisodeMatcher() {
        return tvEpisodeMatcher;
    }

    /**
     * Find the episode files that match the specified episode mode
     * @return List of episode Files or empty list if none found
     * @throws IOException if unable to list any directories
     * @throws ExitException if unable to determine the list of episodes to
     * match
     */
    public abstract List<EpisodeMatch> findMatches() throws IOException, ExitException;

    /**
     * Find the episode files that match the specified episode mode or throw
     * an ExitException if no matches are found.
     * @return List of episode Files
     * @throws IOException if unable to list any directories
     * @throws ExitException if unable to determine the list of episodes to
     * match or if there are no episode matches
     */
    public abstract List<EpisodeMatch> findMatchesOrThrow() throws IOException, ExitException;
    
    @Override
    public int compareTo(EpisodeSelector e) {
        int priority = Integer.compare(getPriority(), e.getPriority());
        if(priority == 0) {
            return getMode().compareTo(e.getMode());
        }
        return priority;
    }
}
