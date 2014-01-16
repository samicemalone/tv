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
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import tv.URI;
import tv.io.LibraryManager;

/**
 *
 * @author Sam Malone
 */
public class VLC extends MediaPlayer {
    
    public VLC() {
        if(LibraryManager.isWindows()) {
            setExecutableFile(new File("C:\\Program Files (x86)\\VideoLAN\\VLC\\vlc.exe"));
        } else {
            setExecutableFile(new File("/usr/bin/vlc"));
        }
    }

    @Override
    public void play(File[] list) {
        String[] command;
        if(getArguments() == null || getArguments().length == 0) {
            command = buildCommandString(list);
        } else {
            command = buildCommandString(list, getArguments());
        }
        try {
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            System.err.println("COULD NOT PLAY VLC SET");
        }
    }

    @Override
    public void enqueue(File[] list) {
        String[] command;
        if(getArguments() == null || getArguments().length == 0) {
            command = buildCommandString(list, "--playlist-enqueue");
        } else {
            List<String> args = Arrays.asList(getArguments());
            args.add("--playlist-enqueue");
            command = buildCommandString(list, args.toArray(new String[] {}));
        }
        try {
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            System.err.println("COULD NOT PLAY VLC SET");
        }
    }

    @Override
    protected String getFormattedPath(String fullPath) {
        return "file:///" + URI.encode(fullPath);
    }
    
}
