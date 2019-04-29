package com.daimeng.livee.api.remote;

import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.daimeng.livee.AppManager;
import com.daimeng.livee.AppContext;
import com.daimeng.livee.ui.MobileLoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ApiUtils {
    public final static int SUCCESS_CODE = 200;//成功请求到服务端
    public final static String TOKEN_TIMEOUT = "700";

    public static JSONArray checkIsSuccess(String res){
        try {
            JSONObject resJson = new JSONObject(res);

            if(Integer.parseInt(resJson.getString("ret")) == SUCCESS_CODE){
                JSONObject dataJson =  resJson.getJSONObject("data");
                String code = dataJson.getString("code");
                if(code.equals(TOKEN_TIMEOUT)){

                    AppManager.getInstance().clearActivitiesAndServices();
                    Intent intent = new Intent(AppContext.getInstance(), MobileLoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    AppContext.getInstance().startActivity(intent);

                    return null;
                }else if(!code.equals("0")){
                    AppContext.showToast(dataJson.get("msg").toString());
                    //Toast.makeText(AppContext.getInstance(),dataJson.get("msg").toString(),Toast.LENGTH_LONG).show();
                    return null;
                }else {
                    return dataJson.getJSONArray("info");
                }
            }else{
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static String checkIsSuccessReturnString(String res){
        try {
            JSONObject resJson = new JSONObject(res);

            if(Integer.parseInt(resJson.getString("ret")) == SUCCESS_CODE){
                JSONObject dataJson =  resJson.getJSONObject("data");
                String code = dataJson.getString("code");
                if(code.equals(TOKEN_TIMEOUT)){

                    AppManager.getInstance().clearActivitiesAndServices();
                    Intent intent = new Intent(AppContext.getInstance(), MobileLoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    AppContext.getInstance().startActivity(intent);

                    return null;
                }else if(!code.equals("0")){
                    AppContext.showToast(dataJson.get("msg").toString());
                    //Toast.makeText(AppContext.getInstance(),dataJson.get("msg").toString(),Toast.LENGTH_LONG).show();
                    return null;
                }else {
                    return dataJson.getString("info");
                }
            }else{
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static String checkIsSuccess2(String res){

        try {
            JSONObject object = new JSONObject(res);
            if(object.getInt("code") != 0){
                if(object.getInt("code") == 700){

                    AppContext.getInstance().cleanLoginInfo();
                    Intent intent = new Intent(AppContext.getInstance(), MobileLoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("EXIT", true);
                    AppContext.getInstance().startActivity(intent);
                    return null;
                }
                AppContext.showToast(object.getString("msg"));
                return null;
            }

            return object.getString("data");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;

    }

    public static <T> T checkIsSuccess(String res,Class<T> clz){

        try {
            JSONObject object = new JSONObject(res);
            if(object.getInt("code") != 0){
                if(object.getInt("code") == 700){

                    AppContext.getInstance().cleanLoginInfo();
                    Intent intent = new Intent(AppContext.getInstance(), MobileLoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("EXIT", true);
                    AppContext.getInstance().startActivity(intent);
                    return null;
                }
                AppContext.showToast(object.getString("msg"));
                return null;
            }

            return JSON.parseObject(object.getString("data"),clz);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;

    }

    public static <T> List<T> formatDataToList(JSONArray resUserListJsonArr, Class<T> c){

        List<T> datas = new ArrayList<>();
        try {
            Gson g = new Gson();
            for (int i = 0; i < resUserListJsonArr.length(); i++) {
                datas.add(g.fromJson(resUserListJsonArr.getString(i),c));
            }

            return datas;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return datas;
    }

}
