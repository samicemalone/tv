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

import java.util.List;
import tv.TVScan;
import tv.exception.ExitException;
import tv.matcher.TVMatcher;
import tv.model.Episode;
import tv.model.EpisodeMatch;

/**
 * For episode modes that don't need to use a pointer, see {@link EpisodeRangeMode}.
 * For episode modes that can read and write to a pointer, see {@link PointerMode}.
 * For episode modes that only need to write to a pointer, see {@link WriteOnlyPointerMode}.
 * @author Sam Malone
 */
public abstract class EpisodeMode {
    
    private final int mode;
    private final TVMatcher tvMatcher;
    
    /**
     * Creates a new EpisodeMode instance
     * @param mode EpisodeModes Episode Mode
     * @param scanner TV Scanner
     * @see EpisodeModes
     */
    public EpisodeMode(int mode, TVScan scanner) {
        this.mode = mode;
        this.tvMatcher = new TVMatcher(scanner);
    }
    
    /**
     * Get the EpisodesMode that represents this EpisodeMode.
     * @see EpisodeModes
     * @return Episode Mode
     */
    public int getMode() {
        return mode;
    }

    /**
     * Get the TVMatcher
     * @return TVMatcher
     */
    public TVMatcher getTvMatcher() {
        return tvMatcher;
    }
    
    /**
     * Find the episode files that match the specified episode mode
     * @return List of episode Files or empty list if none found
     * @throws ExitException if unable to determine the list of episodes to
     * match
     */
    public abstract List<EpisodeMatch> findMatches() throws ExitException;
    
    /**
     * Find the episode files that match the specified episode mode or throw
     * an ExitException if no matches are found.
     * @return List of episode Files
     * @throws ExitException if unable to determine the list of episodes to
     * match or if there are no episode matches
     */
    public abstract List<EpisodeMatch> findMatchesOrThrow() throws ExitException;
    
    /**
     * Get the new episode pointer to be set
     * @param match matched episode
     * @return new episode pointer or null if one should be not be set
     * @throws tv.exception.ExitException if unable to determine the new pointer
     */
    public abstract Episode getNewPointer(EpisodeMatch match) throws ExitException;
    
}
