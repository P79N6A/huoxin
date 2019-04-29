package com.daimeng.livee.fragment;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.daimeng.livee.AppContext;
import com.daimeng.livee.R;
import com.daimeng.livee.api.remote.ApiUtils;
import com.daimeng.livee.api.remote.PhoneLiveApi;
import com.daimeng.livee.interf.BaseFragmentInterface;
import com.daimeng.livee.utils.DialogHelp;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class InviteCodeDialogFragment extends DialogFragment  implements BaseFragmentInterface {


    @BindView(R.id.et_invite_code)
    EditText mEtInviteCode;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_invite_code_input,null);

        ButterKnife.bind(this,view);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void initView(View view) {

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.tv_submit,R.id.tv_cancel})
    public void onClick(View v){

        switch (v.getId()){
            case R.id.tv_submit:

                clickFullInviteCode();
                break;
            case R.id.tv_cancel:
                clickCancelFullInviteCode();
                break;
            default:
                break;

        }
    }

    //点击取消填写邀请码
    private void clickCancelFullInviteCode() {

        PhoneLiveApi.requestCancelInviteCode(AppContext.getInstance().getLoginUid(),AppContext.getInstance().getToken(),"1",null);
        dismiss();
    }

    //提交邀请码
    private void clickFullInviteCode(){

        String inviteCode = mEtInviteCode.getText().toString().trim();
        if(TextUtils.isEmpty(inviteCode)){
            AppContext.showToast("请填写正确邀请码！");
            return;
        }

        final ProgressDialog dialog = DialogHelp.getWaitDialog(getActivity(),"正在提交...");
        dialog.show();
        PhoneLiveApi.requestFullAgentInviteCode(AppContext.getInstance().getLoginUid(),AppContext.getInstance().getToken(),inviteCode,new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {
                dialog.dismiss();
            }

            @Override
            public void onResponse(String response, int id) {

                dialog.dismiss();
                String res = ApiUtils.checkIsSuccess2(response);
                if(res != null){
                    AppContext.showToast("填写成功！");
                    dismiss();
                }
            }
        });
    }


    //检查是否填写了邀请码
    public static void checkInviteCode(final FragmentManager fragmentManager, Context context){

        PhoneLiveApi.requestIsFullAgentInviteCode(AppContext.getInstance().getLoginUid(),AppContext.getInstance().getToken(),new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {

                String res = ApiUtils.checkIsSuccess2(response);
                if(res != null){
                    JSONObject data = JSON.parseObject(res);
                    if(data.getIntValue("is_full_code") != 1){
                        InviteCodeDialogFragment dialogFragment = new InviteCodeDialogFragment();
                        dialogFragment.show(fragmentManager,"InviteCodeDialogFragment");
                    }
                }
            }
        });
    }
}
