package com.daimeng.live.ui;

import android.support.v4.view.ViewPager;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.daimeng.live.AppContext;
import com.daimeng.live.R;
import com.daimeng.live.adapter.ShortVideoAdapter;
import com.daimeng.live.api.remote.ApiUtils;
import com.daimeng.live.api.remote.PhoneLiveApi;
import com.daimeng.live.base.BaseTitleActivity;
import com.daimeng.live.bean.ShortVideoBean;
import com.daimeng.live.event.Event;
import com.daimeng.live.utils.StringUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import fr.castorflex.android.verticalviewpager.VerticalViewPager;
import okhttp3.Call;

public class ShortVideoPlayerTouchActivity extends BaseTitleActivity  {

    public static final String VIDEO_LIST = "VIDEO_LIST";
    public static final String VIDEO_POS = "VIDEO_POS";
    public static final String VIDEO_LIST_PAGE = "VIDEO_LIST_PAGE";

    @BindView(R.id.vertical_view_page)
    VerticalViewPager mVerticalViewPage;

    private List<ShortVideoBean> mShortVideoList = new ArrayList<>();
    private ShortVideoAdapter dummyAdapter;

    public static int select_video_id = 0;
    private int videoPage = 1;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_short_video_player_touch;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void initView() {

        dummyAdapter = new ShortVideoAdapter(getSupportFragmentManager(),mShortVideoList);
        mVerticalViewPage.setAdapter(dummyAdapter);
        mVerticalViewPage.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                select_video_id = StringUtils.toInt(mShortVideoList.get(position).getId());
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
        //mVerticalViewPage.setPageMargin(getResources().getDimensionPixelSize(R.dimen.h5));
        //mVerticalViewPage.setPageMarginDrawable(new ColorDrawable(getResources().getColor(android.R.color.holo_green_dark)));
    }

    @Override
    public void initData() {

        mShortVideoList.addAll(getIntent().<ShortVideoBean>getParcelableArrayListExtra(VIDEO_LIST));
        int selectPos = getIntent().getIntExtra(VIDEO_POS,0);
        videoPage = getIntent().getIntExtra(VIDEO_LIST_PAGE,1);
        //默认第一个选中播放
        if(mShortVideoList.size() > 0){
            select_video_id = StringUtils.toInt(mShortVideoList.get(selectPos).getId());
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

    @Override
    protected boolean hasActionBar() {
        return false;
    }
}
