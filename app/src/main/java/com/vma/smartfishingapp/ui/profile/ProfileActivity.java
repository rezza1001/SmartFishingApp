package com.vma.smartfishingapp.ui.profile;

import android.content.Intent;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.vma.smartfishingapp.R;
import com.vma.smartfishingapp.api.ApiConfig;
import com.vma.smartfishingapp.dom.VmaConstants;
import com.vma.smartfishingapp.ui.component.ConfirmDialog;
import com.vma.smartfishingapp.ui.master.MyActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends MyActivity {

    ItemView item_imei,item_owner,item_phone,item_address,item_sipi,item_gt,item_username;
    TextView txvw_name,txvw_status;
    ImageView imvw_status;
    CircleImageView imvw_profile;

    @Override
    protected int setLayout() {
        return R.layout.activity_profile;
    }

    @Override
    protected void initLayout() {
        item_imei = findViewById(R.id.item_imei);
        item_owner = findViewById(R.id.item_owner);
        txvw_name = findViewById(R.id.txvw_name);
        imvw_profile = findViewById(R.id.imvw_profile);
        item_phone = findViewById(R.id.item_phone);
        item_address = findViewById(R.id.item_address);
        item_sipi = findViewById(R.id.item_sipi);
        item_gt = findViewById(R.id.item_gt);
        item_username = findViewById(R.id.item_username);
        txvw_status = findViewById(R.id.txvw_status);
        imvw_status = findViewById(R.id.imvw_status);
    }

    @Override
    protected void initData() {
        if (mAccountDB.imei.isEmpty()){
            return;
        }

        item_imei.create("Imei", mAccountDB.imei);
        item_owner.create(getResources().getString(R.string.ownerName), mAccountDB.owner);
        item_phone.create(getResources().getString(R.string.phone), mAccountDB.phone);
        item_address.create(getResources().getString(R.string.address), mAccountDB.address);
        item_username.create(getResources().getString(R.string.username), mAccountDB.username);
        item_sipi.create("No. Sipi", mAccountDB.sipi);
        item_sipi.create("GT", mAccountDB.gt);
        txvw_name.setText(mAccountDB.shipName);
        if (!mAccountDB.image.isEmpty()){
            Glide.with(mActivity).load(ApiConfig.PATH_IMAGE +mAccountDB.image).into(imvw_profile);
        }

        if (mAccountDB.regStatus){
            imvw_status.setImageResource(R.drawable.ic_verified);
            txvw_status.setText(getResources().getString(R.string.premium));
            txvw_status.setTextColor(ContextCompat.getColor(mActivity,R.color.gold));
        }
        else {
            imvw_status.setImageResource(R.drawable.ic_unpaid);
            txvw_status.setText(getResources().getString(R.string.free));
            txvw_status.setTextColor(ContextCompat.getColor(mActivity,R.color.red));
        }

    }

    @Override
    protected void initListener() {
        findViewById(R.id.mrly_logout).setOnClickListener(view -> logout());
    }

    private void logout(){
        ConfirmDialog dialog = new ConfirmDialog(mActivity);
        dialog.show(getResources().getString(R.string.are_you_sure_logout));
        dialog.setOnActionListener(confirm -> {
            if (confirm){
                mAccountDB.clearData(mActivity);
                reloadAccount();

                new Handler().postDelayed(() -> sendBroadcast(new Intent(VmaConstants.NOTIFY_RELOAD)),100);
                new Handler().postDelayed(() -> mActivity.finish(),200);
            }
        });
    }
}
