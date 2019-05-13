package com.daimeng.live.fragment;


import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.daimeng.live.R;
import com.daimeng.live.base.BaseFragment;
import com.daimeng.live.bean.RollPicBean;
import com.daimeng.live.other.BannerGlideImageLoader;
import com.daimeng.live.utils.UIHelper;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public abstract class BaseListFragment<T>  extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.OnItemClickListener {

    protected View mHeadView;
    protected Banner mBanner;
    protected ArrayList<RollPicBean> mSlideBeanList = new ArrayList<>();

    @BindView(R.id.swipeRefreshLayout)
    protected SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.my_recycleView)
    protected RecyclerView mRecyclerView;

    protected int page = 1;

    protected ArrayList<T> mListData = new ArrayList<>();
    protected BaseQuickAdapter mBaseQuickAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_base_list, container, false);

        ButterKnife.bind(this,view);
        initView(view);
        initData();

        return view;
    }

    public void initFirst(){

    }

    @Override
    public void initView(View view) {

        initFirst();

        initRecycleView();
        initSwipeRefreshLayout();
        if(hasBanner()){

            initBanner();
        }
    }

    private void initBanner() {

        mHeadView = View.inflate(getContext(), R.layout.view_hot_rollpic,null);
        mBaseQuickAdapter.addHeaderView(mHeadView);

        mBanner = (Banner) mHeadView.findViewById(R.id.banner);
        mBanner.setImageLoader(new BannerGlideImageLoader());
        //设置banner动画效果
        //mBanner.setBannerAnimation(Transformer.DepthPage);
        mBanner.setImages(mSlideBeanList);
        mBanner.start();
        //轮播点击跳转
        mBanner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {

                RollPicBean slideBean = mSlideBeanList.get(position);
                UIHelper.showWebView(getContext(),slideBean.slide_url,"");
            }
        });;
    }


    protected void initSwipeRefreshLayout(){
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.black));
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }


    @Override
    public void initData() {

    }

    private void initRecycleView() {

        RecyclerView.LayoutManager manage = getLayoutManage();
        mRecyclerView.setLayoutManager(manage);

        mBaseQuickAdapter = getAdapter();
        mRecyclerView.setAdapter(mBaseQuickAdapter);

        mBaseQuickAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {

                page ++;
                requestData(false);
            }
        },mRecyclerView);

        mBaseQuickAdapter.setOnItemClickListener(this);
        //mBaseQuickAdapter.disableLoadMoreIfNotFullPage();

    }

    protected void onRefuseOk(List<T> list){

        mSwipeRefreshLayout.setRefreshing(false);
        if(page != 1){

            if(list.size() == 0){

                mBaseQuickAdapter.loadMoreEnd();
            }else{

                mBaseQuickAdapter.loadMoreComplete();
            }
        }else{

            mBaseQuickAdapter.loadMoreComplete();
            mListData.clear();
        }
        mListData.addAll(list);
        mBaseQuickAdapter.notifyDataSetChanged();
    }


    public RecyclerView.LayoutManager getLayoutManage(){

        return new LinearLayoutManager(getContext());
    }

    public abstract BaseQuickAdapter getAdapter();

    @Override
    public void onRefresh() {

        page = 1;

        requestData(false);
    }

    protected boolean hasBanner(){

        return false;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

    }
}
