package com.example.daniel.popularmovies;

import java.io.Serializable;

/**
 * Created by Daniel on 11/11/2015.
 */
public class ReviewItem implements Serializable{

    private String id;
    private String movie_id;
    private String author;
    private String content;

    public ReviewItem(String id, String movie_id, String author, String content){
        this.id=id;
        this.movie_id=movie_id;
        this.author=author;
        this.content=content;
    }

    public String getId(){
        return id;
    }

    public String getMovie_id(){
        return movie_id;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent(){
        return content;
    }
}
