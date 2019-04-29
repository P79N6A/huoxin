package com.daimeng.livee.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimeng.livee.R;
import com.daimeng.livee.bean.AgentUserBean;
import com.daimeng.livee.utils.LiveUtils;
import com.daimeng.livee.utils.SimpleUtils;

import java.util.List;

/**
 * Created by weipeng on 2017/9/13.
 */

public class AgentUserAdapter extends BaseQuickAdapter<AgentUserBean,BaseViewHolder> {

    private Context mContext;

    public AgentUserAdapter(Context context,List<AgentUserBean> data) {
        super(R.layout.item_agent_user,data);
        mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, AgentUserBean item) {

        helper.setText(R.id.item_tv_item_uname,item.user_nicename + "(" + item.id + ")");
        helper.setText(R.id.item_total_profit,"带来收益(票): " + item.total_profit);
        helper.setText(R.id.item_charge_money,"充值总额: " + item.charge_money);
        SimpleUtils.loadImageForView(mContext, (ImageView) helper.getView(R.id.item_iv_user_head),LiveUtils.getHttpUrl(item.avatar),0);
    }
}
