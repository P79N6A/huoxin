package com.daimeng.livee.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.daimeng.livee.AppContext;
import com.daimeng.livee.R;
import com.daimeng.livee.adapter.RankAdapter;
import com.daimeng.livee.api.remote.ApiUtils;
import com.daimeng.livee.api.remote.PhoneLiveApi;
import com.daimeng.livee.base.BaseFragment;
import com.daimeng.livee.bean.RankBean;
import com.daimeng.livee.utils.UIHelper;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class RankListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.rv_content_list)
    RecyclerView mRvContentList;

    @BindView(R.id.sw_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    public static final String RANK_TYPE = "RANK_TYPE";
    public static final String RANK_ACTION = "RANK_ACTION";

    private ArrayList<RankBean> list = new ArrayList<>();
    private RankAdapter rankAdapter;
    private int type;
    private String action;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rank_list, null);

        ButterKnife.bind(this,view);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void initView(View view) {

        type = getArguments().getInt(RANK_TYPE);
        action = getArguments().getString(RANK_ACTION);

        rankAdapter = new RankAdapter(getContext(),type,list);
        mRvContentList.setAdapter(rankAdapter);
        mRvContentList.setLayoutManager(new LinearLayoutManager(getContext()));
        rankAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                UIHelper.showHomePageActivity(getContext(),list.get(position).getId());
            }
        });
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void initData() {
        requestData(false);
    }

    @Override
    protected void requestData(boolean refresh) {

        swipeRefreshLayout.setRefreshing(true);
        if(type == 0){

            PhoneLiveApi.requestGetIncomeOrder(AppContext.getInstance().getLoginUid(),AppContext.getInstance().getToken(),action,callback);
        }else{
            PhoneLiveApi.requestGetConsumpationOrder(AppContext.getInstance().getLoginUid(),AppContext.getInstance().getToken(),action,callback);
        }
    }

    private StringCallback callback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e, int id) {

        }

        @Override
        public void onResponse(String response, int id) {

            swipeRefreshLayout.setRefreshing(false);
            String res = ApiUtils.checkIsSuccess2(response);
            if(res != null){
                list.clear();
                list.addAll(JSON.parseArray(res,RankBean.class));
                rankAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    public void onRefresh() {
        requestData(false);
    }
}
