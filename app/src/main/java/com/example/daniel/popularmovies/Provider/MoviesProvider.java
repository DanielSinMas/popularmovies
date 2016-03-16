package com.example.daniel.popularmovies.Provider;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by Daniel on 28/10/2015.
 */
@ContentProvider(authority = MoviesProvider.AUTHORITY, database = MoviesDatabase.class)
public class MoviesProvider {

    public static final String AUTHORITY = "com.example.daniel.popularmovies.MoviesProvider";

    interface Path{
        String FAVORITES = "favorites";
        String REVIEWS = "reviews";
        String VIDEOS = "videos";
    }

    @TableEndpoint(table = MoviesDatabase.FAVORITES) public static class Favorites{
        @ContentUri(
                path = Path.FAVORITES,
                type = "vnd.android.cursor.dir/favorite",
                defaultSort = FavoriteColumns.TITLE + "ASC"
        )

        public static final Uri FAVORITES_URI = Uri.parse("content://" + AUTHORITY + "/favorites");
    }

    @TableEndpoint(table = MoviesDatabase.REVIEWS) public static class Reviews{
        @ContentUri(
                path = Path.REVIEWS,
                type = "vnd.android.cursor.dir/review",
                defaultSort = ReviewColumns._ID + "ASC"
        )

        public static final Uri REVIEWS_URI = Uri.parse("content://" + AUTHORITY + "/reviews");
    }

    @TableEndpoint(table = MoviesDatabase.VIDEOS) public static class Videos{
        @ContentUri(
                path = Path.VIDEOS,
                type = "vnd.android.cursor.dir/video",
                defaultSort = VideoColumns._ID + "ASC"
        )

        public static final Uri VIDEOS_URI = Uri.parse("content://" + AUTHORITY + "/videos");
    }
}
