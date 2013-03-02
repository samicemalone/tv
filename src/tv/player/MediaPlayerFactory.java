/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tv.player;

/**
 *
 * @author Ice
 */
public class MediaPlayerFactory {
    
    public static MediaPlayer parsePlayer(String player) {
        player = player.toLowerCase();
        if(player.equals("omxplayer")) {
            return new OMXPlayer();
        }
        if(player.equals("stdout")) {
            return new DummyPlayer();
        }
        return new VLC();
    }
    
}
