package com.daimeng.livee.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.daimeng.agora.kit.KSYAgoraStreamer;
import com.daimeng.livee.Constant;
import com.daimeng.livee.base.LivePushBaseActivity;
import com.daimeng.livee.bean.GiftDanmuEntity;
import com.daimeng.livee.bean.LiveStartSettingParamBean;
import com.daimeng.livee.dialog.LiveRtcListDialogFragment;
import com.daimeng.livee.fragment.LivePlugsDialogFragment;
import com.daimeng.livee.dialog.LiveCommon;
import com.hyphenate.util.NetUtils;
import com.daimeng.livee.api.remote.ApiUtils;
import com.daimeng.livee.bean.ChatBean;
import com.daimeng.livee.bean.UserBean;
import com.daimeng.livee.event.Event;
import com.daimeng.livee.fragment.SearchMusicDialogFragment;
import com.daimeng.livee.im.IMControl;
import com.daimeng.livee.utils.InputMethodUtils;
import com.daimeng.livee.utils.SocketMsgUtils;
import com.daimeng.livee.utils.StringUtils;
import com.daimeng.livee.widget.music.ILrcBuilder;
import com.daimeng.livee.widget.music.LrcRow;
import com.daimeng.livee.widget.music.LrcView;
import com.daimeng.livee.AppContext;
import com.daimeng.livee.R;
import com.daimeng.livee.api.remote.PhoneLiveApi;
import com.daimeng.livee.bean.SendGiftBean;
import com.daimeng.livee.fragment.MusicPlayerDialogFragment;
import com.daimeng.livee.interf.IMControlInterface;
import com.daimeng.livee.utils.DialogHelp;
import com.daimeng.livee.utils.LiveUtils;
import com.daimeng.livee.utils.ShareUtils;
import com.daimeng.livee.widget.music.DefaultLrcBuilder;
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
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 直播页面
 */
public class RtmpPushActivity extends LivePushBaseActivity implements SearchMusicDialogFragment.SearchMusicFragmentInterface {
    //歌词显示控件
    @BindView(R.id.lcv_live_start)
    LrcView mLrcView;

    @BindView(R.id.iv_live_camera_control)
    ImageView mIvCameraControl;

    @BindView(R.id.fl_bottom_menu)
    FrameLayout mFlBottomMenu;

    @BindView(R.id.rl_live_music)
    LinearLayout mViewShowLiveMusicLrc;

    @BindView(R.id.iv_attention)
    TextView mIvAttention;

    private Timer mTimer;
    private int mPlayTimerDuration = 1000;

    private MediaPlayer mPlayer;

    //美颜效果调节
    private AppCompatSeekBar mGrindSeekBar;
    private AppCompatSeekBar mWhitenSeekBar;
    private AppCompatSeekBar mRuddySeekBar;

    //房间是否开启定时扣费
    private boolean openMoneyTimeLive = false;
    private LiveStartSettingParamBean mLiveStartSettingParamBean;

    private String liveRtcUid;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_show;
    }

    @Override
    public void initView() {
        super.initView();

        mRoot.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom > oldBottom && InputMethodUtils.isShowSoft(RtmpPushActivity.this)) {
                    changeEditStatus(false);
                }
            }
        });

        mIvAttention.setVisibility(View.GONE);
    }

    @Override
    public void initData() {
        super.initData();

        mUser = AppContext.getInstance().getLoginUser();
        mRoomNum = mUser.id;

        mLiveStartSettingParamBean = getIntent().getParcelableExtra("data");
        mWords = mLiveStartSettingParamBean.getWords();
        //推流地址
        rtmpPushAddress = mLiveStartSettingParamBean.getPush();
        //流名
        mStreamName = mLiveStartSettingParamBean.getStream();
        //是否vip
        is_vip = StringUtils.toInt(mLiveStartSettingParamBean.getIs_vip());

        initPushLiveParam();

        mTvLiveNick.setText(mUser.user_nicename);
        //收益
        mTvIncomeNum.setText(mLiveStartSettingParamBean.getVotestotal());
        //弹幕价格
        barrageFee = StringUtils.toInt(mLiveStartSettingParamBean.getBarrage_fee());
        //直播类型
        String type = mLiveStartSettingParamBean.getType();

        //3是付费直播
        if (StringUtils.toInt(type) == LIVE_TYPE_TIME) {
            openMoneyTimeLive = true;
        }

        //直播参数配置end
        mEmceeHead.setAvatarUrl(mUser.avatar);
        //连接聊天服务器
        initChatConnection();
        startAnimation(3);
        //startGetRankInfo(true);
    }

    //初始化连接聊天服务器
    private void initChatConnection() {
        //连接socket服务器
        try {
            mIMControl = new IMControl(new ChatListenUIRefresh(), this, mLiveStartSettingParamBean.getChatserver());
            //连接到socket服务端
            mIMControl.connectSocketServer(mUser, mStreamName, mUser.id);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.iv_live_open_dialog_menu, R.id.btn_live_sound, R.id.iv_live_emcee_head, R.id.tglbtn_danmu_setting, R.id.rl_live_info,
            R.id.btn_live_end_music, R.id.iv_live_music, R.id.iv_live_meiyan, R.id.iv_live_camera_control, R.id.camera_preview,
            R.id.iv_live_privatechat, R.id.iv_live_back, R.id.ll_yp_label, R.id.iv_live_chat, R.id.bt_send_chat})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //打开插件
            case R.id.iv_live_open_dialog_menu:

                showDialogMenu();
                break;
            //音效
            case R.id.btn_live_sound:
                showSoundEffectsDialog();
                break;
            //展示主播信息弹窗
            case R.id.iv_live_emcee_head:
                showUserInfoDialog(mUser);
                break;
            //展示主播信息弹窗
            case R.id.rl_live_info:
                showUserInfoDialog(mUser);
                break;
            //展示点歌菜单
            case R.id.iv_live_music:
                showSearchMusicDialog();
                break;
            //美颜
            case R.id.iv_live_meiyan:
                adjustBeauty();
                break;
            //设置
            case R.id.iv_live_camera_control:
                showSettingPopUp(v);
                break;
            //开启关闭弹幕
            case R.id.tglbtn_danmu_setting:
                openOrCloseDanMu();
                break;
            case R.id.camera_preview:
                changeEditStatus(false);
                break;
            //私信
            case R.id.iv_live_privatechat:
                showPrivateChat();
                break;
            case R.id.iv_live_back:
                onClickGoBack();
                break;
            //魅力值排行榜
            case R.id.ll_yp_label:
                showDedicateOrder();
                break;
            //聊天输入框
            case R.id.iv_live_chat:
                changeEditStatus(true);
                break;
            case R.id.bt_send_chat:
                if (mDanMuIsOpen) {
                    sendBarrage();
                } else {
                    sendChat();
                }
                break;
            case R.id.iv_live_exit:
                finish();
                break;
            case R.id.btn_live_end_music:
                stopMusic();
                break;

            default:
                break;
        }
    }

    //弹出选项菜单
    private void showDialogMenu() {

        LivePlugsDialogFragment dialogFragment = new LivePlugsDialogFragment();
        dialogFragment.show(getFragmentManager(), "LivePlugsDialogFragment");
    }

    //打开魅力值排行
    private void showDedicateOrder() {

        OrderWebViewActivity.startOrderWebView(RtmpPushActivity.this, mUser.id);
    }

    //当每个聊天被点击显示该用户详细信息弹窗
    @Override
    public void chatListItemClick(ChatBean chat) {

        if (chat.getType() != 13) {
            showUserInfoDialog(chat.mSimpleUserInfo);
        }
    }

    //显示搜索音乐弹窗
    private void showSearchMusicDialog() {
        SearchMusicDialogFragment musicFragment = new SearchMusicDialogFragment();

        musicFragment.show(getSupportFragmentManager(), "SearchMusicDialogFragment");
    }

    //音效调节菜单
    private void showSoundEffectsDialog() {
        MusicPlayerDialogFragment musicPlayerDialogFragment = new MusicPlayerDialogFragment();
        musicPlayerDialogFragment.show(getSupportFragmentManager(), "MusicPlayerDialogFragment");
    }

    //当主播选中了某一首歌,开始播放
    @Override
    public void onSelectMusic(Intent data) {
        startMusicStream(data);
    }


    //socket客户端事件监听处理
    private class ChatListenUIRefresh implements IMControlInterface {

        @Override
        public void onMessageListen(final SocketMsgUtils socketMsg, final int type, final ChatBean c) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (StringUtils.toInt(socketMsg) == Constant.MSG_CODE.SHOT_UP) {
                        AppContext.showToast("你已经被禁言", 0);
                        return;
                    }
                    addChatMessage(c);
                }
            });
        }

        @Override
        public void onShowTrack(final SocketMsgUtils socketMsg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
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
        public void onOpenGame(SocketMsgUtils socketMsg) {

        }

        //游戏结果
        @Override
        public void onGameStart(SocketMsgUtils socketMsg) {

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
        public void onJinhuaGameMessageListen(final SocketMsgUtils socketMsg) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });
        }

        @Override
        public void onRequestRtc(final SocketMsgUtils socketMsg) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (socketMsg.getParam("to_user_id", 0) != StringUtils.toInt(mUser.id)) {
                        return;
                    }

                    new AlertView("提示", socketMsg.getUname() + "请求连麦，是否同意?", "取消", new String[]{"拒绝", "同意"}, null, RtmpPushActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
                        @Override
                        public void onItemClick(Object o, int i) {
                            if (i == 0) {
                                clickReplyRtc(socketMsg.getUid(), 2);
                            } else {
                                clickReplyRtc(socketMsg.getUid(), 1);
                            }

                        }
                    }).setCancelable(true).show();

                }
            });
        }

        @Override
        public void onRequestReplyRtc(SocketMsgUtils socketMsg) {

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
        public void onSendRedPacket(SocketMsgUtils socketMsg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    mLiveRedNewDialog = LiveRedEnvelopeNewDialog.newInstance(socketMsg, mUser, mEmceeInfo);
//                    mLiveRedNewDialog.show(getSupportFragmentManager(), "LiveRedEnvelopeNewDialog");

                }
            });
        }

        @Override
        public void onSendGameGift(final SocketMsgUtils socketMsg) {

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

        @Override
        public void onOpenTimeLive(SocketMsgUtils msgUtils, ChatBean c) {

        }

        @Override
        public void onGameEnd(SocketMsgUtils socketMsg) {

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
                }
            });

        }

        //系统通知
        @Override
        public void onRoomClose(final int code) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (code == 1) {//后台关闭直播

                        videoPlayerEnd();
                        DialogHelp.getMessageDialog(RtmpPushActivity.this, "直播内容涉嫌违规", new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(android.content.DialogInterface dialog, int which) {

                            }
                        }).show();

                    }
                }
            });

        }

        //送礼物
        @Override
        public void onShowSendGift(final SendGiftBean giftInfo, final ChatBean chatBean) {
            //送礼物展示
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    addGiftMessage(giftInfo, chatBean);
                }
            });

        }

        //特权操作
        @Override
        public void onPrivilegeAction(final SocketMsgUtils socketMsg, final ChatBean c) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
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
                    showLit();
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

                    if (!NetUtils.hasNetwork(RtmpPushActivity.this)) {
                        mIMControl.close();
                    }

                }
            });
        }

        @Override
        public void onKickRoom(final SocketMsgUtils socketMsg, final ChatBean c) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addChatMessage(c);
                }
            });

        }

        @Override
        public void onShutUp(final SocketMsgUtils socketMsg, final ChatBean c) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    addChatMessage(c);
                }
            });

        }
    }

    //点击回复连麦
    private void clickReplyRtc(final String toUserId, final int action) {

        showWaitDialog("正在加载...", false);
        PhoneLiveApi.requestReplyRtc(mUser.id, mUser.token, toUserId, action, new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {
                hideWaitDialog();
            }

            @Override
            public void onResponse(String response, int id) {
                hideWaitDialog();
                String res = ApiUtils.checkIsSuccess2(response);
                if (res != null) {
                    mIMControl.doSendRequestReplyRtc(mUser, toUserId, action);
                    if (action == 1) {
                        startRtc();
                        liveRtcUid = toUserId;
                    }
                }
            }
        });
    }

    //结束连麦
    private void clickEndRtc(final String rtcUserId) {

        showWaitDialog("正在加载中...", false);
        PhoneLiveApi.requestEmceeEndRtc(mUser.id, mUser.token, liveRtcUid, new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {
                hideWaitDialog();
            }

            @Override
            public void onResponse(String response, int id) {
                hideWaitDialog();
                String res = ApiUtils.checkIsSuccess2(response);
                if (res != null) {
                    stopRtc();
                    mIMControl.doSendRequestEndRtc(mUser, rtcUserId);
                }
            }
        });
    }

    //开始连麦
    private void startRtc() {
        isLiveRtc = true;
//        mStreamer.setRTCSubScreenRect(0.65f, 0.f, 0.3f, 0.35f, KSYAgoraStreamer
//                .SCALING_MODE_CENTER_CROP);
        mStreamer.setRTCMainScreen(KSYAgoraStreamer.RTC_MAIN_SCREEN_CAMERA);
        String tempChannel = mRoomNum;
        mStreamer.startRTC(tempChannel);
    }


    //结束连麦
    private void stopRtc() {

        isLiveRtc = false;
        if (mStreamer != null) {
            mStreamer.stopRTC();
        }
    }


    //播放音乐
    private void startMusicStream(Intent data) {

        //停止音乐
        mStreamer.stopBgm();

        mViewShowLiveMusicLrc.setVisibility(View.VISIBLE);

        //获取音乐路径
        String musicPath = data.getStringExtra("filepath");
        //获取歌词字符串
        String lrcStr = LiveUtils.getFromFile(musicPath.substring(0, musicPath.length() - 3) + "lrc");
        //mStreamer.getAudioPlayerCapture().getMediaPlayer().setVolume(1,1);
        mStreamer.startBgm(musicPath, true);
        //插入耳机
        //mStreamer.setHeadsetPlugged(true)
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(musicPath);
            mPlayer.setLooping(true);
            mPlayer.setVolume(0, 0);
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                public void onPrepared(MediaPlayer mp) {

                    mp.start();
                    if (mTimer == null) {
                        mTimer = new Timer();

                        mTimer.scheduleAtFixedRate(new TimerTask() {
                            long beginTime = -1;

                            @Override
                            public void run() {
                                if (beginTime == -1) {
                                    beginTime = System.currentTimeMillis();
                                }

                                if (null != mPlayer) {
                                    final long timePassed = mPlayer.getCurrentPosition();
                                    RtmpPushActivity.this.runOnUiThread(new Runnable() {

                                        public void run() {
                                            mLrcView.seekLrcToTime(timePassed);
                                        }
                                    });
                                }

                            }
                        }, 0, mPlayTimerDuration);
                    }
                }
            });
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                public void onCompletion(MediaPlayer mp) {
                    stopLrcPlay();
                }
            });
            mPlayer.prepare();
            mPlayer.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ILrcBuilder builder = new DefaultLrcBuilder();
        List<LrcRow> rows = builder.getLrcRows(lrcStr);

        //设置歌词
        mLrcView.setLrc(rows);
    }

    //停止歌词滚动
    public void stopLrcPlay() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    //停止播放音乐
    private void stopMusic() {
        if (mPlayer != null && null != mStreamer) {
            mStreamer.stopBgm();
            mPlayer.stop();
            mViewShowLiveMusicLrc.setVisibility(View.GONE);
            if (mTimer != null) {
                mTimer.cancel();
            }
        }

    }

    //调整美颜
    private void adjustBeauty() {

        tiPanelLayout.clickMeiyan();
//
//
//        mStreamer.setFrontCameraMirror(true);
//        mStreamer.getImgTexFilterMgt().setFilter(mStreamer.getGLRender(),
//                ImgTexFilterMgt.KSY_FILTER_BEAUTY_PRO);
//        List<ImgFilterBase> filters = mStreamer.getImgTexFilterMgt().getFilter();
//        if (filters != null && !filters.isEmpty()) {
//            final ImgFilterBase filter = filters.get(0);
//            SeekBar.OnSeekBarChangeListener seekBarChangeListener =
//                    new SeekBar.OnSeekBarChangeListener() {
//                        @Override
//                        public void onProgressChanged(SeekBar seekBar, int progress,
//                                                      boolean fromUser) {
//                            if (!fromUser) {
//                                return;
//                            }
//                            float val = progress / 100.f;
//                            if (seekBar == mGrindSeekBar) {
//                                filter.setGrindRatio(val);
//                            } else if (seekBar == mWhitenSeekBar) {
//                                filter.setWhitenRatio(val);
//                            } else if (seekBar == mRuddySeekBar) {
//                                if (filter instanceof ImgBeautyProFilter) {
//                                    val = progress / 50.f - 1.0f;
//                                }
//                                filter.setRuddyRatio(val);
//                            }
//                        }
//
//                        @Override
//                        public void onStartTrackingTouch(SeekBar seekBar) {
//                        }
//
//                        @Override
//                        public void onStopTrackingTouch(SeekBar seekBar) {
//                        }
//                    };
//            BottomView mManageMenu = new BottomView(this, R.style.BottomViewTheme_Transparent,R.layout.view_adjust_beauty);
//
//
//            mRuddySeekBar = (AppCompatSeekBar) mManageMenu.getView().findViewById(R.id.ruddy_seek_bar);
//            mGrindSeekBar = (AppCompatSeekBar) mManageMenu.getView().findViewById(R.id.grind_seek_bar);
//            mWhitenSeekBar = (AppCompatSeekBar) mManageMenu.getView().findViewById(R.id.whiten_seek_bar);
//            mManageMenu.setAnimation(R.style.BottomToTopAnim);
//            mManageMenu.showBottomView(true);
//
//
//            mGrindSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
//            mWhitenSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
//            mRuddySeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
//
//            mGrindSeekBar.setProgress((int)(filter.getGrindRatio() * 100));
//            mWhitenSeekBar.setProgress((int)(filter.getWhitenRatio() * 100));
//            int ruddyVal = (int)(filter.getRuddyRatio() * 100);
//            if (filter instanceof ImgBeautyProFilter) {
//                ruddyVal = (int)(filter.getRuddyRatio() * 50 + 50);
//            }
//            mRuddySeekBar.setProgress(ruddyVal);
//        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event.CommonEvent event) {

        if (event.action == 1) {

            EventBus.getDefault().unregister(this);
            if (!NetUtils.hasNetwork(RtmpPushActivity.this)) {

                videoPlayerEnd();
                new AlertDialog.Builder(RtmpPushActivity.this)
                        .setTitle("提示")
                        .setMessage("网络断开连接,请检查网络后重新开始直播")
                        .setNegativeButton("确定", new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(android.content.DialogInterface dialogInterface, int i) {

                            }
                        })
                        .create()
                        .show();
            }
        } else if (event.action == 2) {

        } else if (event.action == 4) {

        } else if (event.action == 3) {
            //计时房间
            clickOpenTimeLive();
        } else if (event.action == 5) {
            //连麦请求列表
            clickLiveRtcList();
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLiveRtcEvent(Event.LiveRtcEvent event) {

        if (event.action == 1) {
            //关闭连麦
            clickEndRtc(event.rtcUserId);
        }
    }

    //连麦列表
    private void clickLiveRtcList() {

        LiveRtcListDialogFragment dialogFragment = new LiveRtcListDialogFragment();
        dialogFragment.setPushActivity(this);
        dialogFragment.show(getSupportFragmentManager(), "LiveRtcListDialogFragment");
    }

    //点击计时房间
    private void clickOpenTimeLive() {

        if (openMoneyTimeLive) {
            AppContext.showToast("计时房间已打开");
            return;
        }

        LiveCommon.showInputContentDialog(RtmpPushActivity.this, "设置扣费金额", new com.daimeng.livee.interf.DialogInterface() {
            @Override
            public void cancelDialog(View v, Dialog d) {

                d.dismiss();
            }

            @Override
            public void determineDialog(View v, final Dialog d) {

                String coin = ((EditText) d.findViewById(R.id.et_input)).getText().toString();

                if (StringUtils.toInt(coin) > 0) {
                    //修改房间类型为扣费房间
                    PhoneLiveApi.requestSetRoomType(mUser.id, mUser.token, mStreamName, coin, new StringCallback() {

                        @Override
                        public void onError(Call call, Exception e, int id) {
                            d.dismiss();
                            AppContext.showToast("修改房间状态失败");
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            d.dismiss();
                            JSONArray res = ApiUtils.checkIsSuccess(response);

                            if (res != null) {
                                AppContext.showToast("房间已修改为付费计时房间");
                                try {
                                    openMoneyTimeLive = true;
                                    JSONObject data = res.getJSONObject(0);
                                    if (mIMControl != null) {
                                        mIMControl.doSendOpenTimeLive(data.getString("time"), data.getString("money"));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    });
                }
            }
        });
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

    //返回键监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            onClickGoBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10: {
                // 判断权限请求是否通过
                if ((grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) && (grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    showSoundEffectsDialog();
                } else if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    AppContext.showToast("缺少权限,请到设置中修改", 0);
                } else if (grantResults.length > 0 && grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    AppContext.showToast("缺少权限,请到设置中修改", 0);
                }
                break;

            }

            default:
                break;
        }
    }

    //主播点击退出
    private void onClickGoBack() {

        LiveCommon.showConfirmDialog(RtmpPushActivity.this, "提示", getString(R.string.iscloselive), new com.daimeng.livee.interf.DialogInterface() {
            @Override
            public void cancelDialog(View v, Dialog d) {

                d.dismiss();
            }

            @Override
            public void determineDialog(View v, Dialog d) {
                videoPlayerEnd();
            }
        });
    }

    //关闭直播
    @Override
    protected void videoPlayerEnd() {
        IS_START_LIVE = false;
        //停止播放音乐
        stopMusic();
        //停止直播

        mStreamer.stopCameraPreview();
        mStreamer.stopStream();
        mStreamer.stopRTC();

        showWaitDialog("正在关闭直播...", false);
        //请求接口改变直播状态
        PhoneLiveApi.closeLive(mUser.id, mUser.token, mStreamName, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                //showToast3("关闭直播失败" ,0);
                hideWaitDialog();
                showLiveEndDialog(mUser.id, mStreamName);
            }

            @Override
            public void onResponse(String response, int id) {
                hideWaitDialog();
                showLiveEndDialog(mUser.id, mStreamName);
            }
        });

        mGiftShowQueue.clear();
        mListChats.clear();

        if (mGiftView != null) {
            mLiveContent.removeView(mGiftView);
        }
        mShowGiftAnimator.removeAllViews();

        mIMControl.closeLive();

        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }

        //关闭弹幕
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mIMControl.close();
        mIMControl = null;
        if (mStreamer != null) {
            mStreamer.release();

        }
        OkHttpUtils.getInstance().cancelTag("requestSetRoomType");

    }


    //开始直播倒数计时
    private void startAnimation(final int num) {
        final TextView tvNum = new TextView(this);
        tvNum.setTextColor(getResources().getColor(R.color.white));
        tvNum.setText(String.valueOf(num));
        tvNum.setTextSize(30);
        mLiveContent.addView(tvNum);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tvNum.getLayoutParams();
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        tvNum.setLayoutParams(params);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(tvNum, "scaleX", 5f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(tvNum, "scaleY", 5f, 1f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mLiveContent == null) return;
                mLiveContent.removeView(tvNum);
                if (num == 1) {
                    return;
                }
                startAnimation(num == 3 ? 2 : 1);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animatorSet.setDuration(500);
        animatorSet.start();

    }

    //显示设置列表
    private void showSettingPopUp(View v) {

        View popView = getLayoutInflater().inflate(R.layout.pop_view_camera_control, null);
        LinearLayout llLiveCameraControl = (LinearLayout) popView.findViewById(R.id.ll_live_camera_control);
        llLiveCameraControl.measure(0, 0);
        int height = llLiveCameraControl.getMeasuredHeight();
        popView.findViewById(R.id.iv_live_flashing_light).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flashingLightOn = !flashingLightOn;
                mStreamer.toggleTorch(flashingLightOn);
            }
        });
        popView.findViewById(R.id.iv_live_switch_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStreamer.switchCamera();
            }
        });
        popView.findViewById(R.id.iv_live_shar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareUtils.showSharePopWindow(RtmpPushActivity.this, mIvCameraControl);
            }
        });

        PopupWindow popupWindow = new PopupWindow(popView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0], location[1] - height);
    }

    public void share(View v) {
        ShareUtils.share(this, v.getId(), mUser);
    }

    public static void rtmpPushActivity(Context context, LiveStartSettingParamBean data) {
        Intent intent = new Intent(context, RtmpPushActivity.class);
        intent.putExtra("data", data);
        context.startActivity(intent);
    }
}
