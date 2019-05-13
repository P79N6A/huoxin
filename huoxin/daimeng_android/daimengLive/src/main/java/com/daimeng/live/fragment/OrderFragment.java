package com.daimeng.live.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.daimeng.live.AppConfig;
import com.daimeng.live.R;
import com.daimeng.live.base.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrderFragment extends BaseFragment {


    @BindView(R.id.wv_order)
    WebView mWvOrder;
    private int[] mIds;
    private View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_order,null);

        ButterKnife.bind(this, mView);

        initView(mView);
        initData();
        return mView;

    }

    @Override
    public void initData() {

        mWvOrder.loadUrl(AppConfig.MAIN_URL + "/index.php?g=appapi&m=Contribute&a=thorder");
    }

    @Override
    public void initView(View view) {


        mIds = new int[]{R.id.tv_order_sr,R.id.tv_order_sr_day,R.id.tv_order_sr_month,R.id.tv_order_th,R.id.tv_order_th_day,R.id.tv_order_th_month};
    }

    @OnClick({R.id.tv_order_sr,R.id.tv_order_th,R.id.tv_order_sr_day,R.id.tv_order_sr_month,R.id.tv_order_th_month,R.id.tv_order_th_day})
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.tv_order_sr:
                changeTabColor(v.getId());
                mWvOrder.loadUrl(AppConfig.MAIN_URL + "/index.php?g=appapi&m=Contribute&a=srorder");
                break;
            case R.id.tv_order_sr_day:
                changeTabColor(v.getId());
                mWvOrder.loadUrl(AppConfig.MAIN_URL + "/index.php?g=appapi&m=Contribute&a=srorder_d");
                break;
            case R.id.tv_order_sr_month:
                changeTabColor(v.getId());
                mWvOrder.loadUrl(AppConfig.MAIN_URL + "/index.php?g=appapi&m=Contribute&a=srorder_m");
                break;
            case R.id.tv_order_th:
                changeTabColor(v.getId());
                mWvOrder.loadUrl(AppConfig.MAIN_URL + "/index.php?g=appapi&m=Contribute&a=thorder");
                break;
            case R.id.tv_order_th_day:
                changeTabColor(v.getId());
                mWvOrder.loadUrl(AppConfig.MAIN_URL + "/index.php?g=appapi&m=Contribute&a=thorder_d");
                break;
            case R.id.tv_order_th_month:
                changeTabColor(v.getId());
                mWvOrder.loadUrl(AppConfig.MAIN_URL + "/index.php?g=appapi&m=Contribute&a=thorder_m");
                break;

            default:
                break;
        }
    }

    private void changeTabColor(int id){

        for (int i =0; i< mIds.length; i++){
            TextView tv = (TextView) mView.findViewById(mIds[i]);
            if(id != mIds[i]){
                tv.setTextColor(getResources().getColor(R.color.colorGray4));
            }else{
                tv.setTextColor(getResources().getColor(R.color.global));
            }
        }
    }
}
