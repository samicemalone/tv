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
package tv.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sam Malone
 */
public class Config {
        
    private String player;
    private String playerExecutable;
    private final List<String> playerArguments;
    private final List<String> sourceDirs;
    private final List<String> extraDirs;
    private String libraryName;
    private String mediainfoBinary;
    private String tvdbFile;
    
    private boolean isTraktEnabled;
    private String traktUsername;
    private String traktPasswordSha1;
    private String traktApiKey;
    
    public Config() {
        sourceDirs = new ArrayList<String>();
        extraDirs = new ArrayList<String>();
        playerArguments = new ArrayList<String>();
    }

    /**
     * Get the media player given in the config
     * @return media player or null if not set
     */
    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    /**
     * Gets a list of the extra folders given in the config
     * @return list of extra folders or empty list
     */
    public List<String> getExtraFolders() {
        return extraDirs;
    }

    public void addExtraFolder(String extraDir) {
        extraDirs.add(extraDir);
    }

    /**
     * Gets a list of the source folders given in the config
     * @return list of source folders or empty list
     */
    public List<String> getSourceFolders() {
        return sourceDirs;
    }

    public void addSourceFolder(String sourceDir) {
        sourceDirs.add(sourceDir);
    }

    /**
     * Get the windows library name if given in the config
     * @return windows library name or null if not specified
     */
    public String getLibraryName() {
        return libraryName;
    }

    public void setLibraryName(String libraryName) {
        this.libraryName = libraryName;
    }

    /**
     * Get the path to the mediainfo binary as given in the config
     * @return path to mediainfo binary or null if not given
     */
    public String getMediainfoBinary() {
        return mediainfoBinary;
    }

    public void setMediainfoBinary(String mediainfoBinary) {
        this.mediainfoBinary = mediainfoBinary;
    }

    /**
     * Get the path to the TVDB file given in the config
     * @return path to the TVDB file or null if not set
     */
    public String getTVDBFile() {
        return tvdbFile;
    }

    public void setTVDBFile(String tvdbFile) {
        this.tvdbFile = tvdbFile;
    }

    /**
     * Get the path to the media player executable/binary given in the config
     * @return path to media player executable or null if not set
     */
    public String getPlayerExecutable() {
        return playerExecutable;
    }

    public void setPlayerExecutable(String playerExecutable) {
        this.playerExecutable = playerExecutable;
    }

    /**
     * Gets an array of arguments to send to media player which were given
     * in the config
     * @return array of media player arguments or empty array
     */
    public String[] getPlayerArguments() {
        return playerArguments.toArray(new String[] {});
    }

    public void addPlayerArgument(String playerArguments) {
        this.playerArguments.add(playerArguments);
    }

    public boolean isTraktEnabled() {
        return isTraktEnabled;
    }
    
    public void setTraktEnabled(String enabled) {
        if("true".equals(enabled)) {
            isTraktEnabled = true;
        }
    }

    public String getTraktApiKey() {
        return traktApiKey;
    }
    
    public void setTraktApiKey(String traktApiKey) {
        this.traktApiKey = traktApiKey;
    }

    public String getTraktPasswordSha1() {
        return traktPasswordSha1;
    }

    public void setTraktPasswordSha1(String traktPasswordSha1) {
        this.traktPasswordSha1 = traktPasswordSha1;
    }

    public String getTraktUsername() {
        return traktUsername;
    }

    public void setTraktUsername(String traktUsername) {
        this.traktUsername = traktUsername;
    }
    
}
