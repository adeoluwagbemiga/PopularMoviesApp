package com.adeoluwa.android.popularmoviesapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Merlyne on 5/12/2017.
 */

public class Review implements Parcelable {
    private String movieId;
    private String movieReview;
    private String movieReviewer;
    public Review(String id, String review, String reviewer){
        movieId = id;
        movieReview = review;
        movieReviewer = reviewer;
    }

    public String getMovieId(){
        return movieId;
    }
    public String getMovieReview() {

        return movieReview;
    }
    public String getMovieReviewer(){
        return movieReviewer;
    }
    protected Review(Parcel in) {
        movieId = in.readString();
        movieReview = in.readString();
        movieReviewer = in.readString();
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(movieId);
        dest.writeString(movieReview);
        dest.writeString(movieReviewer);
    }
}
