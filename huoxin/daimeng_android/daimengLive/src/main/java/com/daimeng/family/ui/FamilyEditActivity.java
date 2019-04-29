package com.daimeng.family.ui;

import android.view.View;
import android.widget.EditText;

import com.daimeng.livee.AppConfig;
import com.daimeng.livee.AppContext;
import com.daimeng.livee.R;
import com.daimeng.livee.api.remote.ApiUtils;
import com.daimeng.livee.api.remote.PhoneLiveApi;
import com.daimeng.livee.base.BaseActivity;
import com.daimeng.livee.ui.customviews.ActivityTitle;
import com.daimeng.livee.utils.UIHelper;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

public class FamilyEditActivity extends BaseActivity {


    @BindView(R.id.et_ratio)
    EditText mEtRatio;

    @BindView(R.id.view_title)
    ActivityTitle mActivityTitle;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_family_edit;
    }


    @OnClick({R.id.btn_submit,R.id.btn_family_profit_record})
    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.btn_submit:

                showWaitDialog("正在提交数据...",false);
                String ratio = mEtRatio.getText().toString();
                PhoneLiveApi.requestEditRatioFamilyInfo(getUserID(),getUserToken(),ratio,new StringCallback(){

                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {

                        hideWaitDialog();
                        JSONArray res = ApiUtils.checkIsSuccess(response);
                        if(res != null){
                            AppContext.showToast("修改成功");
                        }
                    }
                });
                break;

            case R.id.btn_family_profit_record:

                UIHelper.showWebView(this, AppConfig.MAIN_URL + "/index.php?g=Appapi&m=Agent&a=family_profit_record&fid=" + getUserID(),"");
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

        PhoneLiveApi.requestGetFamilyInfo(getUserID(),getUserToken(),new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {

                JSONArray res = ApiUtils.checkIsSuccess(response);
                if(res != null){

                    try {
                        mEtRatio.setText(res.getJSONObject(0).getString("ratio"));
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
        OkHttpUtils.getInstance().cancelTag("requestGetFamilyInfo");
    }
}
