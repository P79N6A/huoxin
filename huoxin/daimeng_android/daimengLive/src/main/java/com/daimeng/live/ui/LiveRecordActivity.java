package com.daimeng.live.ui;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.daimeng.live.adapter.LiveRecordAdapter;
import com.daimeng.live.api.remote.ApiUtils;
import com.daimeng.live.api.remote.PhoneLiveApi;
import com.daimeng.live.base.BaseActivity;
import com.daimeng.live.bean.LiveRecordBean;
import com.daimeng.live.R;
import com.daimeng.live.ui.customviews.ActivityTitle;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;

/**
 * 直播记录
 */
public class LiveRecordActivity extends BaseActivity {
    @BindView(R.id.rv_live_record)
    RecyclerView mRvLiveRecord;

    @BindView(R.id.fensi)
    LinearLayout mLlLoadingDataEmpty;

    @BindView(R.id.load)
    LinearLayout mLlLoadingError;

    //刷新
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.view_title)
    ActivityTitle mActivityTitle;

    private ArrayList<LiveRecordBean> mRecordList = new ArrayList<>();

    //当前加载的页数
    private int pager = 1;

    //当前选中的直播记录bean
    private LiveRecordBean mLiveRecordBean;
    private LiveRecordAdapter mLiveRecordAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_record;
    }

    @Override
    public void initView() {

        mLiveRecordAdapter = new LiveRecordAdapter(mRecordList);
        mLiveRecordAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mLiveRecordBean = mRecordList.get(position);
                showLiveRecord();
            }
        });
        mRvLiveRecord.setLayoutManager(new LinearLayoutManager(this));
        mRvLiveRecord.setAdapter(mLiveRecordAdapter);
        //上拉加载更多
        mLiveRecordAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                pager ++;
                requestData();
            }
        },mRvLiveRecord);

        mActivityTitle.setReturnListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pager = 1;
                requestData();
            }
        });
    }

    @Override
    public void initData() {
        setActionBarTitle(getString(R.string.liverecord));
        requestData();
    }

    //打开回放记录
    private void showLiveRecord() {

        showWaitDialog("正在获取回放...",false);
        PhoneLiveApi.getLiveRecordById(mLiveRecordBean.getId(),showLiveByIdCallback);

    }
    private StringCallback showLiveByIdCallback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e,int id) {
            hideWaitDialog();
        }

        @Override
        public void onResponse(String response,int id) {
            hideWaitDialog();
            JSONArray res = ApiUtils.checkIsSuccess(response);

            if(res != null){
                try {
                    mLiveRecordBean.setVideo_url(res.getJSONObject(0).getString("url"));
                    LiveRecordPlayerActivity.startVideoBack(LiveRecordActivity.this,mLiveRecordBean);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    };
    private StringCallback callback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e,int id) {
            mLlLoadingDataEmpty.setVisibility(View.GONE);
            mLlLoadingError.setVisibility(View.VISIBLE);
            mRvLiveRecord.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onResponse(String response,int id) {
            swipeRefreshLayout.setRefreshing(false);
            JSONArray res = ApiUtils.checkIsSuccess(response);

            if(res != null){
                List<LiveRecordBean> data = ApiUtils.formatDataToList(res,LiveRecordBean.class);

                if(pager == 1){
                    mRecordList.clear();
                }

                if(data.size() != 0){
                    mLiveRecordAdapter.loadMoreComplete();
                }else{
                    mLiveRecordAdapter.loadMoreEnd();
                }

                mRecordList.addAll(data);
            }else{
                mLiveRecordAdapter.loadMoreFail();
            }

            if (mRecordList.size() > 0){
                fillUI();
            }else{
                mLlLoadingDataEmpty.setVisibility(View.VISIBLE);
                mLlLoadingError.setVisibility(View.GONE);
                mRvLiveRecord.setVisibility(View.INVISIBLE);
            }

        }
    };



    //请求数据
    private void requestData() {

        PhoneLiveApi.getLiveRecord(pager,getIntent().getStringExtra("uid"),callback);
    }

    private void fillUI() {
        mLlLoadingDataEmpty.setVisibility(View.GONE);
        mLlLoadingError.setVisibility(View.GONE);
        mRvLiveRecord.setVisibility(View.VISIBLE);
        mLiveRecordAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag("getLiveRecordById");
        OkHttpUtils.getInstance().cancelTag("getLiveRecord");
    }
}
