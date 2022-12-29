package com.vma.smartfishingapp.ui.maps;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.vma.smartfishingapp.R;
import com.vma.smartfishingapp.database.table.DirectionDB;
import com.vma.smartfishingapp.database.table.LocationDB;
import com.vma.smartfishingapp.dom.VmaConstants;
import com.vma.smartfishingapp.libs.Utility;
import com.vma.smartfishingapp.ui.component.ConfirmDialog;
import com.vma.smartfishingapp.ui.component.option.OptionDialog;
import com.vma.smartfishingapp.ui.master.MyFragment;

import java.util.ArrayList;
import java.util.Date;

public class LocationFragment extends MyFragment {

    private MaterialRippleLayout mrly_add;
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

        mrly_add = view.findViewById(R.id.mrly_add);

    }

    @Override
    protected void initListener() {
        adapter.setOnSelectedListener(this::showOption);
        mrly_add.setOnClickListener(view -> saveLocation());
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
                delete(holder);
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
    private void delete(LocationHolder holder){
        String info = getResources().getString(R.string.are_you_sure_to_delete_location).replace("#X1",holder.getName());
        int start   = info.indexOf(holder.getName());
        int end     = start+ (holder.getName().length());

        SpannableString spanInfo = Utility.BoldText(mActivity, info,start,end,"#F79E46");
        ConfirmDialog dialog = new ConfirmDialog(mActivity);
        dialog.show(getResources().getString(R.string.confirm),spanInfo);
        dialog.setOnActionListener(confirm -> {
            if (confirm){
                LocationDB db = new LocationDB();
                db.delete(mActivity, holder.getId());
                initData();
                Utility.showToastSuccess(mActivity, getResources().getString(R.string.success));
            }
        });
    }

    private void saveLocation(){

        SaveLocationDialog dialog = new SaveLocationDialog(mActivity);
        dialog.show(null);
        dialog.setOnSaveListener(db -> initData());
    }
}
