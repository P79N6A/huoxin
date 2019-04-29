package com.daimeng.livee.bean.callback;

import com.daimeng.livee.bean.CarBean;
import com.daimeng.livee.bean.ConfigBean;
import com.daimeng.livee.bean.GiftBean;

import java.util.List;

/**
 * Created by weipeng on 2018/1/24.
 */

public class InitInfoBean {


    /**
     * config : {"id":"1","site_url":"http://wanglianxin.cn/api/public/","apk_ver":"20180108","apk_url":"http://fir.im/bgsc","sitename":"快拍直播","wx_siteurl":"http://wanglianxin.cn/wxshare/index.php/Phone/index?roomnum=","app_android":"http://fir.im/bgsc","app_ios":"http://fir.im/vkjc","qr_url":"","ipa_ver":"7.2","ipa_url":"http://fir.im/vkjc","site":"http://wanglianxin.cn","live_width":"450","live_height":"800","keyframe":"15","fps":"30","quality":"95","more_img":"","pub_msg":"","lotterybase":"1000000000","topic_num":"5","ex_rate":"9","share_title":"这里主播分成最高可达90%，大家都来观看哦！","share_des":"一个发视频就可以挣钱的软件哟，哈哈！","ios_shelves":"1","name_coin":"钻石","name_votes":"萌票","enter_tip_level":"2","system_notice":"欢迎观看快拍视频直播","is_open_sms":"1","live_chat_level":"1","agent_charge_ratio":"1","sms_max_count":"1","sms_ip_limit":"0","live_vip_limit":"0","share_give_coin":"10","live_system_message":"绿色直播","live_count_down_time":"60","sharing":"10","copyright":"©2017秀星直播    京ICP证100430号    京网文[2015] 0609-239号    新出发京零字东150005号","record":"京公网安备11010502007133号","zombies":"50"}
     * car_list : [{"id":"7","name":"坦克1","thumb":"/data/upload/20170813/598fb291de0eb.gif"}]
     * quick_gift : {"id":"33","type":"2","sid":"0","giftname":"鼓掌","needcoin":"1","gifticon_mini":"","gifticon":"http://demozb.svcode.cn/data/upload/20160525/57450e092bc3f.png","orderno":"1","addtime":"1464143371"}
     */

    private ConfigBean config;
    private GiftBean quick_gift;
    private List<CarBean> car_list;

    public ConfigBean getConfig() {
        return config;
    }

    public void setConfig(ConfigBean config) {
        this.config = config;
    }

    public GiftBean getQuick_gift() {
        return quick_gift;
    }

    public void setQuick_gift(GiftBean quick_gift) {
        this.quick_gift = quick_gift;
    }

    public List<CarBean> getCar_list() {
        return car_list;
    }

    public void setCar_list(List<CarBean> car_list) {
        this.car_list = car_list;
    }



}
