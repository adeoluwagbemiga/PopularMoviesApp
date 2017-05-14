package com.adeoluwa.android.popularmoviesapp;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.net.URL;
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
            Picasso.with(context)
                    .load(movie.getPosterUrl())
                    .placeholder(R.mipmap.futurestudio_logo_transparent)
                    .error(R.mipmap.futurestudio_logo_transparent)
                    .resize(185, 200).into(poster);
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
    }
}
