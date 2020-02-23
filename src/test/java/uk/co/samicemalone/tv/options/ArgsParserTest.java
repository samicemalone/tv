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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.co.samicemalone.tv.MockFileSystem;
import uk.co.samicemalone.tv.action.Action;
import uk.co.samicemalone.tv.action.MediaPlayerAction;
import uk.co.samicemalone.tv.exception.FileNotFoundException;
import uk.co.samicemalone.tv.exception.InvalidArgumentException;
import uk.co.samicemalone.tv.exception.MissingArgumentException;
import uk.co.samicemalone.tv.filter.RandomFilter;
import uk.co.samicemalone.tv.io.LibraryManager;
import uk.co.samicemalone.tv.model.Arguments;
import uk.co.samicemalone.tv.model.PlayerInfo;
import uk.co.samicemalone.tv.plugin.PointerPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 *
 * @author Sam Malone
 */
public class ArgsParserTest {
    
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    
    private File configFile;
    
    public ArgsParserTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws IOException {
        configFile = folder.newFile("tv.conf");
    }
    
    @After
    public void tearDown() {
        
    }

    /**
     * Test of parse method, of class ArgsParser.
     */
    @Test
    public void testParseSingleAction() throws Exception {
        assertTrue(ArgsParser.parse(arg("-h")).isHelpSet());
        assertTrue(ArgsParser.parse(arg("-v")).isVersionSet());
    }
    
    /**
     * Test of parse method, of class ArgsParser.
     */
    @Test
    public void testParseInvalidArgs() throws Exception {
        String[][] argsArray = new String[][] {
            arg("Scrubs", "s01", "--fail"),
            arg("Scrubs", "s01", "-r", "NaN"),
            arg("Modern", "Family", "s01")
        };
        for(String[] args : argsArray) {
            try {
                ArgsParser.parse(args);
                fail();
            } catch (InvalidArgumentException ex) {}
        }
        argsArray = new String[][] {
            arg(""),
            arg("Scrubs", "s01", "--library"),
            arg("Scrubs", "s01", "-u"),
            arg("Scrubs"),
            arg("-l", "Scrubs"),
            arg("-f")
        };
        for(String[] args : argsArray) {
            try {
                ArgsParser.parse(args);
                fail();
            } catch (MissingArgumentException ex) {}
        }
    }
    
    /**
     * Test of parse method, of class ArgsParser.
     */
    @Test
    public void testParseEpisodes() throws Exception {
        // contains all possible arguments that will be parsed, but won't validate
        Arguments args = ArgsParser.parse(arg(
            "Scrubs", "s01", "-l", "--source", MockFileSystem.getSourceFolders().get(0),
            "--library", "TV.library-ms", "-p", "stdout", "--config", configFile.getAbsolutePath(),
            "--trakt", "-r", "2", "-u", "testuser", "-i", "-s"
        ));
        assertEpisodeTestDataValid(args);
    }
    
    private void assertEpisodeTestDataValid(Arguments args) throws AssertionError {
        assertEquals(args.getShow(), "Scrubs");
        assertEquals(args.getEpisodes(), "s01");
        assertEquals(args.getMediaAction(), Action.LIST);
        assertEquals(MockFileSystem.getSourceFolders(), args.getSourceFolders());
        assertEquals(args.getLibraryPath(), "TV.library-ms");
        assertEquals(args.getPlayerInfo(), new PlayerInfo("stdout"));
        assertEquals(args.getConfigPath(), configFile.getAbsolutePath());
        assertEquals(args.getRandomCount(), 2);
        assertEquals(args.getUser(), "testuser");
        assertTrue(args.isTraktPointerSet());
        assertTrue(args.isIgnoreSet());
        assertTrue(args.isSetOnly());
    }
    
    /**
     * Test of parse method, of class ArgsParser.
     */
    @Test
    public void testParseEpisodesArgOrder() throws Exception {
        String[] tvShowEpisodes = arg("Scrubs", "s01");
        String[][] argsOptions = new String[][] {
            arg("-l"), arg("--source", MockFileSystem.getSourceFolders().get(0)),
            arg("--library", "TV.library-ms"), arg("-p", "stdout"), arg("--config", configFile.getAbsolutePath()),
            arg("--trakt"), arg("-r", "2"), arg("-u", "testuser"), arg("-i"), arg("-s")
        };
        List<String> curArgs = new ArrayList<>();
        for(int i = 0; i < argsOptions.length; i++) {
            for(int j = 0; j < argsOptions.length; j++) {
                if(i == j) {
                    curArgs.add(tvShowEpisodes[0]);
                    curArgs.add(tvShowEpisodes[1]);
                }
                curArgs.addAll(Arrays.asList(argsOptions[j]));
            }
            assertEpisodeTestDataValid(ArgsParser.parse(curArgs.toArray(new String[] {})));
            curArgs.clear();
        }
        for(String[] argArray : argsOptions) {
            curArgs.addAll(Arrays.asList(argArray));
        }
        curArgs.add(tvShowEpisodes[0]);
        curArgs.add(tvShowEpisodes[1]);
        assertEpisodeTestDataValid(ArgsParser.parse(curArgs.toArray(new String[] {})));
    }
    
    /**
     * Test of parse method, of class ArgsParser.
     */
    @Test
    public void testParseRandomCount() throws Exception {
        assertEquals(0, ArgsParser.parse(arg("Scrubs", "s$")).getRandomCount());
        assertEquals(1, ArgsParser.parse(arg("Scrubs", "s$", "-r")).getRandomCount());
        assertEquals(5, ArgsParser.parse(arg("Scrubs", "s$", "-r", "5")).getRandomCount());
        assertEquals(1, ArgsParser.parse(arg("Scrubs", "s$", "-r", "-l")).getRandomCount());
        assertEquals(1, ArgsParser.parse(arg("Scrubs", "s$", "-r", "1", "-q")).getRandomCount());
        assertEquals(RandomFilter.ALL, ArgsParser.parse(arg("Scrubs", "s$", "-r", "all")).getRandomCount());
    }

    /**
     * Test of parse method, of class ArgsParser.
     */
    @Test
    public void testParseAction() throws Exception {
        assertEquals(ArgsParser.parse(arg("Scrubs", "s$")).getMediaAction(), Action.PLAY);
        assertEquals(ArgsParser.parse(arg("Scrubs", "s$", "-q")).getMediaAction(), Action.ENQUEUE);
        assertEquals(ArgsParser.parse(arg("Scrubs", "s$", "-l")).getMediaAction(), Action.LIST);
        assertEquals(ArgsParser.parse(arg("Scrubs", "s$", "--list-path")).getMediaAction(), Action.LIST_PATH);
        assertEquals(ArgsParser.parse(arg("Scrubs", "s$", "-c")).getMediaAction(), Action.COUNT);
        assertEquals(ArgsParser.parse(arg("Scrubs", "s$", "--size")).getMediaAction(), Action.SIZE);
        assertEquals(ArgsParser.parse(arg("Scrubs", "s$", "--length")).getMediaAction(), Action.LENGTH);
    }
    
    /**
     * Test of parse method, of class ArgsParser.
     */
    @Test
    public void testParseFile() throws Exception {
        File f = folder.newFile("test.avi");
        Arguments args = ArgsParser.parse(arg(
            "-f", f.getAbsolutePath(), "-q", "-p", "stdout",
            "--config", configFile.getAbsolutePath()
        ));
        assertTrue(args.isFileSet());
        assertEquals(args.getMediaAction(), Action.ENQUEUE);
        assertEquals(args.getPlayerInfo(), new PlayerInfo("stdout"));
        assertEquals(args.getConfigPath(), configFile.getAbsolutePath());
    }
    
    private String[] arg(String... args) {
        return args;
    }

    /**
     * Test of validate method, of class ArgsParser.
     */
    @Test
    public void testValidate() throws Exception {
        File f = folder.newFile("test.avi");
        Arguments args = ArgsParser.parse(arg(
            "Scrubs", "s01", "-l", "--library", "C:\\Users\\Public\\Libraries\\RecordedTV.library-ms", "-p", "stdout",
            "--config", configFile.getAbsolutePath()
        ));
        ArgsParser.validate(args);
        args = ArgsParser.parse(arg(
            "-f", f.getAbsolutePath(), "-l", "-p", "stdout",
            "--config", configFile.getAbsolutePath()
        ));
        ArgsParser.validate(args);
        ArgsParser.validate(ArgsParser.parse(arg("-v")));
        // non existing shows should still be valid because a config is not yet
        // applied. shows will be validated by the environment instead
        ArgsParser.validate(ArgsParser.parse(arg("NonExistentShow", "s01")));
    }
    
    /**
     * Test of validate method, of class ArgsParser.
     */
    @Test
    public void testValidateInvalidArgs() throws Exception {
        Arguments args = ArgsParser.parse(arg(
            "Scrubs", "s01", "-l", "--library", "DoesntExist", "-p", "stdout",
            "--config", configFile.getAbsolutePath()
        ));
        String[][] argsArray = new String[][] {
            arg(),
            arg("Scrubs", "s01", "--fake-argument"),
            arg("Scrubs", "pilot", "-i", "-s")
        };
        try {
            ArgsParser.validate(args);
            fail();
        } catch (InvalidArgumentException ex) {}
        for(String[] arg : argsArray) {
            assertArgumentsInvalid(arg);
        }
        argsArray = new String[][] {
            arg("Scrubs", "s01", "-l", "--config", "invalidConfigPath"),
            arg("-f", "invalidFilePath")
        };
        for(String[] arg : argsArray) {
            args = ArgsParser.parse(arg);
            try {
                ArgsParser.validate(args);
                fail();
            } catch (FileNotFoundException ex) {}
        }
    }
    
    private void assertArgumentsInvalid(String[] args) throws AssertionError {
        try {
            ArgsParser.validate(ArgsParser.parse(args));
            fail("Expected validation to fail");
        } catch (InvalidArgumentException | MissingArgumentException ex) {
            // Success
        } catch (FileNotFoundException ex) {
            fail("Expected InvalidArgumentException");
        }
    }
    
}
