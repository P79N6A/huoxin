package com.daimeng.livee.fragment;


import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.daimeng.livee.AppContext;
import com.daimeng.livee.adapter.ShortVideoListAdapter;
import com.daimeng.livee.api.remote.ApiUtils;
import com.daimeng.livee.api.remote.PhoneLiveApi;
import com.daimeng.livee.bean.ShortVideoBean;
import com.daimeng.livee.utils.UIHelper;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

/**
 * @author weipeng
 */
public class HotShortVideoFragment extends BaseListFragment<ShortVideoBean> {


    @Override
    public void initView(View view) {
        super.initView(view);
    }

    @Override
    public void initData() {
        super.initData();

        mSwipeRefreshLayout.setRefreshing(true);
        requestData(false);
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

        UIHelper.showShortVideoPlayer(getActivity(),mListData,position,page);
    }

    @Override
    public BaseQuickAdapter getAdapter() {
        return new ShortVideoListAdapter(getContext(),mListData);
    }

    @Override
    public RecyclerView.LayoutManager getLayoutManage() {

        return new GridLayoutManager(getContext(),2);
    }

    @Override
    protected void requestData(boolean refresh) {


        PhoneLiveApi.requestGetHotShortVideoList(AppContext.getInstance().getLoginUid(),AppContext.getInstance().getToken(),page,new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {

                String res = ApiUtils.checkIsSuccess2(response);

                if(res != null){

                    onRefuseOk(JSON.parseArray(res,ShortVideoBean.class));
                }
            }
        });
    }
}
