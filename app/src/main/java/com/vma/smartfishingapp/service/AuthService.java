package com.vma.smartfishingapp.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.vma.smartfishingapp.api.ApiConfig;
import com.vma.smartfishingapp.api.PostManager;
import com.vma.smartfishingapp.database.table.AccountDB;
import com.vma.smartfishingapp.database.table.BlackBoxDB;
import com.vma.smartfishingapp.dom.VmaApiConstant;
import com.vma.smartfishingapp.libs.Utility;
import com.vma.smartfishingapp.libs.VmaPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

public class AuthService extends Service {

    private static final String TAG = "AuthService";
    private Timer timer;
    private AccountDB accountDB;
    private int menit = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"Starting");
        accountDB = new AccountDB();
        accountDB.loadData(getApplicationContext());
        startTimer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"Stopped");
        timer.cancel();
    }

    private void startTimer(){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                menit ++;
                sendLocationData();
            }
        },0,60000);
    }

    private void sendLocationData(){
        double lon = 0;
        double lat = 0;
        double speed = 0;
        double course = 0;
        try {
            JSONObject gpsLast = new JSONObject(VmaPreferences.get(getApplicationContext(), VmaApiConstant.GPS_LSAT_DATA));
            lon = gpsLast.getDouble(VmaApiConstant.GPS_ITEM_LON);
            lat = gpsLast.getDouble(VmaApiConstant.GPS_ITEM_LAT);
            speed = gpsLast.getDouble(VmaApiConstant.GPS_ITEM_SPEED);
//            speed = ThreadLocalRandom.current().nextDouble(0, 11);
            course = gpsLast.getDouble(VmaApiConstant.GPS_ITEM_BEARING);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG,"SPEED :"+speed+" menit : "+menit);
//        if (menit % 5 == 0 && speed >= 10){
//            Log.d(TAG,"Send in 5 minute ("+speed+") -> "+accountDB.imei +" ("+lat+","+lon+")");
//            sendToAPI(lon, lat, speed, course);
//        }
//        if (menit % 10 == 0  && (speed >= 5 &&  speed < 10)){
//            Log.d(TAG,"Send in 10 minute ("+speed+") -> "+accountDB.imei +" ("+lat+","+lon+")");
//            sendToAPI(lon, lat, speed, course);
//        }
//        if (menit % 30 == 0  && (speed >= 1 &&  speed < 5)){
//            Log.d(TAG,"Send in 30 minute ("+speed+") -> "+accountDB.imei +" ("+lat+","+lon+")");
//            sendToAPI(lon, lat, speed, course);
//        }
//        if (menit % 60 == 0 && speed < 1){
//            Log.d(TAG,"Send in 60 minute ("+speed+") -> "+accountDB.imei +" ("+lat+","+lon+")");
//            sendToAPI(lon, lat, speed, course);
//            menit = 0;
//        }

        if (menit % 2 == 0 && speed >= 5){
            sendToAPI(lon, lat, speed, course);
        }
        if (menit % 5 == 0  && (speed >= 3 &&  speed < 5)){
            sendToAPI(lon, lat, speed, course);
        }
        if (menit % 15 == 0  && (speed < 3)){
            sendToAPI(lon, lat, speed, course);
        }
        if (menit % 60 == 0  ){
            menit = 0;

            if (!isNetworkConnected()){
                saveToBlackBox(lon,lat, speed, course);
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        return  START_STICKY;
    }

    private void sendToAPI(double lon, double lat, double speed, double course){
        if (!isNetworkConnected()){
            Log.e(TAG,"Network Failed");
            return;
        }
        PostManager post = new PostManager(getApplicationContext(), ApiConfig.POST_SAVE_POSITION);
        post.addParam("imei", accountDB.imei);
        post.addParam("longitude", lon);
        post.addParam("latitude", lat);
        post.addParam("speed", speed);
        post.addParam("course", course);
        post.addParam("status", "Online");
        post.addParam("note", "Automatic "+menit+" Menit");
        post.addParam("time", Utility.getDateString(new Date(),"yyyy-MM-dd HH:mm:ss"));
        post.showloading(false);
        post.exPost();
        post.setOnReceiveListener((obj, code, success, message) -> {
            if (success){
                Log.d(TAG,"sendLocationData Success");
            }
            else {
                Log.d(TAG,"sendLocationData Failed "+ message);
            }
        });
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void saveToBlackBox(double lon, double lat, double speed, double course){
        BlackBoxDB db = new BlackBoxDB();
        db.longitude = lon;
        db.latitude = lat;
        db.speed = speed;
        db.course = course;
        db.isUpload = false;
        db.time = Utility.getDateString(new Date(),"yyyy-MM-dd HH:mm:ss");
        db.insert(getApplicationContext());
    }

}
