package com.daimeng.livee.fragment;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.daimeng.livee.adapter.VideoAndLiveAdapter;
import com.daimeng.livee.bean.LiveCheckInfoBean;
import com.daimeng.livee.bean.ShortVideoBean;
import com.daimeng.livee.bean.VideoAndLiveBean;
import com.daimeng.livee.utils.LiveUtils;
import com.daimeng.livee.utils.StringUtils;
import com.daimeng.livee.AppContext;
import com.daimeng.livee.api.remote.ApiUtils;
import com.daimeng.livee.api.remote.PhoneLiveApi;
import com.daimeng.livee.utils.UIHelper;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

/**
 * 首页左边关注
 */
public class AttentionFragment extends BaseListFragment<VideoAndLiveBean> {


    @Override
    public void initView(View view) {
        super.initView(view);
    }

    @Override
    public void initData() {
        super.initData();

        mSwipeRefreshLayout.setRefreshing(true);
        requestData(false);

    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

        VideoAndLiveBean videoAndLiveBean = mListData.get(position);

        if (StringUtils.toInt(videoAndLiveBean.getType()) == 0) {

            requestCheckLive(videoAndLiveBean);
        } else {

            requestGetVideoInfo(videoAndLiveBean);
        }
    }

    //获取短视频信息
    private void requestGetVideoInfo(VideoAndLiveBean data) {

        PhoneLiveApi.requestGetVideoInfo(AppContext.getInstance().getLoginUid(), data.getId(), new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {

                ShortVideoBean data = ApiUtils.checkIsSuccess(response, ShortVideoBean.class);
                if (data != null) {

                    UIHelper.showShortVideoPlayer(getActivity(), data);
                }
            }
        });
    }

    //获取直播信息
    private void requestCheckLive(VideoAndLiveBean data) {

        PhoneLiveApi.requestCheckLive(AppContext.getInstance().getLoginUid(), AppContext.getInstance().getToken(), data.getUid(), data.getStream(), new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {

                LiveCheckInfoBean data = ApiUtils.checkIsSuccess(response, LiveCheckInfoBean.class);
                if (data != null) {

                    LiveUtils.joinLiveRoom(getContext(), data);
                }
            }
        });
    }

    @Override
    public RecyclerView.LayoutManager getLayoutManage() {

        return new GridLayoutManager(getContext(), 2);
    }

    @Override
    public BaseQuickAdapter getAdapter() {

        return new VideoAndLiveAdapter(getContext(), mListData);
    }

    @Override
    protected void requestData(boolean refresh) {

        PhoneLiveApi.requestGetFollowListData(AppContext.getInstance().getLoginUid(), page, new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {

                String res = ApiUtils.checkIsSuccess2(response);

                if (res != null) {

                    onRefuseOk(JSON.parseArray(res, VideoAndLiveBean.class));
                }
            }
        });
    }


}
