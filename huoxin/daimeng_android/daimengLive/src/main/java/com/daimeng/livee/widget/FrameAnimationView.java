package com.daimeng.livee.widget;

import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.daimeng.livee.R;
import com.daimeng.livee.bean.SendGiftBean;
import com.daimeng.livee.utils.BGTimedTaskManage;
import com.opensource.svgaplayer.SVGACallback;
import com.opensource.svgaplayer.SVGADrawable;
import com.opensource.svgaplayer.SVGADynamicEntity;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;

import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FrameAnimationView extends RelativeLayout implements BGTimedTaskManage.BGTimeTaskRunnable, SVGACallback {

    private SVGAImageView svgaImageView;

    private BGTimedTaskManage timedTaskManage;

    private List<SendGiftBean> list = new ArrayList<>();
    private boolean isRunAnimation = false;
    private Context context;

    public FrameAnimationView(Context context) {
        super(context);
        init(context);
    }


    public FrameAnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FrameAnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {

        this.context = context;
        View view = inflate(context, R.layout.view_live_gift_gif_play, null);
        addView(view);

        svgaImageView = (SVGAImageView) findViewById(R.id.svga_iv);

        svgaImageView.setCallback(this);
        timedTaskManage = new BGTimedTaskManage();
    }

    public void addGift(SendGiftBean item) {

        list.add(item);

    }


    @Override
    public void onRunTask() {

        if (!isRunAnimation && list.size() > 0 && list.get(0).getSvga_url() != null) {
            isRunAnimation = true;
            SVGAParser parser = new SVGAParser(context);
            try {
                parser.parse(new URL(list.get(0).getSvga_url()), new SVGAParser.ParseCompletion() {
                    @Override
                    public void onComplete(@NotNull SVGAVideoEntity videoItem) {
                        if(list.get(0).getGiftname().equals("守护")){
                            SVGADrawable drawable = new SVGADrawable(videoItem,requestDynamicItem(list.get(0).getAvatar(),list.get(0).getShname()+" "+list.get(0).getNicename(),list.get(0).getShname().length()));
                            svgaImageView.setImageDrawable(drawable);
                            svgaImageView.startAnimation();
                        }else {
                            SVGADrawable drawable = new SVGADrawable(videoItem);
                            svgaImageView.setImageDrawable(drawable);
                            svgaImageView.startAnimation();
                        }

                    }

                    @Override
                    public void onError() {
                        isRunAnimation = false;
                    }
                });

            } catch (MalformedURLException e) {
                e.printStackTrace();
                isRunAnimation = false;
            }
        }
    }

    private SVGADynamicEntity requestDynamicItem(String url,String name,int lenth) {
        SVGADynamicEntity dynamicEntity = new SVGADynamicEntity();
        TextPaint textPaint = new TextPaint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(28);
        dynamicEntity.setDynamicImage(url,"99");

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(name);
        spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.YELLOW), 0, lenth, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//        TextPaint textPaint = new TextPaint();
//        textPaint.setColor(Color.WHITE);
//        textPaint.setTextSize(28);
        dynamicEntity.setDynamicText(new StaticLayout(
                spannableStringBuilder,
                0,
                spannableStringBuilder.length(),
                textPaint,
                0,
                Layout.Alignment.ALIGN_CENTER,
                1.0f,
                0.0f,
                false
        ), "banner");

//        dynamicEntity.setDynamicText(name, textPaint, "banner");
        return dynamicEntity;
    }

    @Override
    public void onFinished() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onRepeat() {
        isRunAnimation = false;
        if (list.size() > 0) {
            list.remove(0);
            svgaImageView.stopAnimation();
        }
    }

    @Override
    public void onStep(int i, double v) {

    }

    public void start() {

        timedTaskManage.startRunnable(this, true);
    }

    public void stop() {
        if (timedTaskManage != null) {
            timedTaskManage.stopRunnable();
        }
    }
}
