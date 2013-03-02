/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tv.model;

import java.io.File;
import tv.TVScan;

/**
 *
 * @author Ice
 */
public class Season {
    
    private File seasonDir;
    private String seasonNoString;
    private int seasonNo;
    
    public Season(String show, int season) {
        seasonNo = season;
        seasonNoString = String.format("%02d", season);
        seasonDir = TVScan.getSeasonDirectory(show, seasonNo);
    }
    
    public Season(String show, String season) {
        seasonNo = Integer.valueOf(season);
        seasonNoString = season;
        seasonDir = TVScan.getSeasonDirectory(show, seasonNo);
    }
    
    /**
     * Gets the directory of the season location
     * @return 
     */
    public File getSeasonDir() {
        return seasonDir;
    }
    
    /**
     * Get season number
     * @return 
     */
    public int getSeasonNo() {
        return seasonNo;
    }
    
    /**
     * Gets the season as a string, zero padded to at least 2 characters
     * @return Season String
     */
    public String getSeasonString() {
        return seasonNoString;
    }
}
