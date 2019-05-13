package com.daimeng.live.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.daimeng.live.AppContext;
import com.daimeng.live.R;
import com.daimeng.live.adapter.ChestMenuListAdapter;
import com.daimeng.live.adapter.ViewPageGridViewAdapter;
import com.daimeng.live.bean.ChestMenuBean;
import com.daimeng.live.bean.UserBean;
import com.daimeng.live.interf.RedPacketSendCallBack;
import com.daimeng.live.ui.UserRechargeActivity;
import com.daimeng.live.utils.TDevice;
import com.daimeng.live.viewpagerfragment.LiveWebViewDialogFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * @dw 宝箱功能列表
 */

public class ChestBoxDialogFragment extends DialogFragment {


    private List<ChestMenuBean> mChestMenuList = new ArrayList<>();

    private ViewPageGridViewAdapter mVpGiftAdapter;

    //礼物view
    private ViewPager mVpChestMenuView;


    //当前选中的
    private ChestMenuBean mSelectedGiftItem;

    protected List<RecyclerView> mGiftViews = new ArrayList<>();

    private UserBean mUser;

    //1：activity 2:dialog
//    private final static int MENU_ACTION_TYPE_GAME_WEBVIEW = 1;
    private final static int MENU_ACTION_TYPE_ACTIVITY = 1;
    private final static int MENU_ACTION_TYPE_DIALOG = 2;
    private LiveWebViewDialogFragment liveEggWebViewDialogFragment;
    private ProtectDialogFragment protectDialogFragment;
    private ControlDialogFragment controlDialogFragment;
    private RedPacketDialogFragment redPacketDialogFragment;
    private String eggUrl;
    private String hostId; //主播id
    private String stream; //流名
    private String petUrl;
    private LiveWebViewDialogFragment livePetWebViewDialogFragment;
    private String racingUrl;
    private LiveWebViewDialogFragment liveRacingWebViewDialogFragment;
    private RedPacketSendCallBack callBack;
    private ProtectListDialogFragment protectListDialogFragment;

    public static ChestBoxDialogFragment newInstance(String eggUrl, String petUrl, String racingUrl, String hostId, String stream, RedPacketSendCallBack callBack) {
        ChestBoxDialogFragment instance = new ChestBoxDialogFragment();
        instance.eggUrl = eggUrl;
        instance.petUrl = petUrl;
        instance.racingUrl = racingUrl;
        instance.hostId = hostId;
        instance.stream = stream;
        instance.callBack=callBack;
        return instance;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mUser = AppContext.getInstance().getLoginUser();

        Dialog dialog = new Dialog(getActivity(), R.style.dialog_gift);
        dialog.setContentView(R.layout.view_chest_viewpager);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            params.height = (int) TDevice.dpToPixel(150);
        }
        window.setAttributes(params);
        initView(dialog);
        initData();
        return dialog;
    }

    private void initData() {
        //充值画面
        addChestListData(1,
                getString(R.string.recharge),
                R.drawable.show_buy_recharge_chest,
                MENU_ACTION_TYPE_ACTIVITY,
                new Intent(getContext(), UserRechargeActivity.class),
                null,
                null);

        //守护弹窗
        addChestListData(2,
                "开通守护",
                R.drawable.show_buy_guard_chest,
                MENU_ACTION_TYPE_DIALOG,
                null,
                protectDialogFragment,
                "ProtectDialogFragment");

        //红包
        addChestListData(3,
                "红包",
                R.drawable.chest_box_red_packet,
                MENU_ACTION_TYPE_DIALOG,
                null,
                redPacketDialogFragment,
                "RedPacketDialogFragment");

        if (!TextUtils.isEmpty(eggUrl)) {
            //砸金蛋
            addChestListData(4,
                    "砸金蛋",
                    R.drawable.show_golden_egg_chest,
                    MENU_ACTION_TYPE_DIALOG,
                    null,
                    liveEggWebViewDialogFragment,
                    "liveEggWebViewDialogFragment");
        }

        //宠物赛跑
        addChestListData(5,
                "宠物赛跑",
                R.drawable.chest_box_item_run,
                MENU_ACTION_TYPE_DIALOG,
                null,
                livePetWebViewDialogFragment,
                "livePetWebViewDialogFragment");

        //疯狂赛车
        addChestListData(6,
                "疯狂赛车",
                R.drawable.chestbox_item_game_saiche,
                MENU_ACTION_TYPE_DIALOG,
                null,
                liveRacingWebViewDialogFragment,
                "liveRacingWebViewDialogFragment");

        //房间控场
        addChestListData(7,
                "房间控场",
                R.drawable.promote_manager,
                MENU_ACTION_TYPE_DIALOG,
                null,
                controlDialogFragment,
                "controlDialogFragment");


        //房间控场
        addChestListData(8,
                "守护列表",
                R.drawable.promote_list,
                MENU_ACTION_TYPE_DIALOG,
                null,
                protectListDialogFragment,
                "ProtectListDialogFragment");

        fillList();
    }

    private void addChestListData(int position, String menu_name, int icon, int menu_type, Intent intent, DialogFragment dialogFragment, String tag) {

        ChestMenuBean chestMenuBean = new ChestMenuBean();
        chestMenuBean.setPosition(position);
        chestMenuBean.setMenu_name(menu_name);
        chestMenuBean.setIcon_res(icon);
        chestMenuBean.setType(menu_type);

        chestMenuBean.setIntent(intent);
//        chestMenuBean.setUrl(url);
        chestMenuBean.setDialogFragment(dialogFragment);
        chestMenuBean.setTag(tag);

        mChestMenuList.add(chestMenuBean);

    }


    //展示礼物列表
    private void initView(Dialog dialog) {


        mVpChestMenuView = (ViewPager) dialog.findViewById(R.id.vp_menu_page);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

            LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(ViewPager.LayoutParams.MATCH_PARENT, (int) TDevice.dpToPixel(100));
            mVpChestMenuView.setLayoutParams(p1);
        }

        liveEggWebViewDialogFragment = LiveWebViewDialogFragment.newInstance(eggUrl, false);

        livePetWebViewDialogFragment = LiveWebViewDialogFragment.newInstance(petUrl, false);

        liveRacingWebViewDialogFragment = LiveWebViewDialogFragment.newInstance(racingUrl, false);

        protectDialogFragment = ProtectDialogFragment.newInstance(hostId,stream);
        controlDialogFragment = ControlDialogFragment.newInstance(hostId, true);
        redPacketDialogFragment = RedPacketDialogFragment.newInstance(hostId,stream,callBack);
        protectListDialogFragment = ProtectListDialogFragment.newInstance(hostId,true);


    }

    //单项被选中
    private void giftItemClick(BaseQuickAdapter parent, View view, int position) {

        mSelectedGiftItem = (ChestMenuBean) parent.getItem(position);
        switch (mSelectedGiftItem.getType()) {
            case MENU_ACTION_TYPE_ACTIVITY:
                getActivity().startActivity(mSelectedGiftItem.getIntent());
                break;

            case MENU_ACTION_TYPE_DIALOG:
                mSelectedGiftItem.getDialogFragment().show(getFragmentManager(), mSelectedGiftItem.getTag());
                break;
        }

        dismiss();

    }


    //列表填充
    private void fillList() {

        if (null == mVpGiftAdapter) {

            //礼物item填充
            List<View> mGiftViewList = new ArrayList<>();

            int index = 0;

            int giftsPageSize;
            int giftSPageNum = 8;

            if (mChestMenuList.size() % giftSPageNum == 0) {

                giftsPageSize = mChestMenuList.size() / giftSPageNum;
            } else {
                giftsPageSize = (mChestMenuList.size() / giftSPageNum) + 1;
            }

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                giftsPageSize = 1;

                giftSPageNum = mChestMenuList.size();
            }

            for (int i = 0; i < giftsPageSize; i++) {
                View v = LayoutInflater.from(getActivity()).inflate(R.layout.view_show_gifts_list, null);
                mGiftViewList.add(v);
                List<ChestMenuBean> l = new ArrayList<>();

                for (int j = 0; j < giftSPageNum; j++) {
                    if (index >= mChestMenuList.size()) {
                        break;
                    }
                    l.add(mChestMenuList.get(index));
                    index++;
                }
                mGiftViews.add((RecyclerView) v.findViewById(R.id.gv_gift_list));
                ChestMenuListAdapter chestMenuListAdapter = new ChestMenuListAdapter(l);
                mGiftViews.get(i).setAdapter(chestMenuListAdapter);
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                    layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                    mGiftViews.get(i).setLayoutManager(layoutManager);
                } else {
                    mGiftViews.get(i).setLayoutManager(new GridLayoutManager(getContext(), 4));
                }

                chestMenuListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                        giftItemClick(adapter, view, position);
                    }
                });

            }
            mVpGiftAdapter = new ViewPageGridViewAdapter(mGiftViewList);

        }
        mVpChestMenuView.setAdapter(mVpGiftAdapter);
    }
}
