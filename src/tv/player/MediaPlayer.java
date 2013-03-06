/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
