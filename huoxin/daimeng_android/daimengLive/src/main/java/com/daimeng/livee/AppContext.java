package com.daimeng.livee;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.support.multidex.MultiDex;

import com.daimeng.livee.base.BaseApplication;
import com.daimeng.livee.bean.UserBean;
import com.daimeng.livee.cache.DataCleanManager;
import com.daimeng.livee.event.Event;
import com.daimeng.livee.utils.StringUtils;
import com.daimeng.livee.utils.TLog;
import com.daimeng.livee.utils.UIHelper;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.util.NetUtils;
import com.daimeng.livee.utils.MethodsCompat;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.cookie.CookieJarImpl;
import com.zhy.http.okhttp.cookie.store.PersistentCookieStore;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Properties;
import java.util.UUID;

import cn.jpush.android.api.JPushInterface;
import cn.tillusory.sdk.TiSDK;
import okhttp3.OkHttpClient;


public class AppContext extends BaseApplication {

    private static AppContext instance;

    private String loginUid;
    private String Token;
    private boolean login = false;
    public static String address = "未设置定位信息";
    public static String province = "";
    public static String lng = "";
    public static String lat = "";
    //是否完成定位
    public static boolean LOCAL_READY = false;


    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        init();
        initLogin();
        UIHelper.sendBroadcastForNotice(this);

    }

    private void init() {

        TiSDK.init("6330b68ec82844848cdecc593df4e9ea", this);
        initImageLoader(this);
        //环信初始化
        EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);

        //初始化
        EMClient.getInstance().init(this, options);
        //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(false);
        setGlobalListeners();


        //初始化jpush
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);


        //网络请求初始化
        CookieJarImpl cookieJar = new CookieJarImpl(new PersistentCookieStore(getApplicationContext()));
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .addInterceptor(new LoggerInterceptor("daimeng"))
                //其他配置
                .build();

        OkHttpUtils.initClient(okHttpClient);

    }


    public static void initImageLoader(Context context) {
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoaderConfiguration config = null;
            if (BuildConfig.DEBUG) {
                config = new ImageLoaderConfiguration.Builder(context)
                        .threadPriority(Thread.NORM_PRIORITY - 1)
                        .tasksProcessingOrder(QueueProcessingType.LIFO)
                        .memoryCacheSizePercentage(13)
                        .diskCacheSize(500 * 1024 * 1024)
                        .build();
            } else {
                config = new ImageLoaderConfiguration.Builder(context)
                        .threadPriority(Thread.NORM_PRIORITY - 2)
                        .diskCacheSize(500 * 1024 * 1024)
                        .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                        .tasksProcessingOrder(QueueProcessingType.LIFO).build();
            }
            ImageLoader.getInstance().init(config);
        }

    }


    protected void setGlobalListeners() {
        EMClient.getInstance().addConnectionListener(new MyConnectionListener());
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
    }


    //实现ConnectionListener接口
    private class MyConnectionListener implements EMConnectionListener {
        @Override
        public void onConnected() {
        }

        @Override
        public void onDisconnected(final int error) {
            if (error == EMError.USER_REMOVED) {
                // 显示帐号已经被移除
                TLog.log("显示帐号已经被移除");
            } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                // 显示帐号在其他设备登陆
                TLog.log("显示帐号在其他设备登陆");
            } else {
                if (NetUtils.hasNetwork(AppContext.getInstance())) {
                    //连接不到聊天服务器
                    TLog.log("连接不到聊天服务器");
                } else {
                    //当前网络不可用，请检查网络设置
                    TLog.log("当前网络不可用，请检查网络设置");
                    Event.CommonEvent event = new Event.CommonEvent();
                    event.action = 1;
                    EventBus.getDefault().post(event);
                }

            }
        }
    }

    private EMMessageListener msgListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            TLog.log("收到消息:" + messages);
            Intent broadcastIntent = new Intent("com.daimeng.live");
            broadcastIntent.putExtra("cmd_value", messages.get(0));
            sendBroadcast(broadcastIntent, null);
            //收到消息
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            //收到透传消息
            TLog.log("收到透传消息:" + messages);
        }

        @Override
        public void onMessageRead(List<EMMessage> messages) {

        }

        @Override
        public void onMessageDelivered(List<EMMessage> messages) {

        }


        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            //消息状态变动
            TLog.log("消息状态变动:" + message);
        }
    };

    private void initLogin() {
        UserBean user = getLoginUser();
        if (null != user && StringUtils.toInt(user.id) > 0) {
            login = true;
            loginUid = user.id;
            Token = user.token;
        } else {
            this.cleanLoginInfo();
        }
    }

    /**
     * 获得当前app运行的AppContext
     *
     * @return
     */
    public static AppContext getInstance() {
        return instance;
    }

    public boolean containsProperty(String key) {
        Properties props = getProperties();
        return props.containsKey(key);
    }

    public void setProperties(Properties ps) {
        AppConfig.getAppConfig(this).set(ps);
    }

    public Properties getProperties() {
        return AppConfig.getAppConfig(this).get();
    }

    public void setProperty(String key, String value) {
        AppConfig.getAppConfig(this).set(key, value);
    }

    /**
     * 获取cookie时传AppConfig.CONF_COOKIE
     *
     * @param key
     * @return
     */
    public String getProperty(String key) {
        String res = AppConfig.getAppConfig(this).get(key);
        return res;
    }

    public void removeProperty(String... key) {
        AppConfig.getAppConfig(this).remove(key);
    }

    /**
     * 获取App唯一标识
     *
     * @return
     */
    public String getAppId() {
        String uniqueID = getProperty(AppConfig.CONF_APP_UNIQUEID);
        if (StringUtils.isEmpty(uniqueID)) {
            uniqueID = UUID.randomUUID().toString();
            setProperty(AppConfig.CONF_APP_UNIQUEID, uniqueID);
        }
        return uniqueID;
    }

    /**
     * 获取App安装包信息
     *
     * @return
     */
    public PackageInfo getPackageInfo() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if (info == null)
            info = new PackageInfo();
        return info;
    }

    /**
     * 保存登录信息
     *
     * @param user 用户信息
     */
    @SuppressWarnings("serial")
    public void saveUserInfo(final UserBean user) {
        this.loginUid = user.id;
        this.Token = user.token;
        this.login = true;
        setProperties(new Properties() {
            {
                setProperty("user.uid", user.id);
                setProperty("user.name", user.user_nicename);
                setProperty("user.token", user.token);
                setProperty("user.sign", user.signature);
                setProperty("user.avatar", user.avatar);
                setProperty("user.coin", user.coin);
                setProperty("user.sex", user.sex);
                setProperty("user.signature", user.signature);
                setProperty("user.avatar_thumb", user.avatar_thumb);
                setProperty("user.level", user.level);
                setProperty("user.custombackground", user.custombackground == null ? "" : user.custombackground);
                setProperty("user.guardicon", user.guardicon == null ? "" : user.guardicon);

            }
        });
    }

    //更新用户登录信息单个字段

    /**
     * 更新用户信息
     */
    @SuppressWarnings("serial")
    public void updateUserInfoField(final String key, final String val) {
        setProperties(new Properties() {
            {
                setProperty(key, val);
            }
        });
    }

    /**
     * 更新用户信息
     *
     * @param user
     */
    @SuppressWarnings("serial")
    public void updateUserInfo(final UserBean user) {
        setProperties(new Properties() {
            {
                setProperty("user.uid", user.id);
                setProperty("user.name", user.user_nicename);
                setProperty("user.sign", user.signature);
                setProperty("user.avatar", user.avatar);
                setProperty("user.city", user.city == null ? "" : user.city);
                setProperty("user.coin", user.coin);
                setProperty("user.sex", user.sex);
                setProperty("user.signature", user.signature);
                setProperty("user.avatar_thumb", user.avatar_thumb);
                setProperty("user.level", user.level);
                setProperty("user.custombackground", user.custombackground == null ? "" : user.custombackground);
                setProperty("user.guardicon", user.guardicon == null ? "" : user.guardicon);
            }
        });
    }

    /**
     * 获得登录用户的信息
     *
     * @return
     */
    public UserBean getLoginUser() {
        UserBean user = new UserBean();
        user.id = getProperty("user.uid");
        user.avatar = getProperty("user.avatar");
        user.user_nicename = getProperty("user.name");
        user.token = getProperty("user.token");
        user.city = getProperty("user.city");
        user.coin = getProperty("user.coin");
        user.sex = getProperty("user.sex");
        user.signature = getProperty("user.signature");
        user.avatar = getProperty("user.avatar");
        user.level = getProperty("user.level");
        user.avatar_thumb = getProperty("user.avatar_thumb");
        user.custombackground = getProperty("user.custombackground");
        user.guardicon = getProperty("user.guardicon");
        return user;
    }

    /**
     * 清除登录信息
     */
    public void cleanLoginInfo() {
        this.loginUid = "0";
        this.login = false;
        removeProperty("user.guardicon","user.custombackground", "user.birthday", "user.avatar_thumb", "user.uid", "user.token", "user.name", "user.pwd", "user.avatar", "user.sign", "user.city", "user.coin", "user.sex", "user.signature", "user.signature", "user.avatar", "user.level");
    }

    public String getLoginUid() {
        return loginUid;
    }

    public String getToken() {
        return Token;
    }

    public boolean isLogin() {
        return login;
    }

    /**
     * 用户注销
     */
    public void Logout() {
        cleanLoginInfo();

        this.login = false;
        this.loginUid = "0";
        this.Token = "";

    }

    /**
     * 清除app缓存
     */
    public void clearAppCache() {
        DataCleanManager.cleanDatabases(this);
        // 清除数据缓存
        DataCleanManager.cleanInternalCache(this);
        // 2.2版本才有将应用缓存转移到sd卡的功能
        if (isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
            DataCleanManager.cleanCustomCache(MethodsCompat
                    .getExternalCacheDir(this));
        }
        // 清除编辑器保存的临时内容
        Properties props = getProperties();
        for (Object key : props.keySet()) {
            String _key = key.toString();
            if (_key.startsWith("temp"))
                removeProperty(_key);
        }
    }


    /**
     * 判断当前版本是否兼容目标版本的方法
     *
     * @param VersionCode
     * @return
     */
    public static boolean isMethodsCompat(int VersionCode) {
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        return currentVersion >= VersionCode;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
