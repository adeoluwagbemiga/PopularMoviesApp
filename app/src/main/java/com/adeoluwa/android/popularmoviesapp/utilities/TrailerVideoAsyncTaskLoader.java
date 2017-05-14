package com.adeoluwa.android.popularmoviesapp.utilities;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.adeoluwa.android.popularmoviesapp.NetworkUtils;
import com.adeoluwa.android.popularmoviesapp.Review;
import com.adeoluwa.android.popularmoviesapp.TrailerVideo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Merlyne on 5/14/2017.
 */

public class TrailerVideoAsyncTaskLoader extends AsyncTaskLoader<List<TrailerVideo>> {
    private List<TrailerVideo> mTrailerList = new ArrayList<TrailerVideo>();
    private String mQueryString;
    Context mContext;

    public TrailerVideoAsyncTaskLoader(Context context, String queryString) {
        super(context);
        mQueryString = queryString;
        mContext = context;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        mTrailerList.clear();
        forceLoad();
    }

    @Override
    public List<TrailerVideo> loadInBackground() {
        String result = "";
        try {
            URL url = NetworkUtils.buildTrailerUrl(mQueryString, mContext);
            result = NetworkUtils.getResponseFromHttpUrl(url);

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if(result == null || result.isEmpty()) return (List<TrailerVideo>) new JSONObject();
            JSONObject jsonObject = new JSONObject(result);

            JSONArray resultsArray = jsonObject.getJSONArray("results");
            for(int i = 0; i < resultsArray.length(); i++)
            {
                JSONObject trailer = resultsArray.getJSONObject(i);
                String trailerName = trailer.getString("name");
                String trailerKey = trailer.getString("key");
                String trailerType = trailer.getString("type");
                String trailerId = trailer.getString("id");

                mTrailerList.add(new TrailerVideo(trailerId, trailerName, trailerKey, trailerType));
            }
            return mTrailerList;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mTrailerList;
    }

    @Override
    public void deliverResult(List<TrailerVideo> data) {
        super.deliverResult(data);
        mTrailerList = data;
    }
}
