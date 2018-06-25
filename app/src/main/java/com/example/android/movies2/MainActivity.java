package com.example.android.movies2;

import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.support.v4.content.Loader;

import java.net.URL;

import com.example.android.movies2.DetailsActivity;
import com.example.android.movies2.MoviesAdapter;
import com.example.android.movies2.utility.NetworkUtil;
import com.example.android.movies2.utility.CreateMoviesFromResponseUtil;
import com.example.android.movies2.models.Movie;


public class MainActivity extends AppCompatActivity implements
        MoviesAdapter.MoviesAdapterOnClickHandler,
        LoaderCallbacks<Movie[]>{

    private Movie [] mMovies; //array of movies processed from TMDB result
    private TextView sortText;
    private TextView errorText;
    private Spinner spinner;

    private RecyclerView mRecyclerView;
    private MoviesAdapter mMovieAdapter;

    private static final int MOVIE_LOADER_ID = 0;


    //needed to prevent requesting result from TMDB by the spinner on launch of app
    private boolean sortOptionSelected = false;

    //store currently selected sort order to prevent requesting result from TMDB when the same order is selected
    private String currentlySelectedSortOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //currentlySelectedSortOrder = getResources().getStringArray(R.array.sort_options)[0];
        //spinner = (Sp
        // inner) findViewById(R.id.sort_spinner);
        //sortText = (TextView) findViewById(R.id.sort_options);
        errorText = (TextView) findViewById(R.id.errorTV);

        //spinner.setVisibility(View.INVISIBLE);
        //sortText.setVisibility(View.INVISIBLE);
        errorText.setVisibility(View.INVISIBLE);

        //Log.d("MainActivity", "got here0");
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movie);
        RecyclerView.LayoutManager layoutManager =
                new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(layoutManager);

        //Log.d("MainActivity", "got here1");
        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MoviesAdapter(this, this);

        mRecyclerView.setAdapter(mMovieAdapter);

        LoaderCallbacks<Movie[]> callback = MainActivity.this;

        Bundle loaderBundle = new Bundle();
        loaderBundle.putCharSequence("sort_order", getString(R.string.popular));

        getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, loaderBundle, callback);
        //Log.d("MainActivity", "got here3");
        //initial sort order is popular
        //searchMovies(getString(R.string.popular));

        //populate Spinner. It allows a user to change sort order
        //populateSpinner();

    }

    private void searchMovies(String sortOption){

        //build url needed to request data from TMDB

        Bundle loaderBundle = new Bundle();
        loaderBundle.putCharSequence("sort_order", sortOption);
        //process request in a background thread
        //new MoviesSearchTask().execute(movies);
        getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, loaderBundle, this);
        //Log.d("MainActivity", "got here4");
    }

    //Reference : Sunshine app Grow with Google Phase 1

    @Override
    public Loader<Movie[]> onCreateLoader(int id, final Bundle loaderArgs){
        return new AsyncTaskLoader<Movie[]>(this) {

            Movie [] mMovies = null;

            @Override
            protected void onStartLoading() {
                if(mMovies != null){
                    deliverResult(mMovies);
                }
                else{
                    errorText.setVisibility(View.VISIBLE);
                    forceLoad();
                }
                //super.onStartLoading();
            }

            @Override
            public Movie[] loadInBackground() {

                URL movies = NetworkUtil.buildUrl(loaderArgs.getString("sort_order"));
                //Log.d("loadINBackground", movies+" here1");

                try{
                    String result = NetworkUtil.getResponseFromHttpUrl(movies);
                    //return processed array of Movies
                    //Log.d("loadINBackground", result+"here");
                    return CreateMoviesFromResponseUtil.getEachMovie(result);
                }
                catch (Exception e){
                    e.printStackTrace();
                    //Log.d("loadINBackground", "failed to load data");
                    return null;
                }


            }

            public void deliverResult(Movie [] movies){
                mMovies = movies;
                super.deliverResult(movies);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Movie[]> loader, Movie[] data) {
        errorText.setVisibility(View.INVISIBLE);
        mMovieAdapter.setMoviesData(data);
        if(data == null){
            showErrorMessage();
        }
        else{
            showMovieDataView();
        }
    }

    @Override
    public void onLoaderReset(Loader<Movie[]> loader) {

    }


    @Override
    public void onClick(Movie clickedMovie){
        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        intent.putExtra(DetailsActivity.EXTRA_POSITION, clickedMovie);
        startActivity(intent);
    }

    private void showMovieDataView(){
        errorText.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage(){
        mRecyclerView.setVisibility(View.INVISIBLE);

        errorText.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort_option_menu, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.popular_menu){
            searchMovies(getString(R.string.popular));
            return true;
        }
        if(id == R.id.top_rated_menu){
            searchMovies(getString(R.string.top_rated));
            return true;
        }
        if (id == R.id.fav_menu){
            Intent intent = new Intent(MainActivity.this, Favorites.class);
            startActivity(intent);
        }


        return super.onOptionsItemSelected(item);
    }

}
