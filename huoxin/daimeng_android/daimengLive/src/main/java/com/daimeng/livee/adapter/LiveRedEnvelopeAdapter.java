package com.daimeng.livee.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimeng.livee.AppContext;
import com.daimeng.livee.R;
import com.daimeng.livee.bean.RedListBean;
import com.daimeng.livee.utils.SimpleUtils;
import com.daimeng.livee.widget.CircleImageView;
import com.pizidea.imagepicker.Util;

import java.util.List;

/**
 * @author 作者 E-mail:
 * @version 创建时间：2016-5-28 下午3:23:32 类说明
 */
public class LiveRedEnvelopeAdapter extends BaseQuickAdapter<RedListBean,BaseViewHolder> {

    public LiveRedEnvelopeAdapter(List<RedListBean> data) {
        super(R.layout.item_live_red_envelope, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, RedListBean item) {

        SimpleUtils.loadImageForView(AppContext.getInstance(), (CircleImageView) helper.getView(R.id.iv_head), item.getAvatar(), 0);

        TextView tv_nick_name = helper.getView(R.id.tv_nick_name);
        tv_nick_name.setMaxWidth(Util.dp2px(AppContext.getInstance(), 135));

        helper.setText(R.id.tv_nick_name, item.getUser_nicename());
        helper.setText(R.id.tv_diamonds, item.getCoin() + "");

        ImageView iv_rank = helper.getView(R.id.iv_rank);
        iv_rank.setVisibility(View.GONE);

     //   ImageView iv_global_male = helper.getView(R.id.iv_global_male);

     //   iv_global_male.setVisibility(View.VISIBLE);
//        if (item.getSex() == 2) {
//
//            iv_global_male.setImageResource(R.drawable.choice_sex_femal);
//        } else if (item.getSex() == 1) {
//            iv_global_male.setImageResource(R.drawable.choice_sex_male);
//        } else {
//            iv_global_male.setVisibility(View.GONE);
//        }

        ImageView ll_best = helper.getView(R.id.ll_best);
        if (helper.getLayoutPosition()  > 0) {
            // 隐藏手气最佳
            ll_best.setVisibility(View.GONE);
        } else {
            ll_best.setVisibility(View.VISIBLE);
        }

    }

}
