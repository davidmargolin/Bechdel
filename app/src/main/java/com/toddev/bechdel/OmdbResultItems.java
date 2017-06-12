package com.toddev.bechdel;

/**
 * Created by David Margolin on 5/9/2016.
 * Items from the OMDB database
 */
public class OmdbResultItems {
    private String poster_path;
    private String title;
    private String release_date;
    private String overview;
    private String revenue;
    private String vote_average;
    private String imdb_id;

    public String getimdb_id() {
        return imdb_id;
    }

    public void setimdb_id(String imdb_id) {
        this.imdb_id = imdb_id;
    }

    public String getvote_average() {
        return vote_average;
    }

    public void setvote_average(String vote_average) {
        this.vote_average = vote_average;
    }

    public String getrelease_date() {
        return release_date.substring(0,4);
    }

    public void setrelease_date(String Release_date) {
        release_date = Release_date;
    }

    public String getoverview() {
        return overview;
    }

    public void setoverview(String Overview) {overview = Overview;
    }

    public String getrevenue() {
        return revenue;
    }

    public void setrevenue(String Revenue) {
        revenue = Revenue;
    }

    public String getposter_path() {
        return poster_path;
    }

    public void setposter_path(String poster) {
        poster_path = poster;
    }

    public String gettitle() {
        return title;
    }

    public void settitle(String Title) {
        title = Title;
    }


}
