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

package tv.mode;

import java.util.ArrayList;
import java.util.List;
import tv.ExitCode;
import tv.TV;
import tv.TVScan;
import tv.exception.ExitException;
import tv.model.Episode;
import tv.model.EpisodeMatch;
import tv.model.EpisodeRange;
import tv.model.Range;

/**
 * EpisodeRangeMode should be used for modes that require matching a range of
 * episodes and/or seasons, and modes that do not involve using the pointer.
 * For episode modes that can read and write to a pointer, see {@link PointerMode}.
 * For episode modes that only need to write to a pointer, see {@link WriteOnlyPointerMode}.
 * @author Sam Malone
 */
public class EpisodeRangeMode extends EpisodeMode {

    public EpisodeRangeMode(int mode, TVScan scanner) {
        super(mode, scanner);
    }
    
    /**
     * Build the list of episode Files as specified by the episode mode
     * @return List of episode Files or empty File array if none found
     * @throws ExitException if unable to determine the list of files for the given mode
     */
    @Override
    public List<EpisodeMatch> findMatches() throws ExitException {
        String episodes = TV.ENV.getArguments().getEpisodes();
        String show = TV.ENV.getArguments().getShow();
        List<EpisodeMatch> list = new ArrayList<EpisodeMatch>();
        switch(getMode()) {
            case EpisodeModes.SEASON:
                list = getTvMatcher().matchSeason(show, TVScan.getSeasonNo(episodes));
                break;
            case EpisodeModes.SEASONFROMEP:
                int episode = Integer.valueOf(TVScan.getEpisodeNo(episodes));
                list = getTvMatcher().matchEpisodesFrom(show, TVScan.getSeasonNo(episodes), episode);
                break;
            case EpisodeModes.EPRANGE:
                list = getTvMatcher().matchEpisodeRange(show, EpisodeRange.fromArray(episodes.split("-")));
                break;
            case EpisodeModes.ALL:
                list = getTvMatcher().matchAllEpisodes(show);
                break;
            case EpisodeModes.SEASONRANGE:
                list = getTvMatcher().matchSeasonRange(show, Range.fromSeason(episodes.split("-")));
                break;
            case EpisodeModes.ALLFROMSEASON:
                list = getTvMatcher().matchSeasonsFrom(show, TVScan.getSeasonNo(episodes));
                break;
            case EpisodeModes.LATEST_SEASON:
                list = getTvMatcher().matchLargestSeason(show);
                break;
        }
        return list;
    }
    
    @Override
    public List<EpisodeMatch> findMatchesOrThrow() throws ExitException {
        List<EpisodeMatch> matches = findMatches();
        if(matches.isEmpty()) {
            switch(getMode()) {
                case EpisodeModes.SEASON:
                case EpisodeModes.LATEST_SEASON:
                    throw new ExitException("Unable to match any episodes in the season", ExitCode.EPISODES_NOT_FOUND);
                case EpisodeModes.ALL:
                    throw new ExitException("Unable to match any episodes", ExitCode.EPISODES_NOT_FOUND);
                case EpisodeModes.SEASONFROMEP:
                case EpisodeModes.EPRANGE:
                    throw new ExitException("Unable to match any episodes in the given range", ExitCode.EPISODES_RANGE_NOT_FOUND);
                case EpisodeModes.SEASONRANGE:
                case EpisodeModes.ALLFROMSEASON:
                    throw new ExitException("Unable to match any episodes in the given range", ExitCode.SEASON_RANGE_NOT_FOUND);
            }
        }
        return matches;
    }
    
    /**
     * Get the new episode pointer to be set
     * @param match episode match
     * @return new episode pointer or null if one should be not be set
     * @throws tv.exception.ExitException
     */
    @Override
    public Episode getNewPointer(EpisodeMatch match) throws ExitException {
        return null;
    }
    
}
