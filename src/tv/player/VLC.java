/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tv.player;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import tv.io.LibraryManager;

/**
 *
 * @author Ice
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
        return "file:///" + fullPath;
    }
    
}
