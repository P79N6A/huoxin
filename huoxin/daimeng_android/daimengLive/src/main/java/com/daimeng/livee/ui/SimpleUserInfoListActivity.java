package com.daimeng.livee.ui;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.daimeng.livee.AppConfig;
import com.daimeng.livee.adapter.UserInfoListAdapter;
import com.daimeng.livee.api.remote.ApiUtils;
import com.daimeng.livee.base.BaseActivity;
import com.daimeng.livee.bean.SimpleUserInfo;
import com.daimeng.livee.ui.customviews.ActivityTitle;
import com.daimeng.livee.utils.UIHelper;
import com.daimeng.livee.R;
import com.daimeng.livee.api.remote.PhoneLiveApi;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;

/**
 * 关注列表
 */
public class SimpleUserInfoListActivity extends BaseActivity {
    @BindView(R.id.rv_attentions)
    RecyclerView mRecyclerView;

    @BindView(R.id.ll_loading_data_empty)
    LinearLayout mLlLoadingDataEmpty;

    @BindView(R.id.ll_loading_data_error)
    LinearLayout mLlLoadingError;

    @BindView(R.id.sr_refresh)
    SwipeRefreshLayout mRefreshLayout;

    @BindView(R.id.view_title)
    ActivityTitle mActivityTitle;

    //当前加载的页数
    private int pager = 1;

    private List<SimpleUserInfo> mSimpleUserInfoList = new ArrayList<>();
    private UserInfoListAdapter mUserInfoAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_simple_user_info_list;
    }

    @Override
    public void initView() {

        mRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.global));
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                //下拉刷新重置页数
                pager = 1;
                requestGetData();


            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUserInfoAdapter = new UserInfoListAdapter(mSimpleUserInfoList);
        mRecyclerView.setAdapter(mUserInfoAdapter);
        mUserInfoAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                UIHelper.showHomePageActivity(SimpleUserInfoListActivity.this,mSimpleUserInfoList.get(position).id);
            }
        });

        mActivityTitle.setReturnListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        mUserInfoAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override public void onLoadMoreRequested() {

                if(mSimpleUserInfoList.size() >= AppConfig.PAGE_DATA_COUNT){
                    requestGetData();
                }else{
                    mUserInfoAdapter.loadMoreEnd();
                }

            }
        }, mRecyclerView);
    }



    @Override
    public void initData() {


    }

    private void requestGetData() {


        if(getIntent().getIntExtra("type",0) == 0){
            mActivityTitle.setTitle(getResources().getString(R.string.attention));
            //关注
            PhoneLiveApi.getAttentionList(getUserID(),getIntent().getStringExtra("uid"),pager, callback);
        }else if(getIntent().getIntExtra("type",0) == 1){
            mActivityTitle.setTitle(getResources().getString(R.string.fans));
            //粉丝
            PhoneLiveApi.getFansList(getUserID(),getIntent().getStringExtra("uid"),callback);
        }

    }

    private StringCallback callback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e, int id) {

            mRefreshLayout.setRefreshing(false);
            mLlLoadingDataEmpty.setVisibility(View.GONE);
            mLlLoadingError.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onResponse(String response,int id) {
            JSONArray res = ApiUtils.checkIsSuccess(response);

            if(mRefreshLayout.isRefreshing()){
                pager = 2;
                mSimpleUserInfoList.clear();
            }

            if(res != null){

                List<SimpleUserInfo> data = ApiUtils.formatDataToList(res,SimpleUserInfo.class);
                //上拉加载增加页数
                if(!mRefreshLayout.isRefreshing()){

                    if(data.size() != 0){
                        pager ++;
                        mUserInfoAdapter.loadMoreComplete();
                    }else{
                        mUserInfoAdapter.loadMoreEnd();
                    }
                }else{
                    mUserInfoAdapter.setNewData(mSimpleUserInfoList);
                }

                mSimpleUserInfoList.addAll(data);
            }else{
                if(!mRefreshLayout.isRefreshing()){
                    mUserInfoAdapter.loadMoreFail();
                }
            }

            mRefreshLayout.setRefreshing(false);

            if (mSimpleUserInfoList.size() > 0){
                fillUI();
            }else{
                mLlLoadingDataEmpty.setVisibility(View.VISIBLE);
                mLlLoadingError.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.INVISIBLE);
            }
        }
    };

    private void fillUI() {

        mLlLoadingDataEmpty.setVisibility(View.GONE);
        mLlLoadingError.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mUserInfoAdapter.notifyDataSetChanged();


    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        mRefreshLayout.setRefreshing(true);
        pager = 1;
        requestGetData();
    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag("getAttentionList");
        OkHttpUtils.getInstance().cancelTag("getFansList");
    }
}
