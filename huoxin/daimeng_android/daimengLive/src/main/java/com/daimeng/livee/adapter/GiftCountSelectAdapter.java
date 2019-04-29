package com.daimeng.livee.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimeng.livee.R;
import com.daimeng.livee.bean.GiftSelectCountBean;

import java.util.List;

public class GiftCountSelectAdapter extends BaseQuickAdapter<GiftSelectCountBean,BaseViewHolder>{
    public GiftCountSelectAdapter(List<GiftSelectCountBean> data) {
        super(R.layout.item_select_gift_count,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, GiftSelectCountBean item) {
        helper.setText(R.id.item_tv_name,item.getName());
    }
}
