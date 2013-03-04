/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tv;

/**
 *
 * @author Ice
 */
public class ExitCode {
    public final static int SUCCESS = 0;
    public final static int SHOW_INPUT_REQUIRED = 1; // no show was given
    public final static int SHOW_NOT_FOUND = 2;  // show input doesnt exist
    public final static int PARSE_EPISODES_FAILED = 3; // episode syntax invalid
    public final static int SEASON_NOT_FOUND = 4;  // given season was not found
    public final static int EPISODES_NOT_FOUND = 5; // syntax valid, but not found
    public final static int EPISODES_RANGE_NOT_FOUND = 6; // syntax valid, but not found
    public final static int MISSING_USERNAME = 7;  // username is required with -u flag
    public final static int NO_STORED_EPISODE_DATA = 8;  // episode pointer not set
    public final static int EPISODE_POINTER_INVALID = 9;  // episode pointer offset invalid
    public final static int MISSING_FILE = 10;  // file is required with -f flag
    public final static int FILE_NOT_FOUND = 11;  // file not found (using -f flag)
    public final static int EPISODE_INPUT_REQUIRED = 12;  // episode input doesnt exist
    public final static int MISSING_SOURCE = 13;  // source arg not given
    public final static int MISSING_LIBRARY = 14;  // library arg not given
    public final static int LIBRARY_NOT_FOUND = 15;  // library not found
    public final static int SOURCE_DIR_NOT_FOUND = 16;  // source dir not found
    public final static int SEASON_RANGE_NOT_FOUND = 17; // season range not found
    public final static int MISSING_CONFIG = 18; // config is required with --config flag
    public final static int CONFIG_FILE_NOT_FOUND = 19; // config file not found (with --config)
}
