package com.daimeng.livee.fragment;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.daimeng.livee.AppConfig;
import com.daimeng.livee.R;
import com.daimeng.livee.utils.TDevice;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseListDialogFragment<T>  extends DialogFragment implements SwipeRefreshLayout.OnRefreshListener {


    protected SwipeRefreshLayout mSwipeRefreshLayout;

    protected RecyclerView mRecyclerView;

    protected int page = 1;

    protected List<T> mListData = new ArrayList<>();
    protected BaseQuickAdapter mBaseQuickAdapter;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = new Dialog(getActivity(),R.style.dialog_gift);
        dialog.setContentView(getLayoutId());
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = (int) (TDevice.getScreenHeight() / 2);
        window.setWindowAnimations(R.style.BottomToTopAnim);
        window.setAttributes(params);

        initView(dialog);
        initData();

        return dialog;
    }

    protected int getLayoutId(){
        return R.layout.fragment_base_list;
    }

    public void initFirst(){

    }

    public void initView(Dialog view) {

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycleView);

        initFirst();
        initRecycleView();
        initSwipeRefreshLayout();
    }

    protected void initSwipeRefreshLayout(){

        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.global));
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }


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

                if(mListData.size() >= AppConfig.PAGE_DATA_COUNT){

                    page ++;
                    requestData(false);
                }else{

                    mBaseQuickAdapter.loadMoreEnd();
                }
            }
        },mRecyclerView);

    }

    protected void onRefuseOk(List<T> list){

        if(!mSwipeRefreshLayout.isRefreshing()){

            if(list.size() < AppConfig.PAGE_DATA_COUNT){

                mBaseQuickAdapter.loadMoreEnd();
            }else{

                mBaseQuickAdapter.loadMoreComplete();
            }
        }else{

            mListData.clear();
            mSwipeRefreshLayout.setRefreshing(false);
            mBaseQuickAdapter.loadMoreComplete();
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

    protected void requestData(boolean isCache){

    }
}
