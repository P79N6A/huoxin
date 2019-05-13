package com.daimeng.live.adapter;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimeng.live.R;
import com.daimeng.live.bean.LiveRecordBean;

import java.util.List;

public class LiveRecordAdapter extends BaseQuickAdapter<LiveRecordBean,BaseViewHolder> {

    public LiveRecordAdapter(List<LiveRecordBean> data) {
        super(R.layout.item_live_record,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, LiveRecordBean item) {
        helper.setText(R.id.tv_item_live_record_num,item.getNums());
        helper.setText(R.id.tv_item_live_record_time,item.getLive_time());
        helper.setText(R.id.tv_item_live_record_title, TextUtils.isEmpty(item.getTitle())?"无标题":item.getTitle());
    }

}