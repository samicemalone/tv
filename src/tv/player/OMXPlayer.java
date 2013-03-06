/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tv.player;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author Ice
 */
public class OMXPlayer extends MediaPlayer {
        
    public OMXPlayer() {
        setExecutableFile(new File("/home/pi/omxpipe"));
        setArguments(new String[] {"-o", "hdmi"});
    }

    @Override
    public void play(File[] list) {
        String[] command = buildCommandString(list, getArguments());
        try {
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            System.err.println("COULD NOT PLAY OMX SET");
        }
    }

    @Override
    public void enqueue(File[] list) {
        System.err.println("OMXPlayer does not support enqueuing files");
        //play(new File[] { list[0] });
    }

    @Override
    protected String getFormattedPath(String fullPath) {
        return fullPath;
    }
    
}
