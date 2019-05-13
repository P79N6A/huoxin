package com.daimeng.live.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.daimeng.live.R;
import com.daimeng.live.api.remote.ApiUtils;
import com.daimeng.live.api.remote.PhoneLiveApi;
import com.daimeng.live.bean.PrivateChatUserBean;
import com.daimeng.live.interf.DialogInterface;
import com.daimeng.live.utils.UIHelper;
import com.google.gson.Gson;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import okhttp3.Call;

/**
 * UI公共类
 */
public class LiveCommon {


    public static void showMsgDialog(Context context,String msg){
        new AlertView("提示", msg, null, new String[]{"确定"},
                null, context, AlertView.Style.Alert, null)
                .show();
    }

    //填写内容弹窗
    public static void showInputContentDialog(Context context,String title,final DialogInterface dialogInterface){

        final Dialog dialog = new Dialog(context, R.style.dialog_setting);
        dialog.setContentView(R.layout.dialog_set_room_pass);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        ((TextView)dialog.findViewById(R.id.tv_title)).setText(title);
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogInterface.cancelDialog(view,dialog);
            }
        });
        dialog.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogInterface.determineDialog(view,dialog);
            }
        });


    }

    //提示确认框
    public static void showConfirmDialog(Context context,String title,String content,final DialogInterface dialogInterface){

        final Dialog dialog = new Dialog(context, R.style.dialog_setting);
        dialog.setContentView(R.layout.dialog_confim);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        ((TextView)dialog.findViewById(R.id.tv_title)).setText(title);
        ((TextView)dialog.findViewById(R.id.tv_message_text)).setText(content);
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogInterface.cancelDialog(view,dialog);
            }
        });
        dialog.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogInterface.determineDialog(view,dialog);
            }
        });


    }

    //私信
    public static void showPrivateChat(String uid,String touid,final Context context){

        PhoneLiveApi.getPmUserInfo(uid,touid, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response,int id) {
                JSONArray res = ApiUtils.checkIsSuccess(response);
                if(null != res)
                    try {
                        UIHelper.showPrivateChatMessage(context, new Gson().fromJson(res.getString(0),PrivateChatUserBean.class));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }
        });
    }

}
