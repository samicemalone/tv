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

package tv.filter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import tv.TV;
import uk.co.samicemalone.libtv.VideoFilter;

/**
 *
 * @author Sam Malone
 */
public class RandomFilter {
    
    /**
     * Random count representing all files should be randomised and returned
     */
    public static final int ALL = Integer.MAX_VALUE;
    
    /**
     * Randomises the File list given. If any File in list is a directory,
     * the contents of each directory will be added to the shuffled list.
     * No directories will be present in the shuffled list - only regular files.
     * The number of files returned is determined from the -r argument. If the
     * random count is larger than the number of files, all the media files will
     * be returned randomised. If the random count is less than 1, it will be 
     * clamped to 1. Otherwise the size of list returned will be equal to RANDOM_COUNT
     * @param list List of files and/or directories to be shuffled
     * @return Shuffled file list or empty array
     */
    public static File[] filter(File[] list) {
        if(list.length == 0) {
            return new File[] {};
        }
        List<File> tmp = listFiles(list);
        for(int i = 0; i < 5; i++) {
            Collections.shuffle(tmp, new Random(System.currentTimeMillis()));
        }
        int randomCount = Math.max(1, TV.ENV.getArguments().getRandomCount());
        if(randomCount < tmp.size()) {
            return tmp.subList(0, randomCount).toArray(new File[0]);
        }
        return tmp.toArray(new File[0]);
    }
    
    /**
     * Lists all the files from the mixed list of files and directories recursively
     * @param mixedList List of files and/or directories
     * @return List of files or empty list
     */
    private static List<File> listFiles(File[] mixedList) {
        List<File> fileList = new ArrayList<>();
        for(File file : mixedList) {
            if(file.isDirectory()) {
                fileList.addAll(Arrays.asList(file.listFiles(new VideoFilter())));
            } else {
                fileList.add(file);
            }
        }
        return fileList;
    }
    
}
