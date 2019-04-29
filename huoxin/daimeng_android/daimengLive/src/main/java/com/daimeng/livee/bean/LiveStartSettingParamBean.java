package com.daimeng.livee.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by weipeng on 2017/10/8.
 */

public class LiveStartSettingParamBean implements Parcelable {

    private String stream;
    private String barrage_fee;
    private String votestotal;
    private String push;
    private String chatserver;
    private String is_vip;
    private String type;
    private boolean isFrontCameraMirro;
    private List<String> words;

    public List<String> getWords() {
        return words;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isFrontCameraMirro() {
        return isFrontCameraMirro;
    }

    public void setFrontCameraMirro(boolean frontCameraMirro) {
        isFrontCameraMirro = frontCameraMirro;
    }

    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

    public String getBarrage_fee() {
        return barrage_fee;
    }

    public void setBarrage_fee(String barrage_fee) {
        this.barrage_fee = barrage_fee;
    }

    public String getVotestotal() {
        return votestotal;
    }

    public void setVotestotal(String votestotal) {
        this.votestotal = votestotal;
    }

    public String getPush() {
        return push;
    }

    public void setPush(String push) {
        this.push = push;
    }

    public String getChatserver() {
        return chatserver;
    }

    public void setChatserver(String chatserver) {
        this.chatserver = chatserver;
    }

    public String getIs_vip() {
        return is_vip;
    }

    public void setIs_vip(String is_vip) {
        this.is_vip = is_vip;
    }

    public LiveStartSettingParamBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.stream);
        dest.writeString(this.barrage_fee);
        dest.writeString(this.votestotal);
        dest.writeString(this.push);
        dest.writeString(this.chatserver);
        dest.writeString(this.is_vip);
        dest.writeString(this.type);
        dest.writeByte(this.isFrontCameraMirro ? (byte) 1 : (byte) 0);
        dest.writeStringList(this.words);
    }

    protected LiveStartSettingParamBean(Parcel in) {
        this.stream = in.readString();
        this.barrage_fee = in.readString();
        this.votestotal = in.readString();
        this.push = in.readString();
        this.chatserver = in.readString();
        this.is_vip = in.readString();
        this.type = in.readString();
        this.isFrontCameraMirro = in.readByte() != 0;
        this.words = in.createStringArrayList();
    }

    public static final Creator<LiveStartSettingParamBean> CREATOR = new Creator<LiveStartSettingParamBean>() {
        @Override
        public LiveStartSettingParamBean createFromParcel(Parcel source) {
            return new LiveStartSettingParamBean(source);
        }

        @Override
        public LiveStartSettingParamBean[] newArray(int size) {
            return new LiveStartSettingParamBean[size];
        }
    };
}
