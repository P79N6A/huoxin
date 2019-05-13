package com.daimeng.live.fragment;


import android.view.View;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.daimeng.live.adapter.AdverAdapter;
import com.daimeng.live.api.remote.ApiUtils;
import com.daimeng.live.api.remote.PhoneLiveApi;
import com.daimeng.live.bean.AdverBean;
import com.daimeng.live.utils.UIHelper;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;


public class AdvertFragment extends BaseListFragment<AdverBean> {


    @Override
    public void initView(View view) {
        super.initView(view);

        mBaseQuickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                UIHelper.showWebView(getContext(),mListData.get(position).getUrl(),mListData.get(position).getName());
            }
        });
    }

    @Override
    public void initData() {
        super.initData();
        mSwipeRefreshLayout.setRefreshing(true);
        requestData(false);
    }

    @Override
    protected void requestData(boolean refresh) {
        PhoneLiveApi.requestGetAdvertList(new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {

                String res = ApiUtils.checkIsSuccess2(response);
                if(res != null){
                    onRefuseOk(JSON.parseArray(res,AdverBean.class));
                }
            }
        });
    }

    @Override
    public BaseQuickAdapter getAdapter() {
        return new AdverAdapter(mListData);
    }

}
