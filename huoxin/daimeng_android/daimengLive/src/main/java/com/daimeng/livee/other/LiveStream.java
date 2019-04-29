package com.daimeng.livee.other;

import android.content.Context;

import com.daimeng.agora.kit.KSYAgoraStreamer;


public class LiveStream extends KSYAgoraStreamer {
    private int musicVolue;
    private int mvoice ;

    public LiveStream(Context context) {
        super(context);
    }

    public int getMusicVolue() {
        return musicVolue;
    }

    public void setMusicVolue(int musicVolue) {
        this.musicVolue = musicVolue;
    }

    public int getMvoice() {
        return mvoice;
    }

    public void setMvoice(int mvoice) {
        this.mvoice = mvoice;
    }
}
