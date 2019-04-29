package com.daimeng.livee.ui;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.daimeng.livee.adapter.AgentProfitRecordAdapter;
import com.daimeng.livee.api.remote.ApiUtils;
import com.daimeng.livee.api.remote.PhoneLiveApi;
import com.daimeng.livee.base.BaseListActivity;
import com.daimeng.livee.bean.AgentProfitRecordBean;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

public class AgentProfitRecordActivity extends BaseListActivity<AgentProfitRecordBean> {



    @Override
    public BaseQuickAdapter getAdapter() {
        return new AgentProfitRecordAdapter(mListData);
    }

    @Override
    public void initData() {
        super.initData();

        setActionBarTitle("推广充值收益记录");

        mSwipeRefreshLayout.setRefreshing(true);
        requestData(false);
    }

    @Override
    public void requestData(boolean isCache) {

        PhoneLiveApi.requestGetAgentProfitRecord(mUserId,mUserToken,page,new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {

                String res = ApiUtils.checkIsSuccess2(response);

                if(res != null){

                    onRefuseOk(JSON.parseArray(res,AgentProfitRecordBean.class));
                }
            }
        });
    }
}
