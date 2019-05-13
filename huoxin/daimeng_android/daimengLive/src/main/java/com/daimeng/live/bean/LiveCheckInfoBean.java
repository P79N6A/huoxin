package com.daimeng.live.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by weipeng on 2017/10/9.
 */

public class LiveCheckInfoBean implements Parcelable {

    private String type;
    private String is_first;
    private String time;
    private String type_val;
    private String type_msg;
    private LiveBean live_info;

    public LiveBean getLive_info() {
        return live_info;
    }

    public void setLive_info(LiveBean live_info) {
        this.live_info = live_info;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIs_first() {
        return is_first;
    }

    public void setIs_first(String is_first) {
        this.is_first = is_first;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType_val() {
        return type_val;
    }

    public void setType_val(String type_val) {
        this.type_val = type_val;
    }

    public String getType_msg() {
        return type_msg;
    }

    public void setType_msg(String type_msg) {
        this.type_msg = type_msg;
    }

    public LiveCheckInfoBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.type);
        dest.writeString(this.is_first);
        dest.writeString(this.time);
        dest.writeString(this.type_val);
        dest.writeString(this.type_msg);
        dest.writeParcelable(this.live_info, flags);
    }

    protected LiveCheckInfoBean(Parcel in) {
        this.type = in.readString();
        this.is_first = in.readString();
        this.time = in.readString();
        this.type_val = in.readString();
        this.type_msg = in.readString();
        this.live_info = in.readParcelable(LiveBean.class.getClassLoader());
    }

    public static final Creator<LiveCheckInfoBean> CREATOR = new Creator<LiveCheckInfoBean>() {
        @Override
        public LiveCheckInfoBean createFromParcel(Parcel source) {
            return new LiveCheckInfoBean(source);
        }

        @Override
        public LiveCheckInfoBean[] newArray(int size) {
            return new LiveCheckInfoBean[size];
        }
    };
}
