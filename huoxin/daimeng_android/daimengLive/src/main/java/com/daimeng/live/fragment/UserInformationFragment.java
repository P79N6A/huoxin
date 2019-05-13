package com.daimeng.live.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.daimeng.live.bean.ClientMenuBean;
import com.daimeng.live.bean.ConfigBean;
import com.daimeng.live.ui.RankActivity;
import com.daimeng.live.utils.DialogHelp;
import com.daimeng.live.utils.StringUtils;
import com.daimeng.live.utils.TDevice;
import com.daimeng.live.AppConfig;
import com.daimeng.live.AppContext;
import com.daimeng.live.R;
import com.daimeng.live.api.remote.ApiUtils;
import com.daimeng.live.api.remote.PhoneLiveApi;
import com.daimeng.live.base.BaseFragment;
import com.daimeng.live.bean.UserBean;
import com.daimeng.live.ui.customviews.LineControllerView;
import com.daimeng.live.utils.LiveUtils;
import com.daimeng.live.utils.UIHelper;
import com.daimeng.live.widget.AvatarView;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.BindView;
import okhttp3.Call;

/**
 * 登录用户中心页面
 */
public class UserInformationFragment extends BaseFragment {

    //头像
    @BindView(R.id.iv_avatar)
    AvatarView mIvAvatar;
    //昵称
    @BindView(R.id.tv_name)
    TextView mTvName;

    @BindView(R.id.tv_user_id)
    TextView mTvUserId;

    //粉丝
    @BindView(R.id.tv_follow_num)
    TextView mTvFollowNum;

    //关注
    @BindView(R.id.tv_fans_num)
    TextView mTvFansNum;

    //直播记
    @BindView(R.id.tv_live_num)
    TextView mTVLiveNum;

    @BindView(R.id.iv_sex)
    ImageView mIvSex;

    @BindView(R.id.iv_level)
    ImageView mIvLevel;

    @BindView(R.id.ll_family_manage)
    LineControllerView mLvFamilyManage;

    @BindView(R.id.ll_vip)
    LineControllerView mLvBuyVip;

    @BindView(R.id.ll_short_video)
    LineControllerView mLvShortVideo;

    @BindView(R.id.ll_news_message)
    LineControllerView mLvNewsMessage;

    @BindView(R.id.ll_menu)
    LinearLayout mLlMenu;


    private List<ClientMenuBean> clientMenuList = new ArrayList<>();
    private UserBean mInfo;
    private LineControllerView mLineDiamonds;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_information,
                container, false);
        ButterKnife.bind(this, view);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void onStart() {

        super.onStart();
        mInfo = AppContext.getInstance().getLoginUser();
        fillUI();
    }

    @Override
    public void initData() {

    }

    @Override
    public void initView(View view) {


        view.findViewById(R.id.tv_live_num).setOnClickListener(this);
        view.findViewById(R.id.tv_follow_num).setOnClickListener(this);
        view.findViewById(R.id.tv_fans_num).setOnClickListener(this);
        view.findViewById(R.id.ll_profit).setOnClickListener(this);
        view.findViewById(R.id.iv_setting).setOnClickListener(this);
        view.findViewById(R.id.ll_diamonds).setOnClickListener(this);
        view.findViewById(R.id.iv_edit_info).setOnClickListener(this);
        view.findViewById(R.id.ll_family_manage).setOnClickListener(this);
        view.findViewById(R.id.ll_vip).setOnClickListener(this);
        view.findViewById(R.id.ll_short_video).setOnClickListener(this);
        view.findViewById(R.id.tv_user_id).setOnClickListener(this);
        view.findViewById(R.id.ll_news_message).setOnClickListener(this);
        view.findViewById(R.id.ll_rank).setOnClickListener(this);
        view.findViewById(R.id.ll_live_time).setOnClickListener(this);

        ConfigBean config = LiveUtils.getConfigBean(getContext());

        ((LineControllerView) view.findViewById(R.id.ll_diamonds)).setName("我的" + config.name_coin);
        mLineDiamonds = (LineControllerView) view.findViewById(R.id.ll_diamonds);

        if (StringUtils.toInt(config.is_open_family_model) == 1) {
            mLvFamilyManage.setVisibility(View.VISIBLE);
        }

        if (StringUtils.toInt(config.is_open_vip_model) == 1) {
            //mLvBuyVip.setVisibility(View.VISIBLE);
        }

//        if(StringUtils.toInt(config.is_open_car_model) == 1){
//            mLvBuyCar.setVisibility(View.VISIBLE);
//            mLvCar.setVisibility(View.VISIBLE);
//        }

        if (StringUtils.toInt(config.is_open_short_video) == 1) {
            mLvShortVideo.setVisibility(View.VISIBLE);
            //mLvNewsMessage.setVisibility(View.VISIBLE);
        }
    }

    private void fillUI() {

        mTvUserId.setText(getString(R.string.app_name) + "ID: " + mInfo.id);
        mIvAvatar.setAvatarUrl(mInfo.avatar);
        //昵称
        mTvName.setText(mInfo.user_nicename);

        mIvSex.setImageResource(LiveUtils.getSexRes(mInfo.sex));
        mIvLevel.setImageResource(LiveUtils.getLevelRes(mInfo.level));
    }

    @Override
    protected void requestData(boolean refresh) {
        if (AppContext.getInstance().isLogin()) {
            sendRequestData();
        }

    }

    private void sendRequestData() {

        PhoneLiveApi.getMyUserInfo(AppContext.getInstance().getLoginUid(),
                AppContext.getInstance().getToken(), stringCallback);
    }

    @Override
    public void onClick(View v) {

        for (ClientMenuBean item : clientMenuList) {
            if (v.getId() == StringUtils.toInt(item.getId()) + 1000) {
                UIHelper.showWebView(getContext(), item.getUrl(), item.getName());
                return;
            }
        }
        switch (v.getId()) {

            case R.id.ll_live_time:
                UIHelper.showWebView(getActivity(), AppConfig.MAIN_URL + "/index.php?g=Appapi&m=H5&a=select_live_time&uid=" + mInfo.id + "&token=" + AppContext.getInstance().getToken(), "");
                break;
            case R.id.ll_rank:

                Intent intent = new Intent(getContext(), RankActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_news_message:

                UIHelper.showNewsMessageActivity(getContext());
                break;
            case R.id.tv_user_id:

                TDevice.copyTextToBoard(mInfo.id);
                break;

            case R.id.ll_short_video:

                UIHelper.showShortVideoList(getContext());
                break;
            //家族管理
            case R.id.ll_family_manage:

                requestFamilyInfo();
                break;
            case R.id.iv_avatar:
                break;
            case R.id.tv_live_num:
                UIHelper.showLiveRecordActivity(getActivity(), mInfo.id);
                break;
            case R.id.tv_follow_num:
                UIHelper.showAttentionActivity(getActivity(), mInfo.id);
                break;
            case R.id.tv_fans_num:
                UIHelper.showFansListActivity(getActivity(), mInfo.id);
                break;
            case R.id.iv_setting:
                UIHelper.showSetting(getActivity());
                break;
            case R.id.ll_diamonds:
                //我的钻石
                UIHelper.showMyDiamonds(getActivity());
                break;
            case R.id.ll_profit:
                //收益
                UIHelper.showProfitActivity(getActivity());
                break;
            case R.id.iv_edit_info:
                //编辑资料
                UIHelper.showMyInfoDetailActivity(getContext());
                break;
            case R.id.ll_vip:
                //VIP
                UIHelper.showBuyVipActivity(getContext());
                break;
            default:
                break;
        }
    }


    private StringCallback stringCallback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e, int id) {

        }

        @Override
        public void onResponse(String s, int id) {
            JSONArray res = ApiUtils.checkIsSuccess(s);
            if (res != null) {

                try {
                    com.alibaba.fastjson.JSONObject object = JSON.parseObject(res.getString(0));

                    mInfo = JSON.parseObject(object.getString("user_info"), UserBean.class);
                    AppContext.getInstance().updateUserInfo(mInfo);
                    mLineDiamonds.setContent(mInfo.coin);
                    clientMenuList.clear();
                    mLlMenu.removeAllViews();
                    clientMenuList.addAll(JSON.parseArray(object.getString("menu"), ClientMenuBean.class));
                    //添加后台菜单
                    for (ClientMenuBean item : clientMenuList) {
                        LineControllerView controllerView = new LineControllerView(getActivity(), null);
                        controllerView.setName(item.getName());
                        controllerView.setCanNavImg(getActivity(), LiveUtils.getHttpUrl(item.getImg()));
                        controllerView.setMoreVisible(true);
                        controllerView.setId(StringUtils.toInt(item.getId()) + 1000);
                        controllerView.setOnClickListener(UserInformationFragment.this);
                        mLlMenu.addView(controllerView);
                    }

                    mTVLiveNum.setText(String.format(Locale.CHINA, "直播 %s", object.getJSONObject("user_info").getString("lives")));
                    mTvFollowNum.setText(String.format(Locale.CHINA, "关注 %s", object.getJSONObject("user_info").getString("follows")));
                    mTvFansNum.setText(String.format(Locale.CHINA, "粉丝 %s", object.getJSONObject("user_info").getString("fans")));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    };


    private void requestFamilyInfo() {

        PhoneLiveApi.checkIsFamily(mInfo.id, new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {

                JSONArray res = ApiUtils.checkIsSuccess(response);
                if (res != null) {
                    try {
                        int type = res.getJSONObject(0).getInt("type");
                        int fid = res.getJSONObject(0).getInt("fid");
                        if (type == 0) {

                            DialogHelp.getSelectDialog(getContext(), "请选择", new String[]{"申请加入家族", "创建家族"}, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (i == 1) {
                                        UIHelper.showCrateFamilyActivity(getContext());
                                    } else {
                                        UIHelper.showFamilyList(getContext());
                                    }
                                }
                            }).show();
                        } else if (type == 2) {


                            UIHelper.showFamilyDetailActivity(getContext(), String.valueOf(fid), 2);
                        } else if (type == 1) {

                            UIHelper.showFamilyDetailActivity(getContext(), String.valueOf(fid), 1);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        sendRequestData();
    }


}
