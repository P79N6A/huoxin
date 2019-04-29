package com.daimeng.livee.bean;

/**
 * Created by Administrator on 2016/3/16.
 */
public class ChatBean{
    private String userNick;
    private String sendChatMsg;
    private int type;
    private String content;//聊天纯文本 HHH
    public String is_vip;
    public SimpleUserInfo mSimpleUserInfo;


    public SimpleUserInfo getSimpleUserInfo() {
        return mSimpleUserInfo;
    }

    public void setSimpleUserInfo(SimpleUserInfo simpleUserInfo) {
        mSimpleUserInfo = simpleUserInfo;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUserNick() {
        return userNick;
    }

    public void setUserNick(String userNick) {
        this.userNick = userNick;
    }

    public String getSendChatMsg() {
        return sendChatMsg;
    }

    public void setSendChatMsg(String sendChatMsg) {
        this.sendChatMsg = sendChatMsg;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
