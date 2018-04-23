package com.example.aiy.finishproject.view.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.aiy.finishproject.R;
import com.example.aiy.finishproject.lock.GestureLockLayout;
import com.example.aiy.finishproject.util.SPUtils;

public class VerifyPasswordActivity extends AppCompatActivity {
    private String mPassword;
    private TextView mTipText;
    private GestureLockLayout mLockLayout;
    private Handler mHandler=new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_password);
        initPassword();
        initView();
    }
    private void initPassword(){
        mPassword= SPUtils.getStringSP(this,GestureLockActivity.GESTURE_LOCK_PASSWORD,"[1,2,3]");
    }
    private void initView(){
        mTipText=findViewById(R.id.verify_password_tip_text);
        mLockLayout=findViewById(R.id.verity_password_gesture);
        mLockLayout.setMode(GestureLockLayout.VERIFY_MODE);
        mLockLayout.setDotCount(3);
        mLockLayout.setTryTimes(10);
        mLockLayout.setAnswer(mPassword);
        mLockLayout.setOnLockVerifyListener(new GestureLockLayout.OnLockVerifyListener() {
            @Override
            public void onGestureSelected(int id) {
            }
            @Override
            public void onGestureFinished(boolean isMatched) {
                if (isMatched){
                    startActivity(new Intent(VerifyPasswordActivity.this,MainActivity.class));
                    finish();
                }else {
                    mTipText.setText("密码错误，还有" + mLockLayout.getTryTimes() + "次机会");
                    resetGesture();
                }
            }
            @Override
            public void onGestureTryTimesBoundary() {
                mTipText.setText("机会已用完，请过会再尝试");
                mLockLayout.setTouchable(false);
            }
        });
    }
    private void resetGesture() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mLockLayout.resetGesture();
            }
        }, 200);
    }
}
