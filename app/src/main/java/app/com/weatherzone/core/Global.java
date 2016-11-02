package app.com.weatherzone.core;

import android.app.Application;
import android.util.Log;

import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import app.com.weatherzone.utils.Constants;

/**
 * This application class intialises all global variables that can be accessed across the application.
 * Author : Varun
 * Date : 2/11/2016.
 */
public class Global extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(Constants.TAG,"Initialising Picasso singleton instance");
        //Initialising singleton Picasso instance to load images into cache when downloaded
        Picasso.Builder builder = new Picasso.Builder(this);
        //Creating a default cache directory
        builder.downloader(new OkHttpDownloader(this,Integer.MAX_VALUE));
        Picasso built = builder.build();
        Picasso.setSingletonInstance(built);
        Log.d(Constants.TAG,"Done initialising Picasso singleton instance");

    }
}
