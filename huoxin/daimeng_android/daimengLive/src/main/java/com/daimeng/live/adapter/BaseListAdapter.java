package com.daimeng.live.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimeng.live.AppContext;
import com.daimeng.live.R;
import com.daimeng.live.bean.LiveBean;
import com.daimeng.live.utils.SimpleUtils;

import java.util.List;


public class BaseListAdapter extends BaseQuickAdapter<LiveBean,BaseViewHolder> {

    public BaseListAdapter(List<LiveBean> data) {
        super(R.layout.item_base_list,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, LiveBean item) {
        helper.setText(R.id.item_tv_name,item.user_nicename);

        //用于平滑加载图片
        SimpleUtils.loadImageForView(AppContext.getInstance(), (ImageView) helper.getView(R.id.iv_item_user),item.thumb,0);
    }
}
