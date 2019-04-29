package com.daimeng.family.ui;

import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;

import com.daimeng.livee.AppContext;
import com.daimeng.livee.R;
import com.daimeng.livee.api.remote.ApiUtils;
import com.daimeng.livee.api.remote.PhoneLiveApi;
import com.daimeng.livee.base.BaseActivity;
import com.daimeng.livee.ui.customviews.ActivityTitle;
import com.daimeng.livee.utils.DialogHelp;
import com.daimeng.livee.utils.SimpleUtils;
import com.daimeng.livee.utils.UIHelper;
import com.daimeng.livee.widget.CircleImageView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

public class FamilyDetailActivity extends BaseActivity {

    @BindView(R.id.tv_name)
    TextView mTvName;

    @BindView(R.id.tv_fname)
    TextView mTvFname;

    @BindView(R.id.tv_num)
    TextView mTvNum;

    @BindView(R.id.tv_notice)
    TextView mTvNotice;

    @BindView(R.id.tv_f1)
    TextView mTvF1;

    @BindView(R.id.tv_f2)
    TextView mTvF2;

    @BindView(R.id.iv_avatar)
    CircleImageView mCircleImageView;

    @BindView(R.id.view_title)
    ActivityTitle mActivityTitle;


    private String mFid;
    private int mAction = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_family_detail;
    }


    @OnClick({R.id.tv_f1,R.id.tv_f2})
    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.tv_f1:

                if(mAction == 3){
                    //加入家族
                    showWaitDialog("正在加载中...",false);
                    PhoneLiveApi.requestJoinFamily(AppContext.getInstance().getLoginUid(),AppContext.getInstance().getToken(),mFid,new StringCallback(){

                        @Override
                        public void onError(Call call, Exception e, int id) {
                            hideWaitDialog();
                        }

                        @Override
                        public void onResponse(String response, int id) {

                            hideWaitDialog();
                            JSONArray res = ApiUtils.checkIsSuccess(response);
                            if(res != null){

                                mTvF1.setText("申请中");
                                mTvF1.setEnabled(false);
                            }
                        }
                    });
                }else if(mAction == 1){
                    //管理
                    UIHelper.showFamilyEditActivity(FamilyDetailActivity.this);

                }else if(mAction == 2){
                    //家族成员

                    UIHelper.showFamilyUserListActivity(this,mFid);
                }

                break;
            case R.id.tv_f2:
                if(mAction == 1){
                    //成员管理

                    UIHelper.showFamilyManageActivity(this);
                }else if(mAction == 2){
                    //退出家族

                    DialogHelp.getConfirmDialog(this, "是否退出该家族？", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            if(i == -1){
                                showWaitDialog("正在退出...",false);
                                PhoneLiveApi.requestSigOutFamily(getUserID(),getUserToken(),mFid,new StringCallback(){

                                    @Override
                                    public void onError(Call call, Exception e, int id) {
                                        hideWaitDialog();
                                    }

                                    @Override
                                    public void onResponse(String response, int id) {

                                        hideWaitDialog();
                                        JSONArray res = ApiUtils.checkIsSuccess(response);
                                        if(res != null){

                                            AppContext.showToast("退出成功");
                                            finish();
                                        }
                                    }
                                });
                            }
                        }
                    }).show();
                }

                break;
        }
    }

    @Override
    public void initView() {

        mActivityTitle.setReturnListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mActivityTitle.setMoreListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void initData() {


        mFid = getIntent().getStringExtra("fid");

        PhoneLiveApi.requestGetFamilyDetail(getUserID(), mFid,new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {

                JSONArray res = ApiUtils.checkIsSuccess(response);
                if(res != null){

                    try {
                        JSONObject info = res.getJSONObject(0);
                        mTvName.setText("家族长:" + info.getString("user_nicename"));
                        mTvFname.setText(info.getString("name"));
                        mTvNotice.setText(info.getString("notice"));
                        mTvNum.setText("主播数量:" + info.getString("emcee_num"));
                        SimpleUtils.loadImageForView(FamilyDetailActivity.this,mCircleImageView,info.getString("avatar"),0);

                        mAction = info.getInt("action");

                        if(mAction == 1 && info.getInt("state") == 1){
                            //家族族长，但是未通过审核
                            mTvF1.setVisibility(View.GONE);
                            mTvF2.setVisibility(View.VISIBLE);
                            mTvF2.setText("审核中");
                            mTvF2.setEnabled(false);
                        }else if(mAction == 2 && info.getInt("join_state") == 1){
                            //加入家族审核中
                            mTvF1.setVisibility(View.GONE);
                            mTvF2.setVisibility(View.VISIBLE);
                            mTvF2.setText("审核中");
                            mTvF2.setEnabled(false);
                        }else if(mAction == 1 && info.getInt("state") == 2){

                            //家族族长
                            mTvF1.setVisibility(View.VISIBLE);
                            mTvF2.setVisibility(View.VISIBLE);
                            mTvF1.setText("管理");
                            mTvF2.setText("成员管理");
                        }else if(mAction == 2 && info.getInt("join_state") == 2){
                            //加入家族审核中
                            mTvF1.setText("家族成员");
                            mTvF2.setText("退出家族");
                            mTvF1.setVisibility(View.VISIBLE);
                            mTvF2.setVisibility(View.VISIBLE);
                        }else if(mAction == 3){
                            mTvF1.setVisibility(View.VISIBLE);
                            mTvF1.setText("加入家族");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
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
        OkHttpUtils.getInstance().cancelTag("requestSigOutFamily");
        OkHttpUtils.getInstance().cancelTag("requestGetFamilyDetail");
    }
}
