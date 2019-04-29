package com.daimeng.livee.bean.callback;

/**
 * Created by 魏鹏 on 2018/4/30.
 * 郑州秀星网络科技有限公司
 */

public class RequestGetCloudVideoInfoCallback {
    private String money;
    private String video_url;
    private String is_pay;
    private String pay_type;

    public String getPay_type() {
        return pay_type;
    }

    public void setPay_type(String pay_type) {
        this.pay_type = pay_type;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getIs_pay() {
        return is_pay;
    }

    public void setIs_pay(String is_pay) {
        this.is_pay = is_pay;
    }
}
