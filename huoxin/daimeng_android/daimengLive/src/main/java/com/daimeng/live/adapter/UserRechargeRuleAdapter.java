package com.daimeng.live.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimeng.live.AppContext;
import com.daimeng.live.R;
import com.daimeng.live.bean.RechargeBean;
import com.daimeng.live.utils.LiveUtils;

import java.util.List;

public class UserRechargeRuleAdapter extends BaseQuickAdapter<RechargeBean,BaseViewHolder>{

    public UserRechargeRuleAdapter(List<RechargeBean> data) {
        super(R.layout.item_user_recharge_rule,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, RechargeBean item) {

        helper.setText(R.id.tv_coin,item.coin);
        helper.setText(R.id.tv_coin_name, LiveUtils.getConfigBean(AppContext.getInstance()).name_coin);
        helper.setText(R.id.tv_money,item.money + "元");
    }
}
