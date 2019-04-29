package com.daimeng.livee.bean.callback;

import com.daimeng.livee.bean.PayMenuBean;
import com.daimeng.livee.bean.RechargeBean;

import java.util.List;

public class RequestGetRechargeInfo {


    private List<RechargeBean> recharge_rule;
    private List<PayMenuBean> pay_rule;

    public List<RechargeBean> getRecharge_rule() {
        return recharge_rule;
    }

    public void setRecharge_rule(List<RechargeBean> recharge_rule) {
        this.recharge_rule = recharge_rule;
    }

    public List<PayMenuBean> getPay_rule() {
        return pay_rule;
    }

    public void setPay_rule(List<PayMenuBean> pay_rule) {
        this.pay_rule = pay_rule;
    }

}
