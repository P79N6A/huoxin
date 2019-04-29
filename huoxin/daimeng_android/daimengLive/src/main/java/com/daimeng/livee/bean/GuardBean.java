package com.daimeng.livee.bean;

public class GuardBean {
    private String  guardtoken;
    private String level;
    //coin 剩余金额
    private String coin;

    public String getGuardtoken() {
        return guardtoken;
    }

    public void setGuardtoken(String guardtoken) {
        this.guardtoken = guardtoken;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }
}
