/*
 * Copyright (c) 2013, Sam Malone. All rights reserved.
 * 
 * Redistribution and use of this software in source and binary forms, with or
 * without modification, are permitted provided that the following conditions
 * are met:
 * 
 *  - Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *  - Neither the name of Sam Malone nor the names of its contributors may be
 *    used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
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
 * @author Sam Malone
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
                continue;
            }
            if(args[i].equals("-k") || args[i].equals("--kill")) {
                arg.setShutDown(true);
                return arg;
            }
            if(args[i].equals("-v") || args[i].equals("--version")) {
                arg.setVersion(true);
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
                arg.getPlayerInfo().setPlayer(args[i+1]);
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
        if(arg.isListPathSet()) {
            arg.setMediaActionFlag(Action.LISTPATH);
        }
        if(arg.isFileSet()) {
            return arg;
        }
        if(arg.getSourceFolders().isEmpty()) {
            throw new MissingArgumentException("The --source or --library input is required", ExitCode.SOURCE_DIR_NOT_FOUND);
        }
        if(arg.isServerSet()) {
            return arg;
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
        if(arg.isShutDownSet() || arg.isVersionSet()) {
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
        if(arg.isServerSet()) {
            return;
        }
        if(!TVScan.showExists(arg.getShow())) {
            throw new FileNotFoundException("Unable to find show: " + arg.getShow(), ExitCode.SHOW_NOT_FOUND);
        }
    }
    
    /**
     * Gets the help message
     */
    public static String getHelpMessage() {
        StringBuilder sb = new StringBuilder(4000);
        sb.append("Usage: tv TVSHOW EPISODES [ACTION] [-hvsi] [--source DIR]... [--library NAME]\n");
        sb.append("          [-r [NO]] [-p MP] [-u USER] [--config CONFIG]\n");
        sb.append("       tv -f FILE [ACTION] [-p MP] [--config CONFIG]\n");
        sb.append("       tv -d [--source DIR]... [--library NAME] [-p MP] [--config CONFIG]\n");
        sb.append("       tv -k\n");
        sb.append('\n');
        sb.append("    -u, --user USER   To be used when EPISODES is either prev, cur, next.\n");
        sb.append("                      To set your episode pointer you have to play a single\n");
        sb.append("                      episode first.\n");
        sb.append("    -s, --set         Set the current episode. EPISODES can be prev, cur, next\n");
        sb.append("                      or single episode format.\n");
        sb.append("    -i, --ignore      Do not remember the episode. EPISODES can be prev, cur,\n");
        sb.append("                      next, pilot, latest or single episode format.\n");
        sb.append("    -r, --random [NO] Selects random episode(s) from the EPISODES range given.\n");
        sb.append("                      If NO is omitted, 1 random episode will be returned.\n");
        sb.append("                      If NO is \"all\" then all EPISODES will be randomized.\n");
        sb.append("                      Otherwise NO EPISODES will be returned randomized.\n");
        sb.append("                      EPISODES can be any format that isn't pointer syntax\n");
        sb.append("    -p, --player MP   Sets the media player to use. Default is \"vlc\"\n");
        sb.append("    --config CONFIG   Sets the config file to use\n");
        sb.append("    --source DIR      TV source folder. You can use this option multiple times\n");
        sb.append("    --library NAME    Windows 7 Library NAME will be used to determine sources\n");
        sb.append("    -f, --file FILE   Plays FILE from the filesystem. Can use -q to enqueue\n");
        sb.append("    -d, --daemon      Listens for requests on port 5768\n");
        sb.append("    -k, --kill        Kills the listening daemon\n");
        sb.append("    -h, --help        This help message will be printed then exit.\n");
        sb.append("    -v, --version     This version will be printed then exit.\n");
        sb.append('\n');
        sb.append("ACTION can be one of the following options:\n");
        sb.append("    -q, --enqueue     Enqueue files. Default is to play immediately.\n");
        sb.append("    -l, --list        List episodes matched.\n");
        sb.append("    --list-path       Lists the full paths of the episodes matched.\n");
        sb.append("    -c, --count       Counts the episodes from the EPISODES range given.\n");
        sb.append("                      EPISODES can be any format that isn't pointer syntax\n");
        sb.append("    --size            Prints the total size of the EPISODES/FILE given\n");
        sb.append("    --length          This option requires mediainfo to be installed. It\n");
        sb.append("                      adds up the length of each episode matched in EPISODES\n");
        sb.append("                      or FILE and outputs the total in the format hh:mm:ss\n");
        sb.append('\n');
        sb.append("MP can be one of the following supported media players:\n");
        sb.append("  vlc\n");
        sb.append("  omxplayer\n");
        sb.append('\n');
        sb.append("EPISODES can use the following formats:\n");
        sb.append(" *  s01e02              Single episode\n");
        sb.append("    s02e12-s03e03       Episode range\n");
        sb.append("    s01e04-             Remaining episodes in the season from given episode\n");
        sb.append("    s01                 Whole season\n");
        sb.append("    s02-s04             Season range\n");
        sb.append("    s02-                All seasons from the given season\n");
        sb.append("    all                 Every episode\n");
        sb.append(" *  pilot               Pilot episode. Alias for s01e01\n");
        sb.append(" *  latest              Latest episode\n");
        sb.append(" *  prev, cur, next     Episode based on pointer\n");
        sb.append("    prev-, cur-, next-  Remaining episodes in the season from given pointer\n");
        sb.append('\n');
        sb.append("The pointer will be set whenever single epsisode format is used to play or\n");
        sb.append("enqueue. You can use the ignore flag (-i) to stop the pointer being set.\n");
        sb.append("Options such as --list, --count etc.. do not modify the pointer. It is\n");
        sb.append("recommended to create an alias to run the program or use a config file to\n");
        sb.append("avoid entering the media sources every time its run. Formats above marked\n");
        sb.append("with an asterisk (*) will modify the pointer.\n");
        sb.append('\n');
        sb.append("Examples:      tv Scrubs pilot --source 'D:\\TV'\n");
        sb.append("               tv Scrubs s01 --library TV\n");
        sb.append("               tv Scrubs all -r --source 'D:\\TV' --source 'E:\\Path\\TV'\n");
        sb.append("               tv Scrubs s02e05- --config ~/.tv/tv.conf\n");
        sb.append("               tv Scrubs next --source 'D:\\TV'\n");
        sb.append("using config:  tv Scrubs prev -u testuser\n");
        sb.append("               tv Scrubs s02e05-s02e13\n");
        sb.append("               tv Scrubs s06 --count\n");
        sb.append("               tv -f scrubs.s01e02.avi\n");
        return sb.toString();
    }
    
}
