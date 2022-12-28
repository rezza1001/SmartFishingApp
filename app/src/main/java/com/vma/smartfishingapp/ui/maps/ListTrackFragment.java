package com.vma.smartfishingapp.ui.maps;

import android.os.Bundle;
import android.view.View;

import com.vma.smartfishingapp.R;
import com.vma.smartfishingapp.ui.master.MyFragment;

public class ListTrackFragment extends MyFragment {

    public static ListTrackFragment newInstance() {
        Bundle args = new Bundle();
        ListTrackFragment fragment = new ListTrackFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    protected int setLayout() {
        return R.layout.map_fragment_dpi;
    }

    @Override
    protected void initLayout(View view) {

    }

    @Override
    protected void initListener() {

    }
}
