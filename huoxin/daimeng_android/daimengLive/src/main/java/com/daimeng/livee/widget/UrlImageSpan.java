package com.daimeng.livee.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.daimeng.livee.R;

import java.lang.reflect.Field;

public class UrlImageSpan extends ImageSpan {

    private final Context context;
    private String url;
    //    private TextView tv;
    private boolean picShowed;

    public UrlImageSpan(Context context, String url) {
        super(context, R.drawable.load_img_loading);
        this.url = url;
        this.context = context;
    }

    @Override
    public Drawable getDrawable() {
        if (!picShowed) {
            Glide.with(context).load(url).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    Resources resources = context.getResources();
                    int targetWidth = (int) (resources.getDisplayMetrics().widthPixels * 0.8);
                    Bitmap zoom = zoom(resource, targetWidth);
                    BitmapDrawable b = new BitmapDrawable(resources, resource);

                    b.setBounds(0, 0, (int) context.getResources().getDimension(R.dimen.text_size_24), (int) context.getResources().getDimension(R.dimen.text_size_24));
                    Field mDrawable;
                    Field mDrawableRef;
                    try {
                        mDrawable = ImageSpan.class.getDeclaredField("mDrawable");
                        mDrawable.setAccessible(true);
                        mDrawable.set(UrlImageSpan.this, b);

                        mDrawableRef = DynamicDrawableSpan.class.getDeclaredField("mDrawableRef");
                        mDrawableRef.setAccessible(true);
                        mDrawableRef.set(UrlImageSpan.this, null);

                        picShowed = true;
//                        tv.setText(tv.getText());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        return super.getDrawable();
    }

    /**
     * 按宽度缩放图片
     *
     * @param bmp  需要缩放的图片源
     * @param newW 需要缩放成的图片宽度
     * @return 缩放后的图片
     */
    public static Bitmap zoom(@NonNull Bitmap bmp, int newW) {

        // 获得图片的宽高
        int width = bmp.getWidth();
        int height = bmp.getHeight();

        // 计算缩放比例
        float scale = ((float) newW) / width;

        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, true);

        return newbm;
    }
}
