package com.daimeng.livee.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.daimeng.livee.AppConfig;
import com.daimeng.livee.AppContext;
import com.daimeng.livee.R;
import com.daimeng.livee.api.remote.ApiUtils;
import com.daimeng.livee.api.remote.PhoneLiveApi;
import com.daimeng.livee.base.ShowLiveActivityBase;
import com.daimeng.livee.bean.CarBean;
import com.daimeng.livee.bean.GiftBean;
import com.daimeng.livee.bean.RankBean;
import com.daimeng.livee.bean.callback.InitInfoBean;
import com.daimeng.livee.bean.LiveCheckInfoBean;
import com.daimeng.livee.bean.LiveBean;
import com.daimeng.livee.bean.ConfigBean;
import com.daimeng.livee.bean.SimpleUserInfo;
import com.daimeng.livee.bean.UserBean;
import com.daimeng.livee.interf.DialogInterface;
import com.daimeng.livee.dialog.LiveCommon;
import com.daimeng.livee.other.DrawableRes;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

import okhttp3.Call;


public class LiveUtils {

    private static InitInfoBean initInfo;

    /**
     * @dw 获取歌词字符串
     * @param fileName 歌词文件目录
     * */
    public static String getFromFile(String fileName){
        try {
            FileInputStream fileInputStream = new FileInputStream(fileName);
            InputStreamReader inputReader = new InputStreamReader(fileInputStream);
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line="";
            String Result="";
            while((line = bufReader.readLine()) != null){
                if(line.trim().equals(""))
                    continue;
                Result += line + "\r\n";
            }
            fileInputStream.close();
            inputReader.close();
            bufReader.close();
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void startWeb(Context context,String url){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }

    //用户列表排序
    public static void sortUserList(List<SimpleUserInfo> mUserList){

        for(int i = 0; i < mUserList.size() - 1; i++){
            for(int j = 0 ; j < mUserList.size() - 1 -i; j++){

                //判断等级大小进行排序
                if(StringUtils.toInt(mUserList.get(j).level) <
                        StringUtils.toInt(mUserList.get(j + 1).level)){
                    SimpleUserInfo temp = mUserList.get(j);
                    mUserList.set(j,mUserList.get(j+1));
                    mUserList.set(j + 1,temp);
                }
            }
        }

    }

    //获取等级图片
    public static int getLevelRes(String level){
        int l2 = StringUtils.toInt(level);

        return DrawableRes.LevelImg[(l2 == 0? 0 : l2 - 1)];

    }

    //获取性别图片
    public static int getSexRes(String sex){

        return StringUtils.toInt(sex) == 1 ? R.drawable.global_male : R.drawable.global_female;
    }

    public static SimpleUserInfo getSimleUserInfo(LiveBean liveJson){
        SimpleUserInfo simpleUserInfo = new SimpleUserInfo();

        simpleUserInfo.avatar = liveJson.avatar;
        simpleUserInfo.avatar_thumb = liveJson.avatar_thumb;
        simpleUserInfo.city = liveJson.city;
        simpleUserInfo.user_nicename = liveJson.user_nicename;
        simpleUserInfo.id = liveJson.uid;


        return simpleUserInfo;
    }

    public static SimpleUserInfo getSimleUserInfo(RankBean liveJson){
        SimpleUserInfo simpleUserInfo = new SimpleUserInfo();

        simpleUserInfo.avatar = liveJson.getAvatar();
        simpleUserInfo.avatar_thumb = liveJson.getAvatar_thumb();
        simpleUserInfo.city = liveJson.getCity();
        simpleUserInfo.user_nicename = liveJson.getUser_nicename();
        simpleUserInfo.id = liveJson.getId();


        return simpleUserInfo;
    }


    public static String getFiledJson(String key,String val){

        return "{\"" +key+ "\":\""+ val +"\"}";
    }

    public static String getLimitString(String source, int length){
        if (null!=source && source.length()>length){

            return source.substring(0, length)+"...";
        }
        return source;
    }

    public static ConfigBean getConfigBean(Context context){

        if(initInfo == null){

            initInfo = DBDataUtils.getClassForDB(context,"app_init",InitInfoBean.class);
        }

        return initInfo.getConfig();
    }

    public static GiftBean getQuiteGift(Context context){

        InitInfoBean config = DBDataUtils.getClassForDB(context,"app_init",InitInfoBean.class);

        return config.getQuick_gift();
    }

    public static List<CarBean> getCarList(Context context){

        InitInfoBean config = DBDataUtils.getClassForDB(context,"app_init",InitInfoBean.class);

        return config.getCar_list();
    }


    //获取一张图片的正确地址
    public static String getHttpUrl(String url){

        if(url == null){

            return "";
        }
        if(url.startsWith("http")){

            return url;
        }else{

            return AppConfig.MAIN_URL + url;
        }

    }

    //跳转直播间
    public static void joinLiveRoom(final Context context,final LiveCheckInfoBean data){

        final LiveBean live = data.getLive_info();
        final int roomType = StringUtils.toInt(data.getType());
        if(roomType == ShowLiveActivityBase.LIVE_TYPE_PAY){

            LiveCommon.showConfirmDialog(context, "提示", data.getType_msg(), new DialogInterface() {
                @Override
                public void cancelDialog(View v, Dialog d) {
                    d.dismiss();
                }

                @Override
                public void determineDialog(View v, Dialog d) {
                    PhoneLiveApi.requestCharging(AppContext.getInstance().getLoginUid(),AppContext.getInstance().getToken(),
                            live.uid,live.stream,new StringCallback(){

                                @Override
                                public void onError(Call call, Exception e, int id) {

                                }

                                @Override
                                public void onResponse(String response, int id) {
                                    JSONArray res = ApiUtils.checkIsSuccess(response);

                                    if(res != null){

                                        UserBean userBean = AppContext.getInstance().getLoginUser();
                                        try {
                                            userBean.coin = res.getJSONObject(0).getString("coin");
                                            AppContext.getInstance().saveUserInfo(userBean);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        UIHelper.showLookLiveActivity(context, live,data);
                                    }
                                }
                            });
                    d.dismiss();
                }
            });

        }else if(roomType == ShowLiveActivityBase.LIVE_TYPE_TIME){

            //是否是第一次既进入
            if(StringUtils.toInt(data.getIs_first()) != 0){
                LiveCommon.showConfirmDialog(context, "提示", data.getType_msg(), new DialogInterface() {

                    @Override
                    public void cancelDialog(View v, Dialog d) {
                        d.dismiss();
                    }

                    @Override
                    public void determineDialog(View v, Dialog d) {

                        PhoneLiveApi.requestCharging(AppContext.getInstance().getLoginUid(),AppContext.getInstance().getToken(),
                                live.uid,live.stream,new StringCallback(){

                                    @Override
                                    public void onError(Call call, Exception e, int id) {

                                    }

                                    @Override
                                    public void onResponse(String response, int id) {
                                        JSONArray res = ApiUtils.checkIsSuccess(response);

                                        if(res != null){

                                            UserBean userBean = AppContext.getInstance().getLoginUser();
                                            try {
                                                userBean.coin = res.getJSONObject(0).getString("coin");
                                                AppContext.getInstance().saveUserInfo(userBean);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            UIHelper.showLookLiveActivity(context, live,data);
                                        }
                                    }


                                });
                        d.dismiss();
                    }
                });


            }else{
                //第一次进入
                UIHelper.showLookLiveActivity(context, live,data);
            }

        }else if(roomType == ShowLiveActivityBase.LIVE_TYPE_PWD){

            LiveCommon.showInputContentDialog(context, "请输入房间密码", new DialogInterface() {
                @Override
                public void cancelDialog(View v, Dialog d) {
                    d.dismiss();
                }

                @Override
                public void determineDialog(View v, Dialog d) {
                    EditText et = (EditText) d.findViewById(R.id.et_input);
                    if(!data.getType_msg().equals(MD5.getMD5(et.getText().toString()))
                            && !data.getType_msg().contains(MD5.getMD5(et.getText().toString()))){
                        Toast.makeText(context,"密码错误",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    UIHelper.showLookLiveActivity(context, live,data);
                    d.dismiss();
                }
            });

        }else if(roomType == 4){
            //游戏直播
            UIHelper.showLookLiveActivity(context, live,data);

        }else if(roomType == ShowLiveActivityBase.LIVE_TYPE_ORDINARY){
            UIHelper.showLookLiveActivity(context, live,data);
        }
    }
}
