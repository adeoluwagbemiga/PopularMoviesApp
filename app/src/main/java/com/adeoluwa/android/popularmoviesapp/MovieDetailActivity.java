package com.adeoluwa.android.popularmoviesapp;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adeoluwa.android.popularmoviesapp.data.PopularMoviesContract;
import com.adeoluwa.android.popularmoviesapp.utilities.ReviewAsyncTaskLoader;
import com.adeoluwa.android.popularmoviesapp.utilities.TrailerVideoAsyncTaskLoader;
import com.squareup.picasso.Picasso;

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
    @BindView(R.id.movie_review_title) TextView mReviewHeader;
    @BindView(R.id.movie_trailer_header) TextView mTrailerHeader;
    @BindView(R.id.collapsing_movie_title) TextView mCollapsingTBTitle;
    @BindView(R.id.fab) FloatingActionButton mFab;
    @BindView(R.id.rv_movie_reviews)RecyclerView mReviewRecyclerView;
    @BindView(R.id.rv_movie_trailers)RecyclerView mTrailerRecyclerView;
    private Movie movie = null;
    private byte[] mMoviePoster;

    private List<Review> mReviewList = new ArrayList<>();
    //private RecyclerView mReviewRecyclerView;
    private ReviewAdapter mReviewAdapter;

    private List<TrailerVideo> mTrailerList = new ArrayList<>();
    //private RecyclerView mTrailerRecyclerView;
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
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        //Movie movie = null;

        if(extras != null && extras.containsKey("movie"))
            movie =  intent.getParcelableExtra("movie");
        mMoviePoster = (byte[])intent.getByteArrayExtra("movieposter");
        //collapsingToolbar.setTitle(movie.getMovieTitle());
        collapsingToolbar.setTitleEnabled(false);
        mCollapsingTBTitle.setText(movie.getMovieTitle());
        collapsingToolbar.setContentScrimResource(R.color.black_transparent);
        getSupportActionBar().setTitle(movie.getMovieTitle());

        mOriginalTitle.setText(getString(R.string.overview_title));
        mOverview.setText(movie.getOverview());

        mReleaseDate.setText(movie.getReleasedDate());
        String vote_average = String.valueOf(movie.getViewerRatings()) + getString(R.string.vote_average_denominator);
        mVoteAverage.setText(vote_average);



        if(inProviderMovies(movie.getMovieId())){
            getImage();
        }else{
            Picasso.with(getBaseContext())
                    .load(movie.getPosterUrl())
                    .placeholder(R.mipmap.ic_launcher_2)
                    .error(R.mipmap.ic_launcher_2)
                    .resize(300, 400).into(mMovieBackDrop);
        }

        mReviewRecyclerView = (RecyclerView) findViewById(R.id.rv_movie_reviews);
        mTrailerRecyclerView = (RecyclerView)findViewById(R.id.rv_movie_trailers);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this);
        GridLayoutManager layoutManager2 = new GridLayoutManager(this, 1);
        if(getApplication().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            layoutManager2.setSpanCount(1);
        }else{
            layoutManager2.setSpanCount(2);
        }
        mReviewRecyclerView.setLayoutManager(layoutManager1);
        mReviewRecyclerView.setHasFixedSize(true);
        mTrailerRecyclerView.setLayoutManager(layoutManager2);
        mReviewRecyclerView.getScrollState();
        mTrailerRecyclerView.setHasFixedSize(true);
        if(isConnected()) {
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
                    } else {
                        mReviewHeader.setText(getString(R.string.no_reviews_info));
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
                        mTrailerVideoAdapter = new TrailerVideoAdapter(mTrailerList, (TrailerVideoAdapter.ListItemClickListener) MovieDetailActivity.this);
                        mTrailerRecyclerView.setAdapter(mTrailerVideoAdapter);
                    } else {
                        mTrailerHeader.setText(getString(R.string.no_trailers_info));
                    }
                }

                @Override
                public void onLoaderReset(Loader<List<TrailerVideo>> loader) {

                }
            };

            getSupportLoaderManager().initLoader(REVIEW_LOADER_ID, null, reviewLoaderListener);
            getSupportLoaderManager().initLoader(VIDEO_LOADER_ID, null, trailerVideoLoaderListener);
        }else{
            mReviewHeader.setText(getString(R.string.no_reviews_info));
            mTrailerHeader.setText(getString(R.string.no_trailers_info));
            Toast.makeText(this, getString(R.string.no_network_message), Toast.LENGTH_LONG).show();
        }


        setFabResource();
        mFab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (inProviderMovies(movie.getMovieId())) {
                    if (deleteFavoriteMovie()) {
                        mFab.setImageResource(R.drawable.ic_action_favorite);
                    }
                } else {
                    if (saveFavoriteMovie()) {
                        mFab.setImageResource(R.drawable.ic_action_favorited);
                    }
                }
            }
        });
    }

    private boolean isConnected(){
        ConnectivityManager con = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = con.getActiveNetworkInfo();
        return activeNetInfo != null && activeNetInfo.isConnected();
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
            case R.id.action_share:
                StringBuilder linktoshare = new StringBuilder();
                for (TrailerVideo video : mTrailerList) {
                    linktoshare.append(video.getvideoUrl()).append("\n");
                }
                ShareCompat.IntentBuilder.from(MovieDetailActivity.this).setChooserTitle("Choose").setText(linktoshare.toString()).setType("text/plain").startChooser();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean saveFavoriteMovie() {

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
            Toast.makeText(this, movie.getMovieTitle() + getString(R.string.success_favorite_message), Toast.LENGTH_LONG).show();
            return true;
        } catch (SQLiteConstraintException e) {
            Toast.makeText(this, getString(R.string.failure_favorite_message) + movie.getMovieTitle(), Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private boolean deleteFavoriteMovie(){
        try {
            //Uri uri = Uri.parse(PopularMoviesContract.PopularMoviesEntry.CONTENT_URI);
            String itemtodelete = PopularMoviesContract.PopularMoviesEntry.COLUMN_MOVIE_ID + " = " + movie.getMovieId();
            getContentResolver().delete(PopularMoviesContract.PopularMoviesEntry.CONTENT_URI, itemtodelete, null);
            Toast.makeText(this, movie.getMovieTitle() + getString(R.string.success_unfavorite_message), Toast.LENGTH_LONG).show();
            return true;
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.failure_unfavorite_message) + movie.getMovieTitle(), Toast.LENGTH_LONG).show();
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
    }

    private void setFabResource(){
        if (inProviderMovies(movie.getMovieId())) {
            mFab.setImageResource(R.drawable.ic_action_favorited);
        } else {
            mFab.setImageResource(R.drawable.ic_action_favorite);
        }
    }

    private boolean inProviderMovies(int id) {
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(PopularMoviesContract.PopularMoviesEntry.CONTENT_URI, null, null, null, null);
        boolean isfavorite;
        if (cursor != null && cursor.getCount() > 0) {
            List<Integer> movieId = new ArrayList<>();
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                movieId.add(cursor.getInt(cursor.getColumnIndex(PopularMoviesContract.PopularMoviesEntry.COLUMN_MOVIE_ID)));
                cursor.moveToNext();
            }
            cursor.close();
            isfavorite = movieId.contains(id);
        } else {
            isfavorite = false;
        }
        return isfavorite;
    }

    private void getImage(){
        //Cursor cursor = getContentResolver().query(PopularMoviesContract.PopularMoviesEntry.CONTENT_URI, null, null, null, null);
        String itemtofetch = PopularMoviesContract.PopularMoviesEntry.COLUMN_MOVIE_ID + " = " + movie.getMovieId();
        Cursor cursor = getContentResolver().query(PopularMoviesContract.PopularMoviesEntry.CONTENT_URI, null, itemtofetch, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int indexposter = cursor.getColumnIndex(PopularMoviesContract.PopularMoviesEntry.COLUMN_POSTER);
                Bitmap btmapposter = BitmapFactory.decodeByteArray(cursor.getBlob(indexposter), 0, cursor.getBlob(indexposter).length);
                cursor.close();
                mMovieBackDrop.setImageBitmap(btmapposter);
            }

        }
    }
}
