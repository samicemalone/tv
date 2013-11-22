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

package tv.options;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import tv.DirectoryExistsThread;
import tv.ExitCode;
import tv.TVScan;
import tv.exception.ExitException;
import tv.exception.FileNotFoundException;
import tv.exception.MissingArgumentException;
import tv.model.Arguments;
import tv.model.Config;

/**
 *
 * @author Sam Malone
 */
public abstract class Environment {
    
    private File tvdb;
    private File mediainfo;
    private Arguments args;
    
    /**
     * Get the default TVDB file
     * @return default TVDB file
     */
    public abstract File getDefaultTVDB();
    
    /**
     * Get the default MediaInfo binary file
     * @return default MediaInfo binary file
     */
    public abstract File getDefaultMediaInfoBinary();
    
    /**
     * Get the default config file
     * @return default config file
     */
    public abstract File getDefaultConfig();
    
    /**
     * Get the arguments for this Environment
     * @return arguments or null if none set
     */
    public Arguments getArguments() {
        return args;
    }
    
    /**
     * Set the arguments for this Environment
     * @param args parsed Arguments
     */
    public void setArguments(Arguments args) {
        this.args = args;
    }
    
    /**
     * Get the TVDB file
     * @return TVDB file if set or the default TVDB file otherwise
     */
    public final File getTVDB() {
        return tvdb == null ? getDefaultTVDB() : tvdb;
    }

    /**
     * Get the MediaInfo binary File
     * @return MediaInfo binary File if set or the default File otherwise
     */
    public final File getMediaInfoBinary() {
        return mediainfo == null ? getDefaultMediaInfoBinary() : mediainfo;
    }

    /**
     * Get the Config file.
     * @return Config file if set, otherwise the default Config file
     */
    public final File getConfig() {
        return args.getConfigPath() == null ? getDefaultConfig() : new File(args.getConfigPath());
    }
    
    /**
     * Apply the values from the Config file to this Environment
     * @param config Config
     */
    public void fromConfig(Config config) {
        if(config.getTVDBFile() != null) {
            tvdb = new File(config.getTVDBFile());
        }
        if(config.getMediainfoBinary() != null) {
            mediainfo = new File(config.getMediainfoBinary());
        }
        if(args.getPlayerInfo().getPlayer() == null && config.getPlayer() != null) {
            args.getPlayerInfo().setPlayer(config.getPlayer());
        }
        if(config.getPlayerExecutable() != null) {
            args.getPlayerInfo().setPlayerExecutable(config.getPlayerExecutable());
        }
        if(config.getPlayerArguments() != null && config.getPlayerArguments().length > 0) {
            args.getPlayerInfo().setPlayerArguments(config.getPlayerArguments());
        }
        if(config.getSourceFolders() != null) {
            args.getSourceFolders().addAll(config.getSourceFolders());
        }
    }
    
    /**
     * Validate the current Environment.
     * Arguments that were originally parsed and validated may have changed if
     * a Config has been applied to the Environment. Some arguments, such as the
     * show name, can only be validated once all source and library folders are 
     * known, and are done so here.
     * @throws MissingArgumentException if no source folders are specified
     * @throws FileNotFoundException if the directory for the given show cannot
     * be found.
     */
    public void validate() throws ExitException {
        if(args.isShutDownSet() || args.isVersionSet() || args.isFileSet()) {
            return;
        }
        List<String> existentSources = DirectoryExistsThread.getExistingDirs(args.getSourceFolders());
        args.getSourceFolders().retainAll(existentSources);
        if(args.getSourceFolders().isEmpty()) {
            throw new MissingArgumentException("The --source or --library input is required", ExitCode.SOURCE_DIR_NOT_FOUND);
        }
        if(args.isServerSet()) {
            return;
        }
        if(!TVScan.showExists(args.getShow())) {
            throw new FileNotFoundException("Unable to find show: " + args.getShow(), ExitCode.SHOW_NOT_FOUND);
        }
    }
    
}
