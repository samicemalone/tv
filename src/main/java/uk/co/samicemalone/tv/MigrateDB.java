package uk.co.samicemalone.tv;

import com.j256.ormlite.support.ConnectionSource;
import com.uwetrottmann.trakt5.entities.SearchResult;
import uk.co.samicemalone.tv.exception.TraktException;
import uk.co.samicemalone.tv.io.LibraryManager;
import uk.co.samicemalone.tv.io.TVDBManager;
import uk.co.samicemalone.tv.model.Episode;
import uk.co.samicemalone.tv.options.UnixEnvironment;
import uk.co.samicemalone.tv.options.WindowsEnvironment;
import uk.co.samicemalone.tv.trakt.TraktClient;
import uk.co.samicemalone.tv.trakt.TraktUI;
import uk.co.samicemalone.tv.tvdb.TVDatabase;
import uk.co.samicemalone.tv.tvdb.model.Show;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MigrateDB {

    private static TVDatabase tvDatabase;
    private static Map<String, Integer> traktDB;
    private static TraktClient trakt;

    public static void main(String[] args) throws IOException, SQLException, TraktException {
        TV.ENV = new WindowsEnvironment();
        tvDatabase = new TVDatabase();
        trakt = new TraktClient();
        trakt.authenticate(TV.ENV.getTraktAuthFile());

        List<Episode> oldTVDatabase = MigrateDB.readTVDB();
        traktDB = readTraktDB();

        try (ConnectionSource connection = tvDatabase.connect(TV.ENV.getTVDB())) {
            for (Episode episode : oldTVDatabase) {
                if(episode.getShow().equals("Friends (DVD)")) {
                    continue;
                }
                System.out.println("[Show] " + episode.getShow());
                Show show = getShow(episode.getShow());
                if(!show.hasTraktIds()) {
                    System.out.println("[Search] " + episode.getShow());
                    SearchResult result = getShowSearchResult(show.getName());
                    if(result == null) {
                        System.out.println("[Skipped] " + episode.getShow());
                        continue;
                    }
                    show.setTraktIds(result.show.ids);
                    show.setYear(result.show.year);
                    tvDatabase.createOrUpdateShow(show);
                    System.out.println("[Created] " + episode.getShow());
                }
                System.out.println("[Progress] " + episode.getShow() + " => " + episode.getEpisode());
                tvDatabase.setShowProgress(null, episode);
            }
        }
    }

    private static List<Episode> readTVDB() throws FileNotFoundException {
        TVDBManager tvdb = new TVDBManager(new File("C:\\ProgramData\\Ice\\tv\\tvdb.csv"));
        return tvdb.readAllStorage();
    }

    private static Map<String, Integer> readTraktDB() throws IOException {
        BufferedReader r = new BufferedReader(new FileReader("C:\\ProgramData\\Ice\\tv\\traktdb.csv"));
        Map<String, Integer> traktDB = new HashMap<>();
        try {
            String line;
            while((line = r.readLine()) != null) {
                if(line.isEmpty()) {
                    continue;
                }
                int sepIndex = line.indexOf("\",\"");
                String show = line.substring(1, sepIndex);
                Integer traktId = Integer.parseInt(line.substring(sepIndex + 3, line.length() - 1));
                traktDB.put(show, traktId);
            }
        } finally {
            r.close();
        }
        return traktDB;
    }

    private static Show getShow(String showName) throws SQLException {
        Show show = tvDatabase.getShowByName(showName);
        if(show == null) {
            show = new Show(showName);
            tvDatabase.createOrUpdateShow(show);
        }
        return show;
    }

    private static SearchResult getShowSearchResult(String showName) throws TraktException {
        Pattern showYearInTitle = Pattern.compile("(.*) \\(\\d{4}\\)");
        Matcher m = showYearInTitle.matcher(showName);
        if(m.find()) {
            showName = m.group(1);
        }
        List<SearchResult> searchResults = trakt.searchShow(showName);
        if(searchResults.size() == 1) {
            return searchResults.get(0);
        }
        for (SearchResult searchResult : searchResults) {
            Integer tvdbId = searchResult.show.ids.tvdb;
            if(tvdbId != null && tvdbId.equals(traktDB.get(showName))) {
                System.out.println("[Trakt] TVDB Match");
                return searchResult;
            }
        }
        return TraktUI.readShowSearchResult(showName, searchResults);
    }
}
