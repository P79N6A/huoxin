package com.daimeng.live.bean;

import java.util.List;

/**
 * Created by weipeng on 2017/10/8.
 */

public class LiveEnterRoomBean {


    /**
     * votestotal : 218681
     * barrage_fee : 2
     * userlist_time : 60
     * chatserver : http://114.215.71.87:19967
     * push_url : rtmp://push.yuezimeishi.com/5showcam/
     * pull_url : ?vhost=push.yuezimeishi.com
     * showvideo : 0
     * showvideo_url : rtmp://push.yuezimeishi.com/5showcam/
     * nums : 0
     * game : []
     * gametime : 0
     * gameid : 0
     * gameaction : 0
     * coin : 51678
     * is_vip : 1
     * live_chat_level : 1
     * isattention : 1
     * userlists : []
     */

    private String votestotal;
    private String barrage_fee;
    private String userlist_time;
    private String chatserver;
    private String push_url;
    private String pull_url;
    private String showvideo;
    private String showvideo_url;
    private int nums;
    private String coin;
    private String is_vip;
    private String live_chat_level;
    private String isattention;
    private List<SimpleUserInfo> userlists;
    private String gameaction;
    private String gametime;
    private String gameid;
    private List<String> game;
    private List<String> words;
    private String custombackground;

    public String getGuardicon() {
        return guardicon;
    }

    public void setGuardicon(String guardicon) {
        this.guardicon = guardicon;
    }

    private String  guardicon;

    public String getCustombackground() {
        return custombackground;
    }

    public void setCustombackground(String custombackground) {
        this.custombackground = custombackground;
    }

    public List<String> getWords() {
        return words;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }

    public String getVotestotal() {
        return votestotal;
    }

    public void setVotestotal(String votestotal) {
        this.votestotal = votestotal;
    }

    public String getBarrage_fee() {
        return barrage_fee;
    }

    public void setBarrage_fee(String barrage_fee) {
        this.barrage_fee = barrage_fee;
    }

    public String getUserlist_time() {
        return userlist_time;
    }

    public void setUserlist_time(String userlist_time) {
        this.userlist_time = userlist_time;
    }

    public String getChatserver() {
        return chatserver;
    }

    public void setChatserver(String chatserver) {
        this.chatserver = chatserver;
    }

    public String getPush_url() {
        return push_url;
    }

    public void setPush_url(String push_url) {
        this.push_url = push_url;
    }

    public String getPull_url() {
        return pull_url;
    }

    public void setPull_url(String pull_url) {
        this.pull_url = pull_url;
    }

    public String getShowvideo() {
        return showvideo;
    }

    public void setShowvideo(String showvideo) {
        this.showvideo = showvideo;
    }

    public String getShowvideo_url() {
        return showvideo_url;
    }

    public void setShowvideo_url(String showvideo_url) {
        this.showvideo_url = showvideo_url;
    }

    public int getNums() {
        return nums;
    }

    public void setNums(int nums) {
        this.nums = nums;
    }

    public String getGametime() {
        return gametime;
    }

    public void setGametime(String gametime) {
        this.gametime = gametime;
    }

    public String getGameid() {
        return gameid;
    }

    public void setGameid(String gameid) {
        this.gameid = gameid;
    }

    public String getGameaction() {
        return gameaction;
    }

    public void setGameaction(String gameaction) {
        this.gameaction = gameaction;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public String getIs_vip() {
        return is_vip;
    }

    public void setIs_vip(String is_vip) {
        this.is_vip = is_vip;
    }

    public String getLive_chat_level() {
        return live_chat_level;
    }

    public void setLive_chat_level(String live_chat_level) {
        this.live_chat_level = live_chat_level;
    }

    public String getIsattention() {
        return isattention;
    }

    public void setIsattention(String isattention) {
        this.isattention = isattention;
    }

    public List<?> getGame() {
        return game;
    }

    public void setGame(List<String> game) {
        this.game = game;
    }

    public List<SimpleUserInfo> getUserlists() {
        return userlists;
    }

    public void setUserlists(List<SimpleUserInfo> userlists) {
        this.userlists = userlists;
    }
}
