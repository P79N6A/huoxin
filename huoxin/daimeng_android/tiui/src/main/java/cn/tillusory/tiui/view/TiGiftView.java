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
import cn.tillusory.sdk.bean.TiGift;
import cn.tillusory.sdk.bean.TiTypeEnum;
import cn.tillusory.tiui.R;
import cn.tillusory.tiui.adapter.TiGiftAdapter;

/**
 * Created by Anko on 2018/7/18.
 * Copyright (c) 2018 拓幻科技 - tillusory.cn. All rights reserved.
 */
public class TiGiftView extends LinearLayout implements Observer {

    private TiSDKManager tiSDKManager;
    private RecyclerView giftTV;
    private List<TiGift> giftList = new ArrayList<>();

    public TiGiftView(Context context) {
        super(context);
    }

    public TiGiftView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TiGiftView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TiGiftView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public TiGiftView init(@NonNull TiSDKManager tiSDKManager) {
        this.tiSDKManager = tiSDKManager;

        initView();

        initData();

        return this;
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_gift, this);

        giftTV = findViewById(R.id.giftRV);
    }

    private void initData() {
        giftList.add(TiGift.NO_GIFT);
        giftList.addAll(TiGift.getAllGifts(getContext()));
        TiGiftAdapter giftAdapter = new TiGiftAdapter(giftList, tiSDKManager);

        giftTV.setLayoutManager(new GridLayoutManager(getContext(), 5));
        giftTV.setAdapter(giftAdapter);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof TiTypeEnum) {
            if (arg == TiTypeEnum.Gift) {
                setVisibility(VISIBLE);
            } else {
                setVisibility(GONE);
            }
        }
    }
}
