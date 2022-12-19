package com.vma.smartfishingapp.libs;

import android.content.Context;
import android.location.Location;
import android.util.Log;

/**
 * Created by Mochamad Rezza Gumilang on 30/03/2022
 */
public class LocationConverter {
    private static final String TAG = "LocationConverter";

    public enum Type {LONGITUDE, LATITUDE}

    String mLongitude;
    String mLatitude;

    public LocationConverter(String longitude, String latitude){
        mLongitude = longitude;
        mLatitude = latitude;
    }
    public LocationConverter(double longitude, double latitude){
        mLongitude = longitude+"";
        mLatitude = latitude+"";
    }

    public String getDisplayDegree(Type type){
        double longitude = Double.parseDouble(mLongitude);
        double latitude = Double.parseDouble(mLatitude);

        String strLongitude = Location.convert(longitude, Location.FORMAT_DEGREES);
        String strLatitude = Location.convert(latitude, Location.FORMAT_DEGREES);
        if (type == Type.LONGITUDE){
            return strLongitude;
        }
        else {
            return strLatitude;
        }
    }

    public String getDisplayMinute(Type type){
        double longitude = Double.parseDouble(mLongitude);
        double latitude = Double.parseDouble(mLatitude);

        String strLongitude = Location.convert(longitude, Location.FORMAT_MINUTES);
        String strLatitude = Location.convert(latitude, Location.FORMAT_MINUTES);
        if (type == Type.LONGITUDE){
            return strLongitude;
        }
        else {
            return strLatitude;
        }
    }

    public String getDisplaySecond(Type type){
        double longitude = Double.parseDouble(mLongitude);
        double latitude = Double.parseDouble(mLatitude);

        String strLongitude = Location.convert(longitude, Location.FORMAT_SECONDS);
        String strLatitude = Location.convert(latitude, Location.FORMAT_SECONDS);
        if (type == Type.LONGITUDE){
            return strLongitude;
        }
        else {
            return strLatitude;
        }
    }

    public double getLongitude(){
        return Double.parseDouble(mLongitude);
    }
    public double getLatitude(){
        return Double.parseDouble(mLatitude);
    }

    public String getLongitudeDisplay(Context context){
        int format = VmaGlobalConfig.getCoordinateFormat(context);
        Log.d(TAG,"getLongitudeDisplay format "+format);
        if (format == VmaGlobalConfig.FORMAT_COORDINATE_DEGREE){
            return getDisplayDegree(Type.LONGITUDE);
        }
        else  if (format == VmaGlobalConfig.FORMAT_COORDINATE_MINUTE){
            return getDisplayMinute(Type.LONGITUDE);
        }
        else
            return getDisplaySecond(Type.LONGITUDE);
    }

    public String getLatitudeDisplay(Context context){
        int format = VmaGlobalConfig.getCoordinateFormat(context);
        Log.d(TAG,"getLatitudeDisplay format "+format);
        if (format == VmaGlobalConfig.FORMAT_COORDINATE_DEGREE){
            return getDisplayDegree(Type.LATITUDE);
        }
        else  if (format == VmaGlobalConfig.FORMAT_COORDINATE_MINUTE){
            return getDisplayMinute(Type.LATITUDE);
        }
        else
            return getDisplaySecond(Type.LATITUDE);
    }
}
