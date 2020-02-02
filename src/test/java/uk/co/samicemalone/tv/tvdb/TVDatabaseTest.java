package uk.co.samicemalone.tv.tvdb;

import org.junit.Test;
import uk.co.samicemalone.tv.model.Episode;
import uk.co.samicemalone.tv.tvdb.model.Show;
import uk.co.samicemalone.tv.tvdb.model.ShowProgress;

import java.sql.SQLException;
import java.time.Instant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class TVDatabaseTest extends AbstractTVDatabaseTest {

    @Test
    public void connect() {
        assertNotNull(source);
    }

    @Test
    public void getShowByName() throws SQLException {
        String showName = "The Walking Dead";
        Show show = new Show(showName);
        assertNull(tvdb.getShowByName(showName));
        tvdb.createOrUpdateShow(show);
        Show actualShow = tvdb.getShowByName(showName);
        assertEquals(showName, actualShow.getName());
    }

    @Test
    public void setShowProgress() throws SQLException {
        String showName = "The Walking Dead";
        Show show = new Show(showName);
        Instant watchedAt = Instant.now();
        tvdb.createOrUpdateShow(show);
        ShowProgress expectedProgress = new ShowProgress(show, "", 1, 1);
        Episode episode = new Episode(show.getName(), "", 3, 2);
        episode.setWatchedAt(watchedAt);
        tvdb.setShowProgress(expectedProgress, episode);
        ShowProgress actualProgress = tvdb.getShowProgress(show, "");
        assertNotNull(actualProgress);
        assertEquals(3, actualProgress.getSeason());
        assertEquals(2, actualProgress.getEpisode());
        assertEquals(watchedAt.getEpochSecond(), actualProgress.getWatchedAt().toInstant().getEpochSecond());
    }

    @Test
    public void createOrUpdateShow() throws SQLException {
        String showName = "The Walking Dead";
        String slug = "the-walking-dead";
        Show show = new Show(showName);
        tvdb.createOrUpdateShow(show);
        int id = show.getId();
        Show actualShow = tvdb.getShowByName(showName);
        assertEquals(id, actualShow.getId());
        assertEquals(showName, actualShow.getName());
        assertNull(actualShow.getSlug());
        show.setSlug(slug);
        tvdb.createOrUpdateShow(show);
        actualShow = tvdb.getShowByName(showName);
        assertEquals(id, actualShow.getId());
        assertEquals(slug, actualShow.getSlug());
    }
}
