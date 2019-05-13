package com.daimeng.live.ui;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.daimeng.live.AppContext;
import com.daimeng.live.R;
import com.daimeng.live.adapter.PayRuleAdapter;
import com.daimeng.live.adapter.UserRechargeRuleAdapter;
import com.daimeng.live.alipay.AliPay;
import com.daimeng.live.api.remote.ApiUtils;
import com.daimeng.live.api.remote.PhoneLiveApi;
import com.daimeng.live.base.BaseActivity;
import com.daimeng.live.bean.PayMenuBean;
import com.daimeng.live.bean.RechargeBean;
import com.daimeng.live.bean.callback.RequestGetRechargeInfo;
import com.daimeng.live.bean.callback.RequestPay;
import com.daimeng.live.ui.customviews.ActivityTitle;
import com.daimeng.live.utils.LiveUtils;
import com.daimeng.live.utils.StringUtils;
import com.daimeng.live.wxpay.WChatPay;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;

public class UserRechargeActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener {

    @BindView(R.id.lv_select_num_list)
    RecyclerView mRecycleView;

    @BindView(R.id.view_title)
    ActivityTitle mActivityTitle;

    private RecyclerView mRvPayList;

    private List<RechargeBean> mRechargeList = new ArrayList<>();
    private List<PayMenuBean> mPayRuleList = new ArrayList<>();
    private UserRechargeRuleAdapter mRechargeAdapter;
    private PayRuleAdapter mPayRuleAdapter;
    private WChatPay mWChatPay;
    private AliPay mAliPay;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_recharge;
    }

    @Override
    public void initView() {
        setActionBarTitle("充值");

        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        //mRecycleView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL_LIST));
        mRechargeAdapter = new UserRechargeRuleAdapter(mRechargeList);
        View headView = inflateView(R.layout.view_user_recharge_head);
        mRvPayList = (RecyclerView) headView.findViewById(R.id.rv_pay_list);
        mRvPayList.setLayoutManager(new LinearLayoutManager(this));
        mPayRuleAdapter = new PayRuleAdapter(mPayRuleList);
        mRvPayList.setAdapter(mPayRuleAdapter);

        mRechargeAdapter.addHeaderView(headView);

        mRecycleView.setAdapter(mRechargeAdapter);
        mRechargeAdapter.setOnItemClickListener(this);

        mActivityTitle.setReturnListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mPayRuleAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mPayRuleAdapter.setPayPos(position);
            }
        });
    }

    @Override
    public void initData() {

        mAliPay = new AliPay(this);
        mWChatPay = new WChatPay(this);

        requestData();
    }

    @Override
    public void onClick(View view) {

    }

    //获取数据
    private void requestData() {

        PhoneLiveApi.requestGetRechargeInfo(new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {

                String res = ApiUtils.checkIsSuccess2(response);
                if (res != null) {
                    RequestGetRechargeInfo data = JSON.parseObject(res, RequestGetRechargeInfo.class);
                    mRechargeList.clear();
                    mRechargeList.addAll(data.getRecharge_rule());
                    mRechargeAdapter.notifyDataSetChanged();

                    mPayRuleList.clear();
                    mPayRuleList.addAll(data.getPay_rule());
                    mPayRuleAdapter.notifyDataSetChanged();
                }
            }
        });

    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        pay(position);
    }


    private void pay(int pos) {

        RechargeBean rechargeBean = mRechargeList.get(pos);

        if (mPayRuleAdapter.getPayPos() == -1) {
            AppContext.showToast("请选择支付类型！");
            return;
        }
        //SDK支付
        actionPay(rechargeBean.id);

    }

    //支付
    private void actionPay(String changeId) {

        if (mAliPay == null && mWChatPay == null) {
            return;
        }

        showWaitDialog("正在提交...", false);
        PhoneLiveApi.requestPay(getUserID(), getUserToken(), mPayRuleAdapter.getRule().getId(), changeId, new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {

                hideWaitDialog();
            }

            @Override
            public void onResponse(String response, int id) {

                hideWaitDialog();
                String res = ApiUtils.checkIsSuccess2(response);
                if (res != null) {
                    RequestPay requestPay = JSON.parseObject(res, RequestPay.class);
                    if (StringUtils.toInt(requestPay.getIs_wap()) == 1) {

                        LiveUtils.startWeb(UserRechargeActivity.this, requestPay.getPay_info());
                    } else {

                        if (StringUtils.toInt(requestPay.getType()) == 0) {
                            mAliPay.startPay(requestPay.getPay_info());
                        } else {
                            mWChatPay.startPay(JSON.parseObject(requestPay.getPay_info()));
                        }
                    }
                }

            }
        });
    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }
}
