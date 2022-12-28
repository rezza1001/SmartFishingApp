package com.vma.smartfishingapp.ui.maps;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.esri.arcgisruntime.geometry.Point;
import com.vma.smartfishingapp.R;
import com.vma.smartfishingapp.database.table.DirectionDB;
import com.vma.smartfishingapp.database.table.LocationDB;
import com.vma.smartfishingapp.dom.VmaConstants;
import com.vma.smartfishingapp.libs.DistanceUnit;
import com.vma.smartfishingapp.ui.component.option.OptionDialog;
import com.vma.smartfishingapp.ui.master.MyFragment;

import java.util.ArrayList;
import java.util.Date;

public class LocationFragment extends MyFragment {

    private LocationAdapter adapter;
    private ArrayList<LocationHolder> listLocation = new ArrayList<>();

    public static LocationFragment newInstance() {
        Bundle args = new Bundle();
        LocationFragment fragment = new LocationFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    protected int setLayout() {
        return R.layout.map_fragment_location;
    }

    @Override
    protected void initLayout(View view) {

        RecyclerView rcvw_data = view.findViewById(R.id.rcvw_data);
        rcvw_data.setLayoutManager(new LinearLayoutManager(mActivity));

        adapter = new LocationAdapter(mActivity, listLocation);
        rcvw_data.setAdapter(adapter);
    }

    @Override
    protected void initListener() {
        adapter.setOnSelectedListener(this::showOption);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void initData() {
        listLocation.clear();
        LocationDB db = new LocationDB();
        ArrayList<LocationDB> listDB = db.getAll(mActivity);
        for (LocationDB loc : listDB){
            LocationHolder  holder = new LocationHolder();
            holder.setDate(new Date());
            holder.setLatitude(loc.latitude);
            holder.setLongitude(loc.longitude);
            holder.setName(loc.name);
            holder.setId(loc.id);

            listLocation.add(holder);
        }
        adapter.notifyDataSetChanged();
    }

    private void showOption(LocationHolder holder){
        OptionDialog dialog = new OptionDialog(mActivity);
        dialog.show();
        dialog.addChooser(getResources().getString(R.string.goto_location));
        dialog.addChooser(getResources().getString(R.string.edit_location));
        dialog.addChooser(getResources().getString(R.string.delete));

        dialog.setOnSelectListener((bundle, value) -> {
            if (value.equals(getResources().getString(R.string.delete))){
                LocationDB db = new LocationDB();
                db.delete(mActivity, holder.getId());
                initData();
            }
            else if (value.equals(getResources().getString(R.string.edit_location))){
                editLocation(holder);
            }
            else {
                direct(holder);
            }
        });
    }

    private void editLocation(LocationHolder holder){
        SaveLocationDialog dialog = new SaveLocationDialog(mActivity);
        dialog.showAsEdit(holder.getLongitude(), holder.getLatitude(), holder.getId());
        dialog.setOnSaveListener(db -> initData());
    }

    private void direct(LocationHolder holder){
        DirectionDB db = new DirectionDB();
        if (db.getAll(mActivity).size() >= 1){
            directionOption(holder.getLongitude(), holder.getLatitude());
            return;
        }
        db.id = db.getNextID(mActivity);
        db.longitude = holder.getLongitude();
        db.latitude = holder.getLatitude();
        db.isFinish = false;
        db.insert(mActivity);
        notifyDirection();
    }

    private void directionOption(double lng, double lat){
        OptionDialog dialog = new OptionDialog(mActivity);
        dialog.show();
        dialog.addChooser(getResources().getString(R.string.move_location));
        dialog.addChooser(getResources().getString(R.string.add_location));
        dialog.setOnSelectListener((bundle, value) -> {
            DirectionDB db = new DirectionDB();
            if (value.equals(getResources().getString(R.string.move_location))){
                db.clearData(mActivity);
            }
            db.id = db.getNextID(mActivity);
            db.longitude = lng;
            db.latitude = lat;
            db.isFinish = false;
            db.insert(mActivity);
            notifyDirection();
        });
    }

    private void notifyDirection(){
        mActivity.sendBroadcast(new Intent(VmaConstants.NOTIFY_DIRECTION));
    }
}
