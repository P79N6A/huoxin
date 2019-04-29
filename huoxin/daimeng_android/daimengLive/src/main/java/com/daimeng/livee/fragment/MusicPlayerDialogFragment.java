package com.daimeng.livee.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;

import com.daimeng.livee.ui.RtmpPushActivity;
import com.daimeng.livee.other.LiveStream;
import com.daimeng.livee.R;
import com.daimeng.livee.widget.LiveSeekBar;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;


public class MusicPlayerDialogFragment extends DialogFragment {


    //悠扬
    @BindView(R.id.dialog_music_btn_melodious)
    Button mBtnMelodious;
    //空灵
    @BindView(R.id.dialog_music_btn_ethereal)
    Button mBtnEthereal;

    @BindView(R.id.dialog_music_btn_ktv)
    Button mBtnKtv;

    //伴奏
    @BindView(R.id.dialog_music_sb_banzou)
    LiveSeekBar mSbBanZou;

    @BindView(R.id.dialog_music_sb_voice)
    LiveSeekBar mSbVoice;

    private Button[] mBtns= new Button[3];

    private LiveStream mStream;

    public MusicPlayerDialogFragment() {
    }
    @SuppressLint("ValidFragment")


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_show_music_player);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.BOTTOM;
        window.setWindowAnimations(R.style.BottomToTopAnim);
        window.setAttributes(params);
        ButterKnife.bind(this,dialog);
        initData();
        initView(dialog);

        return dialog;
    }



    private void initData() {
        mStream = ((RtmpPushActivity)getActivity()).mStreamer;

    }

    private void initView(Dialog dialog) {
        mBtns[0] = mBtnMelodious;
        mBtns[1] = mBtnEthereal;
        mBtns[2] = mBtnKtv;

        mSbBanZou.setProgress(mStream.getMusicVolue() == 0 ? 50:mStream.getMusicVolue());
        mSbVoice.setProgress(mStream.getMvoice() == 0 ? 50:mStream.getMvoice());

        //伴奏调节

        mSbBanZou.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                float volume = (float) (seekBar.getProgress())/100;
                mStream.setMusicVolue(seekBar.getProgress());
                //mBgMusic.setVolume(volume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //人声调节
        mSbVoice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                float volume = (float) (seekBar.getProgress())/100;
                mStream.setMvoice(seekBar.getProgress());
                mStream.setVoiceVolume(volume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @OnClick({R.id.iv_close,R.id.dialog_music_btn_melodious,R.id.dialog_music_btn_ktv,R.id.dialog_music_btn_ethereal})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.dialog_music_btn_melodious:
                //mStream.setReverbLevel(1);
                switchSelectState(0);
                break;
            case R.id.dialog_music_btn_ethereal:
                //mStream.setReverbLevel(2);
                switchSelectState(1);
                break;
            case R.id.dialog_music_btn_ktv:
                //mStream.setReverbLevel(3);
                switchSelectState(2);
                break;
            case R.id.iv_close:
                dismiss();
                break;

        }

    }

    //切换按钮状态
    private void switchSelectState(int p) {

        for(int i = 0; i < mBtns.length ; i++){
            if(p != i){
                mBtns[i].setBackgroundResource(R.drawable.btn_circular_not_fill);
            }else{
                mBtns[i].setBackgroundResource(R.drawable.btn_circular_fill);
            }
        }
    }
}