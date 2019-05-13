package com.daimeng.live.viewpagerfragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.daimeng.live.AppContext;
import com.daimeng.live.R;
import com.daimeng.live.ui.UserRechargeActivity;
import com.daimeng.live.utils.DialogHelp;
import com.daimeng.live.widget.X5WebView;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.utils.TbsLog;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LiveWebViewDialogFragment extends DialogFragment implements View.OnClickListener {


    @BindView(R.id.web_view)
    FrameLayout mFrameLayout;

    @BindView(R.id.im_close)
    ImageView im_close;

    private X5WebView mWebView;

    private boolean _isVisible;
    private ProgressDialog _waitDialog;
    private String mUrl;
//
//    private com.daimeng.live.interf.DialogInterface mDialogInterface;

    public static LiveWebViewDialogFragment newInstance(String url, boolean cancelable) {
        LiveWebViewDialogFragment instance = new LiveWebViewDialogFragment();
        instance.setCancelable(cancelable);
        instance.mUrl = url;
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.webview_dialog_fragment, null);

        ButterKnife.bind(this, view);
        initView(view);
        initData();
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.BottomViewTheme_Transparent);
        dialog.setContentView(R.layout.webview_dialog_fragment);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();

        window.setWindowAnimations(R.style.BottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();

        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);

        return dialog;
    }


    public void initData() {
        mWebView.loadUrl(mUrl);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.im_close:
                dismiss();
                break;

            default:
                break;
        }
    }


    private void initView(View view) {

        im_close.setOnClickListener(this);

        _isVisible = true;
        //showWaitDialog("正在加载中...", false);

        mWebView = new X5WebView(getActivity(), null);

        mFrameLayout.addView(mWebView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.FILL_PARENT,
                FrameLayout.LayoutParams.FILL_PARENT));

        mWebView.setBackgroundColor(0);

        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onJsConfirm(com.tencent.smtt.sdk.WebView arg0, String arg1, String arg2,
                                       JsResult arg3) {
                return super.onJsConfirm(arg0, arg1, arg2, arg3);
            }

            View myVideoView;
            View myNormalView;
            IX5WebChromeClient.CustomViewCallback callback;

            @Override
            public void onHideCustomView() {
                if (callback != null) {
                    callback.onCustomViewHidden();
                    callback = null;
                }
                if (myVideoView != null) {
                    ViewGroup viewGroup = (ViewGroup) myVideoView.getParent();
                    viewGroup.removeView(myVideoView);
                    viewGroup.addView(myNormalView);
                }
            }

            @Override
            public boolean onJsAlert(com.tencent.smtt.sdk.WebView arg0, String arg1, String arg2,
                                     JsResult arg3) {
                /**
                 * 这里写入你自定义的window alert
                 */

                AppContext.showToast(arg2);
                //super.onJsAlert(null, arg1, arg2, arg3)
                return true;
            }
        });


        mWebView.setWebViewClient(new com.tencent.smtt.sdk.WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(final com.tencent.smtt.sdk.WebView webView, final String url) {

                //游戏拦截
                if (url.startsWith("bugu://www.pet.com?action=recharge")) {
                    Intent intent = new Intent(getContext(), UserRechargeActivity.class);
                    startActivity(intent);
                    return true;
                }
                if (url.startsWith("bugu://www.pet.com")) {
                    return true;
                }
                webView.loadUrl(url);
                return false;
            }


        });

        mWebView.setLayerType(View.LAYER_TYPE_HARDWARE,null);//开启硬件加速
        com.tencent.smtt.sdk.WebSettings webSetting = mWebView.getSettings();
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(com.tencent.smtt.sdk.WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(false);
        // webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        // webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        //webSetting.setAppCachePath(getActivity().getDir("appcache", 0).getPath());
        ///webSetting.setDatabasePath(getActivity().getDir("databases", 0).getPath());
        //webSetting.setGeolocationDatabasePath(getActivity().getDir("geolocation", 0).getPath());
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.setPluginState(com.tencent.smtt.sdk.WebSettings.PluginState.ON_DEMAND);
        // webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        // webSetting.setPreFectch(true);
        long time = System.currentTimeMillis();
        //mWebView.loadUrl(getaR().getStringExtra("url"));
        TbsLog.d("time-cost", "cost time: "
                + (System.currentTimeMillis() - time));
        CookieSyncManager.createInstance(getContext());
        CookieSyncManager.getInstance().sync();

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        mWebView.destroy();
    }

    public void setDialogInterface(com.daimeng.live.interf.DialogInterface dialogInterface) {
    }


    public ProgressDialog showWaitDialog() {
        return showWaitDialog(R.string.loading);
    }

    public ProgressDialog showWaitDialog(int resid) {
        return showWaitDialog(getString(resid), true);
    }


    public ProgressDialog showWaitDialog(String message, boolean iscancle) {
        if (_isVisible) {
            if (_waitDialog == null) {
                _waitDialog = DialogHelp.getWaitDialog(getContext(), message);
            }
            if (_waitDialog != null) {
                _waitDialog.setCancelable(iscancle);
                _waitDialog.setCanceledOnTouchOutside(iscancle);
                _waitDialog.setMessage(message);
                _waitDialog.show();
            }
            return _waitDialog;
        }
        return null;
    }

    public void hideWaitDialog() {
        if (_isVisible && _waitDialog != null) {
            try {
                _waitDialog.dismiss();
                _waitDialog = null;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
