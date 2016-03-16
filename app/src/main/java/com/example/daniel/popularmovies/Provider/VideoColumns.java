package com.example.daniel.popularmovies.Provider;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by Daniel on 11/11/2015.
 */
public interface VideoColumns {

    @DataType(DataType.Type.INTEGER) @PrimaryKey String _ID = "_id";

    @DataType(DataType.Type.TEXT) @NotNull String MOVIE_ID = "movie_id";

    @DataType(DataType.Type.TEXT) @NotNull String KEY = "key";

    @DataType(DataType.Type.TEXT) @NotNull String NAME = "name";

}
