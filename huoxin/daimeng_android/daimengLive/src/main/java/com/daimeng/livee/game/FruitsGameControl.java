package com.daimeng.livee.game;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.daimeng.livee.AppContext;
import com.daimeng.livee.R;
import com.daimeng.livee.api.remote.ApiUtils;
import com.daimeng.livee.api.remote.PhoneLiveApi;
import com.daimeng.livee.game.base.GameControlInterface;
import com.daimeng.livee.game.base.GameEventInterface;
import com.daimeng.livee.game.base.GameOnClickStakeInterface;
import com.daimeng.livee.game.bean.FruitsGameBean;
import com.daimeng.livee.game.view.FruitsGameView;
import com.daimeng.livee.im.IMControl;
import com.daimeng.livee.utils.StringUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * 水果蔬菜游戏控制器
 */

public class FruitsGameControl implements GameControlInterface {

    private FruitsGameView fruitsGameView;
    private LinearLayout gameViewContent;
    private GameEventInterface gameEventInterface;
    //游戏唯一标示，和游戏验证Token
    private String gameId,fruitsToken;
    //当前选中的押注金额
    private int selectStakeNum = 10;
    //倒计时时间
    public int time;
    private List<FruitsGameBean> mListFruits = new ArrayList<>();
    //押注的下标
    private List<Integer> mListBetIndex = new ArrayList<>();
    private Context mContext;


    public FruitsGameControl(Context context,LinearLayout gameViewContent,boolean isEmcee) {
        mContext = context;
        fruitsGameView = new FruitsGameView(context);

        this.gameViewContent = gameViewContent;
        mListFruits.add(new FruitsGameBean(R.mipmap.card_1,1,"0"));
        mListFruits.add(new FruitsGameBean(R.mipmap.card_2,2,"0"));
        mListFruits.add(new FruitsGameBean(R.mipmap.card_3,3,"0"));
        mListFruits.add(new FruitsGameBean(R.mipmap.card_4,4,"0"));
        mListFruits.add(new FruitsGameBean(R.mipmap.card_5,5,"0"));
        mListFruits.add(new FruitsGameBean(R.mipmap.card_6,6,"0"));

        //选择下注金额
        fruitsGameView.setStakeSelectNumItemClick(new GameOnClickStakeInterface() {
            @Override
            public void onClickStake(int num) {
                selectStakeNum = (int) Math.pow(10,num);

            }
        });

        fruitsGameView.initGameMenu(isEmcee);
    }

    public void addGameEvent(GameEventInterface gameEventInterface){
        this.gameEventInterface = gameEventInterface;
    }

    @Override
    public void openGameResult(String coin,int... res) {

        if(fruitsGameView != null){
            //判断当前用户押注是否中奖
            if(gameEventInterface != null){

                for(int i = 0; i < res.length; i ++){
                    if(mListBetIndex.contains(res[i] + 1)){
                        gameEventInterface.onOpenGameResult(1);
                      break;
                    }
                }
            }

            //展示10秒游戏结果
            new CountDownTimer(10 * 1000,1000){

                @Override
                public void onTick(long l) {

                    fruitsGameView.refreshCountDownTv((int) l/1000);
                }

                @Override
                public void onFinish() {

                    //展示十秒押注情况
                    fruitsGameView.removeGameResultView();
                    fruitsGameView.showGameStatus("游戏即将开始");
                    new CountDownTimer(10 * 1000,1000){

                        @Override
                        public void onTick(long l) {

                            fruitsGameView.refreshCountDownTv((int) l/1000);

                        }

                        @Override
                        public void onFinish() {

                            refreshGameUIData();

                        }
                    }.start();
                }
            }.start();

            int[] pics = new int[]{R.mipmap.card_1,R.mipmap.card_2,R.mipmap.card_3,R.mipmap.card_4,R.mipmap.card_5,R.mipmap.card_6};
            fruitsGameView.openGameResult(pics[res[0]],pics[res[1]],pics[res[2]]);
            fruitsGameView.refreshCoin(coin);

        }

    }

    @Override
    public void setOnItemClick(BaseQuickAdapter.OnItemClickListener itemClick) {
        if(fruitsGameView != null){
            fruitsGameView.setOnItemClick(itemClick);
        }
    }

    @Override
    public void refreshCoin(String coin) {
        if(fruitsGameView != null){
            fruitsGameView.refreshCoin(coin);
        }
    }

    @Override
    public void refreshStakeCoin(int grade,int num) {
        //更新押注数量
        if(fruitsGameView != null){
            int coin;
            coin = StringUtils.toInt(mListFruits.get(grade - 1).coin);
            mListFruits.get(grade - 1).coin = String.valueOf((coin + num));

            fruitsGameView.refreshItemData();
        }
    }

    @Override
    public boolean release() {

        if(fruitsGameView != null && fruitsGameView.getGameStatus()){
            AppContext.showToast("本轮游戏尚未结束");
            return false;
        }
        gameViewContent.removeAllViews();
        fruitsGameView = null;
        mContext = null;
        gameEventInterface = null;
        mListFruits.clear();
        mListBetIndex.clear();
        return true;
    }

    @Override
    public void setStartGameListener(View.OnClickListener onClickListener) {
        if(fruitsGameView != null){
            fruitsGameView.setStartGameListener(onClickListener);
        }
    }

    @Override
    public void setGameOverListener(View.OnClickListener onClickListener) {
        if(fruitsGameView != null){
            fruitsGameView.setGameOverListener(onClickListener);
        }
    }

    @Override
    public void openGame(IMControl imControl) {

        if(imControl != null){
            imControl.doSendOpenGame(2);
        }

        gameViewContent.removeView(fruitsGameView);
        gameViewContent.addView(fruitsGameView);

    }


    @Override
    public void startGame(String liveuid, String stream, final String token, final IMControl imControl) {


        if(fruitsGameView != null){
            if(fruitsGameView.getGameStatus()){
                return;
            }
            fruitsGameView.changeStartGameBtnStatus(true);
        }
        PhoneLiveApi.requestStartGame(liveuid,stream,token,new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                JSONArray data = ApiUtils.checkIsSuccess(response);
                if(data != null){
                    try {

                        fillData(data.getJSONObject(0));
                        //发送socket开始游戏
                        if(imControl != null){
                            imControl.doSendStartFruitsGame(gameId,fruitsToken, String.valueOf(time));
                        }

                        fruitsGameView.setListFruits(mListFruits);
                        fruitsGameView.startGameAnimation();

                        startCountDown();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void stakeGame(String uid, String token, final String grade, final IMControl imControl) {

        PhoneLiveApi.requestStakeGame(uid,token, String.valueOf(selectStakeNum),gameId,grade,new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                JSONArray data = ApiUtils.checkIsSuccess(response);
                if(data != null){

                    if(gameEventInterface != null){
                        gameEventInterface.stakeGameSuccess(selectStakeNum);
                    }
                    //存储下注下标
                    if(!mListBetIndex.contains(StringUtils.toInt(grade))){
                        mListBetIndex.add(StringUtils.toInt(grade));
                    }
                    if(imControl != null){
                        //发送下注广播
                        imControl.doSendBetMessage(grade, String.valueOf(selectStakeNum));
                    }
                    //刷新押注金额
                    refreshStakeCoin(StringUtils.toInt(grade),selectStakeNum);

                }
            }
        });

    }

    @Override
    public void startGame(String gameId,String gameToken,String time) {

        this.gameId = gameId;
        this.fruitsToken = gameToken;
        this.time = StringUtils.toInt(time);
        fruitsGameView.setListFruits(mListFruits);
        fruitsGameView.startGameAnimation();
        startCountDown();
    }


    @Override
    public void goneGame() {
        if(fruitsGameView.getVisibility() == View.VISIBLE){
            fruitsGameView.setVisibility(View.GONE);
        }else{
            fruitsGameView.setVisibility(View.VISIBLE);
        }
    }


    //开始游戏倒计时
    private void startCountDown(){

        new CountDownTimer(time * 1000,1000){

            @Override
            public void onTick(long l) {

                if(fruitsGameView == null)return;

                fruitsGameView.refreshCountDownTv((int) l/1000);
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }

    //填充数据
    private void fillData(JSONObject data) throws JSONException {
        gameId = data.getString("gameid");
        fruitsToken = data.getString("fruitsToken");
        time = data.getInt("time");
        //会掉成功
        if(gameEventInterface != null){
            gameEventInterface.onGameStartSuccess();
        }
    }

    //刷新UI数据
    private void refreshGameUIData() {
        if(gameEventInterface != null){
            gameEventInterface.onGameOver();
        }
        for(FruitsGameBean d : mListFruits){
            d.coin = "0";
        }
        mListBetIndex.clear();
        fruitsGameView.refreshGameUI();

        if(fruitsGameView != null){
            fruitsGameView.changeStartGameBtnStatus(false);
        }
    }
}
