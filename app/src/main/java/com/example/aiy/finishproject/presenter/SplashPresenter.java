package com.example.aiy.finishproject.presenter;

import android.content.Context;

import com.example.aiy.finishproject.util.SPUtils;
import com.example.aiy.finishproject.view.activity.GestureLockActivity;

/**
 * <p>功能简述：
 * <p>Created by Aiy on 2018/5/10.
 */

public class SplashPresenter implements BasePresenter {
    private Context context;
    @Override
    public void initPresenter(Context context) {
        this.context=context;
    }

    public boolean getLockFlag(){
       return SPUtils.getBooleanSP(context, GestureLockActivity.HAS_GESTURE_LOCK,false);
    }
}
