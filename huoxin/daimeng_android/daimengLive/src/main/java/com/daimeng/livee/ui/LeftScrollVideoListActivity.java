package com.daimeng.livee.ui;


import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.daimeng.livee.R;
import com.daimeng.livee.adapter.ViewPageFragmentAdapter;
import com.daimeng.livee.base.BaseActivity;
import com.daimeng.livee.event.Event;
import com.daimeng.livee.fragment.ShortVideoTouchPlayerFragment;
import com.daimeng.livee.fragment.UserVideoListFragment;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;

/**
 * 视频用户中心
 */
public class LeftScrollVideoListActivity extends BaseActivity {


    @BindView(R.id.view_page)
    ViewPager mViewPage;

    public static final String VIDEO_PLAYER_INFO = "VIDEO_PLAYER_INFO";

    private ViewPageFragmentAdapter viewPageFragmentAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_user_video_center;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void initView() {

        Bundle videoBound = getIntent().getBundleExtra("VIDEO_PLAYER_INFO");

        viewPageFragmentAdapter = new ViewPageFragmentAdapter(getSupportFragmentManager(),mViewPage);
        viewPageFragmentAdapter.addTab("视频","video",ShortVideoTouchPlayerFragment.class,videoBound);
        viewPageFragmentAdapter.addTab("用户中心","center",UserVideoListFragment.class,new Bundle());
        mViewPage.setAdapter(viewPageFragmentAdapter);
        mViewPage.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 1){
                    Event.LeftVideoTouchChangeEvent event = new Event.LeftVideoTouchChangeEvent();
                    EventBus.getDefault().post(event);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void initData() {

    }
}
