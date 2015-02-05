package edu.rosehulman.breadcrumb;

import android.text.TextUtils;

/**
 * Created by watterlm on 1/23/2015.
 */
public class Constants {
    public static class constants{
        public static final String DATABASE_NAME = "Breadcrumb.db";
        public static final int DATABASE_VERSION = 1;
        public static final String LOG_NAME = "Breadcrumb";
        public static final int MAP_ZOOM =  20;
        private static final String ARRAY_DIVIDER = ",";

        public static String serialize(String[] content) {
            return TextUtils.join(ARRAY_DIVIDER, content);
        }

        public static String[] deserialize(String content) {
            return content.split(ARRAY_DIVIDER);
        }
    }
}
