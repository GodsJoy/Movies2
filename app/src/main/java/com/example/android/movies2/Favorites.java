package com.example.android.movies2;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.android.movies2.database.AppDatabase;
import com.example.android.movies2.database.MovieEntry;
import com.example.android.movies2.models.Movie;

import java.util.List;

public class Favorites extends AppCompatActivity implements
        MoviesAdapter.MoviesAdapterOnClickHandler{

    //movie recyclerview and adapter
    private RecyclerView mRecyclerView;
    private MoviesAdapter mMovieAdapter;

    //database from which favorite movies are retrieved
    private AppDatabase mDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        setTitle(R.string.favorites);

        mRecyclerView = findViewById(R.id.recyclerview_movie_fav);
        RecyclerView.LayoutManager layoutManager =
                new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(layoutManager);

        //Log.d("MainActivity", "got here1");
        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MoviesAdapter(this, this);

        mRecyclerView.setAdapter(mMovieAdapter);

        mDb = AppDatabase.getsInstance(getApplicationContext());


        processFavMoviesViewModel();

    }

    //processes viewmodel and update adapter with movies retrieved from database
    /*Reference: Android Architecture Components lesson*/
    private void processFavMoviesViewModel(){
        FavMoviesViewModel viewModel = ViewModelProviders.of(this).get(FavMoviesViewModel.class);
        viewModel.getMovies().observe(this, new Observer<List<MovieEntry>>() {
            @Override
            public void onChanged(@Nullable List<MovieEntry> movieEntries) {
                mMovieAdapter.setMoviesData(convertEntryToMovie(movieEntries));
            }
        });
    }

    private Movie[] convertEntryToMovie(List<MovieEntry> movies){
        Movie [] favMovies = new Movie[movies.size()];
        int i = 0;
        for(MovieEntry e : movies){
            favMovies[i] = new Movie(e.getId(),e.getOriginalTitle(),
                    e.getPosterPath(),e.getSynopsis(),e.getRating(),
                    e.getReleaseDate());
            i++;
        }
        return favMovies;
    }

    @Override
    public void onClick(Movie clickedMovie){
        Intent intent = new Intent(Favorites.this, DetailsActivity.class);
        intent.putExtra(DetailsActivity.EXTRA_POSITION, clickedMovie);
        startActivity(intent);
    }
}
