/*
 * Copyright (c) 2013, Sam Malone
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package uk.co.samicemalone.tv.options;

import uk.co.samicemalone.tv.exception.ExitException;
import uk.co.samicemalone.tv.io.LibraryManager;
import uk.co.samicemalone.tv.model.Config;

import java.io.File;

/**
 *
 * @author Sam Malone
 */
public class WindowsEnvironment extends Environment {

    /**
     * Check if the current operating system is running Windows
     * @return  true if OS is windows, false otherwise
     */
    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().startsWith("windows");
    }
    
    public WindowsEnvironment() {
        
    }

    @Override
    public String getDefaultTVDB() {
        return System.getenv("USERPROFILE") + "\\tv\\tvdb.sqlite";
    }

    @Override
    public File getDefaultMediaInfoBinary() {
        return new File("C:\\Program Files\\MediaInfo\\MediaInfo.exe");
    }

    @Override
    public File getDefaultConfig() {
        return new File(System.getenv("USERPROFILE") + "\\tv\\tv.conf");
    }

    @Override
    public File getDefaultTraktAuthFile() {
        return new File(System.getenv("USERPROFILE") + "\\tv\\trakt.auth");
    }

    @Override
    public void fromConfig(Config config) {
        if(getArguments().getLibraryPath() == null && config.getLibraryPath() != null) {
            getArguments().getSourceFolders().addAll(LibraryManager.parseLibraryFolders(config.getLibraryPath()));
        }
        super.fromConfig(config);
    }

    @Override
    public void validate() throws ExitException {
        if(getArguments().getLibraryPath() != null) {
            getArguments().getSourceFolders().addAll(LibraryManager.parseLibraryFolders(getArguments().getLibraryPath()));
        }
        super.validate();
    }
    
}
