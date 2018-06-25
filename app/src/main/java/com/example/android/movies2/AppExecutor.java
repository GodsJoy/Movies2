package com.example.android.movies2;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by ayomide on 6/25/18.
 */
public class AppExecutor {

    private static final Object LOCK = new Object();
    private static AppExecutor sInstance;
    private final Executor diskIO;

    private AppExecutor(Executor diskIO){
        this.diskIO = diskIO;
    }

    public static AppExecutor getInstance(){
        if (sInstance == null){
            synchronized (LOCK){
                sInstance = new AppExecutor(Executors.newSingleThreadExecutor());
            }
        }
        return sInstance;
    }

    public Executor diskIO() {
        return diskIO;
    }
}
