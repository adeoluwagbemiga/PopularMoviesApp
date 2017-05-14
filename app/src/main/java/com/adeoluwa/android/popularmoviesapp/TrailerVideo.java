package com.adeoluwa.android.popularmoviesapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Merlyne on 5/12/2017.
 */

public class TrailerVideo implements Parcelable {
    private String trailerId;
    private String trailerName;
    private String trailerKey;
    private String trailerType;
    private String videoUrl;
    private final static String VIDEO_URL = "https://youtu.be/";
    public TrailerVideo(String id, String name, String key, String type){
        trailerId = id;
        trailerName = name;
        trailerKey = key;
        trailerType = type;
        videoUrl = VIDEO_URL + key;
    }
    protected TrailerVideo(Parcel in) {
        trailerId = in.readString();
        trailerName = in.readString();
        trailerKey = in.readString();
        trailerType = in.readString();
    }

    public String getTrailerId(){
        return trailerId;
    }
    public String getTrailerName(){
        return trailerName;
    }
    public String getTrailerKey(){
        return trailerKey;
    }
    public  String getTrailerType(){
        return trailerType;
    }
    public String getvideoUrl() {
        return videoUrl;
    }
    public static final Creator<TrailerVideo> CREATOR = new Creator<TrailerVideo>() {
        @Override
        public TrailerVideo createFromParcel(Parcel in) {
            return new TrailerVideo(in);
        }

        @Override
        public TrailerVideo[] newArray(int size) {
            return new TrailerVideo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(trailerId);
        dest.writeString(trailerName);
        dest.writeString(trailerKey);
        dest.writeString(trailerType);
    }
}
