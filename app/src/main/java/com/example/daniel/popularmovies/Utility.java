package com.example.daniel.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Daniel on 27/10/2015.
 */
public class Utility {

    public static final String DEV_KEY = "You shall not pass!!";

    private static final String URL_BASE = "http://api.themoviedb.org/3";
    private static final String SORT = "?sort_by=";
    private static String SORT_CRITERIA = "popularity.desc";
    private static final String API = "api_key=";
    private static final String API_KEY = "a1ff53c71d3eae87a2bbefb9e1c3885d";

    public static String updateSortCriteria(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SORT_CRITERIA = translateSortCriteria(prefs.getString(context.getString(R.string.pref_sortOrder_key), context.getString(R.string.pref_sortOrder_default)));
        return SORT_CRITERIA;
    }

    public static String getSortCriteria(){
        return SORT_CRITERIA;
    }

    private static String translateSortCriteria(String initialString){
        switch(initialString){
            case "Most popular":
                return "popularity.desc";
            case "Highest rated":
                return "vote_average.desc";
            case "Favorites":
                return "favorites";
            default:
                return "popularity.desc";
        }
    }

    public static String getReviewsUrl(String id){
        StringBuilder builder = new StringBuilder();
        builder.append(URL_BASE).append(getQueryType(1)).append("/"+id).append("/reviews").append("?"+API).append(API_KEY);
        return builder.toString();
    }

    public static String getTrailersUrl(String id){
        StringBuilder builder = new StringBuilder();
        builder.append(URL_BASE).append(getQueryType(1)).append("/"+id).append("/videos").append("?" + API).append(API_KEY);
        return builder.toString();
    }

    public static String getDiscoverUrl(){
        StringBuilder builder = new StringBuilder();
        builder.append(URL_BASE).append(getQueryType(0)).append(SORT).append(SORT_CRITERIA).append("&"+API).append(API_KEY);
        return builder.toString();
    }

    private static String getQueryType(int type){
        switch (type){
            case 0: return "/discover/movie";
            case 1: return "/movie";
            default: return "/discover/movie";
        }
    }
}
