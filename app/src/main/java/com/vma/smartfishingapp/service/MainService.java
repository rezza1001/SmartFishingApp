package com.vma.smartfishingapp.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.vma.smartfishingapp.dom.VMA_COMMAND;
import com.vma.smartfishingapp.dom.VmaApiConstant;
import com.vma.smartfishingapp.dom.VmaConstants;
import com.vma.smartfishingapp.libs.VmaPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainService extends Service {
    private static final String TAG = "MainService";

    private Location lastLocation = null;
    public static boolean permission = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (permission){
            startGPSRequestLocation();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"onStartCommand");
//        onTaskRemoved(intent);
//        Toast.makeText(getApplicationContext(),"This is a Service running in Background", Toast.LENGTH_SHORT).show();
//        super.onStartCommand(intent, flags, startId);

        return  START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"Stopped");
    }

    public void startGPSRequestLocation(){
        LocationService  locationService = new LocationService(getApplicationContext());
        locationService.create();
        locationService.setOnLocationChangeListener(new LocationService.OnLocationChangeListener() {
            @Override
            public void onChange(Location lastLocation, List<Location> locations) {
                sendLocationUpdate(lastLocation, true);
            }

            @Override
            public void onChangeStatus(boolean available) {
                sendLocationUpdate(lastLocation, available);
            }
        });
    }


    private void sendLocationUpdate(Location location, boolean status){
        lastLocation = location;
        if (lastLocation == null){
            Log.e(TAG,"LOCATION NULL OR NOT FOUND");
            return;
        }
        JSONObject jo = new JSONObject();
        try {
            jo.put(VmaApiConstant.RF_ITEM_CMD, VMA_COMMAND.GNRMC.getValue());
            jo.put(VmaApiConstant.GPS_ITEM_LON, lastLocation.getLongitude());
            jo.put(VmaApiConstant.GPS_ITEM_LAT, lastLocation.getLatitude());

            int speedKm = 0;
            if (lastLocation.hasSpeed()){
                speedKm =(int) ((location.getSpeed()*3600)/1000); // KM/H
            }
            double speedNm = speedKm/1.852;  // Nautical Mile (NM)

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(lastLocation.getTime());
            DateFormat format= new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", new Locale("id"));

            jo.put(VmaApiConstant.GPS_ITEM_STATUS, status);
            jo.put(VmaApiConstant.GPS_ITEM_SPEED, Math.floor(speedNm));
            if (!status){
                jo.put(VmaApiConstant.GPS_ITEM_SPEED, 0);
            }
            jo.put(VmaApiConstant.GPS_ITEM_DATE, format.format(lastLocation.getTime()));
            jo.put(VmaApiConstant.GPS_ITEM_BEARING,0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                jo.put(VmaApiConstant.GPS_ITEM_BEARING, location.getBearingAccuracyDegrees());
            }

            Log.d(TAG,"DATA GPS "+jo);
        }catch (JSONException e){
            e.printStackTrace();
        }
        VmaPreferences.save(getApplicationContext(),VmaApiConstant.GPS_LSAT_DATA, jo.toString()); // SAVE LAST LOCATION

        Intent intent = new Intent(VmaConstants.VMA_GPS);
        intent.putExtra(VmaConstants.SERVICE_DATA, jo.toString());
        sendBroadcast(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartServiceIntent = new Intent(getApplicationContext(),this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        startService(restartServiceIntent);
//        super.onTaskRemoved(rootIntent);
    }


}
