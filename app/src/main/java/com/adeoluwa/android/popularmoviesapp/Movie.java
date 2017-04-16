package com.adeoluwa.android.popularmoviesapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Merlyne on 4/16/2017.
 */

public class Movie implements Parcelable {
    private String movieTitle;
    private double viewerRatings;
    private String overview;
    private String releasedDate;
    private String posterUrl;
    private String backdropUrl;
    private final static String POSTER_URL = "https://image.tmdb.org/t/p/w185";
    private final static String BACKDROP_IMAGE_URL = "https://image.tmdb.org/t/p/w500";

    public Movie(String title, double ratings,
                 String summary, String date, String relativePath){
        movieTitle = title;
        viewerRatings = ratings;
        overview = summary;
        releasedDate = date;
        posterUrl = POSTER_URL + relativePath;
        backdropUrl = POSTER_URL + relativePath;
    }

    protected Movie(Parcel in) {
        movieTitle = in.readString();
        viewerRatings = in.readDouble();
        overview = in.readString();
        releasedDate = in.readString();
        posterUrl = in.readString();
        backdropUrl = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public double getViewerRatings() {
        return viewerRatings;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleasedDate() {
        return releasedDate;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public String getBackdropUrl() {
        return backdropUrl;
    }

    public String getMovieTitle() {

        return movieTitle;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(movieTitle);
        dest.writeDouble(viewerRatings);
        dest.writeString(overview);
        dest.writeString(releasedDate);
        dest.writeString(posterUrl);
        dest.writeString(backdropUrl);
    }

}
