/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tv.player;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import tv.io.LibraryManager;

/**
 *
 * @author Ice
 */
public class VLC extends MediaPlayer {
    
    private static String VLC_PATH = "C:\\Program Files (x86)\\VideoLAN\\VLC\\vlc.exe";
    
    public VLC() {
        if(!LibraryManager.isWindows()) {
            VLC_PATH = "/usr/bin/vlc";
        }
    }
    
    public VLC(String path) {
        VLC_PATH = path;
    }

    @Override
    public void play(File[] list) {
        String[] command = buildCommandString(list);
        try {
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            System.err.println("COULD NOT PLAY VLC SET");
        }
    }

    @Override
    public void enqueue(File[] list) {
        String[] command = buildCommandString(list, "--playlist-enqueue");
        try {
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            System.err.println("COULD NOT PLAY VLC SET");
        }
    }
    
    @Override
    public String getExecutablePath() {
        return VLC_PATH;
    }

    @Override
    protected String getFormattedPath(String fullPath) {
        return "file:///" + fullPath;
    }
    
}
