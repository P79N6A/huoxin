package com.daimeng.livee.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimeng.livee.R;
import com.daimeng.livee.utils.SimpleUtils;

/**
 * 未打开的红包view
 * Created by wangyf on 2018/10/17.
 */
public class LiveRedEnvelopeView extends LinearLayout {

    private final Context context;
    private ImageView iv_envelope_head;
    private TextView tv_envelope_name;
    private ImageView iv_open_redpacket;
    private View ll_close;

    public LiveRedEnvelopeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init();
    }

    public LiveRedEnvelopeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();

    }

    public LiveRedEnvelopeView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    protected void init() {
        View view = View.inflate(context, R.layout.include_live_red_envelope, null);
        iv_envelope_head = view.findViewById(R.id.iv_envelope_head);
        tv_envelope_name = view.findViewById(R.id.tv_envelope_name);
        iv_open_redpacket = view.findViewById(R.id.iv_open_redpacket);
        ll_close = view.findViewById(R.id.ll_close);
        addView(view);
    }


    /**
     * 头像
     */
    public void setIvEnvelopeHead(String envelopeHead) {
        SimpleUtils.loadImageForView(getContext(), iv_envelope_head, envelopeHead, 0);
    }

    /**
     * 名字
     */
    public void setTvEnvelopeName(String envelopeName) {
        tv_envelope_name.setText(envelopeName);
    }

    /**
     * 头像点击监听
     *
     * @param envelopeHeadClickListener
     */
    public void setEnvelopeHeadClickListener(View.OnClickListener envelopeHeadClickListener) {
        iv_envelope_head.setOnClickListener(envelopeHeadClickListener);
    }

    /**
     * 红包点击监听
     *
     * @param openRedpacketClickListener
     */
    public void setOpenRedpacketClickListener(View.OnClickListener openRedpacketClickListener) {
        iv_open_redpacket.setOnClickListener(openRedpacketClickListener);
    }

    /**
     * 关闭点击监听
     *
     * @param closeClickListener
     */
    public void setCloseClickListener(View.OnClickListener closeClickListener) {
        ll_close.setOnClickListener(closeClickListener);
    }

}
