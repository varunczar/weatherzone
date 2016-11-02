package app.com.weatherzone.utils;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

/**
 * This utility class contains all utility methods required for common operations
 * Author : Varun
 * Date : 2/11/2016.
 */

public class Utils {

    //Pattern for incoming timestamp
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    //Pattern for formatted timestamp
    static SimpleDateFormat returnformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");


    /**
     * This method converts the utctime format to a human readable time format
     * @param String representation of utcTime
     * @return timestamp formatted in "dd-MM-yyyy HH:mm:ss" format
     * @throws Exception
     */
    public static String getTime(String utcTime) throws Exception
    {

        //Parse incoming utcTime
        Date intermediate = sdf.parse(utcTime);
        //Format and return human readable timestamp
        return returnformat.format(intermediate);
    }


}
