package com.daimeng.livee.ui;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.daimeng.livee.ui.customviews.ActivityTitle;
import com.daimeng.livee.R;
import com.daimeng.livee.adapter.OrderAdapter;
import com.daimeng.livee.api.remote.ApiUtils;
import com.daimeng.livee.api.remote.PhoneLiveApi;
import com.daimeng.livee.base.BaseActivity;
import com.daimeng.livee.bean.OrderBean;
import com.daimeng.livee.utils.UIHelper;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import org.json.JSONArray;

import java.util.ArrayList;

import butterknife.BindView;
import okhttp3.Call;

/**
 * 贡献排行榜
 */
public class DedicateOrderActivity extends BaseActivity {
    private ArrayList<OrderBean> mOrderList = new ArrayList<>();
    @BindView(R.id.lv_order)
    ListView mOrderListView;
    @BindView(R.id.view_title)
    ActivityTitle mActivityTitle;
    private OrderAdapter mOrderAdapter;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_order;
    }

    @Override
    public void initView() {
        mOrderAdapter = new OrderAdapter(mOrderList,this);
        mOrderListView.setAdapter(mOrderAdapter);

        mOrderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UIHelper.showHomePageActivity(DedicateOrderActivity.this,mOrderList.get(position).getUid());
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

        requestGetData();

    }

    private void requestGetData() {

        PhoneLiveApi.getYpOrder(getIntent().getStringExtra("uid"), new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response,int id) {
                JSONArray res = ApiUtils.checkIsSuccess(response);

                if(null != res){
                    mOrderList.clear();
                    mOrderList.addAll(ApiUtils.formatDataToList(res,OrderBean.class));
                    fillUI();
                }
            }
        });
    }

    private void fillUI() {

       mOrderAdapter.notifyDataSetChanged();
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
        OkHttpUtils.getInstance().cancelTag("getYpOrder");
    }
}
