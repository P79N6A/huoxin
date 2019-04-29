package com.daimeng.livee.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimeng.livee.AppContext;
import com.daimeng.livee.R;
import com.daimeng.livee.bean.LiveBean;
import com.daimeng.livee.utils.SimpleUtils;

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
