package com.daimeng.family.ui;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.daimeng.family.adapter.FamilyUserListAdapter;
import com.daimeng.live.AppContext;
import com.daimeng.live.R;
import com.daimeng.live.api.remote.ApiUtils;
import com.daimeng.live.api.remote.PhoneLiveApi;
import com.daimeng.live.base.BaseActivity;
import com.daimeng.live.bean.SimpleUserInfo;
import com.daimeng.live.ui.customviews.ActivityTitle;
import com.daimeng.live.utils.UIHelper;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;

public class FamilyUserListActivity extends BaseActivity {

    @BindView(R.id.my_recycleView)
    RecyclerView mRecyclerView;

    @BindView(R.id.view_title)
    ActivityTitle mActivityTitle;


    private List<SimpleUserInfo> mSimpleUserInfoList = new ArrayList<>();
    private FamilyUserListAdapter mFamilyUserListAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_family_user_list2;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void initView() {

        mFamilyUserListAdapter = new FamilyUserListAdapter(mSimpleUserInfoList);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
        mRecyclerView.setAdapter(mFamilyUserListAdapter);

        mFamilyUserListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                UIHelper.showHomePageActivity(FamilyUserListActivity.this,mSimpleUserInfoList.get(position).id);
            }
        });

        mActivityTitle.setMoreListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mActivityTitle.setReturnListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void initData() {

        requestData();
    }

    private void requestData() {


        String fid = getIntent().getStringExtra("fid");
        PhoneLiveApi.requestGetFamilyUser(AppContext.getInstance().getLoginUid(),"1",fid,new StringCallback(){

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
    protected boolean hasActionBar() {
        return false;
    }
}
