package com.daimeng.live.viewpagerfragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.daimeng.live.fragment.HotFragment;
import com.daimeng.live.fragment.HotShortVideoFragment;
import com.daimeng.live.fragment.NearbyLiveFragment;
import com.daimeng.live.ui.CloudVideoFragment;
import com.daimeng.live.utils.LiveUtils;
import com.daimeng.live.utils.StringUtils;
import com.daimeng.live.utils.TDevice;
import com.daimeng.live.utils.UIHelper;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.daimeng.live.AppContext;
import com.daimeng.live.R;
import com.daimeng.live.adapter.ViewPageFragmentAdapter;
import com.daimeng.live.base.BaseFragment;
import com.daimeng.live.fragment.AttentionFragment;
import com.daimeng.live.fragment.NewestFragment;
import com.daimeng.live.interf.ListenMessage;
import com.daimeng.live.im.PhoneLivePrivateChat;
import com.daimeng.live.widget.PagerSlidingTabStrip;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;

public class IndexPagerFragment extends BaseFragment implements ListenMessage {

    private View view;
    @BindView(R.id.mviewpager)
    ViewPager pager;

    @BindView(R.id.iv_hot_select_region)
    ImageView mRegion;

    @BindView(R.id.tabs)
    PagerSlidingTabStrip tabs;


    @BindView(R.id.fl_tab_container)
    FrameLayout flTabContainer;

    @BindView(R.id.iv_hot_new_message)
    ImageView mIvNewMessage;

    private ViewPageFragmentAdapter viewPageFragmentAdapter;

    public static int mSex = 0;

    public static String mArea = "";

    private EMMessageListener mMsgListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (view == null) {
            view = inflater.inflate(R.layout.fragment_hot, null);
            ButterKnife.bind(this, view);
            initView();
            initData();
        } else {
            mIvNewMessage.setVisibility(View.GONE);
            tabs.setPageChangeListener();
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            int pos = savedInstanceState.getInt("position");
            pager.setCurrentItem(pos, true);

        }
    }

    @Override
    public void initData() {


    }

    @OnClick({R.id.iv_hot_private_chat, R.id.iv_hot_search})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_hot_private_chat:
                String uid = AppContext.getInstance().getLoginUid();
                if (0 < StringUtils.toInt(uid)) {
                    mIvNewMessage.setVisibility(View.GONE);
                    UIHelper.showPrivateChatSimple(getActivity(), uid);
                }

                break;
            case R.id.iv_hot_search:
                UIHelper.showScreen(getActivity());
                break;
            default:
                break;
        }
    }

    private void initView() {

        mIvNewMessage.setVisibility(View.GONE);

        viewPageFragmentAdapter = new ViewPageFragmentAdapter(getFragmentManager(), pager);
        viewPageFragmentAdapter.addTab(getString(R.string.attention), "dr", AttentionFragment.class, getBundle());
        viewPageFragmentAdapter.addTab(getString(R.string.live), "rm", HotFragment.class, getBundle());

        //是否开启了短视频功能
        if (StringUtils.toInt(LiveUtils.getConfigBean(getContext()).is_open_short_video) == 1) {
            viewPageFragmentAdapter.addTab(getString(R.string.video), "video", HotShortVideoFragment.class, getBundle());
        }


        //是否开启了云点播功能
        if (StringUtils.toInt(LiveUtils.getConfigBean(getContext()).is_open_cloud_video) == 1) {
            viewPageFragmentAdapter.addTab(getString(R.string.cloud_video), "cloud_video", CloudVideoFragment.class, getBundle());
        }
        //广告
        //viewPageFragmentAdapter.addTab(getString(R.string.advert), "advert", AdvertFragment.class, getBundle());
        //viewPageFragmentAdapter.addTab(getString(R.string.hot), "rmsp", HotShortVideoFragment.class, getBundle());


        viewPageFragmentAdapter.addTab(getString(R.string.nearby), "nearby", NearbyLiveFragment.class, getBundle());
        viewPageFragmentAdapter.addTab(getString(R.string.newest), "newest", NewestFragment.class, getBundle());

        pager.setAdapter(viewPageFragmentAdapter);

        pager.setOffscreenPageLimit(2);

        tabs.setViewPager(pager);
        tabs.setUnderlineColor(getResources().getColor(R.color.transparent));
        tabs.setDividerColor(getResources().getColor(R.color.transparent));
        tabs.setIndicatorColor(getResources().getColor(R.color.white));
        tabs.setTextColor(getResources().getColor(R.color.black));
        tabs.setTextSize((int) TDevice.dpToPixel(15));
        tabs.setSelectedTextColor(getResources().getColor(R.color.black));
        tabs.setIndicatorHeight(2);
        tabs.setZoomMax(0f);
        tabs.setIndicatorColorResource(R.color.black);

        pager.setCurrentItem(1);
    }

    @Override
    public void onResume() {
        super.onResume();
        //获取私信未读数量
        if (PhoneLivePrivateChat.getUnreadMsgsCount() > 0) {
            mIvNewMessage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void listenMessage() {

        mMsgListener = new EMMessageListener() {

            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                //收到消息
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mIvNewMessage.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                //收到透传消息
            }

            @Override
            public void onMessageRead(List<EMMessage> messages) {

            }

            @Override
            public void onMessageDelivered(List<EMMessage> messages) {

            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {
                //消息状态变动
            }
        };
        EMClient.getInstance().chatManager().addMessageListener(mMsgListener);

    }

    @Override
    public void unListen() {
        EMClient.getInstance().chatManager().removeMessageListener(mMsgListener);
    }

    @Override
    public void onStart() {
        super.onStart();

        listenMessage();
    }

    @Override
    public void onPause() {
        super.onPause();
        unListen();
    }

    @Override
    public void onDestroy() {
        //注销广播
        super.onDestroy();
        unListen();

    }

    private Bundle getBundle() {
        Bundle bundle = new Bundle();

        return bundle;
    }
}
