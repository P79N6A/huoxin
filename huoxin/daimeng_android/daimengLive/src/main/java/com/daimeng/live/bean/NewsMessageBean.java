package com.daimeng.live.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by weipeng on 2017/11/7.
 */

public class NewsMessageBean implements Parcelable {


    /**
     * id : 6
     * uid : 193
     * touid : 158
     * type : 1
     * vid : 24
     * addtime : 3分钟前
     * state : 0
     * cover_url :
     * user_nicename : 手机用户9813
     */

    private String id;
    private String uid;
    private String touid;
    private String type;
    private String vid;
    private String addtime;
    private String state;
    private String cover_url;
    private String user_nicename;
    private String avatar;
    private String body;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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

    public String getTouid() {
        return touid;
    }

    public void setTouid(String touid) {
        this.touid = touid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCover_url() {
        return cover_url;
    }

    public void setCover_url(String cover_url) {
        this.cover_url = cover_url;
    }

    public String getUser_nicename() {
        return user_nicename;
    }

    public void setUser_nicename(String user_nicename) {
        this.user_nicename = user_nicename;
    }

    public NewsMessageBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.uid);
        dest.writeString(this.touid);
        dest.writeString(this.type);
        dest.writeString(this.vid);
        dest.writeString(this.addtime);
        dest.writeString(this.state);
        dest.writeString(this.cover_url);
        dest.writeString(this.user_nicename);
        dest.writeString(this.avatar);
        dest.writeString(this.body);
    }

    protected NewsMessageBean(Parcel in) {
        this.id = in.readString();
        this.uid = in.readString();
        this.touid = in.readString();
        this.type = in.readString();
        this.vid = in.readString();
        this.addtime = in.readString();
        this.state = in.readString();
        this.cover_url = in.readString();
        this.user_nicename = in.readString();
        this.avatar = in.readString();
        this.body = in.readString();
    }

    public static final Creator<NewsMessageBean> CREATOR = new Creator<NewsMessageBean>() {
        @Override
        public NewsMessageBean createFromParcel(Parcel source) {
            return new NewsMessageBean(source);
        }

        @Override
        public NewsMessageBean[] newArray(int size) {
            return new NewsMessageBean[size];
        }
    };
}
