package com.example.android.movies2;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.android.movies2.database.AppDatabase;
import com.example.android.movies2.database.MovieEntry;

/**
 * Created by ayomide on 6/25/18.
 */

public class DetailsViewModel extends ViewModel {
    private LiveData<MovieEntry> movie;

    public DetailsViewModel(AppDatabase db, String movieId){
        movie = db.movieDao().loadMovieById(movieId);
    }

    public LiveData<MovieEntry> getMovie() {
        return movie;
    }
}
