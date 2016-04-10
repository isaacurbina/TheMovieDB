package com.mobileappsco.training.mymovies.schematic;

import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by admin on 4/10/2016.
 */
public interface ReviewColumns {
    @DataType(DataType.Type.INTEGER) @PrimaryKey
    String _ID = "id";
    @DataType(DataType.Type.TEXT)
    String MID = "mid";
    @DataType(DataType.Type.TEXT)
    String CONTENT = "content";
    @DataType(DataType.Type.TEXT)
    String URL = "url";
}
