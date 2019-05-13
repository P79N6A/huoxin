package com.daimeng.live.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimeng.live.R;
import com.daimeng.live.bean.ShortVideoReplyBean;
import com.daimeng.live.utils.LiveUtils;
import com.daimeng.live.utils.SimpleUtils;
import com.daimeng.live.utils.StringUtils;
import com.daimeng.live.utils.UIHelper;

import java.util.List;
import java.util.Locale;

/**
 * Created by weipeng on 2017/10/18.
 */

public class ShortVideoReplyListAdapter extends BaseQuickAdapter<ShortVideoReplyBean,BaseViewHolder> {

    private Context mContext;
    public ShortVideoReplyListAdapter(Context context,List<ShortVideoReplyBean> data) {
        super(R.layout.item_short_video_reply,data);
        mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, final ShortVideoReplyBean item) {

        if(StringUtils.toInt(item.getReply_comment_count()) > 0){
            helper.setVisible(R.id.item_short_video_reply_comment,true);
            helper.setText(R.id.item_short_video_reply_comment,String.format(Locale.CHINA,"查看全部%s条回复",item.getReply_comment_count()));
        }else{

            helper.setVisible(R.id.item_short_video_reply_comment,false);
        }

        //查看所有评论
        helper.setOnClickListener(R.id.item_short_video_reply_comment, new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UIHelper.showShortVideoReplyCommentListActivity(mContext,item.getId());

            }
        });

        helper.setText(R.id.item_short_video_reply_body,item.getBody());
        helper.setText(R.id.item_short_video_reply_name,item.getUser_nicename());
        helper.setText(R.id.item_short_video_reply_time,item.getAddtime());
        SimpleUtils.loadImageForView(mContext, (ImageView) helper.getView(R.id.item_short_video_avatar), LiveUtils.getHttpUrl(item.getAvatar()),0);
    }
}
