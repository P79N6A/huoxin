package com.daimeng.live.ui;

import android.view.View;
import android.widget.TextView;

import com.daimeng.live.AppContext;
import com.daimeng.live.api.remote.ApiUtils;
import com.daimeng.live.api.remote.PhoneLiveApi;
import com.daimeng.live.base.BaseActivity;
import com.daimeng.live.bean.ConfigBean;
import com.daimeng.live.bean.ProfitBean;
import com.daimeng.live.ui.customviews.ActivityTitle;
import com.daimeng.live.utils.DialogHelp;
import com.daimeng.live.utils.LiveUtils;
import com.google.gson.Gson;
import com.daimeng.live.R;
import com.daimeng.live.widget.BlackTextView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 收益
 */
public class UserProfitActivity extends BaseActivity {

    @BindView(R.id.tv_votes)
    BlackTextView mVotes;
    @BindView(R.id.tv_profit_canwithdraw)
    BlackTextView mCanwithDraw;
    @BindView(R.id.tv_profit_withdraw)
    BlackTextView mWithDraw;

    @BindView(R.id.view_title)
    ActivityTitle mActivityTitle;
    private ProfitBean mProfitBean;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_profit;
    }

    @Override
    public void initView() {
        mActivityTitle.setReturnListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        ConfigBean pConfigBean = LiveUtils.getConfigBean(this);
        ((TextView)findViewById(R.id.tv_profit_tick_name)).setText(pConfigBean.name_votes);
    }

    @Override
    public void initData() {

    }

    private void  requestData() {

        PhoneLiveApi.getWithdraw(getUserID(),getUserToken(), new StringCallback() {
            @Override
            public void onError(Call call, Exception e,int id) {

            }

            @Override
            public void onResponse(String response,int id) {
                JSONArray res = ApiUtils.checkIsSuccess(response);

                if(null != res){
                    try {
                        mProfitBean = new Gson().fromJson(res.getString(0),ProfitBean.class);
                        fillUI();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    private void fillUI() {
        mCanwithDraw.setText(mProfitBean.total);
        mWithDraw.setText(mProfitBean.todaycash);
        mVotes.setText(mProfitBean.votes);
    }



    @OnClick({R.id.btn_profit_cash})
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_profit_cash:

                //UIHelper.showRequestCashActivity(UserProfitActivity.this);
                showWaitDialog2("正在提交信息",false);
                PhoneLiveApi.requestCash(getUserID(),getUserToken(),"",
                        new StringCallback(){

                            @Override
                            public void onError(Call call, Exception e,int id) {
                                hideWaitDialog();
                                AppContext.showToast("接口请求失败");
                            }

                            @Override
                            public void onResponse(String response,int id) {
                                hideWaitDialog();
                                JSONArray res = ApiUtils.checkIsSuccess(response);
                                if(null != res){

                                    try {
                                        DialogHelp.getMessageDialog(UserProfitActivity.this,res.getJSONObject(0).getString("msg"))
                                                .create()
                                                .show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                break;

                default:
                    break;
        }


    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }

    @Override
    public void onResume() {

        super.onResume();
        requestData();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag("getWithdraw");
    }
}
