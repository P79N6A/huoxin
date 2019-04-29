package com.daimeng.livee.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.AudioManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bese.DensityUtil;
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.daimeng.agora.kit.KSYAgoraStreamer;
import com.daimeng.livee.AppContext;
import com.daimeng.livee.Constant;
import com.daimeng.livee.R;
import com.daimeng.livee.api.remote.ApiUtils;
import com.daimeng.livee.api.remote.PhoneLiveApi;
import com.daimeng.livee.base.ShowLiveActivityBase;
import com.daimeng.livee.bean.ChatBean;
import com.daimeng.livee.bean.GiftBean;
import com.daimeng.livee.bean.GiftDanmuEntity;
import com.daimeng.livee.bean.LiveBean;
import com.daimeng.livee.bean.LiveCheckInfoBean;
import com.daimeng.livee.bean.LiveEnterRoomBean;
import com.daimeng.livee.bean.LiveGameUrlBean;
import com.daimeng.livee.bean.SendGiftBean;
import com.daimeng.livee.bean.UserBean;
import com.daimeng.livee.bean.callback.CallBackSendGift;
import com.daimeng.livee.business.LiveTimeBusiness;
import com.daimeng.livee.dialog.LiveCommon;
import com.daimeng.livee.event.Event;
import com.daimeng.livee.fragment.ChestBoxDialogFragment;
import com.daimeng.livee.fragment.GiftListDialogFragment;
import com.daimeng.livee.fragment.LiveRedEnvelopeNewDialog;
import com.daimeng.livee.im.IMControl;
import com.daimeng.livee.interf.IMControlInterface;
import com.daimeng.livee.interf.RedPacketSendCallBack;
import com.daimeng.livee.other.LiveStream;
import com.daimeng.livee.ui.customviews.SwipeAnimationController;
import com.daimeng.livee.utils.DialogHelp;
import com.daimeng.livee.utils.LiveUtils;
import com.daimeng.livee.utils.ShareUtils;
import com.daimeng.livee.utils.SimpleUtils;
import com.daimeng.livee.utils.SocketMsgUtils;
import com.daimeng.livee.utils.StringUtils;
import com.daimeng.livee.utils.TLog;
import com.daimeng.livee.widget.LoadUrlImageView;
import com.daimeng.livee.widget.VideoSurfaceView;
import com.ksyun.media.player.IMediaPlayer;
import com.ksyun.media.player.KSYMediaPlayer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

/*

 * 直播播放页面
 * */
public class RtmpPlayerActivity extends ShowLiveActivityBase implements LiveTimeBusiness.LiveTimeBusinessCallback {
    public static ImageLoader imageLoader = ImageLoader.getInstance();
    public final static String USER_INFO = "USER_INFO";

    @BindView(R.id.vip_btn)
    Button vip_btn;
    @BindView(R.id.video_view)
    VideoSurfaceView mVideoSurfaceView;

    //加载中的背景图
    @BindView(R.id.iv_live_look_loading_bg)
    LoadUrlImageView mIvLoadingBg;

    @BindView(R.id.iv_attention)
    TextView mIvAttention;

    //横竖屏幕切换
    @BindView(R.id.iv_live_direction)
    ImageView mIvDirectionSwitch;

    @BindView(R.id.tv_live_charging_time)
    TextView mTvChargingTime;

    @BindView(R.id.rl_live_quite_gift)
    RelativeLayout mRlQuiteGift;

    @BindView(R.id.iv_live_chestbox)
    ImageView mIvChestbox;

    @BindView(R.id.iv_live_quite_gift)
    ImageView mIvQuiteGift;

    @BindView(R.id.camera_preview)
    GLSurfaceView mCameraPreview;

    private KSYMediaPlayer ksyMediaPlayer;
    private Surface mSurface = null;

    //视频流宽度
    private int mVideoWidth;
    //视频流高度
    private int mVideoHeight;
    //主播信息
    public LiveBean mEmceeInfo;

    private View mLoadingView;
    private LiveRedEnvelopeNewDialog mLiveRedNewDialog;
    private GiftListDialogFragment mGiftListDialogFragment;
    private ChestBoxDialogFragment chestBoxDialogFragment;
    private SwipeAnimationController mSwipeAnimationController;
    //是否点亮过
    private boolean isLit = false;

    private LiveCheckInfoBean liveCheckInfoBean;
    private LiveEnterRoomBean liveEnterRoomBean;

    private int loadVideoErrorCount = 0;
    private GiftBean quiteGift;
    //便宜礼物数量
    private int cheapGiftCount = 0;
    private LiveTimeBusiness mTimeBusiness;
    private KSYAgoraStreamer mStreamer;
    private String eggUrl;
    private String petUrl;
    private String racingUrl;
    private Handler mHandler = new Handler();
    private boolean isLastDiffGift = false;
    private RedPacketSendCallBack redPacketSendCallBack;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_player;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void initView() {
        super.initView();

        SurfaceHolder mSurfaceHolder = mVideoSurfaceView.getHolder();
        mSurfaceHolder.addCallback(mSurfaceCallback);

        mVideoSurfaceView.setOnTouchListener(mTouchListener);
        mVideoSurfaceView.setKeepScreenOn(true);
        mRoot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    onClickLit();
                }
                return mSwipeAnimationController.processEvent(event);
            }
        });

        mSwipeAnimationController = new SwipeAnimationController(this);

        mSwipeAnimationController.setAnimationView(mLiveContent);

        //横屏取消掉聊天ListView
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mLvChatList.setVisibility(View.GONE);
        }

        mGiftListDialogFragment = new GiftListDialogFragment();


    }


    @Override
    public void initData() {
        super.initData();

        quiteGift = LiveUtils.getQuiteGift(this);
        if (quiteGift != null) {
//            mRlQuiteGift.setVisibility(View.VISIBLE);
            SimpleUtils.loadImageForView(this, mIvQuiteGift, quiteGift.getGifticon(), 0);
        }

        Bundle bundle = getIntent().getBundleExtra(USER_INFO);
        liveCheckInfoBean = bundle.getParcelable("check_info");
        //获取用户登陆信息
        mUser = AppContext.getInstance().getLoginUser();

        //获取主播信息
        mEmceeInfo = bundle.getParcelable(USER_INFO);
        mRoomNum = mEmceeInfo.uid;

        mTvLiveNick.setText(mEmceeInfo.user_nicename);
        mStreamName = mEmceeInfo.stream;

        redPacketSendCallBack = new RedPacketSendCallBack() {
            @Override
            public void onSuccess(String redid) {
                if (mIMControl != null) {
                    mIMControl.doSendRedPacket(mUser, mEmceeInfo.uid, redid);
                }

            }
        };

        chestBoxDialogFragment = ChestBoxDialogFragment.newInstance(eggUrl, petUrl, racingUrl, mEmceeInfo.uid, mEmceeInfo.stream, redPacketSendCallBack);

        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        //如果是游戏直播显示屏幕旋转按钮
        if (StringUtils.toInt(liveCheckInfoBean.getType()) == 4) {
            mIvDirectionSwitch.setVisibility(View.VISIBLE);
        } else {
            mIvDirectionSwitch.setVisibility(View.GONE);
        }

        initLive();
        //初始化房间信息
        initRoomInfo();
        //startGetRankInfo(true);
        getGameUrls();


    }

    private void getGameUrls() {
        PhoneLiveApi.getLiveGameUrls(mUser.id
                , mUser.token
                , mEmceeInfo.uid
                , mEmceeInfo.stream
                , new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String s, int id) {
                        String res = ApiUtils.checkIsSuccessReturnString(s);

                        if (res != null) {

                            try {
                                LiveGameUrlBean liveGameUrlBean = JSON.parseObject(res, LiveGameUrlBean.class);

                                eggUrl = liveGameUrlBean.getEgg();
                                petUrl = liveGameUrlBean.getPet();
                                racingUrl = liveGameUrlBean.getRacing();
                                chestBoxDialogFragment = ChestBoxDialogFragment.newInstance(eggUrl, petUrl, racingUrl, mEmceeInfo.uid, mEmceeInfo.stream, redPacketSendCallBack);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    private void initRoomInfo() {

        //请求服务端获取房间基本信息
        PhoneLiveApi.requestEnterRoom(mUser.id
                , mEmceeInfo.uid
                , mUser.token
                , AppContext.address
                , mEmceeInfo.stream
                , new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String s, int id) {
                        String res = ApiUtils.checkIsSuccess2(s);

                        if (res != null) {

                            liveEnterRoomBean = JSON.parseObject(res, LiveEnterRoomBean.class);
                            fillLiveInfo(liveEnterRoomBean);
                        }
                    }
                });

        //设置背景图
        mIvLoadingBg.setVisibility(View.VISIBLE);
        mIvLoadingBg.setImageLoadUrl(mEmceeInfo.avatar);

        mEmceeHead.setAvatarUrl(mEmceeInfo.avatar);
        //初始化直播播放器参数配置

        //是否是计时付费房间 0普通，1收费，2私密，3计时

        //计时付费业务处理
        mTimeBusiness = new LiveTimeBusiness();
        mTimeBusiness.setCallback(this);
        mTimeBusiness.init(this, mEmceeInfo, liveCheckInfoBean);

    }

    private void initLive() {

        //视频播放器init
        ksyMediaPlayer = new KSYMediaPlayer.Builder(this).build();

        ksyMediaPlayer.setOnCompletionListener(mOnCompletionListener);
        ksyMediaPlayer.setOnPreparedListener(mOnPreparedListener);
        ksyMediaPlayer.setOnInfoListener(mOnInfoListener);
        ksyMediaPlayer.setOnVideoSizeChangedListener(mOnVideoSizeChangeListener);
        ksyMediaPlayer.setOnErrorListener(mOnErrorListener);
        ksyMediaPlayer.setScreenOnWhilePlaying(true);

        ksyMediaPlayer.setBufferTimeMax(5);
        try {
            ksyMediaPlayer.setDataSource(mEmceeInfo.pull);
            ksyMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //填充直播间初始化数据
    private void fillLiveInfo(LiveEnterRoomBean data) {
        //财富图标
        mUser.custombackground = data.getCustombackground();
        //守护图标
        if (null != data.getGuardicon()) {
            mUser.guardicon = data.getGuardicon();
        } else {
            mUser.guardicon = "";
        }

        AppContext.getInstance().updateUserInfoField("user.custombackground", mUser.custombackground);
        AppContext.getInstance().updateUserInfoField("user.guardicon", mUser.guardicon);
        mWords = data.getWords();
        //用户列表
        mUserList.addAll(data.getUserlists());

        //房间人数
        IMControl.LIVE_USER_NUMS = data.getNums();

        //弹幕价格
        barrageFee = StringUtils.toInt(data.getBarrage_fee());
        //收益
        mTvIncomeNum.setText(data.getVotestotal());
        LiveUtils.sortUserList(mUserList);
        mUserListAdapter.notifyDataSetChanged();

        if (StringUtils.toInt(data.getIsattention()) == 0) {
            mIvAttention.setVisibility(View.VISIBLE);
        } else {
            mIvAttention.setVisibility(View.GONE);
        }

        //是否是vip
        is_vip = StringUtils.toInt(data.getIs_vip());
        //更新用户余额
        AppContext.getInstance().updateUserInfoField("user.coin", data.getCoin());
        mUser.coin = data.getCoin();

        //链接服务器
        connectToSocketService(data.getChatserver());

    }

    @OnClick({R.id.iv_live_rtc, R.id.iv_live_quite_gift, R.id.iv_live_lit, R.id.iv_live_direction, R.id.iv_live_emcee_head,
            R.id.tglbtn_danmu_setting, R.id.iv_live_shar, R.id.iv_live_privatechat, R.id.iv_live_back,R.id.vip_btn, R.id.ll_yp_label,
            R.id.rl_live_info, R.id.iv_live_chat, R.id.iv_live_look_loading_bg, R.id.bt_send_chat, R.id.iv_live_gift, R.id.iv_attention, R.id.iv_live_chestbox})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_live_rtc:

                clickRequestRtc();
                break;

            case R.id.iv_live_quite_gift:

                clickQuiteGift();
                break;
            case R.id.iv_live_chestbox:

                chestBoxDialogFragment.show(getSupportFragmentManager(), "GiftListDialogFragment");
                break;
            case R.id.vip_btn:
                show2();
                break;
            //点赞
            case R.id.iv_live_lit:
                onClickLit();
                break;
            //横竖屏幕切换
            case R.id.iv_live_direction:
                directionSwitch();
                break;
            case R.id.iv_live_emcee_head:
                showUserInfoDialog(LiveUtils.getSimleUserInfo(mEmceeInfo));
                break;
            case R.id.iv_live_shar:
                ShareUtils.showSharePopWindow(this, v);
                break;
            //私信
            case R.id.iv_live_privatechat:
                showPrivateChat();
                break;
            //退出直播间
            case R.id.iv_live_back:
                finish();
                break;
            //票数排行榜
            case R.id.ll_yp_label:
                Intent intent = new Intent(this,OrderWebViewActivity.class);
                intent.putExtra("muid",mEmceeInfo.uid);
                startActivity(intent);
//                OrderWebViewActivity.startOrderWebView(this, mEmceeInfo.uid);
                break;
            //发言框
            case R.id.iv_live_chat:
                changeEditStatus(true);
                break;
            //开启关闭弹幕
            case R.id.tglbtn_danmu_setting:
                openOrCloseDanMu();
                break;
            case R.id.bt_send_chat:
                //等待优化，可能会造成卡顿
                clickSendChat();
                break;
            case R.id.iv_live_look_loading_bg:

                changeEditStatus(false);
                break;
            case R.id.iv_live_gift:

                new GiftListDialogFragment().show(getSupportFragmentManager(), "GiftListDialogFragment");
                break;
            case R.id.rl_live_info:
                //左上角点击主播信息
                showUserInfoDialog(LiveUtils.getSimleUserInfo(mEmceeInfo));
                break;

            case R.id.iv_attention:
                //关注主播
                clickFollowEmcee();

                break;

            default:

                break;
        }
    }


    //链接IM服务器
    private void connectToSocketService(String chatUrl) {

        //连接socket服务器
        try {
            mIMControl = new IMControl(new ChatListenUIRefresh(), this, chatUrl);
            //连接到socket服务端
            mIMControl.connectSocketServer(mUser, mEmceeInfo.stream, mEmceeInfo.uid);

        } catch (URISyntaxException e) {
            e.printStackTrace();
            TLog.log("connect error");
        }
    }


    //申请连麦
    private void clickRequestRtc() {

        if (isLiveRtc) {
            new AlertView("提示", "是否关闭连麦？", "取消", new String[]{"确定"}, null, this, AlertView.Style.Alert, new OnItemClickListener() {
                @Override
                public void onItemClick(Object o, int i) {

                    clickEndRtc();

                }
            }).setCancelable(true).show();
            return;
        }

        showWaitDialog("正在提交申请...", false);
        PhoneLiveApi.requestLiveRtc(mUser.id, mUser.token, mEmceeInfo.uid, mEmceeInfo.stream, new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {
                hideWaitDialog();
            }

            @Override
            public void onResponse(String response, int id) {
                hideWaitDialog();
                String res = ApiUtils.checkIsSuccess2(response);
                if (res != null) {
                    LiveCommon.showMsgDialog(RtmpPlayerActivity.this, "等待主播回复...");
                    //发送IM消息给主播申请连麦
                    mIMControl.doSendRequestRtc(mUser, mEmceeInfo.uid);
                }
            }
        });
    }

    //结束连麦
    private void clickEndRtc() {

        showWaitDialog("正在加载中...", false);
        PhoneLiveApi.requestEndRtc(mUser.id, mUser.token, mEmceeInfo.uid, new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {
                hideWaitDialog();
            }

            @Override
            public void onResponse(String response, int id) {
                hideWaitDialog();
                String res = ApiUtils.checkIsSuccess2(response);
                if (res != null) {

                    mIMControl.doSendRequestEndRtc(mUser, mEmceeInfo.uid);
                    stopRtc();
                }
            }
        });
    }

    //关注主播
    private void clickFollowEmcee() {

        PhoneLiveApi.showFollow(mUser.id, mEmceeInfo.uid, mUser.token, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
            }

            @Override
            public void onResponse(String response, int id) {
                JSONArray res = ApiUtils.checkIsSuccess(response);
                if (null != res) {
                    mIvAttention.setVisibility(View.GONE);
                    AppContext.showToast("关注成功");
                }
            }
        });

        if (mIMControl != null) {
            mIMControl.doSendSystemMessage(mUser.user_nicename + "关注了主播", mUser, 1);
        }
    }

    //点击发送文字消息
    private void clickSendChat() {
        mUser = AppContext.getInstance().getLoginUser();
        //弹幕判断
        if (mDanMuIsOpen) {
            sendBarrage();
        } else {
            int level = StringUtils.toInt(mPConfigBean.live_chat_level);
            if (StringUtils.toInt(mUser.level) < level) {
                AppContext.showToast("等级大于等于" + level + "才可以发言");
                return;
            }
            if (TextUtils.isEmpty(mChatInput.getText())) {
                return;
            }
            sendChat();
//            String value = mChatInput.getText().toString();
//            if (level >= 3){
//                sendChat();
//            }else {
//                if (value.length() > 15) {
//                    AppContext.showToast("等级小于3级，一句话限制15字以内");
//                } else {
//                    sendChat();
//                }
//            }
        }
    }

    //赠送快捷礼物
    private void clickQuiteGift() {

        PhoneLiveApi.sendGift(mUser, quiteGift, mEmceeInfo.uid, mEmceeInfo.stream, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                JSONArray s = ApiUtils.checkIsSuccess(response);
                if (s != null) {
                    try {
                        cheapGiftCount++;
                        CallBackSendGift callBackSendGift = JSON.parseObject(s.getString(0), CallBackSendGift.class);
                        //获取剩余金额,重新赋值
                        mUser.coin = callBackSendGift.getCoin();
                        mUser.level = callBackSendGift.getLevel();
                        AppContext.getInstance().saveUserInfo(mUser);

                        sendGift(callBackSendGift.getGifttoken(), "y", cheapGiftCount, quiteGift);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    //点亮
    private void onClickLit() {

        int random = new Random().nextInt(3);
        if (!isLit && mIMControl != null) {
            isLit = true;
            mIMControl.doSendLitMsg(mUser, random);
        }
        showLit();
    }

    //横竖屏切换
    private void directionSwitch() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //竖屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        } else {
            //横屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    //分享操作
    public void share(View v) {
        ShareUtils.share(this, v.getId(), LiveUtils.getSimleUserInfo(mEmceeInfo));
    }


    @Override
    public void onPayLiveStartTime() {
        mTvChargingTime.setVisibility(View.VISIBLE);

    }

    @Override
    public void onPayDownTimeRefresh(long time) {
        mTvChargingTime.setVisibility(View.VISIBLE);
        mTvChargingTime.setText(String.format(Locale.CHINA, "倒数计时%d", time));
    }

    @Override
    public void onPayTimeRefresh(int lookTime) {
        mTvChargingTime.setVisibility(View.VISIBLE);
        mTvChargingTime.setText(String.format(Locale.CANADA, "观看第%d分钟", lookTime));
    }

    @Override
    public void onPayDownTimeEnd() {

        if (ksyMediaPlayer != null && !isFinishing()) {
            //暂停直播
            ksyMediaPlayer.pause();
        }
    }

    @Override
    public void onPayCancel() {
        //取消付费
        finish();
    }

    @Override
    public void onPayTimeError(String message) {

        if (ksyMediaPlayer != null) {
            ksyMediaPlayer.pause();

        }

        if (isFinishing()) return;
        Dialog dialog = DialogHelp.getMessageDialog(this, message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        }).create();

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    public void onPayTimeChargeSuccess(String coin) {

        UserBean userBean = AppContext.getInstance().getLoginUser();
        mUser.coin = coin;
        userBean.coin = mUser.coin;
        AppContext.getInstance().saveUserInfo(userBean);

        if (ksyMediaPlayer != null && !ksyMediaPlayer.isPlaying()) {
            ksyMediaPlayer.start();
        }
    }


    //IM事件监听
    private class ChatListenUIRefresh implements IMControlInterface {

        @Override
        public void onMessageListen(final SocketMsgUtils socketMsg, final int type, final ChatBean c) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (StringUtils.toInt(socketMsg.getRetcode()) == Constant.MSG_CODE.SHOT_UP) {
                        AppContext.showToast("你已经被禁言", 0);
                        return;
                    }

                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        //addDanmu(c.getSimpleUserInfo().id, socketMsg.getCt());
                    } else {
                        addChatMessage(c);
                    }
                }
            });
        }

        //弹幕
        @Override
        public void onDanMuMessage(SocketMsgUtils socketMsg, final ChatBean c) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addPayDanmu(c);
                }
            });

        }

        @Override
        public void onOpenGame(final SocketMsgUtils socketMsg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });
        }

        @Override
        public void onGameStart(final SocketMsgUtils socketMsg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });
        }

        @Override
        public void onGameOpenResult(final SocketMsgUtils socketMsg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });
        }

        @Override
        public void onGameBet(final SocketMsgUtils socketMsg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });
        }

        @Override
        public void onOpenTimeLive(final SocketMsgUtils msgUtils, ChatBean c) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (ksyMediaPlayer != null) {
                        ksyMediaPlayer.pause();
                    }

                    //修改直播类型为计时付费
                    mTimeBusiness.getmLiveCheckInfo().setType(String.valueOf(ShowLiveActivityBase.LIVE_TYPE_TIME));
                    //计时付费价格
                    mTimeBusiness.getmLiveCheckInfo().setType_val(msgUtils.getParam("money", "0"));

                    LiveCommon.showConfirmDialog(RtmpPlayerActivity.this, "提示", getString(R.string.pay_time_msg, "1", mTimeBusiness.getmLiveCheckInfo().getType_val())
                            , new com.daimeng.livee.interf.DialogInterface() {
                                @Override
                                public void cancelDialog(View v, Dialog d) {
                                    finish();
                                }

                                @Override
                                public void determineDialog(View v, Dialog d) {
                                    if (mHandler != null) {
                                        mTimeBusiness.chargeLiveRequest();
                                        d.dismiss();
                                    }
                                }
                            });
                }
            });
        }

        @Override
        public void onGameEnd(SocketMsgUtils socketMsg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });
        }

        @Override
        public void onAllGiftMessage(SocketMsgUtils socketMsg) {


        }

        @Override
        public void onConnect(final boolean res) {
            //连接结果
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onConnectRes(res);
                }
            });
        }


        //用户状态改变
        @Override
        public void onUserStateChange(final SocketMsgUtils socketMsg, final UserBean user, final boolean state) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onUserStatusChange(socketMsg, user, state);

                    if (state && !mUserList.contains(user)) {

                    }
                }
            });

        }

        //主播关闭直播
        @Override
        public void onRoomClose(final int code) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        directionSwitch();
                    }

                    showLiveEndDialog(mUser.id, "");
                    videoPlayerEnd();

                }
            });

        }

        @Override
        public void onShowTrack(final SocketMsgUtils socketMsg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //GiftDanmuEntity giftDanmuEntity=JSON.parseObject(socketMsg.getCt(), GiftDanmuEntity.class);
                    GiftDanmuEntity giftDanmuEntity = new GiftDanmuEntity();

                    giftDanmuEntity.setTrackType(socketMsg.getParam("Tracktype", "1"));
                    giftDanmuEntity.setShowid(socketMsg.getParam("showid", "1"));
                    giftDanmuEntity.setSender(socketMsg.getParam("sendname", ""));
                    giftDanmuEntity.setReceiver(socketMsg.getParam("receivename", ""));
                    giftDanmuEntity.setGift(socketMsg.getParam("gift", ""));
                    giftDanmuEntity.setGiftImage(socketMsg.getParam("giftImg", ""));
                    giftDanmuEntity.setStream(socketMsg.getParam("stream", ""));
                    giftDanmuEntity.setBroadcast_msg(socketMsg.getParam("broadcast_msg", ""));
                    giftDanmuEntity.setBroadcast_type(socketMsg.getParam("broadcast_type", ""));

                    String type = giftDanmuEntity.getTrackType();
                    if (type.equals("1")) {
                        addGiftBigDanMu(giftDanmuEntity);
                    } else if (type.equals("0")) {
                        joinGiftAnimation(giftDanmuEntity);
                    }
                }
            });
        }

        //送礼物展示
        @Override
        public void onShowSendGift(final SendGiftBean giftInfo, final ChatBean chatBean) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addGiftMessage(giftInfo, chatBean);
                }
            });

        }

        @Override
        public void onKickRoom(final SocketMsgUtils socketMsg, final ChatBean c) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (socketMsg.get2Uid().equals(mUser.id)) {

                        //踢人
                        videoPlayerEnd();

                        AlertDialog alertDialog = DialogHelp.getMessageDialog(RtmpPlayerActivity.this, "您已被踢出房间",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                })
                                .create();
                        alertDialog.setCancelable(false);
                        alertDialog.setCanceledOnTouchOutside(false);

                        alertDialog.show();
                    }
                    addChatMessage(c);
                }
            });

        }

        @Override
        public void onShutUp(final SocketMsgUtils socketMsg, final ChatBean c) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (socketMsg.get2Uid().equals(mUser.id)) {
                        //禁言
                        changeEditStatus(false);
                    }
                    addChatMessage(c);
                }
            });
        }


        //特权操作
        @Override
        public void onPrivilegeAction(final SocketMsgUtils socketMsg, final ChatBean c) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (socketMsg.getAction().equals("13") && socketMsg.get2Uid().equals(mUser.id)) {

                        DialogHelp.getMessageDialog(RtmpPlayerActivity.this, socketMsg.getCt()).show();
                    }

                    addChatMessage(c);
                }
            });
        }

        //点亮
        @Override
        public void onLit(SocketMsgUtils socketMsg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //showLit(mRandom.nextInt(3));
                }
            });

        }

        //添加僵尸粉丝
        @Override
        public void onAddZombieFans(final SocketMsgUtils socketMsg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addZombieFans(socketMsg.getCt());
                }
            });
        }

        //服务器连接错误
        @Override
        public void onError() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AppContext.showToast("服务器连接错误");
                }
            });
        }

        @Override
        public void onJinhuaGameMessageListen(final SocketMsgUtils socketMsg) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });
        }

        @Override
        public void onRequestRtc(SocketMsgUtils socketMsg) {

        }

        @Override
        public void onRequestReplyRtc(final SocketMsgUtils socketMsg) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (StringUtils.toInt(socketMsg.getParam("to_user_id", 0)) == StringUtils.toInt(mUser.id)) {

                        if (StringUtils.toInt(socketMsg.getParam("reply_action", 0)) == 2) {
                            LiveCommon.showMsgDialog(RtmpPlayerActivity.this, "主播拒绝了你的连麦请求！");
                        } else {
                            onStartRtcInit();
                        }
                    }
                }
            });
        }

        @Override
        public void onRequestEndRtc(final SocketMsgUtils socketMsg) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (StringUtils.toInt(socketMsg.getParam("to_user_id", 0)) == StringUtils.toInt(mUser.id)) {
                        stopRtc();
                        AppContext.showToast("连麦结束！");
                    }
                }
            });
        }

        @Override
        public void onSendRedPacket(final SocketMsgUtils socketMsg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    mLiveRedNewDialog = LiveRedEnvelopeNewDialog.newInstance(socketMsg, mUser, mEmceeInfo);
                    mLiveRedNewDialog.show(getSupportFragmentManager(), "LiveRedEnvelopeNewDialog");

//                    addSystemMessage("");

                }
            });
        }

        @Override
        public void onSendGameGift(final SocketMsgUtils socketMsg) {
            //玩游戏得到的礼物消息
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ChatBean c = new ChatBean();
                    c.setType(Constant.IM_TEXT_TYPE.SYSTEM_GAME_GIFT_MSG);
                    c.setContent(socketMsg.getCt());
                    c.setSendChatMsg(socketMsg.getGift_icon());
                    mListChats.add(c);
                    mChatListAdapter.notifyDataSetChanged();
                    if (mLvChatList != null) {
                        mLvChatList.setSelection(mListChats.size() - 1);
                    }
                }
            });

        }

    }

    //开始连麦
    private void onStartRtcInit() {

        mCameraPreview.setVisibility(View.VISIBLE);
        mVideoSurfaceView.setVisibility(View.GONE);
        ksyMediaPlayer.stop();
        //直播参数配置
        if (mStreamer == null) {
            mStreamer = new LiveStream(this);
            mStreamer.setPreviewFps(15);
            mStreamer.setDisplayPreview(mCameraPreview);
        }
        mStreamer.startCameraPreview();

//        mStreamer.setRTCSubScreenRect(0.65f, 0.f, 0.3f, 0.35f, KSYAgoraStreamer
//                .SCALING_MODE_CENTER_CROP);
        mStreamer.setRTCMainScreen(KSYAgoraStreamer.RTC_MAIN_SCREEN_REMOTE);
        String tempChannel = mRoomNum;
        mStreamer.startRTC(tempChannel);
        isLiveRtc = true;
    }

    //结束连麦
    private void stopRtc() {

        isLiveRtc = false;
        if (mStreamer != null) {
            mStreamer.stopRTC();
            mStreamer.stopCameraPreview();
            mCameraPreview.setVisibility(View.GONE);
            mVideoSurfaceView.setVisibility(View.VISIBLE);
            ksyMediaPlayer.prepareAsync();
        }
    }


    //发送礼物消息
    private void sendGift(final String token, final String eventsend, final int count, final GiftBean giftBean) {
        final GiftDanmuEntity g = new GiftDanmuEntity();
        String sendname = mUser.user_nicename;
        mUser = AppContext.getInstance().getLoginUser();
        String recevename = mEmceeInfo.user_nicename;
        g.setSender(sendname);
        g.setReceiver(recevename);
        g.setGift(count + "个" + giftBean.getGiftname());
        g.setGiftImage(giftBean.getGifticon());
        mIMControl.doSendGift(token, mUser, eventsend, String.valueOf(is_vip));
        mIMControl.doSendMsg("我送了" + count + "个" + giftBean.getGiftname(), mUser, is_vip, 0, 1);
    }


    //直播结束释放资源
    private void videoPlayerEnd() {

        mShowGiftAnimator.removeAllViews();

        if (mGiftListDialogFragment != null && mGiftListDialogFragment.isAdded()) {
            mGiftListDialogFragment.dismiss();
        }

        if (chestBoxDialogFragment != null && chestBoxDialogFragment.isAdded()) {
            chestBoxDialogFragment.dismiss();
        }
        if (mIMControl != null) {
            mIMControl.close();
        }

        if (mStreamer != null) {
            mStreamer.stopCameraPreview();
            mStreamer.stopRTC();
            //mStreamer.release();
        }

        if (ksyMediaPlayer != null) {
            ksyMediaPlayer.release();
            ksyMediaPlayer = null;
        }
        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }

        if (mGiftView != null) {
            mRoot.removeView(mGiftView);
        }

    }

    private IMediaPlayer.OnPreparedListener mOnPreparedListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer mp) {
            //直播开始
            if (null != mLoadingView) {
                mRoot.removeView(mLoadingView);
                mLoadingView = null;
            }
            mIvLoadingBg.setVisibility(View.GONE);

            ksyMediaPlayer.start();
        }
    };


    private IMediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangeListener = new IMediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sarNum, int sarDen) {
            if (mVideoWidth > 0 && mVideoHeight > 0) {
                if (width != mVideoWidth || height != mVideoHeight) {
                    mVideoWidth = mp.getVideoWidth();
                    mVideoHeight = mp.getVideoHeight();

                    if (mVideoSurfaceView != null) {
                        mVideoSurfaceView.setVideoDimension(mVideoWidth, mVideoHeight);
                        mVideoSurfaceView.requestLayout();
                    }
                }
            }
        }
    };

    //视频播放完成
    private IMediaPlayer.OnCompletionListener mOnCompletionListener = new IMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer mp) {

            // 重新链接
            ksyMediaPlayer.reload(mEmceeInfo.pull, true);
        }
    };

    //错误异常监听
    private IMediaPlayer.OnErrorListener mOnErrorListener = new IMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(IMediaPlayer mp, int what, int extra) {

            //播放视频失败
            loadVideoErrorCount++;
            if (loadVideoErrorCount > 3) {
                ksyMediaPlayer.stop();
                ksyMediaPlayer.release();
                AppContext.showToast("视频地址无法播放!");
            }

            return false;
        }
    };

    //视频播放信息
    public IMediaPlayer.OnInfoListener mOnInfoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer iMediaPlayer, int info, int i1) {
            if (info == IMediaPlayer.MEDIA_INFO_RELOADED)
                //重连成功
                TLog.log("重连成功");

            if (info == IMediaPlayer.MEDIA_INFO_SUGGEST_RELOAD) {
                //建议此时重连
                ksyMediaPlayer.reload(mEmceeInfo.pull, true);

            }
            return false;
        }
    };

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            changeEditStatus(false);
            return false;
        }
    };

    private final SurfaceHolder.Callback mSurfaceCallback = new Callback() {
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            if (ksyMediaPlayer != null) {
                final Surface newSurface = holder.getSurface();
                ksyMediaPlayer.setDisplay(holder);
                ksyMediaPlayer.setScreenOnWhilePlaying(true);
                //设置视频缩放模式
                ksyMediaPlayer.setVideoScalingMode(KSYMediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                if (mSurface != newSurface) {
                    mSurface = newSurface;
                    ksyMediaPlayer.setSurface(mSurface);
                }
            }
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

            if (ksyMediaPlayer != null) {
                mSurface = null;
            }
        }
    };


    //当每个聊天被点击显示该用户详细信息弹窗
    @Override
    public void chatListItemClick(ChatBean chat) {
        if (chat.getType() != 13) {

            showUserInfoDialog(chat.mSimpleUserInfo);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event.VideoEvent event) {

        if (event.action == 1 && mIMControl != null) {

            //关注
            mIMControl.doSendSystemMessage(mUser.user_nicename + "关注了主播", mUser, 1);
            mIvAttention.setVisibility(View.GONE);
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(final Event.CloseRoomEvent event) {

        if (event.action == 1) {

            if (!TextUtils.isEmpty(event.roomId) && !event.roomId.equals(mEmceeInfo.uid)) {
                //finish();
                DialogHelp.getConfirmDialog(RtmpPlayerActivity.this, "是否进入切换直播间？", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        finish();
                        requestCheckLive(event.roomId, event.stream);
                    }
                }).show();
            }
        }
    }


    //获取直播信息
    private void requestCheckLive(String liveuid, String stream) {

        PhoneLiveApi.requestCheckLive(AppContext.getInstance().getLoginUid(), AppContext.getInstance().getToken(), liveuid, stream, new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {

                LiveCheckInfoBean data = ApiUtils.checkIsSuccess(response, LiveCheckInfoBean.class);
                if (data != null) {
                    LiveUtils.joinLiveRoom(RtmpPlayerActivity.this, data);
                }
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGiftEvent(Event.SendGiftEvent event) {

        if (mIMControl != null) {

            sendGift(event.giftToken, event.isOutTime, event.count, event.sendGiftBean);
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        int type = StringUtils.toInt(liveCheckInfoBean.getType());
        if (ksyMediaPlayer != null) {
            //倒数计时是否完成
            if (mTimeBusiness.onResumeCheckLive()) {
                ksyMediaPlayer.start();
            } else if (type == 0) {
                ksyMediaPlayer.start();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (ksyMediaPlayer != null) {
            ksyMediaPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        //解除广播
        super.onDestroy();

        if (mIMControl != null) {

            mIMControl.close();

        }
        if (mStreamer != null) {
            mStreamer.stopCameraPreview();
            mStreamer.stopStream();
            mStreamer.stopRTC();
        }
        //mStreamer.release();

        if (ksyMediaPlayer != null) {
            ksyMediaPlayer.release();

        }
        if (mTimeBusiness != null) {
            mTimeBusiness.release();
        }

        OkHttpUtils.getInstance().cancelTag("requestCharging");
    }



    private void show2() {
        final Dialog bottomDialog = new Dialog(this, R.style.BottomDialog);
        final View contentView = LayoutInflater.from(this).inflate(R.layout.layout_bottom_, null);
        bottomDialog.setContentView(contentView);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) contentView.getLayoutParams();
        params.width = getResources().getDisplayMetrics().widthPixels - DensityUtil.dp2px(this, 16f);
        params.bottomMargin = DensityUtil.dp2px(this, 8f);
        contentView.setLayoutParams(params);
        bottomDialog.setCanceledOnTouchOutside(true);
        bottomDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        bottomDialog.show();
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                bottomDialog.dismiss();
            }
        },7000);//延时7s执行
        PhoneLiveApi.getYpOrder(mEmceeInfo.uid, new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }
                    @Override
                    public void onResponse(String response,int id) {
                        try {
                            JSONObject res = new JSONObject(response);
                            if (res!=null){
                                final JSONObject JsonObject = res.optJSONObject("data");
                                if (JsonObject!=null){
                                    final JSONArray jsonArray=JsonObject.optJSONArray("info");
                                    if (jsonArray!=null){
                                        final JSONObject JsonOjt_0 = jsonArray.optJSONObject(0);
                                        if (JsonOjt_0!=null){
                                            JSONObject object_0 = JsonOjt_0.optJSONObject("userinfo");
                                            final String vip_uid_0 = object_0.optString("id");
                                            String textView_0 = object_0.optString("user_nicename");
                                            String vip_image_0 = object_0.optString("avatar_thumb");
                                            final ImageView imageView_1=contentView.findViewById(R.id.bottom_iv1);
                                            final TextView textView_one=contentView.findViewById(R.id.vip_tv_one);
                                            DisplayImageOptions options = new DisplayImageOptions.Builder()
                                                    .cacheInMemory(true)// 设置下载的图片是否缓存在内存中
                                                    .cacheOnDisk(true)// 设置下载的图片是否缓存在SD卡中
                                                    .displayer(new RoundedBitmapDisplayer(100))// 设置成圆角图片
                                                    .build();// 创建DisplayImageOptions对象
                                            // 使用ImageLoader加载图片
                                            imageLoader.displayImage(vip_image_0,
                                                    imageView_1, options);
                                            textView_one.setText(textView_0);
                                            textView_one.setTextColor(Color.rgb(128,0,128));
                                            textView_one.setSingleLine(true);
                                            imageView_1.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    LiveBean liveBean = new LiveBean();
                                                    liveBean.uid=vip_uid_0;
                                                    showUserInfoDialog(LiveUtils.getSimleUserInfo(liveBean));
                                                }
                                            });
//                                                      Glide.with(RtmpPlayerActivity.this).load(path).asBitmap().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageView_1);
                                            JSONObject JsonOjt_1 = jsonArray.optJSONObject(1);
                                            if (JsonOjt_1!=null){
                                                JSONObject object_1 = JsonOjt_1.optJSONObject("userinfo");
                                                final String vip_uid_1 = object_1.optString("id");
                                                String textView_1 = object_1.optString("user_nicename");
                                                String vip_image_1 = object_1.optString("avatar_thumb");
                                                ImageView imageView_2=contentView.findViewById(R.id.bottom_iv2);
                                                final TextView textView_two=contentView.findViewById(R.id.vip_tv_two);
                                                imageLoader.displayImage(vip_image_1,
                                                        imageView_2, options);
                                                textView_two.setText(textView_1);
                                                textView_two.setTextColor(Color.rgb(128,0,128));
                                                textView_two.setSingleLine(true);
                                                imageView_2.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        LiveBean liveBean = new LiveBean();
                                                        liveBean.uid=vip_uid_1;
                                                        showUserInfoDialog(LiveUtils.getSimleUserInfo(liveBean));
                                                    }
                                                });
                                                JSONObject JsonOjt_2 = jsonArray.optJSONObject(2);
                                                if (JsonOjt_2!=null){
                                                    JSONObject object_2 = JsonOjt_2.optJSONObject("userinfo");
                                                    final String vip_uid_2 = object_2.optString("id");
                                                    String textView_2 = object_2.optString("user_nicename");
                                                    String vip_image_2 = object_2.optString("avatar_thumb");
                                                    ImageView imageView_3=contentView.findViewById(R.id.bottom_iv3);
                                                    final TextView textView_three=contentView.findViewById(R.id.vip_tv_three);
                                                    imageLoader.displayImage(vip_image_2,
                                                            imageView_3, options);
                                                    textView_three.setText(textView_2);
                                                    textView_three.setTextColor(Color.rgb(128,0,128));
                                                    textView_three.setSingleLine(true);
                                                    imageView_3.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            LiveBean liveBean = new LiveBean();
                                                            liveBean.uid=vip_uid_2;
                                                            showUserInfoDialog(LiveUtils.getSimleUserInfo(liveBean));
                                                        }
                                                    });
                                                    JSONObject JsonOjt_3 = jsonArray.optJSONObject(3);
                                                    if (JsonOjt_3!=null){
                                                        JSONObject object_3= JsonOjt_3.optJSONObject("userinfo");
                                                        final String vip_uid_3 = object_3.optString("id");
                                                        String vip_image_3 = object_3.optString("avatar_thumb");
                                                        ImageView imageView_4=contentView.findViewById(R.id.bottom_bottom_iv1);
                                                        imageLoader.displayImage(vip_image_3,
                                                                imageView_4, options);
                                                        imageView_4.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                LiveBean liveBean = new LiveBean();
                                                                liveBean.uid=vip_uid_3;
                                                                showUserInfoDialog(LiveUtils.getSimleUserInfo(liveBean));
                                                            }
                                                        });
                                                        JSONObject JsonOjt_4 = jsonArray.optJSONObject(4);
                                                        if (JsonOjt_4!=null){
                                                            JSONObject object_4 = JsonOjt_4.optJSONObject("userinfo");
                                                            final String vip_uid_4 = object_4.optString("id");
                                                            String vip_image_4 = object_4.optString("avatar_thumb");
                                                            ImageView imageView_5=contentView.findViewById(R.id.bottom_bottom_iv2);
                                                            imageLoader.displayImage(vip_image_4,
                                                                    imageView_5, options);
                                                            imageView_5.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View view) {
                                                                    LiveBean liveBean = new LiveBean();
                                                                    liveBean.uid=vip_uid_4;
                                                                    showUserInfoDialog(LiveUtils.getSimleUserInfo(liveBean));
                                                                }
                                                            });
                                                            JSONObject JsonOjt_5 = jsonArray.optJSONObject(5);
                                                            if (JsonOjt_5!=null){
                                                                JSONObject object_5 = JsonOjt_5.optJSONObject("userinfo");
                                                                final String vip_uid_5 = object_5.optString("id");
                                                                String vip_image_5 = object_5.optString("avatar_thumb");
                                                                ImageView imageView_6=contentView.findViewById(R.id.bottom_bottom_iv3);
                                                                imageLoader.displayImage(vip_image_5,
                                                                        imageView_6, options);
                                                                imageView_6.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View view) {
                                                                        LiveBean liveBean = new LiveBean();
                                                                        liveBean.uid=vip_uid_5;
                                                                        showUserInfoDialog(LiveUtils.getSimleUserInfo(liveBean));
                                                                    }
                                                                });
                                                                JSONObject JsonOjt_6 = jsonArray.optJSONObject(6);
                                                                if (JsonOjt_6!=null){
                                                                    JSONObject object_6 = JsonOjt_6.optJSONObject("userinfo");
                                                                    final String vip_uid_6 = object_6.optString("id");
                                                                    String vip_image_6 = object_6.optString("avatar_thumb");
                                                                    ImageView imageView_7=contentView.findViewById(R.id.bottom_bottom_iv4);
                                                                    imageLoader.displayImage(vip_image_6,
                                                                            imageView_7, options);
                                                                    imageView_7.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View view) {
                                                                            LiveBean liveBean = new LiveBean();
                                                                            liveBean.uid=vip_uid_6;
                                                                            showUserInfoDialog(LiveUtils.getSimleUserInfo(liveBean));
                                                                        }
                                                                    });
                                                                    JSONObject JsonOjt_7 = jsonArray.optJSONObject(7);
                                                                    if (JsonOjt_7!=null){
                                                                        JSONObject object_7 = JsonOjt_7.optJSONObject("userinfo");
                                                                        final String vip_uid_7 = object_7.optString("id");
                                                                        String vip_image_7 = object_7.optString("avatar_thumb");
                                                                        ImageView imageView_8=contentView.findViewById(R.id.bottom_bottom_iv5);
                                                                        imageLoader.displayImage(vip_image_7,
                                                                                imageView_8, options);
                                                                        imageView_8.setOnClickListener(new View.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(View view) {
                                                                                LiveBean liveBean = new LiveBean();
                                                                                liveBean.uid=vip_uid_7;
                                                                                showUserInfoDialog(LiveUtils.getSimleUserInfo(liveBean));
                                                                            }
                                                                        });
                                                                        JSONObject JsonOjt_8 = jsonArray.optJSONObject(8);
                                                                        if (JsonOjt_8!=null){
                                                                            JSONObject object_8 = JsonOjt_8.optJSONObject("userinfo");
                                                                            final String vip_uid_8 = object_8.optString("id");
                                                                            String vip_image_8 = object_8.optString("avatar_thumb");
                                                                            ImageView imageView_9=contentView.findViewById(R.id.bottom_bottom_iv6);
                                                                            imageLoader.displayImage(vip_image_8,
                                                                                    imageView_9, options);
                                                                            imageView_9.setOnClickListener(new View.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(View view) {
                                                                                    LiveBean liveBean = new LiveBean();
                                                                                    liveBean.uid=vip_uid_8;
                                                                                    showUserInfoDialog(LiveUtils.getSimleUserInfo(liveBean));
                                                                                }
                                                                            });
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
        );

    }
}
