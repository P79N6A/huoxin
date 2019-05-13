package com.daimeng.live.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by daimeng on 2017/1/17.
 */

public class LiveBean implements Parcelable {


    public String uid;
    public String avatar;
    public String avatar_thumb;
    public String user_nicename;
    public String title;
    public String city;
    public String stream;
    public String nums;
    public String distance;
    public String pull;
    public String thumb;
    public String type;
    public String type_val;
    public String level;



    public LiveBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uid);
        dest.writeString(this.avatar);
        dest.writeString(this.avatar_thumb);
        dest.writeString(this.user_nicename);
        dest.writeString(this.title);
        dest.writeString(this.city);
        dest.writeString(this.stream);
        dest.writeString(this.nums);
        dest.writeString(this.distance);
        dest.writeString(this.pull);
        dest.writeString(this.thumb);
        dest.writeString(this.type);
        dest.writeString(this.type_val);
        dest.writeString(this.level);
    }

    protected LiveBean(Parcel in) {
        this.uid = in.readString();
        this.avatar = in.readString();
        this.avatar_thumb = in.readString();
        this.user_nicename = in.readString();
        this.title = in.readString();
        this.city = in.readString();
        this.stream = in.readString();
        this.nums = in.readString();
        this.distance = in.readString();
        this.pull = in.readString();
        this.thumb = in.readString();
        this.type = in.readString();
        this.type_val = in.readString();
        this.level = in.readString();
    }

    public static final Creator<LiveBean> CREATOR = new Creator<LiveBean>() {
        @Override
        public LiveBean createFromParcel(Parcel source) {
            return new LiveBean(source);
        }

        @Override
        public LiveBean[] newArray(int size) {
            return new LiveBean[size];
        }
    };
}
