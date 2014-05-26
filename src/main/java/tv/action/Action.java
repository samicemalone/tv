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
package tv.action;

import java.io.File;
import tv.exception.ExitException;
import tv.model.Episode;
import uk.co.samicemalone.libtv.VideoFilter;

/**
 *
 * @author Sam Malone
 */
public interface Action {
        
    public final static int PLAY = 1;
    public final static int ENQUEUE = 2;
    public final static int LIST = 3;
    public final static int COUNT = 4;
    public final static int SIZE = 5;
    public final static int LENGTH = 6;
    
    // bit flags
    public final static int RANDOM = 16;
    public final static int LISTPATH = 32;
    
    /**
     * Extension Filter to be used to accept files based on their extension.
     */
    public VideoFilter FILTER = new VideoFilter();
    
    /**
     * Execute the Action on the given File list.
     * @param list List of episiode Files
     * @throws ExitException if an error occurs whilst executing the action.
     */
    public void execute(File[] list) throws ExitException;
    
    /**
     * Execute the Action with the given File and store the episode pointer given
     * @param list List of episode Files
     * @param pointerEpisode New Episode pointer to be set. If null, the pointer
     * will be ignored.
     * @throws ExitException if an error occurs whilst executing the action.
     */
    public void execute(File list, Episode pointerEpisode) throws ExitException;
    
}
