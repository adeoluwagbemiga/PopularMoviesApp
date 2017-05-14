package com.adeoluwa.android.popularmoviesapp;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.adeoluwa.android.popularmoviesapp.data.PopularMoviesContract;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ListItemClickListener,
        LoaderManager.LoaderCallbacks<List<Movie>>{

    private List<Movie> mMovieList;
    private RecyclerView mMovieRecyclerView;
    private MovieAdapter adapter;
    private final static String MOST_POPULAR_MOVIES = "popular";
    private final static String TOP_RATED_MOVIES = "top_rated";
    private static String mSortType = "popular";
    private ProgressBar mProgressBar;
    private boolean mFavorite;
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

       if(getApplication().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            layoutManager.setSpanCount(2);
        }else{
            layoutManager.setSpanCount(4);
        }

        checkConnectionToLoadMovies();
        if(getSupportLoaderManager().getLoader(0)!=null){
            getSupportLoaderManager().initLoader(0,null,this);
        }
    }

    @Override
    public void onItemClick(int position, byte[] movieposter) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra("movie", mMovieList.get(position));
        intent.putExtra("movieposter", movieposter);
        intent.putExtra("favorite", mFavorite);

        startActivity(intent);
    }

    private boolean isConnected(){
        ConnectivityManager con = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = con.getActiveNetworkInfo();
        return activeNetInfo != null && activeNetInfo.isConnected();
    }
    private void getMovies() {
        URL movierepoURL = NetworkUtils.buildUrl(mSortType, MainActivity.this);
        MovieAsyncTask asyncTask = new MovieAsyncTask(mProgressBar, mMovieList, mMovieRecyclerView, adapter, MainActivity.this);
        asyncTask.execute(movierepoURL);
    }
    public void checkConnectionToLoadMovies(){
        if (isConnected()){
            String queryString = mSortType;
            Bundle queryBundle = new Bundle();
            queryBundle.putString("queryString", queryString);
            getSupportLoaderManager().restartLoader(0, queryBundle, this);
            //getMovies();
        }else{
            mProgressBar.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Check device Network", Toast.LENGTH_LONG).show();
        }
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
        mMovieList.clear();
        //noinspection SimplifiableIfStatement
       if (id == R.id.action_popular) {
           mSortType = MOST_POPULAR_MOVIES;
           checkConnectionToLoadMovies();
           mFavorite = false;
            return true;
        }else if(id == R.id.action_toprated){
           mSortType = TOP_RATED_MOVIES;
           checkConnectionToLoadMovies();
           mFavorite = false;
           return true;
       }else if(id == R.id.action_favorite){
           loadFavorites();
           mFavorite = true;
           return true;
       }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
        return new MovieAsyncTaskLoader(this, args.getString("queryString"));
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
        mProgressBar.setVisibility(View.INVISIBLE);
        mMovieList = data;
        adapter = new MovieAdapter(mMovieList, (MovieAdapter.ListItemClickListener) this);
        mMovieRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        //getSupportLoaderManager().initLoader(0,null,this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //getSupportLoaderManager().restartLoader(0,null,this);
    }

    private void loadFavorites(){

        ContentResolver resolver = getContentResolver();
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
                mMovieList.add(new Movie(id, movieTitle, voteAverage, overview, releaseDate, posterPath));
                cursor.moveToNext();
            }
            mProgressBar.setVisibility(View.INVISIBLE);
            adapter = new MovieAdapter(mMovieList, (MovieAdapter.ListItemClickListener) this);
            mMovieRecyclerView.setAdapter(adapter);
            cursor.close();
        }else{
            mProgressBar.setVisibility(View.VISIBLE);
            Toast.makeText(this, "No Favorite Record in the Database", Toast.LENGTH_LONG).show();
        }
    }
}
