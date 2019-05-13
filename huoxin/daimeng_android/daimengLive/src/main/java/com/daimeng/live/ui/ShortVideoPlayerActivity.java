package com.daimeng.live.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
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
import com.daimeng.live.base.BaseTitleActivity;
import com.daimeng.live.bean.ShortVideoBean;
import com.daimeng.live.bean.ShortVideoReplyBean;
import com.daimeng.live.bean.callback.GetShortVideoDetailInfo;
import com.daimeng.live.event.Event;
import com.daimeng.live.fragment.ShortVideoActionDialogFragment;
import com.daimeng.live.fragment.ShortVideoReplyListDialogFragment;
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
import butterknife.OnClick;
import okhttp3.Call;

public class ShortVideoPlayerActivity extends BaseTitleActivity implements ITXLivePlayListener {

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

    @Override
    protected int getLayoutId() {
        return R.layout.activity_short_video_player;
    }

    @OnClick({R.id.ll_author_info,R.id.tv_say,R.id.video_view,R.id.tv_send,R.id.tv_reply_list,R.id.tv_follow_num,R.id.iv_back,R.id.iv_action,R.id.tv_forward_num})
    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.ll_author_info:

                UIHelper.showHomePageActivity(this,shortVideoBean.getUid());
                break;
            case R.id.iv_action:

                showActionDialog();
                break;

            case R.id.iv_back:

                finish();
                break;
            case R.id.tv_say:

                changeInputStatus(true);
                break;

            case R.id.video_view:

                changeInputStatus(false);
                break;

            case R.id.tv_send:
                if(replyBean != null){
                    requestReplyComment();
                    return;
                }
                requestReplyVideo();
                break;

            case R.id.tv_reply_list:


                if(mShortVideoReplyDialog == null){
                    mShortVideoReplyDialog = new ShortVideoReplyListDialogFragment();
                }

                Bundle bundle = new Bundle();
                bundle.putString(ShortVideoReplyListDialogFragment.VIDEO_ID,shortVideoBean.getId());
                mShortVideoReplyDialog.setArguments(bundle);
                mShortVideoReplyDialog.show(getSupportFragmentManager(),"ShortVideoReplyListDialogFragment");
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

    private void changeInputStatus(boolean status){

        if(status){

            mLlInput.setVisibility(View.VISIBLE);
            mEtSay.requestFocus();
            //InputMethodUtils.showSoftInput(this);
        }else{
            replyBean = null;
            mEtSay.setHint("");
            mLlInput.setVisibility(View.GONE);
            InputMethodUtils.hideSoftInput(this);
        }
    }

    private void showActionDialog() {

        ShortVideoActionDialogFragment shortVideoActionDialog = new ShortVideoActionDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ShortVideoActionDialogFragment.SHORT_VIDEO_INFO,shortVideoBean);
        shortVideoActionDialog.setArguments(bundle);
        shortVideoActionDialog.show(getSupportFragmentManager(),"ShortVideoActionDialogFragment");
    }

    /**
    * @dw 视频点赞
    *
    * */
    private void requestShortVideoFollow() {

        PhoneLiveApi.requestShortVideoFollow(mUserId,mUserToken,shortVideoBean.getId(),new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {

                String res = ApiUtils.checkIsSuccess2(response);
                if(res != null){

                    String state = JSON.parseObject(res).getString("follow_state");

                    if(StringUtils.toInt(state) == 1) {
                        mShortVideoDetailInfo.incFollow_num(1);
                    }else{
                        mShortVideoDetailInfo.decFollow_num(1);
                    }
                    changeFollowState(state);
                }
            }
        });
    }

    //发表评论回复
    private void requestReplyComment(){

        if(replyBean == null){
            return;
        }

        String body = mEtSay.getText().toString();
        if(TextUtils.isEmpty(body)){
            AppContext.showToast("请输入回复内容");
        }

        showWaitDialog("正在发表回复...",false);

        PhoneLiveApi.requestReplyComment(mUserId,mUserToken,shortVideoBean.getId(),body,replyBean.getId(),new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {

                hideWaitDialog();
            }

            @Override
            public void onResponse(String response, int id) {

                hideWaitDialog();
                String res = ApiUtils.checkIsSuccess2(response);
                if(res != null){

                    mEtSay.setText("");
                    mLlInput.setVisibility(View.GONE);
                    InputMethodUtils.hideSoftInput(ShortVideoPlayerActivity.this);

                    AppContext.showToast("回复成功");
                }
            }
        });
    }

    //发表评论
    private void requestReplyVideo() {

        String body = mEtSay.getText().toString();
        if(TextUtils.isEmpty(body)){
            AppContext.showToast("请输入评论内容");
        }

        showWaitDialog("正在发表评论...",false);

        PhoneLiveApi.requestReplyVideo(mUserId,mUserToken,shortVideoBean.getId(),body,new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {

                hideWaitDialog();
            }

            @Override
            public void onResponse(String response, int id) {

                hideWaitDialog();
                String res = ApiUtils.checkIsSuccess2(response);
                if(res != null){

                    mEtSay.setText("");
                    mLlInput.setVisibility(View.GONE);
                    InputMethodUtils.hideSoftInput(ShortVideoPlayerActivity.this);

                    AppContext.showToast("发表成功");
                }
            }
        });
    }

    @Override
    public void initView() {

        // 就一行代码
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mImageViewBg = (ImageView) findViewById(R.id.cover);

        mTXLivePlayer = new TXLivePlayer(this);
        mTXPlayConfig = new TXLivePlayConfig();
        mTXCloudVideoView = (TXCloudVideoView) findViewById(R.id.video_view);
        mTXCloudVideoView.disableLog(true);


        mTXCloudVideoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction() == MotionEvent.ACTION_UP){

                    long time = System.currentTimeMillis() - lastClickTime;
                    //TLog.log(String.valueOf(time));
                    if(time < 250){
                        addFollowAnimation(motionEvent.getX(),motionEvent.getY());
                    }
                    lastClickTime = System.currentTimeMillis();
                }

                return false;
            }
        });


    }

    private void addFollowAnimation(float x, float y) {

        if(StringUtils.toInt(mShortVideoDetailInfo.getFollow_state()) == 0){
            mShortVideoDetailInfo.setFollow_state("1");
            requestShortVideoFollow();
        }
        final ImageView follow = new ImageView(this);
        follow.setImageResource(R.drawable.ic_double_follow);
        follow.setX(x);
        follow.setY(y);
        mRlContent.addView(follow);

        ObjectAnimator animator = ObjectAnimator.ofFloat(follow,"translationY",y - 200);
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

    @Override
    public void initData() {

        shortVideoBean = getIntent().getParcelableExtra(VIDEO_DATA);
        SimpleUtils.loadImageForView(this,mImageViewBg,shortVideoBean.getCover_url(),0);

        mTvTitle.setText(shortVideoBean.getTitle());
        requestGetShortVideoDetailInfo();

    }

    //获取视频详情
    private void requestGetShortVideoDetailInfo() {

        PhoneLiveApi.requestGetShortVideoDetailInfo(mUserId,mUserToken,shortVideoBean.getId(),new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {

                mShortVideoDetailInfo = ApiUtils.checkIsSuccess(response,GetShortVideoDetailInfo.class);
                if(mShortVideoDetailInfo != null){

                    mTvForwardNum.setText(SimpleUtils.simplifyString(mShortVideoDetailInfo.getForward_num()));
                    mTvFollowNum.setText(SimpleUtils.simplifyString(mShortVideoDetailInfo.getFollow_num()));
                    mTvReplyNum.setText(SimpleUtils.simplifyString(mShortVideoDetailInfo.getReply_num()));

                    mTvPlayerNum.setText("播放量: " + SimpleUtils.simplifyString(mShortVideoDetailInfo.getPlayer_num()));
                    changeFollowState(mShortVideoDetailInfo.getFollow_state());

                    SimpleUtils.loadImageForView(ShortVideoPlayerActivity.this,mIvAvatar, LiveUtils.getHttpUrl(mShortVideoDetailInfo.getUser_info().avatar),0);
                    mTvUserNicename.setText(mShortVideoDetailInfo.getUser_info().user_nicename);
                    videoUrl = mShortVideoDetailInfo.getVideo_url();
                    startPlay();

                }
            }
        });
    }

    private void changeFollowState(String fs){

        Drawable drawableLeft;
        //设置点赞状态
        if(StringUtils.toInt(fs) == 1){
            drawableLeft = getResources().getDrawable(
                    R.drawable.ic_video_follow);

        }else{

            drawableLeft = getResources().getDrawable(
                    R.drawable.ic_short_video_follow);

        }
        mTvFollowNum.setText(mShortVideoDetailInfo.getFollow_num());
        mTvFollowNum.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,
                null, null, null);
        mShortVideoDetailInfo.setFollow_state(fs);
    }

    private boolean startPlay() {

        mTXLivePlayer.setPlayerView(mTXCloudVideoView);
        mTXLivePlayer.setPlayListener(this);
        mTXLivePlayer.enableHardwareDecode(false);
        mTXLivePlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
        mTXLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);//RENDER_MODE_ADJUST_RESOLUTION
        mTXLivePlayer.setConfig(mTXPlayConfig);

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
        if (event == TXLiveConstants.PLAY_EVT_PLAY_PROGRESS) {
            if (mStartSeek) {
                return;
            }
            if (mImageViewBg.isShown()) {
                mImageViewBg.setVisibility(View.GONE);
            }
            /*int progress = param.getInt(TXLiveConstants.EVT_PLAY_PROGRESS);
            int duration = param.getInt(TXLiveConstants.EVT_PLAY_DURATION);//单位为s
            long curTS = System.currentTimeMillis();
            // 避免滑动进度条松开的瞬间可能出现滑动条瞬间跳到上一个位置
            if (Math.abs(curTS - mTrackingTouchTS) < 500) {
                return;
            }
            mTrackingTouchTS = curTS;

            if (mSeekBar != null) {
                mSeekBar.setProgress(progress);
            }
            if (mProgressTime != null) {
                mProgressTime.setText(String.format(Locale.CHINA, "%02d:%02d/%02d:%02d", (progress) / 60, progress % 60, (duration) / 60, duration % 60));
            }

            if (mSeekBar != null) {
                mSeekBar.setMax(duration);
            }*/
        } else if (event == TXLiveConstants.PLAY_ERR_NET_DISCONNECT) {

            //showErrorAndQuit(TCConstants.ERROR_MSG_NET_DISCONNECTED);

        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_END) {
            /*if (mProgressTime != null) {
                mProgressTime.setText(String.format(Locale.CHINA, "%s", "00:00/00:00"));
            }
            if (mSeekBar != null) {
                mSeekBar.setProgress(0);
            }*/
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

        switch (event.action){

            case Event.ShortVideoPlayer.SHORT_VIDEO_REPLY_COMMENT:

                changeInputStatus(true);
                replyBean = (ShortVideoReplyBean) event.data;
                mEtSay.setHint("回复" + replyBean.getUser_nicename() + ":");

                if(mShortVideoReplyDialog != null){
                    mShortVideoReplyDialog.dismiss();
                }
                break;

            default:

                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTXLivePlayer.resume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mTXLivePlayer.pause();
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
    protected void onDestroy() {
        super.onDestroy();
        mTXLivePlayer.stopPlay(true);
        mTXCloudVideoView.onDestroy();
        stopPlay(true);
    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }
}
