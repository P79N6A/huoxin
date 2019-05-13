package com.daimeng.family.fragment;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.daimeng.family.adapter.FamilyUserListAdapter;
import com.daimeng.live.AppConfig;
import com.daimeng.live.AppContext;
import com.daimeng.live.R;
import com.daimeng.live.api.remote.ApiUtils;
import com.daimeng.live.api.remote.PhoneLiveApi;
import com.daimeng.live.base.BaseFragment;
import com.daimeng.live.bean.SimpleUserInfo;
import com.daimeng.live.utils.DialogHelp;
import com.daimeng.live.utils.UIHelper;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class FamilyListFragment extends BaseFragment {


    @BindView(R.id.my_recycleView)
    RecyclerView mRecyclerView;

    private List<SimpleUserInfo> mSimpleUserInfoList = new ArrayList<>();
    private FamilyUserListAdapter mFamilyUserListAdapter;
    private String mType;
    private String mFid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_family_list, container, false);

        ButterKnife.bind(this,view);

        initView(view);
        initData();
        return view;
    }

    @Override
    public void initView(View view) {

        mFamilyUserListAdapter = new FamilyUserListAdapter(mSimpleUserInfoList);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        mRecyclerView.setAdapter(mFamilyUserListAdapter);

        mFamilyUserListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view,final int position) {

                DialogHelp.getSelectDialog(getContext(), new String[]{"查看个人资料", "查看收益记录", "踢出家族"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if(i == 0){
                            UIHelper.showHomePageActivity(getContext(),mSimpleUserInfoList.get(position).id);
                        }else if(i == 1){

                            UIHelper.showWebView(getContext(), AppConfig.MAIN_URL + "/index.php?g=Appapi&m=agent&a=emccprofit&uid=" + mSimpleUserInfoList.get(position).id,"");
                        }else {

                            showWaitDialog("正在执行操作");
                            PhoneLiveApi.requestKickUser(AppContext.getInstance().getLoginUid(),AppContext.getInstance().getToken(),mSimpleUserInfoList.get(position).id,new StringCallback(){

                                @Override
                                public void onError(Call call, Exception e, int id) {
                                    hideWaitDialog();
                                }

                                @Override
                                public void onResponse(String response, int id) {
                                    hideWaitDialog();
                                    JSONArray res = ApiUtils.checkIsSuccess(response);
                                    if(res != null){

                                        mSimpleUserInfoList.remove(position);
                                        mFamilyUserListAdapter.notifyDataSetChanged();
                                    }
                                }
                            });
                        }
                    }
                }).show();

            }
        });
    }

    @Override
    public void initData() {

        mType = getArguments().getString("type");
        mFid = getArguments().getString("fid");
        requestData(false);
    }

    @Override
    protected void requestData(boolean refresh) {


        PhoneLiveApi.requestGetFamilyUser(AppContext.getInstance().getLoginUid(),mType,mFid,new StringCallback(){

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
    public void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag("requestKickUser");
    }
}
