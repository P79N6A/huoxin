package com.daimeng.livee.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

// TODO: 2018/10/20 by liu 礼物弹幕
//礼物弹幕包含信息
public class GiftDanmuEntity implements Parcelable {
    public static final int DANMAKU_TYPE_SYSTEM = 0;// 系统弹幕消息
    public static final int DANMAKU_TYPE_USERCHAT = 1;// 用户聊天弹幕消息
    private int userId;
    private String sender;
    private String receiver;
    private String gift;
    private String giftImage;
    private String trackType; //1: da
    private String stream;
    private String broadcast_msg;
    private String broadcast_type;

    public String getBroadcast_type() {
        return broadcast_type;
    }

    public void setBroadcast_type(String broadcast_type) {
        this.broadcast_type = broadcast_type;
    }

    public String getBroadcast_msg() {
        return broadcast_msg;
    }

    public void setBroadcast_msg(String broadcast_msg) {
        this.broadcast_msg = broadcast_msg;
    }

    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

    public String getTrackType() {
        return trackType;
    }

    public void setTrackType(String trackType) {
        this.trackType = trackType;
    }

    //直播间
     private String showid;

    public String getShowid() {
        return showid;
    }

    public void setShowid(String showid) {
        this.showid = showid;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    private int type;// 0是系统公屏，1是用户弹幕信息

    private String text;
    private ArrayList<RichMessage> richText; // 富文本

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getGift() {
        return gift;
    }

    public void setGift(String gift) {
        this.gift = gift;
    }

    public String getGiftImage() {
        return giftImage;
    }

    public void setGiftImage(String giftImage) {
        this.giftImage = giftImage;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ArrayList<RichMessage> getRichText() {
        return richText;
    }

    public void setRichText(ArrayList<RichMessage> richText) {
        this.richText = richText;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.sender);
        dest.writeString(this.receiver);
        dest.writeInt(this.userId);
        dest.writeString(this.gift);
        dest.writeString(this.giftImage);
        dest.writeInt(this.type);
        dest.writeString(this.text);
        dest.writeTypedList(this.richText);
    }

    public GiftDanmuEntity() {
    }

    protected GiftDanmuEntity(Parcel in) {
        this.sender = in.readString();
        this.receiver = in.readString();
        this.userId = in.readInt();
        this.gift = in.readString();
        this.giftImage = in.readString();
        this.type = in.readInt();
        this.text = in.readString();
        this.richText = in.createTypedArrayList(RichMessage.CREATOR);
    }

    public static final Parcelable.Creator<GiftDanmuEntity> CREATOR = new Parcelable.Creator<GiftDanmuEntity>() {
        @Override
        public GiftDanmuEntity createFromParcel(Parcel source) {
            return new GiftDanmuEntity(source);
        }

        @Override
        public GiftDanmuEntity[] newArray(int size) {
            return new GiftDanmuEntity[size];
        }
    };
}
