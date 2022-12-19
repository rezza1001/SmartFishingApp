package com.vma.smartfishingapp.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.AnimationUtils;

import com.vma.smartfishingapp.R;
import com.vma.smartfishingapp.master.MyView;

public class DirectionStatusView extends MyView {

    public DirectionStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int setLayout() {
        return R.layout.fishmap_view_direction_status;
    }

    @Override
    protected void initLayout() {
        setVisibility(GONE);
    }

    @Override
    protected void initListener() {

    }

    public void show(){
        setVisibility(VISIBLE);
        startAnimation(AnimationUtils.loadAnimation(mActivity,R.anim.zoom_in));
    }

    public void hide(){
        setVisibility(GONE);
    }
}
