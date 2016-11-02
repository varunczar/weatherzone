package app.com.weatherzone.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import app.com.weatherzone.utils.Constants;

/**
 * This class is responsible for initialising request queues for the Volley library for network operations
 * Author : Varun
 * Date : 2/11/2016.
 */

public class AppController {

    //Initialise a tag with a simple name version of the class
    public static final String TAG = AppController.class
            .getSimpleName();

    //Network request queue
    private RequestQueue requestQueue;

    //Instance of itself
    private static AppController instance;

    /**
     * This method creates a singleton instance of the AppController class
     * @return instance of AppController
     */
    public static synchronized AppController getInstance() {
        //Initialise a new AppController instance if null
        if(instance==null) {
            instance = new AppController();
        }
        return instance;
    }

    /**
     * This method creates a requestque instance
     * @param context
     * @return Request queue instance
     */
    public RequestQueue getRequestQueue(Context context) {
        //Initialise a new request Queue instance if null
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }

        return requestQueue;
    }


    /**
     * This method adds a network request instance to the request queue
     * @param req - request instance
     * @param context - Application context
     */
    public <T> void addToRequestQueue(Request<T> req,Context context) {
        req.setTag(TAG);
        //Set a retry policy for the Volley request
        req.setRetryPolicy(new DefaultRetryPolicy(
                //Time out in milliseconds
                60000,
                //Maximum retries - set to default 1
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                // exponential time set to socket for every retry attempt
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getRequestQueue(context).add(req);
    }

}