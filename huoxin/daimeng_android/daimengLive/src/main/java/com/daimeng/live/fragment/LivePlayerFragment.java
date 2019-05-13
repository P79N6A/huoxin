package com.daimeng.live.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimeng.live.R;
import com.daimeng.live.base.BaseFragment;

import butterknife.ButterKnife;


public class LivePlayerFragment extends BaseFragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_live_player, container, false);

        ButterKnife.bind(this,view);

        initView(view);
        initData();
        return view;
    }


    @Override
    public void initData() {

    }

    @Override
    public void initView(View view) {

    }
}
