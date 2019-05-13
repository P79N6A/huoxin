package com.daimeng.live.game.base;

/**
 * Created by weipeng on 2017/4/6.
 */

public interface GameEventInterface {
    //游戏开启失败
    void onGameError();

    //游戏请求成功
    void onGameStartSuccess();

    //游戏结束
    void onGameOver();

    //押注成功
    void stakeGameSuccess(int coin);

    //押注结果揭晓
    void onOpenGameResult(int status);

}
