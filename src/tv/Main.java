/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tv;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import tv.action.Action;
import tv.action.ActionHandler;
import tv.exception.ExitException;
import tv.io.TVDBManager;
import tv.model.Arguments;
import tv.model.Episode;
import tv.model.Season;
import tv.player.MediaPlayerFactory;
import tv.server.VLCServer;

/**
 *
 * @author Ice
 */
public class Main {
    
    public static List<String> sourceFolders;
    
    public static Episode pointerEpisode;
    public static Arguments ARGS;
    public static int MEDIA_ACTION;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ARGS = null;
        try {
            ARGS = ArgsParser.parse(args);
            if(ARGS == null) {
                ArgsParser.printHelp();
                System.exit(ExitCode.SUCCESS);
            }
            sourceFolders = ARGS.getSourceFolders();
            ArgsParser.validate(ARGS);
        } catch (ExitException ex) {
            System.err.println(ex.getMessage());
            System.exit(ex.getExitCode());
        }
        if(ARGS.isServerSet()) {
            new VLCServer().start();
            return;
        }
        if(ARGS.isShutDownSet()) {
            new VLCServer().shutdown();
            return;
        }
        ActionHandler.MEDIA_PLAYER = MediaPlayerFactory.parsePlayer(ARGS.getPlayer());
        MEDIA_ACTION = ARGS.getMediaAction();
        if(ARGS.isFileSet()) {
            ActionHandler.performAction(ARGS.getFile(), MEDIA_ACTION);
            return;
        }
        int mode = TVScan.getEpisodesMode(ARGS.getEpisodes());
        pointerEpisode = null;
        Season season = getSeasonInfo(mode);
        if(season.getSeasonDir() == null || !season.getSeasonDir().exists()) {
            exitError(ExitCode.SEASON_NOT_FOUND, "Season could not be found");
        } 
        switch(mode) {
            case TVScan.SEASON:
                ActionHandler.performAction(season.getSeasonDir(), MEDIA_ACTION);
                break;
            case TVScan.EPSINGLE:
                singleEpisode(season);
                break;
            case TVScan.POINTER:
                episodePointer(season);
                break;
            case TVScan.SEASONFROMPOINTER:
                seasonFromEpisode(season, pointerEpisode.getEpisodeNo());
                break;
            case TVScan.SEASONFROMEP:
                seasonFromEpisode(season, TVScan.getEpisodeNo(ARGS.getEpisodes()));
                break;
            case TVScan.EPRANGE:
                episodeRange(season, ARGS.getEpisodes().split("-"));
                break;
            case TVScan.ALL:
                allEpisodes(season);
                break;
            case TVScan.SEASONRANGE:
                seasonRange(season, ARGS.getEpisodes().split("-"));
                break;
            case TVScan.ALLFROMSEASON:
                allFromSeason(season);
                break;
            case TVScan.PILOT:
                singleEpisode(season, "01");
                break;
            case TVScan.LATEST:
                singleEpisode(season, TVScan.getLastEpisodeNo(season));
                break;
        }
    }
    
    /**
     * Exits the program with the given exit code and prints the message to stderr
     * @param exitCode
     * @param message 
     */
    private static void exitError(int exitCode, String message) {
        System.err.println(message);
        System.exit(exitCode);
    }
    
    /**
     * Gets the starting season information for the given episode mode
     * @param mode Episode Mode
     * @return Season information for the given episode mode
     */
    private static Season getSeasonInfo(int mode) {
        switch(mode) {
            case TVScan.POINTER:
            case TVScan.SEASONFROMPOINTER:
                try {
                    TVDBManager io = new TVDBManager();
                    io.readStorage(ARGS.getUser());
                    if(io.containsEpisodeData(ARGS.getShow())) {
                        pointerEpisode = io.getEpisode(ARGS.getShow());
                        TVScan.getEpisode(pointerEpisode, ARGS.getEpisodes()); // modifies e
                        return new Season(ARGS.getShow(), pointerEpisode.getSeasonNo());
                    } else {
                        exitError(ExitCode.NO_STORED_EPISODE_DATA, "There is no episode data stored for this show");
                    }
                } catch (FileNotFoundException ex) {
                    exitError(ExitCode.NO_STORED_EPISODE_DATA, "There is no episode data stored for this show");
                }
                break;
            case TVScan.ALL:
            case TVScan.PILOT:
                return new Season(ARGS.getShow(), 1);
            case TVScan.LATEST:
                return new Season(ARGS.getShow(), TVScan.getLastSeasonNo(ARGS.getShow()));
        }
        return new Season(ARGS.getShow(), TVScan.getSeasonNo(ARGS.getEpisodes())); 
    }
    
    /**
     * Performs the action specified by MEDIA_ACTION on all episodes
     * @param season Starting season (usually 1)
     */
    public static void allEpisodes(Season season) {
        File[] eps = TVScan.getAllEpisodes(season, ARGS.getShow());
        if(eps == null || eps.length == 0) {
            exitError(ExitCode.EPISODES_NOT_FOUND, "Unable to find any episodes");
        }
        ActionHandler.performAction(eps, MEDIA_ACTION);
    }
    
    /**
     * Performs the action specified by MEDIA_ACTION on the given episode
     * @param season Season
     * @param episodeNo Episode No
     */
    public static void singleEpisode(Season season, String episodeNo) {
        File ep = TVScan.getEpisode(season, episodeNo);
        if(ep == null) {
            exitError(ExitCode.EPISODES_NOT_FOUND, "Unable to find the episode given");
        }
        if(MEDIA_ACTION == Action.PLAY || MEDIA_ACTION == Action.ENQUEUE) {
            if(!ARGS.isIgnoreSet()) {
                new TVDBManager().writeStorage(ARGS.getShow(), ARGS.getUser(), season.getSeasonString(), episodeNo);
            }
        }
        if(!ARGS.isSetOnly()) {
            ActionHandler.performAction(ep, MEDIA_ACTION);
        }
    }
    
    /**
     * Wrapper method for singleEpisode(Season, String) using the users episode string
     * to determine episode no
     * @param season Season
     */
    public static void singleEpisode(Season season) {
        singleEpisode(season, TVScan.getEpisodeNo(ARGS.getEpisodes()));
    }
    
    /**
     * Performs the action specified by MEDIA_ACTION on the given episode pointer
     * @param season Season
     */
    public static void episodePointer(Season season) {
        File epp = TVScan.getEpisode(season, pointerEpisode.getEpisodeNo());
        if(epp == null) {
            exitError(ExitCode.EPISODE_POINTER_INVALID, "Unable to find the episode offset given");
        }
        if(MEDIA_ACTION == Action.PLAY || MEDIA_ACTION == Action.ENQUEUE) {
            if(!ARGS.isIgnoreSet()) {
                new TVDBManager().writeStorage(pointerEpisode.getShow(), ARGS.getUser(), pointerEpisode.getSeasonNo(), pointerEpisode.getEpisodeNo());
            }
        }
        if(!ARGS.isSetOnly()) {
            ActionHandler.performAction(epp, MEDIA_ACTION);
        }
    }
    
    /**
     * Performs the action specified by MEDIA_ACTION on the given episode range
     * @param season Season
     * @param rangeArray rangeArray[0]: start episode, rangeArray[1]: end episode
     */
    public static void episodeRange(Season season, String[] rangeArray) {
        File[] eprange = TVScan.getEpisodeRange(season, ARGS.getShow(), rangeArray[0], rangeArray[1]);
        if(eprange == null || eprange.length == 0) {
            exitError(ExitCode.EPISODES_RANGE_NOT_FOUND, "Unable to find the episodes in the given range");
        }
        ActionHandler.performAction(eprange, MEDIA_ACTION);
    }
    
    /**
     * Performs the action specified by MEDIA_ACTION on the episodes including
     * and greater than epString in the same season
     * @param season Season
     * @param epString Episode No e.g. 02
     */
    public static void seasonFromEpisode(Season season, String epString) {
        File[] eps = TVScan.getEpisodesFrom(season, epString);
        if(eps == null || eps.length == 0) {
            exitError(ExitCode.EPISODES_RANGE_NOT_FOUND, "Unable to find the episodes in the given range");
        }
        ActionHandler.performAction(eps, MEDIA_ACTION);
    }

    /**
     * Performs the action specified by MEDIA_ACTION on the given season range
     * @param season Starting season
     * @param rangeArray rangeArray[0]: start season, rangeArray[1]: end season e.g. s03
     */
    public static void seasonRange(Season season, String[] rangeArray) {
        File[] range = TVScan.getSeasonRange(season, ARGS.getShow(), rangeArray[0], rangeArray[1]);
        if(range == null || range.length == 0) {
            exitError(ExitCode.SEASON_RANGE_NOT_FOUND, "Unable to find the seasons in the given range");
        }
        ActionHandler.performAction(range, MEDIA_ACTION);
    }

    /**
     * Performs the action specified by MEDIA_ACTION on the seasons, including and greater
     * than Season.
     * @param season Starting season
     */
    public static void allFromSeason(Season season) {
        File[] eps = TVScan.getSeasonsFrom(season, ARGS.getShow());
        if(eps == null || eps.length == 0) {
            exitError(ExitCode.SEASON_RANGE_NOT_FOUND, "Unable to find the episodes in the given range");
        }
        ActionHandler.performAction(eps, MEDIA_ACTION);
    }
}
