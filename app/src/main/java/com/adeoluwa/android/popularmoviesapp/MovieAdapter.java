package com.adeoluwa.android.popularmoviesapp;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.adeoluwa.android.popularmoviesapp.data.PopularMoviesContract;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Merlyne on 4/16/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    final private ListItemClickListener mListClickListener;
    private List<Movie> list;
    private Context context;
    private byte[] mMoviesPoster;


    public interface ListItemClickListener{
        void onItemClick(int position, byte[] movieposter);
    }
    public MovieAdapter(List list, ListItemClickListener listener){
        this.list = list;
        mListClickListener = listener;
    }
    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        int layoutForListItem = R.layout.movie_item;
        boolean attachImmediatelyToParent = false;

        View view = inflater.inflate(layoutForListItem, viewGroup, attachImmediatelyToParent);
        MovieViewHolder viewHolder = new MovieViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (null == list) return 0;
        return list.size();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        @BindView(R.id.rv_image) ImageView poster;

        public MovieViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        void bind(int index){
            Movie movie = list.get(index);

            if (inProviderMovies(movie.getMovieId())) {
                getImage(movie.getMovieId());
            }
            else {
                Picasso.with(context)
                        .load(movie.getPosterUrl())
                        .placeholder(R.mipmap.ic_launcher_2)
                        .error(R.mipmap.ic_launcher_2)
                        .resize(185, 200).into(poster);

            }

        }

        @Override
        public void onClick(View v) {
            int itemClickedIndex = getAdapterPosition();
            Bitmap bitmap = ((BitmapDrawable) poster.getDrawable()).getBitmap();
            ByteArrayOutputStream moviebytearray = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, moviebytearray);
            mMoviesPoster = moviebytearray.toByteArray();

            mListClickListener.onItemClick(itemClickedIndex, mMoviesPoster);
        }

        private void getImage(int movieId){
            //Cursor cursor = getContentResolver().query(PopularMoviesContract.PopularMoviesEntry.CONTENT_URI, null, null, null, null);
            String itemtofetch = PopularMoviesContract.PopularMoviesEntry.COLUMN_MOVIE_ID + " = " + movieId;
            Cursor cursor = context.getContentResolver().query(PopularMoviesContract.PopularMoviesEntry.CONTENT_URI, null, itemtofetch, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int indexposter = cursor.getColumnIndex(PopularMoviesContract.PopularMoviesEntry.COLUMN_POSTER);
                    Bitmap btmapposter = BitmapFactory.decodeByteArray(cursor.getBlob(indexposter), 0, cursor.getBlob(indexposter).length);
                    cursor.close();
                    poster.setImageBitmap(btmapposter);
                }

            }
        }

        private boolean inProviderMovies(int id) {
            ContentResolver resolver = context.getContentResolver();
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
    }

}
