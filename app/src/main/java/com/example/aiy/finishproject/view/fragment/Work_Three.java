package com.example.aiy.finishproject.view.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.aiy.finishproject.R;
import com.example.aiy.finishproject.view.activity.AboutActivity;
import com.example.aiy.finishproject.view.activity.GestureLockActivity;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.lang.ref.WeakReference;


/**
 * <p>功能简述：
 * <p>Created by developer on 2017/11/20.
 */

public class Work_Three extends Fragment implements View.OnClickListener{
    private Handler handler=new MyHandler(this);
    private ImageView about;
    private ImageView clear;
    private ImageView checkversion;
    private ProgressDialog mProgress;
    private KProgressHUD progressDialog;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_work3,container,false);
        clear=view.findViewById(R.id.work3_clear);
        about=view.findViewById(R.id.work3_about);
        checkversion =view.findViewById(R.id.work3_checkversion);
        clear.setOnClickListener(this);
        about.setOnClickListener(this);
        checkversion.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.work3_clear:
                progressDialog = KProgressHUD.create(getActivity())
                        .setMaxProgress(100)
                        .setDetailsLabel("清除缓存中")
                        .setStyle(KProgressHUD.Style.PIE_DETERMINATE)
                        .show();
                progressDialog(2);
                break;
            case R.id.work3_about:
                startActivity(new Intent(getActivity(), AboutActivity.class));
                break;
            case R.id.work3_checkversion:
                /*progressDialog = KProgressHUD.create(getActivity())
                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setCancellable(true)
                        .setLabel("请稍等")
                        .setAnimationSpeed(3)
                        .show();
                progressDialog(3);*/
                startActivity(new Intent(getActivity(), GestureLockActivity.class));
                break;
        }
    }

    private void progressDialog(final int msgWhat) {
        new Thread(new Runnable() {
            int progress = 0;

            @Override
            public void run() {
                while (progress <= 100) {

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    progress++;
                    Message message = handler.obtainMessage();
                    message.what = 1;
                    message.arg1 = progress;
                    handler.sendMessage(message);
                }
                Message message = handler.obtainMessage();
                message.what = msgWhat;
                handler.sendMessage(message);

            }
        }).start();
    }

    private static class MyHandler extends Handler {
        private final WeakReference<Work_Three> mWork_Three;

        public MyHandler(Work_Three work_Three) {
            mWork_Three = new WeakReference<Work_Three>(work_Three);
        }

        @Override
        public void handleMessage(Message msg) {
            Work_Three work_three = mWork_Three.get();
            if (work_three == null) return;
            switch (msg.what) {
                case 1:
                    work_three.progressDialog.setProgress(msg.arg1);
                    break;
                case 2:
                    Toast.makeText(work_three.getActivity(), "清理完成", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    work_three.progressDialog.dismiss();
                    Toast.makeText(work_three.getActivity(), "已经是最新版本", Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    }


}
