package com.example.android.movies2.utility;

import android.app.LoaderManager;
import android.util.Log;

import com.example.android.movies2.models.Movie;
import com.example.android.movies2.models.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ayomide on 5/24/18.
 * This class creates an array of movies from the JSON result from TMDB
 */

public class CreateMoviesFromResponseUtil {

    public static Movie[] getEachMovie(String moviesJSONStr) {
        try {
            if(moviesJSONStr != null) {
                //crete a JSONObject from the raw JSON result
                JSONObject movies = new JSONObject(moviesJSONStr);

                //get results field which describes each movie in a page
                JSONArray moviesResult = movies.getJSONArray("results");

                //create an array of movie with lenght of movies in moviesResult
                Movie[] allMovies = new Movie[moviesResult.length()];

                //information needed to retrieve from moviesResult
                String id = "";
                String originalTitle = "";
                String image_path = "";
                String synopsis = "";
                float rating = 0;
                String releaseDate = null;

                //loop through each item in moviesResult and extract movie details
                for (int i = 0; i < moviesResult.length(); i++) {
                    JSONObject amovie = moviesResult.getJSONObject(i);
                    originalTitle = amovie.getString(Movie.ORIGINAL_TITLE);
                    image_path = amovie.getString(Movie.POSTER_PATH);
                    synopsis = amovie.getString(Movie.OVERVIEW);
                    rating = Float.parseFloat(amovie.getString(Movie.VOTE_AVERAGE));
                    releaseDate = amovie.getString(Movie.RELEASE_DATE);
                    id = amovie.getString(Movie.MOVIE_ID);

                    //create a movie object from information extracted
                    allMovies[i] = new Movie(id,
                            originalTitle,
                            image_path,
                            synopsis,
                            rating,
                            releaseDate);
                    //Log.d("ID", allMovies[i].getId());

                }
                //return the array of movie created
                return allMovies;
            }
            return null;
        }
        catch (JSONException j){
            return null;
        }

    }

    public static String [] getEachTrailer(String trailerJSON){
        try {
            if (trailerJSON != null) {
                //crete a JSONObject from the raw JSON result
                JSONObject trailers = new JSONObject(trailerJSON);

                //get results field which describes each trailer in a page
                JSONArray trailersResult = trailers.getJSONArray("results");

                //create an array of trailerURLs with lenght of trailersResult
                String[] allTrailers = new String[trailersResult.length()];

                for(int i = 0; i < allTrailers.length; i++){
                    JSONObject amovie = trailersResult.getJSONObject(i);
                    allTrailers[i] = amovie.getString("key");
                }
                return allTrailers;
            }
            return null;
        }
        catch (JSONException j){
            return null;
        }
    }

    public static Review[] getEachReview(String reviewJSON){
        try {
            if (reviewJSON != null) {
                //crete a JSONObject from the raw JSON result
                JSONObject reviews = new JSONObject(reviewJSON);

                //get results field which describes each review in a page
                JSONArray reviewsResult = reviews.getJSONArray("results");

                //create an array of reviews with lenght of reviews in ReviewsResult
                Review[] allReviews = new Review[reviewsResult.length()];

                String author = "";
                String content = "";

                for(int i = 0; i < allReviews.length; i++){
                    JSONObject areview = reviewsResult.getJSONObject(i);
                    author = areview.getString("author");
                    content = areview.getString("content");
                    allReviews[i] = new Review(author, content);
                }
                return allReviews;
            }
            return null;
        }
        catch (JSONException j){
            return null;
        }
    }

}
