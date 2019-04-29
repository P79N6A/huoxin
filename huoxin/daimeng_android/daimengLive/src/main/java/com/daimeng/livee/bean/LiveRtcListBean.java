package com.daimeng.livee.bean;

public class LiveRtcListBean {

    private String user_nicename;
    private String user_id;
    private String live_uid;
    private String status;
    private String avatar;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUser_nicename() {
        return user_nicename;
    }

    public void setUser_nicename(String user_nicename) {
        this.user_nicename = user_nicename;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getLive_uid() {
        return live_uid;
    }

    public void setLive_uid(String live_uid) {
        this.live_uid = live_uid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
