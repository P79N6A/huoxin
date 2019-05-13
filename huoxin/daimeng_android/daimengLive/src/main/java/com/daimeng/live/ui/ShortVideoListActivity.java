package com.daimeng.live.ui;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.daimeng.live.adapter.ShortVideoListAdapter;
import com.daimeng.live.api.remote.ApiUtils;
import com.daimeng.live.api.remote.PhoneLiveApi;
import com.daimeng.live.base.BaseListActivity;
import com.daimeng.live.bean.ShortVideoBean;
import com.daimeng.live.utils.UIHelper;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

public class ShortVideoListActivity extends BaseListActivity<ShortVideoBean> {


    @Override
    public void initData() {
        super.initData();
        setActionBarTitle("我的短视频");

        mSwipeRefreshLayout.setRefreshing(true);
        requestData(false);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

        UIHelper.showShortVideoPlayer(ShortVideoListActivity.this,mListData.get(position));
    }

    @Override
    public BaseQuickAdapter getAdapter() {
        return new ShortVideoListAdapter(this,mListData);
    }

    @Override
    public RecyclerView.LayoutManager getLayoutManage() {

        return new GridLayoutManager(this,2);
    }

    @Override
    public void requestData(boolean isCache) {

        PhoneLiveApi.requestGetShortVideoList(mUserId,mUserToken,mUserId,page,new StringCallback(){

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
