package com.daimeng.livee.bean.callback;

import com.daimeng.livee.bean.ShortVideoReplyBean;

import java.util.List;

/**
 * Created by weipeng on 2017/10/19.
 */

public class GetVideoReplyListBean {

    public String count;
    public List<ShortVideoReplyBean> list;

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public List<ShortVideoReplyBean> getList() {
        return list;
    }

    public void setList(List<ShortVideoReplyBean> list) {
        this.list = list;
    }
}
