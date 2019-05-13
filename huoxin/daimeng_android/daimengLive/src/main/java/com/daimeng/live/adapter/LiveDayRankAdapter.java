package com.daimeng.live.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimeng.live.AppContext;
import com.daimeng.live.R;
import com.daimeng.live.bean.RankBean;
import com.daimeng.live.utils.SimpleUtils;

import java.util.List;

public class LiveDayRankAdapter extends BaseQuickAdapter<RankBean,BaseViewHolder>{

    public LiveDayRankAdapter(List<RankBean> data) {
        super(R.layout.item_live_rank,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, RankBean item) {
        SimpleUtils.loadImageForView(AppContext.getInstance(), (ImageView) helper.getView(R.id.iv_avatar),item.getAvatar(),0);
    }
}
