package com.daimeng.livee.ui;

import android.content.DialogInterface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.daimeng.livee.AppContext;
import com.daimeng.livee.R;
import com.daimeng.livee.adapter.BuyVipAdapter;
import com.daimeng.livee.api.remote.ApiUtils;
import com.daimeng.livee.api.remote.PhoneLiveApi;
import com.daimeng.livee.base.BaseActivity;
import com.daimeng.livee.bean.VipBean;
import com.daimeng.livee.ui.customviews.ActivityTitle;
import com.daimeng.livee.utils.DialogHelp;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;

public class BuyVipActivity extends BaseActivity {

    @BindView(R.id.view_title)
    ActivityTitle mActivityTitle;

    @BindView(R.id.my_recycleView)
    RecyclerView mRecyclerView;

    @BindView(R.id.tv_my_vip)
    TextView mTvVip;

    private List<VipBean> mListCar = new ArrayList<>();
    private BuyVipAdapter mBuyVipAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_buy_vip;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void initView() {
        mActivityTitle.setReturnListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mBuyVipAdapter = new BuyVipAdapter(mListCar);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mBuyVipAdapter);

        mBuyVipAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, final int position) {
                DialogHelp.getConfirmDialog(BuyVipActivity.this, "确定购买？", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        showWaitDialog("正在加载中...", false);
                        PhoneLiveApi.requestBuyVip(getUserID(), getUserToken(), mListCar.get(position).id, new StringCallback() {

                            @Override
                            public void onError(Call call, Exception e, int id) {
                                hideWaitDialog();
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                hideWaitDialog();
                                String res = ApiUtils.checkIsSuccess2(response);
                                if (res != null) {

                                    AppContext.showToast("购买成功");
                                }
                            }
                        });
                    }
                }).show();
            }
        });

        View foot = inflateView(R.layout.view_vip_explain);
        mBuyVipAdapter.addFooterView(foot);
    }

    @Override
    public void initData() {

        requestGetGoodsList();
    }

    private void requestGetGoodsList() {

        PhoneLiveApi.requestGetGoodsList(getUserID(), getUserToken(), new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {

                String res = ApiUtils.checkIsSuccess2(response);
                if (res != null) {

                    JSONObject data = JSON.parseObject(res);
                    if (data.getIntValue("is_vip") == 1) {

                        mTvVip.setText(data.getString("vip_expire") + "   VIP等级:" + data.getString("vip_level"));
                    } else {
                        mTvVip.setText("未开通VIP");
                    }

                    mListCar.addAll(JSON.parseArray(data.getString("vip_data"), VipBean.class));
                    mBuyVipAdapter.notifyDataSetChanged();
                }
            }
        });

    }


}
