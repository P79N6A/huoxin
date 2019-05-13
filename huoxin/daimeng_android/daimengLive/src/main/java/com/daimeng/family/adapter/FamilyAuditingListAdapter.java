package com.daimeng.family.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimeng.family.event.FamilyAuditingEvent;
import com.daimeng.live.AppContext;
import com.daimeng.live.R;
import com.daimeng.live.bean.SimpleUserInfo;
import com.daimeng.live.utils.SimpleUtils;
import com.daimeng.live.widget.CircleImageView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by weipeng on 2017/7/29.
 */

public class FamilyAuditingListAdapter extends BaseQuickAdapter<SimpleUserInfo,BaseViewHolder> {
    public FamilyAuditingListAdapter(List<SimpleUserInfo> data) {
        super(R.layout.item_family_auditing,data);
    }

    @Override
    protected void convert(final BaseViewHolder helper, SimpleUserInfo item) {

        helper.setText(R.id.tv_name,item.user_nicename);
        SimpleUtils.loadImageForView(AppContext.getInstance(), (CircleImageView) helper.getView(R.id.iv_avatar),item.avatar,0);

        helper.setOnClickListener(R.id.btn_agree, new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FamilyAuditingEvent event = new FamilyAuditingEvent();
                event.action = 1;
                event.pos = helper.getPosition();
                EventBus.getDefault().post(event);
            }
        });
        helper.setOnClickListener(R.id.btn_refuse, new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FamilyAuditingEvent event = new FamilyAuditingEvent();
                event.action = 2;
                event.pos = helper.getPosition();
                EventBus.getDefault().post(event);
            }
        });

    }
}
