package com.daimeng.live.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimeng.live.AppContext;
import com.daimeng.live.R;
import com.daimeng.live.bean.ProtectBean;
import com.daimeng.live.utils.SimpleUtils;

import java.util.List;

public class ProtectListAdapter extends BaseQuickAdapter<ProtectBean, BaseViewHolder> {
    public ProtectListAdapter(List<ProtectBean> data) {
        super(R.layout.item_select_protect, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ProtectBean item) {

        helper.setText(R.id.tv_show_protect_name, item.getName());
        SimpleUtils.loadImageForView(AppContext.context(), (ImageView) helper.getView(R.id.iv_show_protect_img), item.getIcon(), 0);


    }
}
