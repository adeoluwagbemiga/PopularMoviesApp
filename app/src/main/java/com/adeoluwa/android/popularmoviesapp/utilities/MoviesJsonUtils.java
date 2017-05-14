package com.adeoluwa.android.popularmoviesapp.utilities;

import android.content.ContentValues;
import android.content.Context;

import com.adeoluwa.android.popularmoviesapp.data.PopularMoviesContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

/**
 * Created by Merlyne on 5/11/2017.
 */

public class MoviesJsonUtils {

    private static final String MOVIE_LIST = "results";
   /* private static final String MOVIE_ID = "id";
    private static final String ORIGINAL_TITLE = "original_title";
    private static final String USER_RATING = "vote_average";
    private static final String RELEASE_DATE = "release_date";
    private static final String POSTER_PATH = "poster_path";
    private static final String OVERVIEW = "overview";*/


    private static final String RESPONSE_MESSAGE_CODE = "status_code";


    public static ContentValues[] getMovieContentValuesFromJson(Context context, String movieJsonStr)
            throws JSONException {

        JSONObject movieJson = new JSONObject(movieJsonStr);

        /* Is there an error? */
        if (movieJson.has(RESPONSE_MESSAGE_CODE)) {
            int errorCode = movieJson.getInt(RESPONSE_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

        JSONArray jsonMovieArray = movieJson.getJSONArray(MOVIE_LIST);

        //SunshinePreferences.setLocationDetails(context, cityLatitude, cityLongitude);

        ContentValues[] movieContentValues = new ContentValues[jsonMovieArray.length()];

        for (int i = 0; i < jsonMovieArray.length(); i++) {

            JSONObject movie = jsonMovieArray.getJSONObject(i);
            String movieTitle = movie.getString("original_title");
            double voteAverage = movie.getDouble("vote_average");
            String releaseDate = movie.getString("release_date");
            String posterPath = movie.getString("poster_path");
            String overview = movie.getString("overview");
            int movie_id = movie.getInt("id");

            ContentValues movieValues = new ContentValues();
            movieValues.put(PopularMoviesContract.PopularMoviesEntry.COLUMN_MOVIE_ID, movie_id);
            movieValues.put(PopularMoviesContract.PopularMoviesEntry.COLUMN_POSTER, posterPath);
            movieValues.put(PopularMoviesContract.PopularMoviesEntry.COLUMN_RELEASE_DATE, releaseDate);
            movieValues.put(PopularMoviesContract.PopularMoviesEntry.COLUMN_TITLE, movieTitle);
            movieValues.put(PopularMoviesContract.PopularMoviesEntry.COLUMN_USER_RATING, voteAverage);
            movieValues.put(PopularMoviesContract.PopularMoviesEntry.COLUMN_SYNOPSIS, overview);

            movieContentValues[i] = movieValues;
        }

        return movieContentValues;
    }
}
