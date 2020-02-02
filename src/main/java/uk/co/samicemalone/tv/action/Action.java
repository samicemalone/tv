/*
 * Copyright (c) 2013, Sam Malone. All rights reserved.
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
package uk.co.samicemalone.tv.action;

import uk.co.samicemalone.libtv.model.EpisodeMatch;
import uk.co.samicemalone.tv.exception.ExitException;
import uk.co.samicemalone.tv.tvdb.model.Show;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Sam Malone
 */
public interface Action {
    static List<Action> defaultActions() {
        return Arrays.asList(
            new MediaPlayerAction(Action.PLAY),
            new MediaPlayerAction(Action.ENQUEUE),
            new ListAction(),
            new ListAction(Action.LIST_PATH),
            new CountAction(),
            new SizeAction(),
            new LengthAction()
        );
    }
        
    int PLAY = 1;
    int ENQUEUE = 2;
    int LIST = 3;
    int LIST_PATH = 4;
    int COUNT = 5;
    int SIZE = 6;
    int LENGTH = 7;
    int SEEN = 8;
    int UNSEEN = 9;

    boolean isAction(int action);

    /**
     * Execute the Action on the given EpisodeMatch list
     *
     * @param show
     * @param list List of episode file matches
     * @throws ExitException if an error occurs whilst executing the action.
     */
    void execute(Show show, List<EpisodeMatch> list) throws ExitException;
    
}
