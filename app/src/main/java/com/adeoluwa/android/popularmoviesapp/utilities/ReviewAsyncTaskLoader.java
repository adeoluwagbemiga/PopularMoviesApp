package com.adeoluwa.android.popularmoviesapp.utilities;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.adeoluwa.android.popularmoviesapp.Movie;
import com.adeoluwa.android.popularmoviesapp.NetworkUtils;
import com.adeoluwa.android.popularmoviesapp.Review;

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

public class ReviewAsyncTaskLoader extends AsyncTaskLoader<List<Review>> {

    private List<Review> mReviewList = new ArrayList<Review>();
    private String mQueryString;
    Context mContext;

    public ReviewAsyncTaskLoader(Context context, String queryString) {
        super(context);
        mQueryString = queryString;
        mContext = context;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        mReviewList.clear();
        forceLoad();
    }

    @Override
    public List<Review> loadInBackground() {
        String result = "";
        try {
            URL url = NetworkUtils.buildReviewUrl(mQueryString, mContext);
            result = NetworkUtils.getResponseFromHttpUrl(url);

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            //if(result == null || result.isEmpty()) return (List<Review>) new JSONObject();
            JSONObject jsonObject = new JSONObject(result);

            JSONArray resultsArray = jsonObject.getJSONArray("results");
            for(int i = 0; i < resultsArray.length(); i++)
            {
                JSONObject review = resultsArray.getJSONObject(i);
                //String movieTitle = review.getString("original_title");
                String movieReview = review.getString("content");
                String movieReviewer = review.getString("author");
                String movieId = review.getString("id");

                mReviewList.add(new Review(movieId, movieReview, movieReviewer));
            }
            return mReviewList;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mReviewList;
    }

    @Override
    public void deliverResult(List<Review> data) {
        super.deliverResult(data);
        mReviewList = data;
    }
}
