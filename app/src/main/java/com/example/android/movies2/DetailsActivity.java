package com.example.android.movies2;

import android.content.Intent;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.movies2.models.Movie;
import com.example.android.movies2.utility.NetworkUtil;
import com.squareup.picasso.Picasso;

import java.net.URL;

public class DetailsActivity extends AppCompatActivity implements
        LoaderCallbacks<String []> {

    //used as tag for extra in intent
    public static final String EXTRA_POSITION = "extra_position";
    public static final int VIDEO_LOADER_ID = 1;
    public static final int REVIEW_LOADER_ID = 2;


    //Views to be populated in activity_details
    private TextView mTitleTV;
    private TextView mReleaseDateTV;
    private ImageView mPosterIV;
    private TextView mVoteAVGTV;
    private TextView mSynopsisTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mTitleTV = (TextView) findViewById(R.id.titleTV);
        mReleaseDateTV = (TextView) findViewById(R.id.releaseDateTV);
        mPosterIV = (ImageView) findViewById(R.id.posterIV);
        mVoteAVGTV = (TextView) findViewById(R.id.voteAVGTV);
        mSynopsisTV = (TextView) findViewById(R.id.synopsisTV);

        //get intent used to launch this class
        //Reference : SandWich project in this Phase
        Intent intent = getIntent();
        if(intent == null){
            //close app if intent is null
            closeAndReportError();
        }

        //get extra data from intent which is a Movie object
        Movie m = intent.getParcelableExtra(EXTRA_POSITION);
        if(m == null){
            //close app if object is null
            closeAndReportError();
            return;
        }

        getOtherDetailsFromServer(m.getId(), getString(R.string.video));
        getOtherDetailsFromServer(m.getId(), getString(R.string.review));

        //populate activity_details using Movie data
        //Reference : SandWich project in this Phase
        populateDetails(m);
        setTitle(getString(R.string.details_name));
    }

    private void getOtherDetailsFromServer(String movie_id, String which){
        LoaderCallbacks<String[]> callback = DetailsActivity.this;

        Bundle loaderBundle = new Bundle();
        loaderBundle.putCharSequence("movie_id", movie_id);
        loaderBundle.putCharSequence("which", which);
        if(which == getString(R.string.video)) {
            getSupportLoaderManager().initLoader(VIDEO_LOADER_ID, loaderBundle, callback);
        }
        else{
            getSupportLoaderManager().initLoader(REVIEW_LOADER_ID, loaderBundle, callback);
        }
    }

    //method to populate UI
    private void populateDetails(Movie m){
        //Log.d("movieID", m.getId()+"noid");
        mTitleTV.setText(m.getOriginalTitle());
        mReleaseDateTV.setText(m.getReleaseDate().split("-")[0]);
        Picasso.with(this).load(Movie.IMAGE_PATH.concat(m.getposterPath())).into(mPosterIV);
        mVoteAVGTV.setText(m.getRating()+"/10");
        mSynopsisTV.setText(m.getSynopsis());
    }

    //Reference : SandWich project in this Phase
    private void closeAndReportError(){
        finish();
        Toast.makeText(this, "Error encountered", Toast.LENGTH_SHORT);
    }

    @Override
    public Loader<String[]> onCreateLoader(int id, final Bundle args) {
        Log.d("DetailsBackground", "got here1");
        return new AsyncTaskLoader<String[]>(this) {
            String [] result = null;
            @Override
            protected void onStartLoading() {
                if(result != null){
                    deliverResult(result);
                }
                else{
                    forceLoad();
                }
            }

            @Override
            public String[] loadInBackground() {
                //Log.d("DetailsBackground", "got here");
                URL videos = NetworkUtil.buildVideoOrReviewUrl(args.getString("movie_id"), args.getString("which"));
                //URL reviews = NetworkUtil.buildVideoOrReviewUrl(args.getString("movie_id"), "reviews");
                //Log.d("DetailsBackground", videos.toString());
                try{
                    String videosResult = NetworkUtil.getResponseFromHttpUrl(videos);
                    //String reviewsResult = NetworkUtil.getResponseFromHttpUrl(reviews);
                    Log.d(args.getString("which")+"Result", videosResult+"video");
                    //Log.d("ReviewResult", reviewsResult+"review");
                    return new String[0];
                }
                catch (Exception e){
                    Log.d("VRhttp", "Error while get videos and reviews");
                    return null;
                }

            }

            public void deliverResult(String [] movies){
                super.deliverResult(movies);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] data) {
        //Log.d("Finish", "finish");
        Log.d("LoaderID", "ID: "+loader.getId());
    }

    @Override
    public void onLoaderReset(Loader<String[]> loader) {

    }
}
