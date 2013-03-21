/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tv.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import tv.Main;
import tv.model.Episode;

/**
 *
 * @author Ice
 */
public class TVDBManager extends CSV_IO {
    
    private HashMap<String, Episode> list;
    private ArrayList<Episode> episodeList;
    private String user;
    private boolean isReadOnlyUser;
    
    private final static int CSV_COLUMNS = 4;
    private static File TVDB_FILE = null;
    
    public TVDBManager() {
        super(getTVDBFile());
    }
    
    /**
     * Gets the TVDB File. The default will be used and returned if
     * no TVDB File is already set.
     * @return 
     */
    public static File getTVDBFile() {
        if(TVDB_FILE == null) {
            if(LibraryManager.isWindows()) {
                TVDB_FILE = new File("C:\\ProgramData\\" + System.getProperty("user.name") + "\\tv\\tvdb.csv");
            } else {
                TVDB_FILE = new File(System.getProperty("user.home") + "/.tv/tvdb.csv");
            }
        }
        return TVDB_FILE;
    }
    
    /**
     * Sets the File to be used for the TVDB
     * @param TVDB 
     */
    public static void setTVDBFile(File TVDB) {
        TVDB_FILE = TVDB;
    }
    
    /**
     * Checks if the tv database contains the episode information given show
     * @param show TV Show
     * @return true if the tv database contains episode data, false otherwise
     */
    public boolean containsEpisodeData(String show) {
        return list.containsKey(show);
    }
    
    /**
     * Gets the episode information for the given show
     * @param show TV Show
     * @return Episode data for the given show, or null if doesn't exist
     */
    public Episode getEpisode(String show) {
        return list.get(show);
    }
    
    /**
     * Gets the list of shows from the TV Database as a CSV formatted string
     * @return CSV formatted show list
     */
    public String getCSVShows() {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < Main.sourceFolders.size(); i++) {
            String[] epList = new File(Main.sourceFolders.get(i)).list();
            for(int j = 0; j < epList.length; j++) {
                appendCSVLine(sb, epList[j]);
            }
        }
        return sb.substring(0, sb.length()-1);
    }
    
    /**
     * Gets the list of episodes from the TV Database as a CSV formatted string
     * @param eps CSV formatted episode list
     * @return 
     */
    public String getCSVEpisodes(ArrayList<Episode> eps) {
        StringBuilder sb = new StringBuilder();
        Episode e;
        for(Iterator<Episode> i = eps.iterator(); i.hasNext(); ) {
            e = i.next();
            appendCSVLine(sb, e.getShow(), e.getUser(), e.getSeasonNo(), e.getEpisodeNo());    
        }
        return sb.toString();
    }
    
    /**
     * Writes the given episode information to the TV Database.
     * Will attempt to create the parent directory for the TVDB if
     * it doesn't exist.
     * @param show TV Show
     * @param user User
     * @param season Zero Padded Season No
     * @param episode Zero Padded Episode No
     */
    public void writeStorage(String show, String user, String season, String episode) {
        if(!CSV_FILE.getParentFile().exists()) {
            if(!CSV_FILE.getParentFile().mkdir()) {
                System.out.println("Unable to create a directory at " + CSV_FILE.getParentFile().getAbsolutePath());
                return;
            }
        }
        StringBuilder sb = new StringBuilder();
        boolean found = false;
        try {
            ArrayList<Episode> eps = readAllStorage();
            for(int i = 0; i < eps.size(); i++) {
                if(eps.get(i).getShow().equals(show) && eps.get(i).getUser().equals(user)) {
                    eps.get(i).setSeasonNo(season);
                    eps.get(i).setEpisodeNo(episode);
                    found = true;
                    break;
                }
            }
            Episode e;
            for(Iterator<Episode> i = eps.iterator(); i.hasNext(); ) {
                e = i.next();
                appendCSVLine(sb, e.getShow(), e.getUser(), e.getSeasonNo(), e.getEpisodeNo());    
            }
            if(!found) {
                appendCSVLine(sb, show, user, season, episode);
            }
            writeFile(sb.toString());
        } catch(FileNotFoundException ex) {
            appendCSVLine(sb, show, user, season, episode);
            writeFile(sb.toString());
        }
    }
    
    /**
     * Reads the TV Database information for the given user
     * @param user User
     * @throws FileNotFoundException If the TV database doesn't exist
     */
    public void readStorage(String user) throws FileNotFoundException {
        list = new HashMap<String, Episode>();
        isReadOnlyUser = true;
        this.user = user;
        readFile(CSV_COLUMNS);
        isReadOnlyUser = false;
    }
    
    /**
     * Reads the TV Database information for all users
     * @return List of episode information, empty list if exists but no eps
     * @throws FileNotFoundException If the TV database doesn't exist
     */
    public ArrayList<Episode> readAllStorage() throws FileNotFoundException {
        episodeList = new ArrayList<Episode>();
        readFile(CSV_COLUMNS);
        return episodeList;
    }

    @Override
    protected void handleLine(Matcher m) {
        if(m.find()) {
            if(isReadOnlyUser) {
                if(m.group(2).equals(user)) {
                    list.put(m.group(1), new Episode(m.group(1), m.group(3), m.group(4)));
                }
            } else {
                Episode tmpEp = new Episode(m.group(1), m.group(3), m.group(4));
                tmpEp.setUser(m.group(2));
                episodeList.add(tmpEp);
            }
        }
    }
    
}
