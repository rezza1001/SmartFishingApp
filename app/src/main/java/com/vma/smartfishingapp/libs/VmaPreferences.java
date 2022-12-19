package com.vma.smartfishingapp.libs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

//  VMA Configuration
public class VmaPreferences {
    public final String LOGLEVEL = "llevel";
    public final String LOGTO = "logto";
    public final String LOGMSG = "lmsg";
    public final String DELDB = "deldb";
    public final String FACTORYSET = "factory";
    public final String ZONE_INFO = "zoneinfo";

    public int logto;
    public int logmsg;
    public int  deleteDB;               //  Delete Database(Warning!!!)
    public int factoySet;

    //  From config File
    private final String ASSETS_GPSPORT     = "gpsport";
    private final String ASSETS_RADIOPORT   = "radioport";
    private final String ASSETS_TIMEZONE    = "timezone";
    private final String ASSETS_RADIOVER    = "radiover";
    private final String ASSETS_MAPVER      = "mapver";
    private final String ASSETS_HWVER       = "hwver";
    private final String ASSETS_INTERVAL    = "interval";
    public String gpsPort;
    public String radioPort;
    public int timezone;                //  timezone
    public int interval;                //  saving interval in gps info

    //  Preferences Config
    private final String PREF_AUTOBRIGHT = "autobright";
    private final String PREF_BRIGHT    = "bright";
    private final String PREF_TEMA      = "daynighttema";
    private final String PREF_SHOWBAR   = "showbar";
    private final String PREF_POSFORMAT = "posformat";
    private final String PREF_SID       = "sid";
    private final String PREF_SOWNER    = "sowner";
    private final String PREF_SIPI      = "sipi";
    private final String PREF_TON       = "grosston";
    private final String PREF_TOOLS     = "tools";
    private final String PREF_SHARELOC  = "shareloc";
    private final String PREF_REGIST    = "regist";
    private final String PREF_BTNTICK   = "btntick";
    private final String PREF_ADCTICK   = "adctick";
    private final String PREF_ADCVER    = "adcver";
    private final String PREF_UARTTICK  = "uarttick";
    private final String PREF_ADDEULA   = "eula";
    private final String PREF_ADDNAVI   = "navi";
    private final String PREF_ADDICON   = "navicon";

    //  Preferences Config
    private final String STATUS_RSSI    = "rssi";       //  Radio Signal SSID( -70>5, -77>4, -83>3, -88>2, -101>1, -101<0(1bar toggle)
    private final String STATUS_RADIO   = "radio";      //  Radio Connect Status(1:Connect, 0:not connect)
    private final String STATUS_BLE     = "ble";        //  ble status(1:connect, 0:disconnect, 2:paring, 3 Disable)
    private final String STATUS_PORT    = "port";       //  Port Status( 0:inport, 1:outport)
    private final String STATUS_GPS     = "gps";        //  (2:fail, 1: valid, 0:Invalid)
    private final String STATUS_BLEVEL  = "level";      //  battery level(0~100)
    private final String STATUS_BINDEX  = "index";      //  battery level index(0~5) 0: <20, 1:<40, 2:<60, 3:<80, 4: <=100 5:not used(charging)
    private final String STATUS_DAYNIGHT= "daynight";   //  0:day, 1:night
    private final String STATUS_DATE    = "date";
    private final String STATUS_TIME    = "time";






    public static void save(Context context, String key, String value){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
        Log.d("VMA_PREF","save "+key+" = "+ value);
    }

    public static void save(Context context, String key, int value){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.apply();
        Log.d("VMA_PREF","save "+key+" = "+ value);
    }
    public static void save(Context context, String key, long value){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(key, value);
        editor.apply();
        Log.d("VMA_PREF","save "+key+" = "+ value);
    }
    public static void save(Context context, String key, float value){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(key, value);
        editor.apply();
        Log.d("VMA_PREF","save "+key+" = "+ value);
    }

    public static String get(Context context, String key){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String value = sharedPref.getString(key,"");
        Log.d("VMA_PREF","get "+key+" = "+ (value.isEmpty() ?"<<EMPTY>>": value) );
        return value;
    }
    public static int getInt(Context context, String key){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        int value = sharedPref.getInt(key,-1);
        Log.d("VMA_PREF","get "+key+" = "+value);
        return value;
    }
    public static long getLong(Context context, String key){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        long value = sharedPref.getLong(key,0);
        Log.d("VMA_PREF","get "+key+" = "+value);
        return value;
    }
    public static float getFloat(Context context, String key){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        float value = sharedPref.getFloat(key,0);
        Log.d("VMA_PREF","get "+key+" = "+value);
        return value;
    }

    public static void delete(Context context, String key){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPref.edit().remove(key).apply();
    }


}
