package cn.tillusory.tiui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.util.Observable;
import java.util.Observer;

import cn.tillusory.sdk.TiSDKManager;
import cn.tillusory.sdk.bean.TiDistortionEnum;
import cn.tillusory.sdk.bean.TiFilterEnum;
import cn.tillusory.sdk.bean.TiRockEnum;
import cn.tillusory.sdk.bean.TiTypeEnum;
import cn.tillusory.tiui.adapter.TiDistortionAdapter;
import cn.tillusory.tiui.adapter.TiFilterAdapter;
import cn.tillusory.tiui.adapter.TiRockAdapter;
import cn.tillusory.tiui.adapter.TiTypeAdapter;
import cn.tillusory.tiui.view.TiBeautyView;
import cn.tillusory.tiui.view.TiFaceTrimView;
import cn.tillusory.tiui.view.TiGiftView;
import cn.tillusory.tiui.view.TiStickerView;

import static android.support.v7.widget.RecyclerView.HORIZONTAL;

/**
 * Created by Anko on 2018/5/12.
 * Copyright (c) 2018 拓幻科技 - tillusory.cn. All rights reserved.
 */
public class TiPanelLayout extends ConstraintLayout implements Observer {

    private TiSDKManager tiSDKManager;
    private RecyclerView typeRV;
    private RecyclerView itemRV;
    private ImageView beautyIV;
    private TiBeautyView beautyView;
    private TiFaceTrimView faceTrimView;
    private TiStickerView stickerView;
    private TiGiftView giftView;

    private TiObservable observable;

    private TiRockAdapter tiRockAdapter;
    private TiFilterAdapter tiFilterAdapter;
    private TiDistortionAdapter tiDistortionAdapter;

    public TiPanelLayout(Context context) {
        super(context);
    }

    public TiPanelLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TiPanelLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TiPanelLayout init(@NonNull TiSDKManager tiSDKManager) {
        this.tiSDKManager = tiSDKManager;

        initView();

        initData();

        return this;
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_panel, this);

        typeRV = findViewById(R.id.typeRV);
        itemRV = findViewById(R.id.itemRV);
        beautyIV = findViewById(R.id.beautyIV);
        beautyView = findViewById(R.id.tiBeautyView);
        faceTrimView = findViewById(R.id.tiFaceTrimView);
        stickerView = findViewById(R.id.tiStickerView);
        giftView = findViewById(R.id.tiGiftView);
    }

    private void initData() {

        beautyView.init(tiSDKManager);
        faceTrimView.init(tiSDKManager);
        stickerView.init(tiSDKManager);
        giftView.init(tiSDKManager);

        //增加通知
        observable = new TiObservable();
        observable.addObserver(beautyView);
        observable.addObserver(faceTrimView);
        observable.addObserver(stickerView);
        observable.addObserver(giftView);
        observable.addObserver(this);

        tiRockAdapter = new TiRockAdapter(TiRockEnum.values(), tiSDKManager);
        tiFilterAdapter = new TiFilterAdapter(TiFilterEnum.values(), tiSDKManager);
        tiDistortionAdapter = new TiDistortionAdapter(TiDistortionEnum.values(), tiSDKManager);

        //set typeRV
        typeRV.setLayoutManager(new LinearLayoutManager(getContext(), HORIZONTAL, false));
        typeRV.setAdapter(new TiTypeAdapter(TiTypeEnum.values(), observable));

        //set itemRV
        itemRV.setLayoutManager(new LinearLayoutManager(getContext(), HORIZONTAL, false));

        beautyIV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                beautyIV.setSelected(!beautyIV.isSelected());
                observable.notifyObservers(beautyIV.isSelected());

                typeRV.getAdapter().notifyDataSetChanged();
            }
        });

        //空白处隐藏面板
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                v.performClick();

                beautyIV.setSelected(false);
                observable.notifyObservers(false);

                return false;
            }
        });
    }

    public void clickMeiyan(){
        beautyIV.setSelected(!beautyIV.isSelected());
        observable.notifyObservers(beautyIV.isSelected());

        typeRV.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof Boolean && !(Boolean) arg) {
            typeRV.setVisibility(GONE);
            itemRV.setVisibility(GONE);
            beautyView.setVisibility(GONE);
            faceTrimView.setVisibility(GONE);
            stickerView.setVisibility(GONE);
            giftView.setVisibility(GONE);
            return;
        } else {
            typeRV.setVisibility(VISIBLE);
        }

        if (arg instanceof TiTypeEnum) {
            switch ((TiTypeEnum) arg) {
                case Rock:
                    itemRV.setVisibility(VISIBLE);
                    itemRV.setAdapter(tiRockAdapter);
                    itemRV.scrollToPosition(tiRockAdapter.getSelectedPosition());
                    break;
                case Filter:
                    itemRV.setVisibility(VISIBLE);
                    itemRV.setAdapter(tiFilterAdapter);
                    itemRV.scrollToPosition(tiFilterAdapter.getSelectedPosition());
                    break;
                case Distortion:
                    itemRV.setVisibility(VISIBLE);
                    itemRV.setAdapter(tiDistortionAdapter);
                    itemRV.scrollToPosition(tiDistortionAdapter.getSelectedPosition());
                    break;
                default:
                    itemRV.setVisibility(GONE);
                    break;
            }
        }
    }

}
