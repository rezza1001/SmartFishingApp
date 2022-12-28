package com.vma.smartfishingapp.ui.maps;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vma.smartfishingapp.R;
import com.vma.smartfishingapp.api.ApiConfig;
import com.vma.smartfishingapp.api.PostManager;
import com.vma.smartfishingapp.dom.DpiHolder;
import com.vma.smartfishingapp.dom.VmaApiConstant;
import com.vma.smartfishingapp.libs.Utility;
import com.vma.smartfishingapp.libs.VmaPreferences;
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

        RecyclerView rcvw_data = view.findViewById(R.id.rcvw_data);
        rcvw_data.setLayoutManager(new LinearLayoutManager(mActivity));

        adapter = new DpiAdapter(mActivity,listDpi);
        rcvw_data.setAdapter(adapter);
    }

    @Override
    protected void initListener() {

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
            adapter.notifyDataSetChanged();
        });
    }

    private void process(JSONObject obj){
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
}