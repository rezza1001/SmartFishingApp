package com.vma.smartfishingapp.service;


import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

/**
 * Created by Mochamad Rezza Gumilang on 11/02/2022
 */

public class GpsService extends Service implements LocationListener {
    public static final int REQUEST_SETTING = 10001;

    // flag for GPS status
    boolean isGPSEnabled = false;
    boolean permissionGenreted = true;

    // flag for network status
    boolean isNetworkEnabled = false;

    boolean canGetLocation = false;
    Location location;

    double latitude; // latitude
    double longitude; // longitude
    String mAddress = "Lokasi tidak diketahui";

    private static final String TAG = "GpsService";

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 2; // 1 minute

    protected LocationManager locationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        getLocation();
    }

    public void getLocation() {
        Log.d(TAG, "Start getLocation");
        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    locationManager.registerGnssStatusCallback(new GnssStatus.Callback() {
                        @Override
                        public void onStarted() {
                            super.onStarted();
                            Log.d(TAG, "onStarted");
                        }

                        @Override
                        public void onStopped() {
                            super.onStopped();
                            sendBroadcast(0);
                        }

                        @Override
                        public void onFirstFix(int ttffMillis) {
                            super.onFirstFix(ttffMillis);
                            Log.d(TAG, "onFirstFix " + ttffMillis);
                        }

                        @Override
                        public void onSatelliteStatusChanged(@NonNull GnssStatus status) {
                            super.onSatelliteStatusChanged(status);

                            int satelliteCount = status.getSatelliteCount();
                            int usedSatellites = 0;
                            float totalSnr = 0;
                            for (int i = 0; i < satelliteCount; i++){
                                if (status.usedInFix(i)) {
                                    usedSatellites++;
                                    totalSnr += status.getCn0DbHz(i); //this method obtains the signal from each satellite
                                }
                            }

                            float avgSnr = (usedSatellites > 0) ? totalSnr / usedSatellites: 0.0f;
                            Log.d(TAG, "Total satelites  = " +satelliteCount+" --> Number used satelites: " + usedSatellites + " SNR: " + totalSnr+" avg SNR: "+avgSnr);
                            sendBroadcast(avgSnr);
                        }
                    });
                }
            }

            // Get Status GPS enable
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // Get Network Status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isNetworkEnabled && !isGPSEnabled) {
                forceOnGPS();
                Log.d(TAG, "Disabled isNetworkEnabled & isGPSEnabled");
            } else {
                Log.d(TAG, "isNetworkEnabled & isGPSEnabled is Enabled");
                this.canGetLocation = true;
                Log.d(TAG, "isNetworkEnabled : " + isNetworkEnabled);

                if (isNetworkEnabled) {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        permissionGenreted = false;
                    }
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            longitude = location.getLongitude();
                            latitude = location.getLatitude();
                        }
                    }
                }
                Log.d(TAG, "isGPSEnabled : " + isGPSEnabled);
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                longitude = location.getLongitude();
                                latitude = location.getLatitude();
                            }
                        }
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendBroadcast(float snr){


    }

    private void forceOnGPS() {
        new GpsUtils(getApplicationContext()).turnGPSOn(isGPSEnable -> {
            getLocation();
            if (onConnectionUPListener != null) {
                onConnectionUPListener.onUp();
            }
        });
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged : " + location.getLongitude());
        if (pListenr != null) {
            pListenr.onChange(location);
        }
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        Log.d(TAG, "onLocationChanged : " + locations.size());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        if (location != null) {
            Log.d(TAG, "onStatusChanged : " + provider);
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG, "onProviderEnabled : " + provider);
        if (onConnectionUPListener != null) {
            onConnectionUPListener.onUp();
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        if (onConnectionUPListener != null) {
            onConnectionUPListener.onDown();
        }
    }

    public boolean isPermissionGenreted() {
        permissionGenreted = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        return permissionGenreted;
    }

    public boolean statusGPS() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        if (locationManager != null) {
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        return isGPSEnabled;
    }


    LocationChangeListener pListenr;

    public void setLocationChangeListener(LocationChangeListener mListenr) {
        pListenr = mListenr;
    }


    public interface LocationChangeListener {
        public void onChange(Location location);
    }

    private OnConnectionUPListener onConnectionUPListener;
    public void setOnConnectionUPListener(OnConnectionUPListener pOnConnectionUPListener){
        onConnectionUPListener = pOnConnectionUPListener;
    }
    public interface OnConnectionUPListener{
        public void onUp();
        public void onDown();
    }
}
