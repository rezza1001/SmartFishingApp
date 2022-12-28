package com.vma.smartfishingapp.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.vma.smartfishingapp.dom.VmaConstants;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Mochamad Rezza Gumilang on 10/02/2022
 */
public class TimerService extends Service {

    private LocationManager locationManager;

    private Timer timer;
    int second = 0;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                second++;
                buildLocation();
                if (second % 10 == 0) {
                    Intent intent = new Intent(VmaConstants.VMA_TIMER_TASK);
                    intent.putExtra(VmaConstants.SERVICE_DATA, 10);
                    sendBroadcast(intent);
                }
                if (second % 30 == 0) {
                    Intent intent = new Intent(VmaConstants.VMA_TIMER_TASK);
                    intent.putExtra(VmaConstants.SERVICE_DATA, 30);
                    sendBroadcast(intent);
                }
                if (second % 60 == 0) {
                    Intent intent = new Intent(VmaConstants.VMA_TIMER_TASK);
                    intent.putExtra(VmaConstants.SERVICE_DATA, 60);
                    sendBroadcast(intent);

                    second = 0;
                }

            }
        }, 0, 1000);
    }


    @Override
    public void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }

    private void buildLocation() {
        Location location = getLastKnownLocation();
        if (location == null){
            Log.e("GPS_SERVICE","LOCATION NULL");
            return;
        }

        Log.d("GPS_SERVICE","location "+ location.getLatitude()+", "+location.getLongitude());
    }


    private Location getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        locationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        Log.d("GPS_SERVICE","providers "+providers.size());
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);

            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        return bestLocation;
    }


}
