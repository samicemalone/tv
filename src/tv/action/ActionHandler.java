/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tv.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import tv.CommandUtil;
import tv.MediaInfo;
import tv.MediaUtil;
import tv.filter.ExtensionFilter;
import tv.player.MediaPlayer;

/**
 *
 * @author Ice
 */
public class ActionHandler {
    
    private static int EP_COUNT = 0;
    private static long SIZE_COUNT = 0;
    private static boolean isListPath = false;
    private static ExtensionFilter filter = new ExtensionFilter();
    private static MediaInfo mediaInfo = new MediaInfo();
    
    public static int RANDOM_COUNT = 1;
    public static MediaPlayer MEDIA_PLAYER;
    
    /**
     * Performs the given action on the list of media files/directories given.
     * @param list List of media files/directories
     * @param action Action to be performed
     */
    public static void performAction(File[] list, int action) {
        if(list == null || list.length == 0) {
            return;
        }
        if((action & Action.RANDOM) != 0) {
            performAction(random(list), action ^ Action.RANDOM);
            return;
        }
        if((action & Action.LISTPATH) != 0) {
            action ^= Action.LISTPATH;
            isListPath = true;
        }
        switch (action) {
            case Action.PLAY:
                MEDIA_PLAYER.play(list);
                break;
            case Action.ENQUEUE:
                MEDIA_PLAYER.enqueue(list);
                break;
            case Action.LIST:
                list(list);
                return;
            case Action.COUNT:
                EP_COUNT = 0;
                count(list);
                System.out.println(EP_COUNT);
                return;
            case Action.SIZE:
                SIZE_COUNT = 0;
                size(list);
                System.out.println(MediaUtil.readableFileSize(SIZE_COUNT));
                return;
            case Action.LENGTH:
                System.out.println(MediaUtil.readableLength(length(list)));
        }
    }
    
    /**
     * Wrapper method for performAction(File[] list, int action)
     * @param list List of media files/directories
     * @param action Action to be performed
     */
    public static void performAction(File list, int action) {
        performAction(new File[] { list }, action);
    }
    
    /**
     * Calculates the amount of media files in the given list of
     * files/directories. The result is stored in EP_COUNT. EP_COUNT is not
     * reset when used.
     * @param list 
     */
    private static void count(File[] list) {
        for(int i = 0; i < list.length; i++) {
            if(list[i].isDirectory()) {
                count(list[i].listFiles(filter));
            } else {
                EP_COUNT++;
            }
        }
    }
    
    /**
     * Prints the list of media files from the given list of files/directories
     * to stdout.
     * @param list List of files/directories
     */
    private static void list(File[] list) {
        for(int i = 0; i < list.length; i++) {
            if(list[i].isDirectory()) {
                list(list[i].listFiles(filter));
            } else {
                if(isListPath) {
                    System.out.println(CommandUtil.getCanonicalPath(list[i]));
                } else {
                    System.out.println(list[i].getName());
                }
            }
        }
    }
    
    /**
     * Calculates the size of the media from the files/directories given in list
     * and stores the result in SIZE_COUNT. SIZE_COUNT does not reset when using
     * this method.
     * @param list List of files/directories
     */
    private static void size(File[] list) {
        for(int i = 0; i < list.length; i++) {
            if(list[i].isDirectory()) {
                size(list[i].listFiles(filter));
            } else {
                SIZE_COUNT += list[i].length();
            }
        }
    }
    
    /**
     * Get the length of the media from the files/directories given in list.
     * @param list List of files/directories
     * @return Length in seconds. 0 if list is empty.
     */
    private static long length(File[] list) {
        prepareLength(list);
        return mediaInfo.getLength();
    }
    
    private static void prepareLength(File[] list) {
        for(int i = 0; i < list.length; i++) {
            if(list[i].isDirectory()) {
                prepareLength(list[i].listFiles(filter));
            } else {
                mediaInfo.addFile(list[i].getAbsolutePath());
            }
        }
    }
    
    /**
     * Randomizes the File list given. If any File in list is a directory,
     * the contents of each directory will be added to the shuffled list.
     * No directories will be present in the shuffled list. Only regular files.
     * The amount of files returned is based on RANDOM_COUNT. If RANDOM_COUNT
     * is larger than the amount of media files, all the media files will be 
     * returned randomized. If list is empty, an empty array will be returned.
     * If RANDOM_COUNT is less than 1, it will be clamped to 1. Otherwise
     * the size of list returned will be equal to RANDOM_COUNT
     * @param list List of files and/or directories to be shuffled
     * @return Shuffled file list
     */
    private static File[] random(File[] list) {
        if(list.length == 0) {
            return new File[] {};
        }
        List<File> tmp = new ArrayList<File>();
        for(int i = 0; i < list.length; i++) {
            if(list[i].isDirectory()) {
                tmp.addAll(Arrays.asList(list[i].listFiles(filter)));
            } else {
                tmp.add(list[i]);
            }
        }
        for(int i = 0; i < 5; i++) {
            Collections.shuffle(tmp, new Random(System.currentTimeMillis()));
        }
        RANDOM_COUNT = Math.max(1, RANDOM_COUNT);
        if(RANDOM_COUNT < tmp.size()) {
            return tmp.subList(0, RANDOM_COUNT).toArray(new File[0]);
        } else {
            return tmp.toArray(new File[0]);
        }
    }
    
}
