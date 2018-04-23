package com.example.aiy.finishproject.view.activity;

import android.app.Activity;
import android.os.Handler;
import android.os.Bundle;
import android.os.Message;
import android.widget.ImageView;


import com.bumptech.glide.Glide;
import com.example.aiy.finishproject.Base.BaseActivity;
import com.example.aiy.finishproject.R;
import com.example.aiy.finishproject.util.ActivityUtils;
import com.example.aiy.finishproject.util.SPUtils;

import java.lang.ref.WeakReference;

import javax.crypto.Cipher;

public class SplashActivity extends BaseActivity {
    ImageView mSplash;
    /**
     * 变量简述： 是否拥有密码
     */
    boolean hasLockFlag;
    private Handler mHandler=new MyHandler<>(this);
    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSplash=findViewById(R.id.splash_activity_begin);
        Glide.with(this)
                .load(R.drawable.splash_image)
                .into(mSplash);
        hasLockFlag= SPUtils.getBooleanSP(this,GestureLockActivity.HAS_GESTURE_LOCK,false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    Message message= mHandler.obtainMessage();
                    message.what=1;
                    message.obj=false;
                    mHandler.sendMessage(message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static class MyHandler<T extends SplashActivity> extends Handler{
        private final WeakReference<T> main;

        private MyHandler(T t) {
            this.main = new WeakReference<T>(t);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    T t=main.get();
                    if (t==null) return;
                    if (t.hasLockFlag){
                        ActivityUtils.startActivity(t,VerifyPasswordActivity.class);
                    }else {
                        ActivityUtils.startActivity(t,MainActivity.class);
                    }
                    t.finish();

            }
        }


    }
}
