package com.example.daniel.popularmovies;

/**
 * Created by Daniel on 11/11/2015.
 */
public class VideoItem {

    private String id;
    private String movie_id;
    private String key;
    private String name;

    public VideoItem(String id, String movie_id, String key, String name){
        this.id=id;
        this.movie_id=movie_id;
        this.key=key;
        this.name=name;
    }

    public String getId(){
        return id;
    }

    public String getKey(){
        return key;
    }

    public String getName(){
        return name;
    }

    public String getMovie_id(){
        return movie_id;
    }
}
