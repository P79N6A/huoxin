package com.daimeng.livee.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.alibaba.fastjson.JSON;
import com.daimeng.livee.AppContext;
import com.daimeng.livee.R;
import com.daimeng.livee.adapter.LiveRedEnvelopeAdapter;
import com.daimeng.livee.api.remote.ApiUtils;
import com.daimeng.livee.api.remote.PhoneLiveApi;
import com.daimeng.livee.bean.LiveBean;
import com.daimeng.livee.bean.RNumBean;
import com.daimeng.livee.bean.RedBean;
import com.daimeng.livee.bean.RedListBean;
import com.daimeng.livee.bean.UserBean;
import com.daimeng.livee.utils.SocketMsgUtils;
import com.daimeng.livee.utils.TDevice;
import com.daimeng.livee.widget.LiveRedEnvelopeOpenView;
import com.daimeng.livee.widget.LiveRedEnvelopeView;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * @author 作者 E-mail:
 * @version 创建时间：2016-5-27 下午7:27:15 类说明
 */
public class LiveRedEnvelopeNewDialog extends DialogFragment {

    @BindView(R.id.fl_content)
    FrameLayout fl_content;// 红包列表

    private LiveRedEnvelopeAdapter adapter;
    private RedBean redBean;
    private boolean isOpen = false;

    private RNumBean rNumBean = new RNumBean();
    private LiveRedEnvelopeOpenView liveRedEnvelopeOpenView;
    private LiveRedEnvelopeView liveRedEnvelopeView;
    private Runnable mDismissRunnable;
    private SocketMsgUtils msgUtils;
    private UserBean mUser;
    private LiveBean mEmceeInfo;
    private ArrayList<RedListBean> redListStr = new ArrayList<>();
    private ArrayList<RedListBean> redListBeans = new ArrayList<>();
    private JSONArray mRedResStr;
    protected List<RecyclerView> mRedViews = new ArrayList<>();

    public static LiveRedEnvelopeNewDialog newInstance(SocketMsgUtils msgUtils, UserBean mUser, LiveBean mEmceeInfo) {
        LiveRedEnvelopeNewDialog dialog = new LiveRedEnvelopeNewDialog();
        dialog.msgUtils = msgUtils;
        dialog.mUser = mUser;
        dialog.mEmceeInfo = mEmceeInfo;
        return dialog;
    }

    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());
//    public LiveRedEnvelopeNewDialog(Activity activity, redEnvelopeMsgBean msg)
//    {
//        super(activity);
//        this.redEnvelopeMsgBean = msg;
//        init();
//    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = new Dialog(getActivity(), R.style.dialog);
        dialog.setContentView(R.layout.dialog_live_red_envelope);
        Window window = dialog.getWindow();

        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        DisplayMetrics dm = new DisplayMetrics();
        params.width = (int) TDevice.getScreenWidth() / 5 * 4;

        params.height = (int) TDevice.getScreenHeight() / 3 * 2 - 40;
        //params.height= WindowManager.LayoutParams.WRAP_CONTENT;
        window.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#99FFFFFF")));
        window.setAttributes(params);

        ButterKnife.bind(this, dialog);
        initView();
        initEvent();
        mDismissRunnable = new Runnable() {
            public void run() {
                dismiss();
            }
        };

        return dialog;
    }

    public DialogFragment startDismissRunnable(long delay) {
        this.stopDismissRunnable();
        MAIN_HANDLER.postDelayed(this.mDismissRunnable, delay);
        return this;
    }

    public DialogFragment stopDismissRunnable() {
        MAIN_HANDLER.removeCallbacks(this.mDismissRunnable);
        return this;
    }


    /**
     * 初始化视图
     */
    private void initView() {

    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        //是否打开过红包
        // createLiveRedEnvelopeView();
        if (TextUtils.isEmpty(msgUtils.getCt())) {
            //打开
            createLiveRedEnvelopeOpenView();
            isOpen = true;
        } else {
            //未打开
            createLiveRedEnvelopeView();
            isOpen = false;
        }
    }

    /**
     * 创建未打开红包的view
     */
    private void createLiveRedEnvelopeView() {
        liveRedEnvelopeView = new LiveRedEnvelopeView(getContext());
//        ViewGroup.LayoutParams lp=liveRedEnvelopeOpenView.getLayoutParams();
//        lp.width=500;
//        lp.height=800;
//        liveRedEnvelopeView.setLayoutParams(lp);
        //TODO by wang
        liveRedEnvelopeView.setIvEnvelopeHead(msgUtils.getUHead());
        liveRedEnvelopeView.setTvEnvelopeName(msgUtils.getUname());

//        liveRedEnvelopeView.setIvEnvelopeHead(redEnvelopeMsgBean.getSender().getHead_image());//头像
//        liveRedEnvelopeView.setTvEnvelopeName(redEnvelopeMsgBean.getSender().getNick_name());//昵称
        liveRedEnvelopeView.setOpenRedpacketClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isOpen = true;
                stopDismissRunnable();
                request_red_envelope();//抢红包


            }
        });
//        liveRedEnvelopeView.setEnvelopeHeadClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //点击头像
//            }
//        });
        liveRedEnvelopeView.setCloseClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LiveRedEnvelopeNewDialog.this.dismiss();//关闭
            }
        });
        fl_content.addView(liveRedEnvelopeView);

    }

    /**
     * 创建打开红包的view
     */
    private void createLiveRedEnvelopeOpenView() {
        liveRedEnvelopeOpenView = new LiveRedEnvelopeOpenView(getContext());
        //TODO by wang
        liveRedEnvelopeOpenView.setIvEnvelopeOpenHead(msgUtils.getUHead());
        liveRedEnvelopeOpenView.setTvEnvelopeOpenName(msgUtils.getUname());

//        liveRedEnvelopeOpenView.setIvEnvelopeOpenHead(redEnvelopeMsgBean.getSender().getHead_image());//头像
//        liveRedEnvelopeOpenView.setTvEnvelopeOpenName(redEnvelopeMsgBean.getSender().getNick_name());//昵称
        if (!TextUtils.isEmpty(msgUtils.getCt())) {
            liveRedEnvelopeOpenView.setTvInfo(rNumBean.getMsg());//信息
        }
        liveRedEnvelopeOpenView.setCloseClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LiveRedEnvelopeNewDialog.this.dismiss();//关闭
            }
        });
        liveRedEnvelopeOpenView.setOnHeadClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击头像
            }
        });
        liveRedEnvelopeOpenView.setOnUserClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopDismissRunnable();
                request_user_red_envelope();//查看手气
            }
        });
        fl_content.addView(liveRedEnvelopeOpenView);
    }

    /**
     * TODO 抢红包 by wang
     */
    private void request_red_envelope() {

        PhoneLiveApi.grabRedbag(mUser.id, mUser.token, mEmceeInfo.uid, mEmceeInfo.stream, msgUtils.getCt(), new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                String resw = ApiUtils.checkIsSuccessReturnString(response);
                JSONObject res = null;
                try {
                    RNumBean numBean = JSON.parseObject(resw, RNumBean.class);
                    // String rednum=numBean.getRednum();
                    res = new JSONObject(response);
                    JSONObject dataJson = res.getJSONObject("data");
                    String code = dataJson.getString("code");
                    String msg = dataJson.getString("msg");
                    rNumBean.setMsg(msg);
                    if (code.equals("0")) {
                        AppContext.showToast(msg);
                        fl_content.removeAllViews();//移除fl中的未打开红包视图
                        createLiveRedEnvelopeOpenView();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    /**
     * //Todo 查看手气 by wang
     */
    private void request_user_red_envelope() {

        PhoneLiveApi.redList(msgUtils.getCt(), new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                liveRedEnvelopeOpenView.setViewShowOrHide();
                adapter = new LiveRedEnvelopeAdapter(redListStr);
                liveRedEnvelopeOpenView.getRed_list().setAdapter(adapter);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                liveRedEnvelopeOpenView.getRed_list().setLayoutManager(layoutManager);
                JSONArray jsonArray = ApiUtils.checkIsSuccess(response);
                mRedResStr = jsonArray;

                //  if (null == mVpProtectAdapter) {
                redListStr.clear();
//                    if (redListStr.size() == 0) {
                try {
                    JSONArray redtListJson = mRedResStr;
                    for (int i = 0; i < redtListJson.length(); i++) {

                        redListStr.add(JSON.parseObject(redtListJson.getString(i), RedListBean.class));
                        // mProtectList.add(protectListJson.getString(i),);
                        adapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

        });


    }

    @Override
    public void show(FragmentManager manager, String tag) {
        //是否打开过红包
        if (!isOpen) {
            //未打开
            startDismissRunnable(6 * 1000);
        }
        super.show(manager, tag);
    }
}
