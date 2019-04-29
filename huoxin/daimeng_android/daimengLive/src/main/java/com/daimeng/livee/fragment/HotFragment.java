package com.daimeng.livee.fragment;


import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.daimeng.livee.adapter.NewestAdapter;
import com.daimeng.livee.bean.LiveBean;
import com.daimeng.livee.bean.RollPicBean;
import com.daimeng.livee.api.remote.ApiUtils;
import com.daimeng.livee.api.remote.PhoneLiveApi;
import com.daimeng.livee.utils.UIHelper;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import okhttp3.Call;


/**
 * @dw 首页热门
 */
public class HotFragment extends BaseListFragment<LiveBean> {

    @Override
    public void initData() {

    }

    @Override
    public void initView(View view) {
        super.initView(view);

        mBaseQuickAdapter.disableLoadMoreIfNotFullPage();
        mBaseQuickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                UIHelper.startRtmpPlayerActivity(getContext(), mListData.get(position));
            }
        });
    }

    @Override
    public BaseQuickAdapter getAdapter() {

        return new NewestAdapter(mListData);
    }

    @Override
    public RecyclerView.LayoutManager getLayoutManage() {
        return new GridLayoutManager(getContext(), 2);
    }

    @Override
    protected void requestData(boolean refresh) {

        PhoneLiveApi.requestHotDataNew(page, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {

                String res = ApiUtils.checkIsSuccess2(response);
                if (res != null) {
                    JSONObject object = JSON.parseObject(res);
                    mBanner.update(JSON.parseArray(object.getString("slide"), RollPicBean.class));
                    List<LiveBean> list = JSON.parseArray(object.getString("list"), LiveBean.class);

                    onRefuseOk(list);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        mSwipeRefreshLayout.setRefreshing(true);
        page = 1;
        requestData(false);
    }

    @Override
    protected boolean hasBanner() {
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag("selectTermsScreen");
    }
}
