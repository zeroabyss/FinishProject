package com.example.aiy.finishproject.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * <p>功能简述：Activity相关的操作类
 * <p>Created by Aiy on 2018/4/9.
 */

public class ActivityUtils {
    /**
     * 方法简述： 启动Activity
     * @param last 启动前的activity
     * @param next 要启动的activity的class
     */
    public static void startActivity(Activity last, Class next){
            last.startActivity(new Intent(last,next));
    }
}
