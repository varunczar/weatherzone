package app.com.weatherzone.utils;

import app.com.weatherzone.BuildConfig;

/**
 * This class contains constants used all through the application
 * Author : Varun
 * Date : 2/11/2016.
 */

public class Constants {

    public static final String TAG = "imagesearch";
    public static final String DEFAULT_SEARCH_PARAMETER = "weather";
    public static final String ENCODING_UTF8 = "UTF-8";


    public static final String IMAGE_SEARCH_URL="https://api.500px.com/v1/photos/search?term=TERM" +
            "&page=PAGENUMBER&image_size=3,6&consumer_key=CONSUMERKEY";

    public static final String PLACEHOLDER_TERM = "TERM";
    public static final String PLACEHOLDER_PAGENUMBER = "PAGENUMBER";
    public static final String PLACEHOLDER_CONSUMERKEY = "CONSUMERKEY";

    public static final int KEY_CONNECTION_ERROR = 0;
    public static final int KEY_GENERIC_ERROR = 1;
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_CURRENT_PAGE = "current_page";
    public static final String KEY_TOTAL_PAGES = "total_pages";
    public static final String KEY_PHOTOS = "photos";
    public static final String KEY_CREATED_AT = "created_at";
    public static final String KEY_IMAGES = "images";
    public static final String KEY_URL = "url";
    public static final String KEY_NAME = "name";
    public static final String KEY_USER = "user";
    public static final String KEY_FULLNAME = "fullname";
    public static final String KEY_COUNTRY = "country";


}
