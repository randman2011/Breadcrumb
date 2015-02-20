package edu.rosehulman.breadcrumb;

import android.text.TextUtils;

/**
 * Created by watterlm on 1/23/2015.
 */
public class Constants {
    public static final String DATABASE_NAME = "Breadcrumb.db";
    public static final int DATABASE_VERSION = 2;
    public static final String LOG_NAME = "Breadcrumb";
    public static final int MAP_ZOOM = 17;
    public static final int WORLD_PX_HEIGHT = 256;
    public static final int WORLD_PX_WIDTH = 256;
    public static final int MAP_LINE_WIDTH = 10;
    public static final double LN2 = 0.6931471805599453;
    private static final String ARRAY_DIVIDER = ",";

    public static String serialize(String[] content) {
        return TextUtils.join(ARRAY_DIVIDER, content);
    }

    public static String[] deserialize(String content) {
        return content.split(ARRAY_DIVIDER);
    }
}
