package com.daimeng.livee.adapter;

import android.widget.ImageView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimeng.livee.AppContext;
import com.daimeng.livee.bean.SimpleUserInfo;
import com.daimeng.livee.R;
import com.daimeng.livee.utils.LiveUtils;
import com.daimeng.livee.utils.SimpleUtils;
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
