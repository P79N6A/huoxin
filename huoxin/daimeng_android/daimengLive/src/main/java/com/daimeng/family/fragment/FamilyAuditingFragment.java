package com.daimeng.family.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimeng.family.adapter.FamilyAuditingListAdapter;
import com.daimeng.family.event.FamilyAuditingEvent;
import com.daimeng.livee.AppContext;
import com.daimeng.livee.R;
import com.daimeng.livee.api.remote.ApiUtils;
import com.daimeng.livee.api.remote.PhoneLiveApi;
import com.daimeng.livee.base.BaseFragment;
import com.daimeng.livee.bean.SimpleUserInfo;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class FamilyAuditingFragment extends BaseFragment {

    @BindView(R.id.my_recycleView)
    RecyclerView mRecyclerView;

    private List<SimpleUserInfo> mSimpleUserInfoList = new ArrayList<>();
    private FamilyAuditingListAdapter mFamilyUserListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_family_auditing, container, false);

        ButterKnife.bind(this,view);

        initView(view);
        initData();
        return view;
    }

    @Override
    public void initView(View view) {

        mFamilyUserListAdapter = new FamilyAuditingListAdapter(mSimpleUserInfoList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mFamilyUserListAdapter);

    }

    @Override
    public void initData() {

        requestData(false);
    }

    @Override
    protected void requestData(boolean refresh) {


        PhoneLiveApi.requestGetFamilyUser(AppContext.getInstance().getLoginUid(),"2",AppContext.getInstance().getLoginUid(),new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {

                JSONArray res = ApiUtils.checkIsSuccess(response);
                if(res != null){

                    mSimpleUserInfoList.clear();
                    mSimpleUserInfoList.addAll(ApiUtils.formatDataToList(res,SimpleUserInfo.class));
                    mFamilyUserListAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FamilyAuditingEvent event) {


        if(event.action == 1){

            agreeOrRefuseJoinFamily(1,event.pos);
        }else if(event.action == 2){

            agreeOrRefuseJoinFamily(2,event.pos);
        }
    }

    private void agreeOrRefuseJoinFamily(int a,final int pos) {

        showWaitDialog("正在审核...");
        PhoneLiveApi.requestAuditingFamily(AppContext.getInstance().getLoginUid(),AppContext.getInstance().getToken(),mSimpleUserInfoList.get(pos).id,a,new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {
                hideWaitDialog();
            }

            @Override
            public void onResponse(String response, int id) {

                hideWaitDialog();
                JSONArray res = ApiUtils.checkIsSuccess(response);
                if(res != null){

                    mSimpleUserInfoList.remove(pos);
                    mFamilyUserListAdapter.notifyDataSetChanged();
                }
            }
        });
    }

}
