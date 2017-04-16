package com.adeoluwa.android.popularmoviesapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ListItemClickListener {

    private List<Movie> mMovieList;
    private RecyclerView mMovieRecyclerView;
    private MovieAdapter adapter;
    private String mSortType = "popular";
    private ProgressBar mProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = (ProgressBar) findViewById(R.id.pb_progress_bar);
        mMovieList = new ArrayList<>();
        mMovieRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);

        mMovieRecyclerView.setLayoutManager(layoutManager);
        mMovieRecyclerView.setHasFixedSize(true);

        if (isConnected())
            getMovies();

    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra("movie", mMovieList.get(position));
        startActivity(intent);
    }
    private class MoviesAsyncTask extends AsyncTask<URL, Void, JSONObject> {

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
            adapter = new MovieAdapter(mMovieList, MainActivity.this);
            mMovieRecyclerView.setAdapter(adapter);
        }
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
    private boolean isConnected(){
        ConnectivityManager con = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = con.getActiveNetworkInfo();
        return activeNetInfo != null && activeNetInfo.isConnected();
    }
    private void getMovies() {
        URL movierepoURL = NetworkUtils.buildUrl(mSortType, this);
        new MoviesAsyncTask().execute(movierepoURL);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       if (id == R.id.action_popular) {
           mSortType = "popular";
           getMovies();
            return true;
        }else if(id == R.id.action_toprated){
           mSortType = "top_rated";
           getMovies();
           return true;
       }
        return super.onOptionsItemSelected(item);
    }
}
