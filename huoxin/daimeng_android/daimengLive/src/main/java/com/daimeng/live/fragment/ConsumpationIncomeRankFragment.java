package com.daimeng.live.fragment;


import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimeng.live.R;
import com.daimeng.live.adapter.ViewPageFragmentAdapter;
import com.daimeng.live.base.BaseFragment;
import com.daimeng.live.utils.TDevice;
import com.daimeng.live.widget.PagerSlidingTabStrip;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ConsumpationIncomeRankFragment extends BaseFragment {

    public static String RANK_TYPE = "RANK_TYPE";

    @BindView(R.id.page_sliding_ta_strip)
    PagerSlidingTabStrip mPagerSlidingTabStrip;

    @BindView(R.id.view_page)
    ViewPager mViewPage;

    private int rankType = 0;

    private ViewPageFragmentAdapter viewPageFragmentAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_consumpation_income_rank, null);
        ButterKnife.bind(this,view);

        initView(view);
        initData();

        return view;
    }

    @Override
    public void initView(View view) {

        rankType = getArguments().getInt(RANK_TYPE);
        initPage();
    }

    @Override
    public void initData() {

    }

    private void initPage(){
        viewPageFragmentAdapter = new ViewPageFragmentAdapter(getChildFragmentManager(),mViewPage);

        Bundle dayBundle = new Bundle();
        dayBundle.putInt(RankListFragment.RANK_TYPE,rankType);
        dayBundle.putString(RankListFragment.RANK_ACTION,"day");


        Bundle monthBundle = new Bundle();
        monthBundle.putInt(RankListFragment.RANK_TYPE,rankType);
        monthBundle.putString(RankListFragment.RANK_ACTION,"month");


        Bundle allBundle = new Bundle();
        allBundle.putInt(RankListFragment.RANK_TYPE,rankType);
        allBundle.putString(RankListFragment.RANK_ACTION,"all");

        viewPageFragmentAdapter.addTab(getString(R.string.day_rank), "day_rank", RankListFragment.class,dayBundle);
        viewPageFragmentAdapter.addTab(getString(R.string.month_rank), "month_rank", RankListFragment.class,monthBundle);
        viewPageFragmentAdapter.addTab(getString(R.string.all_rank), "all_rank", RankListFragment.class,allBundle);
        mViewPage.setAdapter(viewPageFragmentAdapter);

        mPagerSlidingTabStrip.setViewPager(mViewPage);
        mPagerSlidingTabStrip.setUnderlineColor(getResources().getColor(R.color.transparent));
        mPagerSlidingTabStrip.setDividerColor(getResources().getColor(R.color.transparent));
        mPagerSlidingTabStrip.setIndicatorColor(getResources().getColor(R.color.white));
        mPagerSlidingTabStrip.setTextColor(getResources().getColor(R.color.black));
        mPagerSlidingTabStrip.setTextSize((int) TDevice.dpToPixel(15));
        mPagerSlidingTabStrip.setSelectedTextColor(getResources().getColor(R.color.black));
        mPagerSlidingTabStrip.setIndicatorHeight(2);
        mPagerSlidingTabStrip.setZoomMax(0f);
        mPagerSlidingTabStrip.setIndicatorColorResource(R.color.black);
        mPagerSlidingTabStrip.setShouldExpand(true);
    }
}
