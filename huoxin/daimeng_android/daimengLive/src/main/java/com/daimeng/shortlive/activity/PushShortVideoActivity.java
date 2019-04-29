package com.daimeng.shortlive.activity;

import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.daimeng.livee.AppConfig;
import com.daimeng.livee.AppContext;
import com.daimeng.livee.R;
import com.daimeng.livee.api.remote.ApiUtils;
import com.daimeng.livee.api.remote.PhoneLiveApi;
import com.daimeng.livee.base.BaseTitleActivity;
import com.daimeng.livee.bean.ConfigBean;
import com.daimeng.livee.utils.LiveUtils;
import com.daimeng.livee.utils.ShareUtils;
import com.daimeng.livee.utils.StringUtils;
import com.daimeng.livee.utils.TLog;
import com.litesuits.common.utils.InputMethodUtils;
import com.tencent.liteav.demo.common.utils.TCConstants;
import com.tencent.liteav.demo.videoupload.TXUGCPublish;
import com.tencent.liteav.demo.videoupload.TXUGCPublishTypeDef;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.BindView;
import butterknife.OnClick;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import okhttp3.Call;

public class PushShortVideoActivity extends BaseTitleActivity {

    @BindView(R.id.iv_cover)
    ImageView mIvCover;

    @BindView(R.id.et_video_title)
    EditText mEtVideoTitle;

    @BindView(R.id.iv_share_qq)
    ImageView mIvShareQQ;

    @BindView(R.id.iv_share_qzon)
    ImageView mIvShareQzon;

    @BindView(R.id.iv_share_wx)
    ImageView mIvShareWx;

    @BindView(R.id.iv_share_pyq)
    ImageView mIvSharePyq;

    private int mVideoSource; // 视频来源

    private String mVideoPath;
    private String mCoverImagePath;

    //视频时长（ms）
    private int mVideoDuration;
    //录制界面传过来的视频分辨率
    private int mVideoResolution;

    private String type = "";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_push_short_video;
    }

    @OnClick({R.id.iv_share_qq,R.id.iv_share_qzon,R.id.iv_share_wx,R.id.iv_share_pyq,R.id.layout_edit})
    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.iv_share_qq:

                type = QQ.NAME;
                mIvShareQQ.setImageResource(R.drawable.share_btn_qq_selected);
                mIvShareQzon.setImageResource(R.drawable.share_btn_qqzone_normal);
                mIvShareWx.setImageResource(R.drawable.share_btn_wechat_normal);
                mIvSharePyq.setImageResource(R.drawable.share_btn_moment_normal);
                break;

            case R.id.iv_share_qzon:

                type = QZone.NAME;
                mIvShareQzon.setImageResource(R.drawable.share_btn_qqzone_selected);
                mIvShareQQ.setImageResource(R.drawable.share_btn_qq_normal);
                mIvShareWx.setImageResource(R.drawable.share_btn_wechat_normal);
                mIvSharePyq.setImageResource(R.drawable.share_btn_moment_normal);
                break;

            case R.id.iv_share_wx:

                type = Wechat.NAME;
                mIvShareQQ.setImageResource(R.drawable.share_btn_qq_normal);
                mIvShareQzon.setImageResource(R.drawable.share_btn_qqzone_normal);
                mIvShareWx.setImageResource(R.drawable.share_btn_wechat_selected);
                mIvSharePyq.setImageResource(R.drawable.share_btn_moment_normal);
                break;

            case R.id.iv_share_pyq:

                type = WechatMoments.NAME;
                mIvShareQQ.setImageResource(R.drawable.share_btn_qq_normal);
                mIvShareQzon.setImageResource(R.drawable.share_btn_qqzone_normal);
                mIvShareWx.setImageResource(R.drawable.share_btn_wechat_normal);
                mIvSharePyq.setImageResource(R.drawable.share_btn_moment_selected);
                break;

            case R.id.layout_edit:

                mEtVideoTitle.requestFocus();

                InputMethodUtils.showSoftInput(this);
                break;

            default:
                break;
        }
    }

    @Override
    public void initView() {

        setActionBarTitle("分享");
        setActivityMoreText("发布");

        setActivityMoreOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                publish();
            }
        });

        ConfigBean config = LiveUtils.getConfigBean(this);
        if(StringUtils.toInt(config.is_open_wx_share) != 1){
            mIvSharePyq.setVisibility(View.GONE);
            mIvShareWx.setVisibility(View.GONE);
        }

        if(StringUtils.toInt(config.is_open_qq_share) != 1){
            mIvShareQQ.setVisibility(View.GONE);
            mIvShareQzon.setVisibility(View.GONE);
        }

    }

    @Override
    public void initData() {

        mVideoSource = getIntent().getIntExtra(TCConstants.VIDEO_RECORD_TYPE, TCConstants.VIDEO_RECORD_TYPE_EDIT);
        mVideoPath = getIntent().getStringExtra(TCConstants.VIDEO_RECORD_VIDEPATH);
        mCoverImagePath = getIntent().getStringExtra(TCConstants.VIDEO_RECORD_COVERPATH);
        mVideoDuration = getIntent().getIntExtra(TCConstants.VIDEO_RECORD_DURATION, 0);
        mVideoResolution = getIntent().getIntExtra(TCConstants.VIDEO_RECORD_RESOLUTION, -1);

        mIvCover.setImageBitmap(BitmapFactory.decodeFile(mCoverImagePath));

    }

    //添加短视频
    private void requestAddShortVideo(TXUGCPublishTypeDef.TXPublishResult result) {

        final String title = mEtVideoTitle.getText().toString();

        PhoneLiveApi.requestAddShortVideo(AppContext.address,AppContext.lat,AppContext.lng,title,result.videoURL,result.coverURL,result.videoId,mUserId,mUserToken,new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {

                hideWaitDialog();
            }

            @Override
            public void onResponse(String response, int id) {

                hideWaitDialog();
                String res = ApiUtils.checkIsSuccess2(response);
                if(res != null){

                    JSONObject data = JSON.parseObject(res);

                    if(!TextUtils.isEmpty(type)){
                        share(data.getString("cover_url"),data.getString("id"));
                    }
                    AppContext.showToast("发布成功");
                    finish();
                }
            }
        });


    }

    private void share(String cover_url,String vid){

        ConfigBean config = LiveUtils.getConfigBean(this);

        ShareUtils.share(this, type,config.share_title,config.share_des,
                cover_url,AppConfig.MAIN_URL + "/index.php?g=Live&m=Stream&a=index&id=" + vid,null);

    }

    private void publish() {

        /*if(!checkParams()){

            return;
        }*/

        //上传
        showWaitDialog(R.string.now_upload);

        //获取sign
        PhoneLiveApi.requestGetApiSing(mUserId,mUserToken,new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(final String response, int id) {

                String res = ApiUtils.checkIsSuccess2(response);
                if(res != null){

                    JSONObject obj = JSON.parseObject(res);
                    String sign = obj.getString("sign");

                    TXUGCPublish txugcPublish = new TXUGCPublish(getApplicationContext());
                    txugcPublish.setListener(new TXUGCPublishTypeDef.ITXVideoPublishListener() {
                        @Override
                        public void onPublishProgress(long uploadBytes, long totalBytes) {
                            TLog.log(totalBytes + "");

                        }

                        @Override
                        public void onPublishComplete(TXUGCPublishTypeDef.TXPublishResult result) {
                            TLog.log(result.retCode + "");

                            requestAddShortVideo(result);
                        }
                    });

                    TXUGCPublishTypeDef.TXPublishParam param = new TXUGCPublishTypeDef.TXPublishParam();
                    // signature计算规则可参考 https://www.qcloud.com/document/product/266/9221
                    param.signature = sign;
                    param.videoPath = mVideoPath;
                    param.coverPath = mCoverImagePath;
                    txugcPublish.publishVideo(param);
                }
            }
        });


        //finish();
    }

    private boolean checkParams() {

        String title = mEtVideoTitle.getText().toString();

        if(TextUtils.isEmpty(title)){
            AppContext.showToast("请输入标题");
            return false;
        }

        return true;
    }
}
