package com.daimeng.live.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimeng.live.R;
import com.daimeng.live.bean.GiftSelectCountBean;

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
