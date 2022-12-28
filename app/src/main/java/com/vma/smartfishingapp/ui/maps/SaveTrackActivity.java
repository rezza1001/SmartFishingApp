package com.vma.smartfishingapp.ui.maps;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.widget.TextView;

import com.vma.smartfishingapp.R;
import com.vma.smartfishingapp.ui.component.VmaButton;
import com.vma.smartfishingapp.ui.component.VmaEditext;
import com.vma.smartfishingapp.database.table.TrackDB;
import com.vma.smartfishingapp.dom.VmaConstants;
import com.vma.smartfishingapp.libs.FileProcessing;
import com.vma.smartfishingapp.libs.MyFileReader;
import com.vma.smartfishingapp.libs.Utility;
import com.vma.smartfishingapp.libs.VmaPreferences;
import com.vma.smartfishingapp.ui.master.MyActivity;
import com.vma.smartfishingapp.service.TrackRecordService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

public class SaveTrackActivity extends MyActivity {

    private VmaEditext edtx_name;
    private TextView txvw_date,txvw_startTime,txvw_speed,txvw_time,txvw_endTime,txvw_distance;
    private VmaButton bbtn_save,bbtn_cancel;
    String mStartPosition = "";
    String mEndPosition = "";
    Date startDate;
    Date endDate;
    float calSpeed = 0;
    ArrayList<Integer> listSpeed = new ArrayList<>();


    @Override
    protected int setLayout() {
        return R.layout.fishmap_activity_save_track;
    }

    @Override
    protected void initLayout() {
        edtx_name = findViewById(R.id.edtx_name);
        txvw_date = findViewById(R.id.txvw_no);
        bbtn_save = findViewById(R.id.bbtn_save);
        bbtn_cancel = findViewById(R.id.bbtn_cancel);
        txvw_startTime = findViewById(R.id.txvw_startTime);
        txvw_speed = findViewById(R.id.txvw_speed);
        txvw_time = findViewById(R.id.txvw_time);
        txvw_endTime = findViewById(R.id.txvw_endTime);
        txvw_distance = findViewById(R.id.txvw_distance);
        String trackName = getResources().getString(R.string.track_name);
        edtx_name.create(trackName,trackName);
        edtx_name.setRightIcon(R.drawable.ic_baseline_edit_24, Color.parseColor("#00B9FF"));

        txvw_date.setText(Utility.getDateString(new Date(),"EEEE, dd MMMM yyyy"));
        bbtn_save.create(getResources().getString(R.string.save),0);
        bbtn_cancel.create(getResources().getString(R.string.cancel),0);
        bbtn_cancel.setButtonType(VmaButton.ButtonType.BLUE_GREY);


    }

    @Override
    protected void initData() {
        loadFileTrack();
    }

    @Override
    protected void initListener() {
        findViewById(R.id.rvly_back).setOnClickListener(view -> onBackPressed());
        bbtn_save.setOnActionListener(view -> {
            save();
        });
        bbtn_cancel.setOnActionListener(view -> {
            mActivity.finish();
        });
    }

    private void save(){
        if (edtx_name.getValue().isEmpty()){
            Utility.showToastError(mActivity,"Please input Track Name");
            return;
        }
        String fromPath = FileProcessing.getRootPath(mActivity)+ TrackRecordService.folderName+"/"+TrackRecordService.fileName;
        String toPath = FileProcessing.getRootPath(mActivity) + TrackRecordService.folderName+"/"+System.currentTimeMillis()+".txt";
        try {
            FileProcessing.Copy(new File(fromPath), new File(toPath));
            TrackDB db = new TrackDB();
            db.name          = edtx_name.getValue();
            db.startTime     = Utility.getDateString(startDate,"dd-MM-yyyy HH:mm:ss");
            db.endTime       = Utility.getDateString(endDate,"dd-MM-yyyy HH:mm:ss");
            db.startPosition = mStartPosition;
            db.endPosition   = mEndPosition;
            db.speed         = calSpeed;
            db.distance      = VmaPreferences.getFloat(mActivity, VmaConstants.TRACKING_DISTANCE);
            db.filePath      = toPath;

            db.insert(mActivity);


            Utility.showToastSuccess(mActivity,"Success");
            Intent intent = new Intent(VmaConstants.TRACK_RECORD_SAVE);
            intent.putExtra("status",0);
            sendBroadcast(intent);

            mActivity.finish();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFileTrack(){

        MyFileReader fileReader = new MyFileReader(mActivity);
        fileReader.setOnReadListener(new MyFileReader.OnReadListener() {
            @Override
            public void onLiveRead(String data) {
                try {
                    JSONObject joStart = new JSONObject(data);
                    listSpeed.add(joStart.getInt("speed"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFinish(String start, String data, String end) {
                mEndPosition = end;
                mStartPosition = data;

                try {
                    JSONObject joStart = new JSONObject(data);
                    startDate = Utility.getDate(joStart.getString("date"),"dd-MM-yyyy HH:mm:ss"); // 11-05-2022 10:07:53

                    JSONObject joEnd = new JSONObject(end);
                    endDate = Utility.getDate(joEnd.getString("date"),"dd-MM-yyyy HH:mm:ss");

                    long difference = endDate.getTime() - startDate.getTime();
                    long seconds = (difference / 1000) % 60;
                    long minutes = (difference / (1000 * 60)) % 60;
                    long hours = (difference / (1000 * 60 * 60)) % 24;
                    String totalTime = hours+" : "+minutes+" : "+ seconds;
                    txvw_time.setText(totalTime);

                    for (Integer speed : listSpeed){
                        calSpeed = calSpeed + speed;
                    }
                    calSpeed = calSpeed/listSpeed.size();
                    DecimalFormat format = new DecimalFormat("0.00");
                    txvw_speed.setText(format.format(calSpeed));

                    txvw_startTime.setText(Utility.getDateString(startDate,"HH:mm:ss"));
                    txvw_endTime.setText(Utility.getDateString(endDate,"HH:mm:ss"));
                    txvw_distance.setText(VmaPreferences.getFloat(mActivity,VmaConstants.TRACKING_DISTANCE)+" NM");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        String recordPath = FileProcessing.getRootPath(mActivity)+TrackRecordService.folderName+"/"+TrackRecordService.fileName;
        fileReader.readFile(recordPath);
    }

}
