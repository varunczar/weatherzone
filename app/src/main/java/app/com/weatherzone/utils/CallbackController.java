package app.com.weatherzone.utils;

import app.com.weatherzone.pojo.APIResponse;

/**
 * This interface manages processing on image data when received successfully
 * Author : Varun
 * Date : 2/11/2016.
 */
public interface CallbackController {

    void processImageData(APIResponse apiResponse);
}
