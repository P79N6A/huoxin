package com.daimeng.livee.ui;


import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.daimeng.livee.AppContext;
import com.daimeng.livee.adapter.CloudVideoAdapter;
import com.daimeng.livee.api.remote.ApiUtils;
import com.daimeng.livee.api.remote.PhoneLiveApi;
import com.daimeng.livee.bean.CloudVideoBean;
import com.daimeng.livee.bean.callback.RequestGetCloudVideoInfoCallback;
import com.daimeng.livee.bean.callback.RequestPayCloudVideoCallback;
import com.daimeng.livee.fragment.BaseListFragment;
import com.daimeng.livee.utils.DialogHelp;
import com.daimeng.livee.utils.StringUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

/**
 * 云视频点播
 */
public class CloudVideoFragment extends BaseListFragment<CloudVideoBean> {


    @Override
    public void initData() {
        super.initData();

        mSwipeRefreshLayout.setRefreshing(true);
        requestData(false);
    }

    @Override
    public void initView(View view) {
        super.initView(view);
        mBaseQuickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                requestGetVideoInfo(mListData.get(position));
            }
        });
    }

    /*
    * 获取视频信息
    * */
    private void requestGetVideoInfo(final CloudVideoBean cloudVideoBean) {

        showWaitDialog("正在获取信息...");
        PhoneLiveApi.requestGetCloudVideoInfo(AppContext.getInstance().getLoginUid(),AppContext.getInstance().getToken(),cloudVideoBean.getId(),new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {

                hideWaitDialog();
            }

            @Override
            public void onResponse(String response, int id) {
                hideWaitDialog();
                String res = ApiUtils.checkIsSuccess2(response);
                if(res != null){
                    RequestGetCloudVideoInfoCallback callback = JSON.parseObject(res,RequestGetCloudVideoInfoCallback.class);

                    if(StringUtils.toInt(callback.getPay_type()) == 0 || StringUtils.toInt(callback.getIs_pay()) == 1){
                        Intent intent = new Intent(getContext(),CloudVideoPlayerActivity.class);
                        intent.putExtra(CloudVideoPlayerActivity.VIDEO_COVER_URL,cloudVideoBean.getCover_url());
                        intent.putExtra(CloudVideoPlayerActivity.VIDEO_URL,callback.getVideo_url());
                        intent.putExtra(CloudVideoPlayerActivity.VIDEO_PAY_TYPE,callback.getPay_type());
                        intent.putExtra(CloudVideoPlayerActivity.VIDEO_MONEY,callback.getMoney());
                        intent.putExtra(CloudVideoPlayerActivity.VIDEO_ID,cloudVideoBean.getId());
                        startActivity(intent);
                        return;
                    }
                    if(StringUtils.toInt(callback.getIs_pay()) != 1){

                        DialogHelp.getConfirmDialog(getContext(), "视频为付费视频是否继续观看？", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                clickBuyVideo(cloudVideoBean);
                            }
                        }).show();
                    }
                }
            }
        });
    }

    //点击支付观看
    private void clickBuyVideo(final CloudVideoBean cloudVideoBean) {

        showWaitDialog("正在加载中...");
        PhoneLiveApi.requestBuyCloudVideo(cloudVideoBean.getId(),AppContext.getInstance().getLoginUid(),AppContext.getInstance().getToken(),new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {
                hideWaitDialog();
            }

            @Override
            public void onResponse(String response, int id) {

                hideWaitDialog();
                String res = ApiUtils.checkIsSuccess2(response);
                if(res != null){
                    RequestPayCloudVideoCallback callback = JSON.parseObject(res,RequestPayCloudVideoCallback.class);
                    Intent intent = new Intent(getContext(),CloudVideoPlayerActivity.class);
                    intent.putExtra(CloudVideoPlayerActivity.VIDEO_COVER_URL,cloudVideoBean.getCover_url());
                    intent.putExtra(CloudVideoPlayerActivity.VIDEO_URL,callback.getVideo_url());
                    intent.putExtra(CloudVideoPlayerActivity.VIDEO_PAY_TYPE,callback.getPay_type());
                    intent.putExtra(CloudVideoPlayerActivity.VIDEO_MONEY,callback.getMoney());
                    intent.putExtra(CloudVideoPlayerActivity.VIDEO_ID,cloudVideoBean.getId());
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    public BaseQuickAdapter getAdapter() {
        return new CloudVideoAdapter(mListData);
    }

    @Override
    public RecyclerView.LayoutManager getLayoutManage() {
        return new GridLayoutManager(getContext(),2);
    }

    @Override
    protected void requestData(boolean refresh) {

        //mSwipeRefreshLayout.setRefreshing(true);
        PhoneLiveApi.requestGetCloudVideoList(page,new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                String res = ApiUtils.checkIsSuccess2(response);
                if(res != null){
                    onRefuseOk(JSON.parseArray(res,CloudVideoBean.class));
                }
            }
        });
    }
}
