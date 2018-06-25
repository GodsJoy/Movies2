package com.example.android.movies2;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.example.android.movies2.database.AppDatabase;

/**
 * Created by ayomide on 6/25/18.
 */
/*Reference: Android Architecture Components lesson*/
public class DetailsViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final AppDatabase mDb;
    private final String mMovieId;

    public DetailsViewModelFactory(AppDatabase db, String moviesId){
        this.mDb = db;
        this.mMovieId = moviesId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new DetailsViewModel(mDb, mMovieId);
    }
}
