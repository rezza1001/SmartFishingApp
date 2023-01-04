package com.vma.smartfishingapp.ui.logbook;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.SpannableString;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vma.smartfishingapp.R;
import com.vma.smartfishingapp.database.table.LogbookDB;
import com.vma.smartfishingapp.database.table.LogbookUploadDB;
import com.vma.smartfishingapp.dom.DpiHolder;
import com.vma.smartfishingapp.libs.Utility;
import com.vma.smartfishingapp.service.MainService;
import com.vma.smartfishingapp.ui.component.ConfirmDialog;
import com.vma.smartfishingapp.ui.master.MyActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class LogbookActivity extends MyActivity {

    LogbookAdapter adapter;

    ArrayList<LogbookHolder> listDpi = new ArrayList<>();

    @Override
    protected int setLayout() {
        return R.layout.activity_logbook;
    }

    @Override
    protected void initLayout() {

        RecyclerView rcvw_data = findViewById(R.id.rcvw_data);
        rcvw_data.setLayoutManager(new LinearLayoutManager(mActivity));

        adapter = new LogbookAdapter(mActivity,listDpi);
        rcvw_data.setAdapter(adapter);

    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void initData() {
        listDpi.clear();
        loadDB();
//
//        PostManager post = new PostManager(mActivity, ApiConfig.POST_DPI);
//        post.addParam("longitude",lon);
//        post.addParam("latitude",lat);
//        post.addParam("distance",80);
//        post.addParam("date",Utility.getDateString(new Date(),"yyyy-MM-dd"));
//        post.exPost();
//        post.setOnReceiveListener((obj, code, success, message) -> {
//            if (success){
//                loadData(obj);
//            }
//            adapter.notifyDataSetChanged();
//        });
    }

    @Override
    protected void initListener() {
        findViewById(R.id.fab_add).setOnClickListener(view -> {
            Intent intent = new Intent(mActivity, AddLogbookActivity.class);
            startActivityForResult(intent,1);
        });

        adapter.setOnSelectedListener(this::confirmToDelete);
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
                }

//                listDpi.add(holder);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadDB(){
        listDpi.clear();
        LogbookDB db = new LogbookDB();
        for (LogbookDB logbookDB : db.getAll(mActivity)){
            LogbookHolder holder = new LogbookHolder();
            FishHolder fishHolder = MainService.MAP_FISH.get(logbookDB.fishId);
            if (fishHolder  == null){
                continue;
            }

            holder.setId(logbookDB.id);
            holder.setFishId(fishHolder.getId());
            holder.setFishName(fishHolder.getName());
            holder.setType(UnitType.find(logbookDB.unit));
            holder.setFishImage(fishHolder.getImageName());
            holder.setLongitude(logbookDB.longitude);
            holder.setLatitude(logbookDB.latitude);
            holder.setQty(logbookDB.qty);
            String sTime = logbookDB.date +" "+ logbookDB.time;

            holder.setTime(Utility.getDate(sTime,"dd/MM/YYYY HH:mm"));
            listDpi.add(holder);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK){
            loadDB();
        }
    }

    private void confirmToDelete(LogbookHolder holder){
        String sValue = holder.getFishName() + " pada tanggal "+ Utility.getDateString(holder.getTime(),"dd MMM yyyy HH:mm");
        String info = getResources().getString(R.string.are_you_sure_to_delete_logbook).replace("#X1",sValue);

        int start   = info.indexOf(sValue);
        int end     = start+ (sValue.length());

        SpannableString spanInfo = Utility.BoldText(mActivity, info,start,end,"#F79E46");

        ConfirmDialog dialog = new ConfirmDialog(mActivity);
        dialog.show(getResources().getString(R.string.delete_logbook),spanInfo);
        dialog.setOnActionListener(confirm -> {
            if (confirm){
                LogbookDB db = new LogbookDB();
                db.delete(mActivity, holder.getId());
                loadDB();

                LogbookUploadDB dbUpload = new LogbookUploadDB();
                dbUpload.deleteByLogbook(mActivity, holder.getId());
            }
        });
    }
}
