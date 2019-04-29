package com.daimeng.livee.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import com.anbetter.danmuku.model.DanMuModel;
import com.anbetter.danmuku.model.utils.DimensionUtil;
import com.anbetter.danmuku.view.IDanMuParent;
import com.anbetter.danmuku.view.OnDanMuTouchCallBackListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.daimeng.livee.AppContext;
import com.daimeng.livee.R;
import com.daimeng.livee.bean.DanmakuEntity;
import com.daimeng.livee.bean.GiftDanmuEntity;
import com.daimeng.livee.bean.RichTextParse;
import com.daimeng.livee.event.Event;
import com.daimeng.livee.widget.UrlImageSpan;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * 弹幕库使用帮助类
 * <p>
 * 建议凡是弹幕中涉及到的图片，大小控制在50kb以内，尺寸控制在100x100以内（单位像素）
 * <p>
 * Created by android_ls on 2016/12/18.
 */
public final class DanMuHelper {

    private ArrayList<WeakReference<IDanMuParent>> mDanMuViewParents;
    private Context mContext;

    public DanMuHelper(Context context) {
        this.mContext = context.getApplicationContext();
        this.mDanMuViewParents = new ArrayList<>();
    }

    public void release() {
        if (mDanMuViewParents != null) {
            for (WeakReference<IDanMuParent> danMuViewParentsRef : mDanMuViewParents) {
                if (danMuViewParentsRef != null) {
                    IDanMuParent danMuParent = danMuViewParentsRef.get();
                    if (danMuParent != null)
                        danMuParent.release();
                }
            }
            mDanMuViewParents.clear();
            mDanMuViewParents = null;
        }

        mContext = null;
    }

    public void add(final IDanMuParent danMuViewParent) {
        if (danMuViewParent != null) {
            danMuViewParent.clear();
        }

        if (mDanMuViewParents != null) {
            mDanMuViewParents.add(new WeakReference<>(danMuViewParent));
        }
    }

    //添加文字弹幕
    public void addDanMu(DanmakuEntity danmakuEntity, boolean broadcast) {
        if (mDanMuViewParents != null) {
            WeakReference<IDanMuParent> danMuViewParent = mDanMuViewParents.get(0);
            if (!broadcast) {
                danMuViewParent = mDanMuViewParents.get(1);
            }

            DanMuModel danMuView = createDanMuView(danmakuEntity);
            if (danMuViewParent != null && danMuView != null && danMuViewParent.get() != null) {
                danMuViewParent.get().add(danMuView);
            }
        }
    }

    //添加大礼物弹幕
    public void addBigGift(GiftDanmuEntity giftDanmuEntity, boolean broadcast) {
        if (mDanMuViewParents != null) {
            WeakReference<IDanMuParent> danMuViewParent = mDanMuViewParents.get(0);
            if (!broadcast) {
                danMuViewParent = mDanMuViewParents.get(1);
            }

            DanMuModel danMuView = createGiftDanView(giftDanmuEntity);
            if (danMuViewParent != null && danMuView != null && danMuViewParent.get() != null) {
                danMuViewParent.get().add(danMuView);
            }
        }
    }


    //礼物弹幕样式
    private DanMuModel createGiftDanView(final GiftDanmuEntity entity) {
        final DanMuModel danMuView = new DanMuModel();
        danMuView.setDisplayType(DanMuModel.RIGHT_TO_LEFT);
        danMuView.setPriority(DanMuModel.NORMAL);
        danMuView.marginLeft = DimensionUtil.dpToPx(mContext, 30);

//        if (entity.getType() == GiftDanmuEntity.DANMAKU_TYPE_USERCHAT) {
        // 图像
        int avatarSize = DimensionUtil.dpToPx(mContext, 30);
        danMuView.avatarWidth = avatarSize;
        danMuView.avatarHeight = avatarSize;

        // 显示的文本内容
        //String content = entity.getSender() + "送给" + entity.getReceiver() + entity.getGift() + entity.getGiftImage();
        String content = entity.getBroadcast_msg() + entity.getGiftImage();
        SpannableString spannableString = new SpannableString(content);

        ForegroundColorSpan foregroundColorSpanName1 = new ForegroundColorSpan(Color.parseColor("#00B2EE"));
        ForegroundColorSpan foregroundColorSpanName2 = new ForegroundColorSpan(Color.parseColor("#00B2EE"));

        if (StringUtils.toInt(entity.getBroadcast_type()) == 1) {

            //普通赠送礼物公屏消息处理
            spannableString.setSpan(foregroundColorSpanName1, 0, entity.getSender().length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);//设置字体颜色
            spannableString.setSpan(foregroundColorSpanName2, entity.getSender().length() + 2, entity.getSender().length() + 2 + entity.getReceiver().length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }else if(StringUtils.toInt(entity.getBroadcast_type()) == 2){
            String msg = entity.getBroadcast_msg();
            String [] msgarr =   msg.split(" ");
            spannableString.setSpan(foregroundColorSpanName1, 3, 3+msgarr[1].length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(foregroundColorSpanName2, msg.indexOf("成为")+3,  msg.indexOf("成为")+3+msgarr[3].length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        } else {
            if(!entity.getGiftImage().equals("")){
                //玩游戏获得礼物弹幕消息处理
                String msg = entity.getBroadcast_msg();
                String [] msgarr =   msg.split(" ");
                spannableString.setSpan(foregroundColorSpanName1, 3, 3+msgarr[1].length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);//设置字体颜色

            }else {
                //红包公屏弹幕消息处理
                String msg = entity.getBroadcast_msg();
//
//                String [] msgarr =   msg.split(" ");
                spannableString.setSpan(foregroundColorSpanName1, 0, msg.indexOf("发了"), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);//设置字体颜色
            }

        }
        UrlImageSpan imageSpan = new UrlImageSpan(AppContext.getInstance(), entity.getGiftImage());
        spannableString.setSpan(imageSpan, content.length() - entity.getGiftImage().length(), content.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        TLog.log("spannableString = " + spannableString);
        danMuView.text = spannableString;
        danMuView.textSize = DimensionUtil.spToPx(mContext, 14);
        danMuView.textColor = ContextCompat.getColor(mContext, R.color.white);
        danMuView.textMarginLeft = DimensionUtil.dpToPx(mContext, 5);


        // 弹幕文本背景
//            danMuView.textBackground = ContextCompat.getDrawable(mContext, R.drawable.corners_danmu);
//            danMuView.textBackgroundMarginLeft = DimensionUtil.dpToPx(mContext, 15);
//            danMuView.textBackgroundPaddingTop = DimensionUtil.dpToPx(mContext, 3);
//            danMuView.textBackgroundPaddingBottom = DimensionUtil.dpToPx(mContext, 3);
//            danMuView.textBackgroundPaddingRight = DimensionUtil.dpToPx(mContext, 15);
        danMuView.avatarHeight = 0;
        danMuView.avatarWidth = 0;
        //点击事件
        danMuView.enableTouch(true);
        danMuView.setOnTouchCallBackListener(new OnDanMuTouchCallBackListener() {

            @Override
            public void callBack(DanMuModel danMuView) {
                Event.CloseRoomEvent event = new Event.CloseRoomEvent();
                event.action = 1;
                event.stream = entity.getStream();
                event.roomId = entity.getShowid();
                EventBus.getDefault().post(event);
            }
        });

        return danMuView;
    }


    //文字弹幕样式
    private DanMuModel createDanMuView(final DanmakuEntity entity) {
        final DanMuModel danMuView = new DanMuModel();
        danMuView.setDisplayType(DanMuModel.RIGHT_TO_LEFT);
        danMuView.setPriority(DanMuModel.NORMAL);
        danMuView.marginLeft = DimensionUtil.dpToPx(mContext, 30);

        if (entity.getType() == DanmakuEntity.DANMAKU_TYPE_USERCHAT) {
            // 图像
            int avatarSize = DimensionUtil.dpToPx(mContext, 40);
            danMuView.avatarWidth = avatarSize;
            danMuView.avatarHeight = avatarSize;

            String avatarImageUrl = entity.getAvatar();
            /*Phoenix.with(mContext)
                    .setUrl(avatarImageUrl)
                    .setWidth(avatarSize)
                    .setHeight(avatarSize)
                    .setResult(new IResult<Bitmap>() {
                        @Override
                        public void onResult(Bitmap bitmap) {
                            danMuView.avatar = CircleBitmapTransform.transform(bitmap);
                        }
                    })
                    .load();*/
            Glide.with(mContext)
                    .load(avatarImageUrl)
                    .asBitmap()
                    .override(avatarSize, avatarSize)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                            danMuView.avatar = createCircleImage(bitmap,bitmap.getWidth());


                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {

                        }
                    });

            // 等级
            /*int level = entity.getLevel();
            int levelResId = getLevelResId(level);
            Drawable drawable = ContextCompat.getDrawable(mContext, levelResId);
            danMuView.levelBitmap = drawable2Bitmap(drawable);
            danMuView.levelBitmapWidth = DimensionUtil.dpToPx(mContext, 33);
            danMuView.levelBitmapHeight = DimensionUtil.dpToPx(mContext, 16);
            danMuView.levelMarginLeft = DimensionUtil.dpToPx(mContext, 5);

            if (level > 0 && level < 100) {
                danMuView.levelText = String.valueOf(level);
                danMuView.levelTextColor = ContextCompat.getColor(mContext, R.color.white);
                danMuView.levelTextSize = DimensionUtil.spToPx(mContext, 14);
            }*/

            // 显示的文本内容
            String name = entity.getName() + "：";
            String content = entity.getText();
            SpannableString spannableString = new SpannableString(name + content);
            spannableString.setSpan(
                    new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.white)),
                    0,
                    name.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            TLog.log("spannableString = " + spannableString);

            danMuView.textSize = DimensionUtil.spToPx(mContext, 14);
            danMuView.textColor = ContextCompat.getColor(mContext, R.color.white);
            danMuView.textMarginLeft = DimensionUtil.dpToPx(mContext, 5);
            danMuView.text = spannableString;

            // 弹幕文本背景
            danMuView.textBackground = ContextCompat.getDrawable(mContext, R.drawable.corners_danmu);
            danMuView.textBackgroundMarginLeft = DimensionUtil.dpToPx(mContext, 15);
            danMuView.textBackgroundPaddingTop = DimensionUtil.dpToPx(mContext, 3);
            danMuView.textBackgroundPaddingBottom = DimensionUtil.dpToPx(mContext, 3);
            danMuView.textBackgroundPaddingRight = DimensionUtil.dpToPx(mContext, 15);

            danMuView.enableTouch(true);
            danMuView.setOnTouchCallBackListener(new OnDanMuTouchCallBackListener() {

                @Override
                public void callBack(DanMuModel danMuView) {

                }
            });
        } else {
            // 显示的文本内容
            danMuView.textSize = DimensionUtil.spToPx(mContext, 14);
            danMuView.textColor = ContextCompat.getColor(mContext, R.color.white);
            danMuView.textMarginLeft = DimensionUtil.dpToPx(mContext, 5);

            if (entity.getRichText() != null) {
                danMuView.text = RichTextParse.parse(mContext, entity.getRichText(), DimensionUtil.spToPx(mContext, 18), false);
            } else {
                danMuView.text = entity.getText();
            }

            // 弹幕文本背景
            danMuView.textBackground = ContextCompat.getDrawable(mContext, R.drawable.corners_danmu);
            danMuView.textBackgroundMarginLeft = DimensionUtil.dpToPx(mContext, 15);
            danMuView.textBackgroundPaddingTop = DimensionUtil.dpToPx(mContext, 3);
            danMuView.textBackgroundPaddingBottom = DimensionUtil.dpToPx(mContext, 3);
            danMuView.textBackgroundPaddingRight = DimensionUtil.dpToPx(mContext, 15);

            danMuView.enableTouch(false);
        }

        return danMuView;
    }

    /**
     * 根据原图和变长绘制圆形图片
     *
     * @param source
     * @param min
     * @return
     */
    private Bitmap createCircleImage(Bitmap source, int min) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(min, min, Bitmap.Config.ARGB_8888);
        /**
         * 产生一个同样大小的画布
         */
        Canvas canvas = new Canvas(target);
        /**
         * 首先绘制圆形
         */
        canvas.drawCircle(min / 2, min / 2, min / 2, paint);
        /**
         * 使用SRC_IN
         */
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        /**
         * 绘制图片
         */
        canvas.drawBitmap(source, 0, 0, paint);
        return target;
    }

    /**
     * Drawable转换成Bitmap
     *
     * @param drawable
     * @return
     */
    public Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            // 转换成Bitmap
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof NinePatchDrawable) {
            // .9图片转换成Bitmap
            Bitmap bitmap = Bitmap.createBitmap(
                    drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(),
                    drawable.getOpacity() != PixelFormat.OPAQUE ?
                            Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        } else {
            return null;
        }
    }

}