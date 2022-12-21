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

}
