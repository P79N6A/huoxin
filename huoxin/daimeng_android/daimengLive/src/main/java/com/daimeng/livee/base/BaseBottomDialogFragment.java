package com.daimeng.livee.base;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.daimeng.livee.R;
import com.daimeng.livee.interf.BaseFragmentInterface;
import com.daimeng.livee.utils.DialogHelp;

import butterknife.ButterKnife;

/**
 * Created by weipeng on 2017/8/30.
 */

public class BaseBottomDialogFragment extends DialogFragment implements BaseFragmentInterface {

    private ProgressDialog mProgressDialog;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.BottomDialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(getLayout(), null);
        ButterKnife.bind(this,view);
        builder.setView(view);

        Dialog dialog = builder.create();

        dialog.setCanceledOnTouchOutside(true);
        // 设置宽度为屏宽、靠近屏幕底部。
        Window window = dialog.getWindow();
        //window.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#99FFFFFF")));
        WindowManager.LayoutParams wlp = window.getAttributes();
        //wlp.alpha = 0.95f;
        wlp.gravity = Gravity.BOTTOM;
        window.setAttributes(wlp);

        initView(view);
        initData();
        return dialog;
    }

    public int getLayout(){

        return 0;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialog.getWindow().setLayout(dm.widthPixels, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public void initView(View view) {


    }

    @Override
    public void initData() {

    }

    protected void showWaitDialog(String msg,boolean isTouch){

        mProgressDialog = DialogHelp.getWaitDialog(getContext(),msg);
        mProgressDialog.setCanceledOnTouchOutside(isTouch);
        mProgressDialog.show();
    }

    protected void hideWaitDialog(){

        if(mProgressDialog != null){
            mProgressDialog.dismiss();
        }
    }
}
