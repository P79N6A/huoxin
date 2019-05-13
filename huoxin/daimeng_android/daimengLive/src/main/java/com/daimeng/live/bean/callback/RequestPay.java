package com.daimeng.live.bean.callback;

public class RequestPay {
    private String pay_info;
    private String is_wap;
    private String type;

    public String getPay_info() {
        return pay_info;
    }

    public void setPay_info(String pay_info) {
        this.pay_info = pay_info;
    }

    public String getIs_wap() {
        return is_wap;
    }

    public void setIs_wap(String is_wap) {
        this.is_wap = is_wap;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
