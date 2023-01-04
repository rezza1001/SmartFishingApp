package com.vma.smartfishingapp.ui.dpi;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vma.smartfishingapp.R;
import com.vma.smartfishingapp.api.ApiConfig;
import com.vma.smartfishingapp.api.PostManager;
import com.vma.smartfishingapp.database.table.DirectionDB;
import com.vma.smartfishingapp.dom.DpiHolder;
import com.vma.smartfishingapp.dom.VmaApiConstant;
import com.vma.smartfishingapp.dom.VmaConstants;
import com.vma.smartfishingapp.libs.Utility;
import com.vma.smartfishingapp.libs.VmaPreferences;
import com.vma.smartfishingapp.ui.component.option.OptionDialog;
import com.vma.smartfishingapp.ui.maps.LocationHolder;
import com.vma.smartfishingapp.ui.maps.MainMapActivity;
import com.vma.smartfishingapp.ui.master.MyActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class DpiActivity extends MyActivity {

    TextView txvw_update;
    DpiAdapter adapter;

    ArrayList<DpiHolder> listDpi = new ArrayList<>();

    @Override
    protected int setLayout() {
        return R.layout.activity_dpi;
    }

    @Override
    protected void initLayout() {
        txvw_update = findViewById(R.id.txvw_update);

        RecyclerView rcvw_data = findViewById(R.id.rcvw_data);
        rcvw_data.setLayoutManager(new LinearLayoutManager(mActivity));

        adapter = new DpiAdapter(mActivity,listDpi);
        rcvw_data.setAdapter(adapter);

    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void initData() {
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

        PostManager post = new PostManager(mActivity, ApiConfig.POST_DPI);
        post.addParam("longitude",lon);
        post.addParam("latitude",lat);
        post.addParam("distance",80);
        post.addParam("date",Utility.getDateString(new Date(),"yyyy-MM-dd"));
        post.exPost();
        post.setOnReceiveListener((obj, code, success, message) -> {
            if (success){
                loadData(obj);
            }
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    protected void initListener() {
        adapter.setOnSelectedListener(menu -> {
            LocationHolder holder = new LocationHolder();
            holder.setLongitude(menu.getLongitude());
            holder.setLatitude(menu.getLatitude());
            direct(holder);
        });
    }

    private void loadData(JSONObject obj){
        try {
            JSONArray data = obj.getJSONArray("data");
            for (int i=0; i<data.length(); i++){
                JSONObject jo = data.getJSONObject(i);

                DpiHolder holder = new DpiHolder();
                holder.setDate(Utility.getDate(jo.getString("date"),"yyyy-MM-dd"));
                holder.setLatitude(jo.getDouble("latitude"));
                holder.setLongitude(jo.getDouble("longitude"));
                holder.setDistance(jo.getDouble("distance"));

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
        mActivity.startActivity(new Intent(mActivity, MainMapActivity.class));
    }
}
