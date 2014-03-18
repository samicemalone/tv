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

package tv.mode;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Sam Malone
 */
public class EpisodeModesTest {
    
    public EpisodeModesTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of episodesValid method, of class EpisodeModes.
     */
    @Test
    public void testEpisodesValid() {
        assertEquals(true, EpisodeModes.episodesValid("s01e02"));
        assertEquals(true, EpisodeModes.episodesValid("s02e12-s03e03"));
        assertEquals(true, EpisodeModes.episodesValid("s01e04-"));
        assertEquals(true, EpisodeModes.episodesValid("s01"));
        assertEquals(true, EpisodeModes.episodesValid("s02-s04"));
        assertEquals(true, EpisodeModes.episodesValid("s02-"));
        assertEquals(true, EpisodeModes.episodesValid("s$"));
        assertEquals(true, EpisodeModes.episodesValid("all"));
        assertEquals(true, EpisodeModes.episodesValid("pilot"));
        assertEquals(true, EpisodeModes.episodesValid("latest"));
        assertEquals(true, EpisodeModes.episodesValid("prev"));
        assertEquals(true, EpisodeModes.episodesValid("cur"));
        assertEquals(true, EpisodeModes.episodesValid("next"));
        assertEquals(true, EpisodeModes.episodesValid("prev-"));
        assertEquals(true, EpisodeModes.episodesValid("cur-"));
        assertEquals(true, EpisodeModes.episodesValid("next-"));
    }
    
    /**
     * Test of episodesValid method, of class EpisodeModes.
     */
    @Test
    public void testEpisodesInvalid() {
        assertEquals(false, EpisodeModes.episodesValid("1x02"));
        assertEquals(false, EpisodeModes.episodesValid("s01-s02e03"));
        assertEquals(false, EpisodeModes.episodesValid("s01e04-s02"));
    }
    
}
