package com.daimeng.livee.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimeng.livee.AppContext;
import com.daimeng.livee.R;
import com.daimeng.livee.bean.ProtectUserListBean;
import com.daimeng.livee.utils.SimpleUtils;
import com.daimeng.livee.utils.UIHelper;
import com.daimeng.livee.widget.BlackTextView;
import com.daimeng.livee.widget.CircleImageView;

import java.util.List;


public class ProtectUserlListAdapter extends BaseAdapter {
    private Activity mActivity;
    private List<ProtectUserListBean> users;

    public ProtectUserlListAdapter(Activity activity, List<ProtectUserListBean> users) {
        this.users = users;
        this.mActivity = activity;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        public CircleImageView mUHead;
        public ImageView mUSex, iv_protect_user_icon, control_item_custombackground;
        public BlackTextView mUNice, mUSign;
        public TextView item_tv_protect_name;
        public RelativeLayout rl_item;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(AppContext.getInstance(), R.layout.item_protect_user_list, null);
            viewHolder = new ViewHolder();
            viewHolder.rl_item = (RelativeLayout) convertView.findViewById(R.id.rl_item);
            viewHolder.mUHead = (CircleImageView) convertView.findViewById(R.id.control_userHead);
            viewHolder.mUSex = (ImageView) convertView.findViewById(R.id.control_item_usex);
//            viewHolder.mULevel = (ImageView) convertView.findViewById(R.id.control_item_ulevel);
            viewHolder.mUNice = (BlackTextView) convertView.findViewById(R.id.control_item_uname);
            viewHolder.mUSign = (BlackTextView) convertView.findViewById(R.id.control_item_usign);
            viewHolder.iv_protect_user_icon = (ImageView) convertView.findViewById(R.id.item_iv_protect_icon);
            viewHolder.item_tv_protect_name = (TextView) convertView.findViewById(R.id.item_tv_protect_name);
            viewHolder.control_item_custombackground = convertView.findViewById(R.id.control_item_custombackground);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final ProtectUserListBean u = users.get(position);
        SimpleUtils.loadImageForView(AppContext.getInstance(), viewHolder.mUHead, u.getAvatar(), 0);
        viewHolder.mUSex.setImageResource(u.getSex() == 1 ? R.drawable.global_male : R.drawable.global_female);
//        viewHolder.mULevel.setImageResource(DrawableRes.LevelImg[u.getLevel() == 0 ? 0 : u.getLevel() - 1]);
        viewHolder.mUNice.setText(u.getUser_nicename());
        viewHolder.mUSign.setText(u.getSignature());
        viewHolder.item_tv_protect_name.setText(u.getName());
        SimpleUtils.loadImageForView(AppContext.getInstance(), viewHolder.iv_protect_user_icon, u.getIcon(), 0);
        SimpleUtils.loadImageForView(AppContext.getInstance(), viewHolder.control_item_custombackground, u.getCustombackground(), 0);
        viewHolder.rl_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIHelper.showHomePageActivity(mActivity, String.valueOf(u.getUid()));
            }
        });
        return convertView;
    }
}
