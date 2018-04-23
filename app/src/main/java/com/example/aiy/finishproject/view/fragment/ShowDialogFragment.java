package com.example.aiy.finishproject.view.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * <p>功能简述：
 * <p>Created by Aiy on 2018/4/10.
 */

public class ShowDialogFragment extends DialogFragment {
    private DialogInterface.OnClickListener mOkListener;
    private DialogInterface.OnClickListener mCancelListener;
    private String mTitle;
    private String message;

    public void setmOkListener(DialogInterface.OnClickListener mOkListener) {
        this.mOkListener = mOkListener;
    }

    public void setmCancelListener(DialogInterface.OnClickListener mCancelListener) {
        this.mCancelListener = mCancelListener;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(mTitle);
        builder.setMessage(message);
        builder.setPositiveButton("确定", mOkListener);
        builder.setNegativeButton("取消", mCancelListener);
        return builder.create();
    }
}
