package com.yynet.un;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.commonlib.bdasr.IRecogListener;
import com.example.commonlib.bdasr.MessageStatusRecogListener;
import com.example.commonlib.bdasr.MyRecognizer;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import at.markushi.ui.CircleButton;
import me.leefeng.promptlibrary.PromptDialog;

import static com.example.commonlib.bdasr.IStatus.STATUS_FINISHED;

public class AddDescription extends AppCompatActivity {
    public static final int START_ASR=2001;
    public static final int STOP_ASR=2002;
     EditText inputTxt;
     TextView countTxt;
     TextView dateTxt;
     CircleButton doneBtn;
     CircleButton asrBtn;

    IRecogListener listener ;
    MyRecognizer myRecognizer ;
    Map<String, Object> params ;
    /**
     * 变量简述： ASR时间太短就不执行和录音一样
     */
    boolean mAsrTouchFlag=true;
    long mAsrFlagTime;
    private Handler mHandler=new MyASRHandler<>(this);
    PromptDialog dialog;
    //todo 修改日期格式 印尼dd.MM.yyyy
    private SimpleDateFormat formatItem = new SimpleDateFormat("yyyy.MM.dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.un_activity_add_descrpition);
        initAsr();
        dialog=new PromptDialog(this);
        inputTxt = (EditText) findViewById(R.id.page3_edit);
        countTxt = (TextView) findViewById(R.id.page3_count);
        dateTxt = (TextView) findViewById(R.id.page3_date);
        doneBtn = (CircleButton) findViewById(R.id.page3_done);
        asrBtn= (CircleButton) findViewById(R.id.add_description_asr_button);
        // 显示日期
        dateTxt.setText(formatItem.format(new Date()));

        // 获取焦点
        inputTxt.setFocusable(true);

        inputTxt.setText(GlobalVariables.getmDescription());
        inputTxt.setSelection(inputTxt.getText().length());
        countTxt.setText(String.valueOf(inputTxt.getText().length()) +"/30");

        // 设置输入文本监听，实时显示字数
        inputTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                countTxt.setText(String.valueOf(s.length())+"/30");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalVariables.setmDescription(inputTxt.getText().toString());
                finish();
            }
        });

        asrBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        mAsrFlagTime=System.currentTimeMillis();
                        mAsrTouchFlag=true;
                        Message message=mHandler.obtainMessage();
                        message.arg1=START_ASR;
                        mHandler.sendMessageDelayed(message,300);
                        break;
                    case MotionEvent.ACTION_UP:
                        long nowTime=System.currentTimeMillis();
                        if (nowTime- mAsrFlagTime <300){
                            mAsrTouchFlag=false;
                        }else {
                            Message message1 = mHandler.obtainMessage();
                            message1.arg1 = STOP_ASR;
                            mHandler.sendMessage(message1);
                        }
                }
                return true;
            }
        });
    }

    private static class MyASRHandler<T extends AddDescription> extends Handler {
        private final WeakReference<T> main;

        private MyASRHandler(T t) {
            this.main = new WeakReference<T>(t);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1){
                case STATUS_FINISHED:
                    String s= (String) msg.obj;
                    if ("ERROR".equals(s.trim())){
                        Toast.makeText(main.get(),"未能识别,请检测是否输入语句并且重试",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    main.get().inputTxt.getText().insert(main.get().inputTxt.getSelectionStart(),s.trim());
                    break;
                case START_ASR:
                    if (!main.get().mAsrTouchFlag) {
                        Toast.makeText(main.get(),"请长按开始语音转换文字",Toast.LENGTH_SHORT).show();
                        return;
                    }else {
                        main.get().dialog.getDefaultBuilder().stayDuration(Long.MAX_VALUE);
                        main.get().dialog.showCustom(R.drawable.ic_descripition_asr, "请开始说话", true);
                        main.get().startASR();
                    }
                    break;
                case STOP_ASR:
                    main.get().dialog.dismiss();
                    main.get().stopASR();
                    break;
            }
        }
    }
    private void initAsr(){
        params=new HashMap<>();
        params.put("accept-audio-data",false);
        params.put("disable-punctuation",false);
        params.put("accept-audio-volume",true);
        params.put("pid",1537);
        listener = new MessageStatusRecogListener(mHandler);
        myRecognizer = new MyRecognizer(this, listener);
    }

    void startASR(){
        myRecognizer.start(params);
    }

    void stopASR(){
        myRecognizer.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myRecognizer.release();
    }
}
