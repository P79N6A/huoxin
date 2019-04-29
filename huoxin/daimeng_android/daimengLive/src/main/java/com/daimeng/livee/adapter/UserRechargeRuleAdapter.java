package com.daimeng.livee.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimeng.livee.AppContext;
import com.daimeng.livee.R;
import com.daimeng.livee.bean.RechargeBean;
import com.daimeng.livee.utils.LiveUtils;

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
