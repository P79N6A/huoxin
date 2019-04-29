package com.daimeng.livee.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.daimeng.livee.Constant;
import com.daimeng.livee.AppContext;
import com.daimeng.livee.R;
import com.daimeng.livee.bean.ChatBean;
import com.daimeng.livee.utils.LiveUtils;
import com.daimeng.livee.utils.SimpleUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 直播间聊天
 */
public class ChatListAdapter extends BaseAdapter {
    private List<ChatBean> mChats = new ArrayList<>();


    public ChatListAdapter(Context mContext) {

    }

    public void setChats(List<ChatBean> chats) {
        this.mChats = chats;
    }

    @Override
    public int getCount() {
        return mChats.size();
    }

    @Override
    public Object getItem(int position) {
        return mChats.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(AppContext.getInstance(), R.layout.item_live_chat, null);
            viewHolder = new ViewHolder();
            viewHolder.mChat1 = (TextView) convertView.findViewById(R.id.tv_chat_1);
            viewHolder.mChat2 = (TextView) convertView.findViewById(R.id.tv_chat_2);
            viewHolder.item_level = (ImageView) convertView.findViewById(R.id.item_level);
            viewHolder.item_custom_level = (ImageView) convertView.findViewById(R.id.item_custom_level);
            viewHolder.guard_layout = (LinearLayout) convertView.findViewById(R.id.guard_layout);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ChatBean c = mChats.get(position);

        switch (c.getType()) {
            case Constant.IM_TEXT_TYPE.TEXT_MSG: {
                viewHolder.item_level.setVisibility(View.VISIBLE);
                viewHolder.item_custom_level.setVisibility(View.VISIBLE);
                String name;
                if (null != c.getSimpleUserInfo() && null != c.getSimpleUserInfo().sex) {
                    if (c.getSimpleUserInfo().sex.equals("2")) {
                        name = "<font color='#F72679'>" + c.getUserNick() + "</font>";
                    } else {
                        name = "<font color='#3D83CB'>" + c.getUserNick() + "</font>";
                    }
                } else {
                    name = "<font color='#ccffcc'>" + c.getUserNick() + "</font>";
                }
                String content = "<font color='#ffffff'>" + c.getSendChatMsg() + "</font>";
                viewHolder.mChat1.setText(Html.fromHtml(name + ":" + content));
            }
            break;

            case Constant.IM_TEXT_TYPE.GIFT_MSG: {

                viewHolder.item_level.setVisibility(View.VISIBLE);
                viewHolder.item_custom_level.setVisibility(View.VISIBLE);

                String name = "<font color='#ffb901'>" + c.getUserNick() + "</font>";
                String content = "<font color='#ffffff'>" + c.getSendChatMsg() + "</font>";
                viewHolder.mChat1.setText(Html.fromHtml(content));
            }
            break;
            case Constant.IM_TEXT_TYPE.SYSTEM_MSG: {
                viewHolder.item_level.setVisibility(View.GONE);
                viewHolder.item_custom_level.setVisibility(View.GONE);

                String name = "<font color='#8772EA'>" + c.getUserNick() + ":" + "</font>";
                String content = "<font color='#8772EA'>" + c.getSendChatMsg() + "</font>";
                viewHolder.mChat1.setText(Html.fromHtml(name + content));
            }
            break;
            case Constant.IM_TEXT_TYPE.SYSTEM_GAME_GIFT_MSG: {

                viewHolder.item_level.setVisibility(View.GONE);
                viewHolder.item_custom_level.setVisibility(View.GONE);
                viewHolder.guard_layout.setVisibility(View.GONE);
            final     String msg = "系统消息："+c.getContent()+" ";
              final   String gift_icon = c.getSendChatMsg();
//               String content = msg + gift_icon;
             //   viewHolder.mChat1.setText( c.getContent());
//                SpannableString spannableString = new SpannableString(content);
//                ForegroundColorSpan foregroundColorSpanName1 = new ForegroundColorSpan(Color.parseColor("#00B2EE"));
//                        String [] msgarr =   msg.split(" ");
//                        spannableString.setSpan(foregroundColorSpanName1, 3, 3+msgarr[1].length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);//设置字体颜色
//
//                UrlImageSpan imageSpan = new UrlImageSpan(AppContext.getInstance(), gift_icon);
//                spannableString.setSpan(imageSpan, content.length() - gift_icon.length(), content.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
//                viewHolder.mChat1.setText(content);


                Glide.with(AppContext.getInstance())
                        .load(c.getSendChatMsg())
                        .asBitmap()
                        .override(50, 50)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                       //        Drawable drawable = new BitmapDrawable(bitmap);
//                                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
//                                viewHolder.mChat1.setCompoundDrawables(null, null, drawable, null);
                                ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#8772EA"));
                                SpannableString spannableString = new SpannableString(msg);
                                Drawable drawable = new BitmapDrawable(bitmap);
                                drawable.setBounds(0, 0, 35, 35);
                                ImageSpan imageSpan = new ImageSpan(drawable,
                                        ImageSpan.ALIGN_BOTTOM);
                                spannableString.setSpan(colorSpan,0,msg.length()-1,Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                                spannableString.setSpan(imageSpan, msg.length()-1, msg
                                        .length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                                viewHolder.mChat1.getText(editText.getSelectionStart(),
//                                        spannableString);
//                                ImageSpan imgSpan = new ImageSpan(drawable);
//                                SpannableString spannableString = new SpannableString(msg);
//                                spannableString.setSpan(imgSpan, 1, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                  viewHolder.mChat1.setText(spannableString);

                            }

                            @Override
                            public void onLoadFailed(Exception e, Drawable errorDrawable) {

                            }
                        });



            }
            break;
        }

        if (c.getSimpleUserInfo() != null) {

            if (null != c.getSimpleUserInfo().guardicon && !c.getSimpleUserInfo().guardicon.equals("")) {
                viewHolder.guard_layout.setVisibility(View.VISIBLE);
                viewHolder.guard_layout.removeAllViews();
                String[] img = c.getSimpleUserInfo().guardicon.split(",");
                for (int i = 0; i < img.length; i++) {
                    if (!img[i].equals("")) {
                        ImageView imageView = new ImageView(convertView.getContext());
                        ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(25, 25);
                        imageView.setLayoutParams(params);
                        SimpleUtils.loadImageForView(AppContext.getInstance(), imageView, img[i], 0);
                        viewHolder.guard_layout.addView(imageView);
                    }
                }
            } else {
                viewHolder.guard_layout.setVisibility(View.GONE);
            }

            SimpleUtils.loadImageForView(AppContext.getInstance(), viewHolder.item_custom_level, c.getSimpleUserInfo().custombackground, 0);
            viewHolder.item_level.setImageResource(LiveUtils.getLevelRes(c.getSimpleUserInfo().level));
        }

        if (c.getType() == Constant.IM_TEXT_TYPE.SYSTEM_MSG) {
            viewHolder.guard_layout.setVisibility(View.GONE);
        }

        //关闭等级显示
        viewHolder.item_level.setVisibility(View.GONE);
        //viewHolder.mChat2.setText(c.getSendChatMsg());
        return convertView;
    }

    protected class ViewHolder {
        TextView mChat1, mChat2;
        ImageView item_level, item_custom_level;
        LinearLayout guard_layout;
    }









}
