package com.daimeng.live.fragment;

import android.app.Dialog;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.daimeng.live.adapter.GiftCountSelectAdapter;
import com.daimeng.live.adapter.GiftListAdapter;
import com.daimeng.live.base.ShowLiveActivityBase;
import com.daimeng.live.bean.GiftSelectCountBean;
import com.daimeng.live.utils.TDevice;
import com.daimeng.live.AppContext;
import com.daimeng.live.R;
import com.daimeng.live.adapter.ViewPageGridViewAdapter;
import com.daimeng.live.api.remote.ApiUtils;
import com.daimeng.live.api.remote.PhoneLiveApi;
import com.daimeng.live.bean.GiftBean;
import com.daimeng.live.bean.UserBean;
import com.daimeng.live.event.Event;
import com.daimeng.live.ui.RtmpPlayerActivity;
import com.daimeng.live.utils.UIHelper;
import com.daimeng.live.widget.BlackTextView;
import com.entity.TuPianRootBean;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * @dw 直播间礼物列表
 */

public class GiftListDialogFragment extends DialogFragment {
    List<TuPianRootBean> list1=new ArrayList<>();
    private TextView tv_bb, tv_gift, tv_gift_count;

    private RelativeLayout mSendGiftLian;

    private BlackTextView mUserCoin;

    //赠送礼物按钮
    private Button mBtnSendGiftBtn;

    private List<GiftBean> mGiftList = new ArrayList<>();

    private ViewPageGridViewAdapter mVpGiftAdapter;

    //礼物view
    private ViewPager mVpGiftView;

    //礼物服务端返回数据
    private JSONArray mGiftResStr;

    //当前选中的礼物
    private GiftBean mSelectedGiftItem;
    //当前选中的position
    private int mpos;

    private int mShowGiftSendOutTime = 5;

    protected List<RecyclerView> mGiftViews = new ArrayList<>();

    private UserBean mUser;

    private Handler mHandler = new Handler();
    private int mCount = 0;
    private int status = 0;

    //礼物item填充
    private List<View> mGiftViewList = new ArrayList<>();

    private int sendPackGiftCount = 1;
    private List<GiftSelectCountBean> packGiftSelectCountList;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mUser = AppContext.getInstance().getLoginUser();

        Dialog dialog = new Dialog(getActivity(), R.style.dialog_gift);
        dialog.setContentView(R.layout.view_show_viewpager);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            params.height = (int) TDevice.dpToPixel(150);
        }
        window.setAttributes(params);
        initView(dialog);
        initData();
        return dialog;
    }


    //展示礼物列表
    private void initView(Dialog dialog) {
        tv_bb = dialog.findViewById(R.id.tv_bb);
        tv_gift = dialog.findViewById(R.id.tv_gift);
        tv_gift_count = dialog.findViewById(R.id.tv_gift_count);

        mVpGiftView = (ViewPager) dialog.findViewById(R.id.vp_gift_page);
        mVpGiftAdapter = new ViewPageGridViewAdapter(mGiftViewList);
        mVpGiftView.setAdapter(mVpGiftAdapter);

        tv_gift_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View popupView = LayoutInflater.from(getContext()).inflate(R.layout.pop_gift_count, null);
                final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

                LinearLayout rl_root = popupView.findViewById(R.id.ll_root);
                RecyclerView rv_content_list = popupView.findViewById(R.id.rv_content_list);
                rv_content_list.setLayoutManager(new LinearLayoutManager(getContext()));
                GiftCountSelectAdapter adapter = new GiftCountSelectAdapter(packGiftSelectCountList);
                rv_content_list.setAdapter(adapter);
                adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                        sendPackGiftCount = packGiftSelectCountList.get(position).getCount();
                        tv_gift_count.setText("数量: X " + sendPackGiftCount);
                        popupWindow.dismiss();
                    }
                });

                rl_root.measure(0, 0);
                int height = rl_root.getMeasuredHeight();

                popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                popupWindow.setBackgroundDrawable(new BitmapDrawable());
                popupWindow.setOutsideTouchable(true);
                int[] location = new int[2];
                view.getLocationOnScreen(location);
                popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, location[0], location[1] - height);
            }
        });

        //背包
        dialog.findViewById(R.id.tv_bb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status = 1;
                tv_gift.setTextColor(getContext().getResources().getColor(R.color.white));
                tv_bb.setTextColor(getContext().getResources().getColor(R.color.global));
                tv_gift_count.setVisibility(View.VISIBLE);
                changeSendGiftBtnStatue(false);
                requestPackGiftData();
            }
        });

        tv_gift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status = 0;
                tv_gift.setTextColor(getContext().getResources().getColor(R.color.global));
                tv_bb.setTextColor(getContext().getResources().getColor(R.color.white));
                tv_gift_count.setVisibility(View.GONE);
                changeSendGiftBtnStatue(false);
                requestGiftData();
            }
        });

        mUserCoin = (BlackTextView) dialog.findViewById(R.id.tv_show_select_user_coin);
        mUserCoin.setText(mUser.coin);
        //点击底部跳转充值页面
        dialog.findViewById(R.id.rl_show_gift_bottom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showMyDiamonds(getActivity());
            }
        });
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

            LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(ViewPager.LayoutParams.MATCH_PARENT, (int) TDevice.dpToPixel(100));
            mVpGiftView.setLayoutParams(p1);
        }
        mSendGiftLian = (RelativeLayout) dialog.findViewById(R.id.iv_show_send_gift_lian);
        mSendGiftLian.bringToFront();
        mSendGiftLian.setOnClickListener(new View.OnClickListener() {//礼物连送
            @Override
            public void onClick(View v) {
                doSendGift("y");//礼物发送
                mShowGiftSendOutTime = 5;
                ((TextView) mSendGiftLian.findViewById(R.id.tv_show_gift_outtime)).setText(String.valueOf(mShowGiftSendOutTime));
            }
        });
        mBtnSendGiftBtn = (Button) dialog.findViewById(R.id.btn_show_send_gift);
        mBtnSendGiftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSendGift(v);
            }
        });
        if (mSelectedGiftItem != null) {
            mBtnSendGiftBtn.setBackgroundResource(R.drawable.bg_send_gift);
        }

    }

    private void initData() {

        packGiftSelectCountList = new ArrayList<>();
        packGiftSelectCountList.add(new GiftSelectCountBean("X 1", 1));
        packGiftSelectCountList.add(new GiftSelectCountBean("X 10", 10));
        packGiftSelectCountList.add(new GiftSelectCountBean("X 50", 50));
        packGiftSelectCountList.add(new GiftSelectCountBean("X 99", 99));
        packGiftSelectCountList.add(new GiftSelectCountBean("X 999", 999));

        requestGiftData();
    }

    private void requestPackGiftData() {
        //获取礼物列表
        PhoneLiveApi.getPackGiftList(mUser.id, mUser.token, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String s, int id) {

                JSONArray res = ApiUtils.checkIsSuccess(s);
                if (res != null && isAdded()) {
                    status = 1;
                    mGiftList.clear();
                    mGiftResStr = res;
                    //mUser.coin  = data.getString("coin");
                    fillGift();

                    if (mGiftList.size() == 0) {
                        status = 0;
                        mVpGiftView.setVisibility(View.GONE);
                    }
                } else {
                    status = 0;
                    mVpGiftView.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "背包为空!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void requestGiftData() {
        //获取礼物列表
        PhoneLiveApi.getGiftList(mUser.id, mUser.token, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String s, int id) {
                JSONArray res = ApiUtils.checkIsSuccess(s);
                if (res != null && isAdded()) {
                    mVpGiftView.setVisibility(View.VISIBLE);
                    try {
                        JSONObject data = res.getJSONObject(0);
                        mGiftList.clear();
                        mGiftResStr = data.getJSONArray("giftlist");
                        mUser.coin = data.getString("coin");
                        mUserCoin.setText(mUser.coin);//重置余额
                        fillGift();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * @param isOutTime 是否连送超时(如果是连送礼物的情况下)
     * @dw 赠送礼物, 请求服务端获取数据扣费
     */
    private void doSendGift(final String isOutTime) {

        mCount++;
        if (mSelectedGiftItem != null) {
            if (mSelectedGiftItem.getType() == 1) {
                mSendGiftLian.setVisibility(View.VISIBLE);
            } else {
                changeSendGiftBtnStatue(true);
            }

            if (status == 1) {

                PhoneLiveApi.sendPackGift(mUser, mSelectedGiftItem, ((RtmpPlayerActivity) getActivity()).mEmceeInfo.uid,
                        ((RtmpPlayerActivity) getActivity()).mEmceeInfo.stream, sendPackGiftCount, new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {

                            }

                            @Override
                            public void onResponse(String response, int id) {
                                JSONArray s = ApiUtils.checkIsSuccess(response);
                                if (s != null) {
                                    //更新背包中礼物剩余数目
                                    for (int i = 0; i < mGiftViews.size(); i++) {
                                        for (int j = 0; j < mGiftViews.get(i).getChildCount(); j++) {
                                            BaseQuickAdapter adapter = (BaseQuickAdapter) mGiftViews.get(i).getAdapter();

                                            if (((GiftBean) adapter.getItem(j)).getGiftname().equals(mSelectedGiftItem.getGiftname())) {
                                                try {
                                                    ((TextView) mGiftViews.get(i).getChildAt(j).findViewById(R.id.count_num)).setText("" + s.getJSONObject(0).getInt("giftcount"));
                                                } catch (Exception e) {

                                                }
                                            }

                                        }
                                    }

                                    try {
                                        ((TextView) mSendGiftLian.findViewById(R.id.tv_show_gift_outtime)).setText(String.valueOf(mShowGiftSendOutTime));
                                        JSONObject tokenJson = s.getJSONObject(0);
                                        //获取剩余金额,重新赋值
                                        mUser.coin = tokenJson.getString("coin");
                                        mUserCoin.setText(mUser.coin);//重置余额
                                        mUser.level = tokenJson.getString("level");
                                        AppContext.getInstance().saveUserInfo(mUser);

                                        Event.SendGiftEvent event = new Event.SendGiftEvent();
                                        event.isOutTime = "n";
                                        event.count = sendPackGiftCount;
                                        event.giftToken = tokenJson.getString("gifttoken");
                                        event.sendGiftBean = mSelectedGiftItem;
                                        EventBus.getDefault().post(event);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }



                                try {
                                    JSONObject jsonObject=new JSONObject(response);
                                    if (null!=jsonObject)
                                    {
                                        JSONObject jsonObjec=jsonObject.optJSONObject("data");
                                        if (null!=jsonObjec)
                                        {
                                            JSONArray jsonArray=jsonObjec.optJSONArray("info");
                                            if (null!=jsonArray)
                                            {
                                                for (int i=0;i<jsonArray.length();i++)
                                                {
                                                    JSONObject jsonObje=jsonArray.optJSONObject(i);
                                                    String tupian= jsonObje.optString("giftname");
                                                    TuPianRootBean tupianRootBean=new TuPianRootBean();
                                                    tupianRootBean.setTupian_mingzi(tupian);
                                                    list1.add(tupianRootBean);
                                                    if (list1 != null && list1.size() > 0) {
                                                        for (int i1 = 0; i1 < list1.size(); i1++) {
                                                            ShowLiveActivityBase activity = ((ShowLiveActivityBase) getActivity());
                                                            activity.DongHuaXiaoGuo(list1.get(i1).getTupian_mingzi());
                                                        }
                                                    }
                                                }}}}
                                                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
            } else {
                PhoneLiveApi.sendGift(mUser, mSelectedGiftItem, ((RtmpPlayerActivity) getActivity()).mEmceeInfo.uid,
                        ((RtmpPlayerActivity) getActivity()).mEmceeInfo.stream, new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {

                            }

                            @Override
                            public void onResponse(String response, int id) {
                                JSONArray s = ApiUtils.checkIsSuccess(response);
                                if (s != null) {
                                    try {

                                        JSONObject tokenJson = s.getJSONObject(0);
                                        //获取剩余金额,重新赋值
                                        mUser.coin = tokenJson.getString("coin");
                                        mUserCoin.setText(mUser.coin);//重置余额
                                        mUser.level = tokenJson.getString("level");
                                        AppContext.getInstance().saveUserInfo(mUser);

                                        Event.SendGiftEvent event = new Event.SendGiftEvent();
                                        event.isOutTime = isOutTime;
                                        event.count = mCount;
                                        event.giftToken = tokenJson.getString("gifttoken");
                                        event.sendGiftBean = mSelectedGiftItem;
                                        EventBus.getDefault().post(event);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                try {
                                    JSONObject jsonObject=new JSONObject(response);
                                    if (null!=jsonObject)
                                    {
                                        JSONObject jsonObjec=jsonObject.optJSONObject("data");
                                        if (null!=jsonObjec)
                                        {
                                            JSONArray jsonArray=jsonObjec.optJSONArray("info");
                                            if (null!=jsonArray)
                                            {
                                                for (int i=0;i<jsonArray.length();i++)
                                                {
                                                    JSONObject jsonObje=jsonArray.optJSONObject(i);
                                                    String tupian= jsonObje.optString("giftname");
                                                    TuPianRootBean tupianRootBean=new TuPianRootBean();
                                                    tupianRootBean.setTupian_mingzi(tupian);
                                                    list1.add(tupianRootBean);
                                                    if (list1 != null && list1.size() > 0) {
                                                        for (int i1 = 0; i1 < list1.size(); i1++) {
                                                            ShowLiveActivityBase activity = ((ShowLiveActivityBase) getActivity());
                                                            activity.DongHuaXiaoGuo(list1.get(i1).getTupian_mingzi());
                                                        }
                                                    }
                                                }}}}
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
            }
        }
    }

    /**
     * @param statue 开启or关闭
     * @dw 赠送礼物按钮状态修改
     */
    private void changeSendGiftBtnStatue(boolean statue) {
        if (statue) {
            mBtnSendGiftBtn.setBackgroundResource(R.drawable.bg_send_gift);
            mBtnSendGiftBtn.setEnabled(true);
        } else {
            mBtnSendGiftBtn.setBackgroundResource(R.drawable.bg_send_gift2);
            mBtnSendGiftBtn.setEnabled(false);
        }
    }

    //连送按钮隐藏
    private void recoverySendGiftBtnLayout() {
        ((TextView) mSendGiftLian.findViewById(R.id.tv_show_gift_outtime)).setText("");
        mSendGiftLian.setVisibility(View.GONE);
        mBtnSendGiftBtn.setVisibility(View.VISIBLE);
        mShowGiftSendOutTime = 5;
    }

    //赠送礼物单项被选中
    private void giftItemClick(BaseQuickAdapter parent, View view, int position) {
        mpos = position;
        mCount = 0;

        if (parent.getItem(position) != mSelectedGiftItem) {
            recoverySendGiftBtnLayout();
            mSelectedGiftItem = (GiftBean) parent.getItem(position);
            //点击其他礼物
            changeSendGiftBtnStatue(true);
            for (int i = 0; i < mGiftViews.size(); i++) {
                for (int j = 0; j < mGiftViews.get(i).getChildCount(); j++) {
                    BaseQuickAdapter adapter = (BaseQuickAdapter) mGiftViews.get(i).getAdapter();
                    if (((GiftBean) adapter.getItem(j)).getType() == 1) {
                        mGiftViews.get(i).getChildAt(j).findViewById(R.id.iv_show_gift_selected).setBackgroundResource(R.drawable.icon_continue_gift);
                    } else {
                        mGiftViews.get(i).getChildAt(j).findViewById(R.id.iv_show_gift_selected).setBackgroundResource(0);
                    }

                }
            }
            view.findViewById(R.id.iv_show_gift_selected).setBackgroundResource(R.drawable.icon_continue_gift_chosen);

        } else {
            if (((GiftBean) parent.getItem(position)).getType() == 1) {
                view.findViewById(R.id.iv_show_gift_selected).setBackgroundResource(R.drawable.icon_continue_gift);
            } else {
                view.findViewById(R.id.iv_show_gift_selected).setBackgroundResource(0);
            }
            mSelectedGiftItem = null;
            changeSendGiftBtnStatue(false);

        }
    }

    /**
     * @param v btn
     * @dw 点击赠送礼物按钮
     */
    private void onClickSendGift(View v) {
        //没有连接ok
        if (!((RtmpPlayerActivity) getActivity()).mConnectionState) {
            return;
        }
        //连送礼物
        if ((mSelectedGiftItem != null) && (mSelectedGiftItem.getType() == 1)) {
            v.setVisibility(View.GONE);
            if (mHandler == null) return;
            //开启连送定时器
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mShowGiftSendOutTime == 1) {
                        mCount = 0;
                        recoverySendGiftBtnLayout();
                        mHandler.removeCallbacks(this);
                        return;
                    }
                    mHandler.postDelayed(this, 1000);
                    mShowGiftSendOutTime--;
                    ((TextView) mSendGiftLian.findViewById(R.id.tv_show_gift_outtime)).setText(String.valueOf(mShowGiftSendOutTime));

                }
            }, 1000);

            doSendGift("y");//礼物发送
        } else {
            doSendGift("n");//礼物发送
        }
    }

    //礼物列表填充
    private void fillGift() {

        if (null != mGiftResStr) {

            mGiftViewList.clear();

            if (mGiftList.size() == 0) {
                try {
                    JSONArray giftListJson = mGiftResStr;
                    for (int i = 0; i < giftListJson.length(); i++) {
                        mGiftList.add(JSON.parseObject(giftListJson.getString(i), GiftBean.class));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            int index = 0;

            int giftsPageSize;
            int giftSPageNum = 8;

            if (mGiftList.size() % giftSPageNum == 0) {

                giftsPageSize = mGiftList.size() / giftSPageNum;
            } else {
                giftsPageSize = (mGiftList.size() / giftSPageNum) + 1;
            }

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                giftsPageSize = 1;

                giftSPageNum = mGiftList.size();
            }

            for (int i = 0; i < giftsPageSize; i++) {
                View v = LayoutInflater.from(getActivity()).inflate(R.layout.view_show_gifts_list, null);
                mGiftViewList.add(v);
                List<GiftBean> l = new ArrayList<>();

                for (int j = 0; j < giftSPageNum; j++) {
                    if (index >= mGiftList.size()) {
                        break;
                    }
                    l.add(mGiftList.get(index));
                    index++;
                }
                mGiftViews.add((RecyclerView) v.findViewById(R.id.gv_gift_list));
                GiftListAdapter giftListAdapter = new GiftListAdapter(l);
                mGiftViews.get(i).setAdapter(giftListAdapter);
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                    layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                    mGiftViews.get(i).setLayoutManager(layoutManager);
                } else {
                    mGiftViews.get(i).setLayoutManager(new GridLayoutManager(getContext(), 4));
                }

                giftListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                        giftItemClick(adapter, view, position);
                    }
                });

            }

        }
        mVpGiftAdapter.notifyDataSetChanged();
    }
}
