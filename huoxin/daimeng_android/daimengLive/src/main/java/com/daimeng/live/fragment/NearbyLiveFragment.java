package com.daimeng.live.fragment;


import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.daimeng.live.AppContext;
import com.daimeng.live.adapter.NewestAdapter;
import com.daimeng.live.api.remote.ApiUtils;
import com.daimeng.live.api.remote.PhoneLiveApi;
import com.daimeng.live.bean.LiveBean;
import com.daimeng.live.event.Event;
import com.daimeng.live.event.EventDefine;
import com.daimeng.live.utils.UIHelper;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import okhttp3.Call;

/**
 * 附近的直播
 */
public class NearbyLiveFragment extends BaseListFragment<LiveBean> {


    @Override
    public void initData() {
        super.initData();

    }

    @Override
    public BaseQuickAdapter getAdapter() {
        return new NewestAdapter(mListData);
    }

    @Override
    public void initView(View view) {
        super.initView(view);

        mBaseQuickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                UIHelper.startRtmpPlayerActivity(getContext(),mListData.get(position));
            }
        });
    }

    @Override
    public RecyclerView.LayoutManager getLayoutManage() {

        return new GridLayoutManager(getContext(),2);
    }

    //附近主播数据请求
    @Override
    protected void requestData(boolean refresh) {

        PhoneLiveApi.requestGetNearLiveListData(AppContext.address,AppContext.lat,AppContext.lng,AppContext.getInstance().getLoginUid(),page,new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {

                String res = ApiUtils.checkIsSuccess2(response);

                if(res != null){

                    onRefuseOk(JSON.parseArray(res,LiveBean.class));
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event.CommonEvent event) {
        if(event.action == EventDefine.LOCAL_READY){
            mSwipeRefreshLayout.setRefreshing(true);
            requestData(false);
        }
    }

    @Override
    protected boolean isSubEvent() {
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag("requestGetNearListData");
    }

}
