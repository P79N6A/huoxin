package com.daimeng.family.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.daimeng.livee.AppContext;
import com.daimeng.livee.R;
import com.daimeng.livee.api.remote.ApiUtils;
import com.daimeng.livee.api.remote.PhoneLiveApi;
import com.daimeng.livee.base.SelectPicActivity;
import com.daimeng.livee.ui.customviews.ActivityTitle;
import com.daimeng.livee.utils.SimpleUtils;
import com.daimeng.livee.widget.CircleImageView;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

public class CreateFamilyActivity extends SelectPicActivity {

    @BindView(R.id.et_notice)
    EditText mEtNotice;

    @BindView(R.id.et_name)
    EditText mEtName;

    @BindView(R.id.iv_avatar)
    CircleImageView mCircleImageView;

    @BindView(R.id.view_title)
    ActivityTitle mActivityTitle;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_create_family;
    }


    @OnClick({R.id.btn_submit,R.id.iv_avatar})
    @Override
    public void onClick(View view) {


        switch (view.getId()){
            case R.id.btn_submit:

                createFamily();
                break;

            case R.id.iv_avatar:

                if(android.os.Build.VERSION.SDK_INT >= 23) {
                    //摄像头权限检测
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        //进行权限请求
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE},
                                5);
                        return;
                    }else{
                        startImagePick();
                    }
                }else{
                    startImagePick();
                }
                break;
        }
    }

    private void createFamily() {

        String f_name = mEtName.getText().toString();
        String f_notice = mEtNotice.getText().toString();

        if(TextUtils.isEmpty(f_name)){

            AppContext.showToast("请填写家族名称");
            return;
        }

        if(TextUtils.isEmpty(f_notice)){

            AppContext.showToast("请填写家族宣言");
            return;
        }

        if(protraitFile == null){

            AppContext.showToast("请上传家族头像");
            return;

        }

        showWaitDialog("正在创建...",false);
        PhoneLiveApi.createFamily(getUserID(),getUserToken(),f_name,f_notice,protraitFile,new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {

                hideWaitDialog();
                JSONArray res = ApiUtils.checkIsSuccess(response);
                if(res != null){

                    AppContext.showToast("创建成功");
                    finish();
                }
            }
        });


    }

    @Override
    public void initView() {

        setActionBarTitle("创建家族");
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

    }

    @Override
    protected void onSelectOk() {

        SimpleUtils.loadImageForView(this,mCircleImageView,protraitFile.getPath(),0);
    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }
}
