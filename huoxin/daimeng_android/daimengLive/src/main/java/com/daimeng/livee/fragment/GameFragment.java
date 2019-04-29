package com.daimeng.livee.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.daimeng.livee.R;
import com.daimeng.livee.game.adapter.GameListAdapter;
import com.daimeng.livee.api.remote.ApiUtils;
import com.daimeng.livee.api.remote.PhoneLiveApi;
import com.daimeng.livee.base.BaseFragment;
import com.daimeng.livee.bean.LiveBean;
import com.daimeng.livee.utils.UIHelper;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;


//游戏直播分类
public class GameFragment extends BaseFragment {

    @BindView(R.id.rl_list)
    RecyclerView mRecyclerView;

    @BindView(R.id.ll_loading_data_empty)
    LinearLayout mLlLoadingDataEmpty;

    @BindView(R.id.ll_loading_data_error)
    LinearLayout mLlLoadingDataError;

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefresh;

    private List<LiveBean> mListLive = new ArrayList<>();
    private GameListAdapter mGameListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);

        ButterKnife.bind(this,view);
        initView(view);
        initData();

        return view;
    }

    @Override
    public void initData() {

        requestData(false);
    }

    @Override
    public void initView(View view) {


        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        mGameListAdapter = new GameListAdapter(mListLive);
        mRecyclerView.setAdapter(mGameListAdapter);

        mGameListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                UIHelper.startRtmpPlayerActivity(getContext(),mListLive.get(position));
            }
        });
        mSwipeRefresh.setColorSchemeColors(getResources().getColor(R.color.global));
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestData(false);
            }
        });
    }

    @Override
    protected void requestData(boolean refresh) {
        PhoneLiveApi.requestGetGameLive(new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {
                mSwipeRefresh.setRefreshing(false);
                mLlLoadingDataEmpty.setVisibility(View.GONE);
                mLlLoadingDataError.setVisibility(View.VISIBLE);
            }

            @Override
            public void onResponse(String response, int id) {
                mSwipeRefresh.setRefreshing(false);
                JSONArray data = ApiUtils.checkIsSuccess(response);
                if(data != null){
                    try {

                        fillUI(data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void fillUI(JSONArray data) throws JSONException {

        mListLive.clear();
        mListLive.addAll(ApiUtils.formatDataToList(data,LiveBean.class));

        mGameListAdapter.notifyDataSetChanged();

        if(mListLive.size() > 0){
            mLlLoadingDataEmpty.setVisibility(View.GONE);
        }else{
            mLlLoadingDataEmpty.setVisibility(View.VISIBLE);
        }

        mLlLoadingDataError.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag("requestGetGameLive");
    }
}
