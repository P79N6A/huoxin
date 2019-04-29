package com.daimeng.livee.adapter;

import android.view.ViewGroup;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimeng.livee.AppContext;
import com.daimeng.livee.R;
import com.daimeng.livee.bean.LiveBean;
import com.daimeng.livee.utils.SimpleUtils;
import com.daimeng.livee.utils.TDevice;

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
