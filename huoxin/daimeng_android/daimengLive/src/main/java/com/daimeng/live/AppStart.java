package com.daimeng.live;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

import com.daimeng.live.api.remote.ApiUtils;
import com.daimeng.live.api.remote.PhoneLiveApi;
import com.daimeng.live.db.DBManager;
import com.daimeng.live.model.JsonModel;
import com.daimeng.live.ui.MainActivity;
import com.daimeng.live.utils.TLog;
import com.hyphenate.chat.EMClient;
import com.daimeng.live.utils.UIHelper;
import com.zhy.http.okhttp.callback.StringCallback;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;

import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * 应用启动界面
 *
 */
public class AppStart extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        TLog.log(sHA1(this));
        final View view = View.inflate(this, R.layout.app_start, null);
        setContentView(view);
        // 渐变展示启动屏
        AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
        aa.setDuration(800);
        view.startAnimation(aa);
        aa.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                PhoneLiveApi.getInitInfo(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        String res = ApiUtils.checkIsSuccess2(response);
                        if(res != null){

                            List<JsonModel> list = DBManager.getInstance(AppStart.this).queryUserList("app_init");

                            JsonModel jsonModel;
                            if(list.size() != 0){
                                jsonModel = list.get(0);
                                jsonModel.setKey("app_init");
                                jsonModel.setValue(res);
                                DBManager.getInstance(AppStart.this).updateUser(jsonModel);
                            }else{
                                jsonModel = new JsonModel();
                                jsonModel.setKey("app_init");
                                jsonModel.setValue(res);
                                DBManager.getInstance(AppStart.this).insertUser(jsonModel);
                            }

                            redirectTo();
                        }
                    }
                });


            }

            @Override
            public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationStart(Animation animation) {}
        });
    }

    //字体库
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    /**
     * 跳转到...
     */
    private void redirectTo() {

        if(!AppContext.getInstance().isLogin()){

            UIHelper.showLoginSelectActivity(this);
            finish();
            return;
        }

        EMClient.getInstance().groupManager().loadAllGroups();
        EMClient.getInstance().chatManager().loadAllConversations();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public static String sHA1(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            String result = hexString.toString();
            return result.substring(0, result.length()-1);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

}
