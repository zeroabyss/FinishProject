package com.example.aiy.finishproject;

import android.app.Application;

import com.blankj.utilcode.util.Utils;

import org.litepal.LitePal;

/**
 * <p>功能简述：
 * <p>Created by Aiy on 2018/4/9.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
        LitePal.initialize(this);

    }
}
