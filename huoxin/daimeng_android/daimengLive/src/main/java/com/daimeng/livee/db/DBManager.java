package com.daimeng.livee.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.daimeng.livee.model.DaoMaster;
import com.daimeng.livee.model.DaoSession;
import com.daimeng.livee.model.JsonModel;
import com.daimeng.livee.model.JsonModelDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.File;
import java.util.List;

public class DBManager {
    private final static String dbName = "live_db";
    private static DBManager mInstance;
    private DaoMaster.DevOpenHelper openHelper;
    private Context context;

    public DBManager(Context context) {
        this.context = context;
        openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
    }

    /**
     * 获取单例引用
     *
     * @param context
     * @return
     */
    public static DBManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (DBManager.class) {
                if (mInstance == null) {
                    mInstance = new DBManager(context);
                }
            }
        }
        return mInstance;
    }
    /**
     * 获取可读数据库
     */
    private SQLiteDatabase getReadableDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
        }
        SQLiteDatabase db = openHelper.getReadableDatabase();
        return db;
    }

    /**
     * 获取可写数据库
     */
    private SQLiteDatabase getWritableDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
        }
        SQLiteDatabase db = openHelper.getWritableDatabase();
        File database=new File("/data/data/" + context.getPackageName() + "/" + dbName);
        if(!database.exists()&& db!=null){
            db.close();
            db = openHelper.getWritableDatabase();
        }

        return db;
    }

    /**
     * 插入一条记录
     *
     * @param json
     */
    public void insertUser(JsonModel json) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        JsonModelDao userDao = daoSession.getJsonModelDao();
        userDao.insert(json);
    }

    /**
     * 插入用户集合
     *
     * @param jsons
     */
    public void insertUserList(List<JsonModel> jsons) {
        if (jsons == null || jsons.isEmpty()) {
            return;
        }
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        JsonModelDao userDao = daoSession.getJsonModelDao();
        userDao.insertInTx(jsons);
    }

    /**
     * 删除一条记录
     *
     * @param json
     */
    public void deleteUser(JsonModel json) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        JsonModelDao userDao = daoSession.getJsonModelDao();
        userDao.delete(json);
    }

    /**
     * 更新一条记录
     *
     * @param json
     */
    public void updateUser(JsonModel json) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        JsonModelDao jsonDao = daoSession.getJsonModelDao();
        jsonDao.update(json);
    }

    /**
     * 查询列表
     */
    public List<JsonModel> queryUserList() {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        JsonModelDao userDao = daoSession.getJsonModelDao();
        QueryBuilder<JsonModel> qb = userDao.queryBuilder();
        List<JsonModel> list = qb.list();
        return list;
    }

    /**
     * 查询列表
     */
    public List<JsonModel> queryUserList(String key) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        JsonModelDao userDao = daoSession.getJsonModelDao();
        QueryBuilder<JsonModel> qb = userDao.queryBuilder();
        qb.where(JsonModelDao.Properties.Key.eq(key));
        List<JsonModel> list = qb.list();
        return list;
    }


}