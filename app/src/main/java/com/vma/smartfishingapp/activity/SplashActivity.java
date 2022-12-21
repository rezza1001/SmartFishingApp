package com.vma.smartfishingapp.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;

import com.vma.smartfishingapp.R;
import com.vma.smartfishingapp.dialog.ConfirmDialog;
import com.vma.smartfishingapp.libs.Utility;
import com.vma.smartfishingapp.master.MyActivity;
import com.vma.smartfishingapp.service.MainService;

import java.util.ArrayList;

public class SplashActivity extends MyActivity {
    @Override
    protected int setLayout() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initLayout() {

    }

    @Override
    protected void initData() {
        ArrayList<String> LIST_PERMISSION = new ArrayList<>();
        LIST_PERMISSION.add(Manifest.permission.ACCESS_FINE_LOCATION);
        LIST_PERMISSION.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        LIST_PERMISSION.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        LIST_PERMISSION.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        LIST_PERMISSION.add(Manifest.permission.RECEIVE_BOOT_COMPLETED);
        LIST_PERMISSION.add(Manifest.permission.BLUETOOTH_ADMIN);
        LIST_PERMISSION.add(Manifest.permission.BLUETOOTH);
        LIST_PERMISSION.add(Manifest.permission.ACCESS_NETWORK_STATE);
        LIST_PERMISSION.add(Manifest.permission.ACCESS_WIFI_STATE);
        LIST_PERMISSION.add(Manifest.permission.CHANGE_WIFI_STATE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            LIST_PERMISSION.add(Manifest.permission.BLUETOOTH_CONNECT);
            LIST_PERMISSION.add(Manifest.permission.FOREGROUND_SERVICE);
        }

        String[] PERMISSIONS =  new String[LIST_PERMISSION.size()];
        boolean hasPermission = Utility.checkPermission(mActivity,PERMISSIONS);
        if (hasPermission){
            startActivity(new Intent(mActivity, MainActivity.class));
            MainService.permission = true;
            mActivity.finish();
        }
        else {
            requestPermission();
        }

    }

    @Override
    protected void initListener() {

    }

    private void requestPermission(){
        ConfirmDialog dialog = new ConfirmDialog(mActivity);
        dialog.show(getResources().getString(R.string.permission),"");
        dialog.setOnActionListener(confirm -> {
            if (confirm){
                configPermission();
            }
            else {
                mActivity.finish();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startActivity(new Intent(mActivity, MainActivity.class));
            MainService.permission = true;
            mActivity.finish();
        } else {
            Utility.showToastError(mActivity,"Permission denied to");
        }
    }

    private void configPermission(){
        ArrayList<String> LIST_PERMISSION = new ArrayList<>();
        LIST_PERMISSION.add(Manifest.permission.ACCESS_FINE_LOCATION);
        LIST_PERMISSION.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        LIST_PERMISSION.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        LIST_PERMISSION.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        LIST_PERMISSION.add(Manifest.permission.RECEIVE_BOOT_COMPLETED);
        LIST_PERMISSION.add(Manifest.permission.BLUETOOTH_ADMIN);
        LIST_PERMISSION.add(Manifest.permission.BLUETOOTH);
        LIST_PERMISSION.add(Manifest.permission.ACCESS_NETWORK_STATE);
        LIST_PERMISSION.add(Manifest.permission.ACCESS_WIFI_STATE);
        LIST_PERMISSION.add(Manifest.permission.CHANGE_WIFI_STATE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            LIST_PERMISSION.add(Manifest.permission.BLUETOOTH_CONNECT);
            LIST_PERMISSION.add(Manifest.permission.FOREGROUND_SERVICE);
        }

        String[] PERMISSIONS =  new String[LIST_PERMISSION.size()];
        int position = 0;
        for (String a : LIST_PERMISSION ){
            PERMISSIONS[position] = a;
            position ++;
        }
        boolean permissionOk = Utility.hasPermission(mActivity,PERMISSIONS);
        if (permissionOk){
            startActivity(new Intent(mActivity, MainActivity.class));
        }
    }
}
