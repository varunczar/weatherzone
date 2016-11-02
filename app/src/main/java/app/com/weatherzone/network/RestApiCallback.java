package app.com.weatherzone.network;

/**
 * This interface manages success and error operations that follow network responses
 * Author : Varun
 * Date : 2/11/2016.
 */
public interface RestApiCallback {

    void processResponse(String response);
    void processErrorResponse(int key,String response);
}
