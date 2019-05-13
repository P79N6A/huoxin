package com.daimeng.live.ui;

import android.view.View;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.daimeng.live.AppContext;
import com.daimeng.live.adapter.NewsMessageAdapter;
import com.daimeng.live.api.remote.ApiUtils;
import com.daimeng.live.api.remote.PhoneLiveApi;
import com.daimeng.live.base.BaseListActivity;
import com.daimeng.live.bean.NewsMessageBean;
import com.daimeng.live.bean.ShortVideoBean;
import com.daimeng.live.utils.UIHelper;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

public class NewsMessageActivity extends BaseListActivity<NewsMessageBean> {


    @Override
    public void initData() {
        super.initData();

        mSwipeRefreshLayout.setRefreshing(true);
        requestData(true);
    }

    @Override
    public void initView() {
        super.initView();

        setActionBarTitle("消息");

        mBaseQuickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                requestGetVideoInfo(mListData.get(position).getVid());
            }
        });
    }

    //获取短视频信息
    private void requestGetVideoInfo(String vid) {

        PhoneLiveApi.requestGetVideoInfo(AppContext.getInstance().getLoginUid(),vid,new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {

                ShortVideoBean data = ApiUtils.checkIsSuccess(response, ShortVideoBean.class);
                if(data != null){

                    UIHelper.showShortVideoPlayer(NewsMessageActivity.this,data);
                }
            }
        });
    }

    @Override
    public BaseQuickAdapter getAdapter() {

        return new NewsMessageAdapter(this,mListData);
    }


    @Override
    public void requestData(boolean isCache) {


        PhoneLiveApi.requestGetNewsMessage(mUserId,mUserToken,page,new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {

                String res = ApiUtils.checkIsSuccess2(response);
                if(res != null){

                    onRefuseOk(JSON.parseArray(res,NewsMessageBean.class));
                }
            }
        });
    }
}
