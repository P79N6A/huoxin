package com.daimeng.livee.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimeng.livee.R;
import com.daimeng.livee.bean.ShortVideoBean;
import com.daimeng.livee.utils.LiveUtils;
import com.daimeng.livee.utils.SimpleUtils;
import com.daimeng.livee.utils.TDevice;

import java.util.List;

/**
 * Created by weipeng on 2017/10/16.
 */

public class ShortVideoListAdapter extends BaseQuickAdapter<ShortVideoBean,BaseViewHolder> {

    private Context context;
    private int screenWidth;
    private int dpToPx1;
    public ShortVideoListAdapter(Context context,List<ShortVideoBean> data) {
        super(R.layout.item_short_video_list,data);
        this.context = context;
        screenWidth = (int) TDevice.getScreenWidth();
        dpToPx1 = (int) TDevice.dpToPixel(1);
    }

    @Override
    protected void convert(BaseViewHolder helper, ShortVideoBean item) {

        int itemWidth = (screenWidth / 2) - dpToPx1/2;
        View contentView = helper.getConvertView();
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(itemWidth,(int)(itemWidth * 1.5));

        params.setMargins(dpToPx1,dpToPx1,dpToPx1,dpToPx1);
        contentView.setLayoutParams(params);

        helper.setText(R.id.item_tv_local,item.getCity());
        helper.setText(R.id.item_tv_title,item.getTitle());
        helper.setText(R.id.item_tv_name,item.getUser_nicename());
        helper.setText(R.id.item_short_video_reply_num,item.getReply());
        SimpleUtils.loadImageForView(context, (ImageView) helper.getView(R.id.item_iv_cover), LiveUtils.getHttpUrl(item.getCover_url()),0);
        SimpleUtils.loadImageForView(context, (ImageView) helper.getView(R.id.item_iv_avatar), LiveUtils.getHttpUrl(item.getAvatar()),0);
    }
}
