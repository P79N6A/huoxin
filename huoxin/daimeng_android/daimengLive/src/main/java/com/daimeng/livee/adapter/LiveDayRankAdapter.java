package com.daimeng.livee.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimeng.livee.AppContext;
import com.daimeng.livee.R;
import com.daimeng.livee.bean.RankBean;
import com.daimeng.livee.utils.SimpleUtils;

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
