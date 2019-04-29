package com.daimeng.livee.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimeng.livee.utils.SimpleUtils;
import com.daimeng.livee.widget.CircleImageView;
import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.tandong.bottomview.view.BottomView;
import com.daimeng.livee.AppContext;
import com.daimeng.livee.R;
import com.daimeng.livee.api.remote.ApiUtils;
import com.daimeng.livee.api.remote.PhoneLiveApi;
import com.daimeng.livee.base.ShowLiveActivityBase;
import com.daimeng.livee.bean.PrivateChatUserBean;
import com.daimeng.livee.bean.SimpleUserInfo;
import com.daimeng.livee.bean.UserBean;
import com.daimeng.livee.event.Event;
import com.daimeng.livee.im.IMControl;
import com.daimeng.livee.utils.LiveUtils;
import com.daimeng.livee.utils.UIHelper;
import com.daimeng.livee.ui.customviews.BottomMenuView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.BindView;
import okhttp3.Call;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

/**
 * 直播间用户列表点击弹窗页面
 */
public class UserInfoDialogFragment extends DialogFragment {

    public   AtClickHelp   callback;
    public interface AtClickHelp {
        void onClick(String name);
    }

    public AtClickHelp getCallback() {
        return callback;
    }

    public void setCallback(AtClickHelp callback) {
        this.callback = callback;
    }

    private UserBean mUser;
    private SimpleUserInfo mToUser;
    private String mRoomNum;
    private IMControl mIMControl;

    @BindView(R.id.tv_show_dialog_u_fllow_num)
    TextView mTvFollowNum;

    @BindView(R.id.tv_show_dialog_u_fans_num)
    TextView mTvFansNum;

    @BindView(R.id.tv_show_dialog_u_ticket)
    TextView mTvTicketNum;

    @BindView(R.id.tv_show_dialog_u_follow_btn)
    TextView mTvFollowBtn;

    @BindView(R.id.tv_live_manage_or_report)
    TextView mTvReportBtn;

    @BindView(R.id.iv_dialog_setting)
    ImageView mIvSetting;

    @BindView(R.id.tv_user_info_id)
    TextView mTvId;

    //只有主页的菜单
    @BindView(R.id.ll_user_info_dialog2)
    LinearLayout mLLUserInfoDialogBottomMenuOwn;

    //私信,关注,主页的菜单
    @BindView(R.id.ll_user_info_dialog)
    LinearLayout mLLUserInfoDialogBottomMenu;

    @BindView(R.id.iv_show_dialog_level)
    ImageView mIvLevel;

    @BindView(R.id.tv_show_dialog_u_address)
    TextView mTvAddress;

    //签名
    @BindView(R.id.tv_user_info_sign)
    TextView mTvSign;

    private int action;
    private BottomView mManageMenu;
    private CircleImageView mAvatar;
    private TextView mTvUname;
    private ImageView mIvUserSex;

    public static final String MY_USER_INFO = "MY_USER_INFO";
    public static final String TO_USER_INFO = "TO_USER_INFO";
    public static final String ROOM_NUM = "ROOM_NUM";


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = new Dialog(getActivity(), R.style.dialog);
        dialog.getWindow().setBackgroundDrawable(new
                ColorDrawable(Color.TRANSPARENT));
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_user_info_dialog, null);
        dialog.setContentView(view);

        ButterKnife.bind(this, view);
        initView(view);
        initData();
        return dialog;
    }

    private void initData() {


        mTvId.setText("ID:" + mToUser.id);
        //获取用户详细信息
        PhoneLiveApi.getUserInfo(mUser.id,mToUser.id,mRoomNum, new StringCallback() {
            @Override
            public void onError(Call call, Exception e,int id) {
            }
            @Override
            public void onResponse(String response,int id) {
                JSONArray res = ApiUtils.checkIsSuccess(response);
                if(res != null){
                    try {
                        JSONObject data = res.getJSONObject(0);

                        mTvFollowNum.setText(data.getString("follows"));
                        mTvFansNum.setText(data.getString("fans"));
                        //mTvSendNum.setText(  "送出:  " + data.getString("consumption"));
                        mTvTicketNum.setText(data.getString("votestotal"));
                        mTvSign.setText(data.getString("signature"));
                        //等级
//                        mIvLevel.setImageResource(LiveUtils.getLevelRes(data.getString("level")));
                        //财富等级

                        JSONObject da = data.getJSONObject("wealth");
                          if(null!=da.getString("custombackground")&&!da.getString("custombackground").equals("")){
                              SimpleUtils.loadImageForView(AppContext.getInstance(),mIvLevel, da.getString("custombackground"), 0);
                          }


                        mTvAddress.setText(data.getString("city"));

                        SimpleUtils.loadImageForView(AppContext.getInstance(),mAvatar,data.getString("avatar_thumb"),R.drawable.default_pic);
                        mTvUname.setText(data.getString("user_nicename"));
                        mIvUserSex.setImageResource(LiveUtils.getSexRes(data.getString("sex")));

                        if (data.getInt("isattention") == 0 && isAdded()) {
                            mTvFollowBtn.setText("关注");
                            mTvFollowBtn.setEnabled(true);
                        } else {
                            mTvFollowBtn.setText("已关注");
                            mTvFollowBtn.setEnabled(false);
                            mTvFollowBtn.setTextColor(getResources().getColor(R.color.gray));
                        }


                        action = data.getInt("action");
                        switch (action){
                            //自己
                            case 0:
                                mTvReportBtn.setVisibility(View.GONE);
                                mIvSetting.setVisibility(View.GONE);
                                break;
                            //普通其他用户
                            case 30:
                                mTvReportBtn.setVisibility(View.VISIBLE);
                                mIvSetting.setVisibility(View.GONE);
                                break;
                            //自己是管理员
                            case 40:
                                mTvReportBtn.setVisibility(View.GONE);
                                mIvSetting.setVisibility(View.VISIBLE);
                                break;
                            //超管管理主播
                            case 60:
                                mTvReportBtn.setVisibility(View.GONE);
                                mIvSetting.setVisibility(View.VISIBLE);
                                break;
                            case 501:
                            case 502:
                                mTvReportBtn.setVisibility(View.GONE);
                                mIvSetting.setVisibility(View.VISIBLE);
                                break;
                            default:
                                break;

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    //举报弹窗
    private void showReportAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("提示");
        builder.setMessage("确定举报?");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                PhoneLiveApi.report(mUser.id,mUser.token, mToUser.id);
                AppContext.showToast(getString(R.string.reportsuccess));
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }

    private void initView(final View view) {
        mUser    = getArguments().getParcelable(MY_USER_INFO);
        mToUser  = getArguments().getParcelable(TO_USER_INFO);
        mRoomNum = getArguments().getString(ROOM_NUM);
        try {

            mIMControl = ((ShowLiveActivityBase) getActivity()).mIMControl;
        }catch (Exception e){

        }


        //是否是自己点击弹窗
        if (mUser.id.equals(mToUser.id)) {
            mTvFollowBtn.setEnabled(false);
            //切换底部菜单
            mLLUserInfoDialogBottomMenuOwn.setVisibility(View.VISIBLE);
            mLLUserInfoDialogBottomMenu.setVisibility(View.GONE);
        }

        view.findViewById(R.id.tv_show_dialog_u_private_chat_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showPrivateMessage((ShowLiveActivityBase) getActivity(), mUser.id, mToUser.id);
            }
        });


        //主页
        view.findViewById(R.id.tv_show_dialog_u_home_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showHomePageActivity(getActivity(), mToUser.id);
                dismiss();
            }
        });
        //
        view.findViewById(R.id.tv_show_dialog_at_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //UIHelper.showHomePageActivity(getActivity(), mToUser.id);
                dismiss();
                callback.onClick(mToUser.user_nicename);
            }
        });

        //主页
        view.findViewById(R.id.tv_show_dialog_u_home_btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showHomePageActivity(getActivity(), mToUser.id);
                dismiss();
            }
        });

        mAvatar = ((CircleImageView) view.findViewById(R.id.av_show_dialog_u_head));
        //mAvatar.setAvatarUrl(mToUser.avatar);
        mTvUname = (TextView) view.findViewById(R.id.tv_show_dialog_u_name);
        mIvUserSex = (ImageView) view.findViewById(R.id.iv_show_dialog_sex);




        mIvSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showManageBottomMenu();
                //dismiss();
            }
        });
        mTvReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReportAlert();
            }
        });


        mTvFollowBtn.setEnabled(false);
        mTvFollowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    EMClient.getInstance().contactManager().removeUserFromBlackList(String.valueOf(mToUser.id));
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
                PhoneLiveApi.showFollow(AppContext.getInstance().getLoginUid(), mToUser.id,AppContext.getInstance().getToken(), null);
                mTvFollowBtn.setEnabled(false);
                mTvFollowBtn.setTextColor(getResources().getColor(R.color.gray));
                mTvFollowBtn.setText(getString(R.string.alreadyfollow));

                if(mToUser.id.equals(mRoomNum)) {
                    Event.VideoEvent event = new Event.VideoEvent();
                    event.action = 1;
                    EventBus.getDefault().post(event);
                }
            }
        });
    }

    //显示管理弹窗
    public void showManageBottomMenu() {
        BottomMenuView mBottomMenuView =  new BottomMenuView(getActivity());
        mManageMenu = new BottomView(getActivity(), R.style.BottomViewTheme_Transparent, mBottomMenuView);
        mBottomMenuView.setOptionData(action, mManageMenu);
        mBottomMenuView.setIsEmcee(mUser.id.equals(mRoomNum));
        mManageMenu.setAnimation(R.style.BottomToTopAnim);
        mManageMenu.showBottomView(false);
    }

    //跳转私信
    public void showPrivateMessage(final ShowLiveActivityBase activity, String uid, String touid) {

        PhoneLiveApi.getPmUserInfo(uid, touid, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                JSONArray res = ApiUtils.checkIsSuccess(response);
                if (null == res) return;
                //UIHelper.showPrivateChatMessage(activity,new Gson().fromJson(res,PrivateChatUserBean.class));
                PrivateChatUserBean chatUserBean = null;
                try {
                    chatUserBean = new Gson().fromJson(res.getString(0), PrivateChatUserBean.class);
                    MessageDetailDialogFragment messageFragment = new MessageDetailDialogFragment();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("user", chatUserBean);
                    messageFragment.setArguments(bundle);
                    //messageFragment.setStyle(MessageDetailDialogFragment.STYLE_NO_TITLE,0);
                    messageFragment.show(activity.getSupportFragmentManager(), "MessageDetailDialogFragment");
                    dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag("getPmUserInfo");
        OkHttpUtils.getInstance().cancelTag("getUserInfo");
        OkHttpUtils.getInstance().cancelTag("getIsFollow");
        OkHttpUtils.getInstance().cancelTag("report");
        OkHttpUtils.getInstance().cancelTag("setCloseLive");
        OkHttpUtils.getInstance().cancelTag("setKick");
        OkHttpUtils.getInstance().cancelTag("setShutUp");
    }


    /**
     * @dw 设置禁言
     * */
    public void setShutUp(){

        PhoneLiveApi.setShutUp(
                mRoomNum,
                mToUser.id,
                mUser.id,
                mUser.token,
                new StringCallback(){

                    @Override
                    public void onError(Call call, Exception e,int id) {

                    }

                    @Override
                    public void onResponse(String response,int id) {
                        JSONArray res = ApiUtils.checkIsSuccess(response);

                        if(null == res) return;
                        mIMControl.doSetShutUp(mUser,mToUser);
                    }
                });
    }

    //设置踢人
    private void setKick() {
        PhoneLiveApi.setKick(
                mRoomNum,
                mToUser.id,
                mUser.id,
                mUser.token,
                new StringCallback(){

                    @Override
                    public void onError(Call call, Exception e,int id) {

                    }

                    @Override
                    public void onResponse(String response,int id) {
                        JSONArray res = ApiUtils.checkIsSuccess(response);

                        if(null == res) return;
                        mIMControl.doSetKick(mUser,mToUser);
                    }
                });
    }

    //超管关闭直播
    private void setCloseLive(String type) {
        PhoneLiveApi.setCloseLive(mUser.id,mUser.token,mToUser.id,type,new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                JSONArray res = ApiUtils.checkIsSuccess(response);

                if(null == res) return;
                mIMControl.doSetCloseLive();
            }
        });
    }


    //设置管理员
    private void setManage() {

        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                PhoneLiveApi.setManage(
                        mRoomNum,
                        mToUser.id,
                        mUser.token,
                        mUser.id,
                        new StringCallback(){

                            @Override
                            public void onError(Call call, Exception e,int id) {
                                Toast.makeText(getContext(),"操作失败",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onResponse(String response,int id) {
                                JSONArray res = ApiUtils.checkIsSuccess(response);
                                if(null == res) return;
                                subscriber.onNext("");
                                mManageMenu.dismissBottomView();

                            }
                        });
            }
        });
        observable.subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                if(action == 502){
                    action = 501;
                    mIMControl.doSetOrRemoveManage(mUser,mToUser,mToUser.user_nicename + "被删除管理员");
                }else{
                    action = 502;
                    mIMControl.doSetOrRemoveManage(mUser,mToUser,mToUser.user_nicename + "被设为管理员");
                }

            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event.DialogEvent event) {

        mManageMenu.dismissBottomView();
        if(event.action == 1){

            setShutUp();

        }else if(event.action == 0){

            setManage();

        }else if(event.action == 3){
            //踢人
            setKick();
        }else if(event.action == 2){
            //关闭直播
            setCloseLive("0");

        }else if(event.action == 4){
            //禁用
            setCloseLive("1");
        }else if(event.action == 5){

            ManageListDialogFragment fragment = new ManageListDialogFragment();
            fragment.setStyle(ManageListDialogFragment.STYLE_NO_TITLE,0);
            fragment.show(getActivity().getSupportFragmentManager(),"ManageListDialogFragment");
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
}
