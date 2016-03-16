package com.example.daniel.popularmovies.Provider;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by Daniel on 28/10/2015.
 */
public interface FavoriteColumns {

    @DataType(DataType.Type.INTEGER) @PrimaryKey @AutoIncrement String _ID = "_id";

    @DataType(DataType.Type.TEXT) @NotNull String TITLE = "title";

    @DataType(DataType.Type.TEXT) @NotNull String POSTER_PATH = "poster_path";

    @DataType(DataType.Type.TEXT) @NotNull String RELEASE_DATE = "release_date";

    @DataType(DataType.Type.TEXT) @NotNull String VOTE_AVERAGE = "vote_average";

    @DataType(DataType.Type.TEXT) @NotNull String SYNOPSIS = "synopsis";
}
