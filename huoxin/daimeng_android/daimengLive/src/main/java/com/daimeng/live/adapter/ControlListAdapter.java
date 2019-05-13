package com.daimeng.live.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.daimeng.live.AppContext;
import com.daimeng.live.R;
import com.daimeng.live.bean.ManageListBean;
import com.daimeng.live.other.DrawableRes;
import com.daimeng.live.utils.SimpleUtils;
import com.daimeng.live.utils.UIHelper;
import com.daimeng.live.widget.BlackTextView;
import com.daimeng.live.widget.CircleImageView;

import java.util.List;


public class ControlListAdapter extends BaseAdapter {
    private Activity mActivity;
    private List<ManageListBean> users;

    public ControlListAdapter(Activity activity,List<ManageListBean> users) {
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
        public ImageView mUSex, mULevel;
        public BlackTextView mUNice, mUSign;
        public RelativeLayout rl_item;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(AppContext.getInstance(), R.layout.item_control, null);
            viewHolder = new ViewHolder();
            viewHolder.rl_item = (RelativeLayout) convertView.findViewById(R.id.rl_item);
            viewHolder.mUHead = (CircleImageView) convertView.findViewById(R.id.control_userHead);
            viewHolder.mUSex = (ImageView) convertView.findViewById(R.id.control_item_usex);
            viewHolder.mULevel = (ImageView) convertView.findViewById(R.id.control_item_ulevel);
            viewHolder.mUNice = (BlackTextView) convertView.findViewById(R.id.control_item_uname);
            viewHolder.mUSign = (BlackTextView) convertView.findViewById(R.id.control_item_usign);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final ManageListBean u = users.get(position);
        SimpleUtils.loadImageForView(AppContext.getInstance(), viewHolder.mUHead, u.getAvatar(), 0);
        viewHolder.mUSex.setImageResource(u.getSex() == 1 ? R.drawable.global_male : R.drawable.global_female);

        viewHolder.mULevel.setImageResource(DrawableRes.LevelImg[u.getLevel() == 0 ? 0 : u.getLevel() - 1]);
        viewHolder.mUNice.setText(u.getUser_nicename());
        viewHolder.mUSign.setText(u.getSignature());
        viewHolder.rl_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIHelper.showHomePageActivity(mActivity, String.valueOf(u.getId()));
            }
        });
        return convertView;
    }
}
