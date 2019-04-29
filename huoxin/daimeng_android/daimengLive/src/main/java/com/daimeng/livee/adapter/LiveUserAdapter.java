package com.daimeng.livee.adapter;

import android.text.TextUtils;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimeng.livee.AppContext;
import com.daimeng.livee.base.ShowLiveActivityBase;
import com.daimeng.livee.bean.LiveBean;
import com.daimeng.livee.utils.LiveUtils;
import com.daimeng.livee.utils.SimpleUtils;
import com.daimeng.livee.utils.StringUtils;
import com.daimeng.livee.R;

import java.util.List;

//热门主播
public class LiveUserAdapter extends BaseQuickAdapter<LiveBean,BaseViewHolder> {

    public LiveUserAdapter(List<LiveBean> data) {
        super(R.layout.item_hot_user,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, LiveBean item) {

        //用于平滑加载图片
        SimpleUtils.loadImageForView(AppContext.getInstance(), (ImageView) helper.getView(R.id.iv_live_user_pic), LiveUtils.getHttpUrl(item.thumb),R.drawable.default_pic);
        helper.setText(R.id.tv_live_nick,item.user_nicename);
        helper.setText(R.id.tv_live_local,item.city);
        helper.setText(R.id.tv_hot_room_title,item.title);
        helper.setText(R.id.tv_live_usernum,item.nums + "人");
        if(!TextUtils.isEmpty(item.title)){
            helper.setVisible(R.id.tv_hot_room_title,true);
        }else{
            helper.setVisible(R.id.tv_hot_room_title,false);
        }

        helper.setVisible(R.id.item_tv_live_type,true);

        if(StringUtils.toInt(item.type) == ShowLiveActivityBase.LIVE_TYPE_PAY || StringUtils.toInt(item.type) == ShowLiveActivityBase.LIVE_TYPE_TIME){

            helper.setVisible(R.id.item_tv_live_info,true);
        }else{

            helper.setVisible(R.id.item_tv_live_info,false);
        }

        //房间类型
        switch (StringUtils.toInt(item.type)){

            case ShowLiveActivityBase.LIVE_TYPE_ORDINARY:

                helper.setText(R.id.item_tv_live_type,"普通房间");
                break;
            case ShowLiveActivityBase.LIVE_TYPE_PAY:

                helper.setText(R.id.item_tv_live_type,"按场付费");
                helper.setText(R.id.item_tv_live_info,item.type_val +  LiveUtils.getConfigBean(AppContext.getInstance()).name_coin + "/场");
                break;
            case ShowLiveActivityBase.LIVE_TYPE_TIME:

                helper.setText(R.id.item_tv_live_type,"计时付费");
                helper.setText(R.id.item_tv_live_info,item.type_val +  LiveUtils.getConfigBean(AppContext.getInstance()).name_coin + "/分钟");
                break;
            case ShowLiveActivityBase.LIVE_TYPE_PWD:

                helper.setText(R.id.item_tv_live_type,"密码房间");
                break;

            default:
                break;
        }

    }

}


