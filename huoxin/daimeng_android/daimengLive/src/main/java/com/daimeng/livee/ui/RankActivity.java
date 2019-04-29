package com.daimeng.livee.ui;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.daimeng.livee.R;
import com.daimeng.livee.adapter.ViewPageFragmentAdapter;
import com.daimeng.livee.base.BaseActivity;
import com.daimeng.livee.fragment.ConsumpationIncomeRankFragment;
import com.daimeng.livee.fragment.RankListFragment;
import com.daimeng.livee.utils.TDevice;
import com.daimeng.livee.widget.PagerSlidingTabStrip;

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
