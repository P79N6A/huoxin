package com.daimeng.live.bean;

import android.content.Intent;
import android.support.v4.app.DialogFragment;

/**
 * Created by Administrator on 2016/3/28.
 */
public class ChestMenuBean {


    private int position;
    private String menu_name;
    private int icon_res;
    private int type; //1:activity 2:dialog
    private Intent intent; //activity跳转用
//    private String url; //游戏地址
    private DialogFragment dialogFragment;
    private String tag;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getMenu_name() {
        return menu_name;
    }

    public void setMenu_name(String menu_name) {
        this.menu_name = menu_name;
    }

    public int getIcon_res() {
        return icon_res;
    }

    public void setIcon_res(int icon_res) {
        this.icon_res = icon_res;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }
//
//    public String getUrl() {
//        return url;
//    }
//
//    public void setUrl(String url) {
//        this.url = url;
//    }

    public DialogFragment getDialogFragment() {
        return dialogFragment;
    }

    public void setDialogFragment(DialogFragment dialogFragment) {
        this.dialogFragment = dialogFragment;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
