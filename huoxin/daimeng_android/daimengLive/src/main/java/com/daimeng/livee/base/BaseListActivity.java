package com.daimeng.livee.base;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.daimeng.livee.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public abstract class BaseListActivity<T> extends BaseTitleActivity implements SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.OnItemClickListener {

    @BindView(R.id.swipeRefreshLayout)
    protected SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.my_recycleView)
    protected RecyclerView mRecyclerView;

    protected int page = 1;

    protected List<T> mListData = new ArrayList<>();
    protected BaseQuickAdapter mBaseQuickAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_base_list;
    }

    protected void initSwipeRefreshLayout(){

        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.global));
        mSwipeRefreshLayout.setOnRefreshListener(this);
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
        mBaseQuickAdapter.disableLoadMoreIfNotFullPage();
        mBaseQuickAdapter.setOnItemClickListener(this);

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

        return new LinearLayoutManager(this);
    }

    public void initFirst(){

    }

    public abstract BaseQuickAdapter getAdapter();

    @Override
    public void onRefresh() {

        page = 1;

        requestData(false);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void initView() {

        initFirst();

        initRecycleView();
        initSwipeRefreshLayout();

    }

    @Override
    public void initData() {

    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

    }
}
