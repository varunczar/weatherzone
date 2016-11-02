package app.com.weatherzone.utils;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.List;

import app.com.weatherzone.R;
import app.com.weatherzone.core.MainActivity;
import app.com.weatherzone.network.Connection;
import app.com.weatherzone.network.RestApiCallback;
import app.com.weatherzone.pojo.APIResponse;
import app.com.weatherzone.pojo.Image;

/**
 * This is a Connection helper class that interacts with the Volley Connector and parses the response
 * received
 * Author : Varun
 * Date : 2/11/2016.
 */

public class ConnectionUtils implements RestApiCallback {

    //Network Connection instance
    private Connection connection;
    //Mainactivity instance
    private MainActivity mainActivity;
    //CallbackController instance that returns the control to the calling activity after processing
    //of data
    private CallbackController callbackController;
    //Response instance
    private APIResponse apiResponse;

    /**
     * This constructor initialises mandatory parameters
     * @param mainActivity - instance of main activity
     * @param callbackController - instance that returns the control to the calling activity after processing
     * @param apiResponse - instance of the response parsed and populated as required by the application
     */
    public ConnectionUtils(MainActivity mainActivity,
                           CallbackController callbackController,
                           APIResponse apiResponse)
    {
        //Create a new connection instance
        connection = new Connection();
        //Set it's callback implementation to the current class to handle data returned from the
        //api url
        connection.setRestApiCallback(this);
        this.mainActivity=mainActivity;
        this.callbackController=callbackController;
        this.apiResponse=apiResponse;
    }

    /**
     * This method fetches image data response off the API url via the connection instance
     * @param search - parameter to search
     * @param pageNumber - pageNumber to fetch data for
     */
    public void fetchAndProcessImageData(String search, int pageNumber)
    {
        try {
            Log.d(Constants.TAG,getClass().getName()+"fetchAndProcessImageData - Performing" +
                    "fetch operation for "+search+" and pageNumber "+pageNumber );
            //Construct the url by replacing placeholders for search term and page number
            String resultantURL = Constants.IMAGE_SEARCH_URL.replace(Constants.PLACEHOLDER_TERM,
                    //Encode the search term in UTF-8 to handle spaces in the search parameter
                    URLEncoder.encode(search, Constants.ENCODING_UTF8))
                    .replace(Constants.PLACEHOLDER_PAGENUMBER, String.valueOf(pageNumber))
                    .replace(Constants.PLACEHOLDER_CONSUMERKEY,String.valueOf(mainActivity.
                            getResources().getString(R.string.consumer_key)));
            //Initiate an API Request call
            connection.getData(resultantURL, mainActivity);
        }
        catch (Exception e)
        {
            //Process error response
            Log.d(Constants.TAG,getClass().getName()+"fetchAndProcessImageData - Unable to parse " +
                    "search" );
            processErrorResponse(Constants.KEY_GENERIC_ERROR,"Unable to parse search");
        }
    }

    /**
     * This method invokes the response processior Asynctask to parse the JSON Response and construct
     * the APIResponse object as needed by the display mechanism of the application
     * @param response - JSON Response
     */
    @Override
    public void processResponse(String response)
    {
        //Invoke the Response Processor
        Log.d(Constants.TAG,getClass().getName()+"processResponse - Performing" +
                "parse and populate by invoking ResponseProcessor" );
        new ResponseProcessor().execute(response);
    }

    /**
     * This method processes the error response if received
     * @param key - type of error
     * @param response - error response
     */
    @Override
    public void processErrorResponse(int key,String response)
    {
        //If Network error, set the lost connectivity flag to true
        if(Constants.KEY_CONNECTION_ERROR==key) {
            Log.d(Constants.TAG,getClass().getName()+"processErrorResponse - Connection lost" );
            mainActivity.setLostConnectivityFlag(true);
        }
        //Show the error message to the user
        Toast.makeText(mainActivity,response,Toast.LENGTH_SHORT).show();
    }

    /***
     * This class is an AsyncTask that parses and processes the response received from the API
     * Request call. This enables all heavy processing to be executed in the background thereby
     * avoiding Application freezing if heavy tasks run on the UI thread
     */
    private class ResponseProcessor extends AsyncTask<String,Void,APIResponse>
    {
        /**
         * This method parses and processes the API response in the background and returns a
         * processed object containing the pageNumber, total number of pages and a list of image
         * objects
         * @param params  - JSON response returned by the API
         * @return APIResponse object containing the pageNumber, total number of pages and a list of image
         * objects
         */
        protected APIResponse doInBackground(String... params)
        {
            String response=params[0];
            try {
                //Create a new apiResponse Instance if null
                Log.d(Constants.TAG,getClass().getName()+"doInBackground - Parsing received data" );
                if (apiResponse == null) {
                    apiResponse = new APIResponse();

                }
                List<Image> imageList = apiResponse.getImages();
                //Parse the json response
                JSONObject jsonObject = new JSONObject(response);
                //Extract current page number
                if(jsonObject.has(Constants.KEY_CURRENT_PAGE))
                {
                    apiResponse.setCurrentPageNumber(jsonObject.getInt(Constants.KEY_CURRENT_PAGE));
                }
                //Extract total number of pages
                if(jsonObject.has(Constants.KEY_TOTAL_PAGES)) {
                    apiResponse.setTotalPages(jsonObject.getInt(Constants.KEY_TOTAL_PAGES));
                }
                //Extract photo details
                if(jsonObject.has(Constants.KEY_PHOTOS)) {
                    JSONArray jsonArray = new JSONArray(jsonObject.getString(Constants.KEY_PHOTOS));
                    int length = jsonArray.length();


                    for (int index = 0; index < length; index++) {
                        JSONObject imageObject = jsonArray.getJSONObject(index);
                        Image image = new Image();
                        //Extract Name of the photo
                        if(imageObject.has(Constants.KEY_NAME)) {
                            image.setTitle(imageObject.getString(Constants.KEY_NAME));
                        }
                        //Extract creation time
                        if(imageObject.has(Constants.KEY_CREATED_AT)) {
                            image.setCreationTime(Utils.getTime(imageObject.getString(Constants.KEY_CREATED_AT)).toString());
                        }
                        //Extract images
                        if(imageObject.has(Constants.KEY_IMAGES)) {
                            JSONArray imageArray = new JSONArray(imageObject.getString(Constants.KEY_IMAGES));
                            if (imageArray.length() == 2) {
                                JSONObject thumbnailUrl = imageArray.getJSONObject(0);
                                //Extract thumbnail
                                if(thumbnailUrl.has(Constants.KEY_URL)) {
                                    image.setThumbnailURL(thumbnailUrl.getString(Constants.KEY_URL));
                                }
                                JSONObject imageUrl = imageArray.getJSONObject(1);
                                //Extract bigger sized image for display
                                if(imageUrl.has(Constants.KEY_URL)) {
                                    image.setImageURL(imageUrl.getString(Constants.KEY_URL));
                                }
                            }
                        }
                        //Extract user information
                        if(imageObject.has(Constants.KEY_USER)) {
                            JSONObject userObject = new JSONObject(imageObject.getString(Constants.KEY_USER));
                            if(userObject.has(Constants.KEY_FULLNAME))
                            {
                                image.setFullName(userObject.getString(Constants.KEY_FULLNAME));
                            }
                            if(userObject.has(Constants.KEY_COUNTRY))
                            {
                                image.setCountry(userObject.getString(Constants.KEY_COUNTRY));
                            }

                        }
                        //Add processed image to the imagelist
                        imageList.add(image);

                    }
                }
                Log.d(Constants.TAG,getClass().getName()+"doInBackground - Done parsing received data" );
            }
            catch(Exception e)
            {
                //Process failed to parse error
                Log.d(Constants.TAG,getClass().getName()+"doInBackground - Failed to parse data" );
                processErrorResponse(Constants.KEY_GENERIC_ERROR,"Failed to parse");
            }
            return apiResponse;
        }

        /**
         * This method is executed on the UI thread and passes on the processed apiResponse instance
         * to the calling activity
         * @param apiResponse
         */
        protected void onPostExecute(APIResponse apiResponse)
        {
            //Call the callbackController to process the apiResponse instance
            Log.d(Constants.TAG,getClass().getName()+"onPostExecute - calling callbackController " +
                    "to process the apiResponse instance" );
            callbackController.processImageData(apiResponse);
        }

    }
}
