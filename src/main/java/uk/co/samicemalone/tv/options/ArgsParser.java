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
package uk.co.samicemalone.tv.options;

import uk.co.samicemalone.tv.ExitCode;
import uk.co.samicemalone.tv.action.Action;
import uk.co.samicemalone.tv.exception.FileNotFoundException;
import uk.co.samicemalone.tv.exception.InvalidArgumentException;
import uk.co.samicemalone.tv.exception.MissingArgumentException;
import uk.co.samicemalone.tv.filter.RandomFilter;
import uk.co.samicemalone.tv.io.LibraryManager;
import uk.co.samicemalone.tv.model.Arguments;
import uk.co.samicemalone.tv.selector.EpisodeSelector;

import java.io.File;
import java.util.List;

/**
 *
 * @author Sam Malone
 */
public class ArgsParser {
    
    /**
     * Attempts to parse the given program arguments.
     * See {@link #validate(Arguments) for validation.
     * @param args program arguments
     * @return Parsed Arguments instance or null if the help flag is set
     * @throws MissingArgumentException if there is an argument missing
     * @throws InvalidArgumentException if an unexpected argument is given
     */
    public static Arguments parse(String[] args) throws MissingArgumentException, InvalidArgumentException {
        Arguments arguments = new Arguments();
        for(String curArg : args) {
            if(curArg.equals("-h") || curArg.equals("--help")) {
                arguments.setIsHelpSet(true);
                return arguments;
            }
            if(curArg.equals("-v") || curArg.equals("--version")) {
                arguments.setVersion(true);
                return arguments;
            }
        }

        boolean isArg = false;
        for(int i = 0; i < args.length; i++) {
            if(isArg) {
                isArg = false;
                continue;
            }
            try {
                isArg = parseOption(arguments, args, i);
            } catch (IndexOutOfBoundsException ex) {
                throw new MissingArgumentException(args[i]);
            }
        }
        if(!arguments.isFileSet()) {
            if(arguments.getShow() == null) {
                throw new MissingArgumentException("The SHOW input is required", ExitCode.SHOW_INPUT_REQUIRED);
            }
            if(arguments.getEpisodes() == null) {
                throw new MissingArgumentException("The EPISODES input is required", ExitCode.EPISODE_INPUT_REQUIRED);
            }
        }
        return arguments;
    }
    
    /**
     * Parse an option from the current argument.
     * @param args arguments
     * @param programArgs program arguments
     * @param curIndex index of the current argument
     * @return true if the next argument index to be parsed is an argument value,
     * false otherwise
     * @throws InvalidArgumentException if an unexpected argument is given
     */
    private static boolean parseOption(Arguments args, String[] programArgs, int curIndex) throws InvalidArgumentException {
        if(parseFlag(args, programArgs[curIndex])) {
            return false;
        }
        if(programArgs[curIndex].equals("--player") || programArgs[curIndex].equals("-p")) {
            args.getPlayerInfo().setPlayer(programArgs[curIndex+1]);
            return true;
        }
        if(programArgs[curIndex].equals("-r") || programArgs[curIndex].equals("--random")) {
            // -r is optional. check if next argument is a value or another arg
            if(curIndex + 1 < programArgs.length && !programArgs[curIndex+1].startsWith("-")) {
                try {
                    args.setRandomCount(
                        "all".equalsIgnoreCase(programArgs[curIndex+1])
                            ? RandomFilter.ALL
                            : Integer.parseInt(programArgs[curIndex+1])
                    );
                    return true;
                } catch (NumberFormatException e) {}
            }
            args.setRandomCount(1);
            return false;
        }
        if(programArgs[curIndex].equals("--config")) {
            args.setConfigPath(programArgs[curIndex+1]);
            return true;
        }
        if(programArgs[curIndex].equals("--source")) {
            args.addSourceFolder(programArgs[curIndex+1]);
            return true;
        }
        if(programArgs[curIndex].equals("--library")) {
            if(LibraryManager.hasLibrarySupport()) {
                args.setLibraryName(programArgs[curIndex+1]);
            }
            return true;
        }
        if(programArgs[curIndex].equals("--user") || programArgs[curIndex].equals("-u")) {
            args.setUser(programArgs[curIndex+1]);
            return true;
        }
        if(programArgs[curIndex].equals("--file") || programArgs[curIndex].equals("-f")) {
            args.setFile(programArgs[curIndex+1]);
            return true;
        }
        if(args.getShow() == null) {
            args.setShow(programArgs[curIndex]);
            args.setEpisode(programArgs[curIndex+1]);
            return true;
        }
        throw new InvalidArgumentException("Unexpected argument " + programArgs[curIndex], ExitCode.UNEXPECTED_ARGUMENT);
    }
    
    /**
     * Parse the given flag
     * @param args Arguments
     * @param flag Flag to parse
     * @return true if the flag was parsed, false if not
     */
    private static boolean parseFlag(Arguments args, String flag) {
        switch (flag) {
            case "--enqueue":
            case "-q":
                args.setMediaAction(Action.ENQUEUE);
                break;
            case "--count":
            case "-c":
                args.setMediaAction(Action.COUNT);
                break;
            case "--set":
            case "-s":
                args.setIsSetOnly(true);
                break;
            case "--ignore":
            case "-i":
                args.setIgnore(true);
                break;
            case "--list":
            case "-l":
                args.setMediaAction(Action.LIST);
                break;
            case "--list-path":
                args.setMediaAction(Action.LIST_PATH);
                break;
            case "--size":
                args.setMediaAction(Action.SIZE);
                break;
            case "--length":
                args.setMediaAction(Action.LENGTH);
                break;
            case "--seen":
                args.setMediaAction(Action.SEEN);
                break;
            case "--unseen":
                args.setMediaAction(Action.UNSEEN);
                break;
            case "--trakt":
                args.setTraktPointer(true);
                break;
            default:
                return false;
        }
        return true;
    }
    
    /**
     * Validates the given Arguments. Validation is successful if no exceptions 
     * were thrown.
     * @param arg parsed Arguments
     * @throws FileNotFoundException if the --config or --file option is set but
     * the given file(s) do not exist
     * @throws InvalidArgumentException if unable to parse the episode string or
     * library name
     */
    public static void validate(Arguments arg) throws FileNotFoundException, InvalidArgumentException {
        if(arg.isVersionSet()) {
            return;
        }
        if(arg.getConfigPath() != null && !new File(arg.getConfigPath()).exists()) {
            throw new FileNotFoundException("The config file given does not exist", ExitCode.CONFIG_FILE_NOT_FOUND);
        }
        if(arg.isFileSet()) {
            File f = arg.getFile();
            if(f == null || !f.exists()) {
                throw new FileNotFoundException("The input file does not exist", ExitCode.FILE_NOT_FOUND);
            }
            return;
        }
        if(arg.getLibraryName() != null && !LibraryManager.isValidLibraryName(arg.getLibraryName())) {
            throw new InvalidArgumentException("Windows Library name is invalid", ExitCode.LIBRARY_NOT_FOUND);
        }
        if(arg.isSetOnly() && arg.isIgnoreSet()) {
            throw new InvalidArgumentException("-s and -i flags cannot be set together", ExitCode.UNEXPECTED_ARGUMENT);
        }
//        List<EpisodeSelector> selectors = EpisodeSelector.defaultSelectors(null);
//        if(selectors.stream().noneMatch(s -> arg.getEpisodes().matches(s.getSelector()))) {
//            throw new InvalidArgumentException("Unable to parse the episodes given: " + arg.getEpisodes(), ExitCode.PARSE_EPISODES_FAILED);
//        }
    }
    
    /**
     * Gets the help message
     * @return help message
     */
    public static String getHelpMessage() {
        StringBuilder sb = new StringBuilder(4000);
        sb.append("Usage: tv TVSHOW EPISODES [ACTION] [-hvsi] [--source DIR]... [--library NAME]\n");
        sb.append("          [-r [NO]] [-p MP] [-u USER] [--trakt] [--config CONFIG]\n");
        sb.append("       tv -f FILE [ACTION] [-p MP] [--config CONFIG]\n");
        sb.append('\n');
        sb.append("    -u, --user USER   To be used when EPISODES is either prev, cur, next.\n");
        sb.append("                      To set your episode pointer you have to play a single\n");
        sb.append("                      episode first.\n");
        sb.append("    -s, --set         Set the current episode. EPISODES can be prev, cur, next\n");
        sb.append("                      or single episode format.\n");
        sb.append("    -i, --ignore      Do not remember the episode. EPISODES can be prev, cur,\n");
        sb.append("                      next, pilot, latest or single episode format.\n");
        sb.append("    --trakt           Use trakt to determine the current episode pointer. For\n");
        sb.append("                      use with navigable EPISODES e.g. \"next\" or remaining\n");
        sb.append("                      episodes in a season e.g. \"next-\". Trakt must be\n");
        sb.append("                      enabled via config.\n");
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
        sb.append("    --seen            Mark the EPISODES/FILE as seen on Trakt. Trakt must be\n");
        sb.append("                      enabled via config.\n");
        sb.append("    --unseen          Mark the EPISODES/FILE as unseen on Trakt. Trakt must\n");
        sb.append("                      be enabled via config.\n");
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
        sb.append("    s$                  Latest season\n");
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
