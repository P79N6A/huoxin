package com.daimeng.live.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimeng.live.AppContext;
import com.daimeng.live.R;
import com.daimeng.live.bean.RechargeBean;
import com.daimeng.live.utils.LiveUtils;
import com.daimeng.live.utils.StringUtils;

import java.util.List;
import java.util.Locale;

/**
 * Created by weipeng on 2017/3/26.
 */

public class DiamondsAdapter extends BaseQuickAdapter<RechargeBean,BaseViewHolder> {

    public DiamondsAdapter(List<RechargeBean> data) {

        super(R.layout.item_select_num,data);
    }

    @Override
        protected void convert(BaseViewHolder helper, RechargeBean rechargeBean) {

            helper.setText(R.id.tv_diamondsnum,rechargeBean.name);
            if(StringUtils.toInt(rechargeBean.give) > 0){
                helper.setVisible(R.id.tv_diamonds_zeng,true);
                helper.setText(R.id.tv_diamonds_zeng,String.format(Locale.CHINA,"赠送%s",rechargeBean.give +  LiveUtils.getConfigBean(AppContext.getInstance()).name_coin));
            }else{
                helper.setVisible(R.id.tv_diamonds_zeng,false);
        }

        helper.setText(R.id.tv_price_explain,rechargeBean.money);
    }
}
