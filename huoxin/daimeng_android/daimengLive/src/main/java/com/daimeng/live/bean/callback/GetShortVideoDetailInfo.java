package com.daimeng.live.bean.callback;

import com.daimeng.live.bean.ShortVideoBean;
import com.daimeng.live.bean.UserBean;
import com.daimeng.live.utils.StringUtils;

/**
 *
 * @author weipeng
 * @date 2017/10/19
 */

public class GetShortVideoDetailInfo {
    private String follow_num;
    private String reply_num;
    private String forward_num;
    private String follow_state;
    private String video_url;

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getPlayer_num() {
        return player_num;
    }

    public void setPlayer_num(String player_num) {
        this.player_num = player_num;
    }

    private String player_num;
    private ShortVideoBean short_video;
    private UserBean user_info;

    public ShortVideoBean getShort_video() {
        return short_video;
    }

    public void setShort_video(ShortVideoBean short_video) {
        this.short_video = short_video;
    }

    public UserBean getUser_info() {
        return user_info;
    }

    public void setUser_info(UserBean user_info) {
        this.user_info = user_info;
    }

    public String getFollow_state() {
        return follow_state;
    }

    public void setFollow_state(String follow_state) {
        this.follow_state = follow_state;
    }

    public String getFollow_num() {
        return follow_num;
    }

    public void setFollow_num(String follow_num) {
        this.follow_num = follow_num;
    }

    public String getReply_num() {
        return reply_num;
    }

    public void setReply_num(String reply_num) {
        this.reply_num = reply_num;
    }

    public String getForward_num() {
        return forward_num;
    }

    public void setForward_num(String forward_num) {
        this.forward_num = forward_num;
    }

    public void incFollow_num(int num){

        follow_num = String.valueOf(StringUtils.toInt(num) + StringUtils.toInt(follow_num));
    }

    public void decFollow_num(int num){

        follow_num = String.valueOf(StringUtils.toInt(follow_num) - StringUtils.toInt(num));
    }
}
