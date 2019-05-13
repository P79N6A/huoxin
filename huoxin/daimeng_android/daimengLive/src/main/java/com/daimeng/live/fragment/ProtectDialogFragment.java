package com.daimeng.live.fragment;

import android.app.Dialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.daimeng.live.AppContext;
import com.daimeng.live.R;
import com.daimeng.live.adapter.ProtectListAdapter;
import com.daimeng.live.adapter.ViewPageGridViewAdapter;
import com.daimeng.live.api.remote.ApiUtils;
import com.daimeng.live.api.remote.PhoneLiveApi;
import com.daimeng.live.bean.ProtectBean;
import com.daimeng.live.bean.UserBean;
import com.daimeng.live.utils.TDevice;
import com.daimeng.live.utils.UIHelper;
import com.daimeng.live.widget.BlackTextView;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class ProtectDialogFragment extends DialogFragment {


    private BlackTextView mUserCoin;

    private List<ProtectBean> mProtectList = new ArrayList<>();

    private ViewPageGridViewAdapter mVpProtectAdapter;

    //守护view
    private ViewPager mVpProtectView;

    //守护服务端返回数据
    private JSONArray mProtectResStr;

    //当前选中的守护
    private ProtectBean mSelectedProtectItem;

    private int mShowProtectSendOutTime = 5;

    protected List<RecyclerView> mProtectViews = new ArrayList<>();

    private UserBean mUser;

    private Handler mHandler = new Handler();
    private int mCount = 0;
    private String hostId; //主播id
    private String stream;

    public interface CallBack{
        void work();
    }

public static ProtectDialogFragment newInstance(String hostId,String stream)
{
    ProtectDialogFragment instance=new ProtectDialogFragment();
    instance.hostId = hostId;
    instance.stream = stream;
    return instance;
}
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mUser = AppContext.getInstance().getLoginUser();

        Dialog dialog = new Dialog(getActivity(), R.style.dialog_gift);
        dialog.setContentView(R.layout.view_show_protect);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            params.height = (int) TDevice.dpToPixel(150);
        }
        window.setAttributes(params);
        initView(dialog);
        initData();
        return dialog;
    }

    private void initData() {

        requestProtectData();
    }

    private void requestProtectData() {
//        ProtectBean aa = new ProtectBean();
//        ProtectBean bb = new ProtectBean();
//        aa.setId(0);
//        aa.setProtectname("天蝎座守护");
//        aa.setProImage("http://live.demonlive.com/default.jpg");
//
//        mProtectList.add(aa);
//        bb.setId(1);
//        bb.setProImage("http://live.demonlive.com/default.jpg");
//        bb.setProtectname("狮子座守护");
//        mProtectList.add(bb);
        //  获取守护列表
        PhoneLiveApi.getProtectList(mUser.id, mUser.token, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String s, int id) {
                Log.d("lys","---"+s);
                JSONArray res = ApiUtils.checkIsSuccess(s);
                if (res != null && isAdded()) {
                    try {

                        mProtectResStr = res;
                        //  mUser.coin  = data.getString("coin");

                        fillProtect();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }
        });
    }

    //展示守护列表
    private void initView(Dialog dialog) {

        mUserCoin = (BlackTextView) dialog.findViewById(R.id.tv_show_select_user_coin);
        mUserCoin.setText(mUser.coin);
        //点击底部跳转充值页面
        dialog.findViewById(R.id.rl_show_protect_bottom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showMyDiamonds(getActivity());
            }
        });
        mVpProtectView = (ViewPager) dialog.findViewById(R.id.vp_protect_page);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

            LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(ViewPager.LayoutParams.MATCH_PARENT, (int) TDevice.dpToPixel(100));
            mVpProtectView.setLayoutParams(p1);
        }


    }


    //守护单项被选中
    private void protectItemClick(BaseQuickAdapter parent, View view, int position) {

        ProtectBean selectItem = (ProtectBean) parent.getItem(position);
        String id = selectItem.getId();

        ProtectContentDialogFragment contentDialogFragment = ProtectContentDialogFragment.newInstance(id,hostId,stream,new CallBack(){

            @Override
            public void work() {
                dismiss();
            }
        });
        contentDialogFragment.show(getActivity().getFragmentManager(), "contentDialogFragment");

    }

    //守护列表填充
    private void fillProtect() {

        if (null == mVpProtectAdapter && null != mProtectResStr) {
            //  if (null == mVpProtectAdapter) {

            if (mProtectList.size() == 0) {
                try {
                    JSONArray protectListJson = mProtectResStr;
                    for (int i = 0; i < protectListJson.length(); i++) {

                        mProtectList.add(JSON.parseObject(protectListJson.getString(i), ProtectBean.class));
                        // mProtectList.add(protectListJson.getString(i),);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            //守护item填充
            List<View> mProtectViewList = new ArrayList<>();

            int index = 0;

            int protectsPageSize;
            int protectSPageNum = 8;

            if (mProtectList.size() % protectSPageNum == 0) {

                protectsPageSize = mProtectList.size() / protectSPageNum;
            } else {
                protectsPageSize = (mProtectList.size() / protectSPageNum) + 1;
            }

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                protectsPageSize = 1;

                protectSPageNum = mProtectList.size();
            }


            for (int i = 0; i < protectsPageSize; i++) {
                View v = LayoutInflater.from(getActivity()).inflate(R.layout.view_show_protectslist, null);
                mProtectViewList.add(v);
                List<ProtectBean> l = new ArrayList<>();

                for (int j = 0; j < protectSPageNum; j++) {
                    if (index >= mProtectList.size()) {
                        break;
                    }
                    l.add(mProtectList.get(index));
                    index++;
                }
                mProtectViews.add((RecyclerView) v.findViewById(R.id.gv_protect_list));
                ProtectListAdapter protectListAdapter = new ProtectListAdapter(l);
                mProtectViews.get(i).setAdapter(protectListAdapter);
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                    mProtectViews.get(i).setLayoutManager(layoutManager);
                } else {
                    mProtectViews.get(i).setLayoutManager(new GridLayoutManager(getActivity(), 4));
                }

                protectListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                        protectItemClick(adapter, view, position);
                    }
                });

            }
            mVpProtectAdapter = new ViewPageGridViewAdapter(mProtectViewList);

        }
        mVpProtectView.setAdapter(mVpProtectAdapter);
    }
}
