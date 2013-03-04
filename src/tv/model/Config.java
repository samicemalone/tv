/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tv.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ice
 */
public class Config {
        
    private String player;
    private List<String> sourceDirs;
    private String libraryName;
    private String mediainfoBinary;
    
    public Config() {
        sourceDirs = new ArrayList<String>();
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public List<String> getSourceFolders() {
        return sourceDirs;
    }

    public void addSourceFolder(String sourceDir) {
        sourceDirs.add(sourceDir);
    }

    public String getLibraryName() {
        return libraryName;
    }

    public void setLibraryName(String libraryName) {
        this.libraryName = libraryName;
    }

    public String getMediainfoBinary() {
        return mediainfoBinary;
    }

    public void setMediainfoBinary(String mediainfoBinary) {
        this.mediainfoBinary = mediainfoBinary;
    }
    
}
