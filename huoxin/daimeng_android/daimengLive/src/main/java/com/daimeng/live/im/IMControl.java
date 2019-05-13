package com.daimeng.live.im;


import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.daimeng.live.Constant;
import com.daimeng.live.R;
import com.daimeng.live.bean.ChatBean;
import com.daimeng.live.bean.GiftDanmuEntity;
import com.daimeng.live.bean.RedEnvelopeMsgBean;
import com.daimeng.live.bean.SendGiftBean;
import com.daimeng.live.bean.SimpleUserInfo;
import com.daimeng.live.bean.UserBean;
import com.daimeng.live.interf.IMControlInterface;
import com.daimeng.live.utils.SocketMsgUtils;
import com.daimeng.live.utils.StringUtils;
import com.daimeng.live.utils.TLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * 直播间业务逻辑处理
 */
public class IMControl {

    public static final String EVENT_NAME = "broadcast";

    private static final int SEND_CHAT = 2;//发言
    private static final int SYSTEM_NOT = 1;//系统消息
    private static final int NOTICE = 0;//提醒
    private static final int PRIVELEGE = 4;//特权操作
    private static final int GAME_MESSAGE = 5;//游戏消息
    private static final int LIVE_RTC = 18;//连麦消息
    private static final int JINHUA_GAME_MSG = 15;
    private static final int RED_PACK = 22;//红包
    public static int LIVE_USER_NUMS = 0;

    private Socket mSocket;

    private Context context;

    private IMControlInterface mIMControl;

    //服务器连接关闭监听
    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            TLog.log("socket断开连接");
        }
    };

    //服务器连接失败监听
    private Emitter.Listener onError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mIMControl.onError();
            TLog.log("socket连接Error");
        }
    };

    //TODO 服务器红包信息监听
    //服务器消息监听
    private Emitter.Listener onMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try {
                JSONArray jsonArray = (JSONArray) args[0];
                for (int i = 0; i < jsonArray.length(); i++) {
                    if (jsonArray.getString(i).equals("stopplay")) {
                        mIMControl.onRoomClose(1);
                        return;
                    }
                    SocketMsgUtils socketMsg = SocketMsgUtils.getFormatJsonMode(jsonArray.getString(i));

                    int action = StringUtils.toInt(socketMsg.getAction());

                    //获取用户动作
                    switch (StringUtils.toInt(socketMsg.getMsgtype())) {
                        case RED_PACK:
                            if (action == 1) {
                                mIMControl.onSendRedPacket(socketMsg);
                                //mIMControl.onRedPackMessage(socketMsg);
                            }
                            break;
                        case GAME_MESSAGE:
                            //水果蔬菜游戏开始
                            if (action == 1) {
                                mIMControl.onGameStart(socketMsg);
                            } else if (action == 2) {
                                //揭晓结果
                                mIMControl.onGameOpenResult(socketMsg);
                            } else if (action == 0) {
                                mIMControl.onOpenGame(socketMsg);
                            } else if (action == 3) {
                                //下注
                                mIMControl.onGameBet(socketMsg);
                            } else if (action == 4) {
                                //结束游戏
                                mIMControl.onGameEnd(socketMsg);
                            }

                            break;
                        case SEND_CHAT://聊天
                            //公聊
                            if (action == 0) {
                                onMessage(socketMsg);
                            }
                            break;
                        case SYSTEM_NOT://系统
                            if (action == 0) {
                                //发送礼物
                                try {
                                    onSendGift(socketMsg);
                                } catch (Exception e) {
                                    TLog.log("send gift error :" + e.getMessage());
                                }
                            } else if (action == 18) {
                                //房间关闭
                                mIMControl.onRoomClose(0);
                            } else if (action == 7) {
                                //弹幕
                                onDanmuMessage(socketMsg);
                            } else if (action == 19) {
                                //房间关闭
                                mIMControl.onRoomClose(1);
                            } else if (action == 2) {
                                //全站广播通知
                                mIMControl.onAllGiftMessage(socketMsg);
                            } else if (action == 21) {
                                // onTrack(socketMsg);
                                mIMControl.onShowTrack(socketMsg);
                            } else if (action == 22) {
                                mIMControl.onSendRedPacket(socketMsg);
                            }else if (action == 23){
                                 //玩游戏送的礼物
                                mIMControl.onSendGameGift(socketMsg);
                            }
                            break;
                        case NOTICE://通知
                            if (action == 0) {
                                //上下线
                                IMControl.LIVE_USER_NUMS += 1;
                                mIMControl.onUserStateChange(socketMsg, JSON.parseObject(socketMsg.getCt(), UserBean.class), true);
                            } else if (action == 1) {
                                IMControl.LIVE_USER_NUMS -= 1;
                                mIMControl.onUserStateChange(socketMsg, JSON.parseObject(socketMsg.getCt(), UserBean.class), false);
                            } else if (action == 2) {
                                //点亮
                                mIMControl.onLit(socketMsg);
                            } else if (action == 3) {
                                //僵尸粉丝推送
                                mIMControl.onAddZombieFans(socketMsg);
                            }
                            break;
                        case PRIVELEGE:
                            onPrivateAction(socketMsg);
                            break;
                        case JINHUA_GAME_MSG:
                            onJinhuaGameMsg(socketMsg);
                            break;
                        case LIVE_RTC:
                            if (action == 1) {
                                //申请连麦
                                mIMControl.onRequestRtc(socketMsg);
                            }
                            if (action == 2) {
                                //回复
                                mIMControl.onRequestReplyRtc(socketMsg);
                            }
                            if (action == 3) {
                                //关闭连麦
                                mIMControl.onRequestEndRtc(socketMsg);
                            }
                            break;
                        default:
                            break;
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


    // TODO  by wang
    private void onSendRedPacket(SocketMsgUtils msgUtils) {

        RedEnvelopeMsgBean c = new RedEnvelopeMsgBean();
        SimpleUserInfo userInfo = new SimpleUserInfo();
        userInfo.id = msgUtils.getUid();
        userInfo.level = msgUtils.getLevel();
        userInfo.user_nicename = msgUtils.getUname();
        userInfo.avatar = msgUtils.getUHead();
        c.setmSimpleUserInfo(userInfo);

        SendGiftBean mSendGiftInfo = JSON.parseObject(msgUtils.getCt(), SendGiftBean.class);//gift info

        mSendGiftInfo.setAvatar(userInfo.avatar);
        mSendGiftInfo.setEvensend(msgUtils.getParam("evensend", "n"));
        mSendGiftInfo.setNicename(userInfo.user_nicename);
    }

    //游戏
    private void onJinhuaGameMsg(SocketMsgUtils socketMsg) {

        mIMControl.onJinhuaGameMessageListen(socketMsg);

    }

    //特权
    private void onPrivateAction(SocketMsgUtils msgUtils) {
        if(msgUtils.getAction().equals("23")){
            mIMControl.onSendGameGift(msgUtils);
        }else {

            ChatBean c = new ChatBean();
            c.setType(Constant.IM_TEXT_TYPE.SYSTEM_MSG);
            c.setSendChatMsg(msgUtils.getCt());
            c.setUserNick("系统消息");
            if (msgUtils.getAction().equals("1")) {
                mIMControl.onShutUp(msgUtils, c);
            } else if (msgUtils.getAction().equals("2")) {
                mIMControl.onKickRoom(msgUtils, c);
            } else if (msgUtils.getAction().equals("5")) {
                mIMControl.onOpenTimeLive(msgUtils, c);
            } else {
                mIMControl.onPrivilegeAction(msgUtils, c);
            }

        }



    }

    private void onDanmuMessage(SocketMsgUtils msgUtils) throws JSONException {

        String ct = msgUtils.getCt();
        ChatBean c = new ChatBean();
        SimpleUserInfo userInfo = new SimpleUserInfo();
        userInfo.id = msgUtils.getUid();
        userInfo.level = msgUtils.getLevel();
        userInfo.user_nicename = msgUtils.getUname();
        userInfo.avatar = msgUtils.getUHead();
        c.setSimpleUserInfo(userInfo);

        JSONObject jsonObject = new JSONObject(ct);

        c.setContent(jsonObject.getString("content"));
        mIMControl.onDanMuMessage(msgUtils, c);
    }

    private void onTrack(SocketMsgUtils msgUtils) {
        mIMControl.onShowTrack(msgUtils);
    }

    //礼物信息
    private void onSendGift(SocketMsgUtils msgUtils) {

        ChatBean c = new ChatBean();
        SimpleUserInfo userInfo = new SimpleUserInfo();
        userInfo.id = msgUtils.getUid();
        userInfo.sex = msgUtils.getParam("sex","0");
        userInfo.level = msgUtils.getLevel();
        userInfo.user_nicename = msgUtils.getUname();
        userInfo.avatar = msgUtils.getUHead();
        c.is_vip = msgUtils.getParam("is_vip", "0");
        c.setSimpleUserInfo(userInfo);

        SendGiftBean mSendGiftInfo = JSON.parseObject(msgUtils.getCt(), SendGiftBean.class);//gift info

        mSendGiftInfo.setAvatar(userInfo.avatar);
        mSendGiftInfo.setEvensend(msgUtils.getParam("evensend", "n"));
        mSendGiftInfo.setNicename(userInfo.user_nicename);

        c.setType(Constant.IM_TEXT_TYPE.GIFT_MSG);
        c.setSendChatMsg("我送了" + mSendGiftInfo.getGiftcount() + "个" + mSendGiftInfo.getGiftname());
        c.setUserNick(userInfo.user_nicename);


        mIMControl.onShowSendGift(mSendGiftInfo, c);
    }



    //消息信息
    private void onMessage(SocketMsgUtils msgUtils) {
        ChatBean c = new ChatBean();
        SimpleUserInfo userInfo = new SimpleUserInfo();
        userInfo.id = msgUtils.getUid();
        userInfo.level = msgUtils.getLevel();
        userInfo.sex =  msgUtils.getParam("sex","0");

        userInfo.user_nicename = msgUtils.getUname();
        userInfo.avatar = msgUtils.getUHead();
        userInfo.custombackground = (msgUtils.getParam("custom_background", ""));
        userInfo.guardicon= (msgUtils.getParam("guardicon", ""));
        c.is_vip = msgUtils.getParam("is_vip", "0");
        c.setSimpleUserInfo(userInfo);
        c.setType(Constant.IM_TEXT_TYPE.TEXT_MSG);
        c.setSendChatMsg(msgUtils.getCt());
        c.setUserNick(msgUtils.getUname());
        mIMControl.onMessageListen(msgUtils, 2, c);
    }


    //服务器连接结果监听
    private Emitter.Listener onConn = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            JSONArray jsonArray = (JSONArray) args[0];
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    if (jsonArray.getString(i).equals("ok")) {
                        mIMControl.onConnect(true);
                    } else {
                        mIMControl.onConnect(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public IMControl(IMControlInterface IMControlInterface, Context context, String chaturl) throws URISyntaxException {
        this.mIMControl = IMControlInterface;
        this.context = context;

        try {

            IO.Options option = new IO.Options();
            option.forceNew = true;
            option.reconnection = true;
            option.reconnectionDelay = 2000;
            mSocket = IO.socket(chaturl, option);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }


    /**
     * @param u 用户信息json格式
     * @dw 连接socket服务端
     */
    public void connectSocketServer(UserBean u, final String stream, String liveuid) {
        publicSocketInitAction(u, stream, liveuid);
    }

    //公共的初始化方法
    public void publicSocketInitAction(final UserBean u, final String stream, final String liveuid) {

        if (null == mSocket) return;
        try {
            mSocket.connect();
            JSONObject dataJson = new JSONObject();
            dataJson.put("uid", u.id);
            dataJson.put("token", u.token);
            dataJson.put("roomnum", liveuid);
            dataJson.put("stream", stream);
            mSocket.emit("conn", dataJson);

            mSocket.on("conn", onConn);
            mSocket.on("broadcastingListen", onMessage);
            mSocket.on(mSocket.EVENT_DISCONNECT, onDisconnect);
            mSocket.on(mSocket.EVENT_ERROR, onError);
            mSocket.on(mSocket.EVENT_RECONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {

                    TLog.log("重连");
                    try {
                        JSONObject dataJson = new JSONObject();
                        dataJson.put("uid", u.id);
                        dataJson.put("token", u.token);
                        dataJson.put("roomnum", stream);
                        dataJson.put("liveuid", liveuid);
                        mSocket.emit("conn", dataJson);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //跑道
    public void doTrack(GiftDanmuEntity g, String token, UserBean mUser, String Tracktype, String showid, String evensend) {
        SocketMsgUtils.getNewJsonMode()
                .set_method_("SystemNot")
                .setAction("21")
                .setMsgtype(String.valueOf(SYSTEM_NOT))
                .setMyUserInfo(mUser)
                .addParamToJson1("uhead", mUser.avatar_thumb)
                .addParamToJson1("sendname", g.getSender())
                .addParamToJson1("receivename", g.getReceiver())
                .addParamToJson1("gift", g.getGift())
                .addParamToJson1("Tracktype", Tracktype)
                .addParamToJson1("showid", showid)
                .addParamToJson1("giftImg", g.getGiftImage())
                .setCt(token)
                .addParamToJson1("evensend", evensend)
                .build()
                .sendMessage(mSocket);

    }

    //红包
    public void doSendRedPacket(UserBean mUser, String roomnum, String redid) {
        SocketMsgUtils.getNewJsonMode()
                .set_method_("SendHb")
                .setAction("1")
                .setMsgtype(String.valueOf(RED_PACK))
                .setMyUserInfo(mUser)
                .addParamToJson1("uhead", mUser.avatar_thumb)
                .addParamToJson1("uid", mUser.id)
                .addParamToJson1("uname", mUser.user_nicename)
                .setCt(redid)
                .addParamToJson1("roomnum", roomnum)
                .build()
                .sendMessage(mSocket);
    }

    //关闭房间
    public void closeLive() {

        SocketMsgUtils.getNewJsonMode()
                .set_method_("StartEndLive")
                .setAction("18")
                .setMsgtype(String.valueOf(SYSTEM_NOT))
                .setCt(context.getString(R.string.livestart))
                .build()
                .sendMessage(mSocket);
    }

    //超管关闭直播
    public void doSetCloseLive() {

        SocketMsgUtils.getNewJsonMode()
                .set_method_("stopLive")
                .setAction("19")
                .setMsgtype(String.valueOf(SYSTEM_NOT))
                .setCt(context.getString(R.string.livestart))
                .build()
                .sendMessage(mSocket);

    }

    /**
     * @param mUser    用户信息
     * @param evensend 是否在连送规定时间内
     * @dw token 发送礼物凭证
     */
    public void doSendGift(String token, UserBean mUser, String evensend, String is_vip) {
        SocketMsgUtils.getNewJsonMode()
                .set_method_("SendGift")
                .setAction("0")
                .setMsgtype(String.valueOf(SYSTEM_NOT))
                .setMyUserInfo(mUser)
                .addParamToJson1("uhead", mUser.avatar_thumb)
                .addParamToJson1("is_vip", is_vip)
                .addParamToJson1("evensend", evensend)
                .addParamToJson1("custom_background", mUser.custombackground)
                .setCt(token)
                .build()
                .sendMessage(mSocket);

    }

    public void doSendRedPacket(String token, UserBean mUser) {
        SocketMsgUtils.getNewJsonMode()
                .set_method_("SendRedPacket")
                .setAction("999")
                .setMsgtype(String.valueOf(SYSTEM_NOT))
                .setMyUserInfo(mUser)
                .addParamToJson1("uhead", mUser.avatar_thumb)
                .setCt(token)
                .build()
                .sendMessage(mSocket);

    }


    /**
     * @param mUser 用户信息
     * @dw token 发送弹幕凭证
     */
    public void doSendBarrage(String token, UserBean mUser) {
        SocketMsgUtils.getNewJsonMode()
                .set_method_("SendBarrage")
                .setAction("7")
                .setMsgtype(String.valueOf(SYSTEM_NOT))
                .setMyUserInfo(mUser)
                .addParamToJson1("uhead", mUser.avatar_thumb)
                .setCt(token)
                .build()
                .sendMessage(mSocket);

    }

    /**
     * @param mUser   当前用户bean
     * @param mToUser 被操作用户bean
     * @dw 禁言
     */
    public void doSetShutUp(UserBean mUser, SimpleUserInfo mToUser) {

        SocketMsgUtils.getNewJsonMode()
                .set_method_("ShutUpUser")
                .setAction("1")
                .setMsgtype(String.valueOf(PRIVELEGE))
                .setMyUserInfo(mUser)
                .set2UserInfo(mToUser)
                .setCt(mToUser.user_nicename + "被禁言30分钟")
                .build()
                .sendMessage(mSocket);

    }

    //踢人
    public void doSetKick(UserBean mUser, SimpleUserInfo mToUser) {

        SocketMsgUtils.getNewJsonMode()
                .set_method_("KickUser")
                .setAction("2")
                .setMsgtype(String.valueOf(PRIVELEGE))
                .setMyUserInfo(mUser)
                .set2UserInfo(mToUser)
                .setCt(mToUser.user_nicename + "被踢出房间")
                .build()
                .sendMessage(mSocket);
    }

    //设为管理员
    public void doSetOrRemoveManage(UserBean user, SimpleUserInfo touser, String content) {

        SocketMsgUtils.getNewJsonMode()
                .set_method_("SystemNot")
                .setAction("13")
                .setMsgtype(String.valueOf(PRIVELEGE))
                .setMyUserInfo(user)
                .set2UserInfo(touser)
                .setCt(content)
                .build()
                .sendMessage(mSocket);


    }

    //发送系统消息
    public void doSendSystemMessage(String msg, UserBean user, int isFollow) {

        SocketMsgUtils.getNewJsonMode()
                .set_method_("SystemNot")
                .setAction("13")
                .addParamToJson1("is_follow", String.valueOf(isFollow))
                .setMsgtype(String.valueOf(PRIVELEGE))
                .setCt(msg)
                .build()
                .sendMessage(mSocket);
    }

    /**
     * @param sendMsg 发言内容
     * @param user    用户信息
     * @dw 发言
     */
    public void doSendMsg(String sendMsg, UserBean user, int is_vip, int reply, int isSendGift) {
        SocketMsgUtils.getNewJsonMode()
                .set_method_("SendMsg")
                .setAction("0")
                .setMsgtype(String.valueOf(SEND_CHAT))
                .setMyUserInfo(user)
                .addParamToJson1("is_send_gift", String.valueOf(isSendGift))
                .addParamToJson1("is_vip", String.valueOf(is_vip))
                .addParamToJson1("custom_background", user.custombackground)
                .addParamToJson1("guardicon",user.guardicon)
                .setCt(sendMsg)
                .build()
                .sendMessage(mSocket);
    }


    /**
     * @param index
     * @param user  用户信息
     * @dw 我点亮了
     */
    public void doSendLitMsg(UserBean user, int index) {

        SocketMsgUtils.getNewJsonMode()
                .set_method_("SendMsg")
                .setAction("0")
                .setMsgtype("2")
                .setMyUserInfo(user)
                .setCt("我点亮了")
                .addParamToJson1("heart", String.valueOf(index + 1))
                .build()
                .sendMessage(mSocket);

    }

    //获取僵尸粉丝
    public void getZombieFans() {

        SocketMsgUtils.getNewJsonMode()
                .set_method_("requestFans")
                .setAction("")
                .setMsgtype("")
                .build()
                .sendMessage(mSocket);

    }

    //打开游戏
    public void doSendOpenGame(int type) {

        SocketMsgUtils.getNewJsonMode()
                .set_method_("gameMessage")
                .setAction("0")
                .addParamToJson1("type", String.valueOf(type))
                .setMsgtype(String.valueOf(GAME_MESSAGE))
                .build()
                .sendMessage(mSocket);

    }

    //开始游戏水果游戏
    public void doSendStartFruitsGame(String gameId, String gameToken, String time) {
        SocketMsgUtils.getNewJsonMode()
                .set_method_("startFruits")
                .setAction("1")
                .setMsgtype(String.valueOf(GAME_MESSAGE))
                .addParamToJson1("gameid", gameId)
                .addParamToJson1("gametoken", gameToken)
                .addParamToJson1("time", time)
                .build()
                .sendMessage(mSocket);
    }

    //发送押注信息
    public void doSendBetMessage(String grade, String coin) {
        SocketMsgUtils.getNewJsonMode()
                .set_method_("gameMessage")
                .setAction("3")
                .setMsgtype(String.valueOf(GAME_MESSAGE))
                .addParamToJson1("grade", grade)
                .addParamToJson1("coin", coin)
                .build()
                .sendMessage(mSocket);
    }

    //结束游戏
    public void doSendEndGame() {
        SocketMsgUtils.getNewJsonMode()
                .set_method_("gameMessage")
                .setAction("4")
                .setMsgtype(String.valueOf(GAME_MESSAGE))
                .build()
                .sendMessage(mSocket);
    }


    //打开定时计费房间
    public void doSendOpenTimeLive(String time, String money) {
        SocketMsgUtils.getNewJsonMode()
                .set_method_("SystemNot")
                .setAction("5")
                .addParamToJson1("time", time)
                .addParamToJson1("money", money)
                .setMsgtype(String.valueOf(PRIVELEGE))
                .build()
                .sendMessage(mSocket);
    }

    //打开游戏画面
    public void doSendOpenJinhuaGame(int type) {

        SocketMsgUtils.getNewJsonMode()
                .set_method_("jinhuaGame")
                .setAction("1")
                .setMsgtype("15")
                .addParamToJson1("type", String.valueOf(type))
                .build()
                .sendMessage(mSocket);

    }

    //金花开始发牌
    public void doSendJinhuaLicensing(UserBean user, String gameid) {
        if (null == mSocket) {
            return;
        }

        SocketMsgUtils.getNewJsonMode()
                .set_method_("jinhuaGame")
                .setAction("2")
                .setMsgtype("15")
                .addParamToJson1("gameid", gameid)
                .setMyUserInfo(user)
                .build()
                .sendMessage(mSocket);
    }

    //开始倒计时
    public void doSendStartJinhuaGameCountDown(UserBean user, String jinhua_token, String gameid, String token, String time) {
        if (null == mSocket) {
            return;
        }

        SocketMsgUtils.getNewJsonMode()
                .set_method_("jinhuaGame")
                .setAction("4")
                .setMsgtype("15")
                .setMyUserInfo(user)
                .addParamToJson1("liveuid", user.id)
                .addParamToJson1("token", jinhua_token)
                .addParamToJson1("gameid", gameid)
                .addParamToJson1("time", time)
                .build()
                .sendMessage(mSocket);
    }

    //下注
    public void doSendJinhuaBetting(String coin, String type, UserBean user) {
        if (null == mSocket) {
            return;
        }

        SocketMsgUtils.getNewJsonMode()
                .set_method_("jinhuaGame")
                .setAction("5")
                .setMsgtype("17")
                .setMyUserInfo(user)
                .addParamToJson1("money", coin)
                .addParamToJson1("type", type)
                .build()
                .sendMessage(mSocket);
    }

    //发送申请连麦消息
    public void doSendRequestRtc(UserBean user, String toUserId) {
        if (null == mSocket) {
            return;
        }
        SocketMsgUtils.getNewJsonMode()
                .set_method_("requestRtc")
                .setAction("1")
                .setMsgtype(String.valueOf(LIVE_RTC))
                .setMyUserInfo(user)
                .addParamToJson1("to_user_id", toUserId)
                .build()
                .sendMessage(mSocket);
    }


    //回复连麦
    public void doSendRequestReplyRtc(UserBean mUser, String toUserId, int action) {
        if (null == mSocket) {
            return;
        }
        SocketMsgUtils.getNewJsonMode()
                .set_method_("requestRtc")
                .setAction("2")
                .setMsgtype(String.valueOf(LIVE_RTC))
                .setMyUserInfo(mUser)
                .addParamToJson1("to_user_id", toUserId)
                .addParamToJson1("reply_action", String.valueOf(action))
                .build()
                .sendMessage(mSocket);
    }


    //结束连麦
    public void doSendRequestEndRtc(UserBean mUser, String toUserId) {
        if (null == mSocket) {
            return;
        }
        SocketMsgUtils.getNewJsonMode()
                .set_method_("requestRtc")
                .setAction("3")
                .setMsgtype(String.valueOf(LIVE_RTC))
                .setMyUserInfo(mUser)
                .addParamToJson1("to_user_id", toUserId)
                .build()
                .sendMessage(mSocket);
    }

    //结束游戏
    public void doSendEndJinhuaGame() {
        if (null == mSocket) {
            return;
        }

        SocketMsgUtils.getNewJsonMode()
                .set_method_("jinhuaGame")
                .setAction("3")
                .setMsgtype("15")
                .build()
                .sendMessage(mSocket);
    }

    //释放资源
    public void close() {
        if (mSocket != null) {
            mSocket.disconnect();
            mSocket.off("conn");
            mSocket.off("broadcastingListen");
            mSocket.off();
            mSocket.close();
            mSocket = null;
        }

    }


}
