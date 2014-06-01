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

package uk.co.samicemalone.tv.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import uk.co.samicemalone.libtv.model.EpisodeMatch;
import uk.co.samicemalone.tv.TV;

/**
 *
 * @author Sam Malone
 */
public class RandomFilter {
    
    /**
     * Random count representing all files should be randomised and returned
     */
    public static final int ALL = Integer.MAX_VALUE;
    
    /**
     * Filter a list of random elements from the list given. The list given is
     * not shuffled directly - a temporary list is shuffled instead.
     * The number of files returned is determined from the -r argument. If the
     * random count is larger than the number of files, all the media files will
     * be returned randomised. If the random count is less than 1, it will be 
     * clamped to 1. Otherwise the size of list returned will be equal to RANDOM_COUNT
     * @param list List of files and/or directories to be shuffled
     * @return Shuffled list or empty array
     */
    public static List<EpisodeMatch> filter(List<EpisodeMatch> list) {
        List<EpisodeMatch> tmp = new ArrayList<>(list);
        if(list.isEmpty()) {
            return tmp;
        }
        Collections.shuffle(tmp, new Random(System.currentTimeMillis()));
        int randomCount = Math.max(1, TV.ENV.getArguments().getRandomCount());
        return randomCount < tmp.size() ? tmp.subList(0, randomCount) : tmp;
    }
    
}
