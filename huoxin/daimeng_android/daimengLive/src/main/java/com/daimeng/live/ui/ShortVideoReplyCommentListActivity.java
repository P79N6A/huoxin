package com.daimeng.live.ui;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.daimeng.live.AppContext;
import com.daimeng.live.adapter.ShortVideoReplyCommentListAdapter;
import com.daimeng.live.api.remote.ApiUtils;
import com.daimeng.live.api.remote.PhoneLiveApi;
import com.daimeng.live.base.BaseListActivity;
import com.daimeng.live.bean.ShortVideoReplyBean;
import com.daimeng.live.bean.callback.GetVideoReplyListBean;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

public class ShortVideoReplyCommentListActivity extends BaseListActivity<ShortVideoReplyBean> {

    public static final String RID = "rid";


    @Override
    public void initData() {
        super.initData();

        requestData(false);
    }

    @Override
    public void initView() {
        super.initView();

        setActionBarTitle("回复");
    }

    @Override
    public BaseQuickAdapter getAdapter() {
        return new ShortVideoReplyCommentListAdapter(mListData);
    }

    @Override
    public void requestData(boolean refresh) {

        PhoneLiveApi.requestGetReplyCommentList(AppContext.getInstance().getLoginUid(),getIntent().getStringExtra(RID),page,new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {

                GetVideoReplyListBean res = ApiUtils.checkIsSuccess(response,GetVideoReplyListBean.class);

                if(res != null){

                    onRefuseOk(res.getList());
                }
            }
        });
    }


}
