package com.daimeng.live.base;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimeng.live.R;
import com.daimeng.live.adapter.ViewPageFragmentAdapter;
import com.daimeng.live.utils.TDevice;
import com.daimeng.live.widget.PagerSlidingTabStrip;

/**
 * 带有导航条的基类
 */
public abstract class BaseViewPagerFragment extends BaseFragment {

    protected PagerSlidingTabStrip mTabStrip;
    protected ViewPager mViewPager;
    protected ViewPageFragmentAdapter mTabsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.base_viewpage_fragment, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mTabStrip = (PagerSlidingTabStrip) view
                .findViewById(R.id.tabs);

        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        mTabsAdapter = new ViewPageFragmentAdapter(getChildFragmentManager(), mViewPager);
        setScreenPageLimit();
        onSetupTabAdapter(view,mTabsAdapter, mViewPager);
        mTabStrip.setViewPager(mViewPager);
        mTabStrip.setDividerColor(getResources().getColor(R.color.global));
        mTabStrip.setIndicatorColor(getResources().getColor(R.color.black));
        mTabStrip.setTextColor(getResources().getColor(R.color.black));
        mTabStrip.setTextSize((int) TDevice.dpToPixel(15));
        mTabStrip.setIndicatorHeight(2);
        mTabStrip.setSelectedTextColor(Color.BLACK);
        mTabStrip.setZoomMax(0f);
        mTabStrip.setUnderlineColorResource(R.color.graywhite);

        // if (savedInstanceState != null) {
        // int pos = savedInstanceState.getInt("position");
        // mViewPager.setCurrentItem(pos, true);
        // }
    }
    
    protected void setScreenPageLimit() {
    }

    // @Override
    // public void onSaveInstanceState(Bundle outState) {
    // //No call for super(). Bug on API Level > 11.
    // if (outState != null && mViewPager != null) {
    // outState.putInt("position", mViewPager.getCurrentItem());
    // }
    // //super.onSaveInstanceState(outState);
    // }

    protected abstract void onSetupTabAdapter(View view,ViewPageFragmentAdapter adapter,ViewPager viewPager);
}