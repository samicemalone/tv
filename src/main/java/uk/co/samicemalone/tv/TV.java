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
package uk.co.samicemalone.tv;

import java.io.IOException;
import java.util.List;
import uk.co.samicemalone.libtv.matcher.path.StandardTVLibrary;
import uk.co.samicemalone.libtv.matcher.path.TVPath;
import uk.co.samicemalone.libtv.model.EpisodeMatch;
import uk.co.samicemalone.tv.action.Action;
import uk.co.samicemalone.tv.exception.ExitException;
import uk.co.samicemalone.tv.exception.TraktUnauthorizedException;
import uk.co.samicemalone.tv.filter.RandomFilter;
import uk.co.samicemalone.tv.io.ConfigParser;
import uk.co.samicemalone.tv.io.LibraryManager;
import uk.co.samicemalone.tv.mode.EpisodeMode;
import uk.co.samicemalone.tv.mode.EpisodeModeFactory;
import uk.co.samicemalone.tv.mode.EpisodeModes;
import uk.co.samicemalone.tv.model.Arguments;
import uk.co.samicemalone.tv.model.Config;
import uk.co.samicemalone.tv.model.Episode;
import uk.co.samicemalone.tv.options.ArgsParser;
import uk.co.samicemalone.tv.options.Environment;
import uk.co.samicemalone.tv.options.UnixEnvironment;
import uk.co.samicemalone.tv.options.WindowsEnvironment;
import uk.co.samicemalone.tv.server.TVServer;
import uk.co.samicemalone.tv.trakt.TraktClient;

/**
 * Entry point to the application.
 * The command line arguments can take various forms/invocations.
 * The default invocation is when TVSHOW EPISODES arguments are given.
 * The file invocation is used when the --file argument is given.
 * Other invocations can be used such as --daemon, --kill, --help etc...
 * @author Sam Malone
 */
public class TV {
      
    public static Environment ENV;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ENV = LibraryManager.isWindows() ? new WindowsEnvironment() : new UnixEnvironment();
        try {
            ENV.setArguments(ArgsParser.parse(args));
            if(ENV.getArguments() == null) {
                System.out.println(ArgsParser.getHelpMessage());
                System.exit(ExitCode.SUCCESS);
            }
            ArgsParser.validate(ENV.getArguments());
            Config config = ConfigParser.parse(ENV.getConfig());
            ENV.fromConfig(config);
            ENV.validate();
        } catch (ExitException ex) {
            System.err.println(ex.getMessage());
            System.exit(ex.getExitCode());
        }
        if(ENV.getArguments().isVersionSet()) {
            System.out.println(Version.VERSION);
            return;
        }
        if(ENV.getArguments().isServerSet()) {
            new TVServer().start();
            return;
        }
        if(ENV.getArguments().isShutDownSet()) {
            new TVServer().shutdown();
            return;
        }
        if(ENV.getArguments().isFileSet()) {
            fileInvocation();
            return;
        }
        episodesInvocation();
    }
    
    /**
     * Run the TV program using the EPISODES invocation (the default)
     */
    private static void episodesInvocation() {
        Arguments args = ENV.getArguments();
        Action mediaAction = args.getMediaAction();
        int mode = EpisodeModes.getEpisodesMode(args.getEpisodes());
        try {
            if(TV.ENV.isTraktEnabled()) {
                TraktClient trakt = new TraktClient(TV.ENV.getTraktCredentials());
                trakt.processJournal();
            }
            TVPath tvPath = new StandardTVLibrary(args.getSourceFolders());
            EpisodeMode episodesMode = EpisodeModeFactory.getEpisodeMode(mode, tvPath);
            List<EpisodeMatch> matches = episodesMode.findMatchesOrThrow();
            Episode pointer = matches.size() == 1 ? episodesMode.getNewPointer(matches.get(0)) : null;
            mediaAction.execute(
                args.getRandomCount() == 0 ? matches : RandomFilter.filter(matches), pointer
            );
        } catch (IOException | TraktUnauthorizedException | ExitException e) {
            System.err.println(e.getMessage());
        }
    }
    
    /**
     * Run the TV program using the FILE invocation (using --file)
     */
    private static void fileInvocation() {
        try {
            ENV.getArguments().getMediaAction().execute(ENV.getArguments().getFile());
        } catch(ExitException e) {
            System.err.println(e.getMessage());
        }
    }
    
}
