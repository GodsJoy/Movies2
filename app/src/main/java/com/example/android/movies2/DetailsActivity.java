package com.example.android.movies2;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.movies2.database.AppDatabase;
import com.example.android.movies2.database.MovieEntry;
import com.example.android.movies2.models.Movie;
import com.example.android.movies2.models.Review;
import com.example.android.movies2.utility.CreateMoviesFromResponseUtil;
import com.example.android.movies2.utility.NetworkUtil;
import com.squareup.picasso.Picasso;

import java.net.URL;

public class DetailsActivity extends AppCompatActivity implements
        LoaderCallbacks<String>,
        TrailersAdapter.TrailersAdapterOnClickListener{

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
    private Button mFavBtn;

    private boolean movieInFav = false;

    //Recyclerviews and adapters for trailers and reviews
    private RecyclerView mRecyclerViewTrailer;
    private RecyclerView mRecyclerViewReview;
    private TrailersAdapter mTrailerAdapter;
    private ReviewsAdapter mReviewAdapter;

    //database to insert movies into favourite database
    private AppDatabase mDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTitleTV = (TextView) findViewById(R.id.titleTV);
        mReleaseDateTV = (TextView) findViewById(R.id.releaseDateTV);
        mPosterIV = (ImageView) findViewById(R.id.posterIV);
        mVoteAVGTV = (TextView) findViewById(R.id.voteAVGTV);
        mSynopsisTV = (TextView) findViewById(R.id.synopsisTV);
        mFavBtn = (Button) findViewById(R.id.favBtn);

        //instantiate database
        mDb = AppDatabase.getsInstance(getApplicationContext());
        //get intent used to launch this class
        //Reference : SandWich project in this Phase
        Intent intent = getIntent();
        if(intent == null){
            //close app if intent is null
            closeAndReportError();
        }

        //get extra data from intent which is a Movie object
        final Movie m = intent.getParcelableExtra(EXTRA_POSITION);
        if(m == null){
            //close app if object is null
            closeAndReportError();
            return;
        }


        //populate activity_details using Movie data
        //Reference : SandWich project in this Phase
        populateDetails(m);
        setTitle(getString(R.string.details_name));

        //set onclicklistener for button to mark as favorite
        mFavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMarkButtonClicked(m);
            }
        });

        //check if this movie is already in the database.
        setAsFav(m.getId());

        //Prepare Review Recyclerview
        mRecyclerViewTrailer = (RecyclerView) findViewById(R.id.recyclerview_trailer);
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerViewTrailer.setLayoutManager(layoutManager);
        mRecyclerViewTrailer.setHasFixedSize(true);
        mTrailerAdapter = new TrailersAdapter(this, this);
        mRecyclerViewTrailer.setAdapter(mTrailerAdapter);

        //Prepare Reviews Recyclerview
        mRecyclerViewReview = (RecyclerView) findViewById(R.id.recyclerview_review);
        layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerViewReview.setLayoutManager(layoutManager);
        mRecyclerViewReview.setHasFixedSize(true);
        mReviewAdapter = new ReviewsAdapter(this);
        mRecyclerViewReview.setAdapter(mReviewAdapter);

        getOtherDetailsFromServer(m.getId(), getString(R.string.video));
        getOtherDetailsFromServer(m.getId(), getString(R.string.review));



    }

    //checks if movie has already been marked as favorite
    private void setAsFav(String movieId){
        DetailsViewModelFactory factory = new DetailsViewModelFactory(mDb, movieId);
        final DetailsViewModel viewModel = ViewModelProviders.of(this, factory).get(DetailsViewModel.class);
        viewModel.getMovie().observe(this, new Observer<MovieEntry>() {
            @Override
            public void onChanged(@Nullable MovieEntry movieEntry) {
                viewModel.getMovie().removeObserver(this);
                if(movieEntry != null){
                    movieInFav = true;
                    mFavBtn.setText(R.string.unfav);
                }

            }
        });
    }

    private void getOtherDetailsFromServer(String movie_id, String which){
        LoaderCallbacks<String> callback = DetailsActivity.this;

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

    //insert favorite movie into database if it is not already present
    //delete movie from database if it is already present
    public void onMarkButtonClicked(final Movie m){
        //String originalTitle, String posterPath, String synopsis, float rating, String releaseDate
        final MovieEntry movie = new MovieEntry(m.getId(), m.getOriginalTitle(),
                m.getposterPath(), m.getSynopsis(), m.getRating(),
                m.getReleaseDate());
        AppExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if(movieInFav){
                    mDb.movieDao().deleteMovie(movie);
                    mFavBtn.setText(R.string.fav);
                    movieInFav = false;
                }
                else{
                    mDb.movieDao().insertMovie(movie);
                    mFavBtn.setText(R.string.unfav);
                    movieInFav = true;
                }
            }
        });
    }

    //Reference : SandWich project in this Phase
    private void closeAndReportError(){
        finish();
        Toast.makeText(this, "Error encountered", Toast.LENGTH_SHORT);
    }

    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        //Log.d("DetailsBackground", "got here1");
        return new AsyncTaskLoader<String>(this) {
            String result = null;
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
            public String loadInBackground() {
                //Log.d("DetailsBackground", "got here");
                URL videos = NetworkUtil.buildVideoOrReviewUrl(args.getString("movie_id"), args.getString("which"));
                //URL reviews = NetworkUtil.buildVideoOrReviewUrl(args.getString("movie_id"), "reviews");
                //Log.d("DetailsBackground", videos.toString());
                try{
                    String result = NetworkUtil.getResponseFromHttpUrl(videos);
                    //String reviewsResult = NetworkUtil.getResponseFromHttpUrl(reviews);
                    //Log.d(args.getString("which")+"Result", result+"video");
                    Log.d("Result", args.getString("which")+result);
                    return result;
                }
                catch (Exception e){
                    //Log.d("VRhttp", "Error while get videos and reviews");
                    return null;
                }

            }

            public void deliverResult(String result){
                super.deliverResult(result);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        if(loader.getId() == VIDEO_LOADER_ID){
            mTrailerAdapter.setTrailerData(CreateMoviesFromResponseUtil.getEachTrailer(data));
        }
        else if(loader.getId() == REVIEW_LOADER_ID){
            mReviewAdapter.setReviewsData(CreateMoviesFromResponseUtil.getEachReview(data));
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    @Override
    public void onClickTrailer(String trailerKey) {
        String id = "http://www.youtube.com/watch?v="+trailerKey;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(id));
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
