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
package uk.co.samicemalone.tv.model;

import uk.co.samicemalone.tv.action.Action;
import uk.co.samicemalone.tv.options.WindowsEnvironment;
import uk.co.samicemalone.tv.util.CygwinUtil;
import uk.co.samicemalone.tv.util.WSLUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sam Malone
 */
public class Arguments {
    
    private int mediaAction = Action.PLAY;
    private int randomCount = 0;
    private String USER = "";
    private String EPISODES;
    private String SHOW;
    private String FILE = "";
    private String CONFIG;
    private String LIBRARY;
    private PlayerInfo PLAYER = new PlayerInfo();
    private final List<String> sourceFolders;
    private boolean isHelpSet = false;
    private boolean isIgnoreSet = false;
    private boolean isSetOnly = false;
    private boolean isTraktPointerSet = false;
    private boolean isVersionSet = false;

    /**
     * Creates a new instance of the class
     */
    public Arguments() {
        sourceFolders = new ArrayList<>();
    }
    
    /**
     * Adds a source folder argument
     * @param folder full path
     */
    public void addSourceFolder(String folder) {
        sourceFolders.add(folder);
    }
    
    /**
     * Gets a list of the source folders specified with the --source flag
     * @return list of source folders or empty list
     */
    public List<String> getSourceFolders() {
        return sourceFolders;
    }

    /**
     * Get the path to the config file
     * @return path to the config file or null if none set
     */
    public String getConfigPath() {
        if(CONFIG != null && WindowsEnvironment.isWindows()) {
            if(CONFIG.startsWith(CygwinUtil.CYGWIN_PATH)) {
                return CygwinUtil.toWindowsPath(CONFIG);
            } else if(CONFIG.startsWith(WSLUtil.WSL_PATH)) {
                return WSLUtil.toWindowsPath(CONFIG);
            }
        }
        return CONFIG;
    }
    
    /**
     * Set the path to the config file
     * @param configPath config file path
     */
    public void setConfigPath(String configPath) {
        this.CONFIG = configPath;
    }

    /**
     * Get the name of the Windows library
     * @return name of the Windows library or null if none set
     */
    public String getLibraryPath() {
        return LIBRARY;
    }
    
    /**
     * Set the name of the Windows library
     * @param libraryPath Windows library name
     */
    public void setLibraryPath(String libraryPath) {
        LIBRARY = libraryPath;
    }
    
    /**
     * Sets the number of random items to be used
     * @param randomCount 
     */
    public void setRandomCount(int randomCount) {
        this.randomCount = randomCount;   
    }
    
    /**
     * Get the number of random items to be selected
     * @return 
     */
    public int getRandomCount() {
        return randomCount;
    }
    
    /**
     * Check if the file argument is set
     * @return true if -f argument is set, false otherwise
     */
    public boolean isFileSet() {
        return !"".equals(FILE);
    }
    
    /**
     * Set the file argument
     * @param file 
     */
    public void setFile(String file) {
        FILE = file;
    }
    
    /**
     * Get the File object representing the path argument given (with -f)
     * Cygwin/WSL paths will be changed into Windows format
     * @return Single File object from -f argument
     */
    public File getFile() {
        if(WindowsEnvironment.isWindows()) {
            if(getFileString().startsWith(CygwinUtil.CYGWIN_PATH)) {
                return new File(CygwinUtil.toWindowsPath(FILE));
            } else if(getFileString().startsWith(WSLUtil.WSL_PATH)) {
                return new File(WSLUtil.toWindowsPath(FILE));
            }
        }
        return new File(FILE);
    }
    
    /**
     * Get the full file string path for a filesystem file
     * @return full path
     */
    public String getFileString() {
        return FILE;
    }

    /**
     * Checks if the flag is set to fetch the pointer from trakt
     * @return true if trakt pointer is to be fetched, false otherwise
     */
    public boolean isTraktPointerSet() {
        return isTraktPointerSet;
    }

    /**
     * Set the flag to fetch the episode pointer from trakt
     * @param isTraktPointer true to fetch traktpointer, false to follow the local pointer
     */
    public void setTraktPointer(boolean isTraktPointer) {
        this.isTraktPointerSet = isTraktPointer;
    }

    /**
     * Checks if the flag is set that only sets the pointer
     * @return true if pointer is to be set only, false otherwise
     */
    public boolean isSetOnly() {
        return isSetOnly;
    }

    /**
     * Set the flag to only set the pointer without playing
     * @param isSetOnly true to set pointer only, false to follow the media action
     */
    public void setIsSetOnly(boolean isSetOnly) {
        this.isSetOnly = isSetOnly;
    }

    /**
     * Checks if the ignore flag is set
     * @return true if ignore flag is set, false otherwise
     */
    public boolean isIgnoreSet() {
        return isIgnoreSet;
    }

    /**
     * Sets the ignore flag
     * @param setIgnore true to ignore, default is false
     */
    public void setIgnore(boolean setIgnore) {
        this.isIgnoreSet = setIgnore;
    }

    public boolean isHelpSet() {
        return isHelpSet;
    }

    public void setIsHelpSet(boolean isHelpSet) {
        this.isHelpSet = isHelpSet;
    }

    /**
     * Sets the episode string argument
     * @param EPISODES Episode string
     */
    public void setEpisode(String EPISODES) {
        this.EPISODES = EPISODES;
    }

    /**
     * Sets the show argument
     * @param SHOW Show
     */
    public void setShow(String SHOW) {
        this.SHOW = SHOW;
    }

    /**
     * Sets the user argument
     * @param USER User
     */
    public void setUser(String USER) {
        this.USER = USER;
    }

    /**
     * Sets the media action
     * @param action media action
     * @see Action
     */
    public void setMediaAction(int action) {
        mediaAction = action;
    }

    /**
     * Gets the media action
     * @return media action
     * @see Action
     */
    public int getMediaAction() {
        return mediaAction;
    }
    
    /**
     * Get user argument
     * @return User
     */
    public String getUser() {
        return USER;
    }
    
    /**
     * Gets the TV show argument
     * @return TV Show
     */
    public String getShow() {
        return SHOW;
    }
    
    /**
     * Gets the episode string e.g. s02e03, next etc...
     * @return Episode string
     */
    public String getEpisodes() {
        return EPISODES;
    }
    
    /**
     * Gets the media player information i.e. type, exe, args
     * @return media player information
     */
    public PlayerInfo getPlayerInfo() {
        return PLAYER;
    }
    
    /**
     * Sets the media player information
     * @param player media player information
     */
    public void setPlayerInfo(PlayerInfo player) {
        PLAYER = player;
    }

    /**
     * Sets the display version flag
     * @param b 
     */
    public void setVersion(boolean b) {
        isVersionSet = b;
    }
    
    /**
     * Checks if the flag is set to display the version
     * @return true if flag is set, false otherwise
     */
    public boolean isVersionSet() {
        return isVersionSet;
    }
}
