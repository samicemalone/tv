/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tv;

import java.io.File;
import tv.action.Action;
import tv.action.ActionHandler;
import tv.exception.ExitException;
import tv.exception.FileNotFoundException;
import tv.exception.InvalidArgumentException;
import tv.exception.MissingArgumentException;
import tv.exception.ParseException;
import tv.io.ConfigManager;
import tv.io.LibraryManager;
import tv.model.Arguments;
import tv.model.Config;

/**
 *
 * @author Ice
 */
public class ArgsParser {
    
    /**
     * Attempts to parse the given arguments. 
     * @param args Arguments
     * @return Parsed Arguments instance or null if the help message is to be
     * displayed only
     * @throws MissingArgumentException if there is an argument missing
     * @throws InvalidArgumentException if windows 7, and library name is invalid
     * @throws ParseException if the episode string is not valid
     * @throws FileNotFoundException if the config file is not found when used with
     * the --config option.
     */
    public static Arguments parse(String[] args) throws ExitException {
        Arguments arg = new Arguments();
        Config config = null;
        int TVSHOW_INDEX = -1; // TVSHOW required arg
        boolean isArg = false;
        for(int i = 0; i < args.length; i++) {
            if(isArg) {
                isArg = false;
                continue;
            }
            if(args[i].equals("-h") || args[i].equals("--help")) {
                return null;
            }
            if(args[i].equals("-d") || args[i].equals("--daemon")) {
                arg.setServer(true);
                return arg;
            }
            if(args[i].equals("-k") || args[i].equals("--kill")) {
                arg.setShutDown(true);
                return arg;
            }
            if(args[i].equals("-r") || args[i].equals("--random")) {
                if(i + 1 < args.length) {
                    // -r is optional. check if next argument is a value or another arg
                    if(!args[i+1].startsWith("-")) {
                        try {
                            if(args[i+1].equalsIgnoreCase("all")) {
                                arg.setRandom(Integer.MAX_VALUE);
                            } else {
                                arg.setRandom(Integer.parseInt(args[i+1]));
                            }
                            isArg = true;
                            continue;
                        } catch (Exception e) {

                        }
                    }
                }
                arg.setRandom(true);
                continue;
            }
            if(args[i].equals("--config")) {
                if(i + 1 < args.length) {
                    if(!new File(args[i+1]).exists()) {
                        throw new FileNotFoundException("The config file given does not exist", ExitCode.CONFIG_FILE_NOT_FOUND);
                    }
                    config = ConfigManager.parseConfig(new File(args[i+1]));
                    isArg = true;
                    continue;
                } else {
                    throw new MissingArgumentException("You need to give an argument with the --config option", ExitCode.MISSING_CONFIG);
                }
            }
            if(args[i].equals("--source")) {
                if(i + 1 < args.length) {
                    arg.addSourceFolder(args[i+1]);
                    isArg = true;
                    continue;
                } else {
                    throw new MissingArgumentException("You need to give an argument with the --source option", ExitCode.MISSING_SOURCE);
                }
            }
            if(args[i].equals("--library")) {
                if(i + 1 < args.length) {
                    if(LibraryManager.isWindows7()) {
                        if(LibraryManager.isValidLibraryName(args[i+1])) {
                            arg.getSourceFolders().addAll(LibraryManager.parseLibraryFolders(args[i+1]));
                        } else {
                            throw new InvalidArgumentException("Windows 7 Library name is invalid", ExitCode.LIBRARY_NOT_FOUND);
                        }
                    }
                    isArg = true;
                    continue;
                } else {
                    throw new MissingArgumentException("You need to give an argument with the --library option", ExitCode.MISSING_LIBRARY);
                }
            }
            if(args[i].equals("--enqueue") || args[i].equals("-q")) {
                arg.setMediaAction(Action.ENQUEUE);
                continue;
            }
            if(args[i].equals("--count") || args[i].equals("-c")) {
                arg.setMediaAction(Action.COUNT);
                continue;
            }
            if(args[i].equals("--set") || args[i].equals("-s")) {
                arg.setIsSetOnly(true);
                continue;
            }
            if(args[i].equals("--ignore") || args[i].equals("-i")) {
                arg.setIgnore(true);
                continue;
            }
            if(args[i].equals("--list") || args[i].equals("-l")) {
                arg.setMediaAction(Action.LIST);
                continue;
            }
            if(args[i].equals("--list-path")) {
                arg.setMediaAction(Action.LIST);
                arg.setListPath(true);
                continue;
            }
            if(args[i].equals("--size")) {
                arg.setMediaAction(Action.SIZE);
                continue;
            }
            if(args[i].equals("--player") || args[i].equals("-p")) {
                arg.setPlayer(args[i+1]);
                isArg = true;
                continue;
            }
            if(args[i].equals("--length")) {
                arg.setMediaAction(Action.LENGTH);
                continue;
            }
            if(args[i].equals("--user") || args[i].equals("-u")) {
                if(i + 1 < args.length) {
                    arg.setUser(args[i+1]);
                    isArg = true;
                    continue;
                } else {
                    throw new MissingArgumentException("You need give a username with the -u or --user flag", ExitCode.MISSING_USERNAME);
                }
            }
            if(args[i].equals("--file") || args[i].equals("-f")) {
                if(i + 1 < args.length) {
                    arg.setFile(args[i+1]);
                    isArg = true;
                    continue;
                } else {
                    throw new MissingArgumentException("You need specify a file with the -f or --file flag", ExitCode.MISSING_FILE);
                }
            }
            if(TVSHOW_INDEX == -1 && !isArg) {
                TVSHOW_INDEX = i;
            }
        }
        ConfigManager.mergeArguments(config, arg);
        if(arg.isFileSet()) {
            return arg;
        }
        if(arg.getSourceFolders().isEmpty()) {
            throw new MissingArgumentException("The --source or --library input is required", ExitCode.SOURCE_DIR_NOT_FOUND);
        }
        if(TVSHOW_INDEX < 0) {
            throw new MissingArgumentException("The SHOW input is required", ExitCode.SHOW_INPUT_REQUIRED);
        }
        if(TVSHOW_INDEX + 1 >= args.length) {
            throw new MissingArgumentException("The EPISODES input is required", ExitCode.EPISODE_INPUT_REQUIRED);
        }
        arg.setShow(args[TVSHOW_INDEX]);
        arg.setEpisode(args[TVSHOW_INDEX+1]);
        if(!TVScan.episodesValid(arg.getEpisodes())) {
            throw new ParseException("Unable to parse the episodes given", ExitCode.PARSE_EPISODES_FAILED);
        }
        if(arg.isRandomSet()) {
            arg.setMediaActionFlag(Action.RANDOM);
            ActionHandler.RANDOM_COUNT = arg.getRandomCount();
        }
        if(arg.isListPathSet()) {
            arg.setMediaActionFlag(Action.LISTPATH);
        }
        return arg;
    }
    
    /**
     * Validates the given Arguments. Validation is successful
     * if no exceptions were thrown.
     * @param arg Parsed Arguments
     * @throws FileNotFoundException if show, source directories, input files
     * cannot be found. if length is set, and mediainfo binary cannot be found
     */
    public static void validate(Arguments arg) throws FileNotFoundException {
        if(arg.isServerSet() || arg.isShutDownSet()) {
            return;
        }
        if(arg.getMediaAction() == Action.LENGTH) {
            if(!MediaInfo.getExecutableFile().exists()) {
                throw new FileNotFoundException("The MediaInfo binary could not be found", ExitCode.FILE_NOT_FOUND);
            }
        }
        if(arg.isFileSet()) {
            File f = arg.getFile();
            if(f == null || !f.exists()) {
                throw new FileNotFoundException("The input file does not exist", ExitCode.FILE_NOT_FOUND);
            }
            return;
        }
        for(String source : arg.getSourceFolders()) {
            if(!new File(source).exists()) {
                throw new FileNotFoundException("The source you have entered does not exist", ExitCode.SOURCE_DIR_NOT_FOUND);
            }
        }
        if(!TVScan.showExists(arg.getShow())) {
            throw new FileNotFoundException("Unable to find show: " + arg.getShow(), ExitCode.SHOW_NOT_FOUND);
        }
    }
    
    /**
     * Prints the help message to stdout
     */
    public static void printHelp() {
        System.out.println("Usage: tv TVSHOW EPISODES (--source DIR)... [-hlqsic] [-r [NO]] [-p MP] [-u USER]");
        System.out.println("       tv TVSHOW EPISODES --library NAME [-hlqsic] [-r [NO]] [-p MP] [-u USER]");
        System.out.println("       tv -f FILE");
        System.out.println();
        System.out.println("    -q, --enqueue     Enqueue files. Default is to play immediately.");
        System.out.println("    -l, --list        List episodes matched.");
        System.out.println("    --list-path       Lists the full paths of the episodes matched.");
        System.out.println("    -u, --user USER   To be used when EPISODES is either prev, cur, next.");
        System.out.println("                      To set your episode pointer you have to play a single");
        System.out.println("                      episode first.");
        System.out.println("    -s, --set         Set the current episode. EPISODES can be prev, cur, next");
        System.out.println("                      or single episode format.");
        System.out.println("    -i, --ignore      Do not remember the episode. EPISODES can be prev, cur,");
        System.out.println("                      next, pilot, latest or single episode format.");
        System.out.println("    -r, --random [NO] Selects random episode(s) from the EPISODES range given.");
        System.out.println("                      If NO is omitted, 1 random episode will be returned.");
        System.out.println("                      If NO is \"all\" then all EPISODES will be randomized.");
        System.out.println("                      Otherwise NO EPISODES will be returned randomized.");
        System.out.println("                      EPISODES can be any format that isn't pointer syntax");
        System.out.println("    -c, --count       Counts the episodes from the EPISODES range given.");
        System.out.println("                      EPISODES can be any format that isn't pointer syntax");
        System.out.println("    -p, --player MP   Sets the media player to use. Default is \"vlc\"");
        System.out.println("    --config CONFIG   Sets the config file to use");
        System.out.println("    --source DIR      TV source folder. You can use this option multiple times");
        System.out.println("    --library NAME    Windows 7 Library NAME will be used to determine sources");
        System.out.println("    --size            Prints the total size of the EPISODES given");
        System.out.println("    -f, --file FILE   Plays FILE from the filesystem. Can use -q to enqueue");
        System.out.println("    -d, --daemon      Listens for requests on port 5768");
        System.out.println("    -k, --kill        Kills the listening daemon");
        System.out.println("    -h, --help        This help message will be outputted.");
        System.out.println();
        System.out.println("MP can be one of the following supported media players:");
        System.out.println("  vlc");
        System.out.println("  omxplayer");
        System.out.println();
        System.out.println("EPISODES can use the following formats:");
        System.out.println(" *  s01e02              Single episode");
        System.out.println("    s02e12-s03e03       Episode range");
        System.out.println("    s01e04-             Remaining episodes in the season from given episode");
        System.out.println("    s01                 Whole season");
        System.out.println("    s02-s04             Season range");
        System.out.println("    s02-                All seasons from the given season");
        System.out.println("    all                 Every episode");
        System.out.println(" *  pilot               Pilot episode. Alias for s01e01");
        System.out.println(" *  latest              Latest episode");
        System.out.println(" *  prev, cur, next     Episode based on pointer");
        System.out.println("    prev-, cur-, next-  Remaining episodes in the season from given pointer");
        System.out.println();
        System.out.println("The pointer will be set whenever single epsisode format is used to play or");
        System.out.println("enqueue. You can use the ignore flag (-i) to stop the pointer being set.");
        System.out.println("Options such as --list, --count etc.. do not modify the pointer. It is ");
        System.out.println("recommended to create an alias to run the program to avoid entering the");
        System.out.println("media sources every time its run. Formats above marked with an asterisk (*)");
        System.out.println("will modify the pointer.");
        System.out.println();
        System.out.println("Examples:    tv Scrubs s01 --source 'D:\\TV'");
        System.out.println("             tv Scrubs s01e01 --library TV");
        System.out.println("             tv Scrubs next --source 'D:\\TV'");
        System.out.println("             tv Scrubs prev -u USER --library Television");
        System.out.println("             tv Scrubs s02e05- --library TV");
        System.out.println("             tv Scrubs s02e05-s02e13 --library TV");
        System.out.println("             tv Scrubs all -r --source 'D:\\TV' --source 'E:\\Path\\TV'");
        System.out.println("             tv Scrubs s06 -c --library TV");
        System.out.println("             tv -f scrubs.s01e02.avi");
        System.out.println();
    }
    
}
