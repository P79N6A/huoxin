package com.daimeng.live.fragment;

import android.app.Dialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

import com.daimeng.live.AppContext;
import com.daimeng.live.R;
import com.daimeng.live.adapter.ProtectUserlListAdapter;
import com.daimeng.live.api.remote.ApiUtils;
import com.daimeng.live.api.remote.PhoneLiveApi;
import com.daimeng.live.bean.ProtectUserListBean;
import com.daimeng.live.utils.TDevice;
import com.google.gson.Gson;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class ProtectListDialogFragment extends DialogFragment {

    private List<ProtectUserListBean> mControlList;
    ListView mListViewControlList;
    private ProtectUserlListAdapter controlListAdapter;
    private String hostId;

    public static ProtectListDialogFragment newInstance(String hostId, boolean cancelable) {
        ProtectListDialogFragment instance = new ProtectListDialogFragment();
        instance.setCancelable(cancelable);
        instance.hostId = hostId;
        return instance;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = new Dialog(getActivity(), R.style.dialog_control);

        dialog.setContentView(R.layout.fragment_protect_list);
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
        Log.d("lyhostid", hostId);
        PhoneLiveApi.getProtecUsertList(hostId, callback);
    }

    private StringCallback callback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e, int id) {
            AppContext.showToast("获取守护列表失败");
        }

        @Override
        public void onResponse(String response, int id) {
            //Log.d("lysh", response);
            JSONArray manageListJsonObject = ApiUtils.checkIsSuccess(response);
            if (null != manageListJsonObject) {
                try {
                    mControlList = new ArrayList<>();
                    Gson g = new Gson();
                    if (!(manageListJsonObject.length() > 0)) return;
                    for (int i = 0; i < manageListJsonObject.length(); i++) {
                        mControlList.add(g.fromJson(manageListJsonObject.getString(i), ProtectUserListBean.class));
                    }

                    fillUI();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void fillUI() {
        controlListAdapter = new ProtectUserlListAdapter(getActivity(), mControlList);

        mListViewControlList.setAdapter(controlListAdapter);

    }

}
