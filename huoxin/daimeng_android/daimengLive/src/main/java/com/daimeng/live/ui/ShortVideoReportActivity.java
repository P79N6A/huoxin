package com.daimeng.live.ui;

import android.view.View;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.daimeng.live.AppContext;
import com.daimeng.live.R;
import com.daimeng.live.adapter.ShortVideoReportAdapter;
import com.daimeng.live.api.remote.ApiUtils;
import com.daimeng.live.api.remote.PhoneLiveApi;
import com.daimeng.live.base.BaseListActivity;
import com.daimeng.live.bean.ShortVideoReportBean;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.OnClick;
import okhttp3.Call;

/**
 * @author weipeng
 */
public class ShortVideoReportActivity extends BaseListActivity<ShortVideoReportBean> {

    public static final String SHORT_VIDEO_ID = "SHORT_VIDEO_ID";
    private String mVid;
    private int selectPos = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_short_video_report;
    }

    @Override
    public BaseQuickAdapter getAdapter() {
        return new ShortVideoReportAdapter(mListData);
    }

    @OnClick({R.id.btn_submit})
    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.btn_submit:


                requestShortVideoReport();
                break;
        }
    }

    @Override
    public void initData() {
        super.initData();


        setActionBarTitle("举报");
        mVid = getIntent().getStringExtra(SHORT_VIDEO_ID);
    }

    @Override
    public void initView() {
        super.initView();

        mSwipeRefreshLayout.setRefreshing(true);
        requestData(false);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

        selectPos = position;
        int i = 0;
        for(ShortVideoReportBean data : mListData){
            if(i == selectPos){
                data.setSelect(1);
            }else{
                data.setSelect(0);
            }
            i++;
        }
        mBaseQuickAdapter.notifyDataSetChanged();

    }

    private void requestShortVideoReport() {

        showWaitDialog("正在提交举报信息...",false);
        PhoneLiveApi.requestShortVideoReport(mUserId, mUserToken, mVid,mListData.get(selectPos).getId(), new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

                hideWaitDialog();
            }

            @Override
            public void onResponse(String response, int id) {

                hideWaitDialog();
                String res = ApiUtils.checkIsSuccess2(response);

                if(res != null){

                    AppContext.showToast("举报成功");
                }
            }
        });
    }

    @Override
    public void requestData(boolean isCache) {

        PhoneLiveApi.requestGetShortVideoReportList(new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {

                String res = ApiUtils.checkIsSuccess2(response);
                if(res != null){

                    onRefuseOk(JSON.parseArray(res,ShortVideoReportBean.class));
                }
            }
        });
    }
}
