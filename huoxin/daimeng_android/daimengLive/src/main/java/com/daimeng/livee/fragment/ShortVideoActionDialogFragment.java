package com.daimeng.livee.fragment;


import android.content.DialogInterface;
import android.view.View;
import android.widget.LinearLayout;

import com.daimeng.livee.AppConfig;
import com.daimeng.livee.AppContext;
import com.daimeng.livee.R;
import com.daimeng.livee.api.remote.ApiUtils;
import com.daimeng.livee.api.remote.PhoneLiveApi;
import com.daimeng.livee.base.BaseBottomDialogFragment;
import com.daimeng.livee.bean.ConfigBean;
import com.daimeng.livee.bean.ShortVideoBean;
import com.daimeng.livee.utils.DialogHelp;
import com.daimeng.livee.utils.LiveUtils;
import com.daimeng.livee.utils.ShareUtils;
import com.daimeng.livee.utils.StringUtils;
import com.daimeng.livee.utils.TDevice;
import com.daimeng.livee.utils.UIHelper;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import okhttp3.Call;

/**
 * 短视频操作
 */
public class ShortVideoActionDialogFragment extends BaseBottomDialogFragment implements View.OnClickListener {

    public static final String SHORT_VIDEO_INFO = "SHORT_VIDEO_INFO";

    private LinearLayout mDelReport;
    private LinearLayout mCopyUrl;
    private LinearLayout mLlDel;

    private ShortVideoBean mShortVideoInfo;

    @Override
    public int getLayout() {
        return R.layout.fragment_share_dialog;
    }

    @Override
    public void initData() {

        mShortVideoInfo = getArguments().getParcelable(SHORT_VIDEO_INFO);

        //显示删除按钮
        if(mShortVideoInfo.getUid().equals(AppContext.getInstance().getLoginUid())){
            mLlDel.setVisibility(View.VISIBLE);
        }else{
            mLlDel.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void initView(View view) {


        mDelReport = (LinearLayout) view.findViewById(R.id.ll_report);
        mLlDel = (LinearLayout) view.findViewById(R.id.ll_del);
        mCopyUrl = (LinearLayout) view.findViewById(R.id.ll_copy_url);
        view.findViewById(R.id.layout_share_wx).setOnClickListener(this);
        view.findViewById(R.id.layout_share_pyq).setOnClickListener(this);
        view.findViewById(R.id.layout_share_qq).setOnClickListener(this);
        view.findViewById(R.id.layout_share_qzon).setOnClickListener(this);

        ConfigBean config = LiveUtils.getConfigBean(getContext());
        if(StringUtils.toInt(config.is_open_qq_share) == 1){
            view.findViewById(R.id.layout_share_qq).setVisibility(View.VISIBLE);
            view.findViewById(R.id.layout_share_qzon).setVisibility(View.VISIBLE);
        }
        if(StringUtils.toInt(config.is_open_wx_share) == 1){
            view.findViewById(R.id.layout_share_wx).setVisibility(View.VISIBLE);
            view.findViewById(R.id.layout_share_pyq).setVisibility(View.VISIBLE);
        }


        mDelReport.setOnClickListener(this);
        mLlDel.setOnClickListener(this);
        mCopyUrl.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.ll_report:

                UIHelper.showShortVideoReport(getContext(),mShortVideoInfo.getId());
                break;

            case R.id.ll_del:

                requestShortVideoDel();
                break;
            case R.id.ll_copy_url:

                TDevice.copyTextToBoard(AppConfig.MAIN_URL + "/index.php?g=Live&m=Stream&a=index&id=" + mShortVideoInfo.getId());
                break;
            case R.id.layout_share_wx:

                share(Wechat.NAME);
                break;
            case R.id.layout_share_pyq:

                share(WechatMoments.NAME);
                break;
            case R.id.layout_share_qq:

                share(QQ.NAME);
                break;
            case R.id.layout_share_qzon:

                share(QZone.NAME);
                break;



            default:

                break;
        }
    }

    private void share(String name){

        ConfigBean config = LiveUtils.getConfigBean(getContext());

        ShareUtils.share(getContext(), name, config.share_title, config.share_des,
                mShortVideoInfo.getCover_url(), AppConfig.MAIN_URL + "/index.php?g=Live&m=Stream&a=index&id=" + mShortVideoInfo.getId(), new PlatformActionListener() {
                    @Override
                    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {

                        //添加转发次数
                        PhoneLiveApi.requestShareNumAdd(AppContext.getInstance().getLoginUid(),AppContext.getInstance().getToken(),mShortVideoInfo.getId(),new StringCallback(){

                            @Override
                            public void onError(Call call, Exception e, int id) {

                            }

                            @Override
                            public void onResponse(String response, int id) {

                            }
                        });
                    }

                    @Override
                    public void onError(Platform platform, int i, Throwable throwable) {

                    }

                    @Override
                    public void onCancel(Platform platform, int i) {

                    }
                });

    }

    private void requestShortVideoDel() {

        DialogHelp.getConfirmDialog(getContext(), "确定删除吗", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                showWaitDialog("正在删除...",false);
                PhoneLiveApi.requestShortVideoDel(AppContext.getInstance().getLoginUid(),AppContext.getInstance().getToken(),mShortVideoInfo.getId(),new StringCallback(){

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        hideWaitDialog();
                    }

                    @Override
                    public void onResponse(String response, int id) {

                        hideWaitDialog();
                        String res = ApiUtils.checkIsSuccess2(response);
                        if(res != null){

                            AppContext.showToast("删除成功");
                            getActivity().finish();
                        }
                    }
                });
            }
        }).show();
    }
}
