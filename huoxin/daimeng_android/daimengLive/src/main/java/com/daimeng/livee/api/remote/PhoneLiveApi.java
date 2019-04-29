package com.daimeng.livee.api.remote;

import com.daimeng.livee.AppConfig;
import com.daimeng.livee.AppContext;
import com.daimeng.livee.bean.GiftBean;
import com.daimeng.livee.bean.UserBean;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import cn.sharesdk.framework.PlatformDb;

/**
 * 接口获取
 */
public class PhoneLiveApi {

    /**
    * 登陆
    * @param phone
    * @param code
    * */
    public static void login(String phone,String password,StringCallback callback){
        String url = AppConfig.MAIN_URL_API;
        try {
            OkHttpUtils.get()
                    .url(url)
                    .addParams("service","Login.userLogin")
                    .addParams("user_login",phone)
                    .addParams("user_pass",URLEncoder.encode(password,"UTF-8"))
                    .build()
                    .execute(callback);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
    public static void reg(String user_login,String user_pass,String user_pass2,String code,StringCallback callback){
        String url = AppConfig.MAIN_URL_API;
        try {
            OkHttpUtils.get()
                    .url(url)
                    .addParams("service","Login.userReg")
                    .addParams("user_login",user_login)
                    .addParams("user_pass",URLEncoder.encode(user_pass,"UTF-8"))
                    .addParams("user_pass2",URLEncoder.encode(user_pass2,"UTF-8"))
                    .addParams("code",code)
                    .build()
                    .execute(callback);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    public static void findPass(String user_login,String user_pass,String user_pass2,String code,StringCallback callback){
        String url = AppConfig.MAIN_URL_API;
        try {
            OkHttpUtils.get()
                    .url(url)
                    .addParams("service","Login.userFindPass")
                    .addParams("user_login",user_login)
                    .addParams("user_pass",URLEncoder.encode(user_pass,"UTF-8"))
                    .addParams("user_pass2",URLEncoder.encode(user_pass2,"UTF-8"))
                    .addParams("code",code)
                    .tag("findPass")
                    .build()
                    .execute(callback);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取用户信息
     * @param token appkey
     * @param uid 用户id
     * @param callback 回调
     * */
    public static void getMyUserInfo(String uid, String token,StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","User.getBaseInfo")
                .addParams("uid",uid)
                .addParams("token",token)
                .tag("getMyUserInfo")
                .build()
                .execute(callback);

    }
    /**
     * 获取其他用户信息
     * @param uid 用户id
     * @param callback 回调
     * */
    public static void getOtherUserInfo(int uid,StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","User.getUserInfo")
                .addParams("uid", String.valueOf(uid))
                .tag("getOtherUserInfo")
                .build()
                .execute(callback);

    }
    /**
     * @dw 修改用户信息

     * */
    public static void saveInfo(String fields, String uid, String token,StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service", "User.updateFields")
                .addParams("fields", fields)
                .addParams("uid",uid)
                .addParams("token",token)
                .tag("saveInfo")
                .build()
                .execute(callback);
    }

    /**
     * @dw 进入直播间初始化信息
     * @param uid 当前用户id
     * @param showId 主播id
     * @param token  token
     * */
    public static void enterRoom(String uid,String showId,String token,String address,String stream,StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Live.enterRoom")
                .addParams("uid", uid)
                .addParams("liveuid",showId)
                .addParams("token", token)
                .addParams("city", address)
                .addParams("stream",stream)
                    .tag("initRoomInfo")
                .build()
                .execute(callback);
    }


    /**
    * @dw 开始直播
     * @param uid 主播id
     * @param title 开始直播标题
     * @param token
     * */
    public static void createLive(String uid,String a1,String a2, String title, String token,String name, File file,String type,String type_val,StringCallback callback) {
        try {
            PostFormBuilder postFormBuilder = OkHttpUtils.post()
                    .url(AppConfig.MAIN_URL_API)
                    .addParams("service", "Live.createRoom")
                    .addParams("uid", String.valueOf(uid))
                    .addParams("title",URLEncoder.encode(title,"UTF-8"))
                    .addParams("user_nicename",name)
                    .addParams("avatar",a1)
                    .addParams("avatar_thumb",a2)
                    .addParams("city", AppContext.address)
                    .addParams("province",AppContext.province)
                    .addParams("lat", AppContext.lat)
                    .addParams("lng",AppContext.lng)
                    .addParams("token",token)
                    .addParams("type",type)
                    .addParams("type_val",type_val);
            if(file != null){
                postFormBuilder.addFile("file",file.getName(),file)
                        .tag("createLive")
                        .build()
                        .execute(callback);
            }else{
                postFormBuilder
                        .tag("createLive")
                        .build()
                        .execute(callback);
            }
        } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
        }

    }
    /**
    * @dw 关闭直播
    * @param token 用户的token
    * */
    public static void closeLive(String id,String token,String stream,StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Live.stopRoom")
                .addParams("uid",id)
                .addParams("token",token)
                .addParams("stream",stream)
                .tag("closeLive")
                .build()
                .execute(callback);
    }

    /**
    * @dw 获取礼物列表
    * @param callback
    * */
    public static void getGiftList(String uid,String token,StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Live.getGiftList")
                .addParams("uid",uid)
                .addParams("token",token)
                .tag("getGiftList")
                .build()
                .execute(callback);
    }

    /**
     * @dw 获取守护列表
     * @param callback
     * */
    public static void getProtectList(String uid,String token,StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Guard.ruleList")
                .addParams("uid",uid)
                .addParams("token",token)
                .tag("ruleList")
                .build()
                .execute(callback);
    }

    /**
     * @dw 获取守护详情
     * @param callback
     * */
    public static void getProtectContent(String uid,String token,String rid,StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Guard.rule")
                .addParams("uid",uid)
                .addParams("token",token)
                .addParams("rid",rid)
                .tag("getProtectContent")
                .build()
                .execute(callback);
    }

    /**
     * @dw 开通守护
     * @param callback
     * */
    public static void payProtect(String uid,String token,String liveuid,String stream,String giftid,String guardid,String guardnum,StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Guard.ruleBuy")
                .addParams("uid",uid)
                .addParams("token",token)
                .addParams("liveuid",liveuid)
                .addParams("stream",stream)
                .addParams("giftid",giftid)
                .addParams("guardid",guardid)
                .addParams("guardnum",guardnum)
                .tag("payProtect")
                .build()
                .execute(callback);
    }

    /**
    * @dw 赠送礼物
    * @param g 赠送礼物信息
    * @param mUser 用户信息
    * @param mNowRoomNum 房间号(主播id)
    * */
    public static void sendGift(UserBean mUser, GiftBean g, String mNowRoomNum,String stream, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Live.sendGift")
                .addParams("token",mUser.token)
                .addParams("uid",String.valueOf(mUser.id))
                .addParams("liveuid", String.valueOf(mNowRoomNum))
                .addParams("giftid", String.valueOf(g.getId()))
                .addParams("giftcount","1")
                .addParams("stream",stream)
                .tag("sendGift")
                .build()
                .execute(callback);
    }

    /**
     * @dw 生成红包
     * */
    public static void createRedbag(String uid, String token, String liveuid,String stream,String redcoin,String rednum, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Redbag.red")
                .addParams("token",token)
                .addParams("uid",uid)
                .addParams("liveuid", liveuid)
                .addParams("rednum", rednum)
                .addParams("redcoin",redcoin)
                .addParams("stream",stream)
                .tag("createRedbag")
                .build()
                .execute(callback);
    }

    /**
     * @dw 抢红包
     * */
    public static void grabRedbag(String uid, String token, String liveuid,String stream,String redid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Redbag.gabredbag")
                .addParams("token",token)
                .addParams("uid",uid)
                .addParams("liveuid", liveuid)
                .addParams("redid", redid)
                .addParams("stream",stream)
                .tag("grabRedbag")
                .build()
                .execute(callback);
    }

    /**
     * @dw 抢红包记录接口
     * */
    public static void redList(String redid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Redbag.redlist")
                .addParams("redid", redid)
                .tag("redlist")
                .build()
                .execute(callback);
    }

    /**
     * @dw 红包金额、数量，数据接口
     * */
    public static void getRedNum(String uid,StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Redbag.rednum")
                .addParams("uid",uid)
                .tag("rednum")
                .build()
                .execute(callback);
    }


     /**
     * @dw 发送弹幕
     * @param content  弹幕信息
     * @param mUser 用户信息
     * @param mNowRoomNum 房间号(主播id)
     * */
    public static void sendBarrage(UserBean mUser, String content, String mNowRoomNum,String stream, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Live.sendBarrage")
                .addParams("token",mUser.token)
                .addParams("uid",mUser.id)
                .addParams("liveuid",mNowRoomNum)
                .addParams("content",content)
                .addParams("stream",stream)
                .addParams("giftid","1")
                .addParams("giftcount","1")
                .tag("sendBarrage")
                .build()
                .execute(callback);
    }
    /**
    * @dw 获取其他用户信息
    * @param uid 其他用户id
    * @param ucuid 当前用户自己的id
    * */
    public static void getUserInfo(String uid,String ucuid,String liveuid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Live.getPop")
                .addParams("uid",uid)
                .addParams("touid",ucuid)
                .addParams("liveuid",liveuid)
                .tag("getUserInfo")
                .build()
                .execute(callback);
    }
    /**
    * @dw 判断是否关注
    * @param touid 当前主播id\
    * @param uid 当前用户uid
    * */
    public static void getIsFollow(String uid, String touid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service", "User.isAttent")
                .addParams("uid",uid)
                .addParams("touid",touid)
                .tag("getIsFollow")
                .build()
                .execute(callback);

    }
    /**
     * @dw 关注
     * @param uid 当前用户id
     * @param touid 关注用户id
     */
    public static void showFollow(String uid, String touid,String token,StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service", "User.setAttent")
                .addParams("uid",uid)
                .addParams("touid",touid)
                .addParams("token",token)
                .tag("showFollow")
                .build()
                .execute(callback);
    }
    /**
    * @dw 获取homepage中的用户信息
    * @param uid 查询用户id
    * */
    public static void getHomePageUInfo(String uid,String touid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service", "User.getUserHome")
                .addParams("uid",uid)
                .addParams("touid",touid)
                .tag("getHomePageUInfo")
                .build()
                .execute(callback);
    }

    /**
     * @dw 获取homepage用户的fans
     * @param ucid 自己的id
     * @param uid 查询用户id  */
    public static void getFansList(String uid, String touid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service", "User.getFansList")
                .addParams("uid",uid)
                .addParams("touid",touid)
                .tag("getFansList")
                .build()
                .execute(callback);
    }

    /**
     * @dw 获取homepage用户的关注用户列表get
     * @param ucid 自己的id
     * @param uid 查询用户id
     * */
    public static void getAttentionList(String uid, String ucid,int pager,StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service", "User.getFollowsList")
                .addParams("uid",uid)
                .addParams("touid",ucid)
                .addParams("p", String.valueOf(pager))
                .tag("getAttentionList")
                .build()
                .execute(callback);
    }

    /**
     * @dw 获取魅力值排行
     * @param uid 查询用户id
     * */
    public static void getYpOrder(String uid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","User.getContributeList")
                .addParams("touid",uid)
                .tag("getYpOrder")
                .build()
                .execute(callback);

    }

    /**
     * @dw 获取收益信息
     * @param uid 查询用户id
     * @param token token
     * */
    public static void getWithdraw(String uid, String token, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service", "User.getProfit")
                .addParams("uid",uid)
                .addParams("token",token)
                .tag("getWithdraw")
                .build()
                .execute(callback);

    }
    /**
     * @dw 获取最新
     * */
    public static void getNewestUserList(int pager,StringCallback callback) {

        String url = AppConfig.MAIN_URL + "/index.php?g=Mobile&m=Page&a=requestGetNews";
        OkHttpUtils.get()
                .url(url)
                .addParams("p", String.valueOf(pager))
                .tag("getNewestUserList")
                .build()
                .execute(callback);

    }
    /**
    * @dw 更新头像
    * @param uid 用户id
    * @param token
    * @param protraitFile 图片文件
    *
    * */
    public static void updatePortrait(String uid,String token, File protraitFile, StringCallback callback) {
        OkHttpUtils.post()
                .url(AppConfig.MAIN_URL_API+"appapi/")
                .addParams("service","User.updateAvatar")
                .addFile("file", "wp.png", protraitFile)
                .addParams("uid",uid)
                .addParams("token", token)
                //.url(AppConfig.MAIN_URL_API + "appapi/?service=User.upload")
                .tag("phonelive")
                .build()
                .execute(callback);

    }

    /**
    * @dw 提现
    * @param uid 用户id
    * @param token
    * */
    public static void requestCash(String uid, String token,String money, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","User.setCash")
                .addParams("uid",String.valueOf(uid))
                .addParams("token", token)
                .addParams("money", money)
                .tag("requestCash")
                .build()
                .execute(callback);
    }
    /**
     * @dw 直播记录
     * @param uid 用户id
     * */
    public static void getLiveRecord(int page,String uid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","User.getLiverecord")
                .addParams("uid",uid)
                .addParams("touid",uid)
                .addParams("p", String.valueOf(page))
                .tag("getLiveRecord")
                .build()
                .execute(callback);
    }
    /**
    * @dw 支付宝下订单
    * @param uid 用户id
    * */
    public static void getAliPayOrderNum(String uid,String changeid, String num,String money,StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Charge.getAliOrder")
                .addParams("uid",uid)
                .addParams("money",money)
                .addParams("changeid",changeid)
                .addParams("coin",num)
                .tag("getAliPayOrderNum")
                .build()
                .execute(callback);
    }
    //定位
    public static void getAddress(StringCallback callback) {
        OkHttpUtils.get()
                .url("http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json")
                .tag("phonelive")
                .build()
                .execute(callback);
    }

    /**
    *@dw 搜索
    *@param screenKey 搜索关键词
    *@param uid 用户id
    * */
    public static void search(String screenKey, StringCallback callback,String uid) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Home.search")
                .addParams("key",screenKey)
                .addParams("uid",uid)
                .tag("search")
                .build()
                .execute(callback);
    }
    /**
    * @dw 获取地区列表
    *
    * */
    public static void getAreaList(StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","User.getArea")
                .tag("getAreaList")
                .build()
                .execute(callback);
    }
    /**
    * @dw 地区检索
    * @param sex 性别
    * @param area 地区
    * */

    public static void selectTermsScreen(int sex, String area, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","User.searchArea")
                .addParams("sex",String.valueOf(sex))
                .addParams("key",area)
                .tag("selectTermsScreen")
                .build()
                .execute(callback);
    }
    /**
    * @dw 批量获取用户信息
    * @param uidList 用户id字符串 以逗号分割
    *
    * */
    public static void getMultiBaseInfo(int action,String uid,String uidList, StringCallback callback) {

            OkHttpUtils.get()
                    .url(AppConfig.MAIN_URL_API)
                    .addParams("service","User.getMultiInfo")
                    .addParams("uids",uidList)
                    .addParams("type","2")
                    .addParams("uid",String.valueOf(uid))
                    .tag("getMultiBaseInfo")
                    .build()
                    .execute(callback);

    }

    /**
    * @dw 获取已关注正在直播的用户
    * @param uid 用户id
    * */
    public static void getAttentionLive(int pager,String uid, StringCallback callback) {

        String url = AppConfig.MAIN_URL + "/index.php?g=Mobile&m=Page&a=requestGetFollowLive";
        OkHttpUtils.get()
                .url(url)
                .addParams("uid",uid)
                .addParams("p", String.valueOf(pager))
                .tag("requestGetFollowLive")
                .build()
                .execute(callback);
    }
    /**
    * @dw 获取用户信息私聊专用
    * @param uid  当前用户id
    * @param ucuid  to uid
    * */

    public static void getPmUserInfo(String uid, String ucuid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","User.getPmUserInfo")
                .addParams("uid",uid)
                .addParams("touid",ucuid)
                .tag("getPmUserInfo")
                .build()
                .execute(callback);

    }

    /**
     * @dw 微信支付
     * @param uid 用户id
     * @param price 价格
     * */
    public static void wxPay(String uid,String changeid, String price,String num ,StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Charge.getWxOrder")
                .addParams("uid",uid)
                .addParams("changeid",changeid)
                .addParams("coin",num)
                .addParams("money",price)
                .build()
                .execute(callback);

    }
    /**
    * @dw 第三方登录
    * @param platDB  用户信息
    * @param type 平台
    * */
    public static void otherLogin(String type,PlatformDb platDB, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Login.userLoginByThird")
                .addParams("openid",platDB.getUserId())
                .addParams("nicename",platDB.getUserName())
                .addParams("type",type)
                .addParams("avatar",platDB.getUserIcon())
                .tag("otherLogin")
                .build()
                .execute(callback);
    }
    /**
    * @dw 设为管理员
    * @param roomnum 房间号码
    * @param touid 操作id
    * @param token 用户登录token
    * */
    public static void setManage(String roomnum, String touid,String token,String uid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Live.setAdmin")
                .addParams("liveuid",roomnum)
                .addParams("touid",touid)
                .addParams("uid",uid)
                .addParams("token",token)
                .tag("setManage")
                .build()
                .execute(callback);

    }
    /**
    * @dw 判断是否为管理员
    * @param uid 用户id
    * @param showid 房间号码
    * */

    public static void isManage(String showid, String uid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","User.getIsAdmin")
                .addParams("liveuid",showid)
                .addParams("uid",uid)
                .tag("isManage")
                .build()
                .execute(callback);

    }
    /**
    * @dw 禁言
    * @param showid 房间id
    * @param touid 被禁言用户id
    * @param token 用户登录token
    * */
    public static void setShutUp(String showid, String touid, String uid,String token, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Live.setShutUp")
                .addParams("liveuid",showid)
                .addParams("touid",touid)
                .addParams("uid",uid)
                .addParams("token",token)
                .tag("setShutUp")
                .build()
                .execute(callback);
    }
    //是否禁言解除
    public static void isShutUp(int uid, int showid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","User.isShutUp")
                .addParams("liveuid",String.valueOf(showid))
                .addParams("uid",String.valueOf(uid))
                .tag("isShutUp")
                .build()
                .execute(callback);
    }
    //token是否过期
    public static void tokenIsOutTime(String uid, String token, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","User.iftoken")
                .addParams("uid",uid)
                .addParams("token",token)
                .tag("tokenIsOutTime")
                .build()
                .execute(callback);
    }
    /**
    * @dw 拉黑
    * */
    public static void pullTheBlack(String uid, String touid, String token,StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","User.setBlack")
                .addParams("uid",uid)
                .addParams("touid", touid)
                .addParams("token",token)
                .tag("pullTheBlack")
                .build()
                .execute(callback);

    }
    /**
    * @dw 黑名单列表
    * */
    public static void getBlackList(String uid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","User.getBlackList")
                .addParams("uid", uid)
                .addParams("touid", uid)
                .tag("getBlackList")
                .build()
                .execute(callback);
    }

    public static void getMessageCode(String phoneNum,String methodName,StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service",methodName)
                .addParams("mobile", phoneNum)
                .tag("getMessageCode")
                .build()
                .execute(callback);
    }
    /**
    * @dw 获取用户余额
    * @param uid 用户id
    * */
    public static void getUserDiamondsNum(String uid,String token, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","User.getUserPrivateInfo")
                .addParams("uid",uid)
                .addParams("token",token)
                .tag("getUserDiamondsNum")
                .build()
                .execute(callback);
    }
    /**
    * @dw 点亮
    * @param uid 用户id
    * @param token 用户登录token
    * @param showid 房间号
    * */
    public static void showLit(String uid,String token,String showid) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","User.setLight")
                .addParams("uid",uid)
                .addParams("token",token)
                .addParams("showid",showid)
                .tag("showLit")
                .build()
                .execute(null);
    }
    /**
    * @dw 百度接口搜索音乐
    * @param keyword 歌曲关键词
    * */
    public static void searchMusic(String keyword, StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","live.searchMusic")
                .addParams("key",keyword)
                .tag("searchMusic")
                .build()
                .execute(callback);

    }
    /**
    * @dw 获取music信息
    * @param songid 歌曲id
    * */
    public static void getMusicFileUrl(String songid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Live.getDownurl")
                .addParams("audio_id",songid)
                .tag("getMusicFileUrl")
                .build()
                .execute(callback);
    }

    /**
     * @param musicUrl 下载地址
     * @dw 下载音乐文件
     */
    public static void downloadMusic(String musicUrl, FileCallBack fileCallBack) {
        OkHttpUtils.get()
                .url(musicUrl)
                .tag("downloadMusic")
                .build()
                .execute(fileCallBack);
    }
    /**
     * @dw 开播等级限制
     * */
    public static void getLevelLimit(String uid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","User.getLevelLimit")
                .addParams("uid",String.valueOf(uid))
                .tag("phonelive")
                .build()
                .execute(callback);
    }
    /**
     * @dw 检查新版本
     * */
    public static void checkUpdate(StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","User.getVersion")
                .tag("checkUpdate")
                .build()
                .execute(callback);
    }

    public static void downloadLrc(String musicLrc, FileCallBack fileCallBack) {
        OkHttpUtils.get()
                .url(musicLrc)
                .tag("downloadLrc")
                .build()
                .execute(fileCallBack);
    }
    /**
    * @dw 下载最新apk
    * @param apkUrl  下载地址
    * */
    public static void getNewVersionApk(String apkUrl,FileCallBack fileCallBack) {
        OkHttpUtils.get()
                .url(apkUrl)
                .tag("getNewVersionApk")
                .build()
                .execute(fileCallBack);
    }
    /**
    * @dw 获取管理员列表
    * @param uid  用户id
    * */
    public static void getManageList(String uid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Live.getAdminList")
                .addParams("liveuid",String.valueOf(uid))
                .tag("getManageList")
                .build()
                .execute(callback);
    }

    //举报
    public static void report(String uid, String token,String touid) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Live.setReport")
                .addParams("uid",uid)
                .addParams("touid",touid)
                .addParams("token",token)
                .addParams("content","涉嫌违规")
                .tag("report")
                .build()
                .execute(null);

    }


    //获取直播记录
    public static void getLiveRecordById(String showid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","User.getAliCdnRecord")
                .addParams("id",showid)
                .tag("getLiveRecordById")
                .build()
                .execute(callback);
    }


    /**
     * @dw 判断是否购买vip
     * @param uid 用户id
     * @param viplevel vip登记
     *
     * */
    public static void isBuyVip(int uid, String viplevel, StringCallback isBuyVipCallback) {
        OkHttpUtils.post()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","User.isBuyVip")
                .addParams("uid",String.valueOf(uid))
                .addParams("viplevel",viplevel)
                .build()
                .execute(isBuyVipCallback);
    }

    public static void getConfig(StringCallback buyVipCallback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Home.getConfig")
                .tag("getConfig")
                .build()
                .execute(buyVipCallback);
    }

    public static void getChangePass(String uid,String token,String oldpass,String pass1st,String pass2nd, StringCallback getChangePassCallback) {
        try {
            OkHttpUtils.get()
                    .url(AppConfig.MAIN_URL_API)
                    .addParams("service","User.updatePass")
                    .addParams("uid",uid)
                    .addParams("token",token)
                    .addParams("oldpass",URLEncoder.encode(oldpass,"UTF-8"))
                    .addParams("pass",URLEncoder.encode(pass1st,"UTF-8"))
                    .addParams("pass2",URLEncoder.encode(pass2nd,"UTF-8"))
                    .tag("getChangePass")
                    .build()
                    .execute(getChangePassCallback);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void getPubMsg(StringCallback getPubMsgCallback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","User.pub_msg")
                .tag("getPubMsg")
                .build()
                .execute(getPubMsgCallback);
    }

    //修改直播状态
    public static void changeLiveState(String uid,String token,String stream,String status,StringCallback callback){
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Live.changeLive")
                .addParams("uid",uid)
                .addParams("token",token)
                .addParams("stream",stream)
                .addParams("status",status)
                .build()
                .execute(callback);
    }

    //检查房间状态
    public static void checkoutRoom(String uid, String token, String stream, String liveuid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Live.checkLive")
                .addParams("uid",uid)
                .addParams("token",token)
                .addParams("stream",stream)
                .addParams("liveuid",liveuid)
                .build()
                .execute(callback);
    }

    //热门数据
    public static void requestHotData(int pager,StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Home.getHot")
                .addParams("p", String.valueOf(pager))
                .tag("requestHotData")
                .build()
                .execute(callback);
    }

    //我的钻石
    public static void requestBalance(String userID, String userToken, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","User.getBalance")
                .addParams("uid",userID)
                .addParams("token",userToken)
                .tag("requestBalance")
                .build()
                .execute(callback);
    }

    //三方登录开启状态
    public static void requestOtherLoginStatus(StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Home.getLogin")
                .tag("requestOtherLoginStatus")
                .build()
                .execute(callback);
    }

    //踢人
    public static void setKick(String showid, String touid, String uid,String token, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Live.kicking")
                .addParams("liveuid",showid)
                .addParams("touid",touid)
                .addParams("uid",uid)
                .addParams("token",token)
                .tag("setKick")
                .build()
                .execute(callback);
    }

    //超管关闭直播
    public static void setCloseLive(String id, String token, String liveuid, String type, StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Live.superStopRoom")
                .addParams("liveuid",liveuid)
                .addParams("token",token)
                .addParams("uid",id)
                .addParams("type",type)
                .tag("setCloseLive")
                .build()
                .execute(callback);
    }

    //房间扣费
    public static void requestCharging(String uid, String token,String liveuid,String stream, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Live.roomCharge")
                .addParams("liveuid",liveuid)
                .addParams("token",token)
                .addParams("uid",uid)
                .addParams("stream",stream)
                .tag("requestCharging")
                .build()
                .execute(callback);
    }

    public static void getLiveEndInfo(String stream, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Live.stopInfo")
                .addParams("stream",stream)
                .tag("getLiveEndInfo")
                .build()
                .execute(callback);
    }

    //请求首游戏主播
    public static void requestGetGameLive(StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Home.getGame")
                .tag("requestGetGameLive")
                .build()
                .execute(callback);
    }

    /**
    * @dw 请求开始游戏
    * @param liveuid 主播ID
    * @param stream 流地址
    * @param token 主播token
    * */
    public static void requestStartGame(String liveuid, String stream, String token, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Game.fruitsGameStart")
                .addParams("liveuid",liveuid)
                .addParams("stream",stream)
                .addParams("token",token)
                .tag("requestStartGame")
                .build()
                .execute(callback);
    }


    /**
    * @dw 下注
    * @param uid 用户id
    * @param coin 下注金额
    * @param token
    * @param gameId 游戏id
    * @param grade 下注的哪一个
    * */
    public static void requestStakeGame(String uid, String token, String coin,String gameId,String grade, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Game.fruitsBet")
                .addParams("uid",uid)
                .addParams("coin",coin)
                .addParams("token",token)
                .addParams("gameid",gameId)
                .addParams("grade",grade)
                .tag("requestStakeGame")
                .build()
                .execute(callback);
    }

    /**
    * @dw 获取用户余额
    * @param uid 用户id
    * @param token 用户token
    * */
    public static void requestGetUserCoin(String uid,String token,StringCallback callback){
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","User.getUserCoin")
                .addParams("uid",uid)
                .addParams("token",token)
                .tag("requestGetUserCoin")
                .build()
                .execute(callback);
    }

    /**
    * @dw 修改房间类型
    * @param uid
    * @param token 用户token
    * @param stream 流明
    * @param coin 扣费金额
    * */
    public static void requestSetRoomType(String uid, String token, String stream,String coin, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Live.openChargingRoom")
                .addParams("liveuid",uid)
                .addParams("token",token)
                .addParams("stream",stream)
                .addParams("coin",coin)
                .tag("requestSetRoomType")
                .build()
                .execute(callback);
    }


    public static void requestJinhuaBet(String gameId, int bettingCoin, int grade, String uid, String token, StringCallback callback){

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service", "Game.JinhuaBet")
                .addParams("gameid", gameId)
                .addParams("coin", String.valueOf(bettingCoin))
                .addParams("grade", String.valueOf(grade))
                .addParams("uid", uid)
                .addParams("token", token)
                .tag("requestBetting")
                .build()
                .execute(callback);
    }
    //炸金花发牌接口
    public static void requestJinhuaStartGame(String liveuid, String stream, String token, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service", "Game.Jinhua")
                .addParams("liveuid", liveuid)
                .addParams("stream", stream)
                .addParams("token", token)
                .tag("requestStartGame")
                .build()
                .execute(callback);
    }

    //获取中奖结果
    public static void requestJinhuaBettingResult(String uid, String gameId, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service", "Game.settleGame")
                .addParams("gameid", gameId)
                .addParams("uid", uid)
                .build()
                .execute(callback);
    }

    public static void closeGameJinhua(String stream, String liveuid, String token, String gameid, String type, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service", "Game.jinhuaEndGame")
                .addParams("stream", stream)
                .addParams("liveuid", liveuid)
                .addParams("token", token)
                .addParams("gameid", gameid)
                .addParams("type", type)
                .tag("closeGameJinhua")
                .build()
                .execute(callback);
    }


    public static void createFamily(String uid,String token,String f_name, String f_notice, File protraitFile, StringCallback callback) {

        OkHttpUtils.post()
                .url(AppConfig.MAIN_URL_API+"appapi/")
                .addParams("service","Family.create_family")
                .addFile("file",protraitFile.getName(), protraitFile)
                .addParams("uid",uid)
                .addParams("token", token)
                .addParams("family_name",f_name)
                .addParams("notice",f_notice)
                .tag("createFamily")
                .build()
                .execute(callback);
    }

    public static void checkIsFamily(String uid, StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service", "Family.check_is_family")
                .addParams("uid",uid)
                .build()
                .execute(callback);
    }

    public static void requestGetFamilyList(String uid, StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service", "Family.list_family")
                .addParams("uid",uid)
                .build()
                .execute(callback);
    }

    public static void requestJoinFamily(String uid, String token,String fid, StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service", "Family.join_family")
                .addParams("uid",uid)
                .addParams("token",token)
                .addParams("fid",fid)
                .build()
                .execute(callback);
    }

    public static void requestGetFamilyDetail(String uid, String fid, StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service", "Family.detail_family")
                .addParams("uid",uid)
                .addParams("fid",fid)
                .addParams("fid",fid)
                .tag("requestGetFamilyDetail")
                .build()
                .execute(callback);
    }

    public static void requestGetFamilyUser(String uid, String type,String fid, StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service", "Family.users_family")
                .addParams("uid",uid)
                .addParams("type",type)
                .addParams("fid",fid)
                .build()
                .execute(callback);
    }

    public static void requestAuditingFamily(String uid, String token, String touid, int a, StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service", "Family.auditing_family")
                .addParams("uid",uid)
                .addParams("token",token)
                .addParams("touid",touid)
                .addParams("action", String.valueOf(a))
                .tag("requestAuditingFamily")
                .build()
                .execute(callback);
    }

    public static void requestSigOutFamily(String uid, String token, String fid, StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service", "Family.sig_out_family")
                .addParams("uid",uid)
                .addParams("token",token)
                .addParams("fid",fid)
                .tag("requestSigOutFamily")
                .build()
                .execute(callback);
    }

    public static void requestKickUser(String uid, String token, String touid, StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service", "Family.kick_family")
                .addParams("uid",uid)
                .addParams("token",token)
                .addParams("touid",touid)
                .tag("requestKickUser")
                .build()
                .execute(callback);
    }

    public static void requestGetFamilyInfo(String uid, String token, StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service", "Family.get_info_family")
                .addParams("uid",uid)
                .addParams("token",token)
                .tag("requestGetFamilyInfo")
                .build()
                .execute(callback);
    }

    public static void requestEditRatioFamilyInfo(String uid, String token, String radio, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service", "Family.edit_ratio_family")
                .addParams("uid",uid)
                .addParams("token",token)
                .addParams("ratio",radio)
                .tag("requestEditRadioFamilyInfo")
                .build()
                .execute(callback);
    }

    public static void requestGetIsAgent(String uid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service", "User.is_agent")
                .addParams("uid",uid)
                .tag("requestGetIsAgent")
                .build()
                .execute(callback);
    }

    public static void getInitInfo(StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL + "/index.php?g=mobile")
                .addParams("m","Page")
                .addParams("a","app_init")
                .tag("getInitInfo")
                .build()
                .execute(callback);
    }

    public static void requestGetGoodsList(String uid, String token, StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL + "/index.php?g=mobile")
                .addParams("m","Page")
                .addParams("a","requestGetGoodsList")
                .addParams("uid",uid)
                .addParams("token",token)
                .tag("requestGetMyGoodsList")
                .build()
                .execute(callback);
    }

    public static void requestBuyVip(String uid, String token, String id, StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL + "/index.php?g=mobile")
                .addParams("m","Vip")
                .addParams("a","requestBuyVip")
                .addParams("uid",uid)
                .addParams("token",token)
                .addParams("vip_id",id)
                .tag("requestBuyVip")
                .build()
                .execute(callback);
    }

    public static void requestGetAgentUserList(String uid, String token, int page,StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL + "/index.php?g=mobile")
                .addParams("m","Page")
                .addParams("a","requestGetAgentUserList")
                .addParams("uid",uid)
                .addParams("token",token)
                .addParams("p", String.valueOf(page))
                .tag("requestGetAgentUserList")
                .build()
                .execute(callback);
    }

    public static void requestGetAgentProfitRecord(String uid, String token, int page,StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL + "/index.php?g=mobile")
                .addParams("m","Page")
                .addParams("a","requestGetAgentProfitRecord")
                .addParams("uid",uid)
                .addParams("token",token)
                .addParams("p", String.valueOf(page))
                .tag("requestGetAgentProfitRecord")
                .build()
                .execute(callback);
    }

    public static void requestCheckIsAgent(String uid, String token, StringCallback callback) {


        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL + "/index.php?g=mobile")
                .addParams("m","User")
                .addParams("a","requestCheckIsAgent")
                .addParams("uid",uid)
                .addParams("token",token)
                .tag("requestCheckIsAgent")
                .build()
                .execute(callback);
    }

    public static void requestApplyAgent(String uid, String token, StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL + "/index.php?g=mobile")
                .addParams("m","User")
                .addParams("a","requestApplyAgent")
                .addParams("uid",uid)
                .addParams("token",token)
                .tag("requestApplyAgent")
                .build()
                .execute(callback);
    }

    public static void requestGetApiSing(String uid, String token, StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL + "/index.php?g=mobile")
                .addParams("m","ShortVideo")
                .addParams("a","requestGetApiSing")
                .addParams("uid",uid)
                .addParams("token",token)
                .tag("requestGetApiSing")
                .build()
                .execute(callback);
    }

    public static void requestAddShortVideo(String city,String lat,String lng,String title, String videoURL, String coverURL, String videoId, String uid, String token, StringCallback callback) {

        OkHttpUtils.post()
                .url(AppConfig.MAIN_URL + "/index.php?g=mobile&m=ShortVideo&a=requestAddShortVideo")
                .addParams("uid",uid)
                .addParams("token",token)
                .addParams("title",title)
                .addParams("videoid",videoId)
                .addParams("video_url",videoURL)
                .addParams("cover_url",coverURL)
                .addParams("lat",lat)
                .addParams("lng",lng)
                .addParams("city",city)
                .tag("requestAddShortVideo")
                .build()
                .execute(callback);
    }

    public static void requestGetShortVideoList(String uid, String token, String touid,int p, StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL + "/index.php?g=mobile")
                .addParams("m","ShortVideo")
                .addParams("a","requestGetShortVideoList")
                .addParams("uid",uid)
                .addParams("token",token)
                .addParams("p", String.valueOf(p))
                .addParams("touid",touid)
                .tag("requestGetShortVideoList")
                .build()
                .execute(callback);
    }

    public static void requestReplyVideo(String uid, String token, String vid, String body, StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL + "/index.php?g=mobile")
                .addParams("m","ShortVideo")
                .addParams("a","requestReplyVideo")
                .addParams("uid",uid)
                .addParams("token",token)
                .addParams("vid",vid)
                .addParams("body",body)
                .tag("requestReplyVideo")
                .build()
                .execute(callback);
    }

    public static void requestGetVideoReplyList(String uid, String vid, int page, StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL + "/index.php?g=mobile")
                .addParams("m","ShortVideo")
                .addParams("a","requestGetVideoReplyList")
                .addParams("uid",uid)
                .addParams("vid",vid)
                .addParams("p", String.valueOf(page))
                .tag("requestGetVideoReplyList")
                .build()
                .execute(callback);
    }

    public static void requestGetShortVideoDetailInfo(String uid, String token, String vid, StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL + "/index.php?g=mobile")
                .addParams("m","ShortVideo")
                .addParams("a","requestGetShortVideoDetailInfo")
                .addParams("uid",uid)
                .addParams("token",token)
                .addParams("vid",vid)
                .tag("requestGetShortVideoDetailInfo")
                .build()
                .execute(callback);
    }

    //热门数据
    public static void requestHotDataNew(int pager,StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL + "/index.php?g=mobile")
                .addParams("m","Page")
                .addParams("a","requestGetHotUser")
                .addParams("p", String.valueOf(pager))
                .tag("requestGetRedUser")
                .build()
                .execute(callback);
    }

    public static void requestShortVideoFollow(String uid, String token, String vid, StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL + "/index.php?g=mobile")
                .addParams("m","ShortVideo")
                .addParams("a","requestShortVideoFollow")
                .addParams("uid",uid)
                .addParams("token",token)
                .addParams("vid",vid)
                .tag("requestShortVideoFollow")
                .build()
                .execute(callback);
    }

    public static void requestGetHotShortVideoList(String uid, String token,int p, StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL + "/index.php?g=mobile")
                .addParams("m","ShortVideo")
                .addParams("a","requestGetHotShortVideoList")
                .addParams("uid",uid)
                .addParams("token",token)
                .addParams("p", String.valueOf(p))
                .tag("requestGetHotShortVideoList")
                .build()
                .execute(callback);
    }

    public static void requestGetShortVideoReportList(StringCallback callback) {


        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL + "/index.php?g=mobile")
                .addParams("m","ShortVideo")
                .addParams("a","requestGetShortVideoReportList")
                .tag("requestGetShortVideoReportList")
                .build()
                .execute(callback);
    }

    public static void requestShortVideoReport(String uid, String token, String vid,String rid ,StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL + "/index.php?g=mobile")
                .addParams("m","ShortVideo")
                .addParams("a","requestShortVideoReport")
                .addParams("uid",uid)
                .addParams("token",token)
                .addParams("vid",vid)
                .addParams("rid",rid)
                .tag("requestShortVideoReport")
                .build()
                .execute(callback);
    }

    public static void requestShortVideoDel(String uid, String token, String vid, StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL + "/index.php?g=mobile")
                .addParams("m","ShortVideo")
                .addParams("a","requestShortVideoDel")
                .addParams("uid",uid)
                .addParams("token",token)
                .addParams("vid",vid)
                .tag("requestShortVideoDel")
                .build()
                .execute(callback);
    }

    public static void requestSearchUser(String key, StringCallback callback, String uid) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL + "/index.php?g=mobile")
                .addParams("m","Page")
                .addParams("a","requestSearchUser")
                .addParams("uid",uid)
                .addParams("key",key)
                .tag("requestSearchUser")
                .build()
                .execute(callback);
    }

    public static void requestGetNearLiveListData(String address, String lat, String lng, String uid,int p, StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL + "/index.php?g=mobile")
                .addParams("m","Page")
                .addParams("a","requestGetNearLiveListData")
                .addParams("city",address)
                .addParams("lat",lat)
                .addParams("lng",lng)
                .addParams("uid",uid)
                .addParams("p", String.valueOf(p))
                .tag("requestGetNearListData")
                .build()
                .execute(callback);
    }

    public static void requestGetNearListData(String address, String lat, String lng, String uid,int p, StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL + "/index.php?g=mobile")
                .addParams("m","Page")
                .addParams("a","requestGetNearListData")
                .addParams("city",address)
                .addParams("lat",lat)
                .addParams("lng",lng)
                .addParams("uid",uid)
                .addParams("p", String.valueOf(p))
                .tag("requestGetNearListData")
                .build()
                .execute(callback);
    }

    public static void requestGetIndexVideoListData( String uid,int p, StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL + "/index.php?g=mobile")
                .addParams("m","Page")
                .addParams("a","requestGetIndexVideoListData")
                .addParams("uid",uid)
                .addParams("p", String.valueOf(p))
                .tag("requestGetIndexVideoListData")
                .build()
                .execute(callback);
    }

    public static void requestCheckLive(String uid, String token, String liveuid, String stream, StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL + "/index.php?g=mobile")
                .addParams("m","Live")
                .addParams("a","requestCheckLive")
                .addParams("uid",uid)
                .addParams("token",token)
                .addParams("liveuid",liveuid)
                .addParams("stream",stream)
                .tag("requestSearchUser")
                .build()
                .execute(callback);
    }

    public static void requestGetVideoInfo(String uid, String vid, StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL + "/index.php?g=mobile")
                .addParams("m","ShortVideo")
                .addParams("a","requestGetVideoInfo")
                .addParams("uid",uid)
                .addParams("vid",vid)
                .tag("requestGetVideoInfo")
                .build()
                .execute(callback);
    }

    public static void requestGetFollowListData(String uid, int page, StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL + "/index.php?g=mobile")
                .addParams("m","Page")
                .addParams("a","requestGetFollowListData")
                .addParams("uid",uid)
                .addParams("p", String.valueOf(page))
                .tag("requestGetFollowListData")
                .build()
                .execute(callback);
    }

    public static void requestEnterRoom(String uid, String showId, String token, String address, String stream, StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL + "/index.php?g=mobile")
                .addParams("m","Live")
                .addParams("a","requestEnterRoom")
                .addParams("uid", uid)
                .addParams("liveuid",showId)
                .addParams("token", token)
                .addParams("city", address)
                .addParams("stream",stream)
                .tag("initRoomInfo")
                .build()
                .execute(callback);
    }

    public static void requestPayOrderId(String uid,String token,String money,String num,String chargid,String type,StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL + "/index.php?g=Appapi&m=NewPay&a=submitOrderInfo")
                .addParams("uid",uid)
                .addParams("token",token)
                .addParams("coin",num)
                .addParams("money",money)
                .addParams("chargeid",chargid)
                .addParams("type",type)
                .tag("requestPayOrderId")
                .build()
                .execute(callback);

    }

    public static void requestReplyComment(String uid, String token, String vid, String body, String rid, StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL + "/index.php?g=mobile")
                .addParams("m","ShortVideo")
                .addParams("a","requestReplyComment")
                .addParams("uid",uid)
                .addParams("token",token)
                .addParams("vid",vid)
                .addParams("body",body)
                .addParams("rid",rid)
                .tag("requestReplyComment")
                .build()
                .execute(callback);
    }

    public static void requestGetReplyCommentList(String uid, String rid, int page, StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL + "/index.php?g=mobile")
                .addParams("m","ShortVideo")
                .addParams("a","requestGetReplyCommentList")
                .addParams("uid",uid)
                .addParams("rid",rid)
                .addParams("p", String.valueOf(page))
                .tag("requestGetReplyCommentList")
                .build()
                .execute(callback);
    }

    public static void requestShareNumAdd(String uid, String token, String vid, StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL + "/index.php?g=mobile")
                .addParams("m","ShortVideo")
                .addParams("a","requestShareNumAdd")
                .addParams("uid",uid)
                .addParams("token",token)
                .addParams("vid", vid)
                .tag("requestShareNumAdd")
                .build()
                .execute(callback);
    }

    public static void requestGetNewsMessage(String uid, String token,int page, StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL + "/index.php?g=mobile")
                .addParams("m","ShortVideo")
                .addParams("a","requestGetNewsMessage")
                .addParams("uid",uid)
                .addParams("token",token)
                .addParams("p", String.valueOf(page))
                .tag("requestGetNewsMessage")
                .build()
                .execute(callback);
    }

    public static void requestGetConsumpationOrder(String uid, String token,String action, StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL + "/index.php?g=mobile")
                .addParams("m","Rank")
                .addParams("a","consumption_rank")
                .addParams("uid",uid)
                .addParams("token",token)
                .addParams("action",action)
                .tag("consumption_rank")
                .build()
                .execute(callback);
    }

    public static void requestGetIncomeOrder(String uid, String token,String action, StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL + "/index.php?g=mobile")
                .addParams("m","Rank")
                .addParams("a","income_rank")
                .addParams("uid",uid)
                .addParams("token",token)
                .addParams("action",action)
                .tag("income_rank")
                .build()
                .execute(callback);
    }

    public static void requestGetCloudVideoList(int page, StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL + "/index.php?g=mobile")
                .addParams("m","Page")
                .addParams("a","request_get_cloud_video_list")
                .addParams("page", String.valueOf(page))
                .tag("request_get_cloud_video_list")
                .build()
                .execute(callback);
    }

    public static void requestGetCloudVideoInfo(String uid, String token, String id, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL + "/index.php?g=mobile")
                .addParams("m","Page")
                .addParams("a","request_get_cloud_video_info")
                .addParams("uid",uid)
                .addParams("token",token)
                .addParams("v_id", id)
                .tag("request_get_cloud_video_info")
                .build()
                .execute(callback);
    }

    public static void requestBuyCloudVideo(String id, String uid, String token, StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL + "/index.php?g=mobile")
                .addParams("m","Page")
                .addParams("a","request_buy_cloud_video")
                .addParams("uid",uid)
                .addParams("token",token)
                .addParams("v_id", id)
                .tag("request_buy_cloud_video")
                .build()
                .execute(callback);
    }

    public static void requestGetAdvertList(StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL + "/index.php?g=mobile")
                .addParams("m","Page")
                .addParams("a","request_get_advert_list")
                .tag("request_get_advert_list")
                .build()
                .execute(callback);
    }

    public static void requestCloudVideoChargePay(String id, String uid, String token, StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL + "/index.php?g=mobile")
                .addParams("m","Page")
                .addParams("a","request_cloud_video_charge")
                .addParams("uid",uid)
                .addParams("token",token)
                .addParams("v_id", id)
                .tag("request_cloud_video_charge")
                .build()
                .execute(callback);
    }

    public static void requestLiveRtc(String uid, String token, String liveUid, String stream, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL + "/index.php?g=mobile")
                .addParams("m","Live")
                .addParams("a","request_rtc_live")
                .addParams("uid",uid)
                .addParams("token",token)
                .addParams("stream_id", stream)
                .addParams("live_uid", liveUid)
                .tag("requestLiveRtc")
                .build()
                .execute(callback);
    }

    public static void requestReplyRtc(String uid, String token, String toUserId,int action, StringCallback callback) {
        OkHttpUtils.get()
            .url(AppConfig.MAIN_URL + "/index.php?g=mobile")
            .addParams("m","Live")
            .addParams("a","request_reply_rtc")
            .addParams("uid",uid)
            .addParams("token",token)
            .addParams("action", String.valueOf(action))
            .addParams("request_uid", toUserId)
            .tag("requestReplyRtc")
            .build()
            .execute(callback);
    }

    public static void requestEndRtc(String uid, String token, String liveUid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL + "/index.php?g=mobile")
                .addParams("m","Live")
                .addParams("a","request_rtc_end")
                .addParams("uid",uid)
                .addParams("token",token)
                .addParams("live_uid", liveUid)
                .tag("requestEndRtc")
                .build()
                .execute(callback);
    }

    public static void requestEmceeEndRtc(String uid, String token, String requestUid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL + "/index.php?g=mobile")
                .addParams("m","Live")
                .addParams("a","request_emcee_rtc_end")
                .addParams("uid",uid)
                .addParams("token",token)
                .addParams("request_uid", requestUid)
                .tag("requestEndRtc")
                .build()
                .execute(callback);
    }

    public static void requestGetLiveRtcList(String uid, String token, String streamId, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL + "/index.php?g=mobile")
                .addParams("m","Live")
                .addParams("a","request_rtc_user_list")
                .addParams("uid",uid)
                .addParams("token",token)
                .addParams("stream_id", streamId)
                .tag("requestGetLiveRtcList")
                .build()
                .execute(callback);
    }

    public static void requestIsFullAgentInviteCode(String uid, String token, StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL + "/index.php?g=mobile")
                .addParams("m","User")
                .addParams("a","request_is_full_agent_invite_code")
                .addParams("uid",uid)
                .addParams("token",token)
                .tag("requestIsFullAgentInviteCode")
                .build()
                .execute(callback);
    }

    public static void requestFullAgentInviteCode(String uid, String token,String inviteCode, StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL + "/index.php?g=mobile")
                .addParams("m","User")
                .addParams("a","request_full_agent_invite_code")
                .addParams("uid",uid)
                .addParams("token",token)
                .addParams("invite_code",inviteCode)
                .tag("requestFullAgentInviteCode")
                .build()
                .execute(callback);
    }

    public static void requestCancelInviteCode(String uid, String token,String inviteCode, StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL + "/index.php?g=mobile")
                .addParams("m","User")
                .addParams("a","request_cancel_full_invite_code")
                .addParams("uid",uid)
                .addParams("token",token)
                .addParams("invite_code",inviteCode)
                .tag("requestCancelInviteCode")
                .build()
                .execute(callback);
    }

    public static void requestCheckIsAuth(String userID, StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL + "/index.php?g=mobile")
                .addParams("m","User")
                .addParams("a","request_check_is_auth")
                .addParams("uid",userID)
                .tag("requestCheckIsAuth")
                .build()
                .execute(callback);
    }

    public static void requestGetRechargeInfo(StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL + "/index.php?g=mobile")
                .addParams("m","Page")
                .addParams("a","request_get_recharge_info")
                .tag("requestGetRechargeInfo")
                .build()
                .execute(callback);
    }

    public static void requestPay(String userID, String userToken,String payId,String chargeId,StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL + "/index.php?g=mobile")
                .addParams("m","Pay")
                .addParams("a","request_pay")
                .addParams("uid",userID)
                .addParams("token",userToken)
                .addParams("pay_id",payId)
                .addParams("charge_id",chargeId)
                .tag("requestPay")
                .build()
                .execute(callback);
    }

    public static void requestGetLiveRankList(String uid, StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL + "/index.php?g=mobile")
                .addParams("m","Live")
                .addParams("a","request_get_site_coin_order")
                .addParams("live_uid",uid)
                .tag("requestGetLiveRankList")
                .build()
                .execute(callback);
    }

    /**
     * 获取游戏页面地址
     *
     * @param token    appkey
     * @param uid      用户id
     * @param liveuid  主播id
     * @param stream   流名
     * @param callback 回调
     */
    public static void getLiveGameUrls(String uid, String token,String liveuid,String stream,StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Guard.egg")
                .addParams("uid",uid)
                .addParams("token",token)
                .addParams("liveuid",liveuid)
                .addParams("stream",stream)
                .tag("getLiveGameUrls")
                .build()
                .execute(callback);

    }

    public static void getProtecUsertList(String liveuid, StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Live.getGuardLists")
                .addParams("liveuid",liveuid)
                .tag("getProtecUsertList")
                .build()
                .execute(callback);
    }

    public static void getPackGiftList(String uid, String token, StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Guard.knapsack")
                .addParams("uid",uid)
                .addParams("token",token)
                .tag("getPackGiftList")
                .build()
                .execute(callback);
    }

    public static void sendPackGift(UserBean mUser, GiftBean g, String mNowRoomNum, String stream,int count, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Guard.knapsackGift")
                .addParams("token",mUser.token)
                .addParams("uid",String.valueOf(mUser.id))
                .addParams("liveuid", String.valueOf(mNowRoomNum))
                .addParams("giftid", String.valueOf(g.getGiftid()))
                .addParams("giftcount",String.valueOf(count))
                .addParams("stream",stream)
                .tag("sendPackGift")
                .build()
                .execute(callback);
    }
}
