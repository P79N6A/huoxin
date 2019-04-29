package com.daimeng.livee.fragment;


import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.daimeng.livee.AppContext;
import com.daimeng.livee.adapter.ShortVideoListAdapter;
import com.daimeng.livee.api.remote.ApiUtils;
import com.daimeng.livee.api.remote.PhoneLiveApi;
import com.daimeng.livee.bean.ShortVideoBean;
import com.daimeng.livee.event.Event;
import com.daimeng.livee.utils.UIHelper;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import okhttp3.Call;

/**
 * 用户中心
 */
public class UserVideoListFragment extends BaseListFragment<ShortVideoBean> {

    @Override
    public void initView(View view) {
        super.initView(view);
    }

    @Override
    public void initData() {
        super.initData();
    }

    @Override
    public BaseQuickAdapter getAdapter() {
        return new ShortVideoListAdapter(getContext(),mListData);
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

        UIHelper.showShortVideoPlayer(getActivity(),mListData,position,page);
    }

    @Override
    public RecyclerView.LayoutManager getLayoutManage() {
        return new GridLayoutManager(getContext(),2);
    }

    @Override
    protected void requestData(boolean refresh) {
        PhoneLiveApi.requestGetShortVideoList(AppContext.getInstance().getLoginUid(), AppContext.getInstance().getToken(),
                String.valueOf(ShortVideoTouchPlayerFragment.select_video_user_id), page, new StringCallback() {
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event.LeftVideoTouchChangeEvent event) {
        page = 1;
        requestData(false);
    }
        @Override
    protected boolean isSubEvent() {
        return true;
    }
}
