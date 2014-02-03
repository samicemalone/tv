/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tv.player;

import java.io.File;
import tv.io.LibraryManager;

/**
 * 
 * @author Sam Malone
 */
public class AceStreamPlayer extends VLC {

    public AceStreamPlayer() {
        if(LibraryManager.isWindows()) {
            String format = "%s\\AppData\\Roaming\\ACEStream\\player\\ace_player.exe";
            setExecutableFile(new File(String.format(format, System.getProperty("user.home"))));
        } else {
            setExecutableFile(new File("/usr/bin/acestreamplayer"));
        }
    }
    
}
