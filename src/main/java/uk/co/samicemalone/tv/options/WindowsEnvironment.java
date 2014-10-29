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

import java.io.File;
import uk.co.samicemalone.tv.exception.ExitException;
import uk.co.samicemalone.tv.io.LibraryManager;
import uk.co.samicemalone.tv.model.Config;

/**
 *
 * @author Sam Malone
 */
public class WindowsEnvironment extends Environment {
    
    public WindowsEnvironment() {
        
    }

    @Override
    public File getDefaultTVDB() {
        return new File("C:\\ProgramData\\" + System.getProperty("user.name") + "\\tv\\tvdb.csv");
    }

    @Override
    public File getDefaultMediaInfoBinary() {
        return new File("C:\\Program Files\\MediaInfo\\MediaInfo.exe");
    }

    @Override
    public File getDefaultConfig() {
        return new File("C:\\ProgramData\\" + System.getProperty("user.name") + "\\tv\\tv.conf");
    }

    @Override
    public File getDefaultTraktDB() {
        return new File("C:\\ProgramData\\" + System.getProperty("user.name") + "\\tv\\traktdb.csv");
    }

    @Override
    public File getDefaultTraktDBJournal() {
        return new File("C:\\ProgramData\\" + System.getProperty("user.name") + "\\tv\\traktdb-journal.csv");
    }

    @Override
    public void fromConfig(Config config) {
        if(getArguments().getLibraryName() == null && config.getLibraryName() != null && LibraryManager.hasLibrarySupport()) {
            getArguments().getSourceFolders().addAll(LibraryManager.parseLibraryFolders(config.getLibraryName()));
        }
        super.fromConfig(config);
    }

    @Override
    public void validate() throws ExitException {
        if(getArguments().getLibraryName() != null && LibraryManager.hasLibrarySupport()) {
            getArguments().getSourceFolders().addAll(LibraryManager.parseLibraryFolders(getArguments().getLibraryName()));
        }
        super.validate();
    }
    
}
