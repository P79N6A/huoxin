package com.daimeng.live.base;


import android.opengl.GLSurfaceView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.daimeng.live.AppContext;
import com.daimeng.live.Constant;
import com.daimeng.live.R;
import com.daimeng.live.api.remote.PhoneLiveApi;
import com.daimeng.live.other.LiveStream;
import com.daimeng.live.tisdk.TiFilter;
import com.daimeng.live.utils.LiveUtils;
import com.daimeng.live.utils.StringUtils;
import com.daimeng.live.utils.TLog;
import com.ksyun.media.streamer.encoder.VideoEncodeFormat;
import com.ksyun.media.streamer.kit.KSYStreamer;
import com.ksyun.media.streamer.kit.StreamerConstants;

import butterknife.BindView;
import cn.tillusory.sdk.TiSDKManager;
import cn.tillusory.sdk.TiSDKManagerBuilder;
import cn.tillusory.tiui.TiPanelLayout;

public class LivePushBaseActivity extends ShowLiveActivityBase {

    //渲染视频
    @BindView(R.id.camera_preview)
    GLSurfaceView mCameraPreview;

    public LiveStream mStreamer;

    protected String rtmpPushAddress;

    protected int pauseTime = 0;
    //闪光灯开启状态
    protected boolean flashingLightOn;

    //是否开启直播
    protected boolean IS_START_LIVE = true;

    //todo ----- tillusory start -----
    protected TiSDKManager tiSDKManager;
    protected TiPanelLayout tiPanelLayout;
    //todo ----- tillusory end -----


    protected void initPushLiveParam(){

        //直播参数配置
        mStreamer = new LiveStream(this);
        mStreamer.setUrl(rtmpPushAddress);
        mStreamer.setPreviewFps(15);

        int mTargetResolution = StreamerConstants.VIDEO_RESOLUTION_360P;
        switch (StringUtils.toInt(LiveUtils.getConfigBean(this).video_definition)){
            case Constant.VIDEO_RESOLUTION_480P:
                mTargetResolution = StreamerConstants.VIDEO_RESOLUTION_480P;
                break;
            case Constant.VIDEO_RESOLUTION_540P:
                mTargetResolution = StreamerConstants.VIDEO_RESOLUTION_540P;
                break;
            case Constant.VIDEO_RESOLUTION_720P:
                mTargetResolution = StreamerConstants.VIDEO_RESOLUTION_720P;
                break;

            default:
                break;
        }

        mStreamer.setPreviewResolution(mTargetResolution);
        mStreamer.setTargetResolution(mTargetResolution);
        //mStreamer.setVideoBitrate(500);
        mStreamer.setEncodeMethod(StreamerConstants.ENCODE_METHOD_SOFTWARE);
        mStreamer.setVideoEncodeScene(VideoEncodeFormat.ENCODE_SCENE_DEFAULT);
        mStreamer.setDisplayPreview(mCameraPreview);
        mStreamer.setOnInfoListener(mOnInfoListener);
        mStreamer.setOnErrorListener(mOnErrorListener);
        //mStreamer.setTargetResolution(StreamerConstants.VIDEO_RESOLUTION_720P);

        //todo ----- tillusory start ----- 关闭金山云自带的美颜
//        enableBeautyFilter();
        tiSDKManager = new TiSDKManagerBuilder().build();
        tiPanelLayout = new TiPanelLayout(this).init(tiSDKManager);
        addContentView(tiPanelLayout, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mStreamer.getImgTexFilterMgt().setFilter(new TiFilter(mStreamer.getGLRender(),tiSDKManager));
    }


    private KSYStreamer.OnInfoListener mOnInfoListener =  new KSYStreamer.OnInfoListener() {
        public void onInfo(int what, int msg1, int msg2) {
            switch (what) {
                case StreamerConstants.KSY_STREAMER_CAMERA_INIT_DONE:
                    TLog.log("初始化完成");
                    mStreamer.startStream();
                    break;
                case StreamerConstants.KSY_STREAMER_OPEN_STREAM_SUCCESS:
                    TLog.log("推流成功");
                    PhoneLiveApi.changeLiveState(mUser.id,mUser.token,mStreamName,"1",null);
                    break;
                case StreamerConstants.KSY_STREAMER_FRAME_SEND_SLOW:

                    AppContext.showToast("网络状况不好",0);
                    break;
                case StreamerConstants.KSY_STREAMER_EST_BW_RAISE:

                    break;
                case StreamerConstants.KSY_STREAMER_EST_BW_DROP:

                    break;
                default:
                    TLog.log("OnInfo: " + what + " msg1: " + msg1 + " msg2: " + msg2);
                    break;
            }
        }
    };
    private KSYStreamer.OnErrorListener mOnErrorListener = new KSYStreamer.OnErrorListener() {
        //
        @Override
        public void onError(int what, int msg1, int msg2) {
            switch (what) {
                case StreamerConstants.KSY_STREAMER_ERROR_DNS_PARSE_FAILED:
                    //url域名解析失败
                    TLog.log( "url域名解析失败");
                    break;
                case StreamerConstants.KSY_STREAMER_ERROR_CONNECT_FAILED:
                    //网络连接失败，无法建立连接
                    TLog.log("网络连接失败");
                    break;
                case StreamerConstants.KSY_STREAMER_ERROR_PUBLISH_FAILED:
                    //跟RTMP服务器完成握手后,向{streamname}推流失败)
                    TLog.log("跟RTMP服务器完成握手后,向推流失败)");

                    break;
                case StreamerConstants.KSY_STREAMER_ERROR_CONNECT_BREAKED:
                    //网络连接断开
                    TLog.log("网络连接断开");

                    break;
                case StreamerConstants.KSY_STREAMER_ERROR_AV_ASYNC:
                    //音视频采集pts差值超过5s
                    TLog.log("KSY_STREAMER_ERROR_AV_ASYNC " + msg1 + "ms");
                    break;
                case StreamerConstants.KSY_STREAMER_VIDEO_ENCODER_ERROR_UNSUPPORTED:
                    //编码器初始化失败
                    TLog.log("编码器初始化失败");
                    break;
                case StreamerConstants.KSY_STREAMER_VIDEO_ENCODER_ERROR_UNKNOWN:
                    //视频编码失败
                    TLog.log("视频编码失败");
                    break;
                case StreamerConstants.KSY_STREAMER_AUDIO_ENCODER_ERROR_UNSUPPORTED:
                    //音频初始化失败
                    TLog.log("音频初始化失败");
                    break;
                case StreamerConstants.KSY_STREAMER_AUDIO_ENCODER_ERROR_UNKNOWN:
                    //音频编码失败
                    TLog.log("音频编码失败");
                    break;
                case StreamerConstants.KSY_STREAMER_AUDIO_RECORDER_ERROR_START_FAILED:
                    //录音开启失败
                    TLog.log("录音开启失败");
                    break;
                case StreamerConstants.KSY_STREAMER_AUDIO_RECORDER_ERROR_UNKNOWN:
                    //录音开启未知错误
                    TLog.log("录音开启未知错误");
                    break;
                case StreamerConstants.KSY_STREAMER_CAMERA_ERROR_UNKNOWN:
                    //摄像头未知错误
                    TLog.log("摄像头未知错误");
                    break;
                case StreamerConstants.KSY_STREAMER_CAMERA_ERROR_START_FAILED:
                    //打开摄像头失败
                    TLog.log( "打开摄像头失败");
                    break;
                case StreamerConstants.KSY_STREAMER_CAMERA_ERROR_SERVER_DIED:
                    //系统Camera服务进程退出
                    TLog.log("系统Camera服务进程退出");
                    break;
                default:

                    break;
            }

            switch (what) {
                case StreamerConstants.KSY_STREAMER_CAMERA_ERROR_UNKNOWN:
                case StreamerConstants.KSY_STREAMER_CAMERA_ERROR_START_FAILED:
                case StreamerConstants.KSY_STREAMER_AUDIO_RECORDER_ERROR_START_FAILED:
                case StreamerConstants.KSY_STREAMER_AUDIO_RECORDER_ERROR_UNKNOWN:
                    break;
                case StreamerConstants.KSY_STREAMER_CAMERA_ERROR_SERVER_DIED:
                    mStreamer.stopCameraPreview();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mStreamer.startCameraPreview();
                        }
                    }, 5000);
                    break;
                //重连
                default:
                    if(mStreamer != null && IS_START_LIVE){
                        mStreamer.startStream();
                    }

                    if(mHandler != null){
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mStreamer.startStream();
                            }
                        }, 3000);
                    }

                    break;
            }

        }
    };

    @Override
    public void onPause() {
        super.onPause();

        mStreamer.onPause();
        if(IS_START_LIVE){
            mStreamer.stopCameraPreview();
        }
        mStreamer.stopStream();
        if(IS_START_LIVE && mHandler != null){
            mHandler.postDelayed(pauseRunnable,1000);
        }
        //提示
        mIMControl.doSendSystemMessage("稍等片刻马上回来!",mUser,0);

    }

    private Runnable pauseRunnable = new Runnable() {
        @Override
        public void run() {
            pauseTime ++ ;
            if(pauseTime >= 60){

                mHandler.removeCallbacks(this);
                videoPlayerEnd();

                return;
            }
            TLog.log(pauseTime + "定时器");
            mHandler.postDelayed(this,1000);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        // 一般可以在onResume中开启摄像头预览
        mStreamer.startCameraPreview();
        // 调用KSYStreamer的onResume接口
        mStreamer.onResume();
        //重置时间,如果超过预期则关闭直播
        if(pauseTime  >=  60){
            showLiveEndDialog(mUser.id,mStreamName);
        }else if(mHandler != null){
            mHandler.removeCallbacks(pauseRunnable);
        }
        pauseTime = 0;

    }

    protected void videoPlayerEnd(){

    }


}
