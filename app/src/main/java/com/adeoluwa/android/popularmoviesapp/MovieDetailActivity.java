package com.adeoluwa.android.popularmoviesapp;

import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailActivity extends AppCompatActivity {

    @BindView(R.id.movie_title) TextView mOriginalTitle;
    @BindView(R.id.overview) TextView mOverview;
    @BindView(R.id.release_date) TextView mReleaseDate;
    @BindView(R.id.vote_average) TextView mVoteAverage;
    @BindView(R.id.movie_backdrop) ImageView mMovieBackDrop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        Movie movie = null;

        if(extras != null && extras.containsKey("movie"))
            movie =  intent.getParcelableExtra("movie");

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
    }
}
