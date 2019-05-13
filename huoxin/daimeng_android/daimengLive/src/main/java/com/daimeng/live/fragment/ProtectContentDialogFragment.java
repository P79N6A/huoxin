package com.daimeng.live.fragment;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.daimeng.live.AppContext;
import com.daimeng.live.R;
import com.daimeng.live.api.remote.ApiUtils;
import com.daimeng.live.api.remote.PhoneLiveApi;
import com.daimeng.live.bean.GiftBean;
import com.daimeng.live.bean.ProtectBean;
import com.daimeng.live.bean.ProtectContentBean;
import com.daimeng.live.bean.UserBean;
import com.daimeng.live.interf.BaseFragmentInterface;
import com.daimeng.live.utils.SimpleUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class ProtectContentDialogFragment extends DialogFragment implements BaseFragmentInterface {

    @BindView(R.id.protect_close_btn)
    Button close_btn;
    @BindView(R.id.img_protect)
    ImageView imgprotect;
    @BindView(R.id.protect_name)
    TextView protect_name;
    @BindView(R.id.protect_price)
    TextView protect_price;
    @BindView(R.id.validity_day)
    TextView validity_day;
    @BindView(R.id.protect_content_txt)
    TextView protect_content_txt;
    @BindView(R.id.paypro_btn)
    Button paypro_btn;
    private UserBean mUser;
    private ProtectBean pro;
    //守护端服务器返回数据
    private ProtectContentBean mProtectContent;
    private GiftBean mSelectedGiftItem;
    private String rid;
    private String hostId;
    private String stream;
    private ProtectDialogFragment.CallBack callback;

    public static ProtectContentDialogFragment newInstance(String id, String hostId, String stream, ProtectDialogFragment.CallBack callBack) {
        ProtectContentDialogFragment fragment = new ProtectContentDialogFragment();
        fragment.rid = id;
        fragment.hostId = hostId;
        fragment.stream = stream;
        fragment.callback = callBack;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_protect_content, null);
        if (getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        ButterKnife.bind(this, view);

        initView(view);
        initData();
        return view;
    }

    @Override
    public void initView(View view) {

    }

    @Override
    public void initData() {
        mUser = AppContext.getInstance().getLoginUser();
        protectContentData();
    }

    private void protectContentData() {
        PhoneLiveApi.getProtectContent(mUser.id, mUser.token, rid, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                String s = ApiUtils.checkIsSuccessReturnString(response);
                ProtectContentBean protectContentBean = JSON.parseObject(s, ProtectContentBean.class);
                fillUI(protectContentBean);
            }
        });
    }

    //填充守护详情数据
    private void fillUI(ProtectContentBean mProtectContent) {
        SimpleUtils.loadImageForView(AppContext.getInstance(), imgprotect, mProtectContent.getRule().get(0).getIcon(), 0);
        //    imgprotect.setImageURI(Uri.parse(mProtectContent.getRule().get(0).getIcon()));
        protect_name.setText(mProtectContent.getRule().get(0).getName());

        protect_price.setText(mProtectContent.getRule().get(0).getNum());
        validity_day.setText(mProtectContent.getRule().get(0).getDay());
        String[] contentData = mProtectContent.getData();
        String ct = "";
        for (int i = 0; i < contentData.length; i++) {
            ct = ct + "-" + contentData[i] + "\n";

        }
        protect_content_txt.setText(ct);
    }

    @OnClick(R.id.paypro_btn)
    public void onClickpay(View v) {
        payGuard();
    }

    //购买守护道具
    private void payGuard() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        builder.setMessage("确定购买这个道具吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
                callback.work();
                PhoneLiveApi.payProtect(mUser.id, mUser.token, hostId, stream, "2", rid, protect_price.getText().toString(), new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        JSONArray res = ApiUtils.checkIsSuccess(response);
                        try {
                            JSONObject resJson = new JSONObject(response);
                            JSONObject dataJson = resJson.getJSONObject("data");
                            String code = dataJson.getString("code");
                            if (code.equals("0")) {
                                AppContext.showToast("购买成功！");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ;
                    }
                });
            }
        });
        //String iid=((RtmpPlayerActivity) getActivity()).mEmceeInfo.uid;
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @OnClick(R.id.protect_close_btn)
    public void onClick(View v) {
        getDialog().dismiss();
    }


}
