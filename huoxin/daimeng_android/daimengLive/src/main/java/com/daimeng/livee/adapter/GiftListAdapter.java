package com.daimeng.livee.adapter;

import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimeng.livee.AppContext;
import com.daimeng.livee.R;
import com.daimeng.livee.bean.GiftBean;
import com.daimeng.livee.utils.SimpleUtils;

import java.util.List;

/**
 * Created by weipeng on 2017/3/29.
 */

public class GiftListAdapter extends BaseQuickAdapter<GiftBean, BaseViewHolder> {


    public GiftListAdapter(List<GiftBean> data) {
        super(R.layout.item_select_gift, data);
    }
    @Override
    protected void convert(BaseViewHolder helper, GiftBean item) {
        helper.setText(R.id.tv_show_gift_price,String.valueOf(item.getNeedcoin()));
        helper.setText(R.id.tv_show_gift_name,item.getGiftname());
        SimpleUtils.loadImageForView(AppContext.context(), (ImageView) helper.getView(R.id.iv_show_gift_img),item.getGifticon(),0);
        if(null!=item.getCount()&&!item.getCount().equals("0")){
            helper.getView(R.id.count_num).setVisibility(View.VISIBLE);
            helper.setText(R.id.count_num,item.getCount());
        }else {
            helper.getView(R.id.count_num).setVisibility(View.GONE);
        }

        if(item.getType() == 1){
            helper.getView(R.id.iv_show_gift_selected).setBackgroundResource(R.drawable.icon_continue_gift);
        }
    }
}
