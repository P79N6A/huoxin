package com.daimeng.livee.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.daimeng.livee.AppContext;
import com.daimeng.livee.AppManager;
import com.daimeng.livee.R;
import com.daimeng.livee.interf.BaseViewInterface;
import com.daimeng.livee.interf.DialogControl;
import com.daimeng.livee.ui.customviews.ActivityTitle;
import com.daimeng.livee.dialog.CommonToast;
import com.daimeng.livee.utils.DialogHelp;
import com.daimeng.livee.utils.StringUtils;
import com.daimeng.livee.utils.TDevice;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/6/22.
 */
public abstract class BaseTitleActivity extends AppCompatActivity implements
        DialogControl, View.OnClickListener, BaseViewInterface {

    private ActivityTitle mActivityTitle;
    public static final String INTENT_ACTION_EXIT_APP = "INTENT_ACTION_EXIT_APP";

    private boolean _isVisible;
    private ProgressDialog _waitDialog;

    protected LayoutInflater mInflater;
    private LinearLayout rootLayout;
    protected String mUserId;
    protected String mUserToken;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base_title);

        initActivityTile();
        if (!hasActionBar()) {
            mActivityTitle.setVisibility(View.GONE);
        }
        onBeforeSetContentLayout();
        if (getLayoutId() != 0) {
            setContentView(getLayoutId());
        }

        mInflater = getLayoutInflater();

        mUserId = AppContext.getInstance().getLoginUid();
        mUserToken = AppContext.getInstance().getToken();

        // 通过注解绑定控件
        ButterKnife.bind(this);
        init(savedInstanceState);
        initView();
        initData();
        _isVisible = true;
    }

    public void requestData(boolean isCache){

    }

    @Override
    public void setContentView(int layoutId) {
        setContentView(View.inflate(this, layoutId, null));
    }

    @Override
    public void setContentView(View view) {
        rootLayout = (LinearLayout) findViewById(R.id.root_layout);
        if (rootLayout == null) return;
        rootLayout.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

    }
    private void initActivityTile() {
        mActivityTitle = (ActivityTitle) findViewById(R.id.activity_title);
        mActivityTitle.setReturnListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void hideMoreText(){
        mActivityTitle.hideMoreText();
    }

    protected boolean regEvent(){

        return false;
    }

    @CallSuper
    @Override
    public void onStart() {
        super.onStart();
        //和removeActivity对应
        AppManager.getInstance().addActivity(this);

        if(regEvent()){
            EventBus.getDefault().register(this);
        }
    }


    @CallSuper
    @Override
    public void onStop() {
        super.onStop();

        // 极端情况下，系统会杀死APP进程，并不执行onDestroy()，
        // 因此需要使用onStop()来释放资源，从而避免内存泄漏。
        AppManager.getInstance().removeActivity(this);

        if(regEvent()){
            EventBus.getDefault().unregister(this);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        TDevice.hideSoftKeyboard(getCurrentFocus());

    }


    public void setActionBarTitle(String title) {
        if (StringUtils.isEmpty(title)) {
            title = getString(R.string.app_name);
        }
        if (hasActionBar() && mActivityTitle != null) {

            mActivityTitle.setTitle(title);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showToast(int msgResid, int icon, int gravity) {
        showToast(getString(msgResid), icon, gravity);
    }

    public void showToast(String message, int icon, int gravity) {
        CommonToast toast = new CommonToast(this);
        toast.setMessage(message);
        toast.setMessageIc(icon);
        toast.setLayoutGravity(gravity);
        toast.show();
    }

    public void setActivityMoreText(String text){
        if(mActivityTitle != null){
            mActivityTitle.setMoreText(text);
        }
    }

    public void setActivityMoreOnClick(View.OnClickListener onClick){
        if(mActivityTitle != null){
            mActivityTitle.setMoreListener(onClick);
        }
    }

    @Override
    public ProgressDialog showWaitDialog() {
        return showWaitDialog(R.string.loading);
    }

    @Override
    public ProgressDialog showWaitDialog(int resid) {
        return showWaitDialog(getString(resid),true);
    }

    @Override
    public ProgressDialog showWaitDialog2(String text, boolean iscancle) {

        return showWaitDialog(text,iscancle);
    }

    @Override
    public ProgressDialog showWaitDialog(String message, boolean iscancle) {
        if (_isVisible) {
            if (_waitDialog == null) {
                _waitDialog = DialogHelp.getWaitDialog(this, message);
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

    @Override
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

    protected void onBeforeSetContentLayout() {}

    protected boolean hasActionBar() {
        return true;
    }

    protected int getLayoutId() {
        return 0;
    }

    protected View inflateView(int resId) {
        return mInflater.inflate(resId, null);
    }

    protected int getActionBarTitle() {
        return R.string.app_name;
    }

    protected boolean hasBackButton() {
        return false;
    }

    protected void init(Bundle savedInstanceState) {}

}
