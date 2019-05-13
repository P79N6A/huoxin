package com.daimeng.live.ui;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.daimeng.live.api.remote.ApiUtils;
import com.daimeng.live.bean.ConfigBean;
import com.daimeng.live.bean.LiveStartSettingParamBean;
import com.daimeng.live.bean.UserBean;
import com.daimeng.live.interf.DialogInterface;
import com.daimeng.live.dialog.LiveCommon;
import com.daimeng.live.utils.InputMethodUtils;
import com.daimeng.live.AppContext;
import com.daimeng.live.R;
import com.daimeng.live.api.remote.PhoneLiveApi;
import com.daimeng.live.base.BaseActivity;
import com.daimeng.live.utils.LiveUtils;
import com.daimeng.live.utils.ShareUtils;
import com.daimeng.live.utils.StringUtils;
import com.pizidea.imagepicker.AndroidImagePicker;
import com.pizidea.imagepicker.bean.ImageItem;
import com.zhouwei.blurlibrary.EasyBlur;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;


public class SettingPushLiveActivity extends BaseActivity {
    //填写直播标题
    @BindView(R.id.et_start_live_title)
    EditText mStartLiveTitle;

    //开始直播遮罩层
    @BindView(R.id.rl_start_live_bg)
    RelativeLayout mStartLiveBg;

    @BindView(R.id.iv_bg)
    ImageView mIvBg;

    //开始直播btn
    @BindView(R.id.btn_start_live)
    Button mBtnStartLive;

    @BindView(R.id.iv_live_select_pic)
    ImageView mIvLivePic;

    @BindView(R.id.tv_live_type)
    TextView mTvLiveType;

    //分享模式 7为不分享任何平台
    private int shareType = 7;
    private UserBean mUser;

    private boolean isFrontCameraMirro = false;
    private boolean isClickStartLive = false;

    private String type = "0";
    private String type_val = "";
    private Bitmap mLivePic;
    private File mFileLivePic;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ready_start_live;
    }

    @Override
    public void initView() {

        findViewById(R.id.iv_live_share_weibo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLiveShare(v,0);
                shareType = 0 == shareType?7:0;
            }
        });
        findViewById(R.id.iv_live_share_timeline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLiveShare(v,2);
                shareType = 2 == shareType?7:2;
            }
        });
        findViewById(R.id.iv_live_share_wechat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLiveShare(v,1);
                shareType = 1 == shareType?7:1;
            }
        });

        findViewById(R.id.iv_live_share_qq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLiveShare(v,3);
                shareType = 3 == shareType?7:3;
            }
        });
        findViewById(R.id.iv_live_share_qqzone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLiveShare(v,4);
                shareType = 4 == shareType?7:4;
            }
        });

        ConfigBean config = LiveUtils.getConfigBean(this);
        if(StringUtils.toInt(config.is_open_wx_share) != 1){
            findViewById(R.id.iv_live_share_wechat).setVisibility(View.GONE);
            findViewById(R.id.iv_live_share_timeline).setVisibility(View.GONE);
        }

        if(StringUtils.toInt(config.is_open_wb_share) != 1){
            findViewById(R.id.iv_live_share_weibo).setVisibility(View.GONE);
        }

        if(StringUtils.toInt(config.is_open_qq_share) != 1){
            findViewById(R.id.iv_live_share_qq).setVisibility(View.GONE);
            findViewById(R.id.iv_live_share_qqzone).setVisibility(View.GONE);
        }


        Bitmap finalBitmap = EasyBlur.with(this)
                .bitmap(BitmapFactory.decodeResource(getResources(),R.drawable.main_bkg)) //要模糊的图片
                .radius(10)//模糊半径
                .blur();
        mIvBg.setImageBitmap(finalBitmap);

    }

    @Override
    public void initData() {

        mUser = AppContext.getInstance().getLoginUser();
    }

    @OnClick({R.id.tv_live_type,R.id.iv_live_exit,R.id.btn_start_live,R.id.iv_live_select_pic})
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_start_live://创建房间
                //请求服务端存储记录
                createRoom();
                break;
            case R.id.iv_live_exit:
                finish();
                break;
            case R.id.iv_live_select_pic:

                clickSelectPic();
                break;
            case R.id.tv_live_type:

                clickLiveType();
                break;
            default:
                break;
        }
    }

    //选择封面
    private void clickSelectPic() {
        AndroidImagePicker.getInstance().setSelectLimit(1);
        //multi select
        AndroidImagePicker.getInstance().pickMulti(SettingPushLiveActivity.this, true, new AndroidImagePicker.OnImagePickCompleteListener() {
            @Override
            public void onImagePickComplete(List<ImageItem> items) {
                if(items != null && items.size() > 0){
                    mLivePic = BitmapFactory.decodeFile(items.get(0).path);
                    mFileLivePic = new File(items.get(0).path);

                    mIvLivePic.setImageBitmap(mLivePic);
                }
            }
        });
    }

    //选择房间类型
    private void clickLiveType() {

        new AlertView("选择房间类型", null, "取消", null,
                new String[]{"普通房间", "密码房间","按场付费","按时付费"},
                this, AlertView.Style.ActionSheet, new OnItemClickListener(){
            public void onItemClick(Object o,int position){

                if(position == 1){
                    showInputPwdDialog();
                }else if(position == 2){
                    showMoneyDialog();
                }else if(position == 3){
                    showTimeMoneyDialog();
                }else{
                    mTvLiveType.setText("普通房间");
                    type = "0";
                }

            }
        }).show();

    }

    //按时付费房间
    private void showTimeMoneyDialog() {
        LiveCommon.showInputContentDialog(SettingPushLiveActivity.this, "设置分钟收费金额"
                , new DialogInterface() {
                    @Override
                    public void cancelDialog(View v, Dialog d) {
                        d.dismiss();
                    }

                    @Override
                    public void determineDialog(View v, Dialog d) {
                        EditText e = (EditText) d.findViewById(R.id.et_input);
                        if(TextUtils.isEmpty(e.getText().toString())){
                            AppContext.showToast("金额不能为空",0);
                            return;
                        }
                        mTvLiveType.setText("分钟收费");
                        type = "3";
                        type_val = e.getText().toString();
                        d.dismiss();
                    }
                });
    }

    //输入密码弹窗
    private void showInputPwdDialog() {

        LiveCommon.showInputContentDialog(SettingPushLiveActivity.this, "设置密码"
                        , new DialogInterface() {
                            @Override
                            public void cancelDialog(View v, Dialog d) {
                                d.dismiss();
                            }

                            @Override
                            public void determineDialog(View v, Dialog d) {

                                EditText e = (EditText) d.findViewById(R.id.et_input);
                                if(TextUtils.isEmpty(e.getText().toString())){
                                    AppContext.showToast("密码不能为空",0);
                                    return;
                                }
                                mTvLiveType.setText("密码房间");
                                type = "1";
                                type_val = e.getText().toString();
                                d.dismiss();
                            }
                        });
    }

    //输入金额弹窗
    private void showMoneyDialog(){
        LiveCommon.showInputContentDialog(SettingPushLiveActivity.this, "设置收费金额"
                        , new DialogInterface() {
                            @Override
                            public void cancelDialog(View v, Dialog d) {
                                d.dismiss();
                            }

                            @Override
                            public void determineDialog(View v, Dialog d) {
                                EditText e = (EditText) d.findViewById(R.id.et_input);
                                if(TextUtils.isEmpty(e.getText().toString())){
                                    AppContext.showToast("金额不能为空",0);
                                    return;
                                }
                                mTvLiveType.setText("按场付费");
                                type = "2";
                                type_val = e.getText().toString();
                                d.dismiss();
                            }
                        });
    }

    /**
     * @dw 创建直播房间
     * 请求服务端添加直播记录,分享直播
     * */
    private void createRoom() {

        if(shareType != 7){

            ShareUtils.share(SettingPushLiveActivity.this, shareType, mUser,null);
        }else {
            readyStart();
        }
        isClickStartLive = true;
        InputMethodUtils.closeSoftKeyboard(this);
        mBtnStartLive.setTextColor(getResources().getColor(R.color.white));
    }
    /**
     * @dw 准备直播
     * */
    private void readyStart() {

        //请求服务端
        PhoneLiveApi.createLive(
                mUser.id,
                mUser.avatar,
                mUser.avatar_thumb,
                StringUtils.getNewString(mStartLiveTitle.getText().toString()),
                mUser.token ,
                mUser.user_nicename,
                mFileLivePic,
                type,
                type_val,
                new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e,int id) {
                        AppContext.showToast("开启直播失败,请退出重试");
                    }

                    @Override
                    public void onResponse(String s,int id) {
                        JSONArray res = ApiUtils.checkIsSuccess(s);
                        if(res != null){
                            try {
                                LiveStartSettingParamBean data = JSON.parseObject(res.getString(0),LiveStartSettingParamBean.class);
                                data.setType(type);
                                data.setFrontCameraMirro(isFrontCameraMirro);
                                RtmpPushActivity.rtmpPushActivity(SettingPushLiveActivity.this,data);

                                finish();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isClickStartLive && shareType != 7){
            readyStart();
        }
    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }

    /**
     * @dw 开始直播分享
     * @param v 点击按钮
     * @param type 分享平台
     * */
    private void startLiveShare(View v,int type){
        String titleStr = "";
        if(type == shareType){
            String titlesClose[] = getResources().getStringArray(R.array.live_start_share_close);
            titleStr = titlesClose[type];
        }else {
            String titlesOpen[] = getResources().getStringArray(R.array.live_start_share_open);
            titleStr = titlesOpen[type];
        }

        View popView  =  getLayoutInflater().inflate(R.layout.pop_view_share_start_live,null);
        TextView title = (TextView) popView.findViewById(R.id.tv_pop_share_start_live_prompt);
        title.setText(titleStr);
        PopupWindow pop = new PopupWindow(popView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,true);
        popView.measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setOutsideTouchable(true);
        int location[] = new int[2];
        v.getLocationOnScreen(location);
        pop.setFocusable(false);

        pop.showAtLocation(v, Gravity.NO_GRAVITY,location[0] + v.getWidth()/2 - popView.getMeasuredWidth()/2,location[1]- popView.getMeasuredHeight());

    }


}