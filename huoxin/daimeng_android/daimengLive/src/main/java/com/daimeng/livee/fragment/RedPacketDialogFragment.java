package com.daimeng.livee.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.daimeng.livee.AppContext;
import com.daimeng.livee.R;
import com.daimeng.livee.api.remote.ApiUtils;
import com.daimeng.livee.api.remote.PhoneLiveApi;
import com.daimeng.livee.bean.RedBean;
import com.daimeng.livee.bean.RedNumBean;
import com.daimeng.livee.bean.UserBean;
import com.daimeng.livee.interf.RedPacketSendCallBack;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.angmarch.views.NiceSpinner;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.Arrays;
import java.util.LinkedList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 直播间用户列表点击弹窗页面
 */
public class RedPacketDialogFragment extends DialogFragment implements View.OnClickListener {

    @BindView(R.id.spinner_packet_part)
    NiceSpinner spinner_packet_part;

    @BindView(R.id.spinner_packet_count)
    NiceSpinner spinner_packet_count;
    @BindView(R.id.overage_num)
    TextView overage_num;
    @BindView(R.id.red_packet_close)
    ImageView red_packet_close;
    private JSONArray mRedPacketStr;
    private LinkedList<String> data_part;
    private LinkedList<String> data_count;
    private String hostId;
    private String stream;
    private RedPacketSendCallBack callBack;

    public static RedPacketDialogFragment newInstance(String hostId, String stream, RedPacketSendCallBack callBack) {
        RedPacketDialogFragment fragment = new RedPacketDialogFragment();
        fragment.hostId = hostId;
        fragment.stream = stream;
        fragment.callBack = callBack;
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = new Dialog(getActivity(), R.style.dialog);
        dialog.getWindow().setBackgroundDrawable(new
                ColorDrawable(Color.TRANSPARENT));
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.red_packet_dialog, null);
        dialog.setContentView(view);

        ButterKnife.bind(this, view);
        initView(view);
        initData();
        return dialog;
    }

    private void initData() {
        UserBean mUser = AppContext.getInstance().getLoginUser();
        PhoneLiveApi.getRedNum(mUser.id, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                String res = ApiUtils.checkIsSuccessReturnString(response);
                RedNumBean redNumBean = JSON.parseObject(res, RedNumBean.class);
                //红包价值
                data_count = new LinkedList<>(Arrays.asList(redNumBean.getRedcoin()));
                spinner_packet_count.attachDataSource(data_count);
                //红包数量
                data_part = new LinkedList<>(Arrays.asList(redNumBean.getRednum()));
                spinner_packet_part.attachDataSource(data_part);
                overage_num.setText(redNumBean.getCoin());

            }
        });


//        //红包价值
//        data_count = new LinkedList<>(Arrays.asList("1000", "2000", "5000", "10000"));
//        spinner_packet_count.attachDataSource(data_count);
//
//        //红包数量
//        data_part = new LinkedList<>(Arrays.asList("10", "20", "50", "100"));
//        spinner_packet_part.attachDataSource(data_part);


    }


    private void initView(final View view) {
        spinner_packet_part.setBackgroundResource(R.drawable.bg_spinner);
        spinner_packet_part.setGravity(Gravity.CENTER);

        spinner_packet_count.setBackgroundResource(R.drawable.bg_spinner);
        spinner_packet_count.setGravity(Gravity.CENTER);
    }

    @OnClick({R.id.red_packet_close, R.id.bt_send_red_packet})
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.red_packet_close:
                dismiss();
                break;
            case R.id.bt_send_red_packet:
                sendRedPacket();
                break;
        }
    }


    private void sendRedPacket() {
        final UserBean mUser = AppContext.getInstance().getLoginUser();
        PhoneLiveApi.createRedbag(mUser.id, mUser.token, hostId, stream, data_count.get(spinner_packet_count.getSelectedIndex()), data_part.get(spinner_packet_part.getSelectedIndex()), new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                JSONArray res = ApiUtils.checkIsSuccess(response);

                if (res != null && callBack != null) {
                    try {
                        RedBean rb = JSON.parseObject(res.getString(0), RedBean.class);
                        String token = rb.getGifttoken();
                        String redid = rb.getRedid();
                        callBack.onSuccess(redid);
                        AppContext.showToast("发送成功！");
                        dismiss();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    mIMControl.doSendRedPacket(mUser, hostId, response);
                }
            }
        });

        //Toast.makeText(getContext(), "发送" + data_count.get(spinner_packet_count.getSelectedIndex()) + "钻石，分成" + data_part.get(spinner_packet_part.getSelectedIndex()), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag("getPmUserInfo");
        OkHttpUtils.getInstance().cancelTag("getUserInfo");
        OkHttpUtils.getInstance().cancelTag("getIsFollow");
        OkHttpUtils.getInstance().cancelTag("report");
        OkHttpUtils.getInstance().cancelTag("setCloseLive");
        OkHttpUtils.getInstance().cancelTag("setKick");
        OkHttpUtils.getInstance().cancelTag("setShutUp");
    }


}
