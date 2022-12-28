package com.vma.smartfishingapp.ui.maps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.vma.smartfishingapp.R;
import com.vma.smartfishingapp.libs.LocationConverter;
import com.vma.smartfishingapp.ui.master.MyView;

public class CoordinateView extends MyView {

    private TextView txvw_longitude,txvw_latitude,txvw_speed;
    public CoordinateView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int setLayout() {
        return R.layout.fishmap_view_coordinate;
    }

    @Override
    protected void initLayout() {
        txvw_longitude = findViewById(R.id.txvw_longitude);
        txvw_latitude = findViewById(R.id.txvw_latitude);
        txvw_speed = findViewById(R.id.txvw_speed);
    }

    public void create(){
        CardView card_coordinate = findViewById(R.id.card_coordinate);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            card_coordinate.setOutlineSpotShadowColor(ContextCompat.getColor(mActivity, R.color.primaryDark));
        }
    }

    @Override
    protected void initListener() {

    }

    public void setLocation(String longitude, String latitude){
        LocationConverter locationConverter = new LocationConverter(longitude, latitude);
        txvw_longitude.setText(locationConverter.getLongitudeDisplay(mActivity));
        txvw_latitude.setText(locationConverter.getLatitudeDisplay(mActivity));
    }

    public void setLocation(LocationConverter location){
        txvw_longitude.setText(location.getLongitudeDisplay(mActivity));
        txvw_latitude.setText(location.getLatitudeDisplay(mActivity));
    }

    @SuppressLint("SetTextI18n")
    public void setSpeed(double speed){
        txvw_speed.setText(""+speed);
    }
}
