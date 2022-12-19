package com.vma.smartfishingapp.widget;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.vma.smartfishingapp.R;
import com.vma.smartfishingapp.dom.VmaApiConstant;
import com.vma.smartfishingapp.dom.VmaConstants;
import com.vma.smartfishingapp.libs.Compass;
import com.vma.smartfishingapp.master.MyFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * Created by Mochamad Rezza Gumilang on 14/02/2022
 */
public class CompassFragment extends MyFragment {

    private ImageView imvw_compass;
    private TextView txvw_knotValue,txvw_courseValue,txvw_courseInfo2,txvw_courseInfo1;
    private Compass mCompass;

    public static CompassFragment newInstance() {
        Bundle args = new Bundle();
        CompassFragment fragment = new CompassFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int setLayout() {
        return R.layout.wiget_fragment_compass;
    }

    @Override
    protected void initLayout(View view) {
        txvw_knotValue = view.findViewById(R.id.txvw_knotValue);
        imvw_compass = view.findViewById(R.id.imvw_compass);
        txvw_courseValue = view.findViewById(R.id.txvw_courseValue);
        txvw_courseInfo2 = view.findViewById(R.id.txvw_courseInfo2);
        txvw_courseInfo1 = view.findViewById(R.id.txvw_courseInfo1);

        mCompass = new Compass();
        mCompass.create(mActivity);

        String language = Locale.getDefault().getLanguage();
        if (language.equals("en")){
            imvw_compass.setImageResource(R.drawable.compass_frame_en);
        }
        else {
            imvw_compass.setImageResource(R.drawable.compass_frame);
        }
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    protected void initListener() {
        mCompass.setOnChangeListener((degree, azimuth, currentAzimuth, name, description) -> {
            Animation anim = new RotateAnimation(-currentAzimuth, -azimuth,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
            anim.setDuration(500);
            anim.setRepeatCount(0);
            anim.setFillAfter(true);
            imvw_compass.startAnimation(anim);
            txvw_courseValue.setText(String.format("%.0f", degree)+"\u00B0");
            txvw_courseInfo1.setText(name);
            txvw_courseInfo2.setText("("+description+")");
        });
    }

    @Override
    protected void initData() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(VmaConstants.VMA_GPS);
        mActivity.registerReceiver(receiver, intentFilter);
    }

    @SuppressLint("SetTextI18n")
    private void processDataGps(String data){
        try {
            JSONObject jo = new JSONObject(data);
            int speed = jo.getInt(VmaApiConstant.GPS_ITEM_SPEED);
            txvw_knotValue.setText(speed+"");

            Location temp = new Location(LocationManager.GPS_PROVIDER);
            temp.setLatitude(jo.getLong(VmaApiConstant.GPS_ITEM_LAT));
            temp.setLongitude(jo.getLong(VmaApiConstant.GPS_ITEM_LON));
            mCompass.setLocation(temp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mCompass.onStart();
    }

    @Override
    public void onDestroy() {
        mCompass.onDestroy();
        super.onDestroy();
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(VmaConstants.VMA_GPS)){
                String gpsData = intent.getStringExtra(VmaConstants.SERVICE_DATA);
                processDataGps(gpsData);
            }
        }
    };

}
