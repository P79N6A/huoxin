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
public class TiFaceTrimView extends LinearLayout implements Observer {

    private TiSDKManager tiSDKManager;

    private ImageView switchIV;
    private List<SeekBar> seekBars = new ArrayList<>();
    private List<TextView> textViews = new ArrayList<>();

    public TiFaceTrimView(Context context) {
        super(context);
    }

    public TiFaceTrimView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

    }

    public TiFaceTrimView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TiFaceTrimView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public TiFaceTrimView init(@NonNull TiSDKManager tiSDKManager) {
        this.tiSDKManager = tiSDKManager;

        initView();

        initData();

        return this;
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_face_trim, this);

        switchIV = findViewById(R.id.enableFaceTrimIV);

        seekBars.add(0, (SeekBar) findViewById(R.id.eyeMagnifyingSB));
        seekBars.add(1, (SeekBar) findViewById(R.id.chinSlimmingSB));

        textViews.add(0, (TextView) findViewById(R.id.eyeMagnifyingTV));
        textViews.add(1, (TextView) findViewById(R.id.chinSlimmingTV));
    }

    private void initData() {
        //屏蔽点击事件
        setOnClickListener(null);

        switchIV.setSelected(tiSDKManager.isFaceTrimEnable());

        textViews.get(0).setText(new StringBuilder().append(tiSDKManager.getEyeMagnifying()));
        textViews.get(1).setText(new StringBuilder().append(tiSDKManager.getChinSlimming()));

        for (SeekBar seekBar : seekBars) {
            seekBar.setEnabled(tiSDKManager.isFaceTrimEnable());
        }

        switchIV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switchIV.setSelected(!switchIV.isSelected());
                tiSDKManager.setFaceTrimEnable(switchIV.isSelected());
                for (SeekBar seekBar : seekBars) {
                    seekBar.setEnabled(switchIV.isSelected());
                }
            }
        });

        seekBars.get(0).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textViews.get(0).setText(new StringBuilder().append(progress));
                tiSDKManager.setEyeMagnifying(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBars.get(0).setProgress(tiSDKManager.getEyeMagnifying());

        seekBars.get(1).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textViews.get(1).setText(new StringBuilder().append(progress));
                tiSDKManager.setChinSlimming(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBars.get(1).setProgress(tiSDKManager.getChinSlimming());
    }

    @Override
    public void update(Observable o, Object arg) {

        if (arg instanceof TiTypeEnum) {
            if (arg == TiTypeEnum.FaceTrim) {
                setVisibility(VISIBLE);
            } else {
                setVisibility(GONE);
            }
        }

    }
}
