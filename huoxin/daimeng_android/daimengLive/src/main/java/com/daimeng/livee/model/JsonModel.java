package com.daimeng.livee.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class JsonModel {

    @Id(autoincrement = true)
    private Long id;

    private String key;

    private String value;


    @Generated(hash = 881125148)
    public JsonModel(Long id, String key, String value) {
        this.id = id;
        this.key = key;
        this.value = value;
    }

    @Generated(hash = 1855619162)
    public JsonModel() {
    }
    

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
