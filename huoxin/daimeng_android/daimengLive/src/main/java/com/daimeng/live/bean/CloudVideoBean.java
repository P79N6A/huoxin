package com.daimeng.live.bean;

import com.daimeng.live.AppContext;
import com.daimeng.live.utils.LiveUtils;

/**
 * Created by 魏鹏 on 2018/4/30.
 * 郑州秀星网络科技有限公司
 */

public class CloudVideoBean {


    /**
     * title : 测试
     * cover_url : http://qiniu1.anbig.com/20180429_5ae5e5b40e00a.jpg
     * video_url : /data/upload/video/big_buck_bunny.mp4
     * pay_type : 0
     * money :
     * id : 1
     */

    private String title;
    private String cover_url;
    private String video_url;
    private String pay_type;
    private String money;
    private String id;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover_url() {
        return cover_url;
    }

    public void setCover_url(String cover_url) {
        this.cover_url = cover_url;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getPay_type() {
        return pay_type;
    }

    public void setPay_type(String pay_type) {
        this.pay_type = pay_type;
    }

    public String getMoney() {
        return money;
    }
    public String getFormatMoney() {
        return money +  LiveUtils.getConfigBean(AppContext.getInstance()).name_coin;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
