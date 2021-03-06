package com.daimeng.live.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimeng.live.AppContext;
import com.daimeng.live.R;
import com.daimeng.live.bean.AdverBean;
import com.daimeng.live.utils.SimpleUtils;

import java.util.List;

/**
 * Created by 魏鹏 on 2018/4/30.
 * 郑州秀星网络科技有限公司
 */

public class AdverAdapter extends BaseQuickAdapter<AdverBean,BaseViewHolder> {
    public AdverAdapter(List<AdverBean> data) {
        super(R.layout.item_adver,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AdverBean item) {

        helper.setText(R.id.item_tv_name,item.getName());
        SimpleUtils.loadImageForView(AppContext.getInstance(), (ImageView) helper.getView(R.id.item_iv_thumb),item.getThumb(),0);
    }
}
