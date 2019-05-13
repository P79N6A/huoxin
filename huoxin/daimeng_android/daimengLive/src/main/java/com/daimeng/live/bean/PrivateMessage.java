package com.daimeng.live.bean;

import com.hyphenate.chat.EMMessage;

/**
 * Created by daimeng on 16/8/15.
 */
public class PrivateMessage {
    public EMMessage message;
    public String uHead;

    public static PrivateMessage crateMessage(EMMessage message,String uHead){
        PrivateMessage privateMessage = new PrivateMessage();
        privateMessage.message = message;
        privateMessage.uHead = uHead;
        return privateMessage;
    }



}
