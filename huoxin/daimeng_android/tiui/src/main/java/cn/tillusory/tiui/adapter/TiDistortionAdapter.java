package cn.tillusory.tiui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.tillusory.sdk.TiSDKManager;
import cn.tillusory.sdk.bean.TiDistortionEnum;
import cn.tillusory.tiui.R;

/**
 * Created by Anko on 2018/5/13.
 * Copyright (c) 2018 拓幻科技 - tillusory.cn. All rights reserved.
 */
public class TiDistortionAdapter extends RecyclerView.Adapter<TiViewHolder> {

    private TiDistortionEnum[] list;

    private int selectedPosition = 0;
    private TiSDKManager tiSDKManager;

    public TiDistortionAdapter(TiDistortionEnum[] list, TiSDKManager tiSDKManager) {
        this.list = list;
        this.tiSDKManager = tiSDKManager;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    @NonNull
    @Override
    public TiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView textView = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text, parent, false);
        return new TiViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(@NonNull final TiViewHolder holder, int position) {

        holder.textView.setText(list[position].getString(holder.itemView.getContext()));

        if (selectedPosition == position) {
            holder.textView.setSelected(true);
            tiSDKManager.setDistortionEnum(list[position]);
        } else {
            holder.textView.setSelected(false);
        }

        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = holder.getAdapterPosition();
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.length;
    }
}