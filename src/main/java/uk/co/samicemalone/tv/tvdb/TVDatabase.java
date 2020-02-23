package uk.co.samicemalone.tv.tvdb;


import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import uk.co.samicemalone.tv.model.Episode;
import uk.co.samicemalone.tv.tvdb.model.Show;
import uk.co.samicemalone.tv.tvdb.model.ShowProgress;

import java.sql.SQLException;

public class TVDatabase {

    public static final String IN_MEMORY_DATABASE = ":memory:";

    private ConnectionSource connectionSource;

    private Dao<Show, Integer> showDao;
    private Dao<ShowProgress, Integer> showProgressDao;

    public TVDatabase() {
        this(null);
    }

    public TVDatabase(ConnectionSource source) {
        this.connectionSource = source;
    }

    public ConnectionSource connect(String databasePath) throws SQLException {
        String databaseUrl = String.format("jdbc:sqlite:%s", databasePath);
        connectionSource = new JdbcConnectionSource(databaseUrl);

        showDao = DaoManager.createDao(connectionSource, Show.class);
        showProgressDao = DaoManager.createDao(connectionSource, ShowProgress.class);

        TableUtils.createTableIfNotExists(connectionSource, Show.class);
        TableUtils.createTableIfNotExists(connectionSource, ShowProgress.class);

        return connectionSource;
    }

    public Show getShowByName(String showName) throws SQLException {
        QueryBuilder<Show, Integer> qb = showDao.queryBuilder();
        qb.where().eq("name", new SelectArg(showName));
        PreparedQuery<Show> query = qb.prepare();
        return showDao.queryForFirst(query);
    }

    public ShowProgress getShowProgress(int id) throws SQLException {
        return showProgressDao.queryForId(id);
    }

    public ShowProgress getShowProgress(Show show, String tag) throws SQLException {
        QueryBuilder<Show, Integer> showQb = showDao.queryBuilder();
        showQb.where().eq("name", new SelectArg(show.getName()));

        QueryBuilder<ShowProgress, Integer> progressQb = showProgressDao.queryBuilder().join(showQb);
        progressQb.where().eq("tag", new SelectArg(tag));

        return showProgressDao.queryForFirst(progressQb.prepare());
    }

    public void setShowProgress(ShowProgress currentProgress, Episode newEpisode) throws SQLException {
        if(currentProgress == null || currentProgress.getId() == 0) {
            Show show = getShowByName(newEpisode.getShow());
            ShowProgress showProgress = new ShowProgress(
                show,
                newEpisode.getUser(),
                newEpisode.getSeason(),
                newEpisode.getEpisode()
            );
            showProgress.setWatchedAt(newEpisode.getWatchedAt());
            showProgressDao.create(showProgress);
        } else {
            UpdateBuilder<ShowProgress, Integer> updateBuilder = showProgressDao.updateBuilder();
            updateBuilder
                .updateColumnValue("season", newEpisode.getSeason())
                .updateColumnValue("episode", newEpisode.getEpisode())
                .where()
                .idEq(currentProgress.getId());
            updateBuilder.update();
        }
    }

    public void createOrUpdateShow(Show show) throws SQLException {
        if(show.getId() == 0) {
            showDao.create(show);
        } else {
            showDao.update(show);
        }
    }

    public ConnectionSource getConnectionSource() {
        return connectionSource;
    }
}
