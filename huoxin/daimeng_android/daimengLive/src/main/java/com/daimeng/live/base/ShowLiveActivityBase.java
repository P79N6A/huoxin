package com.daimeng.live.base;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.anbetter.danmuku.DanMuView;
import com.bese.ShuZuFangZhi;
import com.bese.myAnimation1;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.daimeng.live.AppContext;
import com.daimeng.live.Constant;
import com.daimeng.live.R;
import com.daimeng.live.adapter.ChatListAdapter;
import com.daimeng.live.adapter.LiveDayRankAdapter;
import com.daimeng.live.adapter.UserListAdapter;
import com.daimeng.live.api.remote.ApiUtils;
import com.daimeng.live.api.remote.PhoneLiveApi;
import com.daimeng.live.bean.CarBean;
import com.daimeng.live.bean.ChatBean;
import com.daimeng.live.bean.ConfigBean;
import com.daimeng.live.bean.DanmakuEntity;
import com.daimeng.live.bean.GiftDanmuEntity;
import com.daimeng.live.bean.LiveCarInfoBean;
import com.daimeng.live.bean.RankBean;
import com.daimeng.live.bean.SendGiftBean;
import com.daimeng.live.bean.SimpleUserInfo;
import com.daimeng.live.bean.UserBean;
import com.daimeng.live.event.Event;
import com.daimeng.live.fragment.LiveEmceeEndFragmentDialog;
import com.daimeng.live.fragment.LiveEndFragmentDialog;
import com.daimeng.live.fragment.UserInfoDialogFragment;
import com.daimeng.live.im.IMControl;
import com.daimeng.live.im.PhoneLivePrivateChat;
import com.daimeng.live.interf.DialogInterface;
import com.daimeng.live.utils.DanMuHelper;
import com.daimeng.live.utils.DpOrSp2PxUtil;
import com.daimeng.live.utils.InputMethodUtils;
import com.daimeng.live.utils.LiveUtils;
import com.daimeng.live.utils.SimpleUtils;
import com.daimeng.live.utils.SocketMsgUtils;
import com.daimeng.live.utils.StringUtils;
import com.daimeng.live.utils.TDevice;
import com.daimeng.live.utils.TLog;
import com.daimeng.live.viewpagerfragment.PrivateChatCorePagerDialogFragment;
import com.daimeng.live.widget.AvatarView;
import com.daimeng.live.widget.BlackButton;
import com.daimeng.live.widget.BlackEditText;
import com.daimeng.live.widget.CircleImageView;
import com.daimeng.live.widget.FrameAnimationView;
import com.daimeng.live.widget.MarqueeTextView;
import com.daimeng.live.widget.SGTextView;
import com.daimeng.live.widget.SpaceRecycleView;
import com.google.gson.Gson;
import com.king.view.flutteringlayout.FlutteringLayout;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.PropertyValuesHolder;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import okhttp3.Call;
import pl.droidsonroids.gif.GifImageView;

// 直播间基类
public class ShowLiveActivityBase extends BaseActivity {


    public static final int LIVE_TYPE_ORDINARY = 0;
    public static final int LIVE_TYPE_TIME = 3;
    public static final int LIVE_TYPE_PAY = 2;
    public static final int LIVE_TYPE_PWD = 1;
    public myAnimation1 manimation;
    public static long lastClickTime = 0L;
        @BindView(R.id.dong_hua)
    ImageView dong_hua;
    @BindView(R.id.ll_live_game_content)
    protected LinearLayout mRlGameContent;

    @BindView(R.id.rl_live_root)
    protected RelativeLayout mRoot;

    @BindView(R.id.heart_layout)
    protected FlutteringLayout mHeartLayout;

    //连送礼物动画显示区
    @BindView(R.id.ll_show_gift_animator)
    protected LinearLayout mShowGiftAnimator;

    @BindView(R.id.view_live_content)
    protected RelativeLayout mLiveContent;

    //观众数量
    @BindView(R.id.tv_live_nick)
    protected TextView mTvLiveNick;

    @BindView(R.id.tv_income_num)
    protected TextView mTvIncomeNum;

    //聊天listView
    @BindView(R.id.lv_live_room)
    protected ListView mLvChatList;

    //点击chat按钮
    @BindView(R.id.iv_live_chat)
    protected TextView mLiveChat;

    //左上角主播
    @BindView(R.id.iv_live_emcee_head)
    protected AvatarView mEmceeHead;

    //底部menu
    @BindView(R.id.ll_bottom_menu)
    protected RelativeLayout mButtonMenu;

    @BindView(R.id.fl_bottom_menu)
    protected FrameLayout mButtonMenuFrame;

    //chatInput
    @BindView(R.id.ll_live_chat_edit)
    protected LinearLayout mLiveChatEdit;
    @BindView(R.id.l_gift)
    protected LinearLayout l_gift;
    @BindView(R.id.ll_yp_label)
    protected LinearLayout mLiveLade;

    @BindView(R.id.et_live_chat_input)
    protected BlackEditText mChatInput;

    @BindView(R.id.tv_live_number)
    protected TextView mTvLiveNum;

    //观众列别listView
    @BindView(R.id.hl_room_user_list)
    protected RecyclerView mRvUserList;

    @BindView(R.id.iv_live_new_message)
    protected ImageView mIvNewPrivateChat;

    @BindView(R.id.tglbtn_danmu_setting)
    protected BlackButton mBtnDanMu;

    @BindView(R.id.tv_live_join_room_animation)
    TextView mTvJoinRoomAnimation;

    @BindView(R.id.rl_top)
    RelativeLayout rl_top;

    @BindView(R.id.ll_gift)
    LinearLayout ll_gift;

    @BindView(R.id.giftDanView)
    protected DanMuView giftDanView;

    @BindView(R.id.giftDani)
    protected MarqueeTextView giftMidDan;

    @BindView(R.id.danmakuView)
    protected DanMuView mDanmakuView;

    @BindView(R.id.frame_animation_view)
    protected FrameAnimationView frame_animation_view;

    @BindView(R.id.rv_content_order_list)
    RecyclerView rv_content_order_list;

    @BindView(R.id.tv_live_tick_name)
    TextView tv_live_tick_name;

    protected Gson mGson = new Gson();

    //当前正在显示的两个动画
    private Map<Integer, View> mGiftShowNow = new HashMap<>();
    //礼物消息队列
    protected Map<Integer, SendGiftBean> mGiftShowQueue = new HashMap();
    //进入房间动画列队
    private List<UserBean> mJoinRoomAnimationQueue = new ArrayList<>();
    //发送礼物消息队列
    private List<GiftDanmuEntity> mJoinGiftQueue = new ArrayList<>();
    //座驾队列
    protected List<LiveCarInfoBean> mCarShowQueue = new ArrayList<>();

    protected int mShowGiftFirstUid = 0, mShowGiftSecondUid = 0;
    //socket服务器连接状态
    public boolean mConnectionState = false;

    //聊天adapter
    protected ChatListAdapter mChatListAdapter;
    //用户列表adapter
    protected UserListAdapter mUserListAdapter;
    //聊天list
    protected List<ChatBean> mListChats = new ArrayList<>();
    //用户列表list
    protected List<SimpleUserInfo> mUserList = new ArrayList<>();

    //socket
    public IMControl mIMControl;

    protected UserBean mUser;
    protected Handler mHandler;
    //屏幕宽度
    protected int mScreenWidth, mScreenHeight;
    //房间号码
    public String mRoomNum, mStreamName;
    //弹幕开启状态
    protected boolean mDanMuIsOpen = false;
    protected BroadcastReceiver broadCastReceiver;
    //礼物
    protected View mGiftView;
    //弹幕价格
    protected int barrageFee;

    protected List<CarBean> mListCar = new ArrayList<>();
    protected int is_vip = 0;
    protected ConfigBean mPConfigBean;

    protected boolean isLiveRtc = false;

    protected List<String> mWords = new ArrayList<>();
    private List<RankBean> orderListData = new ArrayList<>();
    private LiveDayRankAdapter orderAdapter;

    private DanMuHelper danMuHelper;
    private DanMuHelper giftBigRunway;
    private DanMuHelper danMuHelper2;

    @Override
    public void initData() {
        //屏幕常量
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mHandler = new Handler();
        //初始化直播播放器参数配置

        List<CarBean> carList = LiveUtils.getCarList(this);
        if (carList != null) {
            mListCar.addAll(carList);
        }
        registerPrivateBroadcast();

    }

    //注册私信广播监听
    protected void registerPrivateBroadcast() {

        IntentFilter cmdFilter = new IntentFilter("com.daimeng.live");
        if (broadCastReceiver == null) {
            broadCastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    // TODO Auto-generated method stub
                    mIvNewPrivateChat.setVisibility(View.VISIBLE);
                }
            };
        }
        registerReceiver(broadCastReceiver, cmdFilter);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void initView() {

        mChatListAdapter = new ChatListAdapter(this);
        mChatListAdapter.setChats(mListChats);
        mLvChatList.setAdapter(mChatListAdapter);

        final LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(OrientationHelper.HORIZONTAL);
        mRvUserList.setLayoutManager(manager);
        //设置每个item间距
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.space_5);
        mRvUserList.addItemDecoration(new SpaceRecycleView(spacingInPixels));
        mRvUserList.setAdapter(mUserListAdapter = new UserListAdapter(mUserList));
        mScreenWidth = (int) TDevice.getScreenWidth();
        mScreenHeight = (int) TDevice.getScreenHeight();

        mPConfigBean = LiveUtils.getConfigBean(this);
        tv_live_tick_name.setText(mPConfigBean.name_votes);

        mTvJoinRoomAnimation.setX(mScreenWidth);

        //聊天listView点击事件注册
        mLvChatList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    changeEditStatus(false);
                }
                return false;
            }
        });


        //用户列表点击事件
        mUserListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //展示用户详细信息弹窗
                showUserInfoDialog(mUserList.get(position));
            }
        });


        //弹幕控制器和视图关联
        mDanmakuView.prepare();
        danMuHelper = new DanMuHelper(this);
        danMuHelper.add(mDanmakuView);

        //大礼物弹幕
        giftDanView.prepare();
        giftDanView.setOnDanMuExistListener(new DanMuView.OnDetectHasCanTouchedDanMusListener() {

            @Override
            public void hasNoCanTouchedDanMus(boolean hasDanMus) {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                if (hasDanMus) {
                    layoutParams.setMargins(0, DpOrSp2PxUtil.dp2pxConvertInt(AppContext.getInstance(), 40), 0, 0);
                    //  ll_gift.setBackgroundColor(getResources().getColor(R.color.gift_danmu_bg));
                    ll_gift.setBackgroundResource(R.drawable.gift_brun);

                } else {
                    layoutParams.setMargins(0, DpOrSp2PxUtil.dp2pxConvertInt(AppContext.getInstance(), 10), 0, 0);
                    ll_gift.setBackgroundColor(getResources().getColor(R.color.transparent));
                }
                rl_top.setLayoutParams(layoutParams);
            }
        });
        giftBigRunway = new DanMuHelper(this);
        giftBigRunway.add(giftDanView);
        // 小礼物弹幕
//        giftMidDan.prepare();
//        danMuHelper2 = new DanMuHelper(this);
//        danMuHelper2.add(giftMidDan);

        mLvChatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                chatListItemClick(mListChats.get(position));
            }
        });

        //隐藏开始游戏直播
        //mIvStartGame.setVisibility(View.GONE);

        LinearLayoutManager orderManage = new LinearLayoutManager(this);
        orderManage.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_content_order_list.setLayoutManager(orderManage);
        rv_content_order_list.setAdapter(orderAdapter = new LiveDayRankAdapter(orderListData));
        orderAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                showUserInfoDialog(LiveUtils.getSimleUserInfo(orderListData.get(position)));
            }
        });

        frame_animation_view.start();


        addSystemMessage(mPConfigBean.live_system_message);
    }


    //关闭或者开启弹幕功能
    protected void openOrCloseDanMu() {

        mDanMuIsOpen = !mDanMuIsOpen;
        if (mDanMuIsOpen) {
            if (mChatInput.getText().toString().equals("")) {
                mChatInput.setHint("弹幕，" + barrageFee + LiveUtils.getConfigBean(AppContext.getInstance()).name_coin + "/条");
            }
        } else {
            mChatInput.setHint("");
        }
        mBtnDanMu.setBackgroundResource(mDanMuIsOpen ? R.drawable.tuanmubutton1 : R.drawable.tanmubutton);

    }

    //发送聊天
    protected void sendChat() {
        String sendMsg = mChatInput.getText().toString();
        sendMsg = sendMsg.trim();

        for (String word : mWords) {

            if (sendMsg.contains(word)) {

                sendMsg = sendMsg.replace(word, "***");
            }
        }
        if (mConnectionState && !TextUtils.isEmpty(sendMsg)) {
            mChatInput.setText("");
            mIMControl.doSendMsg(sendMsg, mUser, is_vip, 0, 0);
        }
    }

    //进入显示礼物队列信息初始化
    protected View initShowEvenSentSendGift(SendGiftBean mSendGiftInfo) {

        View view = getLayoutInflater().inflate(R.layout.item_show_gift_animator, null);
        if (mShowGiftFirstUid == 0) {
            mShowGiftFirstUid = mSendGiftInfo.getUid();
        } else {
            mShowGiftSecondUid = mSendGiftInfo.getUid();
        }
        mGiftShowNow.put(mSendGiftInfo.getUid(), view);
        return view;
    }


    //定时检测当前显示礼物是否超时过期
    protected boolean timingDelGiftAnimation(int index) {

        int id = index == 1 ? mShowGiftFirstUid : mShowGiftSecondUid;

        SendGiftBean mSendGiftInfo = mGiftShowQueue.get(id);

        if (mSendGiftInfo != null) {

            long time = System.currentTimeMillis() - mSendGiftInfo.getSendTime();
            if ((time > 4000) && (mShowGiftAnimator != null)) {
                //超时 从礼物队列和显示队列中移除
                mShowGiftAnimator.removeView(mGiftShowNow.get(id));

                mGiftShowQueue.remove(id);

                mGiftShowNow.remove(id);
                if (index == 1) {
                    mShowGiftFirstUid = 0;
                } else {
                    mShowGiftSecondUid = 0;
                }
                //从礼物队列中获取一条新的礼物信息进行显示
                if (mGiftShowQueue.size() != 0) {

                    Iterator iterator = mGiftShowQueue.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry entry = (Map.Entry) iterator.next();
                        SendGiftBean sendGift = (SendGiftBean) entry.getValue();

                        if (mShowGiftFirstUid != sendGift.getUid() && mShowGiftSecondUid != sendGift.getUid()) {//判断队列中的第一个礼物是否已经正在显示
                            showEvenSentGiftAnimation(initShowEvenSentSendGift(sendGift), sendGift);
                            break;
                        }
                    }
                }
                return false;
            } else {
                return true;
            }
        }
        return true;
    }


    //点亮
    protected void showLit() {
        mHeartLayout.addHeart();
    }

    protected void switchPlayAnimation(SendGiftBean mSendGiftBean) {
        //普通连送礼物
        showOrdinaryGiftInit(mSendGiftBean);
    }


    //当用户状态改变
    protected void onUserStatusChange(SocketMsgUtils socketMsgUtils, UserBean user, boolean state) {
        //设置在线人数
        mTvLiveNum.setText(String.format(Locale.CANADA, "%d人观看", IMControl.LIVE_USER_NUMS));

        for (int i = 0; i < mUserList.size(); i++) {

            if (user.id.equals(mUserList.get(i).id)) {
                mUserList.remove(i);
                break;
            }
        }

        if (state && !mUserList.contains(user)) {//用户上线
            //判断该用户是否存在列表中
            mUserList.add(user);
            TLog.log("加入" + user.id);
            //user.shurl = "https://github.com/yyued/SVGA-Samples/blob/master/kingset.svga?raw=true";
            //判断shurl是否显示守护动画（）
            if (user.id.equals(mUser.id)) {
                mIMControl.doSendMsg("来了", mUser, is_vip, 0, 0);
            }

            if (null != user.shurl && !user.shurl.equals("")) {
                SendGiftBean sendGiftBean = new SendGiftBean();
                sendGiftBean.setAvatar(user.avatar);
                sendGiftBean.setNicename(user.user_nicename);
                sendGiftBean.setSvga_url(user.shurl);
                sendGiftBean.setShname(user.shname);
                sendGiftBean.setGiftname("守护");
                frame_animation_view.addGift(sendGiftBean);
            }


            if (StringUtils.toInt(user.level) >= StringUtils.toInt(LiveUtils.getConfigBean(AppContext.getInstance()).enter_tip_level)) {
                joinRoomAnimation(user);
            }

            //座驾
            for (CarBean car : mListCar) {
                try {
                    if (car.getId().equals(socketMsgUtils.getCtObject().getString("car_id"))) {
                        LiveCarInfoBean carBean = new LiveCarInfoBean();
                        carBean.car_bean = car;
                        carBean.user_name = user.user_nicename;
                        carBean.avatar = user.avatar;

                        joinRoomCarAnimation(carBean);
                        break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } else {

            TLog.log("离开" + user.id);
        }


        //列表重新排序
        LiveUtils.sortUserList(mUserList);
        mUserListAdapter.notifyDataSetChanged();
    }

    private void joinRoomCarAnimation(LiveCarInfoBean car) {

        mCarShowQueue.add(car);

        if (mCarShowQueue.size() == 1) {
            startCarAnimation();
        }
    }

    //座驾动画
    private void startCarAnimation() {
        LiveCarInfoBean car = mCarShowQueue.get(0);
        final View carView = inflateView(R.layout.view_join_room_car);
        CircleImageView avatar = (CircleImageView) carView.findViewById(R.id.iv_avatar);
        TextView name = (TextView) carView.findViewById(R.id.tv_name);
        TextView tv_car = (TextView) carView.findViewById(R.id.tv_car);
        GifImageView car_img = (GifImageView) carView.findViewById(R.id.iv_car);
        name.setText(car.user_name);
        tv_car.setText("乘坐【" + car.car_bean.getName() + "】驾到");
        SimpleUtils.loadImageForView(this, avatar, car.avatar, 0);
        SimpleUtils.loadImageForView(this, car_img, LiveUtils.getHttpUrl(car.car_bean.getThumb()), 0);

        mLiveContent.addView(carView);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) carView.getLayoutParams();
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        carView.setLayoutParams(params);


        ObjectAnimator a1 = ObjectAnimator.ofFloat(carView, "translationX", -mScreenWidth, 0);
        a1.setDuration(3000);
        a1.start();

        a1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {

                if (mHandler != null) {

                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            ObjectAnimator a1 = ObjectAnimator.ofFloat(carView, "translationX", mScreenWidth);
                            a1.setDuration(2000);
                            a1.start();

                            a1.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animator) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animator) {

                                    mLiveContent.removeView(carView);
                                    mCarShowQueue.remove(0);

                                    if (mCarShowQueue.size() != 0) {

                                        startCarAnimation();
                                    }

                                }

                                @Override
                                public void onAnimationCancel(Animator animator) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animator) {

                                }
                            });

                        }
                    }, 2000);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }
    public void DongHuaXiaoGuo(String tupian){
        Log.e("图片名字","++++++++++++"+tupian);
        if (tupian.equals("比心"))
        {
            if (!ShowLiveActivityBase.isFastClick(8000))
            {
                manimation= new myAnimation1(dong_hua, ShuZuFangZhi.xindongImages() ,ShuZuFangZhi.xindongDurations());
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        dong_hua.setVisibility(View.GONE);
                        /**
                         *要执行的操作
                         */
                    }
                }, 5500);//3秒后执行Runnable中的run方法
                dong_hua.setVisibility(View.VISIBLE);
            }
        }
        else if (tupian.equals("游轮"))
        {
            if (!ShowLiveActivityBase.isFastClick(8000)) {
                manimation = new myAnimation1(dong_hua, ShuZuFangZhi.xindongImages1(), ShuZuFangZhi.xindongDurations1());
//            dong_hua.setVisibility(View.GONE);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        dong_hua.setVisibility(View.GONE);
                        /**
                         *要执行的操作
                         */
                    }
                }, 7500);//3秒后执行Runnable中的run方法
                dong_hua.setVisibility(View.VISIBLE);
            }
        }else if (tupian.equals("魔法城堡"))
        {
            if (!ShowLiveActivityBase.isFastClick(13500)) {
                manimation = new myAnimation1(dong_hua, ShuZuFangZhi.xindongImages2(), ShuZuFangZhi.xindongDurations2());
//            dong_hua.setVisibility(View.GONE);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        dong_hua.setVisibility(View.GONE);
                        /**
                         *要执行的操作
                         */
                    }
                }, 13000);//3秒后执行Runnable中的run方法
                dong_hua.setVisibility(View.VISIBLE);
            }
        }
        else {
            dong_hua.setVisibility(View.GONE);
        }

    }
    /**
     * @param mShowGiftLayout 礼物显示View
     * @param gitInfo         赠送的礼物信息
     * @dw 连送
     * @author 魏鹏
     */
    protected void showEvenSentGiftAnimation(final View mShowGiftLayout, final SendGiftBean gitInfo) {
        final AvatarView mGiftIcon = (AvatarView) mShowGiftLayout.findViewById(R.id.av_gift_icon);
        ((AvatarView) mShowGiftLayout.findViewById(R.id.av_gift_uhead)).setAvatarUrl(gitInfo.getAvatar());
        ((TextView) mShowGiftLayout.findViewById(R.id.tv_gift_uname)).setText(gitInfo.getNicename());
        ((TextView) mShowGiftLayout.findViewById(R.id.tv_gift_gname)).setText(gitInfo.getGiftname());
        mGiftIcon.setAvatarUrl(LiveUtils.getHttpUrl(gitInfo.getGifticon()));

        if (mShowGiftAnimator != null) {
            mShowGiftAnimator.addView(mShowGiftLayout);//添加到动画区域显示效果
        }
        //动画开始平移
        ObjectAnimator oa1 = ObjectAnimator.ofFloat(mShowGiftLayout, "translationX", -340f, 0f);
        oa1.setDuration(300);
        oa1.start();
        oa1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                showGiftNumAnimation(mShowGiftLayout, gitInfo.getUid(), true);
                //礼物图片平移动画
                ObjectAnimator giftIconAnimator = ObjectAnimator.ofFloat(mGiftIcon, "translationX", -40f, TDevice.dpToPixel(190));
                giftIconAnimator.setDuration(500);
                giftIconAnimator.start();
                //获取当前礼物是正在显示的哪一个
                final int index = mShowGiftFirstUid == gitInfo.getUid() ? 1 : 2;
                if (mHandler != null) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (timingDelGiftAnimation(index)) {
                                if (mHandler != null) {
                                    mHandler.postDelayed(this, 1000);
                                }
                            } else {
                                mHandler.removeCallbacks(this);
                            }

                        }
                    }, 1000);
                }

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }


    /**
     * @param uid 送礼物用户id,需要根据id取出队列中的礼物信息进行赠送时间重置
     * @dw 礼物数量增加动画
     */
    protected void showGiftNumAnimation(View view, int uid, boolean isReset) {

        SGTextView num = (SGTextView) view.findViewById(R.id.tv_show_gift_num);
        num.setTextSize(TDevice.dpToPixel(10
        ));
        String color = getResources().getString(R.string.send_lian_gift_text_color);
        num.setStyle("#ffffff", color, color, 3, 10);
        num.setText("x" + String.valueOf(mGiftShowQueue.get(uid).getGiftcount()));

        RelativeLayout ll_gift_content = (RelativeLayout) view.findViewById(R.id.ll_gift_content);

        PropertyValuesHolder p1 = PropertyValuesHolder.ofFloat("scaleX", 1.2f, 0.2f, 1f);
        PropertyValuesHolder p2 = PropertyValuesHolder.ofFloat("scaleY", 1.2f, 0.2f, 1f);
        ObjectAnimator.ofPropertyValuesHolder(ll_gift_content, p1, p2).setDuration(500).start();
        if (isReset) {
            mGiftShowQueue.get(uid).setSendTime(System.currentTimeMillis());//重置发送时间
        }
    }

    /**
     * @param mSendGiftInfo 赠送礼物信息
     * @dw 连送礼物初始化
     */
    protected void showOrdinaryGiftInit(final SendGiftBean mSendGiftInfo) {
        //礼物动画View
        View mShowGiftLayout = mGiftShowNow.get(mSendGiftInfo.getUid());
        //设置当前礼物赠送时间
        mSendGiftInfo.setSendTime(System.currentTimeMillis());
        boolean isShow = false;//是否刚刚加入正在显示队列
        boolean isFirst = false;//是否是第一次赠送礼物
        //是否是第一次送礼物,为空表示礼物队列中没有查询到该用户的送礼纪录
        if (mGiftShowQueue.get(mSendGiftInfo.getUid()) == null) {
            mGiftShowQueue.put(mSendGiftInfo.getUid(), mSendGiftInfo);//加入礼物消息队列
            //将是否第一次送礼设为true
            isFirst = true;
        }

        if(mSendGiftInfo.getGiftcount() > 7777777){
            int count = mSendGiftInfo.getGiftcount() - 7777777;
            mSendGiftInfo.setGiftcount(count);
            mGiftShowQueue.get(mSendGiftInfo.getUid()).setGiftcount(count);
        }

        //是否是新的礼物类型,对比两次礼物的id是否一致
        boolean isNewGift = !(mSendGiftInfo.getGiftid() == mGiftShowQueue.get(mSendGiftInfo.getUid()).getGiftid());
        //当前的正在显示礼物队列不够两条(最多两条),并且当前送礼物用户不在list中
        if ((mGiftShowNow.size() < 2) && (mShowGiftLayout == null)) {
            //初始化显示礼物布局和信息
            mShowGiftLayout = initShowEvenSentSendGift(mSendGiftInfo);
            isShow = true;
        }
        /*
         * mShowGiftLayout不等于null表示在正在显示的礼物队列中查询到了该用户送礼物纪录
         * 将是否正在显示isShow设置为true
         * */
        if (mShowGiftLayout != null) {
            isShow = true;
        }
        /*
         * 如果是新礼物(表示礼物队列中存在送礼物纪录)
         * 存在就将最新礼物的icon和数量重置,并且覆盖older信息
         * */
        if (isNewGift && mShowGiftLayout != null) {
            ((AvatarView) mShowGiftLayout.findViewById(R.id.av_gift_icon)).setAvatarUrl(mSendGiftInfo.getGifticon());

            showGiftNumAnimation(mShowGiftLayout, mSendGiftInfo.getUid(), false);
            ((TextView) mShowGiftLayout.findViewById(R.id.tv_gift_gname)).setText(mSendGiftInfo.getGiftname());
            //新礼物覆盖之前older礼物信息
            mGiftShowQueue.put(mSendGiftInfo.getUid(), mSendGiftInfo);
        }
        /*
         * 判断是否是连送礼物并且不是第一次赠送并且不是新礼物
         * 不是第一次赠送并且不是新礼物才需要添加数量(否则数量和礼物信息需要重置),
         * */
        if (mSendGiftInfo.getEvensend().equals("y") && (!isFirst) && (!isNewGift)) {//判断当前礼物是否属于连送礼物
            //是连送礼物,将消息队列中的礼物赠送数量加1
            mGiftShowQueue.get(mSendGiftInfo.getUid()).setGiftcount(mGiftShowQueue.get(mSendGiftInfo.getUid()).getGiftcount() + 1);
        }
        //需要显示在屏幕上并且是第一次送礼物需要进行动画初始化
        if (isShow && isFirst) {

            TLog.log(">>>>>>>>>>>>>>>>>> 添加新礼物开始初始化");
            showEvenSentGiftAnimation(mShowGiftLayout, mSendGiftInfo);
        } else if (isShow && (!isNewGift)) {//存在显示队列并且不是新礼物进行数量加一动画
            showGiftNumAnimation(mShowGiftLayout, mSendGiftInfo.getUid(), true);
        }
    }

    /**
     * @param mSendGiftInfo 赠送礼物信息
     * @dw 赠送礼物进行初始化操作
     * 判断当前礼物是属于豪华礼物还是连送礼物,并且对魅力值进行累加
     */
    protected void showGiftInit(SendGiftBean mSendGiftInfo) {
        //票数更新
        if (null != mTvIncomeNum && null != mSendGiftInfo) {
            mTvIncomeNum.setText(String.valueOf(StringUtils.toInt(mTvIncomeNum.getText().toString()) + mSendGiftInfo.getTotalcoin()));
        }

        switchPlayAnimation(mSendGiftInfo);

    }

    public void chatListItemClick(ChatBean chat) {

    }

    //显示输入框
    protected void changeEditStatus(boolean status) {
        if (status) {
            mChatInput.setFocusable(true);
            mChatInput.setFocusableInTouchMode(true);
            mChatInput.requestFocus();
            InputMethodUtils.toggleSoftKeyboardState(this);
            mLiveChatEdit.setVisibility(View.VISIBLE);
            mButtonMenu.setVisibility(View.GONE);
        } else {
            if (mLiveChatEdit.getVisibility() != View.GONE && InputMethodUtils.isShowSoft(this)) {
                InputMethodUtils.closeSoftKeyboard(this);
                mButtonMenu.setVisibility(View.VISIBLE);
                mLiveChatEdit.setVisibility(View.GONE);
            }
        }

    }

    //添加一条聊天
    protected void addChatMessage(ChatBean c) {

        if (mListChats.size() > 30) mListChats.remove(0);
//        if(null!=c.getSimpleUserInfo()){
//            SimpleUserInfo simpleUserInfo = c.getSimpleUserInfo();
//            simpleUserInfo.sex = mUser.sex;
//            c.setSimpleUserInfo(simpleUserInfo);
//        }

        mListChats.add(c);
        mChatListAdapter.notifyDataSetChanged();

        if (mLvChatList != null) {
            mLvChatList.setSelection(mListChats.size() - 1);
        }
    }

    //添加礼物消息
    protected void addGiftMessage(final SendGiftBean giftInfo, final ChatBean chatBean) {

        if (!TextUtils.isEmpty(giftInfo.getSvga_url())) {
            frame_animation_view.addGift(giftInfo);
        }
        showGiftInit(giftInfo);
    }


    //添加弹幕
    protected void addPayDanmu(final ChatBean c) {

        DanmakuEntity danmakuEntity = new DanmakuEntity();
        danmakuEntity.setAvatar(c.getSimpleUserInfo().avatar);
        danmakuEntity.setName(c.getSimpleUserInfo().user_nicename);
        danmakuEntity.setText(c.getContent());
        danmakuEntity.setUserId(c.getSimpleUserInfo().id);
        danmakuEntity.setType(DanmakuEntity.DANMAKU_TYPE_USERCHAT);
        danMuHelper.addDanMu(danmakuEntity, true);
    }


    //礼物中间进入
    protected void startJoinGift() {

        final GiftDanmuEntity g = mJoinGiftQueue.get(0);

        //        String content = "      "+ g.getSender() + "送给" + g.getReceiver() + g.getGift() + g.getGiftImage();
        //String content = g.getSender() + "送给" + g.getReceiver() + g.getGift();
        String content = g.getBroadcast_msg();
        final SpannableStringBuilder name = new SpannableStringBuilder(content);

        ForegroundColorSpan foregroundColorSpanName1 = new ForegroundColorSpan(Color.parseColor("#00B2EE"));
        ForegroundColorSpan foregroundColorSpanName2 = new ForegroundColorSpan(Color.parseColor("#00B2EE"));
        name.setSpan(foregroundColorSpanName1, 0, g.getSender().length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);//设置字体颜色
        name.setSpan(foregroundColorSpanName2, g.getSender().length() + 2, g.getSender().length() + 2 + g.getReceiver().length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
//        UrlImageSpan imageSpan = new UrlImageSpan(AppContext.getInstance(), g.getGiftImage());
//        name.setSpan(imageSpan, content.length() - g.getGiftImage().length(), content.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);


        //两个动画间时间间隔一秒
        new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if (mJoinGiftQueue.size() != 0) {
                    l_gift.setVisibility(View.VISIBLE);
                    giftMidDan.setVisibility(View.VISIBLE);
                    giftMidDan.setText(name);
                    giftMidDan.setSingleLine(true);

                    giftMidDan.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Event.CloseRoomEvent event = new Event.CloseRoomEvent();
                            event.action = 1;
                            event.stream = g.getStream();
                            event.roomId = g.getShowid();
                            EventBus.getDefault().post(event);
                        }
                    });

                    //文字滚动前显示2秒
                    new CountDownTimer(2000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {

                            giftMidDan.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                            giftMidDan.setHorizontallyScrolling(true); //让文字可以水平滑动
                            giftMidDan.setMarqueeRepeatLimit(1);
                            giftDanView.setSelected(true);
                            //文字滚动前显示3秒
                            new CountDownTimer(3000, 1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {

                                }

                                @Override
                                public void onFinish() {
                                    mJoinGiftQueue.remove(0);
                                    giftMidDan.setVisibility(View.GONE);
                                    l_gift.setVisibility(View.GONE);
                                    giftMidDan.setEllipsize(TextUtils.TruncateAt.END);
                                    giftMidDan.setHorizontallyScrolling(false); //让文字可以水平滑动
                                    giftMidDan.setMarqueeRepeatLimit(1);
                                    giftDanView.setSelected(false);
                                    if (mJoinGiftQueue.size() != 0) {
                                        startJoinGift();
                                    }
                                }
                            }.start();
                        }

                    }.start();
                }
            }
        }.start();
    }

    //用户信息弹窗
    protected void showUserInfoDialog(SimpleUserInfo toUserInfo) {

        UserInfoDialogFragment u = new UserInfoDialogFragment();
        Bundle b = new Bundle();
        b.putParcelable(UserInfoDialogFragment.MY_USER_INFO, mUser);
        b.putParcelable(UserInfoDialogFragment.TO_USER_INFO, toUserInfo);
        b.putString(UserInfoDialogFragment.ROOM_NUM, mRoomNum);
        u.setArguments(b);
        u.show(getSupportFragmentManager(), "UserInfoDialogFragment");
        u.setCallback(new UserInfoDialogFragment.AtClickHelp() {
            @Override
            public void onClick(String name) {
                mChatInput.setText(getString(R.string.at) + name + mChatInput.getText());
                changeEditStatus(true);
            }
        });

    }

    /**
     * @dw 显示私信页面
     */
    protected void showPrivateChat() {
        //解除私信广播监听
        try {
            unregisterReceiver(broadCastReceiver);
        } catch (Exception e) {

        }
        //隐藏新消息标记
        mIvNewPrivateChat.setVisibility(View.GONE);
        PrivateChatCorePagerDialogFragment privateChatFragment = new PrivateChatCorePagerDialogFragment();

        privateChatFragment.show(getSupportFragmentManager(), "PrivateChatCorePagerDialogFragment");
        privateChatFragment.setDialogInterface(new DialogInterface() {
            @Override
            public void cancelDialog(View v, Dialog d) {
                //弹窗关闭继续监听新消息
                registerPrivateBroadcast();

                //获取私信未读数量

                if (PhoneLivePrivateChat.getUnreadMsgsCount() > 0) {
                    mIvNewPrivateChat.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void determineDialog(View v, Dialog d) {

            }
        });

    }

    //连接结果
    public void onConnectRes(boolean res) {
        if (res) {
            mConnectionState = true;

            //请求僵尸粉丝
            mIMControl.getZombieFans();
        }
    }

    //僵尸粉丝
    protected void addZombieFans(String zombies) {

        JSONArray fans = ApiUtils.checkIsSuccess(zombies);
        if (null != fans) {
            try {
                //设置在线用户数量
                JSONObject jsonInfoObj = fans.getJSONObject(0);
                JSONArray fansJsonArray = jsonInfoObj.getJSONArray("list");

                if (!(mUserList.size() >= 20) && fansJsonArray.length() > 0) {
                    for (int i = 0; i < fansJsonArray.length(); i++) {
                        UserBean u = mGson.fromJson(fansJsonArray.getString(i), UserBean.class);
                        mUserList.add(u);
                    }
                    LiveUtils.sortUserList(mUserList);
                    mUserListAdapter.notifyDataSetChanged();
                }
                //在线人数统计
                if (fansJsonArray.length() > 0) {
                    IMControl.LIVE_USER_NUMS = StringUtils.toInt(jsonInfoObj.getString("nums"), 0);
                    mTvLiveNum.setText(String.format(Locale.CANADA, "%d人观看", IMControl.LIVE_USER_NUMS));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //进场动画
    private void joinRoomAnimation(UserBean user) {

        mJoinRoomAnimationQueue.add(user);

        if (mJoinRoomAnimationQueue.size() == 1) {
            startJoinRoomAnimation();
        }
    }

    //小跑道礼物
    protected void joinGiftAnimation(GiftDanmuEntity g) {
        mJoinGiftQueue.add(g);
        if (mJoinGiftQueue.size() == 1) {
            startJoinGift();
        }
    }

    //大跑道礼物
    protected void addGiftBigDanMu(final GiftDanmuEntity g) {
        GiftDanmuEntity giftDanmuEntity = new GiftDanmuEntity();
        giftDanmuEntity.setShowid(g.getShowid());
        giftDanmuEntity.setStream(g.getStream());
        giftDanmuEntity.setSender(g.getSender());
        giftDanmuEntity.setReceiver(g.getReceiver());
        giftDanmuEntity.setGift(g.getGift());
        giftDanmuEntity.setGiftImage(g.getGiftImage());
        giftDanmuEntity.setBroadcast_msg(g.getBroadcast_msg());
        giftDanmuEntity.setBroadcast_type(g.getBroadcast_type());
        giftBigRunway.addBigGift(giftDanmuEntity, true);
    }


    //用户金光一闪进入动画
    private void startJoinRoomAnimation() {

        UserBean user = mJoinRoomAnimationQueue.get(0);

        SpannableStringBuilder name = new SpannableStringBuilder(user.user_nicename);
        //添加等级图文混合
//        Drawable levelDrawable = getResources().getDrawable(LiveUtils.getLevelRes(user.level));
//        levelDrawable.setBounds(0, 0, (int) TDevice.dpToPixel(35), (int) TDevice.dpToPixel(15));
//        VerticalImageSpan levelImage = new VerticalImageSpan(levelDrawable);
        name.setSpan(new ForegroundColorSpan(Color.parseColor("#00FAF6")), 0, name.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        name.setSpan(levelImage, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        name.append("进入房间");

        mTvJoinRoomAnimation.setText(name);
        ObjectAnimator animation = ObjectAnimator.ofFloat(mTvJoinRoomAnimation, "translationX", -mTvJoinRoomAnimation.getWidth(), 0);
        animation.setDuration(1500);
        animation.start();
        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {

                if (mHandler == null) return;

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        mTvJoinRoomAnimation.setX(mScreenWidth);
                        mJoinRoomAnimationQueue.remove(0);

                        if (mJoinRoomAnimationQueue.size() != 0) {

                            startJoinRoomAnimation();
                        }

                    }
                }, 1500);


            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }


    //直播结束弹窗遮罩
    protected void showLiveEndDialog(String uid, String stream) {

        if (!uid.equals(mRoomNum)) {
            LiveEndFragmentDialog liveEndFragmentDialog = new LiveEndFragmentDialog();
            Bundle bundle = new Bundle();
            bundle.putString("roomnum", mRoomNum);
            liveEndFragmentDialog.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(liveEndFragmentDialog, "liveEndFragmentDialog");
            transaction.commitAllowingStateLoss();
        } else {
            LiveEmceeEndFragmentDialog dialog = new LiveEmceeEndFragmentDialog();
            Bundle bundle = new Bundle();
            bundle.putString("stream", stream);
            dialog.setArguments(bundle);
            dialog.show(getSupportFragmentManager(), "LiveEmceeEndFragmentDialog");
        }

    }

    //弹幕状态控制
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IMControl.LIVE_USER_NUMS = 0;
        danMuHelper.release();
        if (frame_animation_view != null) {
            frame_animation_view.stop();
        }

        OkHttpUtils.getInstance().cancelTag("getConfig");
        OkHttpUtils.getInstance().cancelTag("sendBarrage");
    }

    @Override
    protected void onStop() {
        super.onStop();
        //解除广播
        try {
            unregisterReceiver(broadCastReceiver);
        } catch (Exception e) {

        }
    }

    @Override
    public void onClick(View view) {

    }

    //发送弹幕
    protected void sendBarrage() {

        if (mChatInput.getText().toString().trim().length() == 0 || (!mConnectionState)) return;

        PhoneLiveApi.sendBarrage(mUser, mChatInput.getText().toString(), mRoomNum, mStreamName, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {

                JSONArray data = ApiUtils.checkIsSuccess(response);
                if (data != null) {
                    try {
                        JSONObject tokenJson = data.getJSONObject(0);

                        mUser.coin = tokenJson.getString("coin");
                        mUser.level = tokenJson.getString("level");
                        mIMControl.doSendBarrage(tokenJson.getString("barragetoken"), mUser);
                        mChatInput.setText("");
                        mChatInput.setHint("弹幕，" + barrageFee + LiveUtils.getConfigBean(AppContext.getInstance()).name_coin + "/条");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

    }

    //更新用户余额
    protected void refreshUserCoin() {
        PhoneLiveApi.requestGetUserCoin(mUser.id, mUser.token, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {

                JSONArray data = ApiUtils.checkIsSuccess(response);
                if (data != null) {
                    try {
                        mUser.coin = data.getJSONObject(0).getString("coin");
                        AppContext.getInstance().saveUserInfo(mUser);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    //获取直播间贡献排行榜
    protected void startGetRankInfo(boolean isNow) {

        if (isNow) {
            getRankInfoRunnable.run();
        } else {
            mHandler.postDelayed(getRankInfoRunnable, 60 * 60 * 1000);
        }
    }

    private Runnable getRankInfoRunnable = new Runnable() {
        @Override
        public void run() {

            PhoneLiveApi.requestGetLiveRankList(mRoomNum, new StringCallback() {

                @Override
                public void onError(Call call, Exception e, int id) {

                }

                @Override
                public void onResponse(String response, int id) {

                    String res = ApiUtils.checkIsSuccess2(response);
                    if (res != null) {
                        orderListData.clear();
                        orderListData.addAll(JSON.parseArray(res, RankBean.class));
                        orderAdapter.notifyDataSetChanged();

                        startGetRankInfo(false);
                    }

                }
            });
        }
    };

    protected void addSystemMessage(String body) {
        ChatBean c = new ChatBean();
        c.setType(Constant.IM_TEXT_TYPE.SYSTEM_MSG);
        c.setSendChatMsg(body);
        c.setUserNick("系统消息");
        mListChats.add(c);
        mChatListAdapter.notifyDataSetChanged();
    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }

    public synchronized static boolean isFastClick(int i) {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < i) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

}
