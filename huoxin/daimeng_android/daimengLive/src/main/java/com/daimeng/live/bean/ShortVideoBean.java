package com.daimeng.live.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.daimeng.live.AppContext;

/**
 * Created by weipeng on 2017/10/16.
 */

public class ShortVideoBean implements Parcelable {

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String uid;
    private String video_url;
    private String title;
    private String addtime;
    private String city;
    private String follow;
    private String forward;
    private String cover_url;
    private String videoid;
    private String user_nicename;
    private String avatar;
    private String reply;

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public String getCity() {
        if(TextUtils.isEmpty(city)){
            return AppContext.address;
        }
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getFollow() {
        return follow;
    }

    public void setFollow(String follow) {
        this.follow = follow;
    }

    public String getForward() {
        return forward;
    }

    public void setForward(String forward) {
        this.forward = forward;
    }

    public String getCover_url() {
        return cover_url;
    }

    public void setCover_url(String cover_url) {
        this.cover_url = cover_url;
    }

    public String getVideoid() {
        return videoid;
    }

    public void setVideoid(String videoid) {
        this.videoid = videoid;
    }

    public ShortVideoBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.uid);
        dest.writeString(this.video_url);
        dest.writeString(this.title);
        dest.writeString(this.addtime);
        dest.writeString(this.city);
        dest.writeString(this.follow);
        dest.writeString(this.forward);
        dest.writeString(this.cover_url);
        dest.writeString(this.videoid);
        dest.writeString(this.user_nicename);
        dest.writeString(this.avatar);
    }

    protected ShortVideoBean(Parcel in) {
        this.id = in.readString();
        this.uid = in.readString();
        this.video_url = in.readString();
        this.title = in.readString();
        this.addtime = in.readString();
        this.city = in.readString();
        this.follow = in.readString();
        this.forward = in.readString();
        this.cover_url = in.readString();
        this.videoid = in.readString();
        this.user_nicename = in.readString();
        this.avatar = in.readString();
    }

    public static final Creator<ShortVideoBean> CREATOR = new Creator<ShortVideoBean>() {
        @Override
        public ShortVideoBean createFromParcel(Parcel source) {
            return new ShortVideoBean(source);
        }

        @Override
        public ShortVideoBean[] newArray(int size) {
            return new ShortVideoBean[size];
        }
    };
}
