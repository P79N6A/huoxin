package com.daimeng.live.ui;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.daimeng.live.api.remote.PhoneLiveApi;
import com.daimeng.live.bean.LiveBean;
import com.daimeng.live.bean.LiveCheckInfoBean;
import com.daimeng.live.broadcast.PushReceiver;
import com.daimeng.live.fragment.InviteCodeDialogFragment;
import com.daimeng.live.interf.BaseViewInterface;
import com.daimeng.live.utils.DialogHelp;
import com.daimeng.live.utils.LiveUtils;
import com.daimeng.live.utils.StringUtils;
import com.daimeng.live.utils.TDevice;
import com.daimeng.live.utils.TLog;
import com.daimeng.live.utils.UIHelper;
import com.daimeng.live.utils.UpdateManager;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.daimeng.live.AppContext;
import com.daimeng.live.AppManager;
import com.daimeng.live.R;
import com.daimeng.live.api.remote.ApiUtils;
import com.daimeng.live.base.BaseActivity;
import com.daimeng.live.em.MainTab;
import com.daimeng.live.utils.LoginUtils;
import com.daimeng.live.widget.MyFragmentTabHost;
import com.tandong.bottomview.view.BottomView;
import com.tencent.liteav.demo.videorecord.TCVideoRecordActivity;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Set;

import butterknife.BindView;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import okhttp3.Call;


//主页面
public class MainActivity extends BaseActivity implements TabHost.OnTabChangeListener, BaseViewInterface, View.OnTouchListener {

    @BindView(android.R.id.tabhost)
    MyFragmentTabHost mTabHost;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    public boolean isStartingLive = true;

    @Override
    public void initView() {
        AppManager.getInstance().addActivity(this);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        if (Build.VERSION.SDK_INT > 10) {
            mTabHost.getTabWidget().setShowDividers(0);
        }

        initTabs();
        mTabHost.setCurrentTab(100);
        mTabHost.setOnTabChangedListener(this);
        mTabHost.setNoTabChangedTag("1");


    }

    private void initTabs() {

        final MainTab[] tabs = MainTab.values();
        final int size = tabs.length;
        String[] title = new String[]{"首页", "", "我的"};

        for (int i = 0; i < size; i++) {
            MainTab mainTab = tabs[i];

            TabHost.TabSpec tab = mTabHost.newTabSpec(String.valueOf(mainTab.getResName()));
            View indicator = LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.tab_indicator, null);
            ImageView tabImg = (ImageView) indicator.findViewById(R.id.tab_img);
            TextView tabTv = (TextView) indicator.findViewById(R.id.tv_wenzi);
            Drawable drawable = this.getResources().getDrawable(
                    mainTab.getResIcon());
            tabTv.setText(title[i]);

            if (i == 1) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) TDevice.dpToPixel(30), ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 2, 0);
                tabImg.setLayoutParams(params);
                tabImg.setVisibility(View.GONE);
            }
            tabImg.setImageDrawable(drawable);
            tab.setIndicator(indicator);
            tab.setContent(new TabHost.TabContentFactory() {

                @Override
                public View createTabContent(String tag) {
                    return new View(MainActivity.this);
                }
            });

            mTabHost.addTab(tab, mainTab.getClz(), null);

            mTabHost.getTabWidget().getChildAt(i).setOnTouchListener(this);

        }

        mTabHost.getTabWidget().getChildAt(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLive();
            }
        });

        mTabHost.setCurrentTab(0);
    }

    @Override
    public void initData() {
        //检查token是否过期
        checkTokenIsOutTime();
        //注册极光推送
        registerJpush();
        //登录环信
        loginIM();
        //检查是否有最新版本
        checkNewVersion();
        //激光推送
        checkPushLiveNotify();
        initAMap();

        InviteCodeDialogFragment.checkInviteCode(getFragmentManager(), this);
    }


    //选择直播模式弹窗
    private void showSelectLiveTypeDialog() {

        //是否允许客户端上传视频
        if (StringUtils.toInt(LiveUtils.getConfigBean(MainActivity.this).is_open_user_upload_video) != 1) {
            UIHelper.showRtmpPushActivity(MainActivity.this, 0);
            return;
        }

        final BottomView bottomView = new BottomView(this, R.style.BottomViewTheme_Transparent, R.layout.dialog_select_live_type);
        bottomView.setAnimation(R.style.BottomToTopAnim);

        bottomView.getView().findViewById(R.id.tv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomView.dismissBottomView();
            }
        });

        //直播
        bottomView.getView().findViewById(R.id.iv_live).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIHelper.showRtmpPushActivity(MainActivity.this, 0);
                bottomView.dismissBottomView();
            }
        });

        //录制短视频
        bottomView.getView().findViewById(R.id.iv_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (StringUtils.toInt(LiveUtils.getConfigBean(MainActivity.this).is_need_auth_upload_video) == 0) {

                    Intent intent = new Intent(MainActivity.this, TCVideoRecordActivity.class);
                    startActivity(intent);
                } else {
                    PhoneLiveApi.requestCheckIsAuth(getUserID(), new StringCallback() {

                        @Override
                        public void onError(Call call, Exception e, int id) {

                        }

                        @Override
                        public void onResponse(String response, int id) {

                            String res = ApiUtils.checkIsSuccess2(response);
                            if (res != null) {
                                com.alibaba.fastjson.JSONObject data = JSON.parseObject(res);
                                if (data.getIntValue("is_auth") == 1) {
                                    Intent intent = new Intent(MainActivity.this, TCVideoRecordActivity.class);
                                    startActivity(intent);
                                } else {
                                    AppContext.showToast("未认证无法上传视频！");
                                }
                            }
                        }
                    });
                }

                bottomView.dismissBottomView();
            }
        });
        bottomView.showBottomView(true);


    }

    @Override
    protected void onStop() {
        super.onStop();
        isStartingLive = true;
    }

    //登录环信即时聊天
    private void loginIM() {
        String uid = String.valueOf(getUserID());

        EMClient.getInstance().login(uid,
                "daimenglive" + uid, new EMCallBack() {//回调
                    @Override
                    public void onSuccess() {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                EMClient.getInstance().groupManager().loadAllGroups();
                                EMClient.getInstance().chatManager().loadAllConversations();
                                TLog.log("环信[登录聊天服务器成功]");
                            }
                        });
                    }

                    @Override
                    public void onProgress(int progress, String status) {

                    }

                    @Override
                    public void onError(int code, String message) {
                        if (204 == code) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AppContext.showToast("聊天服务器登录和失败,请重新登录");
                                }
                            });

                        }
                        TLog.log("环信[主页登录聊天服务器失败" + "code:" + code + "MESSAGE:" + message + "]");
                    }
                });


    }

    //注册极光推送
    private void registerJpush() {
        JPushInterface.setAlias(this, AppContext.getInstance().getLoginUid() + "PUSH",
                new TagAliasCallback() {
                    @Override
                    public void gotResult(int i, String s, Set<String> set) {
                        TLog.log("极光推送注册[" + i + "I" + "S:-----" + s + "]");
                    }
                });

    }

    //检查token是否过期
    private void checkTokenIsOutTime() {
        LoginUtils.tokenIsOutTime(null);
    }

    //检查是否有最新版本
    private void checkNewVersion() {

        UpdateManager manager = new UpdateManager(this, false);
        manager.checkUpdate();

    }

    //开始直播初始化
    public void startLive() {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            //摄像头权限检测
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED
                    ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                //进行权限请求
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        5);

            } else {
                showSelectLiveTypeDialog();
            }
        } else {
            showSelectLiveTypeDialog();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 5: {
                // 判断权限请求是否通过
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults.length > 0 && grantResults[2] != PackageManager.PERMISSION_GRANTED && grantResults.length > 0 && grantResults[3] != PackageManager.PERMISSION_GRANTED) {

                    showSelectLiveTypeDialog();

                } else if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    AppContext.showToast("权限不足,请去设置中修改");
                } else if (grantResults.length > 0 && grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    AppContext.showToast("权限不足,请去设置中修改");
                } else if (grantResults.length > 0 && grantResults[2] != PackageManager.PERMISSION_GRANTED || grantResults.length > 0 && grantResults[3] != PackageManager.PERMISSION_GRANTED) {
                    AppContext.showToast("权限不足,请去设置中修改", 0);
                } else if (grantResults.length > 0 && grantResults[3] != PackageManager.PERMISSION_GRANTED) {
                    AppContext.showToast("定位权限未打开", 0);
                } else if (grantResults.length > 0 && grantResults[4] != PackageManager.PERMISSION_GRANTED) {
                    AppContext.showToast("权限不足,请去设置中修改", 0);
                }
            }
            default:
                break;
        }
    }


    private void checkPushLiveNotify() {

        final Bundle bundle = getIntent().getBundleExtra(PushReceiver.PUSH_LIVE_INFO_BOUND);
        if (bundle != null) {
            final LiveBean liveInfo = bundle.getParcelable(PushReceiver.PUSH_LIVE_INFO);

            DialogHelp.getConfirmDialog(this, "是否进入" + liveInfo.user_nicename + "的直播间？", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    PhoneLiveApi.requestCheckLive(AppContext.getInstance().getLoginUid(), AppContext.getInstance().getToken(), liveInfo.uid, liveInfo.stream, new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {

                        }

                        @Override
                        public void onResponse(String response, int id) {

                            LiveCheckInfoBean data = ApiUtils.checkIsSuccess(response, LiveCheckInfoBean.class);
                            if (data != null) {
                                UIHelper.showLookLiveActivity(MainActivity.this, liveInfo, data);
                            }
                        }
                    });
                }
            }).show();
        }
    }


    @Override
    public void onTabChanged(String tabId) {
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        return false;
    }


    @Override
    public void onClick(View view) {

    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }
}
