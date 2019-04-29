package com.daimeng.livee.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimeng.livee.AppContext;
import com.daimeng.livee.R;
import com.daimeng.livee.bean.VipBean;
import com.daimeng.livee.utils.LiveUtils;
import com.daimeng.livee.utils.SimpleUtils;

import java.util.List;

/**
 * Created by weipeng on 2017/8/15.
 */

public class BuyVipAdapter extends BaseQuickAdapter<VipBean,BaseViewHolder> {

    public BuyVipAdapter(List<VipBean> data) {
        super(R.layout.item_buy_vip, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, VipBean item) {

        helper.setText(R.id.item_tv_price,item.coin +  LiveUtils.getConfigBean(AppContext.getInstance()).name_coin);
        helper.setText(R.id.item_tv_car_name,item.cname);
        helper.setText(R.id.item_tv_name,item.name);
        helper.setText(R.id.item_tv_time,item.time + "天");
        helper.setText(R.id.item_tv_level,"等级:" + item.level);
        SimpleUtils.loadGifImageForView(AppContext.getInstance(), (ImageView) helper.getView(R.id.item_iv_car), LiveUtils.getHttpUrl(item.cthumb),0);
    }
}
