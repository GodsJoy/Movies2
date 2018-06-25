package com.example.android.movies2;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.example.android.movies2.database.AppDatabase;
import com.example.android.movies2.database.MovieEntry;

import java.util.List;

/**
 * Created by ayomide on 6/25/18.
 */
/*Reference: Android Architecture Components lesson*/
public class FavMoviesViewModel extends AndroidViewModel {

    private LiveData<List<MovieEntry>> movies;
    public FavMoviesViewModel(Application application){
        super(application);
        AppDatabase database = AppDatabase.getsInstance(this.getApplication());
        movies = database.movieDao().loadAllMovies();
    }

    public LiveData<List<MovieEntry>> getMovies() {
        return movies;
    }
}
