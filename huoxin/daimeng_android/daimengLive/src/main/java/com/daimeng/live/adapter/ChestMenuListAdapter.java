package com.daimeng.live.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimeng.live.R;
import com.daimeng.live.bean.ChestMenuBean;

import java.util.List;

/**
 * Created by weipeng on 2017/3/29.
 */

public class ChestMenuListAdapter extends BaseQuickAdapter<ChestMenuBean, BaseViewHolder> {

    public ChestMenuListAdapter(List<ChestMenuBean> data) {
        super(R.layout.item_chest_menu, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ChestMenuBean item) {

        helper.setText(R.id.tv_name, item.getMenu_name());
        helper.setImageResource(R.id.iv_menu_img, item.getIcon_res());

    }
}
