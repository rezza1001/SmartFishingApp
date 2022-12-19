package com.vma.smartfishingapp.libs;

import android.content.Context;

import com.vma.smartfishingapp.R;

/**
 * Created by Mochamad Rezza Gumilang on 29/03/2022
 */
public class VmaGlobalConfig {
    private static final String FORMAT_COORDINATE = "CoordinateFormat";
    public static final int FORMAT_COORDINATE_SECONDS = 3;
    public static final int FORMAT_COORDINATE_MINUTE = 2;
    public static final int FORMAT_COORDINATE_DEGREE = 1;


    public static void setCoordinateFormat(Context context, int format){
        VmaPreferences.save(context, FORMAT_COORDINATE, format);
    }

    public static int getCoordinateFormat(Context context){
        int format = VmaPreferences.getInt(context, FORMAT_COORDINATE);
        if (format == 0){
            return FORMAT_COORDINATE_DEGREE;
        }
        return VmaPreferences.getInt(context, FORMAT_COORDINATE);
    }

    public static String getCoordinateFormatName(Context context){
        int format = getCoordinateFormat(context);
        if (format == FORMAT_COORDINATE_SECONDS){
            return context.getResources().getString(R.string.decimal_seconds);
        }
        if (format == FORMAT_COORDINATE_MINUTE){
            return context.getResources().getString(R.string.decimal_minutes);
        }
        else {
            return context.getResources().getString(R.string.decimal_degree);
        }
    }
}
