package com.daimeng.livee.game;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.daimeng.livee.AppContext;
import com.daimeng.livee.api.remote.ApiUtils;
import com.daimeng.livee.api.remote.PhoneLiveApi;
import com.daimeng.livee.bean.UserBean;
import com.daimeng.livee.game.view.PokersGameView;
import com.daimeng.livee.im.IMControl;
import com.daimeng.livee.utils.StringUtils;
import com.daimeng.livee.utils.UIHelper;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import okhttp3.Call;

/**
 * Created by weipeng on 2017/3/7.
 */

public class PokersGameControl {

    private String mJinhuaToken, mGameId, mGameTime;
    private LinearLayout gameViewContent;
    private PokersGameView mPokersLayout;
    private GameInterface mGameInterface;

    public static final int POKERS_OPEN_VIEW = 1;//打开
    public static final int POKERS_START_GAME = 2;//开始发牌
    public static final int POKERS_CLOSE_GAME = 3;//关闭游戏
    public static final int POKERS_COUNT_DOWN = 4;//倒计时
    public static final int POKERS_BETTING_GAME = 5;//下注
    public static final int POKERS_RESULT_GAME = 6;//开奖
    public static final int POKERS_HAVE_HAND_GAME = 7;//进行中
    public int mBettingCoin = 10;
    public int mGameStatus = 0;

    private int betting1Total = 0, betting2Total = 0, betting3Total = 0, betting1 = 0, betting2 = 0, betting3 = 0;

    private boolean isEmcee;

    public PokersGameControl(LinearLayout gameViewContent, Context context, boolean isemcee) {

        this.gameViewContent = gameViewContent;
        mPokersLayout = new PokersGameView(context);

        this.isEmcee = isemcee;
        setOnRechargeClick(context);

        mPokersLayout.initGameMenu(isEmcee);

        setIsVisibleBettingView(true);
    }


    public void initGameView(Context context) {

        mGameStatus = 0;
        mBettingCoin = 10;
        betting1Total = 0;
        betting2Total = 0;
        betting3Total = 0;
        betting1 = 0;
        betting2 = 0;
        betting3 = 0;
        mJinhuaToken = "0";
        mGameId = "0";
        mGameTime = "0";


        mPokersLayout.initGameView();
        mPokersLayout.initGameMenu(isEmcee);
        setIsVisibleBettingView(true);
        setOnRechargeClick(context);
    }

    public void openGame(IMControl imControl) {

        if(imControl != null){
            imControl.doSendOpenJinhuaGame(1);
        }

        gameViewContent.removeView(mPokersLayout);
        gameViewContent.addView(mPokersLayout);

    }


    //开始游戏(主播)
    public void startGame(String liveuid, String stream, String token) {

        if (mGameStatus == POKERS_START_GAME) {

            return;
        }
        setIsVisibleStartWords();
        if (mGameInterface != null) {
            mPokersLayout.setOnGameListen(mGameInterface);
        }

        requestStartGame(liveuid, stream, token);
        mGameStatus = POKERS_START_GAME;
    }

    //是否显示下注栏
    public void setIsVisibleBettingView(boolean isvisible) {
        mPokersLayout.setIsVisibleBettingView(isvisible);
    }
    //是否显示开始提示
    public void setIsVisibleStartWords(){
        mPokersLayout.setIsVisibleStartWords();
    }

    //开始游戏
    public void startGame() {

        if (mGameStatus == POKERS_START_GAME || mGameStatus == POKERS_HAVE_HAND_GAME) {

            if (mGameStatus == POKERS_HAVE_HAND_GAME) mGameStatus = POKERS_START_GAME;

            return;
        }

        if (mPokersLayout.getVisibility() == View.GONE) {
            mPokersLayout.setVisibility(View.VISIBLE);
        }
        mPokersLayout.startGame();
        setIsVisibleStartWords();
        mGameStatus = POKERS_START_GAME;

    }

    //开始倒计时
    public void startCountDown() {

        if (mGameStatus == POKERS_COUNT_DOWN || mGameStatus == POKERS_HAVE_HAND_GAME) {

            if (mGameStatus == POKERS_HAVE_HAND_GAME) mGameStatus = POKERS_COUNT_DOWN;

            return;
        }
        mPokersLayout.startCountDown(StringUtils.toInt(mGameTime));
        mGameStatus = POKERS_COUNT_DOWN;
    }

    //游戏进行中
    public void setGameStatusOnHaveInHand() {

        mGameStatus = POKERS_HAVE_HAND_GAME;

        mPokersLayout.setGameStatusOnHaveInHand(mGameTime);
        mPokersLayout.changeBettingCoin(1, 0, betting1Total);
        mPokersLayout.changeBettingCoin(2, 0, betting2Total);
        mPokersLayout.changeBettingCoin(3, 0, betting3Total);

    }

    //揭晓游戏结果
    public void openGameResult(String res, Handler handler) {

        if (mGameStatus == POKERS_RESULT_GAME) {

            return;
        }

        mGameStatus = POKERS_RESULT_GAME;

        try {
            JSONArray result = new JSONArray(res);

            for (int i = 0; i < result.length(); i++) {

                JSONArray a = result.getJSONArray(i);

                postShowResult(i, a, handler);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //设置游戏监听器
    public void setOnGameListen(GameInterface gameListen) {

        this.mGameInterface = gameListen;

        if (mGameInterface != null) {
            mPokersLayout.setOnGameListen(mGameInterface);
        }
    }


    //延时执行显示结果
    private void postShowResult(final int index, final JSONArray data, Handler handler) {

        if (handler == null) return;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    mPokersLayout.showResult(index, data.getString(0), data.getString(1), data.getString(2), data.getInt(6), data.getInt(3));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, index * 1000 * 2);

    }


    //下注修改数量
    public void changeBettingCoin(String muid, String uid, int index, int coin) {
        if (index == 1) {
            if (uid.equals(muid)) {
                betting1 += coin;
            }
            betting1Total += coin;
            mPokersLayout.changeBettingCoin(index, betting1, betting1Total);
        } else if (index == 2) {

            if (uid.equals(muid)) {
                betting2 += coin;
            }
            betting2Total += coin;
            mPokersLayout.changeBettingCoin(index, betting2, betting2Total);
        } else {

            if (uid.equals(muid)) {
                betting3 += coin;
            }
            betting3Total += coin;
            mPokersLayout.changeBettingCoin(index, betting3, betting3Total);
        }

    }

    //开始游戏
    private void requestStartGame(String liveuid, String stream, String token) {

        PhoneLiveApi.requestJinhuaStartGame(liveuid, stream, token, new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {

                JSONArray res = ApiUtils.checkIsSuccess(response);
                if (res != null) {
                    try {
                        mJinhuaToken = res.getJSONObject(0).getString("token");
                        mGameId = res.getJSONObject(0).getString("gameid");
                        mGameTime = res.getJSONObject(0).getString("time");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mPokersLayout.startGame();
                }
            }
        });
    }


    //下注
    public void requestBetting(final int index, final UserBean user, final IMControl chatServer) {

        PhoneLiveApi.requestJinhuaBet(mGameId, mBettingCoin, index, user.id, user.token, new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {

                        JSONArray data = ApiUtils.checkIsSuccess(response);

                        if (data != null) {
                            chatServer.doSendJinhuaBetting(String.valueOf(mBettingCoin), String.valueOf(index), user);
                            try {

                                String coin = data.getJSONObject(0).getString("coin");

                                setCoin(coin);
                                UserBean u = AppContext.getInstance().getLoginUser();
                                u.coin = coin;

                                AppContext.getInstance().saveUserInfo(u);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }


    //关闭或者打开游戏
    public void closeGame(final IMControl chatServer, final UserBean user, String stream) {

        if (mGameStatus != 0) {
            Toast.makeText(AppContext.getInstance(), "请等待当前游戏结束", Toast.LENGTH_SHORT).show();
            return;
        }

        PhoneLiveApi.closeGameJinhua(stream, user.id, user.token, mGameId, "1", new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                JSONArray data = ApiUtils.checkIsSuccess(response);
                if (data != null) {

                    chatServer.doSendEndJinhuaGame();
                }
            }
        });

    }

    public void setCoin(String coin) {
        mPokersLayout.setCoin(coin);
    }

    public void setOnRechargeClick(final Context context) {

        mPokersLayout.setOnRechargeClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIHelper.showMyDiamonds(context);
            }
        });
    }

    public boolean release() {

        if(mPokersLayout != null && mGameStatus != 0 ){
            AppContext.showToast("本轮游戏尚未结束");
            return false;
        }
        gameViewContent.removeAllViews();
        mPokersLayout = null;
        mGameInterface = null;
        return true;
    }


    public int getGameStatus() {
        return mGameStatus;
    }

    public String getJinhuaToken() {
        return mJinhuaToken;
    }

    public String getGameId() {
        return mGameId;
    }

    public void setGameId(String gameId) {
        mGameId = gameId;
    }

    public String getGameTime() {
        return mGameTime;
    }

    public void setGameTime(String gameTime) {
        mGameTime = gameTime;
    }

    public void setBettingCoin(int bettingCoin) {
        mBettingCoin = bettingCoin;
    }

    public int getBettingCoin() {
        return mBettingCoin;
    }


    public void setBetting2Total(int betting2Total) {
        this.betting2Total = betting2Total;
    }

    public void setBetting3Total(int betting3Total) {
        this.betting3Total = betting3Total;
    }

    public void setBetting1Total(int betting1Total) {
        this.betting1Total = betting1Total;
    }

    public void setEmcee(boolean emcee) {
        isEmcee = emcee;
    }



    public static interface GameInterface {
        void onStartLicensing();

        void onStartCountDown();

        void onEndCountDown();

        void onClickBetting(int index);

        void onShowResultEnd();

        void onClickStartGame();

        void onStartGameCommit();

        void onSelectBettingNum(int coin);

        void onInitGameView();

        void onClickGameOver();
    }
}
