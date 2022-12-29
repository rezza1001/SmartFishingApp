package com.vma.smartfishingapp.ui.maps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.vma.smartfishingapp.R;
import com.vma.smartfishingapp.dom.VmaConstants;
import com.vma.smartfishingapp.ui.main.BottomMenuView;
import com.vma.smartfishingapp.ui.master.MyActivity;

public class MainMapActivity extends MyActivity {

    FrameLayout frame_body,frame_body2;
    BottomMenuView menu_bottom;

    @Override
    protected int setLayout() {
        return R.layout.fishmap_activity_main;
    }

    @Override
    protected void initLayout() {
        frame_body = findViewById(R.id.frame_body);
        menu_bottom = findViewById(R.id.menu_bottom);
        frame_body2 = findViewById(R.id.frame_body2);
        frame_body2.setVisibility(View.GONE);

        IntentFilter intentFilter =  new IntentFilter();
        intentFilter.addAction(VmaConstants.SHOW_TRACK);
        intentFilter.addAction(VmaConstants.NOTIFY_DIRECTION);
        intentFilter.addAction(VmaConstants.NOTIFY_SHOW_TRACK);
        registerReceiver(receiver,intentFilter);

    }

    @Override
    protected void initData() {
        createMap();
    }

    @Override
    protected void initListener() {
        menu_bottom.setOnSelectedListener(position -> {
            if (position == 1){
                frame_body2.setVisibility(View.INVISIBLE);
                frame_body.setVisibility(View.VISIBLE);
            }
            else {
                frame_body.setVisibility(View.INVISIBLE);
                frame_body2.setVisibility(View.VISIBLE);
                gotToPage(position);
            }
        });
    }

//    @Override
//    public void onBackPressed() {
//        moveTaskToBack(true);
//    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    private void gotToPage(int page){
        Fragment fragment = DpiFragment.newInstance();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (page == 3){
            fragment = LocationFragment.newInstance();
        }
        else if (page == 4){
            fragment = ListTrackFragment.newInstance();
        }
        fragmentTransaction.setCustomAnimations(R.anim.push_up_in, R.anim.push_down_out);
        fragmentTransaction.replace(frame_body2.getId(), fragment,"page_"+page);
        fragmentTransaction.attach(fragment);
        fragmentTransaction.commit();
    }

    private void createMap(){
        Fragment fragment = MapFragment.newInstance();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.push_up_in, R.anim.push_down_out);
        fragmentTransaction.replace(frame_body.getId(), fragment,"page_map");
        fragmentTransaction.attach(fragment);
        fragmentTransaction.commit();
    }


    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
             if (intent.getAction().equals(VmaConstants.NOTIFY_DIRECTION) ||
                    intent.getAction().equals(VmaConstants.NOTIFY_SHOW_TRACK) ){
                menu_bottom.setSelected(0);
            }

        }
    };



}
