package com.daimeng.livee.alipay;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;
import com.alipay.sdk.app.PayTask;
import com.daimeng.livee.AppContext;

//支付宝配置信息调用支付类
public class AliPay{
    public static final String TAG = "alipay-sdk";

    private static final int SDK_PAY_FLAG = 1;

    private static final int SDK_CHECK_FLAG = 2;

    private static final int ALIPAY = 3;

    private Activity mPayActivity;

    public AliPay(Activity payActivity) {
        this.mPayActivity = payActivity;
    }

    public void startPay(final String payInfo){
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(mPayActivity);
                // 调用支付接口
                String result = alipay.pay(payInfo,true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }


    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    Result resultObj = new Result((String) msg.obj);

                    String resultStatus = resultObj.toString();

                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultObj.getResultStatus(), "9000")) {
                        AppContext.showToast("支付成功");

                        //mPayActivity.rechargeResult(true,rechargeNum);

                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000” 代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultObj.getResultStatus(), "8000")) {
                            AppContext.showToast("支付结果确认中");
                        } else {
                            AppContext.showToast("支付失败");
                        }
                    }
                    break;
                }
                case SDK_CHECK_FLAG: {
                    Toast.makeText(mPayActivity, "检查结果为：" + msg.obj,
                            Toast.LENGTH_SHORT).show();
                    break;
                }
                case ALIPAY:{

                    startPay((String) msg.obj);
                    break;
                }
                default:
                    break;
            }
        };
    };

}
