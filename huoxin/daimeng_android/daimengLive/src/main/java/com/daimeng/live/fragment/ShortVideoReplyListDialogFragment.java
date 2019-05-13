package com.daimeng.live.fragment;


import android.app.Dialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.daimeng.live.AppContext;
import com.daimeng.live.R;
import com.daimeng.live.adapter.ShortVideoReplyListAdapter;
import com.daimeng.live.api.remote.ApiUtils;
import com.daimeng.live.api.remote.PhoneLiveApi;
import com.daimeng.live.bean.callback.GetVideoReplyListBean;
import com.daimeng.live.bean.ShortVideoReplyBean;
import com.daimeng.live.event.Event;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;

import okhttp3.Call;


public class ShortVideoReplyListDialogFragment extends BaseListDialogFragment<ShortVideoReplyBean> {

    public static final String VIDEO_ID = "VIDEO_ID";

    private TextView mTvReplyNum;

    private String vid;
    private ImageView mIvClose;

    @Override
    public void initData() {
        super.initData();

        vid = getArguments().getString(VIDEO_ID);
        mSwipeRefreshLayout.setRefreshing(true);

        requestData(false);
    }


    @Override
    public void initView(Dialog view) {
        super.initView(view);

        mTvReplyNum = (TextView) view.findViewById(R.id.tv_reply_num);
        mIvClose = (ImageView) view.findViewById(R.id.iv_close);
        mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        mBaseQuickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                //UIHelper.showHomePageActivity(getContext(),mListData.get(position).getUid());
                Event.ShortVideoPlayer event = new Event.ShortVideoPlayer();
                event.action = Event.ShortVideoPlayer.SHORT_VIDEO_REPLY_COMMENT;
                event.data = mListData.get(position);
                EventBus.getDefault().post(event);

            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_short_video_reply_list;
    }

    @Override
    public BaseQuickAdapter getAdapter() {
        return new ShortVideoReplyListAdapter(getContext(),mListData);
    }

    @Override
    protected void requestData(boolean refresh) {

        PhoneLiveApi.requestGetVideoReplyList(AppContext.getInstance().getLoginUid(),vid,page,new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {

                GetVideoReplyListBean res = ApiUtils.checkIsSuccess(response,GetVideoReplyListBean.class);

                if(res != null && isAdded()){

                    mTvReplyNum.setText(getResources().getString(R.string.short_video_reply_num,res.getCount()));
                    onRefuseOk(res.getList());
                }
            }
        });
    }
}
