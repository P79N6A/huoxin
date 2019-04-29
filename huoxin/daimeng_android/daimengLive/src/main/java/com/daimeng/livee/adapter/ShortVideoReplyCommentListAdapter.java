package com.daimeng.livee.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimeng.livee.R;
import com.daimeng.livee.bean.ShortVideoReplyBean;
import com.daimeng.livee.utils.LiveUtils;
import com.daimeng.livee.utils.SimpleUtils;

import java.util.List;

/**
 * Created by weipeng on 2017/11/7.
 */

public class ShortVideoReplyCommentListAdapter extends BaseQuickAdapter<ShortVideoReplyBean,BaseViewHolder> {

    public ShortVideoReplyCommentListAdapter(List<ShortVideoReplyBean> data) {
        super(R.layout.item_short_video_reply,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ShortVideoReplyBean item) {



        helper.setText(R.id.item_short_video_reply_body,item.getBody());
        helper.setText(R.id.item_short_video_reply_name,item.getUser_nicename());
        helper.setText(R.id.item_short_video_reply_time,item.getAddtime());
        SimpleUtils.loadImageForView(mContext, (ImageView) helper.getView(R.id.item_short_video_avatar), LiveUtils.getHttpUrl(item.getAvatar()),0);
    }
}
