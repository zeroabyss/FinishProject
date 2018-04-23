package com.example.aiy.finishproject.view.activity;

import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aiy.finishproject.R;
import com.example.aiy.finishproject.lock.GestureLockDisplayView;
import com.example.aiy.finishproject.lock.GestureLockLayout;
import com.example.aiy.finishproject.lock.JDLockView;
import com.example.aiy.finishproject.util.LoggerUtils;
import com.example.aiy.finishproject.util.SPUtils;
import com.suke.widget.SwitchButton;

import java.util.List;

public class GestureLockActivity extends AppCompatActivity {
    public static final String HAS_GESTURE_LOCK="has_gesture_lock";
    public static final String GESTURE_LOCK_PASSWORD="gesture_lock_password";
    private GestureLockLayout mGestureLockLayout;
    private GestureLockDisplayView mLockDisplayView;
    private TextView mSettingHintText;
    private SwitchButton sb;
    private ImageView mback;
    private Handler mHandler=new Handler();
    /**
     * 变量简述： 是否开启手势功能
     */
    boolean hasLockFlag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_lock);
        initViews();
        initEvents();
        initSwitchButton();
    }
    private void initSwitchButton(){
        sb=findViewById(R.id.switchbutton);
        hasLockFlag= SPUtils.getBooleanSP(this,HAS_GESTURE_LOCK,false);
        if (hasLockFlag)
            mGestureLockLayout.setTouchable(true);
        else
            mGestureLockLayout.setTouchable(false);

        sb.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked){
                    mGestureLockLayout.setTouchable(true);
                }else
                    mGestureLockLayout.setTouchable(false);
                SPUtils.setBooleanSp(GestureLockActivity.this,HAS_GESTURE_LOCK,false);
            }
        });
        sb.setChecked(hasLockFlag);
    }
    private void initViews() {
        mback=findViewById(R.id.about_back);
        mback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mGestureLockLayout = (GestureLockLayout) findViewById(R.id.l_gesture_view);
        mLockDisplayView = (GestureLockDisplayView) findViewById(R.id.l_display_view);
        mSettingHintText = (TextView) findViewById(R.id.tv_setting_hint);
        //设置提示view 每行每列点的个数
        mLockDisplayView.setDotCount(3);
        //设置提示view 选中状态的颜色
        mLockDisplayView.setDotSelectedColor(Color.parseColor("#01A0E5"));
        //设置提示view 非选中状态的颜色
        mLockDisplayView.setDotUnSelectedColor(Color.TRANSPARENT);
        //设置手势解锁view 每行每列点的个数
        mGestureLockLayout.setDotCount(3);
        //设置手势解锁view 最少连接数
        mGestureLockLayout.setMinCount(3);
        //默认解锁样式为手Q手势解锁样式
        mGestureLockLayout.setLockView(new JDLockView(this));
        //设置手势解锁view 模式为重置密码模式
        mGestureLockLayout.setMode(GestureLockLayout.RESET_MODE);
    }

    private void initEvents() {
        mGestureLockLayout.setOnLockResetListener(new GestureLockLayout.OnLockResetListener() {
            @Override
            public void onConnectCountUnmatched(int connectCount, int minCount) {
                //连接数小于最小连接数时调用

                mSettingHintText.setText("最少连接" + minCount + "个点");
                resetGesture();
            }

            @Override
            public void onFirstPasswordFinished(List<Integer> answerList) {
                //第一次绘制手势成功时调用

                mSettingHintText.setText("确认解锁图案");
                //将答案设置给提示view
                mLockDisplayView.setAnswer(answerList);
                //重置
                resetGesture();
            }

            @Override
            public void onSetPasswordFinished(boolean isMatched, List<Integer> answerList) {
                //第二次密码绘制成功时调用

                if (isMatched) {
                    //两次答案一致，保存
                    mSettingHintText.setText("设置密码成功");
                    Toast.makeText(GestureLockActivity.this,"设置完成",Toast.LENGTH_SHORT).show();
                    SPUtils.setBooleanSp(GestureLockActivity.this,HAS_GESTURE_LOCK,true);
                    SPUtils.setStringSp(GestureLockActivity.this,GESTURE_LOCK_PASSWORD,getStringLockFormat(answerList));
                    restart();

                } else {
                    resetGesture();
                }
            }
        });
    }

    /**
     * 重置
     */
    private void resetGesture() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mGestureLockLayout.resetGesture();
            }
        }, 200);
    }
    private void restart(){
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mGestureLockLayout.setMode(GestureLockLayout.RESET_MODE);
                mGestureLockLayout.resetGesture();
            }
        },200);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    private String getStringLockFormat(List<Integer> list){
        StringBuilder sb= new StringBuilder("[");
        for(int i=0;i<list.size();i++){
            if (i==0){
                sb.append(list.get(i));
            }else {
                sb.append(",").append(list.get(i));
            }
        }
        sb.append("]");
        LoggerUtils.d("密码为:"+sb.toString());
        return sb.toString();
    }
}
