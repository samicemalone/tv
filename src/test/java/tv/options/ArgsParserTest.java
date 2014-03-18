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

package tv.options;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.hamcrest.CoreMatchers.instanceOf;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import tv.MockFileSystem;
import tv.action.Action;
import tv.action.CountAction;
import tv.action.LengthAction;
import tv.action.ListAction;
import tv.action.MediaPlayerAction;
import tv.action.SizeAction;
import tv.exception.FileNotFoundException;
import tv.exception.InvalidArgumentException;
import tv.exception.MissingArgumentException;
import tv.filter.RandomFilter;
import tv.io.LibraryManager;
import tv.model.Arguments;
import tv.model.PlayerInfo;

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
     * @throws java.lang.Exception
     */
    @Test
    public void testParseSingleAction() throws Exception {
        assertNull(ArgsParser.parse(arg("-h")));
        assertTrue(ArgsParser.parse(arg("-k")).isShutDownSet());
        assertTrue(ArgsParser.parse(arg("-v")).isVersionSet());
    }
    
    /**
     * Test of parse method, of class ArgsParser.
     * @throws java.lang.Exception
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
     * @throws java.lang.Exception
     */
    @Test
    public void testParseEpisodes() throws Exception {
        // contains all possible arguments that will be parsed, but won't validate
        Arguments args = ArgsParser.parse(arg(
            "Scrubs", "s01", "-l", "--source", MockFileSystem.getSourceFolders().get(0),
            "--library", "TV", "-p", "stdout", "--config", configFile.getAbsolutePath(),
            "--trakt", "-r", "2", "-u", "testuser", "-i", "-s"
        ));
        assertEpisodeTestDataValid(args);
    }
    
    private void assertEpisodeTestDataValid(Arguments args) throws AssertionError {
        assertEquals(args.getShow(), "Scrubs");
        assertEquals(args.getEpisodes(), "s01");
        assertEquals(args.getMediaAction(), new ListAction());
        assertEquals(MockFileSystem.getSourceFolders(), args.getSourceFolders());
        assertEquals(args.getLibraryName(), LibraryManager.isWindows7() ? "TV" : null);
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
     * @throws java.lang.Exception
     */
    @Test
    public void testParseEpisodesArgOrder() throws Exception {
        String[] tvShowEpisodes = arg("Scrubs", "s01");
        String[][] argsOptions = new String[][] {
            arg("-l"), arg("--source", MockFileSystem.getSourceFolders().get(0)),
            arg("--library", "TV"), arg("-p", "stdout"), arg("--config", configFile.getAbsolutePath()),
            arg("--trakt"), arg("-r", "2"), arg("-u", "testuser"), arg("-i"), arg("-s")
        };
        List<String> curArgs = new ArrayList<String>();
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
            for(String arg : argArray) {
                curArgs.addAll(Arrays.asList(arg));
            }
        }
        curArgs.add(tvShowEpisodes[0]);
        curArgs.add(tvShowEpisodes[1]);
        assertEpisodeTestDataValid(ArgsParser.parse(curArgs.toArray(new String[] {})));
    }
    
    /**
     * Test of parse method, of class ArgsParser.
     * @throws java.lang.Exception
     */
    @Test
    public void testParseRandomCount() throws Exception {
        assertEquals(0, ArgsParser.parse(arg("Scrubs", "s$")).getRandomCount());
        assertEquals(1, ArgsParser.parse(arg("Scrubs", "s$", "-r")).getRandomCount());
        assertEquals(5, ArgsParser.parse(arg("Scrubs", "s$", "-r", "5")).getRandomCount());
        assertEquals(RandomFilter.ALL, ArgsParser.parse(arg("Scrubs", "s$", "-r", "all")).getRandomCount());
    }
    
    /**
     * Test of parse method, of class ArgsParser.
     * @throws java.lang.Exception
     */
    @Test
    public void testParseServer() throws Exception {
        File extraDir = folder.newFolder("extra");
        List<String> extraFolders = Arrays.asList(new String[] { extraDir.getAbsolutePath() });
        Arguments args = ArgsParser.parse(arg(
            "-d", "--files-from", extraDir.getAbsolutePath(),
            "--source", MockFileSystem.getSourceFolders().get(0), "--library",
            "TV", "-p", "stdout", "--config", configFile.getAbsolutePath()
        ));
        assertTrue(args.isServerSet());
        assertEquals(extraFolders, args.getExtraFolders());
        assertEquals(MockFileSystem.getSourceFolders(), args.getSourceFolders());
        assertEquals(args.getLibraryName(), "TV");
        assertEquals(args.getPlayerInfo(), new PlayerInfo("stdout"));
        assertEquals(args.getConfigPath(), configFile.getAbsolutePath());
    }
    
    /**
     * Test of parse method, of class ArgsParser.
     * @throws java.lang.Exception
     */
    @Test
    public void testParseAction() throws Exception {
        assertEquals(ArgsParser.parse(arg("Scrubs", "s$")).getMediaAction(), new MediaPlayerAction(Action.PLAY));
        assertEquals(ArgsParser.parse(arg("Scrubs", "s$", "-q")).getMediaAction(), new MediaPlayerAction(Action.ENQUEUE));
        assertEquals(ArgsParser.parse(arg("Scrubs", "s$", "-l")).getMediaAction(), new ListAction());
        assertEquals(ArgsParser.parse(arg("Scrubs", "s$", "--list-path")).getMediaAction(), new ListAction(true));
        assertThat(ArgsParser.parse(arg("Scrubs", "s$", "-c")).getMediaAction(), instanceOf(CountAction.class));
        assertThat(ArgsParser.parse(arg("Scrubs", "s$", "--size")).getMediaAction(), instanceOf(SizeAction.class));
        assertThat(ArgsParser.parse(arg("Scrubs", "s$", "--length")).getMediaAction(), instanceOf(LengthAction.class));
    }
    
    /**
     * Test of parse method, of class ArgsParser.
     * @throws java.lang.Exception
     */
    @Test
    public void testParseFile() throws Exception {
        File f = folder.newFile("test.avi");
        Arguments args = ArgsParser.parse(arg(
            "-f", f.getAbsolutePath(), "-q", "-p", "stdout",
            "--config", configFile.getAbsolutePath()
        ));
        assertTrue(args.isFileSet());
        assertEquals(args.getMediaAction(), new MediaPlayerAction(Action.ENQUEUE));
        assertEquals(args.getPlayerInfo(), new PlayerInfo("stdout"));
        assertEquals(args.getConfigPath(), configFile.getAbsolutePath());
    }
    
    private String[] arg(String... args) {
        return args;
    }

    /**
     * Test of validate method, of class ArgsParser.
     * @throws java.lang.Exception
     */
    @Test
    public void testValidate() throws Exception {
        File f = folder.newFile("test.avi");
        Arguments args = ArgsParser.parse(arg(
            "Scrubs", "s01", "-l", "--library", "Documents", "-p", "stdout",
            "--config", configFile.getAbsolutePath()
        ));
        ArgsParser.validate(args);
        args = ArgsParser.parse(arg(
            "-f", f.getAbsolutePath(), "-l", "-p", "stdout",
            "--config", configFile.getAbsolutePath()
        ));
        ArgsParser.validate(args);
        ArgsParser.validate(ArgsParser.parse(arg("-k")));
        ArgsParser.validate(ArgsParser.parse(arg("-v")));
        // non existing shows should still be valid because a config is not yet
        // applied. shows will be validated by the environment instead
        ArgsParser.validate(ArgsParser.parse(arg("NonExistentShow", "s01")));
    }
    
    /**
     * Test of validate method, of class ArgsParser.
     * @throws java.lang.Exception
     */
    @Test
    public void testValidateInvalidArgs() throws Exception {
        Arguments args = ArgsParser.parse(arg(
            "Scrubs", "s01", "-l", "--library", "DoesntExist", "-p", "stdout",
            "--config", configFile.getAbsolutePath()
        ));
        String[][] argsArray = new String[][] {
            arg("Scrubs", "-r", "-l"), arg("s01", "-u"),
            arg("Scrubs", "pilot", "-i", "-s")
        };
        try {
            ArgsParser.validate(args);
            if(LibraryManager.isWindows7()) {
                fail();
            }
        } catch (InvalidArgumentException ex) {}
        for(String[] arg : argsArray) {
            assertArgumentsInvalid(ArgsParser.parse(arg));
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
    
    private void assertArgumentsInvalid(Arguments args) throws AssertionError {
        try {
            ArgsParser.validate(args);
            fail();
        } catch (InvalidArgumentException ex) {
        } catch (FileNotFoundException ex) {
            fail();
        }
    }
    
}
