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

package uk.co.samicemalone.tv.model;

import uk.co.samicemalone.tv.TVScan;

/**
 *
 * @author Sam Malone
 */
public class EpisodeRange {
    
    public static EpisodeRange fromArray(String[] episodesRange) {
        return new EpisodeRange(
            TVScan.getSeasonNo(episodesRange[0]),
            Integer.valueOf(TVScan.getEpisodeNo(episodesRange[0])),
            TVScan.getSeasonNo(episodesRange[1]),
            Integer.valueOf(TVScan.getEpisodeNo(episodesRange[1]))
        );
    }
    
    private final int startSeason;
    private final int startEpisode;
    private final int endSeason;
    private final int endEpisode;

    public EpisodeRange(int startSeason, int startEpisode, int endSeason, int endEpisode) {
        this.startSeason = startSeason;
        this.startEpisode = startEpisode;
        this.endSeason = endSeason;
        this.endEpisode = endEpisode;
    }

    public int getStartSeason() {
        return startSeason;
    }

    public int getStartEpisode() {
        return startEpisode;
    }

    public int getEndSeason() {
        return endSeason;
    }

    public int getEndEpisode() {
        return endEpisode;
    }
    
    /**
     * If the start and end seasons are the same, the episodes can be
     * represented as a Range.
     * @return Range of episodes or null if start/end seasons aren't the same
     */
    public Range toRange() {
        return startSeason == endSeason ? new Range(startEpisode, endEpisode) : null;
    }
    
}
