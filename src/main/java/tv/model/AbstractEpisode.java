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

package tv.model;

import java.util.Collection;

/**
 *
 * @author Sam Malone
 */
public abstract class AbstractEpisode {
    
    public abstract Collection<Integer> getEpisodes();
    
    public Range getEpisodesAsRange() {
        int min = Integer.MAX_VALUE;
        int max = -1;
        for(int ep : getEpisodes()) {
            if(ep < min) {
                min = ep;
            }
            if(ep > max) {
                max = ep;
            }
        }
        return new Range(min, max);
    }
    
    public boolean episodeMatches(MatchCondition<Integer> condition) {
        for(Integer ep : getEpisodes()) {
            if(condition.matches(ep)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isEpisodeNo(final int episode) {
        return episodeMatches(new MatchCondition<Integer>() {
            @Override
            public boolean matches(Integer toMatch) {
                return toMatch == episode;
            }
        });
    }
    
    public boolean isEpisodeInRange(final Range range) {
        return episodeMatches(new MatchCondition<Integer>() {
            @Override
            public boolean matches(Integer toMatch) {
                return toMatch >= range.getStart() && toMatch <= range.getEnd();
            }
        });
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(int episode : getEpisodes()) {
            sb.append('e').append(String.format("%02d", episode));
        }
        return sb.toString();
    }
    
}
