package com.daimeng.live.adapter;

import android.widget.ImageView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimeng.live.AppContext;
import com.daimeng.live.bean.SimpleUserInfo;
import com.daimeng.live.R;
import com.daimeng.live.utils.LiveUtils;
import com.daimeng.live.utils.SimpleUtils;
import java.util.List;

/**
 * 用户列表adapter
 */
public class UserListAdapter extends BaseQuickAdapter<SimpleUserInfo, BaseViewHolder> {

    public UserListAdapter(List<SimpleUserInfo> data) {
        super(R.layout.item_live_user_list,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SimpleUserInfo item) {

        SimpleUtils.loadImageForView(AppContext.getInstance(),(ImageView) helper.getView(R.id.av_userHead), LiveUtils.getHttpUrl(item.avatar),R.mipmap.ic_launcher);
    }

}
