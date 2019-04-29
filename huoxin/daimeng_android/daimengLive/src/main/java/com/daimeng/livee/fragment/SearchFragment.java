package com.daimeng.livee.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.daimeng.livee.AppContext;
import com.daimeng.livee.R;
import com.daimeng.livee.adapter.UserBaseInfoAdapter;
import com.daimeng.livee.api.remote.ApiUtils;
import com.daimeng.livee.api.remote.PhoneLiveApi;
import com.daimeng.livee.base.BaseFragment;
import com.daimeng.livee.bean.SimpleUserInfo;
import com.daimeng.livee.utils.UIHelper;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 用户搜索
 */
public class SearchFragment extends BaseFragment {
    @BindView(R.id.et_search_input)
    EditText mSearchKey;

    @BindView(R.id.lv_search)
    ListView mLvSearch;


    private UserBaseInfoAdapter  mUserBaseInfoAdapter;

    private List<SimpleUserInfo> mUserList = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search_index,null);
        ButterKnife.bind(this,view);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void initView(View view) {

        mSearchKey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                search();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mLvSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UIHelper.showHomePageActivity(getActivity(),mUserList.get(position).id);
            }
        });

        mLvSearch.setAdapter(mUserBaseInfoAdapter = new UserBaseInfoAdapter(mUserList));
        mLvSearch.setEmptyView(view.findViewById(R.id.iv_empty));
    }

    @Override
    public void initData() {

    }
    @OnClick({R.id.tv_search_btn})
    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.tv_search_btn:
                getActivity().finish();
                break;
            default:
                break;
        }
    }

    //搜索
    private void search() {


        String screenKey = mSearchKey.getText().toString().trim();
        if(TextUtils.isEmpty(screenKey)){
            return;
        }
        //showWaitDialog();

        PhoneLiveApi.requestSearchUser(screenKey,new StringCallback() {
            @Override
            public void onError(Call call, Exception e,int id) {
                hideWaitDialog();
            }

            @Override
            public void onResponse(String response,int id) {
                //hideWaitDialog();
                String res = ApiUtils.checkIsSuccess2(response);

                if(null != res){
                    mUserList.clear();
                    mUserList.addAll(JSON.parseArray(res, SimpleUserInfo.class));
                    fillUI();
                }
            }
        }, AppContext.getInstance().getLoginUid());

    }

    private void fillUI() {
        mUserBaseInfoAdapter.notifyDataSetChanged();
    }

}
