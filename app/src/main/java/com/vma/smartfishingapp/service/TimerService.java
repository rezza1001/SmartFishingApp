package com.vma.smartfishingapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;

import com.vma.smartfishingapp.dom.VmaConstants;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Mochamad Rezza Gumilang on 10/02/2022
 */
public class TimerService extends Service {

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
        timer  = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                second ++;
                if (second % 10 == 0){
                    Intent intent = new Intent(VmaConstants.VMA_TIMER_TASK);
                    intent.putExtra(VmaConstants.SERVICE_DATA, 10);
                    sendBroadcast(intent);
                }
                if (second % 30 == 0){
                    Intent intent = new Intent(VmaConstants.VMA_TIMER_TASK);
                    intent.putExtra(VmaConstants.SERVICE_DATA, 30);
                    sendBroadcast(intent);
                }
                if (second % 60 == 0){
                    Intent intent = new Intent(VmaConstants.VMA_TIMER_TASK);
                    intent.putExtra(VmaConstants.SERVICE_DATA, 60);
                    sendBroadcast(intent);

                    second = 0;
                }
            }
        }, 0,1000);
    }


    @Override
    public void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }

}
