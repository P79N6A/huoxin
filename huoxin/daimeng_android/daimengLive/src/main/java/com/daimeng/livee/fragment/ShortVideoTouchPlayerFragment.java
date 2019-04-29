package com.daimeng.livee.fragment;


import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.daimeng.livee.AppContext;
import com.daimeng.livee.R;
import com.daimeng.livee.adapter.ShortVideoAdapter;
import com.daimeng.livee.api.remote.ApiUtils;
import com.daimeng.livee.api.remote.PhoneLiveApi;
import com.daimeng.livee.base.BaseFragment;
import com.daimeng.livee.bean.ShortVideoBean;
import com.daimeng.livee.event.Event;
import com.daimeng.livee.utils.StringUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.castorflex.android.verticalviewpager.VerticalViewPager;
import okhttp3.Call;

/**
 *
 */
public class ShortVideoTouchPlayerFragment extends BaseFragment {

    public static final String VIDEO_LIST = "VIDEO_LIST";
    public static final String VIDEO_POS = "VIDEO_POS";
    public static final String VIDEO_LIST_PAGE = "VIDEO_LIST_PAGE";

    @BindView(R.id.vertical_view_page)
    VerticalViewPager mVerticalViewPage;

    private List<ShortVideoBean> mShortVideoList = new ArrayList<>();
    private ShortVideoAdapter dummyAdapter;

    public static int select_video_id = 0;
    public static int select_video_user_id = 0;
    private int videoPage = 1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_short_video_touch_player, container, false);

        ButterKnife.bind(this,view);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void initView(View view) {
        dummyAdapter = new ShortVideoAdapter(getChildFragmentManager(),mShortVideoList);
        mVerticalViewPage.setAdapter(dummyAdapter);
        mVerticalViewPage.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                select_video_id = StringUtils.toInt(mShortVideoList.get(position).getId());
                select_video_user_id = StringUtils.toInt(mShortVideoList.get(position).getUid());
                Event.OnTouchShortVideoPlayerPageChange event = new Event.OnTouchShortVideoPlayerPageChange();
                EventBus.getDefault().post(event);

                if(position == mShortVideoList.size() - 1){
                    requestData(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void initData() {

        mShortVideoList.addAll(getArguments().<ShortVideoBean>getParcelableArrayList(VIDEO_LIST));
        int selectPos = getArguments().getInt(VIDEO_POS,0);
        videoPage = getArguments().getInt(VIDEO_LIST_PAGE,1);
        //默认第一个选中播放
        if(mShortVideoList.size() > 0){
            select_video_id = StringUtils.toInt(mShortVideoList.get(selectPos).getId());
            select_video_user_id = StringUtils.toInt(mShortVideoList.get(selectPos).getUid());
        }
        mVerticalViewPage.setCurrentItem(selectPos);
        mVerticalViewPage.setOffscreenPageLimit(1);
        dummyAdapter.notifyDataSetChanged();
    }


    @Override
    public void requestData(boolean isCache) {

        videoPage ++;
        PhoneLiveApi.requestGetHotShortVideoList(AppContext.getInstance().getLoginUid(),AppContext.getInstance().getToken(),videoPage,new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {

                String res = ApiUtils.checkIsSuccess2(response);

                if(res != null){

                    mShortVideoList.addAll(JSON.parseArray(res,ShortVideoBean.class));
                    dummyAdapter.notifyDataSetChanged();
                }
            }
        });
    }


}
