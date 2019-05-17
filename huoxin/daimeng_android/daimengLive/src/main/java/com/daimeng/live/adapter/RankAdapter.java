package com.daimeng.live.adapter;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimeng.live.AppContext;
import com.daimeng.live.R;
import com.daimeng.live.bean.ConfigBean;
import com.daimeng.live.bean.RankBean;
import com.daimeng.live.bean.SimpleUserInfo;
import com.daimeng.live.utils.LiveUtils;
import com.daimeng.live.utils.SimpleUtils;

import java.util.List;

/**
 * Created by 魏鹏 on 2018/4/16.
 * 郑州秀星网络科技有限公司
 */

public class RankAdapter extends BaseQuickAdapter<RankBean,BaseViewHolder> {

    private List<SimpleUserInfo> userInfos;
    private int type;
    private ConfigBean mConfigBean;
    public RankAdapter(Context context,int type,List<RankBean> data) {
        super(R.layout.item_order_user,data);
        this.type = type;
        mConfigBean = LiveUtils.getConfigBean(context);
    }
    @Override
    protected void convert(BaseViewHolder helper, RankBean item) {
//        SimpleUtils.loadImageForView(AppContext.getInstance(), item_custom, c.getSimpleUserInfo().custombackground, 0);
//        item_custom.setImageResource(LiveUtils.getLevelRes(c.getSimpleUserInfo().level));

        SimpleUtils.loadImageForView(AppContext.getInstance(), (ImageView) helper.getView(R.id.ci_order_item_u_head),
                item.getAvatar(),R.drawable.default_img);
//        SimpleUtils.loadImageForView(AppContext.getInstance(), (ImageView) helper.getView(R.id.tv_order_item_u_level),
//                item.getAvatar(),R.drawable.default_img);
        helper.setImageResource(R.id.tv_order_item_u_level,LiveUtils.getLevelRes(item.getConsumption()));
        helper.setImageResource(R.id.iv_order_item_u_sex,LiveUtils.getSexRes(item.getSex()));
        Log.e("item.getAvatar_thumb = ","------RankBean." +item.getAvatar_thumb());
        Log.e("item.getAvatar = ","------RankBean." +item.getAvatar());
        Log.e("item.getBirthday = ","------RankBean." +item.getBirthday());
        Log.e("item.getConsumption = ","------RankBean." +item.getConsumption());
        Log.e("item.getLevel = ","------RankBean." +item.getLevel());
        Log.e("item.getVip_level = ","------RankBean." +item.getVip_level());

        helper.setText(R.id.tv_order_item_u_name,item.getUser_nicename());
        if(type == 0){
            helper.setText(R.id.tv_order_item_u_gx,  "获得" + item.getTotal() + mConfigBean.name_votes);
        }else{
            helper.setText(R.id.tv_order_item_u_gx,"消费" + item.getTotal() + mConfigBean.name_coin);
        }
        helper.setText(R.id.tv_order_item_u_no,"  No." + (helper.getPosition() + 1));
    }
}
