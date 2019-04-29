package com.entity;

public class TuPianRootBean {
    String tupian_mingzi;

    @Override
    public String toString() {
        return "TuPianRootBean{" +
                "tupian_mingzi='" + tupian_mingzi + '\'' +
                '}';
    }
    public String getTupian_mingzi() {
        return tupian_mingzi;
    }
    public void setTupian_mingzi(String tupian_mingzi) {
        this.tupian_mingzi = tupian_mingzi;
    }
}
