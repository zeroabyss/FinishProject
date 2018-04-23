package com.example.aiy.finishproject.Base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * <p>功能简述：
 * <p>Created by Aiy on 2018/4/9.
 */

public abstract class BaseFragment extends Fragment {
    protected abstract int getLayoutId();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(getLayoutId(),container,false);
        return view;
    }
}
