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
package tv.player;

import java.io.File;
import java.util.Arrays;

/**
 *
 * @author Ice
 */
public abstract class MediaPlayer {
    
    protected File EXECUTABLE;
    protected String[] ARGUMENTS;
    
    /**
     * Plays the given file list in the media player
     * @param list File List
     */
    public abstract void play(File[] list);
    
    /**
     * Enqueues the given file list in the media player
     * @param list File List
     */
    public abstract void enqueue(File[] list);
    
    /**
     * Gets the media player executable file
     * @return Media Player Executable
     */
    public File getExecutableFile() {
        return EXECUTABLE;
    }
    
    /**
     * Sets the media player executable file
     * @param binary 
     */
    public void setExecutableFile(File binary) {
        EXECUTABLE = binary;
    }
    
    /**
     * Gets the arguments to be sent the to the media player executable
     * @return Arguments or null if none set
     */
    public String[] getArguments() {
        return ARGUMENTS;
    }
    
    /**
     * Sets the arguments to be sent to the media player executable
     * @param args Arguments 
     */
    public void setArguments(String[] args) {
        ARGUMENTS = args;
    }
    
    /**
     * Gets a formatted path from an existing file path.
     * e.g. VLC prepends file:/// to the path
     * @param fullPath
     * @return 
     */
    protected abstract String getFormattedPath(String fullPath);
    
    /**
     * Builds a command string array (one argument per element) for
     * the given file list and prepends the given args after the executable
     * but but before the file list.
     * @param list File List
     * @param args Media Player Arguments
     * @return 
     */
    protected final String[] buildCommandString(File[] list, String... args) {
        int offset;
        if(args.length == 0 || Arrays.asList(args).contains(null)) {
            offset = 1;
        } else {
            offset = args.length + 1;
        }
        String[] command = new String[list.length + offset];
        command[0] = getExecutableFile().getAbsolutePath();
        if(args.length > 0) {
            System.arraycopy(args, 0, command, 1, args.length);
        }
        for(int i = 0; i < list.length; i++) {
            command[offset + i] = getFormattedPath(list[i].getAbsolutePath());
        }
        return command;
    }
    
}
