/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tv.player;

import java.io.File;
import tv.model.PlayerInfo;

/**
 *
 * @author Ice
 */
public class MediaPlayerFactory {
    
    public static MediaPlayer parsePlayer(PlayerInfo player) {
        MediaPlayer p;
        if(player.getPlayer() == null) {
            p = new VLC();
        } else {
            String name = player.getPlayer().toLowerCase();
            if(name.equals("omxplayer")) {
                p = new OMXPlayer();
            } else if(name.equals("stdout")) {
                p = new DummyPlayer();
            } else {
                p = new VLC();
            }
        }
        if(player.getPlayerExecutable() != null) {
            p.setExecutableFile(new File(player.getPlayerExecutable()));
        }
        if(player.getPlayerArguments() != null) {
            p.setArguments(player.getPlayerArguments());
        }
        return p;
    }
    
}
