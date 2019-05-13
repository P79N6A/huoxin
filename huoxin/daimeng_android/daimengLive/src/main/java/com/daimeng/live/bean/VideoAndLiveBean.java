package com.daimeng.live.bean;

/**
 * Created by weipeng on 2017/10/23.
 */

public class VideoAndLiveBean {


    /**
     * user_nicename : 手机用户9813
     * avatar : http://oy6b650kf.bkt.clouddn.com/20171021205351_05c4adf14742431861df0b901771fb73?imageView2/2/w/600/h/600
     * cover_url :
     * id : 158
     * uid : 158
     * addtime : 1508656960
     * type : 0
     */

    private String user_nicename;
    private String avatar;
    private String cover_url;
    private String id;
    private String uid;
    private String addtime;
    private int type;
    private String city;
    private String title;
    private String stream;

    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getUser_nicename() {
        return user_nicename;
    }

    public void setUser_nicename(String user_nicename) {
        this.user_nicename = user_nicename;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCover_url() {
        return cover_url;
    }

    public void setCover_url(String cover_url) {
        this.cover_url = cover_url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
