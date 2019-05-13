package com.daimeng.live.business;

import android.app.Dialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;

import com.daimeng.live.AppContext;
import com.daimeng.live.api.remote.ApiUtils;
import com.daimeng.live.api.remote.PhoneLiveApi;
import com.daimeng.live.base.ShowLiveActivityBase;
import com.daimeng.live.bean.ConfigBean;
import com.daimeng.live.bean.LiveCheckInfoBean;
import com.daimeng.live.bean.LiveBean;
import com.daimeng.live.dialog.LiveCommon;
import com.daimeng.live.utils.LiveUtils;
import com.daimeng.live.utils.StringUtils;
import com.daimeng.live.utils.UIHelper;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import okhttp3.Call;

/**
 * Created by 魏鹏 on 2018/4/16.
 * 郑州秀星网络科技有限公司
 */

public class LiveTimeBusiness extends LivePayBuinessBase implements LivePayBusinessInterface{


    //观看了的分钟数
    private int lookTime = 0;


    //倒数计时是否完成
    private int countDownTime = 0;

    private Context mContext;

    private LiveTimeBusinessCallback callback;
    private Handler mHandler;
    private ConfigBean configBean;

    //主播信息
    public LiveBean mEmceeInfo;


    public void setCallback(LiveTimeBusinessCallback callback) {
        this.callback = callback;
    }

    @Override
    public void init(Context context, LiveBean emceeInfo, LiveCheckInfoBean liveCheckInfo) {

        mContext = context;
        mEmceeInfo = emceeInfo;
        mLiveCheckInfo = liveCheckInfo;
        configBean = LiveUtils.getConfigBean(context);
        mHandler = new Handler();

        checkLiveType();
    }

    @Override
    public boolean onResumeCheckLive() {

        int type = StringUtils.toInt(mLiveCheckInfo.getType_val());

        return (countDownTime < StringUtils.toInt(configBean.live_count_down_time) &&
                type == ShowLiveActivityBase.LIVE_TYPE_TIME) || (lookTime > 0  && type == ShowLiveActivityBase.LIVE_TYPE_TIME);
    }

    @Override
    public void release() {
        if(mHandler != null){
            mHandler.removeCallbacks(chargingLiveRunnable);
        }
    }

    //检查房间类型做对应操作
    private void checkLiveType() {

        if(StringUtils.toInt(mLiveCheckInfo.getType()) != ShowLiveActivityBase.LIVE_TYPE_TIME){
            return;
        }

        if(callback != null){
            callback.onPayLiveStartTime();
        }

        //不是第一次进行试看了，直接执行定时扣费任务
        if(StringUtils.toInt(mLiveCheckInfo.getIs_first()) == 1){
            lookTime ++ ;
            if(callback != null){
                callback.onPayTimeRefresh(lookTime);
            }
            mHandler.postDelayed(chargingLiveRunnable, 60 * 1000);

            return;
        }

        startDownTime();
    }


    //开始免费计时
    private void startDownTime() {

        //试看15秒
        new CountDownTimer(StringUtils.toInt(configBean.live_count_down_time) * 1000,1000){

            @Override
            public void onTick(long l) {

                countDownTime += 1;
                int time = StringUtils.toInt(configBean.live_count_down_time);

                //更新倒计时时间
                if(callback != null){
                    callback.onPayDownTimeRefresh(time - countDownTime);
                }
            }

            @Override
            public void onFinish() {

                if(mContext == null){
                    return;
                }

                //计时试看结束
                if(callback != null){
                    callback.onPayDownTimeEnd();
                }

                LiveCommon.showConfirmDialog(mContext, "提示", "试看结束，每分钟" + mLiveCheckInfo.getType_val()
                        +  LiveUtils.getConfigBean(AppContext.getInstance()).name_coin, new com.daimeng.live.interf.DialogInterface() {
                    @Override
                    public void cancelDialog(View v, Dialog d) {
                        if(callback != null){
                            callback.onPayCancel();
                        }
                    }

                    @Override
                    public void determineDialog(View v, Dialog d) {

                        d.dismiss();
                        if(mHandler != null){
                            chargeLiveRequest();
                        }
                    }
                });


            }
        }.start();
    }


    //计时房间定时收费任务
    private Runnable chargingLiveRunnable = new Runnable() {
        @Override
        public void run() {
            chargeLiveRequest();
        }
    };

    public void chargeLiveRequest(){

        PhoneLiveApi.requestCharging(AppContext.getInstance().getLoginUid(),AppContext.getInstance().getToken(),
                mEmceeInfo.uid,mEmceeInfo.stream,new StringCallback(){

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if(callback != null){
                            callback.onPayTimeError("扣费失败，请退出直播间重试");
                        }
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        JSONArray res = ApiUtils.checkIsSuccess(response);

                        if(res != null){

                            lookTime ++;
                            //自增观看时长
                            if(callback != null){
                                callback.onPayTimeRefresh(lookTime);
                                try {
                                    //扣费成功更新余额
                                    callback.onPayTimeChargeSuccess( res.getJSONObject(0).getString("coin"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            if(mHandler != null){
                                mHandler.postDelayed(chargingLiveRunnable, 60 * 1000);
                            }
                        }else{
                            //扣费失败
                            if(callback != null){
                                callback.onPayTimeError("余额不足，请充值");
                                UIHelper.showMyDiamonds(mContext);
                            }
                        }
                    }
                });
    }


    public interface LiveTimeBusinessCallback{

        void onPayLiveStartTime();

        void onPayDownTimeRefresh(long time);

        void onPayTimeRefresh(int time);

        void onPayDownTimeEnd();

        void onPayCancel();

        void onPayTimeError(String msg);

        void onPayTimeChargeSuccess(String coin);
    }
}
