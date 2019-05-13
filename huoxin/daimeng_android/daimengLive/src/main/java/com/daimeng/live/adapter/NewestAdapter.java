package com.daimeng.live.adapter;

import android.view.ViewGroup;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimeng.live.AppContext;
import com.daimeng.live.R;
import com.daimeng.live.bean.LiveBean;
import com.daimeng.live.utils.SimpleUtils;
import com.daimeng.live.utils.TDevice;

import java.util.List;

/**
 * Created by daimeng on 2016/12/23.
 */

public class NewestAdapter extends BaseQuickAdapter<LiveBean, BaseViewHolder> {

    private int width = 0;
    public NewestAdapter(List<LiveBean> data) {
        super(R.layout.item_newest_user, data);
        width = (int) (TDevice.getScreenWidth()/2);
    }

    @Override
    protected void convert(BaseViewHolder helper, LiveBean item) {

        helper.getConvertView().setLayoutParams(new ViewGroup.LayoutParams(width,width));
        SimpleUtils.loadImageForView(AppContext.getInstance(), (ImageView) helper.getView(R.id.iv_newest_item_user),item.thumb,0);
        helper.setText(R.id.item_tv_name,item.user_nicename);
        helper.setText(R.id.item_tv_watch_count,item.nums + "人");
        helper.setText(R.id.item_tv_city,item.city);
        helper.setText(R.id.item_tv_distance,item.distance);
    }
}
