package uk.co.samicemalone.tv.tvdb.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.uwetrottmann.trakt5.entities.ShowIds;

@DatabaseTable(tableName = "show")
public class Show {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false, indexName = "indexShowName")
    private String name;

    @DatabaseField()
    private int year;

    @DatabaseField()
    private int traktId;

    @DatabaseField()
    private int tvdbId;

    @DatabaseField()
    private String imdbId;

    @DatabaseField()
    private String slug;

    public Show() {

    }

    public Show(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTraktId() {
        return traktId;
    }

    public void setTraktId(int traktId) {
        this.traktId = traktId;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getTVDBId() {
        return tvdbId;
    }

    public void setTVDBId(int tvdbId) {
        this.tvdbId = tvdbId;
    }

    public String getIMDBId() {
        return imdbId;
    }

    public void setIMDBId(String imdbId) {
        this.imdbId = imdbId;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public void setTraktIds(ShowIds ids) {
        traktId = ids.trakt;
        tvdbId = ids.tvdb;
        imdbId = ids.imdb;
        slug = ids.slug;
    }

    public boolean hasTraktIds() {
        return traktId > 0 || tvdbId > 0 || imdbId != null || slug != null;
    }
}
