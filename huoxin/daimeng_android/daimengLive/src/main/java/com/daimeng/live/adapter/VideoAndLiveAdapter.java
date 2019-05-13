package com.daimeng.live.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimeng.live.R;
import com.daimeng.live.bean.VideoAndLiveBean;
import com.daimeng.live.utils.LiveUtils;
import com.daimeng.live.utils.SimpleUtils;
import com.daimeng.live.utils.StringUtils;
import com.daimeng.live.utils.TDevice;

import java.util.List;

/**
 * Created by weipeng on 2017/10/23.
 */

public class VideoAndLiveAdapter extends BaseQuickAdapter<VideoAndLiveBean,BaseViewHolder> {


    private Context context;
    private int screenWidth;
    private int dpToPx1;
    public VideoAndLiveAdapter(Context context,List<VideoAndLiveBean> data) {
        super(R.layout.item_short_video_and_live,data);
        this.context = context;
        screenWidth = (int) TDevice.getScreenWidth();
        dpToPx1 = (int) TDevice.dpToPixel(1);
    }

    @Override
    protected void convert(BaseViewHolder helper, VideoAndLiveBean item) {

        int itemWidth = (screenWidth / 2) - dpToPx1/2;
        View contentView = helper.getConvertView();
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(itemWidth,(int)(itemWidth * 1.5));

        params.setMargins(dpToPx1,dpToPx1,dpToPx1,dpToPx1);
        contentView.setLayoutParams(params);

        if(StringUtils.toInt(item.getType()) == 0){

            helper.setVisible(R.id.item_ll_state,false);
        }else{
            helper.setVisible(R.id.item_ll_state,false);
        }

        helper.setText(R.id.item_tv_title,item.getTitle());
        helper.setText(R.id.item_tv_name,item.getUser_nicename());
        SimpleUtils.loadImageForView(context, (ImageView) helper.getView(R.id.item_iv_cover), LiveUtils.getHttpUrl(item.getCover_url()),0);
        SimpleUtils.loadImageForView(context, (ImageView) helper.getView(R.id.item_iv_avatar), LiveUtils.getHttpUrl(item.getAvatar()),0);
    }
}
