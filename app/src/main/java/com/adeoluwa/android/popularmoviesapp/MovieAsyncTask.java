package com.adeoluwa.android.popularmoviesapp;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Created by Merlyne on 4/21/2017.
 */

public class MovieAsyncTask extends AsyncTask<URL, Void, JSONObject> {
    private ProgressBar mProgressBar;
    private MovieAdapter adapter;
    private List<Movie> mMovieList;
    private Context mContext;
    private RecyclerView mMovieRecyclerView;
    public MovieAsyncTask(ProgressBar progressBar,
                          List<Movie> movieList, RecyclerView recyclerView, MovieAdapter movieAdapter,
                          Context context){
        mProgressBar = progressBar;
        adapter = movieAdapter;
        mMovieList = movieList;
        mContext = context;
        mMovieRecyclerView = recyclerView;
    }
    @Override
    public void onPreExecute(){
        mProgressBar.setVisibility(View.VISIBLE);
    }
    @Override
    protected JSONObject doInBackground(URL... params) {
        String r = "";
        try {
            r = NetworkUtils.getResponseFromHttpUrl(params[0]);

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if(r.isEmpty() || r == null) return new JSONObject();
            return new JSONObject(r);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public void onPostExecute(JSONObject jsonObject){
        JsonParser(jsonObject);
        mProgressBar.setVisibility(View.INVISIBLE);
        adapter = new MovieAdapter(mMovieList, (MovieAdapter.ListItemClickListener) mContext);
        mMovieRecyclerView.setAdapter(adapter);
    }

    private void JsonParser(JSONObject jsonObject) {
        mMovieList.clear();
        try {
            JSONArray list = jsonObject.getJSONArray("results");

            for(int i = 0; i < list.length(); i++)
            {
                JSONObject movie = list.getJSONObject(i);
                String movieTitle = movie.getString("original_title");
                double voteAverage = movie.getDouble("vote_average");
                String releaseDate = movie.getString("release_date");
                String posterPath = movie.getString("poster_path");
                String overview = movie.getString("overview");

                mMovieList.add(new Movie(movieTitle, voteAverage, overview, releaseDate, posterPath));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
