package com.daimeng.live.ui;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.daimeng.live.adapter.AgentUserAdapter;
import com.daimeng.live.api.remote.ApiUtils;
import com.daimeng.live.api.remote.PhoneLiveApi;
import com.daimeng.live.base.BaseListActivity;
import com.daimeng.live.bean.AgentUserBean;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

public class AgentUserListActivity extends BaseListActivity<AgentUserBean> {



    @Override
    public BaseQuickAdapter getAdapter() {
        return new AgentUserAdapter(this,mListData);
    }

    @Override
    public void initData() {
        super.initData();

        setActionBarTitle("客户列表");
        mSwipeRefreshLayout.setRefreshing(true);
        requestData(false);
    }

    @Override
    public void requestData(boolean isCache) {

        PhoneLiveApi.requestGetAgentUserList(mUserId,mUserToken,page,new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {

                String res = ApiUtils.checkIsSuccess2(response);

                if(res != null){

                    onRefuseOk(JSON.parseArray(res,AgentUserBean.class));
                }
            }
        });
    }
}