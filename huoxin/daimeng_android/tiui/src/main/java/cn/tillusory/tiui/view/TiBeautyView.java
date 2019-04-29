package cn.tillusory.tiui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import cn.tillusory.sdk.TiSDKManager;
import cn.tillusory.sdk.bean.TiTypeEnum;
import cn.tillusory.tiui.R;

/**
 * Created by Anko on 2018/5/12.
 * Copyright (c) 2018 拓幻科技 - tillusory.cn. All rights reserved.
 */
public class TiBeautyView extends LinearLayout implements Observer {

    private TiSDKManager tiSDKManager;

    private ImageView switchIV;
    private List<SeekBar> seekBars = new ArrayList<>();
    private List<TextView> textViews = new ArrayList<>();

    public TiBeautyView(Context context) {
        super(context);
    }

    public TiBeautyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TiBeautyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TiBeautyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public TiBeautyView init(@NonNull TiSDKManager tiSDKManager) {
        this.tiSDKManager = tiSDKManager;

        initView();

        initData();

        return this;
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_beauty, this);

        switchIV = findViewById(R.id.enableBeautyIV);

        seekBars.add(0, (SeekBar) findViewById(R.id.whiteningSB));
        seekBars.add(1, (SeekBar) findViewById(R.id.blemishRemovalSB));
        seekBars.add(2, (SeekBar) findViewById(R.id.tendernessSB));
        seekBars.add(3, (SeekBar) findViewById(R.id.saturationSB));

        textViews.add(0, (TextView) findViewById(R.id.whiteningTV));
        textViews.add(1, (TextView) findViewById(R.id.blemishRemovalTV));
        textViews.add(2, (TextView) findViewById(R.id.tendernessTV));
        textViews.add(3, (TextView) findViewById(R.id.saturationTV));
    }

    private void initData() {
        //屏蔽点击事件
        setOnClickListener(null);

        switchIV.setSelected(tiSDKManager.isBeautyEnable());

        textViews.get(0).setText(new StringBuilder().append(tiSDKManager.getSkinWhitening()));
        textViews.get(1).setText(new StringBuilder().append(tiSDKManager.getSkinBlemishRemoval()));
        textViews.get(2).setText(new StringBuilder().append(tiSDKManager.getSkinTenderness()));
        textViews.get(3).setText(new StringBuilder().append(tiSDKManager.getSkinSaturation()));

        for (SeekBar seekBar : seekBars) {
            seekBar.setEnabled(tiSDKManager.isBeautyEnable());
        }

        switchIV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switchIV.setSelected(!switchIV.isSelected());
                tiSDKManager.setBeautyEnable(switchIV.isSelected());
                for (SeekBar seekBar : seekBars) {
                    seekBar.setEnabled(switchIV.isSelected());
                }
            }
        });

        seekBars.get(0).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textViews.get(0).setText(new StringBuilder().append(progress));
                tiSDKManager.setSkinWhitening(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBars.get(0).setProgress(tiSDKManager.getSkinWhitening());

        seekBars.get(1).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textViews.get(1).setText(new StringBuilder().append(progress));
                tiSDKManager.setSkinBlemishRemoval(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBars.get(1).setProgress(tiSDKManager.getSkinBlemishRemoval());

        seekBars.get(2).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textViews.get(2).setText(new StringBuilder().append(progress));
                tiSDKManager.setSkinTenderness(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBars.get(2).setProgress(tiSDKManager.getSkinTenderness());

        seekBars.get(3).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textViews.get(3).setText(new StringBuilder().append(progress));
                tiSDKManager.setSkinSaturation(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBars.get(3).setProgress(tiSDKManager.getSkinSaturation());
    }

    @Override
    public void update(Observable o, Object arg) {

        if (arg instanceof TiTypeEnum) {
            if (arg == TiTypeEnum.Beauty) {
                setVisibility(VISIBLE);
            } else {
                setVisibility(GONE);
            }
        }

    }
}
