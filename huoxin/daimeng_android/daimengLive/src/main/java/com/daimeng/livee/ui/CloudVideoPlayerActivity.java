package com.daimeng.livee.ui;

import android.content.pm.ActivityInfo;
import android.view.View;

import com.bumptech.glide.Glide;
import com.daimeng.livee.AppContext;
import com.daimeng.livee.R;
import com.daimeng.livee.api.remote.ApiUtils;
import com.daimeng.livee.api.remote.PhoneLiveApi;
import com.daimeng.livee.base.BaseActivity;
import com.daimeng.livee.utils.BGTimedTaskManage;
import com.daimeng.livee.utils.LiveUtils;
import com.daimeng.livee.utils.StringUtils;
import com.daimeng.livee.utils.TLog;
import com.zhy.http.okhttp.callback.StringCallback;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;
import okhttp3.Call;

public class CloudVideoPlayerActivity extends BaseActivity implements BGTimedTaskManage.BGTimeTaskRunnable {

    private BGTimedTaskManage bgTimedTaskManage;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_cloud_video_player;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void initView() {
        //全屏
        payType = getIntent().getStringExtra(VIDEO_PAY_TYPE);
        money = getIntent().getStringExtra(VIDEO_MONEY);
        videoID = getIntent().getStringExtra(VIDEO_ID);
        init();
    }

    @Override
    public void initData() {

    }

    public static final String VIDEO_ID = "VIDEO_ID";
    public static final String VIDEO_URL = "VIDEO_URL";
    public static final String VIDEO_COVER_URL = "VIDEO_COVER_URL";
    public static final String VIDEO_PAY_TYPE = "VIDEO_PAY_TYPE";
    public static final String VIDEO_MONEY = "VIDEO_MONEY";

    private String videoUrl;
    private String coverUrl;
    //扣费金额
    private String money;
    //付费类型
    private String payType;
    private String videoID;


    protected void init() {


        videoUrl = getIntent().getStringExtra(VIDEO_URL);
        if(videoUrl == null){
            AppContext.showToast("链接地址错误");
            return;
        }
        coverUrl = getIntent().getStringExtra(VIDEO_COVER_URL);

        JZVideoPlayerStandard jzVideoPlayerStandard = (JZVideoPlayerStandard) findViewById(R.id.videoplayer);
        jzVideoPlayerStandard.setUp(LiveUtils.getHttpUrl(videoUrl)
                , JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL);

        Glide.with(this).load(coverUrl)
                .into(jzVideoPlayerStandard.thumbImageView);

        JZVideoPlayer.FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        JZVideoPlayer.NORMAL_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

        if(StringUtils.toInt(payType) == 2){
            bgTimedTaskManage = new BGTimedTaskManage();
            bgTimedTaskManage.setTime(60 * 1000);
            bgTimedTaskManage.startRunnable(this,false);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();

        //Change these two variables back
        JZVideoPlayer.FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_SENSOR;
        JZVideoPlayer.NORMAL_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    @Override
    public void onRunTask() {

        requestChargePay();
    }

    /*
    * 按时扣费
    * */
    private void requestChargePay() {
        PhoneLiveApi.requestCloudVideoChargePay(videoID,AppContext.getInstance().getLoginUid(),AppContext.getInstance().getToken(),new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {

                String res = ApiUtils.checkIsSuccess2(response);
                if(res == null){
                    AppContext.showToast("扣费失败!");
                    finish();
                }else{
                    TLog.log("短视频定时扣费成功!");
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(bgTimedTaskManage != null){
            bgTimedTaskManage.stopRunnable();
        }
    }
}
