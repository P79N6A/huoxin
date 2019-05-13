package com.daimeng.live.fragment;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.daimeng.live.AppContext;
import com.daimeng.live.R;
import com.daimeng.live.api.remote.ApiUtils;
import com.daimeng.live.api.remote.PhoneLiveApi;
import com.daimeng.live.base.BaseFragment;
import com.daimeng.live.bean.ShortVideoBean;
import com.daimeng.live.bean.ShortVideoReplyBean;
import com.daimeng.live.bean.callback.GetShortVideoDetailInfo;
import com.daimeng.live.event.Event;
import com.daimeng.live.ui.ShortVideoPlayerTouchActivity;
import com.daimeng.live.utils.LiveUtils;
import com.daimeng.live.utils.SimpleUtils;
import com.daimeng.live.utils.StringUtils;
import com.daimeng.live.utils.UIHelper;
import com.daimeng.live.widget.CircleImageView;
import com.litesuits.common.utils.InputMethodUtils;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;


public class ShortVideoPlayerFragment extends BaseFragment implements ITXLivePlayListener {


    public static final String VIDEO_DATA = "video_data";

    @BindView(R.id.ll_input)
    LinearLayout mLlInput;

    @BindView(R.id.et_say)
    EditText mEtSay;

    @BindView(R.id.tv_forward_num)
    TextView mTvForwardNum;

    @BindView(R.id.tv_follow_num)
    TextView mTvFollowNum;

    @BindView(R.id.tv_reply_list)
    TextView mTvReplyNum;

    @BindView(R.id.iv_avatar)
    CircleImageView mIvAvatar;

    @BindView(R.id.tv_user_nicename)
    TextView mTvUserNicename;

    @BindView(R.id.tv_short_video_title)
    TextView mTvTitle;

    @BindView(R.id.rl_content)
    RelativeLayout mRlContent;

    @BindView(R.id.tv_player_num)
    TextView mTvPlayerNum;

    private TXCloudVideoView mTXCloudVideoView;

    private TXLivePlayer mTXLivePlayer = null;
    private TXLivePlayConfig mTXPlayConfig = null;


    boolean mVideoPlay = false;
    boolean mVideoPause = false;
    boolean mAutoPause = false;
    private ShortVideoBean shortVideoBean;

    private boolean mStartSeek = false;
    private long lastClickTime = 0;

    private ImageView mImageViewBg;
    private GetShortVideoDetailInfo mShortVideoDetailInfo;
    private ShortVideoReplyListDialogFragment mShortVideoReplyDialog;
    private ShortVideoReplyBean replyBean;
    private String videoUrl = "";

    private String mUserId;
    private String mUserToken;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_short_video_player, container, false);

        ButterKnife.bind(this, view);
        initView(view);
        initData();
        return view;
    }


    @Override
    public void initData() {

        mUserId = AppContext.getInstance().getLoginUid();
        mUserToken = AppContext.getInstance().getToken();
        shortVideoBean = getArguments().getParcelable(VIDEO_DATA);
        SimpleUtils.loadImageForView(getContext(), mImageViewBg, shortVideoBean.getCover_url(), 0);

        mTvTitle.setText(shortVideoBean.getTitle());
        requestGetShortVideoDetailInfo();

    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void initView(View view) {

        mImageViewBg = (ImageView) view.findViewById(R.id.cover);

        mTXLivePlayer = new TXLivePlayer(getContext());
        mTXPlayConfig = new TXLivePlayConfig();
        mTXCloudVideoView = (TXCloudVideoView) view.findViewById(R.id.video_view);
        mTXCloudVideoView.disableLog(true);

        mTXLivePlayer.setPlayerView(mTXCloudVideoView);
        mTXLivePlayer.setPlayListener(this);
        mTXLivePlayer.enableHardwareDecode(false);
        mTXLivePlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
        mTXLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);//RENDER_MODE_ADJUST_RESOLUTION
        mTXLivePlayer.setConfig(mTXPlayConfig);

        mTXCloudVideoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

                    long time = System.currentTimeMillis() - lastClickTime;
                    //TLog.log(String.valueOf(time));
                    if (time < 250) {
                        addFollowAnimation(motionEvent.getX(), motionEvent.getY());
                    }
                    lastClickTime = System.currentTimeMillis();
                }

                return false;
            }
        });


    }

    @OnClick({R.id.ll_author_info, R.id.tv_say, R.id.video_view, R.id.tv_send, R.id.tv_reply_list, R.id.tv_follow_num, R.id.iv_back, R.id.iv_action, R.id.tv_forward_num})
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.ll_author_info:

                UIHelper.showHomePageActivity(getContext(), shortVideoBean.getUid());
                break;
            case R.id.iv_action:

                showActionDialog();
                break;

            case R.id.iv_back:

                getActivity().finish();
                break;
            case R.id.tv_say:

                changeInputStatus(true);
                break;

            case R.id.video_view:

                changeInputStatus(false);
                break;

            case R.id.tv_send:
                if (replyBean != null) {
                    requestReplyComment();
                    return;
                }
                requestReplyVideo();
                break;

            case R.id.tv_reply_list:

                if (mShortVideoReplyDialog == null) {
                    mShortVideoReplyDialog = new ShortVideoReplyListDialogFragment();
                }

                Bundle bundle = new Bundle();
                bundle.putString(ShortVideoReplyListDialogFragment.VIDEO_ID, shortVideoBean.getId());
                mShortVideoReplyDialog.setArguments(bundle);
                mShortVideoReplyDialog.show(getFragmentManager(), "ShortVideoReplyListDialogFragment");
                break;

            case R.id.tv_follow_num:

                requestShortVideoFollow();
                break;

            case R.id.tv_forward_num:

                showActionDialog();
                break;

            default:
                break;
        }
    }

    /**
     * @dw 视频点赞
     */
    private void requestShortVideoFollow() {

        PhoneLiveApi.requestShortVideoFollow(mUserId, mUserToken, shortVideoBean.getId(), new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {

                String res = ApiUtils.checkIsSuccess2(response);
                if (res != null) {

                    String state = JSON.parseObject(res).getString("follow_state");

                    if (StringUtils.toInt(state) == 1) {
                        mShortVideoDetailInfo.incFollow_num(1);
                    } else {
                        mShortVideoDetailInfo.decFollow_num(1);
                    }
                    changeFollowState(state);
                }
            }
        });
    }

    //发表评论回复
    private void requestReplyComment() {

        if (replyBean == null) {
            return;
        }

        String body = mEtSay.getText().toString();
        if (TextUtils.isEmpty(body)) {
            AppContext.showToast("请输入回复内容");
        }

        showWaitDialog("正在发表回复...");

        PhoneLiveApi.requestReplyComment(mUserId, mUserToken, shortVideoBean.getId(), body, replyBean.getId(), new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {

                hideWaitDialog();
            }

            @Override
            public void onResponse(String response, int id) {

                hideWaitDialog();
                String res = ApiUtils.checkIsSuccess2(response);
                if (res != null) {

                    mEtSay.setText("");
                    mLlInput.setVisibility(View.GONE);
                    InputMethodUtils.hideSoftInput(getActivity());

                    AppContext.showToast("回复成功");
                }
            }
        });
    }

    //获取视频详情
    private void requestGetShortVideoDetailInfo() {

        PhoneLiveApi.requestGetShortVideoDetailInfo(mUserId, mUserToken, shortVideoBean.getId(), new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {

                mShortVideoDetailInfo = ApiUtils.checkIsSuccess(response, GetShortVideoDetailInfo.class);
                if (mShortVideoDetailInfo != null) {

                    mTvForwardNum.setText(SimpleUtils.simplifyString(mShortVideoDetailInfo.getForward_num()));
                    mTvFollowNum.setText(SimpleUtils.simplifyString(mShortVideoDetailInfo.getFollow_num()));
                    mTvReplyNum.setText(SimpleUtils.simplifyString(mShortVideoDetailInfo.getReply_num()));

                    mTvPlayerNum.setText("播放量: " + SimpleUtils.simplifyString(mShortVideoDetailInfo.getPlayer_num()));
                    changeFollowState(mShortVideoDetailInfo.getFollow_state());

                    if (mShortVideoDetailInfo.getUser_info() != null) {
                        SimpleUtils.loadImageForView(getContext(), mIvAvatar, LiveUtils.getHttpUrl(mShortVideoDetailInfo.getUser_info().avatar), 0);
                        mTvUserNicename.setText(mShortVideoDetailInfo.getUser_info().user_nicename);
                    }

                    videoUrl = mShortVideoDetailInfo.getVideo_url();

                    if (ShortVideoPlayerTouchActivity.select_video_id == StringUtils.toInt(shortVideoBean.getId()) ||
                            ShortVideoTouchPlayerFragment.select_video_id == StringUtils.toInt(shortVideoBean.getId())) {
                        startPlay();

                    }
                }
            }
        });
    }


    //发表评论
    private void requestReplyVideo() {

        String body = mEtSay.getText().toString();
        if (TextUtils.isEmpty(body)) {
            AppContext.showToast("请输入评论内容");
        }

        showWaitDialog("正在发表评论...");

        PhoneLiveApi.requestReplyVideo(mUserId, mUserToken, shortVideoBean.getId(), body, new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {

                hideWaitDialog();
            }

            @Override
            public void onResponse(String response, int id) {

                hideWaitDialog();
                String res = ApiUtils.checkIsSuccess2(response);
                if (res != null) {

                    mEtSay.setText("");
                    mLlInput.setVisibility(View.GONE);
                    InputMethodUtils.hideSoftInput(getActivity());

                    AppContext.showToast("发表成功");
                }
            }
        });
    }

    private void changeInputStatus(boolean status) {

        if (status) {

            mLlInput.setVisibility(View.VISIBLE);
            mEtSay.requestFocus();
            //InputMethodUtils.showSoftInput(this);
        } else {
            replyBean = null;
            mEtSay.setHint("");
            mLlInput.setVisibility(View.GONE);
            InputMethodUtils.hideSoftInput(getActivity());
        }
    }

    private void showActionDialog() {

        ShortVideoActionDialogFragment shortVideoActionDialog = new ShortVideoActionDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ShortVideoActionDialogFragment.SHORT_VIDEO_INFO, shortVideoBean);
        shortVideoActionDialog.setArguments(bundle);
        shortVideoActionDialog.show(getFragmentManager(), "ShortVideoActionDialogFragment");
    }

    private void addFollowAnimation(float x, float y) {

        if (StringUtils.toInt(mShortVideoDetailInfo.getFollow_state()) == 0) {
            mShortVideoDetailInfo.setFollow_state("1");
            requestShortVideoFollow();
        }
        final ImageView follow = new ImageView(getContext());
        follow.setImageResource(R.drawable.ic_double_follow);
        follow.setX(x);
        follow.setY(y);
        mRlContent.addView(follow);

        ObjectAnimator animator = ObjectAnimator.ofFloat(follow, "translationY", y - 200);
        animator.setDuration(500);
        animator.start();
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {

                mRlContent.removeView(follow);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

    }

    private void changeFollowState(String fs) {
        if (!isAdded()) {
            return;
        }

        Drawable drawableLeft;
        //设置点赞状态
        if (StringUtils.toInt(fs) == 1) {
            drawableLeft = getResources().getDrawable(
                    R.drawable.ic_video_follow);

        } else {

            drawableLeft = getResources().getDrawable(
                    R.drawable.ic_short_video_follow);

        }
        mTvFollowNum.setText(mShortVideoDetailInfo.getFollow_num());
        mTvFollowNum.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,
                null, null, null);
        mShortVideoDetailInfo.setFollow_state(fs);
    }

    private boolean startPlay() {

        int result = mTXLivePlayer.startPlay(videoUrl, TXLivePlayer.PLAY_TYPE_LOCAL_VIDEO); // result返回值：0 success;  -1 empty url; -2 invalid url; -3 invalid playType;
        if (result != 0) {
            //mStartPreview.setBackgroundResource(R.drawable.icon_record_start);
            return false;
        }

        mVideoPlay = true;
        return true;
    }

    protected void stopPlay(boolean clearLastFrame) {
        if (mTXLivePlayer != null) {
            mTXLivePlayer.setPlayListener(null);
            mTXLivePlayer.stopPlay(clearLastFrame);
            mVideoPlay = false;
        }
    }


    @Override
    public void onPlayEvent(int event, Bundle param) {
        if (mTXCloudVideoView != null) {
            mTXCloudVideoView.setLogText(null, param, event);
        }

        if (event == TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME) {
            if (mImageViewBg.isShown()) {
                mImageViewBg.setVisibility(View.GONE);
            }
        }
        if (event == TXLiveConstants.PLAY_EVT_PLAY_PROGRESS) {
            if (mStartSeek) {
                return;
            }
        } else if (event == TXLiveConstants.PLAY_ERR_NET_DISCONNECT) {

            //showErrorAndQuit(TCConstants.ERROR_MSG_NET_DISCONNECTED);

        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_END) {

            stopPlay(false);
            mImageViewBg.setVisibility(View.VISIBLE);
            startPlay();
        }
    }

    @Override
    public void onNetStatus(Bundle bundle) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event.ShortVideoPlayer event) {

        switch (event.action) {

            case Event.ShortVideoPlayer.SHORT_VIDEO_REPLY_COMMENT:

                changeInputStatus(true);
                replyBean = (ShortVideoReplyBean) event.data;
                mEtSay.setHint("回复" + replyBean.getUser_nicename() + ":");

                if (mShortVideoReplyDialog != null) {
                    mShortVideoReplyDialog.dismiss();
                }
                break;

            default:

                break;
        }
    }

    //视频页面滑动切换
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTouchPageChange(Event.OnTouchShortVideoPlayerPageChange event) {

        if ((isVisible() && ShortVideoPlayerTouchActivity.select_video_id == StringUtils.toInt(shortVideoBean.getId()))
                || (isVisible() && ShortVideoTouchPlayerFragment.select_video_id == StringUtils.toInt(shortVideoBean.getId()))) {
            if (mTXCloudVideoView != null) {
                startPlay();
            }
        } else {
            stopPlay(true);
            if (mTXCloudVideoView != null) {
                mTXCloudVideoView.onDestroy();
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
    public void onResume() {
        super.onResume();
        mTXLivePlayer.resume();

    }

    @Override
    public void onPause() {
        super.onPause();
        mTXLivePlayer.pause();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event.LeftVideoTouchChangeEvent event) {
        mTXLivePlayer.pause();
    }

}
