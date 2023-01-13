package com.vma.smartfishingapp.ui.maps;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vma.smartfishingapp.R;
import com.vma.smartfishingapp.api.ApiConfig;
import com.vma.smartfishingapp.api.PostManager;
import com.vma.smartfishingapp.database.table.DirectionDB;
import com.vma.smartfishingapp.database.table.DpiDB;
import com.vma.smartfishingapp.dom.DpiHolder;
import com.vma.smartfishingapp.dom.VmaApiConstant;
import com.vma.smartfishingapp.dom.VmaConstants;
import com.vma.smartfishingapp.libs.DistanceUnit;
import com.vma.smartfishingapp.libs.Utility;
import com.vma.smartfishingapp.libs.VmaPreferences;
import com.vma.smartfishingapp.ui.component.option.OptionDialog;
import com.vma.smartfishingapp.ui.dpi.DpiAdapter;
import com.vma.smartfishingapp.ui.master.MyFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class DpiFragment extends MyFragment {

    TextView txvw_update;
    DpiAdapter adapter;
    LinearLayout lnly_empty;

    ArrayList<DpiHolder> listDpi = new ArrayList<>();

    public static DpiFragment newInstance() {
        Bundle args = new Bundle();
        DpiFragment fragment = new DpiFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int setLayout() {
        return R.layout.map_fragment_dpi;
    }

    @Override
    protected void initLayout(View view) {
        txvw_update = view.findViewById(R.id.txvw_update);
        lnly_empty = view.findViewById(R.id.lnly_empty);

        RecyclerView rcvw_data = view.findViewById(R.id.rcvw_data);
        rcvw_data.setLayoutManager(new LinearLayoutManager(mActivity));

        adapter = new DpiAdapter(mActivity,listDpi);
        rcvw_data.setAdapter(adapter);
    }

    @Override
    protected void initListener() {
        adapter.setOnSelectedListener(menu -> {
            LocationHolder holder = new LocationHolder();
            holder.setLatitude(menu.getLatitude());
            holder.setLongitude(menu.getLongitude());
            direct(holder);
        });
    }

    @Override
    protected void initData() {
        load();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void load(){
        listDpi.clear();

        double lon = 0;
        double lat = 0;
        try {
            JSONObject gpsLast = new JSONObject(VmaPreferences.get(mActivity, VmaApiConstant.GPS_LSAT_DATA));
            lon = gpsLast.getDouble("longitude");
            lat = gpsLast.getDouble("latitude");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!Utility.isNetworkConnected(mActivity)){
            loadFormDB(lon, lat);
            adapter.notifyDataSetChanged();
            if (listDpi.size() == 0){
                lnly_empty.setVisibility(View.VISIBLE);
            }
            else {
                lnly_empty.setVisibility(View.GONE);
            }
            return;
        }

        PostManager post = new PostManager(mActivity, ApiConfig.POST_DPI);
        post.addParam("longitude",lon);
        post.addParam("latitude",lat);
        post.addParam("distance",80);
        post.addParam("date", Utility.getDateString(new Date(),"yyyy-MM-dd"));
        post.exPost();
        post.setOnReceiveListener((obj, code, success, message) -> {
            if (success){
                process(obj);
            }
            if (listDpi.size() == 0){
                lnly_empty.setVisibility(View.VISIBLE);
            }
            else {
                lnly_empty.setVisibility(View.GONE);
            }
            adapter.notifyDataSetChanged();
        });
    }

    private void process(JSONObject obj){
        DpiDB mDPI = new DpiDB();
        mDPI.clearData(mActivity);

        try {
            JSONArray data = obj.getJSONArray("data");
            for (int i=0; i<data.length(); i++){
                JSONObject jo = data.getJSONObject(i);

                DpiHolder holder = new DpiHolder();
                holder.setDate(Utility.getDate(jo.getString("date"),"yyyy-MM-dd"));
                holder.setLatitude(jo.getDouble("latitude"));
                holder.setLongitude(jo.getDouble("longitude"));

                double distance = jo.getDouble("distance") / 1.852;
                holder.setDistance(distance);

                DpiDB db = new DpiDB();
                db.name = jo.getString("date");
                db.date = jo.getString("date");
                db.longitude = jo.getDouble("longitude");
                db.latitude = jo.getDouble("latitude");
                db.insert(mActivity);

                if (i ==0 ){
                    Date date = Utility.getDate(jo.getString("date"),"yyyy-MM-dd");
                    txvw_update.setText(Utility.getDateString(date,"dd MMM yyyy"));
                }

                listDpi.add(holder);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadFormDB(double longitude, double latitude){
        DpiDB db = new DpiDB();
        ArrayList<DpiDB> dpiDBS = db.getAll(mActivity);
        for (DpiDB dpidb : dpiDBS){
            DpiHolder holder = new DpiHolder();
            holder.setDate(Utility.getDate(dpidb.date,"yyyy-MM-dd"));
            holder.setLatitude(dpidb.latitude);
            holder.setLongitude(dpidb.longitude);

            DistanceUnit unit = new DistanceUnit(mActivity);
            unit.calcDistance(dpidb.latitude,dpidb.longitude,latitude,longitude);
            holder.setDistance(unit.getNm());

            if (listDpi.size() == 0){
                Date date = Utility.getDate(dpidb.date,"yyyy-MM-dd");
                txvw_update.setText(Utility.getDateString(date,"dd MMM yyyy"));
            }

            listDpi.add(holder);
        }
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
