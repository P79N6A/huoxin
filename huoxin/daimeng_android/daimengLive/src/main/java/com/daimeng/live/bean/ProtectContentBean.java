package com.daimeng.live.bean;

import java.util.List;

public class ProtectContentBean {
    // id  name icon num day
    public List<ProtectItemBean> rule;

    public String[] data;

    public List<ProtectItemBean> getRule() {
        return rule;
    }

    public void setRule(List<ProtectItemBean> rule) {
        this.rule = rule;
    }

    public String[] getData() {
        return data;
    }

    public void setData(String[] data) {
        this.data = data;
    }
}
