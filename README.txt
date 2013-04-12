NAME
   tv - keep track of where you are upto with TV shows and lets you play/queue
        ranges of episodes/seasons in a media player.

SYNOPSIS
   tv TVSHOW EPISODES [ACTION] [-hvsi] [--source DIR]... [--library NAME]
      [-r [NO]] [-p PLAYER] [-u USER] [--config CONFIG]
   tv -f FILE [ACTION] [-p PLAYER] [--config CONFIG]
   tv -d [--source DIR]... [--library NAME] [-p PLAYER] [--config CONFIG]
   tv -k 

DESCRIPTION
   "tv" is a utility that allows you keep track of the position that you are
   currently at with your TV shows. It supports multiple 'users' to allow
   concurrent use without overwriting the default users position.

   It can select a single episode, a range of episodes, the remaining episodes
   in a season, whole seasons, a range of seasons and all episodes to be played
   or enqueued in a media player.

   It also allows you to randomise the episode(s) selected, list the episode(s)
   selected, count the number of episodes selected and get a total size for the
   episode(s) selected.

   The default action is to play EPISODES immediately in the given media player.

OPTIONS
   The order of the options does not matter as long as TVSHOW EPISODES appear
   together. TVSHOW and EPISODES are required.

   TVSHOW
      The directory name of the TV show that exists in one the source
      directories or in the windows library if given.
   EPISODES
      Formats marked with an asterisk will modify the episode pointer:
      *  s01e02              Single episode
         s02e12-s03e03       Episode range
         s01e04-             Remaining episodes in the season from given episode
         s01                 Whole season
         s02-s04             Season range
         s02-                All seasons from the given season
         all                 Every episode
      *  pilot               Pilot episode. Alias for s01e01
      *  latest              Latest episode
      *  prev, cur, next     Episode based on pointer
         prev-, cur-, next-  Remaining episodes in the season from given pointer
   ACTION
      The default action is to play immediately. The following actions are also
      also available:
         -q, --enqueue
            Enqueues file(s) in the media player instead of playing immediately.
         -l, --list
            Lists the file name of the episode(s) matched by EPISODES or FILE.
         --list-path
            List the full file path(s) of the episode(s) matched by EPISODES or 
            FILE.
         -c, --count
            Counts the number of episodes from the EPISODES range given.
         --length
            This option requires the mediainfo program to be installed. It adds
            up the length of each episode matched in EPISODES or FILE and 
            outputs the total in the format hh:mm:ss
         --size
            Prints the total size of the episodes matched from the EPISODES
            string or FILE.
   PLAYER
      The following media players are currently supported:
         vlc (default)
         omxplayer

   --config CONFIG
      Sets CONFIG as the configuration file to use. For the default 
      configuration file see the FILES section.

   -d, --daemon
      This is an experimental feature. Starts in daemon mode and listens on
      port 5768 for commands.

   -f FILE, --file FILE
      Plays FILE from the filesystem. Can be used with -q to enqueue instead of
      play.

   -h, --help
      The help message will be output and the program will exit.

   -i, --ignore
      If you wish to use an EPISODES format that will modify the episode
      pointer, this flag will perform the given action but not save the pointer.
      E.g. When used with pilot, latest, next, s02e01 etc..., the pointer will
      stay the same.

   -k, --kill
      This is an experimental feature. When a tv daemon is already running,
      this flag will shut the daemon down.

   --library NAME
      This option only works on Windows 7. It adds each directory that library
      NAME consists of, as the source directories to look up TV shows. If using
      this option, then the --source argument doesn't need to be set although it
      can still be used in conjunction.

   -p PLAYER, --player PLAYER
      Sets the media player to use. The default is "vlc". The list of available
      media players is available under the OPTIONS section.

   -r [NO], --random [NO]
      Selects random episode(s) from the EPISODES range given. If NO is omitted,
      1 random episode will be selected. If NO is "all" then every episode in 
      the EPISODES range will be randomised.

   -s, --set
      Sets the current episode pointer only. This can be used when EPISODES is
      a format that modifies the pointer e.g. next, pilot, s01e03 etc...

   (--source DIR)...
      Sets DIR as a source folder that contains TV shows. This option can be
      used multiple times to add multiple source directories. There must be
      some source directory set, whether this by via an argument or set in a
      configuration file. If using Windows 7 and the --library option is used,
      then source doesn't need to be set, as the sources will be added from the
      library.

   -u USER, --user USER
      Sets the USER to avoid overwriting the default episode pointer so that 
      multiple users can share the same configuration and database. If omitted,
      the default user will be used (an empty string).

   -v, --version
      The program version will be printed and the program will exit.

DAEMON COMMANDS
   The listening daemon can accept the following commands:
      list_shows           Gets the list of shows. Double quoted, one show per
                           line.
      list_stored_eps      Gets the stored episodes (tvdb) in csv format. New
                           line if none. Fields terminated by , enclosed by "
      get_show_name FILE   Get show name of FILE. Assumes /showname/parent/FILE
      shutdown             Shuts down the daemon
      tv TVARGS            Run tv command with same usage as in the SYNOPSIS.
			
FILES
   The directory structure required for the episodes to be found is as follows:
         SOURCE/TVSHOW/Season x/
      OR SOURCE/TVSHOW/Series x/

   The following files are used by tv:
      tvdb.csv
         This file stores the pointers for the TV shows. It is created as needed
         by the application.
      tv.conf
         This is the default file name of the configuration file. This file does
         not exist by default. See sample.tv.conf for usage.
      sample.tv.conf
         This is a sample tv configuration file. This should be renamed/copied
         to tv.conf and edited to match your specifications. This file contains
         comments describing each variable. You can specify the path to the
         config file with the --config command.

   Default Directories
      Windows C:\ProgramData\$USER\tv\
      Linux ~/.tv/

EXAMPLES
   tv -f /path/to/scrubs.s01e02.avi

   Without Configuration File Sources Set
      Play pilot and set pointer
         tv Scrubs pilot --library TV
      Play next episode using omxplayer
         tv Scrubs next --source 'D:\TV' -p omxplayer
      Play previous episode for some_user 
         tv Scrubs prev -u some_user --library Television 
      Play one random episode from any season
         tv Scrubs all -r --source 'D:\TV' --source 'E:\Path\TV'

   With Configuration File Sources Set
      tv Scrubs s01                    # Play Season 1
      tv "Modern Family" s02 -r all    # Play Season 2 in random order
      tv Scrubs s02-s03 -l             # List Episodes From Season 2 to 3
      tv Scrubs s03e01                 # Play episode s03e01 and set pointer
      tv Scrubs next -q                # Queue next ep (s03e02) and move pointer
      tv Scrubs s03e15-s04e05          # Play Episode Range
      tv Scrubs latest --length        # Gets the length of the latest episode
      tv Scrubs s04e06 -s              # Set pointer only. Does not get played.
      tv Scrubs next- -u some_user     # Play remaining episodes in season for 
                                       #  some user

COPYRIGHT
   Copyright (c) 2013, Sam Malone. All rights reserved.

LICENSING
   The tv source code, binaries and man page are licensed under a BSD License.
   See LICENSE for details.

AUTHOR
   Sam Malone