package com.vma.smartfishingapp.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.vma.smartfishingapp.R;
import com.vma.smartfishingapp.api.ApiConfig;
import com.vma.smartfishingapp.component.MenuView;
import com.vma.smartfishingapp.component.VmaButton;
import com.vma.smartfishingapp.dialog.ConfirmDialog;
import com.vma.smartfishingapp.dialog.LoginDialog;
import com.vma.smartfishingapp.dom.MenuHolder;
import com.vma.smartfishingapp.master.MyActivity;
import com.vma.smartfishingapp.widget.CompassFragment;
import com.vma.smartfishingapp.widget.DateWidget;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends MyActivity {

    DateWidget widget_time;
    FrameLayout frame_compass;
    MenuView menu_bottom,menu_bottom2;
    VmaButton bbtn_login;
    RelativeLayout rvly_profile;
    TextView txvw_ship,txvw_owner;
    CircleImageView imvw_kapal;

    @Override
    protected int setLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initLayout() {
        widget_time = findViewById(R.id.widget_time);
        widget_time.create();

        frame_compass = findViewById(R.id.frame_compass);
        addCompass();

        menu_bottom = findViewById(R.id.menu_bottom);
        menu_bottom2 = findViewById(R.id.menu_bottom2);
        rvly_profile = findViewById(R.id.rvly_profile);
        txvw_ship = findViewById(R.id.txvw_ship);
        txvw_owner = findViewById(R.id.txvw_owner);
        imvw_kapal = findViewById(R.id.imvw_kapal);

        createMenu();

        bbtn_login = findViewById(R.id.bbtn_login);
        bbtn_login.create("Login",0);
//        bbtn_login
    }

    @Override
    protected void initData() {
        rvly_profile.clearAnimation();
        bbtn_login.clearAnimation();
        Glide.with(mActivity).load(R.drawable.default_kapal).into(imvw_kapal);
        if (mAccountDB.imei.isEmpty()){
            rvly_profile.startAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.zoom_out));
            new Handler().postDelayed(() -> rvly_profile.setVisibility(View.GONE),250);

            bbtn_login.startAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.push_up_in));
            bbtn_login.setVisibility(View.VISIBLE);
            return;
        }
        rvly_profile.startAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.zoom_in));
        rvly_profile.setVisibility(View.VISIBLE);
        bbtn_login.setVisibility(View.GONE);
        txvw_ship.setText(mAccountDB.shipName);
        txvw_owner.setText(mAccountDB.owner);
        if (!mAccountDB.image.isEmpty()){
            Glide.with(mActivity).load(ApiConfig.PATH_IMAGE + mAccountDB.image).into(imvw_kapal);
        }


    }

    @Override
    protected void initListener() {
        bbtn_login.setOnActionListener(view -> showLogin());
        findViewById(R.id.rvly_logout).setOnClickListener(view -> logout());
    }

    protected void addCompass(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = CompassFragment.newInstance();
        fragmentTransaction.replace(frame_compass.getId(), fragment,"compass");
        fragmentTransaction.detach(fragment);
        fragmentTransaction.attach(fragment);
        fragmentTransaction.commit();
    }

    private void createMenu(){
        menu_bottom.create(true);
        menu_bottom.addMenu(new MenuHolder(MenuHolder.MENU.FISH_MAP,"Map",R.drawable.menu_map, createDrawable(R.drawable.menu_fishmap)));
        menu_bottom.addMenu(new MenuHolder(MenuHolder.MENU.LOGBOOK,"eLogbook",R.drawable.ic_logbook, createDrawable(R.drawable.menu_logbook)));
        menu_bottom.addMenu(new MenuHolder(MenuHolder.MENU.WEATHER,getResources().getString(R.string.weather),R.drawable.ic_weather, createDrawable(R.drawable.menu_weather)));

        menu_bottom2.create(true);
        menu_bottom2.addMenu(new MenuHolder(MenuHolder.MENU.MESSAGE,"DPI",R.drawable.ic_message, createDrawable(R.drawable.menu_message)));
        menu_bottom2.addMenu(new MenuHolder(MenuHolder.MENU.SOS,"SOS",R.drawable.ic_sos, createDrawable(R.drawable.menu_sos)));
        menu_bottom2.addMenu(new MenuHolder(MenuHolder.MENU.SETTING,getResources().getString(R.string.setting),R.drawable.ic_settings, createDrawable(R.drawable.menu_setting)));

//        menu_bottom2.addMenu(new MenuHolder(MenuHolder.MENU.CAMERA,"Camera",R.drawable.ic_baseline_camera_24, createDrawable(R.drawable.menu_console)));
//        menu_bottom2.addMenu(new MenuHolder(MenuHolder.MENU.GALLERY,"Gallery",R.drawable.ic_gallery, createDrawable(R.drawable.menu_fishmap)));
    }

    private Drawable createDrawable(int res){
        return ResourcesCompat.getDrawable(getResources(),res,null);
    }

    private void showLogin(){
        LoginDialog dialog = new LoginDialog(mActivity);
        dialog.show();
        dialog.setOnActionListener(new LoginDialog.OnActionListener() {
            @Override
            public void onRegister() {
                Intent intent = new Intent(mActivity, RegisterActivity.class);
                mActivity.startActivityForResult(intent,1);
            }

            @Override
            public void onLogin() {
                reloadAccount();
                initData();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK){
            reloadAccount();
            initData();
        }
    }

    private void logout(){
        ConfirmDialog dialog = new ConfirmDialog(mActivity);
        dialog.show(getResources().getString(R.string.are_you_sure_logout));
        dialog.setOnActionListener(confirm -> {
            if (confirm){
                mAccountDB.clearData(mActivity);
                reloadAccount();
                initData();
            }
        });

    }
}