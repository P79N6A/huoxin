package com.daimeng.live.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.daimeng.live.AppContext;
import com.daimeng.live.R;
import com.daimeng.live.bean.RechargeBean;
import com.daimeng.live.utils.LiveUtils;
import com.daimeng.live.utils.StringUtils;
import com.daimeng.live.widget.BlackTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by daimeng on 2017/1/11.
 */

public class RechangeAdapter extends BaseAdapter {

    private List<RechargeBean> rechanList = new ArrayList<>();

    public RechangeAdapter(List<RechargeBean> rechanList) {
        this.rechanList = rechanList;
    }

    @Override
    public int getCount() {
        return rechanList.size();
    }

    @Override
    public Object getItem(int position) {
        return rechanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RechargeBean rechargeBean = rechanList.get(position);
        ViewHolder holder;
        if(convertView == null){
            convertView = View.inflate(AppContext.getInstance(),R.layout.item_select_num,null);
            holder = new ViewHolder();
            holder.mDiamondsNum = (BlackTextView) convertView.findViewById(R.id.tv_diamondsnum);
            holder.mPriceExplain = (BlackTextView) convertView.findViewById(R.id.tv_price_explain);
            holder.mPriceText = (BlackTextView) convertView.findViewById(R.id.tv_diamonds_zeng);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mDiamondsNum.setText(rechargeBean.coin +  LiveUtils.getConfigBean(AppContext.getInstance()).name_coin);
        if(StringUtils.toInt(rechargeBean.give) > 0){
            holder.mPriceExplain.setVisibility(View.VISIBLE);
            holder.mPriceExplain.setText(String.format(Locale.CHINA,"赠送%s",rechargeBean.give +  LiveUtils.getConfigBean(AppContext.getInstance()).name_coin));
        }else{
            holder.mPriceExplain.setVisibility(View.GONE);
        }

        holder.mPriceText.setText(rechargeBean.money);
        return convertView;
    }
    private class ViewHolder{
        BlackTextView mDiamondsNum,mPriceExplain;
        BlackTextView mPriceText;
    }
}