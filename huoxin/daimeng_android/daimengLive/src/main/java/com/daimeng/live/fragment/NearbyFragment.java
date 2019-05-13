package com.daimeng.live.fragment;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.daimeng.live.AppContext;
import com.daimeng.live.adapter.VideoAndLiveAdapter;
import com.daimeng.live.api.remote.ApiUtils;
import com.daimeng.live.api.remote.PhoneLiveApi;
import com.daimeng.live.bean.LiveCheckInfoBean;
import com.daimeng.live.bean.ShortVideoBean;
import com.daimeng.live.bean.VideoAndLiveBean;
import com.daimeng.live.event.Event;
import com.daimeng.live.event.EventDefine;
import com.daimeng.live.utils.LiveUtils;
import com.daimeng.live.utils.StringUtils;
import com.daimeng.live.utils.UIHelper;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import okhttp3.Call;

/**
* @dw 附近同城
*
* */
public class NearbyFragment extends BaseListFragment<VideoAndLiveBean> {


    @Override
    public void initView(View view) {
        super.initView(view);
    }

    @Override
    public void initData() {
        super.initData();

        mSwipeRefreshLayout.setRefreshing(true);

    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

        VideoAndLiveBean videoAndLiveBean = mListData.get(position);

        if(StringUtils.toInt(videoAndLiveBean.getType()) == 0){

            requestCheckLive(videoAndLiveBean);
        }else{

            requestGetVideoInfo(videoAndLiveBean);
        }
    }

    //获取短视频信息
    private void requestGetVideoInfo(VideoAndLiveBean data) {

        PhoneLiveApi.requestGetVideoInfo(AppContext.getInstance().getLoginUid(),data.getId(),new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {

                ShortVideoBean data = ApiUtils.checkIsSuccess(response, ShortVideoBean.class);
                if(data != null){

                    UIHelper.showShortVideoPlayer(getActivity(),data);
                }
            }
        });
    }

    //获取直播信息
    private void requestCheckLive(VideoAndLiveBean data) {

        PhoneLiveApi.requestCheckLive(AppContext.getInstance().getLoginUid(),AppContext.getInstance().getToken(),data.getUid(),data.getStream(),new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {

                LiveCheckInfoBean data = ApiUtils.checkIsSuccess(response, LiveCheckInfoBean.class);
                if(data != null){

                    LiveUtils.joinLiveRoom(getContext(),data);
                }
            }
        });
    }

    @Override
    public RecyclerView.LayoutManager getLayoutManage() {

        return new GridLayoutManager(getContext(),2);
    }

    @Override
    public BaseQuickAdapter getAdapter() {

        return new VideoAndLiveAdapter(getContext(),mListData);
    }

    @Override
    protected void requestData(boolean refresh) {

        PhoneLiveApi.requestGetNearListData(AppContext.address,AppContext.lat,AppContext.lng,AppContext.getInstance().getLoginUid(),page,new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {

                String res = ApiUtils.checkIsSuccess2(response);

                if(res != null){

                    onRefuseOk(JSON.parseArray(res,VideoAndLiveBean.class));
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event.CommonEvent event) {

        if(event.action == EventDefine.LOCAL_READY){

            requestData(false);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
