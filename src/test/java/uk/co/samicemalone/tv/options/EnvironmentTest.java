/*
 * Copyright (c) 2014, Sam Malone. All rights reserved.
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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.co.samicemalone.tv.FileSystemEnvironment;
import uk.co.samicemalone.tv.MockFileSystem;
import uk.co.samicemalone.tv.exception.ExitException;
import uk.co.samicemalone.tv.io.ConfigParser;
import uk.co.samicemalone.tv.io.LibraryManager;
import uk.co.samicemalone.tv.model.Arguments;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 *
 * @author Sam Malone
 */
public class EnvironmentTest extends FileSystemEnvironment {
    
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    
    private File configFile;
    private Environment env;
    
    public EnvironmentTest() {
    }
    
    @Before
    public void setUp() throws IOException {
        configFile = folder.newFile("tv.conf");
        env = LibraryManager.isWindows() ? new WindowsEnvironment() : new UnixEnvironment();
    }

    /**
     * Test of validate method, of class Environment.
     */
    @Test
    public void testValidateSourcesArgs() throws Exception {
        Arguments args = ArgsParser.parse(arg(
            "Scrubs", "s01", "--source", MockFileSystem.getSourceFolders().get(0),
            "--source", "InvalidSource"
        ));
        env.setArguments(args);
        env.validate();
        assertEquals(args.getSourceFolders(), MockFileSystem.getSourceFolders());
    }

    /**
     * Test of validate method, of class Environment.
     */
    @Test
    public void testValidateEnvironmentArgsPrecedence() throws Exception {
        writeConfig("LIBRARY_PATH=Videos\nPLAYER=vlc");
        Arguments args = ArgsParser.parse(arg(
            "Scrubs", "s01", "--library", "TV.library-ms", "-p", "stdout"
        ));
        env.setArguments(args);
        env.fromConfig(ConfigParser.parse(configFile));
        assertEquals(env.getArguments().getLibraryPath(), "TV.library-ms");
        assertEquals(env.getArguments().getPlayerInfo().getPlayer(), "stdout");
    }

    /**
     * Test of validate method, of class Environment.
     */
    @Test
    public void testValidateEnvironmentConfig() throws Exception {
        writeConfig("SOURCE=" + MockFileSystem.getSourceFolders().get(0));
        Arguments args = ArgsParser.parse(arg("Scrubs", "s01"));
        env.setArguments(args);
        env.fromConfig(ConfigParser.parse(configFile));
        env.validate();
        assertEquals(args.getSourceFolders(), MockFileSystem.getSourceFolders());
    }
    

    /**
     * Test of validate method, of class Environment.
     */
    @Test
    public void testValidateInvalidEnvironmentConfig() throws Exception {
        writeConfig("SOURCE=/path/doesnt/exist");
        Arguments args = ArgsParser.parse(arg("Scrubs", "s01"));
        env.setArguments(args);
        env.fromConfig(ConfigParser.parse(configFile));
        try {
            env.validate();
            fail();
        } catch (ExitException ex) {}
    }
    
    /**
     * Test of validate method, of class Environment.
     */
    @Test
    public void testValidateInvalidEnvironmentArgs() throws Exception {
        String[][] argsArray = new String[][] {
            arg("Scrubs", "s01"),
            arg("Scrubs", "s01", "--source", "InvalidSource"),
            arg("ShowDoesntExist", "s01", "--source", MockFileSystem.getSourceFolders().get(0))
        };
        for(String[] args : argsArray) {
            Arguments a = ArgsParser.parse(args);
            try {
                env.setArguments(a);
                env.validate();
                fail();
            } catch (ExitException ex) {}
        }
    }

    private void writeConfig(String config) throws IOException {
        FileWriter w = new FileWriter(configFile);
        w.write(config);
        w.flush();
    }
    
}
