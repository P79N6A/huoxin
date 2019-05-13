package com.daimeng.live.wxpay;

import android.app.Activity;

import com.alibaba.fastjson.JSONObject;
import com.daimeng.live.utils.LiveUtils;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.daimeng.live.AppContext;

/**
 * Created by Administrator on 2016/4/14.
 */
public class WChatPay {
    IWXAPI msgApi;

    private Activity context;

    public WChatPay(Activity context) {
        this.context = context;
        // 将该app注册到微信
        msgApi = WXAPIFactory.createWXAPI(context,null);
        msgApi.registerApp(LiveUtils.getConfigBean(context).wx_app_id);
    }

    public void startPay(JSONObject signInfo) {
        PayReq req = new PayReq();
        req.appId        = signInfo.getString("appid");
        req.partnerId    = signInfo.getString("partnerid");
        req.prepayId     = signInfo.getString("prepayid");//预支付会话ID
        req.packageValue = "Sign=WXPay";
        req.nonceStr     = signInfo.getString("noncestr");
        req.timeStamp    = signInfo.getString("timestamp");
        req.sign         = signInfo.getString("sign");
        if(msgApi.sendReq(req)){
            AppContext.showToast("微信支付");
        }else{
            AppContext.showToast("请查看您是否安装微信");
        }

    }
}
