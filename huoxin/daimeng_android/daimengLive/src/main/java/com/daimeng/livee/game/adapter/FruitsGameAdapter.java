package com.daimeng.livee.game.adapter;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimeng.livee.R;
import com.daimeng.livee.game.bean.FruitsGameBean;
import com.daimeng.livee.utils.SimpleUtils;

import java.util.List;

public class FruitsGameAdapter extends BaseQuickAdapter<FruitsGameBean, BaseViewHolder> {

    public FruitsGameAdapter(List<FruitsGameBean> data) {
        super(R.layout.item_game_fruits,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, FruitsGameBean item) {
        helper.setImageResource(R.id.item_iv_game_fruits,item.img);
        helper.setText(R.id.item_tv_game_coin, SimpleUtils.simplifyString(item.coin.equals("0") ? "" : item.coin));
    }
}