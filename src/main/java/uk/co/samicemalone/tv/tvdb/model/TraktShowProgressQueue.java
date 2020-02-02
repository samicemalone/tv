package uk.co.samicemalone.tv.tvdb.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import uk.co.samicemalone.tv.model.Episode;

import java.util.Date;

@DatabaseTable(tableName = "showProgressQueue")
public class TraktShowProgressQueue {
    public static final String SEEN = "SEEN";
    public static final String UNSEEN = "UNSEEN";

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false, foreign = true, columnName = "showId", foreignAutoRefresh = true)
    private Show show;

    @DatabaseField(canBeNull = false, defaultValue = SEEN)
    private String markType;

    @DatabaseField(canBeNull = false)
    private int season;

    @DatabaseField(canBeNull = false)
    private int episode;

    @DatabaseField(dataType = DataType.DATE_STRING)
    private Date watchedAt;

    public TraktShowProgressQueue() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Show getShow() {
        return show;
    }

    public void setShow(Show show) {
        this.show = show;
    }

    public String getMarkType() {
        return markType;
    }

    public void setMarkType(String markType) {
        this.markType = markType;
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public int getEpisode() {
        return episode;
    }

    public void setEpisode(int episode) {
        this.episode = episode;
    }

    public Date getWatchedAt() {
        return watchedAt;
    }

    public void setWatchedAt(Date watchedAt) {
        this.watchedAt = watchedAt;
    }

    public Episode toEpisode() {
        return new Episode(show.getName(), null, season, episode).setWatchedAt(watchedAt.toInstant());
    }
}
