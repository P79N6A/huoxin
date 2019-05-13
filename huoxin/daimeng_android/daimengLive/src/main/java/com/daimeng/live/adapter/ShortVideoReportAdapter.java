package com.daimeng.live.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimeng.live.R;
import com.daimeng.live.bean.ShortVideoReportBean;

import java.util.List;

/**
 * Created by weipeng on 2017/10/21.
 */

public class ShortVideoReportAdapter extends BaseQuickAdapter<ShortVideoReportBean,BaseViewHolder> {

    public ShortVideoReportAdapter(List<ShortVideoReportBean> data) {
        super(R.layout.item_short_video_report,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ShortVideoReportBean item) {

        helper.setText(R.id.item_tv_report_name,item.getName());

        if(item.getSelect() == 1){
            helper.setImageResource(R.id.item_iv_select_state,R.drawable.ic_radio_select);
        }else{
            helper.setImageResource(R.id.item_iv_select_state,R.drawable.ic_radio_not_select);
        }
    }
}
