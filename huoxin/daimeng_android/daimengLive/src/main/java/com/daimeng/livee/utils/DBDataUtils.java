package com.daimeng.livee.utils;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.daimeng.livee.model.JsonModel;

import java.util.List;

/**
 * Created by weipeng on 2017/8/15.
 */

public class DBDataUtils {

    public static <T> T getClassForDB(Context context, String key, Class<T> clz){

        List<JsonModel> list = com.daimeng.livee.db.DBManager.getInstance(context).queryUserList(key);
        JsonModel model = list.get(0);

        return JSON.parseObject(model.getValue(),clz);
    }
}
