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
package uk.co.samicemalone.tv.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import uk.co.samicemalone.tv.ExitCode;
import uk.co.samicemalone.tv.exception.ParseException;
import uk.co.samicemalone.tv.model.Config;

/**
 *
 * @author Sam Malone
 */
public class ConfigParser {
    
    /**
     * Parses the configFile and returns a Config object
     * @param configFile Config File
     * @return Config
     * @throws ParseException if unable to parse the config file
     */
    public static Config parse(File configFile) throws ParseException {
        Config c = new Config();
        try (BufferedReader br = Files.newBufferedReader(configFile.toPath(), StandardCharsets.UTF_8)) {
            String line;
            while((line = br.readLine()) != null) {
                if(line.isEmpty() || line.trim().charAt(0) == '#') {
                    continue;
                }
                parseLine(c, line);
            }
        } catch(IOException ex) {
            
        }
        return c;
    }
    
    /**
     * Parses a line of the config file and adds the data to the given
     * Config object
     * @param c Config
     * @param line Line of Config File
     * @throws ParseException if unable to determine a key and value from the line
     */
    private static void parseLine(Config c, String line) throws ParseException {
        String key, value;
        try {
            int equalsIndex = line.indexOf('=');
            key = line.substring(0, equalsIndex).trim();
            value = line.substring(equalsIndex + 1).trim();
        } catch(IndexOutOfBoundsException ex) {
            throw new ParseException("Unable to parse the line " + line, ExitCode.CONFIG_PARSE_ERROR);
        }
        if(!value.isEmpty()) {
            parseConfig(c, key, value);
        }
    }
    
    /**
     * Parse the config with the given key and applies the value to the
     * appropriate action in the config.
     * @param c Config to be applied to
     * @param key key defining which action to apply
     * @param value value of config to set
     */
    private static void parseConfig(Config c, String key, String value) {
        switch(key) {
            case "TVDB_FILE":
                c.setTVDBFile(value);
                break;
            case "SOURCE":
                c.addSourceFolder(value);
                break;
            case "MEDIAINFO_BINARY":
                c.setMediainfoBinary(value);
                break;
            case "LIBRARY_NAME":
                if(LibraryManager.hasLibrarySupport()) {
                    c.setLibraryName(value);
                }
                break;
            case "PLAYER":
                c.setPlayer(value);
                break;
            case "PLAYER_EXECUTABLE":
                c.setPlayerExecutable(value);
                break;
            case "PLAYER_ARGUMENTS":
                c.addPlayerArgument(value);
                break;
            case "FILES_FROM":
                c.addExtraFolder(value);
                break;
            case "ENABLE_TRAKT":
                c.setTraktEnabled(value);
                break;
            case "TRAKT_USERNAME":
                c.setTraktUsername(value);
                break;
            case "TRAKT_PASSWORD_SHA1":
                c.setTraktPasswordSha1(value);
                break;
            case "TRAKT_API_KEY":
                c.setTraktApiKey(value);
                break;
            case "TRAKT_USE_CHECKINS":
                c.setTraktUseCheckins(value);
                break;
        }
    }
    
}