package com.daimeng.live.utils;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.alibaba.fastjson.JSON;
import com.daimeng.family.ui.CreateFamilyActivity;
import com.daimeng.family.ui.FamilyDetailActivity;
import com.daimeng.family.ui.FamilyEditActivity;
import com.daimeng.family.ui.FamilyListActivity;
import com.daimeng.family.ui.FamilyManageActivity;
import com.daimeng.family.ui.FamilyUserListActivity;
import com.daimeng.live.AppContext;
import com.daimeng.live.api.remote.ApiUtils;
import com.daimeng.live.api.remote.PhoneLiveApi;
import com.daimeng.live.bean.LiveCheckInfoBean;
import com.daimeng.live.bean.LiveBean;
import com.daimeng.live.bean.ShortVideoBean;
import com.daimeng.live.fragment.ManageListDialogFragment;
import com.daimeng.live.fragment.ShortVideoTouchPlayerFragment;
import com.daimeng.live.ui.AgentActivity;
import com.daimeng.live.ui.AgentProfitRecordActivity;
import com.daimeng.live.ui.AgentUserListActivity;
import com.daimeng.live.ui.BuyVipActivity;
import com.daimeng.live.ui.EditInfoActivity;
import com.daimeng.live.ui.LeftScrollVideoListActivity;
import com.daimeng.live.ui.NewsMessageActivity;
import com.daimeng.live.ui.SettingActivity;
import com.daimeng.live.ui.ShortVideoListActivity;
import com.daimeng.live.ui.ShortVideoPlayerActivity;
import com.daimeng.live.ui.ShortVideoPlayerTouchActivity;
import com.daimeng.live.ui.ShortVideoReplyCommentListActivity;
import com.daimeng.live.ui.ShortVideoReportActivity;
import com.daimeng.live.bean.PrivateChatUserBean;
import com.daimeng.live.bean.SimpleBackPage;
import com.daimeng.live.ui.SimpleUserInfoListActivity;
import com.daimeng.live.em.ChangInfo;
import com.daimeng.live.ui.ChangePassActivity;
import com.daimeng.live.ui.MobileFindPassActivity;
import com.daimeng.live.ui.MobileRegActivity;
import com.daimeng.live.ui.SettingPushLiveActivity;
import com.daimeng.live.ui.DedicateOrderActivity;
import com.daimeng.live.ui.ActionBarSimpleBackActivity;
import com.daimeng.live.ui.HomePageActivity;
import com.daimeng.live.ui.LiveRecordActivity;
import com.daimeng.live.ui.MobileLoginActivity;
import com.daimeng.live.ui.MainActivity;
import com.daimeng.live.ui.UserInfoDetailActivity;
import com.daimeng.live.ui.UserLevelActivity;
import com.daimeng.live.ui.UserProfitActivity;
import com.daimeng.live.ui.UserRechargeActivity;
import com.daimeng.live.ui.UserSelectAvatarActivity;
import com.daimeng.live.ui.SimpleBackActivity;
import com.daimeng.live.ui.RtmpPlayerActivity;
import com.daimeng.live.ui.WebViewActivity;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import okhttp3.Call;

/**
 * 界面帮助类
 */

public class UIHelper {
    /**
     * 发送通知广播
     *
     * @param context
     */
    public static void sendBroadcastForNotice(Context context) {
        /*Intent intent = new Intent(NoticeService.INTENT_ACTION_BROADCAST);
        context.sendBroadcast(intent);*/
    }
    /**
     * 手机登录
     *
     * @param context
     */

    public static void showMobilLogin(Context context) {
        Intent intent = new Intent(context, MobileLoginActivity.class);
        context.startActivity(intent);
    }

    /**
     * 手机密码注册
     *
     * @param context
     */

    public static void showMobileRegLogin(Context context) {
        Intent intent = new Intent(context, MobileRegActivity.class);
        context.startActivity(intent);
    }

    /**
     * 手机密码注册
     *
     * @param context
     */

    public static void showUserFindPass(Context context) {
        Intent intent = new Intent(context, MobileFindPassActivity.class);
        context.startActivity(intent);
    }
    /**
     * 登陆选择
     *
     * @param context
     */
    public static void showLoginSelectActivity(Context context) {
        /*Intent intent = new Intent(context, SelectLoginActivity.class);
        context.startActivity(intent);*/
        showMobilLogin(context);

    }

    /**
     * 首页
     *
     * @param context
     */
    public static void showMainActivity(Context context) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setClass(context, MainActivity.class);
        //Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);

    }
    /**
     * 我的详细资料
     *
     * @param context
     */
    public static void showMyInfoDetailActivity(Context context) {
        Intent intent = new Intent(context, UserInfoDetailActivity.class);
        context.startActivity(intent);
    }
    /**
     * 编辑资料
     *
     * @param context
     */
    public static void showEditInfoActivity(UserInfoDetailActivity context, String action,
                                            String prompt, String defaultStr, ChangInfo changInfo) {
        Intent intent = new Intent(context, EditInfoActivity.class);
        intent.putExtra(EditInfoActivity.EDITACTION,action);
        intent.putExtra(EditInfoActivity.EDITDEFAULT,defaultStr);
        intent.putExtra(EditInfoActivity.EDITPROMP,prompt);
        intent.putExtra(EditInfoActivity.EDITKEY, changInfo.getAction());
        context.startActivity(intent);
        //context.overridePendingTransition(R.anim.activity_open_start, 0);
    }

    public static void showSelectAvatar(UserInfoDetailActivity context, String avatar) {
        Intent intent = new Intent(context, UserSelectAvatarActivity.class);
        intent.putExtra("uhead",avatar);
        context.startActivity(intent);
        //context.overridePendingTransition(R.anim.activity_open_start, 0);
    }

    /**
     * 获取webviewClient对象
     *
     * @return
     */
    public static WebViewClient getWebViewClient() {

        return new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //showUrlRedirect(view.getContext(), url);
                return true;
            }
        };
    }

    /**
     * 我的等级
     *
     * @return
     */
    public static void showLevel(Context context) {
        Intent intent = new Intent(context, UserLevelActivity.class);
        context.startActivity(intent);
    }
    /**
     * 我的钻石
     *
     * @return
     */
    public static void showMyDiamonds(Context context) {
        Intent intent = new Intent(context, UserRechargeActivity.class);
        context.startActivity(intent);
    }
    /**
     * 我的收益
     *
     * @return
     */
    public static void showProfitActivity(Context context) {
        Intent intent = new Intent(context, UserProfitActivity.class);
        context.startActivity(intent);
    }
    /**
     * 设置
     *
     * @return
     */
    public static void showSetting(Context context) {
        Intent intent = new Intent(context, SettingActivity.class);
        context.startActivity(intent);
    }
    /**
     * 看直播
     *
     * @return
     */
    public static void showLookLiveActivity(Context context, LiveBean live, LiveCheckInfoBean data) {
        Intent intent = new Intent(context, RtmpPlayerActivity.class);

        Bundle bundle = new Bundle();
        bundle.putParcelable("check_info",data);
        bundle.putParcelable("USER_INFO", live);

        intent.putExtra(RtmpPlayerActivity.USER_INFO,bundle);
        context.startActivity(intent);
    }
    /**
     * 直播
     */
    public static void showRtmpPushActivity(Context context,int type) {
        Intent intent = new Intent(context, SettingPushLiveActivity.class);
        intent.putExtra("type",type);
        context.startActivity(intent);
    }
    /*
    * 其他用户个人信息
    * */
    public static void showHomePageActivity(Context context,String id) {
        Intent intent = new Intent(context, HomePageActivity.class);
        intent.putExtra("uid",id);
        context.startActivity(intent);
    }
    /*
    * 粉丝列表
    * */
    public static void showFansListActivity(Context context, String uid) {
        Intent intent = new Intent(context, SimpleUserInfoListActivity.class);
        intent.putExtra("uid",uid);
        intent.putExtra("type",1);
        context.startActivity(intent);
    }
    /*
    * 关注列表
    * */
    public static void showAttentionActivity(Context context, String uid) {
        Intent intent = new Intent(context, SimpleUserInfoListActivity.class);
        intent.putExtra("uid",uid);
        intent.putExtra("type",0);
        context.startActivity(intent);
    }
    //魅力值贡献榜
    public static void showDedicateOrderActivity(Context context, String uid) {

        Intent intent = new Intent(context, DedicateOrderActivity.class);
        intent.putExtra("uid",uid);
        context.startActivity(intent);
    }
    //直播记录
    public static void showLiveRecordActivity(Context context, String uid) {
        Intent intent = new Intent(context, LiveRecordActivity.class);
        intent.putExtra("uid",uid);
        context.startActivity(intent);
    }
    //私信页面
    public static void showPrivateChatSimple(Context context, String uid) {
        Intent intent = new Intent(context, SimpleBackActivity.class);
        intent.putExtra("uid",uid);
        intent.putExtra(SimpleBackActivity.BUNDLE_KEY_PAGE, SimpleBackPage.USER_PRIVATECORE.getValue());
        context.startActivity(intent);
    }
    //私信详情
    public static void showPrivateChatMessage(Context context, PrivateChatUserBean user) {
        Intent intent = new Intent(context, SimpleBackActivity.class);
        intent.putExtra("user",user);
        intent.putExtra(SimpleBackActivity.BUNDLE_KEY_PAGE, SimpleBackPage.USER_PRIVATECORE_MESSAGE.getValue());
        context.startActivity(intent);

    }
    //地区选择
    public static void showSelectArea(Context context) {
        Intent intent = new Intent(context,SimpleBackActivity.class);
        intent.putExtra(SimpleBackActivity.BUNDLE_KEY_PAGE,SimpleBackPage.AREA_SELECT.getValue());
        context.startActivity(intent);
    }
    //搜索
    public static void showScreen(Context context) {
        Intent intent = new Intent(context,SimpleBackActivity.class);
        intent.putExtra(SimpleBackActivity.BUNDLE_KEY_PAGE,SimpleBackPage.INDEX_SECREEN.getValue());
        context.startActivity(intent);
    }
    //打开网页
    public static void showWebView(Context context,String url, String title) {
        Intent intent = new Intent(context, WebViewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("url",url);
        bundle.putString("title",title);
        intent.putExtra("URL_INFO",bundle);
        context.startActivity(intent);
    }
    //黑名单
    public static void showBlackList(Context context) {
        Intent intent = new Intent(context,ActionBarSimpleBackActivity.class);
        intent.putExtra(ActionBarSimpleBackActivity.BUNDLE_KEY_PAGE,SimpleBackPage.USER_BLACK_LIST.getValue());
        context.startActivity(intent);
    }
    //推送管理
    public static void showPushManage(Context context) {
        Intent intent = new Intent(context,ActionBarSimpleBackActivity.class);
        intent.putExtra(ActionBarSimpleBackActivity.BUNDLE_KEY_PAGE,SimpleBackPage.USER_PUSH_MANAGE.getValue());
        context.startActivity(intent);
    }
    //搜索歌曲
    public static void showSearchMusic(Activity context) {
        Intent intent = new Intent(context,SimpleBackActivity.class);
        intent.putExtra(SimpleBackActivity.BUNDLE_KEY_PAGE,SimpleBackPage.LIVE_START_MUSIC.getValue());
        context.startActivityForResult(intent,1);
    }
    //管理员列表
    public static void shoManageListActivity(Context context) {
        Intent intent = new Intent(context,ManageListDialogFragment.class);
        context.startActivity(intent);
    }

    public static void showChangePassActivity(Context context) {
        Intent intent = new Intent(context, ChangePassActivity.class);
        context.startActivity(intent);
    }

    //进入直播间
    public static void startRtmpPlayerActivity(final Context context,final LiveBean live){

        PhoneLiveApi.checkoutRoom(AppContext.getInstance().getLoginUid()
                ,AppContext.getInstance().getToken(),live.stream,live.uid, new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {

                        JSONArray res = ApiUtils.checkIsSuccess(response);
                        if (res != null) {
                            try {
                                final LiveCheckInfoBean data = JSON.parseObject(res.getString(0),LiveCheckInfoBean.class);
                                data.setLive_info(live);
                                LiveUtils.joinLiveRoom(context,data);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });



    }

    public static void showCrateFamilyActivity(Context context) {

        Intent intent = new Intent(context, CreateFamilyActivity.class);
        context.startActivity(intent);
    }

    public static void showFamilyList(Context context) {

        Intent intent = new Intent(context, FamilyListActivity.class);
        context.startActivity(intent);
    }

    public static void showFamilyDetailActivity(Context context, String fid,int action ) {

        Intent intent = new Intent(context, FamilyDetailActivity.class);
        intent.putExtra("fid",fid);
        intent.putExtra("action",action);
        context.startActivity(intent);
    }

    public static void showFamilyManageActivity(Context context) {
        Intent intent = new Intent(context, FamilyManageActivity.class);
        context.startActivity(intent);
    }

    public static void showFamilyUserListActivity(Context context, String fid) {
        Intent intent = new Intent(context, FamilyUserListActivity.class);
        intent.putExtra("fid",fid);
        context.startActivity(intent);
    }

    public static void showFamilyEditActivity(Context context) {

        Intent intent = new Intent(context, FamilyEditActivity.class);
        context.startActivity(intent);
    }

    public static void showBuyVipActivity(Context context) {

        Intent intent = new Intent(context, BuyVipActivity.class);
        context.startActivity(intent);
    }

    public static void showAgentProfitRecord(Context context) {

        Intent intent = new Intent(context, AgentProfitRecordActivity.class);
        context.startActivity(intent);
    }

    public static void showAgentActivity(Context context) {

        Intent intent = new Intent(context, AgentActivity.class);
        context.startActivity(intent);
    }

    public static void showAgentUserActivity(Context context) {

        Intent intent = new Intent(context, AgentUserListActivity.class);
        context.startActivity(intent);
    }

    public static void showShortVideoList(Context context) {

        Intent intent = new Intent(context, ShortVideoListActivity.class);
        context.startActivity(intent);
    }

    public static void showShortVideoPlayer(Activity context, ArrayList<ShortVideoBean> list, int pos,int page) {

        Intent intent = new Intent(context, ShortVideoPlayerTouchActivity.class);
        intent.putExtra(ShortVideoPlayerTouchActivity.VIDEO_LIST,list);
        intent.putExtra(ShortVideoPlayerTouchActivity.VIDEO_POS,pos);
        intent.putExtra(ShortVideoPlayerTouchActivity.VIDEO_LIST_PAGE,page);
        context.startActivity(intent);
        //context.overridePendingTransition(R.anim.slide_in_top,R.anim.slide_in_top);
    }

    public static void showShortVideoPlayerLeftTouchCenter(Activity context, ArrayList<ShortVideoBean> list, int pos,int page) {

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(ShortVideoTouchPlayerFragment.VIDEO_LIST,list);
        bundle.putInt(ShortVideoTouchPlayerFragment.VIDEO_POS,pos);
        bundle.putInt(ShortVideoTouchPlayerFragment.VIDEO_LIST_PAGE,page);
        Intent intent = new Intent(context, LeftScrollVideoListActivity.class);
        intent.putExtra(LeftScrollVideoListActivity.VIDEO_PLAYER_INFO,bundle);
        context.startActivity(intent);
        //context.overridePendingTransition(R.anim.slide_in_top,R.anim.slide_in_top);
    }

    public static void showShortVideoPlayer(Activity context, ShortVideoBean shortVideoBean) {

        Intent intent = new Intent(context, ShortVideoPlayerActivity.class);
        intent.putExtra(ShortVideoPlayerActivity.VIDEO_DATA,shortVideoBean);
        context.startActivity(intent);
        //context.overridePendingTransition(R.anim.slide_in_top,R.anim.slide_in_top);
    }


    public static void showShortVideoReport(Context context,String vid) {
        Intent intent = new Intent(context, ShortVideoReportActivity.class);
        intent.putExtra(ShortVideoReportActivity.SHORT_VIDEO_ID,vid);
        context.startActivity(intent);
    }

    public static void showShortVideoReplyCommentListActivity(Context context,String rid) {

        Intent intent = new Intent(context, ShortVideoReplyCommentListActivity.class);
        intent.putExtra(ShortVideoReplyCommentListActivity.RID,rid);
        context.startActivity(intent);
    }

    public static void showNewsMessageActivity(Context context) {

        Intent intent = new Intent(context, NewsMessageActivity.class);
        context.startActivity(intent);
    }
}
