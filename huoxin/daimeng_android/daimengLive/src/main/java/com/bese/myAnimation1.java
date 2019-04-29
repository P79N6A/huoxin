package com.bese;

import android.widget.ImageView;

public class myAnimation1 {
    private ImageView mImageView;   //播方动画的相应布局
    private int[] mImageRes;
    private int[] durations;

    public myAnimation1(ImageView pImageView, int[] pImageRes,
                        int[] durations) {
        this.mImageView = pImageView;
        this.durations = durations;
        this.mImageRes= pImageRes;
        mImageView.setImageResource(mImageRes[1]);
        play(1);
    }

    private void play(final int pImageNo) {
        mImageView.postDelayed(new Runnable() {     //采用延迟启动子线程的方式
            public void run() {
                mImageView.setImageResource(mImageRes[pImageNo]);
                if (pImageNo == mImageRes.length-1)
                    return;
                else
                    play(pImageNo + 1);
            }
        }, durations[pImageNo-1]);
    }
}
