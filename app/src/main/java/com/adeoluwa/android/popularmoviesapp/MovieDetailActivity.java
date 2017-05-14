package com.adeoluwa.android.popularmoviesapp;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.adeoluwa.android.popularmoviesapp.data.PopularMoviesContract;
import com.adeoluwa.android.popularmoviesapp.data.PopularMoviesProvider;
import com.adeoluwa.android.popularmoviesapp.utilities.ReviewAsyncTaskLoader;
import com.adeoluwa.android.popularmoviesapp.utilities.TrailerVideoAsyncTaskLoader;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailActivity extends AppCompatActivity implements TrailerVideoAdapter.ListItemClickListener {

    @BindView(R.id.movie_title) TextView mOriginalTitle;
    @BindView(R.id.overview) TextView mOverview;
    @BindView(R.id.release_date) TextView mReleaseDate;
    @BindView(R.id.vote_average) TextView mVoteAverage;
    @BindView(R.id.movie_backdrop) ImageView mMovieBackDrop;
    //@BindView(R.id.rv_movie_reviews)RecyclerView mReviewRecyclerView;
    //@BindView(R.id.rv_movie_trailers)RecyclerView mTrailerRecyclerView;
    private Movie movie = null;
    private byte[] mMoviePoster;
    private boolean mFavorite;

    private List<Review> mReviewList;
    private RecyclerView mReviewRecyclerView;
    private ReviewAdapter mReviewAdapter;

    private List<TrailerVideo> mTrailerList;
    private RecyclerView mTrailerRecyclerView;
    private TrailerVideoAdapter mTrailerVideoAdapter;

    private LoaderManager.LoaderCallbacks<List<Review>> reviewLoaderListener;
    private LoaderManager.LoaderCallbacks<List<TrailerVideo>> trailerVideoLoaderListener;

    private static final int REVIEW_LOADER_ID = 0;
    private static final int VIDEO_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        //Movie movie = null;

        if(extras != null && extras.containsKey("movie"))
            movie =  intent.getParcelableExtra("movie");
        //mMoviePoster = (byte[])intent.getByteArrayExtra("movieposter");
        mFavorite = intent.getBooleanExtra("favorite", mFavorite);

        mOriginalTitle.setText(movie.getMovieTitle());
        mOverview.setText(movie.getOverview());

        mReleaseDate.setText(movie.getReleasedDate());
        String vote_average = String.valueOf(movie.getViewerRatings()) + "/10";
        mVoteAverage.setText(vote_average);

        Picasso.with(getBaseContext())
                .load(movie.getBackdropUrl())
                .placeholder(R.mipmap.futurestudio_logo_transparent)
                .error(R.mipmap.futurestudio_logo_transparent)
                .resize(300, 400).into(mMovieBackDrop);
        mReviewRecyclerView = (RecyclerView) findViewById(R.id.rv_movie_reviews);
        mTrailerRecyclerView = (RecyclerView)findViewById(R.id.rv_movie_trailers);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this);
        mReviewRecyclerView.setLayoutManager(layoutManager1);
        mReviewRecyclerView.setHasFixedSize(true);
        mTrailerRecyclerView.setLayoutManager(layoutManager2);
        mTrailerRecyclerView.setHasFixedSize(true);
        reviewLoaderListener = new LoaderManager.LoaderCallbacks<List<Review>>() {
            @Override
            public Loader<List<Review>> onCreateLoader(int id, Bundle args) {
                return new ReviewAsyncTaskLoader(MovieDetailActivity.this, String.valueOf(movie.getMovieId()));
            }

            @Override
            public void onLoadFinished(Loader<List<Review>> loader, List<Review> data) {
                if (data != null & data.size() != 0) {
                    mReviewList = data;
                    mReviewAdapter = new ReviewAdapter(mReviewList);
                    mReviewRecyclerView.setAdapter(mReviewAdapter);
                }
            }

            @Override
            public void onLoaderReset(Loader<List<Review>> loader) {

            }
        };
        trailerVideoLoaderListener = new LoaderManager.LoaderCallbacks<List<TrailerVideo>>() {
            @Override
            public Loader<List<TrailerVideo>> onCreateLoader(int id, Bundle args) {
                return new TrailerVideoAsyncTaskLoader(MovieDetailActivity.this, String.valueOf(movie.getMovieId()));
            }

            @Override
            public void onLoadFinished(Loader<List<TrailerVideo>> loader, List<TrailerVideo> data) {
                if (data != null & data.size() != 0) {
                    mTrailerList = data;
                    mTrailerVideoAdapter = new TrailerVideoAdapter(mTrailerList, (TrailerVideoAdapter.ListItemClickListener)MovieDetailActivity.this);
                    mTrailerRecyclerView.setAdapter(mTrailerVideoAdapter);
                }
            }

            @Override
            public void onLoaderReset(Loader<List<TrailerVideo>> loader) {

            }
        };

        getSupportLoaderManager().initLoader(REVIEW_LOADER_ID, null, reviewLoaderListener);
        getSupportLoaderManager().initLoader(VIDEO_LOADER_ID, null, trailerVideoLoaderListener);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id){
            case R.id.action_favorite:


                    if(saveFavoriteMovie()){
                        item.setIcon(R.drawable.ic_action_favorited);
                    }else if(deleteFavoriteMovie()){
                        item.setIcon(R.drawable.ic_action_favorite);
                    }
                    return true;
            case R.id.action_share:
                StringBuilder linktoshare = new StringBuilder();
                for (TrailerVideo video : mTrailerList
                        ) {
                    linktoshare.append(video.getvideoUrl()).append("\n");
                }
                ShareCompat.IntentBuilder.from(MovieDetailActivity.this).setChooserTitle("Choose").setText(linktoshare.toString()).setType("text/plain").startChooser();
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private boolean saveFavoriteMovie() {

        URL url = null;
        try {
            url = new URL(movie.getPosterUrl());
            Bitmap poster = BitmapFactory.decodeStream(url.openConnection().getInputStream());

            ByteArrayOutputStream moviebytearray = new ByteArrayOutputStream();
            poster.compress(Bitmap.CompressFormat.JPEG, 100, moviebytearray);
            mMoviePoster = moviebytearray.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ContentValues mContentValues = new ContentValues();

        mContentValues.put(PopularMoviesContract.PopularMoviesEntry.COLUMN_RELEASE_DATE, movie.getReleasedDate());
        mContentValues.put(PopularMoviesContract.PopularMoviesEntry.COLUMN_MOVIE_ID, movie.getMovieId());
        mContentValues.put(PopularMoviesContract.PopularMoviesEntry.COLUMN_TITLE, movie.getMovieTitle());
        mContentValues.put(PopularMoviesContract.PopularMoviesEntry.COLUMN_SYNOPSIS, movie.getOverview());
        mContentValues.put(PopularMoviesContract.PopularMoviesEntry.COLUMN_USER_RATING, movie.getViewerRatings());
        mContentValues.put(PopularMoviesContract.PopularMoviesEntry.COLUMN_POSTER_PATH, movie.getPosterUrl());
        mContentValues.put(PopularMoviesContract.PopularMoviesEntry.COLUMN_POSTER, mMoviePoster);

        try {
            getContentResolver().insert(PopularMoviesContract.PopularMoviesEntry.CONTENT_URI, mContentValues);

            return true;
        } catch (SQLiteConstraintException e) {

            return false;
        }
    }

    private boolean deleteFavoriteMovie(){
        try {
            Uri uri = Uri.parse(PopularMoviesContract.PopularMoviesEntry.CONTENT_URI + "/#");

            getContentResolver().delete(uri, String.valueOf(movie.getMovieId()), null);

            return true;
        } catch (Exception e) {
            return false;
        }
    }


    @Override
    public void onItemClick(int position) {
        TrailerVideo trailerVideo = mTrailerList.get(position);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailerVideo.getvideoUrl()));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
        startActivity(intent);
    }
}
