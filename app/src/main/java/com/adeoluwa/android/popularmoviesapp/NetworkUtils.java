package com.adeoluwa.android.popularmoviesapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Merlyne on 4/16/2017.
 */

public class NetworkUtils {
    static String MOVIE_BASE_URL;
    static String PARAM_API;
    static String API_KEY;

    public static URL buildUrl(String sortBy, Context context) {
        MOVIE_BASE_URL = context.getString(R.string.base_url);
        PARAM_API = context.getString(R.string.api_param);
        API_KEY = context.getString(R.string.api_key);

        Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendEncodedPath(sortBy)
                .appendQueryParameter(PARAM_API, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    @SuppressLint("NewApi")
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = null;
        StringBuilder builder;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            int response = urlConnection.getResponseCode();

            if (response == HttpURLConnection.HTTP_OK)
            {
                builder = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(urlConnection.getInputStream())))
                {
                    String line;

                    while ((line = reader.readLine()) != null){
                        builder.append(line);
                    }
                }
                return builder.toString();
            }
        }catch (Exception e)
        {

        } finally {
            urlConnection.disconnect();
        }
        return null;
    }
}
