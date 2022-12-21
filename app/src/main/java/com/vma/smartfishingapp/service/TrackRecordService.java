package com.vma.smartfishingapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.vma.smartfishingapp.dom.VmaApiConstant;
import com.vma.smartfishingapp.dom.VmaConstants;
import com.vma.smartfishingapp.libs.FileProcessing;
import com.vma.smartfishingapp.libs.LocationConverter;
import com.vma.smartfishingapp.libs.Utility;
import com.vma.smartfishingapp.libs.VmaPreferences;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Mochamad Rezza Gumilang on 10/02/2022
 */
public class TrackRecordService extends Service {
    private final IBinder mBinder = new MyBinder();

    private Timer timer;
    int second = 0;
    int minute = 0;
    int hour = 0;

    float distance = 0;

    double lastLongitude = 0, lastLatitude = 0;

    public static final String fileName = "tracking.txt";
    public static final String folderName = "track";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FileProcessing.createFolder(getApplicationContext(),folderName);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                second++;
                if (second % 60 == 0) {
                    minute = minute + 1;
                    second = 0;
                }
                if (minute % 60 == 0 && minute > 0) {
                    hour = hour + 1;
                    minute = 0;
                }

                Intent intent = new Intent(VmaConstants.TRACK_RECORD_TASK);
                intent.putExtra(VmaConstants.HOUR, hour);
                intent.putExtra(VmaConstants.MINUTE, minute);
                intent.putExtra(VmaConstants.SECOND, second);
                sendBroadcast(intent);

                getLocation();

            }
        }, 0, 1000);
    }

    private void getLocation() {
        double longitude = 0, latitude = 0;
        String prefData = VmaPreferences.get(getApplicationContext(), VmaApiConstant.GPS_LSAT_DATA);
        if (prefData.isEmpty()){
            return;
        }

        try {
            JSONObject jo = new JSONObject(prefData);
            if (jo.has(VmaApiConstant.GPS_ITEM_SNR)){
                return;
            }
            LocationConverter converter = new LocationConverter(jo.getString("longitude"),jo.getString("latitude"));
            latitude = converter.getLatitude();
            longitude = converter.getLongitude();


            if (lastLatitude != 0.0 && lastLongitude != 0.0){
                Point point1 = new Point(lastLongitude, lastLatitude, SpatialReferences.getWgs84());
                Point point2 = new Point(longitude, latitude, SpatialReferences.getWgs84());
                float currDistance = Utility.getDistance(point1,point2);
                if (currDistance < 0.5){
                  return;
                }
                distance = distance + currDistance;
                FileProcessing.WriteFileToLog(getApplicationContext(),folderName,fileName,jo.toString());

                lastLongitude = longitude;
                lastLatitude = latitude;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public class MyBinder extends Binder {
        public TrackRecordService getService() {
            return TrackRecordService.this;
        }
    }

    @Override
    public void onDestroy() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        DecimalFormat format = new DecimalFormat("0.00", symbols);
        float xDistance = Float.parseFloat(format.format(distance));
        VmaPreferences.save(getApplicationContext(),VmaConstants.TRACKING_DISTANCE, xDistance);
        timer.cancel();
        super.onDestroy();
    }

}
