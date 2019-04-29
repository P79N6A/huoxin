package com.daimeng.livee.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimeng.livee.AppContext;
import com.daimeng.livee.R;
import com.daimeng.livee.bean.CloudVideoBean;
import com.daimeng.livee.utils.SimpleUtils;
import com.daimeng.livee.utils.StringUtils;

import java.util.List;

/**
 * Created by 魏鹏 on 2018/4/30.
 * 郑州秀星网络科技有限公司
 */

public class CloudVideoAdapter extends BaseQuickAdapter<CloudVideoBean,BaseViewHolder> {
    public CloudVideoAdapter(List<CloudVideoBean> data) {
        super(R.layout.item_cloud_video,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, CloudVideoBean item) {

        SimpleUtils.loadImageForView(AppContext.getInstance(), (ImageView) helper.getView(R.id.item_iv_cover),item.getCover_url(),0);
        helper.setText(R.id.item_tv_title,item.getTitle());

        switch (StringUtils.toInt(item.getPay_type())){
            case 0:
                helper.setText(R.id.item_tv_detail,"免费");
                break;
            case 1:

                helper.setText(R.id.item_tv_detail,"按场付费/" + item.getFormatMoney());
                break;
            case 2:

                helper.setText(R.id.item_tv_detail,"按时付费/" + item.getFormatMoney());
                break;
            case 3:

                helper.setText(R.id.item_tv_detail,"VIP视频");
                break;
            default:
                break;
        }
    }
}
