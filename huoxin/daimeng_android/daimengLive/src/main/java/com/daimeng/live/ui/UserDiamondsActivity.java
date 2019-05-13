package com.daimeng.live.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.daimeng.live.AppContext;
import com.daimeng.live.adapter.DiamondsAdapter;
import com.daimeng.live.utils.DialogHelp;
import com.daimeng.live.widget.DividerItemDecoration;
import com.daimeng.live.R;
import com.daimeng.live.bean.RechargeJson;
import com.daimeng.live.utils.StringUtils;
import com.daimeng.live.wxpay.WChatPay;
import com.daimeng.live.alipay.AliPay;
import com.daimeng.live.api.remote.ApiUtils;
import com.daimeng.live.api.remote.PhoneLiveApi;
import com.daimeng.live.base.BaseActivity;
import com.daimeng.live.bean.RechargeBean;
import com.daimeng.live.widget.BlackTextView;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;


/**
 * 我的钻石
 */
public class UserDiamondsActivity extends BaseActivity {

    @BindView(R.id.lv_select_num_list)
    RecyclerView mRecycleView;

    private List<RechargeBean> mRechargeList = new ArrayList<>();

    private final int ALI_PAY    = 1;
    private final int WX_PAY  = 2;

    private int PAY_MODE = WX_PAY;

    private BlackTextView mTvCoin;

    private WChatPay mWChatPay;
    private AliPay mAliPayUtils;

    private DiamondsAdapter mRechargeAdapter;
    private RechargeJson mRechargeJson;
    private int selectPosition = 1;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_diamonds;
    }

    @Override
    public void initView() {

        View mHeadView = getLayoutInflater().inflate(R.layout.view_diamonds_head,null);
        mTvCoin  = (BlackTextView) mHeadView.findViewById(R.id.tv_coin);

        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mRecycleView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL_LIST));
        mRechargeAdapter = new DiamondsAdapter(mRechargeList);

        //加头部
        mRechargeAdapter.addHeaderView(mHeadView);

        mRechargeAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                selectPosition = position;
                pay();
            }
        });

        mRecycleView.setAdapter(mRechargeAdapter);

        //点击返回
        mHeadView.findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void pay() {

        final RechargeBean rechargeBean = mRechargeList.get(selectPosition);
        final int pay_type = getResources().getInteger(R.integer.pay_type);

        DialogHelp.getSelectDialog(UserDiamondsActivity.this, new String[]{"点击支付宝充值", "点击微信充值"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(i == 0){
                    PAY_MODE = ALI_PAY;
                }else if(i == 1){
                    PAY_MODE = WX_PAY;
                }

                //SDK支付
                if(pay_type == 0){

                    actionPay(String.valueOf(rechargeBean.money),rechargeBean.coin,rechargeBean.id);
                }else if(pay_type == 1){
                    //WAP支付

                    actionPayWap(String.valueOf(rechargeBean.money),rechargeBean.coin,rechargeBean.id);
                }


            }
        }).show();

    }


    @Override
    public void initData() {

        requestData();

    }


    //支付
    private void actionPayWap(String money, String num,String changeid) {

        if (checkPayMode()) {

            showWaitDialog("正在提交...",false);
            PhoneLiveApi.requestPayOrderId(getUserID(),getUserToken(),money,num,changeid, String.valueOf(PAY_MODE),new StringCallback(){

                @Override
                public void onError(Call call, Exception e, int id) {

                    hideWaitDialog();
                }

                @Override
                public void onResponse(String response, int id) {

                    hideWaitDialog();

                    try {
                        JSONObject res = new JSONObject(response);
                        JSONObject data = res.getJSONObject("info");
                        if(res.getInt("code") != 0){

                            AppContext.showToast(res.getString("msg"));
                            return;
                        }

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(data.getString("code_url")));
                        startActivity(intent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    }


    //支付
    private void actionPay(String money, String num,String changeid) {

        if(mAliPayUtils == null && mWChatPay == null){
            return;
        }

    }

    //检查支付配置
    private boolean checkPayMode(){

        if(PAY_MODE == ALI_PAY){
            if(mRechargeJson.aliapp_switch.equals("1")){
                return true;
            }else{

                AppContext.showToast("支付宝未开启",0);
                return false;
            }
        }else if(PAY_MODE == WX_PAY){
            if(mRechargeJson.wx_switch.equals("1")){
                return true;
            }else{

                AppContext.showToast("微信未开启",0);
                return false;
            }
        }

        return false;

    }


    //获取数据
    private void requestData() {

        PhoneLiveApi.requestBalance(getUserID(),getUserToken(),new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                JSONArray array = ApiUtils.checkIsSuccess(response);

                if(array != null){

                    try {
                        fillUI(array.getString(0));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

    }

    private void fillUI(String data) {

        mAliPayUtils = new AliPay(this);
        mWChatPay    = new WChatPay(this);

        mRechargeJson = JSON.parseObject(data,RechargeJson.class);
        mRechargeList.clear();
        mRechargeList.addAll(mRechargeJson.rules);
        mRechargeAdapter.notifyDataSetChanged();
        mTvCoin.setText(mRechargeJson.coin);
    }

    @Override
    public void onClick(View v) {


    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }

    //充值结果
    public void rechargeResult(boolean isOk, String rechargeMoney) {
        if(isOk){
            mTvCoin.setText(String.valueOf(StringUtils.toInt(mTvCoin.getText().toString()) +
                    StringUtils.toInt(rechargeMoney)));
        }
    }

}
