package com.daimeng.livee.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.daimeng.livee.AppContext;
import com.daimeng.livee.R;
import com.daimeng.livee.utils.SimpleUtils;
import com.daimeng.livee.utils.StringUtils;

/**
 * 未打开的红包view
 * Created by wangyf on 2018/10/17.
 */
public class LiveRedEnvelopeOpenView extends LinearLayout {
    private final Context context;
    private ImageView iv_envelope_open_head;
    private TextView tv_envelope_open_name;
    private TextView tv_info;
    private TextView tv_user_red_envelope;
    private ListView list_red;
    private View ll_open_close;
    private View ll_open_top;
    private RecyclerView red_list;

    public LiveRedEnvelopeOpenView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        setOrientation(VERTICAL);
        init();
    }

    public LiveRedEnvelopeOpenView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setOrientation(VERTICAL);
        init();
    }

    public LiveRedEnvelopeOpenView(Context context) {
        super(context);
        this.context = context;
        setOrientation(VERTICAL);
        init();
    }


    protected void init() {
        View view = View.inflate(context, R.layout.include_live_red_envelope_open, null);
        iv_envelope_open_head = view.findViewById(R.id.iv_envelope_open_head);
        tv_envelope_open_name = view.findViewById(R.id.tv_envelope_open_name);
        tv_info = view.findViewById(R.id.tv_info);
        tv_user_red_envelope = view.findViewById(R.id.tv_user_red_envelope);
        list_red = view.findViewById(R.id.list_red);
        ll_open_close = view.findViewById(R.id.ll_open_close);
        ll_open_top = view.findViewById(R.id.ll_open_top);
        red_list = view.findViewById(R.id.gv_red_list);

        addView(view);

    }

    /**
     * 设置视图的显示与隐藏
     */
    public void setViewShowOrHide() {
        tv_user_red_envelope.setVisibility(View.GONE);
        red_list.setVisibility(View.VISIBLE);
        ll_open_top.setVisibility(View.VISIBLE);
    }

    /**
     * 获取ListView
     */
    public RecyclerView getRed_list() {
        return red_list;
    }

    public ListView getListView() {
        return list_red;
    }

    /**
     * 头像
     */
    public void setIvEnvelopeOpenHead(String envelopeOpenHead) {
        if (StringUtils.isEmpty(envelopeOpenHead)) {
            iv_envelope_open_head.setImageResource(R.drawable.bg_head_image_loading);
            return;
        }

        SimpleUtils.loadImageForView(AppContext.getInstance(), iv_envelope_open_head, envelopeOpenHead, R.drawable.bg_head_image_loading);
    }

    /**
     * 名字
     */
    public void setTvEnvelopeOpenName(String envelopeOpenName) {
        tv_envelope_open_name.setText(envelopeOpenName);
    }

    /**
     * 信息
     */
    public void setTvInfo(String info) {
        if (!TextUtils.isEmpty(info)) {
            tv_info.setText(info);
        }
    }

    /**
     * 头像点击监听
     *
     * @param onHeadClickListener
     */
    public void setOnHeadClickListener(View.OnClickListener onHeadClickListener) {
        iv_envelope_open_head.setOnClickListener(onHeadClickListener);
    }

    /**
     * 查看手气点击监听
     *
     * @param onUserClickListener
     */
    public void setOnUserClickListener(View.OnClickListener onUserClickListener) {
        tv_user_red_envelope.setOnClickListener(onUserClickListener);
    }

    /**
     * 关闭点击监听
     *
     * @param closeClickListener
     */
    public void setCloseClickListener(View.OnClickListener closeClickListener) {
        ll_open_close.setOnClickListener(closeClickListener);
    }

}
