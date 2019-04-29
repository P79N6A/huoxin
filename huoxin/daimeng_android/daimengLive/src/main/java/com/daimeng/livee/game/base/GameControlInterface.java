package com.daimeng.livee.game.base;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.daimeng.livee.im.IMControl;

public interface GameControlInterface {

    //打开游戏
    void openGame(IMControl imControl);

    //开始游戏(主播)
    void startGame(String liveuid,String stream,String token,IMControl imControl);

    //开始游戏(用户)
    void startGame(String gameId,String gameToken,String time);

    //隐藏游戏
    void goneGame();

    //游戏押注
    void stakeGame(String uid,String token,String grade,IMControl imControl);

    //事件监听
    void addGameEvent(GameEventInterface gameEventInterface);

    //揭晓游戏结果
    void openGameResult(String coin,int... img);

    //设置下注item监听
    void setOnItemClick(BaseQuickAdapter.OnItemClickListener itemClick);

    //刷新用户余额
    void refreshCoin(String coin);

    //刷新押注信息
    void refreshStakeCoin(int grade,int num);

    //释放
    boolean release();

    //开始游戏按钮事件
    void setStartGameListener(View.OnClickListener onClickListener);

    //结束事件按钮
    void setGameOverListener(View.OnClickListener onClickListener);

   /* //获取游戏ID
    String getGameId();

    //获取游戏Token
    String getGameToken();

    //获取游戏倒计时时间
    int getTime();*/
}
