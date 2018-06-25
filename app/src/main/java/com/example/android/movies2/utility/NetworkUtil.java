package com.example.android.movies2.utility;

import android.net.ParseException;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by ayomide on 5/19/18.
 */

public class NetworkUtil {
    final static String TMDB_BASE_URL = "http://api.themoviedb.org/3/movie/";

    final static String PARAM_QUERY = "api_key";

    //key obtained from TMDB. Please initialize key with correct key
    final static String key = "0c6de49f4f02c733fe6152ce2e0288b6";

    //builds url for using a specified sorted order, popular or top_rated
    //Reference : SunShine App in Grow with Google Phase 1
    public static URL buildUrl(String sortOrder){
        Uri builtUrl = Uri.parse(TMDB_BASE_URL+sortOrder).buildUpon()
                .appendQueryParameter(PARAM_QUERY, key)
                .build();

        URL url = null;
        try{
            url = new URL(builtUrl.toString());
        }
        catch (MalformedURLException e){
            Log.d("SortOrder", "Error while build url when sorted");
        }

        return url;
    }
    //which can be video or review
    public static URL buildVideoOrReviewUrl(String movie_id, String which){
        Uri builtUri = Uri.parse(TMDB_BASE_URL+movie_id+"/"+which).buildUpon()
                .appendQueryParameter(PARAM_QUERY, key)
                .build();

        URL url = null;
        try{
            url = new URL(builtUri.toString());
        }
        catch (MalformedURLException e){
            Log.d("VideoReviewURL", "Video url error");
        }
        return url;
    }

    //methods to get HTTP response
    //Reference : Sunshine app Grow with Google Phase 1
    public static String getResponseFromHttpUrl(URL url){
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) url.openConnection();
            InputStream input = con.getInputStream();
            Scanner scn = new Scanner(input);
            scn.useDelimiter("\\A");

            if(scn.hasNext()){
                return scn.next();
            }
            else{
                return null;
            }
        }
        catch (IOException e){
            return null;
        }
        finally {
            con.disconnect();
        }
    }

}
