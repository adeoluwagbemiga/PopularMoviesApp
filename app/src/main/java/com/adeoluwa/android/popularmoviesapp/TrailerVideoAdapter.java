package com.adeoluwa.android.popularmoviesapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Merlyne on 5/12/2017.
 */

public class TrailerVideoAdapter extends RecyclerView.Adapter<TrailerVideoAdapter.TrailerVideoViewHolder> {
    final private ListItemClickListener mListClickListener;
    private List<TrailerVideo> list;
    private Context context;

    private final String THUMBNAIL_BASE_URL = "https://img.youtube.com/vi/";
    private final String THUMBNAIL_END_URL = "/hqdefault.jpg";

    public interface ListItemClickListener{
        void onItemClick(int position);
    }
    public TrailerVideoAdapter(List list, TrailerVideoAdapter.ListItemClickListener listener){
        this.list = list;
        mListClickListener = listener;
    }
    @Override
    public TrailerVideoAdapter.TrailerVideoViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        int layoutForListItem = R.layout.movie_trailer_item;
        boolean attachImmediatelyToParent = false;

        View view = inflater.inflate(layoutForListItem, viewGroup, attachImmediatelyToParent);
        TrailerVideoAdapter.TrailerVideoViewHolder viewHolder = new TrailerVideoAdapter.TrailerVideoViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TrailerVideoAdapter.TrailerVideoViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (null == list) return 0;
        return list.size();
    }

    public class TrailerVideoViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        @BindView(R.id.movie_trailer_name)TextView mName;
        @BindView(R.id.movie_trailer_type)TextView mVideoType;
        @BindView(R.id.iv_movie_poster)ImageView mMovieVideoPoster;


        public TrailerVideoViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        void bind(int index){
            TrailerVideo trailerVideo = list.get(index);
            mName.setText(trailerVideo.getTrailerName());
            mVideoType.setText(trailerVideo.getTrailerType());
            String url = THUMBNAIL_BASE_URL + trailerVideo.getTrailerKey() + THUMBNAIL_END_URL;
            Picasso.with(context)
                    .load(url)
                    .placeholder(R.mipmap.futurestudio_logo_transparent)
                    .error(R.mipmap.futurestudio_logo_transparent).into(mMovieVideoPoster);
        }

        @Override
        public void onClick(View v) {
            int itemClickedIndex = getAdapterPosition();
            mListClickListener.onItemClick(itemClickedIndex);
        }
    }
}