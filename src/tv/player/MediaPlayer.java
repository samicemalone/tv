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
     * Gets the path the to the media player executable
     * @return 
     */
    public abstract String getExecutablePath();
    
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
        command[0] = getExecutablePath();
        if(args.length > 0) {
            System.arraycopy(args, 0, command, 1, args.length);
        }
        for(int i = 0; i < list.length; i++) {
            command[offset + i] = getFormattedPath(list[i].getAbsolutePath());
        }
        return command;
    }
    
}
