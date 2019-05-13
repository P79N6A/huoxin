package com.daimeng.live.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimeng.live.R;
import com.daimeng.live.bean.AgentProfitRecordBean;

import java.util.List;

/**
 * Created by weipeng on 2017/9/13.
 */

public class AgentProfitRecordAdapter extends BaseQuickAdapter<AgentProfitRecordBean,BaseViewHolder> {

    public AgentProfitRecordAdapter(List<AgentProfitRecordBean> data) {
        super(R.layout.item_agent_profit,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AgentProfitRecordBean item) {

        helper.setVisible(R.id.item_title,helper.getPosition() == 0);
        helper.setText(R.id.item_tv_uid,item.uid);
        helper.setText(R.id.item_tv_profit,item.profit);
        helper.setText(R.id.item_tv_addtime,item.addtime);

    }
}
