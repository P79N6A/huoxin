package com.daimeng.live.utils;

import android.webkit.JavascriptInterface;

public class JavaScriptInterface {

    @JavascriptInterface
    public void copyText(String content) {
        TDevice.copyTextToBoard(content);
    }

    public void openWechatPay(){

    }
}