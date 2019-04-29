package cn.tillusory.tiui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import cn.tillusory.sdk.TiSDKManager;
import cn.tillusory.sdk.bean.TiSticker;
import cn.tillusory.sdk.bean.TiTypeEnum;
import cn.tillusory.tiui.R;
import cn.tillusory.tiui.adapter.TiStickerAdapter;

/**
 * Created by Anko on 2018/5/23.
 * Copyright (c) 2018 拓幻科技 - tillusory.cn. All rights reserved.
 */
public class TiStickerView extends LinearLayout implements Observer {

    private TiSDKManager tiSDKManager;
    private RecyclerView stickerRV;
    private List<TiSticker> stickerList = new ArrayList<>();

    public TiStickerView(Context context) {
        super(context);
    }

    public TiStickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TiStickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TiStickerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public TiStickerView init(@NonNull TiSDKManager tiSDKManager) {
        this.tiSDKManager = tiSDKManager;

        initView();

        initData();

        return this;
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_sticker, this);

        stickerRV = findViewById(R.id.stickerRV);
    }

    private void initData() {
        stickerList.add(TiSticker.NO_STICKER);
        stickerList.addAll(TiSticker.getAllStickers(getContext()));
        TiStickerAdapter stickerAdapter = new TiStickerAdapter(stickerList, tiSDKManager);

        stickerRV.setLayoutManager(new GridLayoutManager(getContext(), 5));
        stickerRV.setAdapter(stickerAdapter);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof TiTypeEnum) {
            if (arg == TiTypeEnum.Sticker) {
                setVisibility(VISIBLE);
            } else {
                setVisibility(GONE);
            }
        }
    }
}
