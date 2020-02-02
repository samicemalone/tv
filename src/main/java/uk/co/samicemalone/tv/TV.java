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

import com.j256.ormlite.logger.LocalLog;
import uk.co.samicemalone.tv.action.FileAction;
import uk.co.samicemalone.tv.exception.ExitException;
import uk.co.samicemalone.tv.io.ConfigParser;
import uk.co.samicemalone.tv.io.LibraryManager;
import uk.co.samicemalone.tv.model.Arguments;
import uk.co.samicemalone.tv.model.Config;
import uk.co.samicemalone.tv.options.ArgsParser;
import uk.co.samicemalone.tv.options.Environment;
import uk.co.samicemalone.tv.options.UnixEnvironment;
import uk.co.samicemalone.tv.options.WindowsEnvironment;
import uk.co.samicemalone.tv.tvdb.TVDatabase;
import uk.co.samicemalone.tv.tvdb.model.Show;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Entry point to the application.
 * The command line arguments can take various forms/invocations.
 * The default invocation is when TVSHOW EPISODES arguments are given.
 * The file invocation is used when the --file argument is given.
 * @author Sam Malone
 */
public class TV {
      
    public static Environment ENV;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "INFO");
        ENV = LibraryManager.isWindows() ? new WindowsEnvironment() : new UnixEnvironment();
        try {
            ENV.setArguments(ArgsParser.parse(args));
            if(ENV.getArguments().isHelpSet()) {
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
        } else if(ENV.getArguments().isFileSet()) {
            fileInvocation();
        } else {
            episodesInvocation();
        }
    }

    /**
     * Run the TV program using the EPISODES invocation (the default)
     */
    private static void episodesInvocation() {
        Arguments args = ENV.getArguments();
        Application app = new Application(new TVDatabase());

        try {
            app.run(args);
        } catch (SQLException | IOException e) {
            System.err.format("[db] [%s] %s\n", e.getClass(), e.getMessage());
        } catch (ExitException e) {
            System.err.format("[warning] [%s] %s\n", e.getClass(), e.getMessage());
        } catch (Exception e) {
            System.err.format("[error] [%s] %s\n", e.getClass(), e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Run the TV program using the FILE invocation (using --file)
     */
    private static void fileInvocation() {
        List<FileAction> actions = FileAction.defaultFileActions();
        Arguments args = ENV.getArguments();

        FileAction action = null;
        for(FileAction a : actions) {
            if(a.isAction(args.getMediaAction())) {
                action = a;
                break;
            }
        }
        try {
            if(action == null) {
                throw new ExitException("[action] invalid action type", ExitCode.UNEXPECTED_ARGUMENT);
            }
            action.execute(args.getFile());
        } catch(ExitException e) {
            System.err.println(e.getMessage());
        }
    }
    
}
