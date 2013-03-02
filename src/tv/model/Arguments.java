/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tv.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import tv.action.Action;

/**
 *
 * @author Ice
 */
public class Arguments {
    
    private int MEDIA_ACTION = Action.PLAY;
    private int RANDOM_COUNT = 1;
    private String USER = "";
    private String EPISODES;
    private String SHOW;
    private String FILE = "";
    private String PLAYER = "";
    private List<String> sourceFolders = null;
    private boolean isSetOnly = false;
    private boolean isIgnoreSet = false;
    private boolean isRandomSet = false;
    private boolean isServerSet = false;
    private boolean isShutDownSet = false;
    private boolean isListPathSet = false;
    
    /**
     * Creates a new instance of the class
     */
    public Arguments() {
        sourceFolders = new ArrayList<String>();
    }

    /**
     * Sets the list of source folders
     * @param folders List of folders containing the full folder path
     */
    public void setSourceFolders(List<String> folders) {
        this.sourceFolders = folders;
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
     * Check if the shut down flag is set
     * @return true if the flag is set, false otherwise
     */
    public boolean isShutDownSet() {
        return isShutDownSet;
    }
    
    /**
     * Sets the shut down flag
     * @param b 
     */
    public void setShutDown(boolean b) {
        isShutDownSet = b;
    }
    
    /**
     * Check if the server daemon flag is set
     * @return true if the flag is set, false otherwise
     */
    public boolean isServerSet() {
        return isServerSet;
    }
    
    /**
     * Set the server daemon flag
     * @param server 
     */
    public void setServer(boolean server) {
        isServerSet = server;
    }
    
    /**
     * Check if the random flag is set
     * @return true if the random flag is set, false otherwise
     */
    public boolean isRandomSet() {
        return isRandomSet;
    }
    
    /**
     * Sets the random flag
     * @param random
     */
    public void setRandom(boolean random) {
        isRandomSet = random;
    }
    
    /**
     * Sets the number of random items to be used
     * @param randomCount 
     */
    public void setRandom(int randomCount) {
        isRandomSet = true;
        RANDOM_COUNT = randomCount;   
    }
    
    /**
     * Get the number of random items to be selected
     * @return 
     */
    public int getRandomCount() {
        return RANDOM_COUNT;
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
     * Cygwin paths will be changed into Windows format
     * @return Single File object from -f argument
     */
    public File getFile() {
        if(getFileString().startsWith("/cygdrive/")) {
            char drive = getFileString().charAt(10);
            return new File(String.valueOf(drive).toUpperCase() + ":\\" + getFileString().substring(12));
        } else {
            return new File(getFileString());
        }
    }
    
    /**
     * Get the full file string path for a filesystem file
     * @return full path
     */
    public String getFileString() {
        return FILE;
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
     * @param isSetOnly true to set pointer only, false to follow the VLC action
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
     * @param VLC_ACTION VLC action
     * @see VLC
     */
    public void setMediaAction(int ACTION) {
        this.MEDIA_ACTION = ACTION;
    }
    
    /**
     * Gets the media action
     * @return VLC action
     * @see VLC
     */
    public int getMediaAction() {
        return MEDIA_ACTION;
    }
    
    /**
     * Applies the given ACTION_FLAG to the current media action.
     * E.g. ACTION_FLAG = Action.RANDOM, Action.LISTPATH
     * @param ACTION_FLAG Flag from Action class
     */
    public void setMediaActionFlag(int ACTION_FLAG) {
        MEDIA_ACTION |= ACTION_FLAG;
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
     * Gets the media player string e.g. vlc, omxplayer
     * @return media player string
     */
    public String getPlayer() {
        return PLAYER;
    }
    
    /**
     * Sets the media player e.g. vlc, omxplayer
     * @param player 
     */
    public void setPlayer(String player) {
        PLAYER = player;
    }

    /**
     * Sets the list path flag
     * @param b 
     */
    public void setListPath(boolean b) {
        isListPathSet = b;
    }
    
    /**
     * Checks if the flag is set to display the full paths when listing
     * @return true if flag is set, false otherwise
     */
    public boolean isListPathSet() {
        return isListPathSet;
    }
}
