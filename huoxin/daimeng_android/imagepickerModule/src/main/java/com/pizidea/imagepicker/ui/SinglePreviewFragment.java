package com.pizidea.imagepicker.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.pizidea.imagepicker.GlideImgLoader;
import com.pizidea.imagepicker.ImgLoader;
import com.pizidea.imagepicker.UilImgLoader;
import com.pizidea.imagepicker.bean.ImageItem;
import com.pizidea.imagepicker.widget.TouchImageView;

@SuppressLint("ValidFragment")
public class SinglePreviewFragment extends Fragment {
    public static final String KEY_URL = "key_url";
    private static final String TAG = "";
    private TouchImageView imageView;
    private String url;
    private Context mContext;
    private boolean enableSingleTap;
    private ImgLoader mImagePresenter;

    public SinglePreviewFragment(Context context, boolean enableSingleTap, ImgLoader imagePresenter) {
        mContext = context;
        this.enableSingleTap = enableSingleTap;
        mImagePresenter = imagePresenter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();

        ImageItem imageItem = (ImageItem) bundle.getSerializable(KEY_URL);

        url = imageItem.path;

        Log.i(TAG, "=====current show image path:" + url);

        imageView = new TouchImageView(mContext);
        imageView.setBackgroundColor(0xff000000);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(params);

        imageView.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (enableSingleTap) {
                    if(mContext instanceof ImagePreviewFragment.OnImageSingleTapClickListener){
                        ((ImagePreviewFragment.OnImageSingleTapClickListener)mContext).onImageSingleTap(e);
                    }
                }
                return false;
            }
            @Override public boolean onDoubleTapEvent(MotionEvent e) {
                return false;
            }
            @Override public boolean onDoubleTap(MotionEvent e) {
                return false;
            }

        });

        ((UilImgLoader)mImagePresenter).onPresentImage(imageView, url, imageView.getWidth());//display the image with your own ImageLoader

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return imageView;
    }

}