package com.daimeng.livee.ui;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.daimeng.livee.AppConfig;
import com.daimeng.livee.R;
import com.daimeng.livee.base.BaseActivity;
import com.daimeng.livee.ui.customviews.ActivityTitle;

import java.util.Locale;

import butterknife.BindView;

public class OrderWebViewActivity extends BaseActivity {

    @BindView(R.id.order_wv)
    WebView mWebView;
    @BindView(R.id.ll_day)
    LinearLayout mLlDayView;
    @BindView(R.id.ll_week)
    LinearLayout mLlWeekView;
    @BindView(R.id.view_day)
    View mViewDay;
    @BindView(R.id.ll_all)
    LinearLayout mLlAllView;
    @BindView(R.id.view_all)
    View mViewAll;

    @BindView(R.id.view_week)
    View mViewWeek;

    @BindView(R.id.view_title)
    ActivityTitle mActivityTitle;

    private String mUid;

    private int num;

    private String mmUid;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_order_web_view;
    }

    @Override
    public void onClick(View view) {

    }
    public void initView() {
        mActivityTitle.setReturnListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mLlDayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                num=1;
                showData(false);
            }
        });
        mLlWeekView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                num=2;
                showData(false);
            }
        });
        mLlAllView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                num=3;
                showData(true);
            }
        });
    }

    @Override
    public void initData() {

        mUid = getIntent().getStringExtra("uid");
        showData(false);

    }

    private void showData(boolean b) {
        if (num==1){
            mViewWeek.setVisibility(View.GONE);
            mViewDay.setVisibility(b ? View.GONE : View.VISIBLE);
            mViewAll.setVisibility(!b ? View.GONE : View.VISIBLE);
            mmUid=getIntent().getStringExtra("muid");
            String url = String.format(Locale.CHINA,
                    AppConfig.MAIN_URL + "/index.php?g=appapi&m=Contribute&a=order&type=week&uid="+ mmUid +"&type=%s",b ? "all" : "3");
            mWebView.loadUrl(url);
            num=0;
        }
        if (num==2){
            mViewDay.setVisibility(View.GONE);
            mViewWeek.setVisibility(b ? View.GONE : View.VISIBLE);
            mViewAll.setVisibility(!b ? View.GONE : View.VISIBLE);
            mmUid=getIntent().getStringExtra("muid");
            String url = String.format(Locale.CHINA,
                    AppConfig.MAIN_URL + "/index.php?g=appapi&m=Contribute&a=order&type=week&uid="+ mmUid +"&type=%s",b ? "all" : "week");
            mWebView.loadUrl(url);
            num=0;
        }
        if (num==3){
            mViewDay.setVisibility(View.GONE);
            mViewWeek.setVisibility(b ? View.GONE : View.VISIBLE);
            mViewAll.setVisibility(!b ? View.GONE : View.VISIBLE);
            mmUid=getIntent().getStringExtra("muid");
            String url = String.format(Locale.CHINA,
                    AppConfig.MAIN_URL + "/index.php?g=appapi&m=Contribute&a=order&type=week&uid="+ mmUid +"&type=%s",b ? "all" : "week");
            mWebView.loadUrl(url);
            num=0;
        }
    }
    protected boolean hasActionBar() {
        return false;
    }

    public static void startOrderWebView(Context context,String uid){
        Intent intent = new Intent(context,DedicateOrderActivity.class);
        intent.putExtra("uid",uid);
        context.startActivity(intent);
    }
}
