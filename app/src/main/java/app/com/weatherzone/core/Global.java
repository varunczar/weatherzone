package app.com.weatherzone.core;

import android.app.Application;

import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * This application class intialises all global variables that can be accessed across the application.
 * Author : Varun
 * Date : 2/11/2016.
 */
public class Global extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //Initialising singleton Picasso instance to load images into cache when downloaded
        Picasso.Builder builder = new Picasso.Builder(this);
        //Creating a default cache directory
        builder.downloader(new OkHttpDownloader(this,Integer.MAX_VALUE));
        Picasso built = builder.build();
        Picasso.setSingletonInstance(built);

    }
}
