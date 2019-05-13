package com.daimeng.live.ui;

import android.view.View;

import com.daimeng.live.R;
import com.daimeng.live.base.BaseActivity;
import com.daimeng.live.widget.BlackTextView;

import butterknife.BindView;

//支付宝回调页面
public class AlipayResultActivity extends BaseActivity {
    @BindView(R.id.tv_alipaypay_result)
    BlackTextView mAliPayResult;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_alipay_result;
    }

    @Override
    public void initView() {

    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    @Override
    public void initData() {
        setActionBarTitle(getString(R.string.payresult));
        if(getIntent().getIntExtra("result",0) == 1){
            mAliPayResult.setText("ok");
        }else{
            mAliPayResult.setText("no");
        }
    }

    @Override
    public void onClick(View v) {

    }
}