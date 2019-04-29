package cn.tillusory.tiui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.tillusory.sdk.bean.TiTypeEnum;
import cn.tillusory.tiui.R;
import cn.tillusory.tiui.TiObservable;

/**
 * Created by Anko on 2018/5/12.
 * Copyright (c) 2018 拓幻科技 - tillusory.cn. All rights reserved.
 */
public class TiTypeAdapter extends RecyclerView.Adapter<TiViewHolder> {

    private TiTypeEnum[] list;

    private int selectedPosition = 0;
    private TiObservable observable;

    public TiTypeAdapter(TiTypeEnum[] list, TiObservable observable) {
        this.list = list;
        this.observable = observable;
    }

    @NonNull
    @Override
    public TiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView textView = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text, parent, false);
        observable.notifyObservers(list[selectedPosition]);
        return new TiViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(@NonNull final TiViewHolder holder, int position) {

        holder.textView.setText(list[position].getString(holder.itemView.getContext()));

        if (selectedPosition == position) {
            observable.notifyObservers(list[selectedPosition]);
            holder.textView.setSelected(true);
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
