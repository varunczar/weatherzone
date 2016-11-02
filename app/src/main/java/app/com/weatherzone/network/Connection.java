package app.com.weatherzone.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.com.weatherzone.utils.Constants;
import app.com.weatherzone.core.AppController;

/**
 * This class is responsible for connectivity to network data via the Volley Library
 * Author : Varun
 * Date : 2/11/2016.
 */
public class Connection {

    //Callback interface to return  a response to the calling activity
    private RestApiCallback restApiCallback;


    public void setRestApiCallback(RestApiCallback restApiCallback) {
        this.restApiCallback = restApiCallback;
    }

    /**
     * This method processes a get request and returns a response to the calling method
     * @param url - url to fetch data from
     * @param context - application context
     */
    public void getData(String url, final Context context) {
        //Create a get request that expects a String response
        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //Process the response when received by invoking the process method in the calling
                //interface
                restApiCallback.processResponse(response.toString());

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(Constants.TAG, "Error: " + error.getMessage());

                if (error instanceof NoConnectionError) {
                    //Process a connectivity error
                    restApiCallback.processErrorResponse(Constants.KEY_CONNECTION_ERROR,"No internet Access, Check your internet connection.");
                }
                else {
                    //Process the generic error returned
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                        String json = new String(response.data);
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(json);

                            if (jsonObject != null && jsonObject.has(Constants.KEY_MESSAGE)) {
                                restApiCallback.processErrorResponse(Constants.KEY_GENERIC_ERROR,
                                        jsonObject.getString(Constants.KEY_MESSAGE));
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    } else {
                        restApiCallback.processErrorResponse(Constants.KEY_GENERIC_ERROR,"Error processing request");
                    }
                }

            }
        }) ;
        //Add string request to the Volley Request queue for processing
        AppController.getInstance().addToRequestQueue(strReq,context);

    }

}