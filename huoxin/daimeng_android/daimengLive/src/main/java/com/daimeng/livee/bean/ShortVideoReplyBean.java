package com.daimeng.livee.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by weipeng on 2017/10/18.
 */

public class ShortVideoReplyBean implements Parcelable {


    /**
     * id : 2
     * uid : 156
     * body : 看看
     * addtime : 2017-10-17 17:44:49
     * vid : 15
     * rid : 0
     * user_nicename : 静止
     * avatar : http://q.qlogo.cn/qqapp/100371282/E5EE2DBA78F5E7B2AE4C900F4DC3E235/40
     */

    private String id;
    private String uid;
    private String body;
    private String addtime;
    private String vid;
    private String rid;
    private String user_nicename;
    private String avatar;
    private String reply_comment_count;

    public String getReply_comment_count() {
        return reply_comment_count;
    }

    public void setReply_comment_count(String reply_comment_count) {
        this.reply_comment_count = reply_comment_count;
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

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
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

    public ShortVideoReplyBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.uid);
        dest.writeString(this.body);
        dest.writeString(this.addtime);
        dest.writeString(this.vid);
        dest.writeString(this.rid);
        dest.writeString(this.user_nicename);
        dest.writeString(this.avatar);
        dest.writeString(this.reply_comment_count);
    }

    protected ShortVideoReplyBean(Parcel in) {
        this.id = in.readString();
        this.uid = in.readString();
        this.body = in.readString();
        this.addtime = in.readString();
        this.vid = in.readString();
        this.rid = in.readString();
        this.user_nicename = in.readString();
        this.avatar = in.readString();
        this.reply_comment_count = in.readString();
    }

    public static final Creator<ShortVideoReplyBean> CREATOR = new Creator<ShortVideoReplyBean>() {
        @Override
        public ShortVideoReplyBean createFromParcel(Parcel source) {
            return new ShortVideoReplyBean(source);
        }

        @Override
        public ShortVideoReplyBean[] newArray(int size) {
            return new ShortVideoReplyBean[size];
        }
    };
}
