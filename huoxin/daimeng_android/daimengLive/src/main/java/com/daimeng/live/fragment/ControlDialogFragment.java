package com.daimeng.live.fragment;

import android.app.Dialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

import com.daimeng.live.AppContext;
import com.daimeng.live.R;
import com.daimeng.live.adapter.ControlListAdapter;
import com.daimeng.live.api.remote.ApiUtils;
import com.daimeng.live.api.remote.PhoneLiveApi;
import com.daimeng.live.bean.ManageListBean;
import com.daimeng.live.utils.TDevice;
import com.google.gson.Gson;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class ControlDialogFragment extends DialogFragment {

    private List<ManageListBean> mControlList;
    ListView mListViewControlList;
    private ControlListAdapter controlListAdapter;
    private String hostId;

    public static ControlDialogFragment newInstance(String hostId, boolean cancelable) {
        ControlDialogFragment instance = new ControlDialogFragment();
        instance.setCancelable(cancelable);
        instance.hostId = hostId;
        return instance;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = new Dialog(getActivity(), R.style.dialog_control);

        dialog.setContentView(R.layout.fragment_control_list);
        mListViewControlList = dialog.findViewById(R.id.lv_control_list);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            params.height = (int) TDevice.dpToPixel(150);
        }

        window.setAttributes(params);
        // initView(dialog);
        initData();
        return dialog;
    }


    public void initView(View view) {

    }

    public void initData() {
        PhoneLiveApi.getManageList(hostId, callback);
    }

    private StringCallback callback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e, int id) {
            AppContext.showToast("获取管理员列表失败");
        }

        @Override
        public void onResponse(String response, int id) {
            JSONArray manageListJsonObject = ApiUtils.checkIsSuccess(response);
            if (null != manageListJsonObject) {
                try {
                    mControlList = new ArrayList<>();
                    Gson g = new Gson();
                    if (!(manageListJsonObject.length() > 0)) return;
                    for (int i = 0; i < manageListJsonObject.length(); i++) {
                        mControlList.add(g.fromJson(manageListJsonObject.getString(i), ManageListBean.class));
                    }

                    fillUI();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void fillUI() {
        controlListAdapter = new ControlListAdapter(getActivity(), mControlList);

        mListViewControlList.setAdapter(controlListAdapter);

    }

}
