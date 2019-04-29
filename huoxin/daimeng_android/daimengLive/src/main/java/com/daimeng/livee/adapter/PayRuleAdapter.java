package com.daimeng.livee.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimeng.livee.AppContext;
import com.daimeng.livee.R;
import com.daimeng.livee.bean.PayMenuBean;
import com.daimeng.livee.utils.LiveUtils;
import com.daimeng.livee.utils.SimpleUtils;

import java.util.List;

public class PayRuleAdapter extends BaseQuickAdapter<PayMenuBean,BaseViewHolder>{

    private int payPos = -1;

    public PayRuleAdapter(List<PayMenuBean> data) {
        super(R.layout.item_pay_rule,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, PayMenuBean item) {

        helper.setText(R.id.item_tv_pay_name,item.getName());
        SimpleUtils.loadImageForView(AppContext.getInstance(), (ImageView) helper.getView(R.id.item_tv_pay_icon),LiveUtils.getHttpUrl(item.getIcon()),0);

        helper.setChecked(R.id.item_cb_pay,helper.getPosition() == payPos);
    }

    public void setPayPos(int payPos) {
        this.payPos = payPos;
        notifyDataSetChanged();
    }

    public int getPayPos() {
        return payPos;
    }

    public PayMenuBean getRule(){
        return getItem(payPos);
    }
}
