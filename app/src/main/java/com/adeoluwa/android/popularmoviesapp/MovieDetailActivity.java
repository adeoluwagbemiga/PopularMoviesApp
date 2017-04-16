package com.adeoluwa.android.popularmoviesapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {
    private TextView mOriginalTitle;
    private TextView mOverview;
    private TextView mReleaseDate;
    private TextView mVoteAverage;
    private ImageView mMovieBackDrop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        mOriginalTitle = (TextView) findViewById(R.id.movie_title);
        mOverview  = (TextView) findViewById(R.id.overview);
        mReleaseDate  = (TextView) findViewById(R.id.release_date);
        mVoteAverage  = (TextView) findViewById(R.id.vote_average);
        mMovieBackDrop  = (ImageView) findViewById(R.id.movie_backdrop);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        Movie movie = null;

        if(extras != null && extras.containsKey("movie"))
            movie =  intent.getParcelableExtra("movie");

        //setDetails(moveDetails);
        mOriginalTitle.setText(movie.getMovieTitle());
        mOverview.setText(movie.getOverview());

        mReleaseDate.setText(movie.getReleasedDate());
        String vote_average = String.valueOf(movie.getViewerRatings()) + "/10";
        mVoteAverage.setText(vote_average);

        Picasso.with(getBaseContext()).load(movie.getBackdropUrl()).resize(300, 400).into(mMovieBackDrop);
    }
}
