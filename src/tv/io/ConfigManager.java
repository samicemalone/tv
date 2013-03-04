/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tv.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import tv.ExitCode;
import tv.MediaInfo;
import tv.exception.InvalidArgumentException;
import tv.model.Arguments;
import tv.model.Config;

/**
 *
 * @author Ice
 */
public class ConfigManager {
    
    /**
     * Merges the given Config with the given Arguments.
     * If config is null, the default config path will be checked
     * @param config Config
     * @param args Arguments
     * @throws InvalidArgumentException if windows 7 library name isn't valid
     */
    public static void mergeArguments(Config config, Arguments args) throws InvalidArgumentException {
        if(config == null) {
            config = parseConfig(getDefaultConfigFile());
        }
        if(config.getLibraryName() != null) {
            if(LibraryManager.isWindows7()) {
                if(LibraryManager.isValidLibraryName(config.getLibraryName())) {
                    args.getSourceFolders().addAll(LibraryManager.parseLibraryFolders(config.getLibraryName()));
                } else {
                    throw new InvalidArgumentException("Windows 7 Library name is invalid", ExitCode.LIBRARY_NOT_FOUND);
                }
            }
        }
        if(config.getMediainfoBinary() != null) {
            MediaInfo.setExecutableFile(new File(config.getMediainfoBinary()));
        }
        if(config.getPlayer() != null) {
            args.setPlayer(config.getPlayer());
        }
        if(config.getSourceFolders() != null) {
            args.getSourceFolders().addAll(config.getSourceFolders());
        }
    }
    
    /**
     * Get the default config file
     * @return Config File
     */
    public static File getDefaultConfigFile() {
        if(LibraryManager.isWindows()) {
            return new File("C:\\ProgramData\\" + System.getProperty("user.name") + "\\tv\\tv.conf");
        } else {
            return new File(System.getProperty("user.home") + "/.tv/tv.conf");
        }
    }
    
    /**
     * Parses the configFile and returns a Config object
     * @param configFile Config File
     * @return Config
     */
    public static Config parseConfig(File configFile) {
        Config c = new Config();
        try {
            BufferedReader br = new BufferedReader(new FileReader(configFile));
            String line;
            while((line = br.readLine()) != null) {
                parseLine(c, line);
            }
            br.close();
        } catch(IOException e) {
            
        }
        return c;
    }
    
    /**
     * Parses a line of the config file and adds the data to the given
     * Config object
     * @param c Config
     * @param line Line of Config File
     */
    private static void parseLine(Config c, String line) {
        if(line.isEmpty() || line.charAt(0) == '#') {
            return;
        }
        int equalsIndex = line.indexOf('=');
        String key = line.substring(0, equalsIndex);
        String value = line.substring(equalsIndex + 1);
        if(key.equals("SOURCE")) {
            c.addSourceFolder(value);
        }
        if(key.equals("MEDIAINFO_BINARY")) {
            c.setMediainfoBinary(value);
        }
        if(key.equals("LIBRARY_NAME")) {
            c.setLibraryName(value);
        }
        if(key.equals("PLAYER")) {
            c.setPlayer(value);
        }
    }
    
}