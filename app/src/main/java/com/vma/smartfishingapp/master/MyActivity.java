package com.vma.smartfishingapp.master;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.vma.smartfishingapp.VmaApplication;
import com.vma.smartfishingapp.database.table.AccountDB;
import com.vma.smartfishingapp.libs.Utility;

import java.util.ArrayList;

public abstract class MyActivity extends AppCompatActivity {

    protected String TAG = "MyActivity";
    protected Activity mActivity;
    private VmaApplication vmaApplication;

    protected AccountDB mAccountDB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setLayout());
        mActivity = this;
        vmaApplication = (VmaApplication) getApplication();

        mAccountDB = new AccountDB();
        mAccountDB.loadData(mActivity);

        TAG = mActivity.getClass().getSimpleName();
        configPermission();

        initLayout();
        initData();
        initListener();
    }

    protected void reloadAccount(){
        mAccountDB = new AccountDB();
        mAccountDB.loadData(mActivity);
    }

    protected abstract int setLayout();
    protected abstract void initLayout();
    protected abstract void initData();
    protected abstract void initListener();


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
            vmaApplication.startGPSRequestLocation(mActivity);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            vmaApplication.startGPSRequestLocation(mActivity);
        } else {
            Utility.showToastError(mActivity,"Permission denied to");
        }
    }
}
