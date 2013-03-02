/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tv.player;

import java.io.File;

/**
 *
 * @author Ice
 */
public class DummyPlayer extends MediaPlayer {

    @Override
    public void play(File[] list) {
        for(File file : list) {
            System.out.println("PLAY: " + file.getName());
        }
    }

    @Override
    public void enqueue(File[] list) {
        for(File file : list) {
            System.out.println("ENQUEUE: " + file.getName());
        }
    }

    @Override
    public String getExecutablePath() {
        return "/not/used";
    }

    @Override
    protected String getFormattedPath(String fullPath) {
        return "/not/used";
    }
    
}
