package com.daimeng.livee.fragment;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.daimeng.livee.adapter.NewestAdapter;
import com.daimeng.livee.api.remote.ApiUtils;
import com.daimeng.livee.bean.LiveBean;
import com.daimeng.livee.utils.UIHelper;
import com.daimeng.livee.api.remote.PhoneLiveApi;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;


/**
 * 首页最新直播
 */
public class NewestFragment extends BaseListFragment<LiveBean>{


    @Override
    public void initData() {
        super.initData();

        mSwipeRefreshLayout.setRefreshing(true);
        requestData(false);
    }

    @Override
    public BaseQuickAdapter getAdapter() {
        return new NewestAdapter(mListData);
    }

    @Override
    public void initView(View view) {
        super.initView(view);

        mBaseQuickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                UIHelper.startRtmpPlayerActivity(getContext(),mListData.get(position));
            }
        });
    }

    @Override
    public RecyclerView.LayoutManager getLayoutManage() {

        return new GridLayoutManager(getContext(),2);
    }

    //最新主播数据请求


    @Override
    protected void requestData(boolean refresh) {

        PhoneLiveApi.getNewestUserList(page,new StringCallback() {
            @Override
            public void onError(Call call, Exception e,int id) {


            }

            @Override
            public void onResponse(String response,int id) {

                String res = ApiUtils.checkIsSuccess2(response);
                if(res != null){

                    onRefuseOk(JSON.parseArray(res,LiveBean.class));
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag("getNewestUserList");
    }



}
