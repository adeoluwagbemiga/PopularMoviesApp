package com.adeoluwa.android.popularmoviesapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ListItemClickListener {

    private List<Movie> mMovieList;
    private RecyclerView mMovieRecyclerView;
    private MovieAdapter adapter;
    private final static String MOST_POPULAR_MOVIES = "popular";
    private final static String TOP_RATED_MOVIES = "top_rated";
    private static String mSortType = "popular";
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

       if(getApplication().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            layoutManager.setSpanCount(2);
        }else{
            layoutManager.setSpanCount(4);
        }

        checkConnectionToLoadMovies();
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra("movie", mMovieList.get(position));
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
            getMovies();
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

        //noinspection SimplifiableIfStatement
       if (id == R.id.action_popular) {
           mSortType = MOST_POPULAR_MOVIES;
           checkConnectionToLoadMovies();
            return true;
        }else if(id == R.id.action_toprated){
           mSortType = TOP_RATED_MOVIES;
           checkConnectionToLoadMovies();
           return true;
       }
        return super.onOptionsItemSelected(item);
    }


}
