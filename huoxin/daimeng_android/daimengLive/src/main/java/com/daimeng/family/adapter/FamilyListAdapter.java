package com.daimeng.family.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimeng.family.bean.FamilyBean;
import com.daimeng.family.event.FamilyEvent;
import com.daimeng.livee.AppContext;
import com.daimeng.livee.R;
import com.daimeng.livee.utils.SimpleUtils;
import com.daimeng.livee.utils.StringUtils;
import com.daimeng.livee.widget.CircleImageView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by weipeng on 2017/7/29.
 */

public class FamilyListAdapter extends BaseQuickAdapter<FamilyBean,BaseViewHolder> {
    public FamilyListAdapter(List<FamilyBean> data) {
        super(R.layout.item_family,data);
    }

    @Override
    protected void convert(final BaseViewHolder helper, FamilyBean item) {

        helper.setText(R.id.tv_name,item.name);
        SimpleUtils.loadImageForView(AppContext.getInstance(), (CircleImageView) helper.getView(R.id.iv_avatar),item.avatar,0);

        helper.setText(R.id.tv_detail,"家族长:" + item.user_nicename + "   人数:" + item.emcee_num);
        final int join_state = StringUtils.toInt(item.join_state);
        if(join_state == 1){

            helper.setText(R.id.btn_join,"审核中");
        }else if(join_state == 0){

            helper.setText(R.id.btn_join,"加入家族");
        }
        helper.setOnClickListener(R.id.btn_join, new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(join_state == 0){

                    FamilyEvent familyEvent = new FamilyEvent();
                    familyEvent.action = 1;
                    familyEvent.pos = helper.getPosition();
                    EventBus.getDefault().post(familyEvent);
                }
            }
        });
    }
}
