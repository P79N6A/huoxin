package com.daimeng.family.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimeng.live.AppContext;
import com.daimeng.live.R;
import com.daimeng.live.bean.SimpleUserInfo;
import com.daimeng.live.utils.LiveUtils;
import com.daimeng.live.utils.SimpleUtils;

import java.util.List;

public class FamilyUserListAdapter extends BaseQuickAdapter<SimpleUserInfo,BaseViewHolder> {

    public FamilyUserListAdapter(List<SimpleUserInfo> data) {
        super(R.layout.item_family_user,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SimpleUserInfo item) {

        helper.setText(R.id.tv_name,item.user_nicename);
        SimpleUtils.loadImageForView(AppContext.getInstance(), (ImageView) helper.getView(R.id.iv_avatar),item.avatar,0);
        helper.setImageResource(R.id.iv_sex, LiveUtils.getSexRes(item.sex));
    }
}
