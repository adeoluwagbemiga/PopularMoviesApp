package com.adeoluwa.android.popularmoviesapp;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;


import com.adeoluwa.android.popularmoviesapp.data.PopularMoviesContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Merlyne on 5/12/2017.
 */

public class MovieAsyncTaskLoader extends AsyncTaskLoader<List<Movie>> {
    private List<Movie> mMovieList = new ArrayList<Movie>();
    private String mQueryString;
    Context mContext;
    public MovieAsyncTaskLoader(Context context, String queryString) {
        super(context);
        mQueryString = queryString;
        mContext = context;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        mMovieList.clear();
        forceLoad();
    }

    @Override
    public List<Movie> loadInBackground() {
        if(mQueryString.equals("favorites")){
            ContentResolver resolver = mContext.getContentResolver();
            Cursor cursor = resolver.query(PopularMoviesContract.PopularMoviesEntry.CONTENT_URI, null, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {

                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {

                    int id = cursor.getInt(cursor.getColumnIndex(PopularMoviesContract.PopularMoviesEntry.COLUMN_MOVIE_ID));
                    String movieTitle =cursor.getString(cursor.getColumnIndex(PopularMoviesContract.PopularMoviesEntry.COLUMN_TITLE));
                    String overview =cursor.getString(cursor.getColumnIndex(PopularMoviesContract.PopularMoviesEntry.COLUMN_SYNOPSIS));
                    String releaseDate =cursor.getString(cursor.getColumnIndex(PopularMoviesContract.PopularMoviesEntry.COLUMN_RELEASE_DATE));
                    double voteAverage =cursor.getDouble(cursor.getColumnIndex(PopularMoviesContract.PopularMoviesEntry.COLUMN_USER_RATING));
                    String posterPath =cursor.getString(cursor.getColumnIndex(PopularMoviesContract.PopularMoviesEntry.COLUMN_POSTER_PATH));
                    byte[] poster = cursor.getBlob(cursor.getColumnIndex(PopularMoviesContract.PopularMoviesEntry.COLUMN_POSTER));
                    mMovieList.add(new Movie(id, movieTitle, voteAverage, overview, releaseDate, posterPath, poster));
                    cursor.moveToNext();
                }
                cursor.close();
            }
        }else {
            String result = "";
            try {
                URL url = NetworkUtils.buildUrl(mQueryString, mContext);
                result = NetworkUtils.getResponseFromHttpUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (result == null || result.isEmpty()) return mMovieList;
                JSONObject jsonObject = new JSONObject(result);

                JSONArray resultsArray = jsonObject.getJSONArray("results");
                for (int i = 0; i < resultsArray.length(); i++) {
                    JSONObject movie = resultsArray.getJSONObject(i);
                    String movieTitle = movie.getString("original_title");
                    double voteAverage = movie.getDouble("vote_average");
                    String releaseDate = movie.getString("release_date");
                    String posterPath = movie.getString("poster_path");
                    String overview = movie.getString("overview");
                    int id = movie.getInt("id");

                    mMovieList.add(new Movie(id, movieTitle, voteAverage, overview, releaseDate, posterPath));
                }
                return mMovieList;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return mMovieList;
    }

    @Override
    public void deliverResult(List<Movie> data) {
        super.deliverResult(data);
        mMovieList = data;
    }

}
