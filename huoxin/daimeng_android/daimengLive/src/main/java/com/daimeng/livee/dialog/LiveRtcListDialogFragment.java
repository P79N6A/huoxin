package com.daimeng.livee.dialog;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.daimeng.livee.AppContext;
import com.daimeng.livee.R;
import com.daimeng.livee.adapter.LiveRtcListAdapter;
import com.daimeng.livee.api.remote.ApiUtils;
import com.daimeng.livee.api.remote.PhoneLiveApi;
import com.daimeng.livee.base.BaseBottomDialogFragment;
import com.daimeng.livee.bean.LiveRtcListBean;
import com.daimeng.livee.event.Event;
import com.daimeng.livee.ui.RtmpPushActivity;
import com.daimeng.livee.utils.StringUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;

public class LiveRtcListDialogFragment extends BaseBottomDialogFragment implements BaseQuickAdapter.OnItemClickListener {

    @BindView(R.id.rv_content_list)
    RecyclerView mRvContentList;

    private List<LiveRtcListBean> list = new ArrayList<>();
    private RtmpPushActivity activity;
    private LiveRtcListAdapter liveRtcListAdapter;

    public void setPushActivity(RtmpPushActivity activity){
        this.activity = activity;
    }

    @Override
    public int getLayout() {
        return R.layout.dialog_live_rtc_list;
    }

    @Override
    public void initView(View view) {
        mRvContentList.setLayoutManager(new LinearLayoutManager(getContext()));
        liveRtcListAdapter = new LiveRtcListAdapter(list);
        mRvContentList.setAdapter(liveRtcListAdapter);
        liveRtcListAdapter.setOnItemClickListener(this);
    }

    @Override
    public void initData() {

        requestGetData();
    }

    private void requestGetData() {

        PhoneLiveApi.requestGetLiveRtcList(AppContext.getInstance().getLoginUid(),AppContext.getInstance().getToken(),activity.mStreamName,new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {

                String res = ApiUtils.checkIsSuccess2(response);
                if(res != null){
                    list.clear();
                    list.addAll(JSON.parseArray(res,LiveRtcListBean.class));
                    liveRtcListAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        activity = null;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, final int position) {
        if(StringUtils.toInt(list.get(position).getStatus()) == 1){
            new AlertView("提示", "是否断开连麦?", "取消", new String[]{"确定"}, null, getContext(), AlertView.Style.Alert, new OnItemClickListener() {
                @Override
                public void onItemClick(Object o, int i) {

                    if(i == 0){
                        Event.LiveRtcEvent event = new Event.LiveRtcEvent();
                        event.action = 1;
                        event.rtcUserId = list.get(position).getUser_id();
                        EventBus.getDefault().post(event);
                    }
                }
            }).setCancelable(true).show();
        }
    }
}
