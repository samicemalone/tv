package uk.co.samicemalone.tv.tvdb.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import uk.co.samicemalone.tv.model.Episode;

import java.time.Instant;
import java.util.Date;

@DatabaseTable(tableName = "showProgress")
public class ShowProgress {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false, columnName = "showId", foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    private Show show;

    @DatabaseField
    private String tag;

    @DatabaseField(canBeNull = false)
    private int season;

    @DatabaseField(canBeNull = false)
    private int episode;

    @DatabaseField(
        dataType = DataType.DATE_STRING,
        format = "yyyy-MM-dd HH:mm:ss",
        columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
    )
    private Date watchedAt;

    public ShowProgress() {

    }

    public ShowProgress(Show show, String tag, int season, int episode) {
        this.show = show;
        this.tag = tag;
        this.season = season;
        this.episode = episode;
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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
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

    public void setWatchedAt(Instant watchedAt) {
        this.watchedAt = new Date(watchedAt.toEpochMilli());
    }

    public Episode toEpisode() {
        Episode e = new Episode(show.getName(), tag, season, episode);
        e.setWatchedAt(watchedAt.toInstant());
        return e;
    }
}
