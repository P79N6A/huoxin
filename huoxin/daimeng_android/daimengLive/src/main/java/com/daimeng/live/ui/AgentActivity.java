package com.daimeng.live.ui;

import android.view.View;

import com.daimeng.live.AppConfig;
import com.daimeng.live.AppContext;
import com.daimeng.live.R;
import com.daimeng.live.base.BaseTitleActivity;
import com.daimeng.live.utils.TDevice;
import com.daimeng.live.utils.UIHelper;

import butterknife.OnClick;

public class AgentActivity extends BaseTitleActivity{


    @Override
    protected int getLayoutId() {
        return R.layout.activity_agent;
    }

    @OnClick({R.id.ll_user_list,R.id.ll_agent_record,R.id.ll_share})
    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.ll_user_list:

                UIHelper.showAgentUserActivity(this);

                break;

            case R.id.ll_agent_record:

                UIHelper.showAgentProfitRecord(this);
                break;

            case R.id.ll_share:

                //复制推广链接
                TDevice.copyTextToBoard( AppConfig.MAIN_URL + "/index.php?g=Appapi&m=Spread&a=reg&suid=" + AppContext.getInstance().getLoginUid());
                break;
        }
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

        setActionBarTitle("我的推广");


    }
}
