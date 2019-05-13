package com.daimeng.live.ui;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.daimeng.live.R;
import com.daimeng.live.adapter.ViewPageFragmentAdapter;
import com.daimeng.live.base.BaseActivity;
import com.daimeng.live.fragment.ConsumpationIncomeRankFragment;
import com.daimeng.live.fragment.RankListFragment;
import com.daimeng.live.utils.TDevice;
import com.daimeng.live.widget.PagerSlidingTabStrip;

import butterknife.BindView;

public class RankActivity extends BaseActivity {

    @BindView(R.id.page_sliding_ta_strip)
    PagerSlidingTabStrip mPagerSlidingTabStrip;

    @BindView(R.id.view_page)
    ViewPager mViewPage;


    private ViewPageFragmentAdapter viewPageFragmentAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_rank;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void initView() {

        initPage();
    }

    @Override
    public void initData() {

    }

    private void initPage(){
        viewPageFragmentAdapter = new ViewPageFragmentAdapter(getSupportFragmentManager(),mViewPage);
        Bundle incomeRank = new Bundle();
        incomeRank.putInt(RankListFragment.RANK_ACTION,0);

        Bundle consumpationRank = new Bundle();
        consumpationRank.putInt(ConsumpationIncomeRankFragment.RANK_TYPE,1);
        viewPageFragmentAdapter.addTab(getString(R.string.income_rank), "income_rank", ConsumpationIncomeRankFragment.class,incomeRank );
        viewPageFragmentAdapter.addTab(getString(R.string.consumpation_rank), "consumpation_rank", ConsumpationIncomeRankFragment.class,consumpationRank);
        mViewPage.setAdapter(viewPageFragmentAdapter);

        mViewPage.setOffscreenPageLimit(2);

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
