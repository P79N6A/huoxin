package com.daimeng.live.ui;

import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.daimeng.live.adapter.ShortVideoListAdapter;
import com.daimeng.live.bean.ConfigBean;
import com.daimeng.live.bean.ShortVideoBean;
import com.daimeng.live.dialog.LiveCommon;
import com.daimeng.live.utils.SimpleUtils;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.daimeng.live.AppConfig;
import com.daimeng.live.AppContext;
import com.daimeng.live.R;
import com.daimeng.live.api.remote.ApiUtils;
import com.daimeng.live.api.remote.PhoneLiveApi;
import com.daimeng.live.base.BaseActivity;
import com.daimeng.live.bean.LiveRecordBean;
import com.daimeng.live.bean.UserHomePageBean;
import com.daimeng.live.utils.LiveUtils;
import com.daimeng.live.utils.StringUtils;
import com.daimeng.live.utils.TDevice;
import com.daimeng.live.utils.UIHelper;
import com.daimeng.live.widget.AvatarView;
import com.daimeng.live.widget.BlackTextView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 他人信息
 */
public class HomePageActivity extends BaseActivity {



    //昵称
    @BindView(R.id.tv_home_page_uname)
    BlackTextView mTvUNice;

    @BindView(R.id.iv_home_page_sex)
    ImageView mUSex;

    @BindView(R.id.iv_home_page_level)
    ImageView mULevel;

    public ImageView mULevels;

    //头像
    @BindView(R.id.av_home_page_uhead)
    AvatarView mUHead;

    @BindView(R.id.iv_top_bg)
    ImageView mIvTopBg;


    //关注数
    @BindView(R.id.tv_home_page_follow)
    BlackTextView mTvUFollowNum;

    //粉丝数
    @BindView(R.id.tv_home_page_fans)
    BlackTextView mTvUFansNum;

    //个性签名
    @BindView(R.id.tv_home_page_sign)
    BlackTextView mTvUSign;

    @BindView(R.id.tv_home_page_sign2)
    BlackTextView mTvUSign2;

    @BindView(R.id.tv_home_page_num)
    BlackTextView mTvUNum;

    @BindView(R.id.ll_home_page_index)
    LinearLayout mHomeIndexPage;

    @BindView(R.id.ll_home_page_video)
    LinearLayout mHomeVideoPage;

    @BindView(R.id.tv_home_page_index_btn)
    BlackTextView mPageIndexBtn;

    @BindView(R.id.tv_home_page_video_btn)
    BlackTextView mPageVideoBtn;

    @BindView(R.id.tv_home_page_menu_follow)
    BlackTextView mFollowState;

    @BindView(R.id.tv_home_page_black_state)
    BlackTextView mTvBlackState;

    @BindView(R.id.ll_home_page_bottom_menu)
    LinearLayout mLLBottomMenu;

    @BindView(R.id.rv_live_record)
    RecyclerView mRvShortVideo;

    @BindView(R.id.view_1)
    View mViewLine1;

    @BindView(R.id.view_2)
    View mViewLine2;

    @BindView(R.id.refresh_short_video)
    SwipeRefreshLayout mRefreshShortVideo;

    @BindView(R.id.rl_live_status)
    RelativeLayout mRlLiveStatusView;

    @BindView(R.id.tv_home_tick_order)
    TextView mTvTickOrderTitle;

    //当前选中的直播记录bean
    private LiveRecordBean mLiveRecordBean;

    private String uid;

    private AvatarView[] mOrderTopNoThree = new AvatarView[3];

    private UserHomePageBean mUserHomePageBean;

    private ArrayList<ShortVideoBean> mShortVideoList = new ArrayList<>();

    private ShortVideoListAdapter mLiveShortVideoAdapter;

    private int mVideoPage = 1;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    public void initView() {

        mOrderTopNoThree[0] = (AvatarView) findViewById(R.id.av_home_page_order1);
        mOrderTopNoThree[1] = (AvatarView) findViewById(R.id.av_home_page_order2);
        mOrderTopNoThree[2] = (AvatarView) findViewById(R.id.av_home_page_order3);


        initRecycleView();

        ConfigBean pConfigBean = LiveUtils.getConfigBean(this);

        mTvTickOrderTitle.setText(pConfigBean.name_votes + "排行榜");
    }

    //短视频列表
    private void initRecycleView() {

        mLiveShortVideoAdapter = new ShortVideoListAdapter(this, mShortVideoList);
        mLiveShortVideoAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                UIHelper.showShortVideoPlayer(HomePageActivity.this, mShortVideoList.get(position));
            }
        });
        mRefreshShortVideo.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestGetShortVideoList();
            }
        });
        mRvShortVideo.setLayoutManager(new GridLayoutManager(this, 2));
        mRvShortVideo.setAdapter(mLiveShortVideoAdapter);
    }

    @Override
    public void initData() {

        uid = getIntent().getStringExtra("uid");

        if (uid.equals(getUserID())) {
            mLLBottomMenu.setVisibility(View.GONE);
        }

        mRefreshShortVideo.setRefreshing(true);
        requestGetShortVideoList();

        requestGetHomePageUInfo();

    }

    private void requestGetHomePageUInfo() {

        //请求用户信息
        PhoneLiveApi.getHomePageUInfo(getUserID(), uid, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
            }

            @Override
            public void onResponse(String response, int id) {
                JSONArray res = ApiUtils.checkIsSuccess(response);
                if (res != null) {

                    try {
                        mUserHomePageBean = JSON.parseObject(res.getString(0), UserHomePageBean.class);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    fillUIUserInfo();
                }
            }
        });
    }


    //ui填充
    private void fillUIUserInfo() {


        if (mUserHomePageBean.islive.equals("1")) {
            mRlLiveStatusView.setVisibility(View.VISIBLE);
            mRlLiveStatusView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UIHelper.startRtmpPlayerActivity(HomePageActivity.this, mUserHomePageBean.liveinfo);
                }
            });
        } else {
            mRlLiveStatusView.setVisibility(View.GONE);
        }

        mUHead.setAvatarUrl(mUserHomePageBean.avatar);
        SimpleUtils.loadImageForView(this, mIvTopBg, mUserHomePageBean.avatar, 0);
        mTvUNice.setText(mUserHomePageBean.user_nicename);
        mUSex.setImageResource(LiveUtils.getSexRes(mUserHomePageBean.sex));
//        mULevel.setImageResource(LiveUtils.getLevelRes(mUserHomePageBean.level));
        if(null!=mUserHomePageBean.getWealth().getCustombackground()&&!mUserHomePageBean.getWealth().equals("")){
            SimpleUtils.loadImageForView(this,  mULevel, mUserHomePageBean.getWealth().getCustombackground(), 0);
        }
        mULevels=mULevel;

        mTvUFansNum.setText(getString(R.string.fans) + ":" + mUserHomePageBean.fans);
        mTvUFollowNum.setText(getString(R.string.attention) + ":" + mUserHomePageBean.follows);
        mTvUSign.setText(mUserHomePageBean.signature);
        mTvUSign2.setText(mUserHomePageBean.signature);
        mTvUNum.setText(mUserHomePageBean.id);
        mFollowState.setText(StringUtils.toInt(mUserHomePageBean.isattention) == 0 ? getString(R.string.follow2) : getString(R.string.alreadyfollow));
        mTvBlackState.setText(StringUtils.toInt(mUserHomePageBean.isblack) == 0 ? getString(R.string.pullblack) : getString(R.string.relieveblack));

        List<UserHomePageBean.ContributeBean> os = mUserHomePageBean.contribute;
        for (int i = 0; i < os.size(); i++) {
            mOrderTopNoThree[i].setAvatarUrl(os.get(i).getAvatar());
        }


    }

    @OnClick({R.id.ll_home_page_menu_lahei, R.id.ll_home_page_menu_privatechat, R.id.tv_home_page_menu_follow, R.id.rl_home_pager_yi_order, R.id.tv_home_page_follow, R.id.tv_home_page_index_btn, R.id.tv_home_page_video_btn, R.id.iv_home_page_back, R.id.tv_home_page_fans})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_home_page_menu_privatechat:
                //发送私信
                openPrivateChat();
                break;
            case R.id.ll_home_page_menu_lahei:
                //拉黑用户
                pullTheBlack();
                break;
            case R.id.tv_home_page_menu_follow:
                //关注用户
                followUser();
                break;
            case R.id.tv_home_page_index_btn:
                //切换到信息页面
                changeLineStatus(true);
                mHomeIndexPage.setVisibility(View.VISIBLE);
                mHomeVideoPage.setVisibility(View.GONE);
                mPageIndexBtn.setTextColor(getResources().getColor(R.color.global));
                mPageVideoBtn.setTextColor(getResources().getColor(R.color.black));
                break;
            case R.id.tv_home_page_video_btn:
                //切换回放记录
                changeLineStatus(false);
                mHomeIndexPage.setVisibility(View.GONE);
                mHomeVideoPage.setVisibility(View.VISIBLE);
                mPageIndexBtn.setTextColor(getResources().getColor(R.color.black));
                mPageVideoBtn.setTextColor(getResources().getColor(R.color.global));

                break;
            case R.id.iv_home_page_back:
                finish();
                break;
            case R.id.tv_home_page_fans:
                //粉丝
                UIHelper.showFansListActivity(this, uid);
                break;
            case R.id.tv_home_page_follow:
                //关注
                UIHelper.showAttentionActivity(this, uid);
                break;
            case R.id.rl_home_pager_yi_order:
                //魅力值排行榜
                OrderWebViewActivity.startOrderWebView(this, uid);
                break;

            default:
                break;
        }

    }

    private void changeLineStatus(boolean status) {

        if (status) {
            mViewLine1.setBackgroundResource(R.color.global);
            mViewLine2.setBackgroundColor(Color.parseColor("#E2E2E2"));
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mViewLine1.getLayoutParams();
            params.height = (int) TDevice.dpToPixel(2);
            mViewLine1.setLayoutParams(params);

            LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) mViewLine2.getLayoutParams();
            params2.height = 1;
            mViewLine2.setLayoutParams(params2);
        } else {
            mViewLine2.setBackgroundResource(R.color.global);
            mViewLine1.setBackgroundColor(Color.parseColor("#E2E2E2"));

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mViewLine2.getLayoutParams();
            params.height = (int) TDevice.dpToPixel(2);
            mViewLine2.setLayoutParams(params);

            LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) mViewLine1.getLayoutParams();
            params2.height = 1;
            mViewLine1.setLayoutParams(params2);
        }

    }


    //打开直播记录
    private void showLiveRecord() {

        showWaitDialog("正在获取回放...", false);

        PhoneLiveApi.getLiveRecordById(mLiveRecordBean.getId(), new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                hideWaitDialog();
            }

            @Override
            public void onResponse(String response, int id) {
                hideWaitDialog();
                JSONArray res = ApiUtils.checkIsSuccess(response);

                if (res != null) {
                    try {
                        mLiveRecordBean.setVideo_url(res.getJSONObject(0).getString("url"));
                        LiveRecordPlayerActivity.startVideoBack(HomePageActivity.this, mLiveRecordBean);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

    }

    //短视频列表
    private void requestGetShortVideoList() {

        PhoneLiveApi.requestGetShortVideoList(getUserID(), getUserToken(), uid, mVideoPage, new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {

                String res = ApiUtils.checkIsSuccess2(response);
                if (res != null) {

                    List<ShortVideoBean> list = JSON.parseArray(res, ShortVideoBean.class);
                    if (!mRefreshShortVideo.isRefreshing()) {

                        if (list.size() < AppConfig.PAGE_DATA_COUNT) {

                            mLiveShortVideoAdapter.loadMoreEnd();
                        } else {

                            mLiveShortVideoAdapter.loadMoreComplete();
                        }
                    } else {

                        mShortVideoList.clear();
                        mRefreshShortVideo.setRefreshing(false);
                    }
                    mShortVideoList.addAll(list);
                    mLiveShortVideoAdapter.notifyDataSetChanged();
                }
            }
        });
    }


    //拉黑
    private void pullTheBlack() {

        PhoneLiveApi.pullTheBlack(AppContext.getInstance().getLoginUid(), uid,
                AppContext.getInstance().getToken(),
                new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        AppContext.showToast("操作失败");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        JSONArray res = ApiUtils.checkIsSuccess(response);
                        if (null == res) return;
                        if (StringUtils.toInt(mUserHomePageBean.isblack) == 0) {
                            //第二个参数如果为true，则把用户加入到黑名单后双方发消息时对方都收不到；false，则我能给黑名单的中用户发消息，但是对方发给我时我是收不到的
                            try {
                                EMClient.getInstance().contactManager().addUserToBlackList(String.valueOf(mUserHomePageBean.id), true);
                            } catch (HyphenateException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                EMClient.getInstance().contactManager().removeUserFromBlackList(String.valueOf(mUserHomePageBean.id));
                            } catch (HyphenateException e) {
                                e.printStackTrace();
                            }
                        }

                        int isBlack = StringUtils.toInt(mUserHomePageBean.isblack);

                        mUserHomePageBean.isblack = (isBlack == 0 ? "1" : "0");

                        mTvBlackState.setText(isBlack == 0 ? getString(R.string.relieveblack) : getString(R.string.pullblack));
                        AppContext.showToast(isBlack == 0 ? "拉黑成功" : "解除拉黑", 0);

                    }
                });
    }

    //私信
    private void openPrivateChat() {

        if (StringUtils.toInt(mUserHomePageBean.isblack2) == 1) {
            AppContext.showToast("你已被对方拉黑无法私信");
            return;
        }

        if (null != mUserHomePageBean) {

            LiveCommon.showPrivateChat(getUserID(), mUserHomePageBean.id, HomePageActivity.this);

        }
    }

    //关注
    private void followUser() {

        PhoneLiveApi.showFollow(getUserID(), uid, getUserToken(), new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                mUserHomePageBean.isattention = (
                        StringUtils.toInt(mUserHomePageBean.isattention) == 0 ? "1" : "0");

                if (StringUtils.toInt(mUserHomePageBean.isattention) == 0) {
                    mFollowState.setText(getString(R.string.follow2));
                } else {

                    mFollowState.setText(getString(R.string.alreadyfollow));
                    if (StringUtils.toInt(mUserHomePageBean.isblack) != 0) {
                        pullTheBlack();
                    }
                }
            }
        });
    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag("getHomePageUInfo");
        OkHttpUtils.getInstance().cancelTag("showFollow");
        OkHttpUtils.getInstance().cancelTag("getPmUserInfo");
        OkHttpUtils.getInstance().cancelTag("pullTheBlack");
        OkHttpUtils.getInstance().cancelTag("getHomePageUInfo");
        OkHttpUtils.getInstance().cancelTag("getLiveRecord");
    }
}
