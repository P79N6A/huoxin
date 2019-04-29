package com.daimeng.family.ui;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.daimeng.family.fragment.FamilyAuditingFragment;
import com.daimeng.family.fragment.FamilyListFragment;
import com.daimeng.livee.AppContext;
import com.daimeng.livee.R;
import com.daimeng.livee.adapter.ViewPageFragmentAdapter;
import com.daimeng.livee.base.BaseActivity;
import com.daimeng.livee.ui.customviews.ActivityTitle;
import com.daimeng.livee.widget.PagerSlidingTabStrip;

import butterknife.BindView;

public class FamilyManageActivity extends BaseActivity {

    @BindView(R.id.my_pageSlidingTagStrip)
    PagerSlidingTabStrip mPagerSlidingTabStrip;

    @BindView(R.id.my_viewPage)
    ViewPager mViewPager;

    @BindView(R.id.view_title)
    ActivityTitle mActivityTitle;

    private ViewPageFragmentAdapter mViewPageFragmentAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_family_user_list;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void initView() {

        mActivityTitle.setReturnListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Bundle bundle = new Bundle();
        bundle.putString("fid", AppContext.getInstance().getLoginUid());
        bundle.putString("type","1");
        mViewPageFragmentAdapter = new ViewPageFragmentAdapter(getSupportFragmentManager(),mViewPager);
        mViewPageFragmentAdapter.addTab("家族成员","jzcy", FamilyListFragment.class,bundle);
        mViewPageFragmentAdapter.addTab("成员申请","jzsq", FamilyAuditingFragment.class,bundle);
        mViewPager.setAdapter(mViewPageFragmentAdapter);

        mPagerSlidingTabStrip.setViewPager(mViewPager);
        mPagerSlidingTabStrip.setUnderlineColor(getResources().getColor(R.color.white));
        mPagerSlidingTabStrip.setDividerColor(getResources().getColor(R.color.white));
        mPagerSlidingTabStrip.setIndicatorColor(getResources().getColor(R.color.global));
        mPagerSlidingTabStrip.setTextColor(getResources().getColor(R.color.colorGray3));

        mPagerSlidingTabStrip.setSelectedTextColor(getResources().getColor(R.color.black));
        mPagerSlidingTabStrip.setIndicatorHeight(4);
        mPagerSlidingTabStrip.setZoomMax(0f);
        mPagerSlidingTabStrip.setIndicatorColorResource(R.color.global);


        mActivityTitle.setMoreListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void initData() {

    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }
}
