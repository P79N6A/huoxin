package com.daimeng.livee.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimeng.livee.R;
import com.daimeng.livee.bean.NewsMessageBean;
import com.daimeng.livee.utils.SimpleUtils;
import com.daimeng.livee.utils.UIHelper;

import java.util.List;

/**
 * Created by weipeng on 2017/11/7.
 */

public class NewsMessageAdapter extends BaseQuickAdapter<NewsMessageBean,BaseViewHolder> {

    private Context mContext;

    public NewsMessageAdapter(Context context,List<NewsMessageBean> data) {
        super(R.layout.item_news_message,data);
        mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, final NewsMessageBean item) {

        SimpleUtils.loadImageForView(mContext, (ImageView) helper.getView(R.id.item_iv_avatar),item.getAvatar(),0);
        SimpleUtils.loadImageForView(mContext, (ImageView) helper.getView(R.id.item_iv_cover),item.getCover_url(),0);
        helper.setText(R.id.item_tv_body,item.getBody());
        helper.setText(R.id.item_iv_time,item.getAddtime());
        helper.getView(R.id.item_iv_avatar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIHelper.showHomePageActivity(mContext,item.getUid());
            }
        });


    }
}
