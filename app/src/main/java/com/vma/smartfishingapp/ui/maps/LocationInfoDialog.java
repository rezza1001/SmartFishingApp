package com.vma.smartfishingapp.ui.maps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.vma.smartfishingapp.R;
import com.vma.smartfishingapp.libs.DistanceUnit;
import com.vma.smartfishingapp.libs.LocationConverter;
import com.vma.smartfishingapp.libs.VmaUtils;
import com.vma.smartfishingapp.ui.master.MyDialog;

import java.util.Locale;

public class LocationInfoDialog extends MyDialog {

    private CardView card_body;
    private TextView txvw_location,txvw_distance,txvw_heading;

    private Point distancePoint;

    public LocationInfoDialog(@NonNull Context context) {
        super(context);
    }


    @Override
    protected int setLayout() {
        return R.layout.fishmap_location_dialog_info;
    }

    @Override
    protected void initLayout(View view) {
        card_body = view.findViewById(R.id.card_body);
        txvw_location = view.findViewById(R.id.txvw_location);
        txvw_distance = view.findViewById(R.id.txvw_distance);
        txvw_heading = view.findViewById(R.id.txvw_heading);
        card_body.setVisibility(View.INVISIBLE);

        view.findViewById(R.id.rvly_root).setOnClickListener(view1 -> dismiss());
        view.findViewById(R.id.rvly_save).setOnClickListener(view1 -> {
            if (onActonListener != null){
                onActonListener.onSave(distancePoint,distancePoint.getX(),distancePoint.getY());
                dismiss();
            }
        });
        view.findViewById(R.id.rvly_direct).setOnClickListener(view1 -> {
            if (onActonListener != null){
                onActonListener.onDirect(distancePoint,distancePoint.getX(),distancePoint.getY());
                dismiss();
            }
        });
    }

    @Override
    public void show() {
        super.show();
        card_body.setVisibility(View.VISIBLE);
        card_body.setAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.push_up_in));
    }

    @SuppressLint("SetTextI18n")
    public void setLocation(Point point, double currentLat, double currentLng){
        distancePoint = point;
        double lat = distancePoint.getY();
        double lng = distancePoint.getX();

        LocationConverter converter = new LocationConverter(lng, lat);
        txvw_location.setText(converter.getLongitudeDisplay(mActivity) +" "+converter.getLatitudeDisplay(mActivity));
        Point currentPoint = new Point(currentLng, currentLat, SpatialReferences.getWgs84());
        DistanceUnit unit = new DistanceUnit();
        unit.calcDistance(distancePoint, currentPoint);
        txvw_distance.setText(unit.getDisplay());

        double bearing = VmaUtils.bearing(currentPoint.getY(), currentPoint.getX(), converter.getPoint().getY(), converter.getPoint().getX());
        String bear = String.format(Locale.ENGLISH, "%d°", (int)bearing);
        txvw_heading.setText(bear);

    }

    @Override
    public void dismiss() {
        card_body.setVisibility(View.VISIBLE);
        card_body.clearAnimation();
        card_body.setAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.push_down_out));
        if (onActonListener != null){
            onActonListener.onDismiss();
        }
        new Handler().postDelayed(super::dismiss,500);
    }


    private OnActonListener onActonListener;
    public void setOnActonListener(OnActonListener onActonListener){
        this.onActonListener = onActonListener;
    }
    public interface OnActonListener{
        void onDirect(Point point, double lng, double lat);
        void onSave(Point point, double lng, double lat);
        void onDismiss();
    }
}
