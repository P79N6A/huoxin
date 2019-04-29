package cn.tillusory.tiui.adapter;

import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

/**
 * Created by Anko on 2018/5/12.
 * Copyright (c) 2018 拓幻科技 - tillusory.cn. All rights reserved.
 */
public class TiViewHolder extends RecyclerView.ViewHolder {

    public TextView textView;

    public TiViewHolder(TextView itemView) {
        super(itemView);
        textView = itemView;
    }

}
