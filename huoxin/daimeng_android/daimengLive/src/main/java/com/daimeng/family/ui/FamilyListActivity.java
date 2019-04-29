package com.daimeng.family.ui;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.daimeng.family.adapter.FamilyListAdapter;
import com.daimeng.family.bean.FamilyBean;
import com.daimeng.family.event.FamilyEvent;
import com.daimeng.livee.AppContext;
import com.daimeng.livee.R;
import com.daimeng.livee.api.remote.ApiUtils;
import com.daimeng.livee.api.remote.PhoneLiveApi;
import com.daimeng.livee.base.BaseActivity;
import com.daimeng.livee.ui.customviews.ActivityTitle;
import com.daimeng.livee.utils.UIHelper;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;

public class FamilyListActivity extends BaseActivity {


    @BindView(R.id.my_recycleView)
    RecyclerView mRecyclerView;

    @BindView(R.id.view_title)
    ActivityTitle mActivityTitle;

    private List<FamilyBean> mFamilyList = new ArrayList<>();
    private FamilyListAdapter mFamilyListAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_family_list;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void initView() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mFamilyListAdapter = new FamilyListAdapter(mFamilyList);
        mRecyclerView.setAdapter(mFamilyListAdapter);

        //点击查看家族详情
        mFamilyListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                UIHelper.showFamilyDetailActivity(FamilyListActivity.this,mFamilyList.get(position).fid,3);
            }
        });

        mActivityTitle.setReturnListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mActivityTitle.setMoreListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void initData() {


        PhoneLiveApi.requestGetFamilyList(getUserID(),new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {

                JSONArray res = ApiUtils.checkIsSuccess(response);
                if(res != null){

                    mFamilyList.clear();
                    mFamilyList.addAll(ApiUtils.formatDataToList(res,FamilyBean.class));
                    mFamilyListAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    protected boolean hasActionBar() {
        return false;
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
    public void onMessageEvent(final FamilyEvent event) {

        if(event.action == 1){

            showWaitDialog("正在加载中...",false);
            PhoneLiveApi.requestJoinFamily(AppContext.getInstance().getLoginUid(),AppContext.getInstance().getToken(),mFamilyList.get(event.pos).fid,new StringCallback(){

                @Override
                public void onError(Call call, Exception e, int id) {
                    hideWaitDialog();
                }

                @Override
                public void onResponse(String response, int id) {

                    hideWaitDialog();
                    JSONArray res = ApiUtils.checkIsSuccess(response);
                    if(res != null){

                        mFamilyList.get(event.pos).join_state = "1";
                        mFamilyListAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }
}
