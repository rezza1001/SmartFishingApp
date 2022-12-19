package com.vma.smartfishingapp.component;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.vma.smartfishingapp.R;
import com.vma.smartfishingapp.activity.SaveTrackActivity;
import com.vma.smartfishingapp.dialog.ConfirmSaveTrackDialog;
import com.vma.smartfishingapp.dom.VmaConstants;
import com.vma.smartfishingapp.libs.FileProcessing;
import com.vma.smartfishingapp.libs.VmaPreferences;
import com.vma.smartfishingapp.master.MyView;
import com.vma.smartfishingapp.service.TrackRecordService;

public class RecordButtonView extends MyView {

    RelativeLayout rvly_record;
    ImageView imvw_record;
    ConfirmSaveTrackDialog confirmSaveTrackDialog;

    public RecordButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int setLayout() {
        return R.layout.fishmap_view_recordbutton;
    }

    @Override
    protected void initLayout() {
        rvly_record = findViewById(R.id.rvly_record);
        imvw_record = findViewById(R.id.imvw_record);

    }

    public void create(){
        int tag = VmaPreferences.getInt(mActivity, VmaConstants.TRACKING_RECORD);
        if (tag == 1){
            imvw_record.setImageResource(R.drawable.navi_stop_record);
            new Handler().postDelayed(() -> {
                if (onRecordListener != null){
                    onRecordListener.onStart();
                }
            },500);
        }
        else {
            imvw_record.setImageResource(R.drawable.navi_start_record);
        }
    }

    @Override
    protected void initListener() {
        rvly_record.setOnClickListener(view -> record());
    }

    private void record(){
        int tag = VmaPreferences.getInt(mActivity, VmaConstants.TRACKING_RECORD);
        if (tag == 0){
            VmaPreferences.save(mActivity,VmaConstants.TRACKING_RECORD,1);
            imvw_record.setImageResource(R.drawable.navi_stop_record);
            startService();
        }
        else {
            showConfirm();
        }
    }

    private void showConfirm(){

        confirmSaveTrackDialog = new ConfirmSaveTrackDialog(mActivity);
        confirmSaveTrackDialog.show();
        confirmSaveTrackDialog.setOnActionListener(new ConfirmSaveTrackDialog.OnActionListener() {
            @Override
            public void OnSaveAction() {
                VmaPreferences.save(mActivity,VmaConstants.TRACKING_RECORD,0);
                imvw_record.setImageResource(R.drawable.navi_start_record);
                stopService();
                Intent intent = new Intent(mActivity, SaveTrackActivity.class);
                mActivity.startActivity(intent);
            }

            @Override
            public void OnNoAction() {
                stopAndClearData();
            }

            @Override
            public void OnCancelAction() {

            }
        });
    }

    private void stopAndClearData(){
        VmaPreferences.save(mActivity,VmaConstants.TRACKING_RECORD,0);
        imvw_record.setImageResource(R.drawable.navi_start_record);
        stopService();

        String path = FileProcessing.getRootPath(mActivity)+ TrackRecordService.folderName;
        FileProcessing.DeleteFile(path,TrackRecordService.fileName);

        try {
            mActivity.unregisterReceiver(receiver);
        }catch (Exception e){
            e.printStackTrace();
        }
        if (onRecordListener != null){
            onRecordListener.onFinish();
        }
    }

    private void startService(){
        mActivity.registerReceiver(receiver, new IntentFilter(VmaConstants.TRACK_RECORD_SAVE));

        Intent service = new Intent(mActivity.getApplicationContext(), TrackRecordService.class);
        mActivity.getApplicationContext().startService(service);
        if (onRecordListener != null){
            onRecordListener.onStart();
        }
    }

    private void stopService(){
        try {
            Context context = mActivity.getApplicationContext();
            Intent service = new Intent( context, TrackRecordService.class);
            context.stopService(service);
            if (onRecordListener != null){
                onRecordListener.onStop();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private OnRecordListener onRecordListener;
    public void setOnRecordListener(OnRecordListener onRecordListener){
        this.onRecordListener = onRecordListener;
    }
    public interface OnRecordListener{
        void onStart();
        void onStop();
        void onFinish();
    }


    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(VmaConstants.TRACK_RECORD_SAVE)){
                if (intent.getIntExtra("status",-1) == 0){
                    confirmSaveTrackDialog.dismiss();
                }
                new Handler().postDelayed(() -> stopAndClearData(),500);
            }
        }
    };



}
