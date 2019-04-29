package com.daimeng.livee.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimeng.livee.AppContext;
import com.daimeng.livee.R;
import com.daimeng.livee.bean.ProtectBean;
import com.daimeng.livee.utils.SimpleUtils;

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
