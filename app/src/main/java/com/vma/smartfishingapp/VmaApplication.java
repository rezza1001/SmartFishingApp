package com.vma.smartfishingapp;

import android.app.Application;
import android.content.Intent;
import android.os.Build;

import com.vma.smartfishingapp.dom.VmaConstants;
import com.vma.smartfishingapp.libs.VmaLanguage;
import com.vma.smartfishingapp.libs.VmaTheme;
import com.vma.smartfishingapp.service.MainService;
import com.vma.smartfishingapp.service.TimerService;

public class VmaApplication extends Application {
    private static final String TAG = "VmaApplication";
    public static String language = "id";

    public static float LastBearing = 0;

    public static int mPosFormat = VmaConstants.VMA_GPSFORMAT_DEG;

    @Override
    public void onCreate() {
        super.onCreate();
        language = VmaLanguage.INDONESIA;

        VmaTheme.setTheme(getApplicationContext(),VmaTheme.DAY_MODE);
        startService(new Intent(this, TimerService.class));

        startService(new Intent(this, MainService.class));
        VmaLanguage.changeToIndonesia(this);
    }


}
