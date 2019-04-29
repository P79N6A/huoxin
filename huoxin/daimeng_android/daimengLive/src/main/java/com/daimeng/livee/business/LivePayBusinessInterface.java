package com.daimeng.livee.business;

import android.content.Context;

import com.daimeng.livee.bean.LiveCheckInfoBean;
import com.daimeng.livee.bean.LiveBean;

/**
 * Created by 魏鹏 on 2018/4/16.
 * 郑州秀星网络科技有限公司
 */

public interface LivePayBusinessInterface {

    void init(Context context, LiveBean emceeInfo, LiveCheckInfoBean liveCheckInfo);

    boolean onResumeCheckLive();

    void release();
}
