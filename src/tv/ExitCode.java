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
package tv;

/**
 *
 * @author Sam Malone
 */
public class ExitCode {
    public final static int SUCCESS = 0;
    public final static int SHOW_INPUT_REQUIRED = 1; // no show was given
    public final static int SHOW_NOT_FOUND = 2;  // show input doesnt exist
    public final static int PARSE_EPISODES_FAILED = 3; // episode syntax invalid
    public final static int SEASON_NOT_FOUND = 4;  // given season was not found
    public final static int EPISODES_NOT_FOUND = 5; // syntax valid, but not found
    public final static int EPISODES_RANGE_NOT_FOUND = 6; // syntax valid, but not found
    public final static int MISSING_USERNAME = 7;  // username is required with -u flag
    public final static int NO_STORED_EPISODE_DATA = 8;  // episode pointer not set
    public final static int EPISODE_POINTER_INVALID = 9;  // episode pointer offset invalid
    public final static int MISSING_FILE = 10;  // file is required with -f flag
    public final static int FILE_NOT_FOUND = 11;  // file not found (using -f flag)
    public final static int EPISODE_INPUT_REQUIRED = 12;  // episode input doesnt exist
    public final static int MISSING_SOURCE = 13;  // source arg not given
    public final static int MISSING_LIBRARY = 14;  // library arg not given
    public final static int LIBRARY_NOT_FOUND = 15;  // library not found
    public final static int SOURCE_DIR_NOT_FOUND = 16;  // source dir not found
    public final static int SEASON_RANGE_NOT_FOUND = 17; // season range not found
    public final static int MISSING_CONFIG = 18; // config is required with --config flag
    public final static int CONFIG_FILE_NOT_FOUND = 19; // config file not found (with --config)
    public final static int CONFIG_PARSE_ERROR = 20; // config file could not be parsed
    public final static int MISSING_ARGUMENT = 21; // argument is required with an option
    public final static int UNEXPECTED_ARGUMENT = 22; // unexpected argument given
}
