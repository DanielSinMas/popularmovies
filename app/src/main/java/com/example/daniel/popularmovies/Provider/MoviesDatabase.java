package com.example.daniel.popularmovies.Provider;

import android.provider.MediaStore;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by Daniel on 28/10/2015.
 */
@Database(version = MoviesDatabase.VERSION)
public final class MoviesDatabase {

    public static final int VERSION = 1;

    @Table(FavoriteColumns.class) public static final String FAVORITES = "favorites";

    @Table(ReviewColumns.class) public static final String REVIEWS = "reviews";

    @Table(VideoColumns.class) public static final String VIDEOS = "videos";
}
