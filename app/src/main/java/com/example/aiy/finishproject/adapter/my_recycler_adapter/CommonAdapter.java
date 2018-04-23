package com.example.aiy.finishproject.adapter.my_recycler_adapter;

import android.content.Context;


import com.example.aiy.finishproject.adapter.my_recycler_adapter.Base.Delegate;
import com.example.aiy.finishproject.adapter.my_recycler_adapter.Base.ViewHolder;

import java.util.List;


/**
 * <p>功能简述：通用型单布局
 * <p>Created by developer on 2017/12/13.
 */

public abstract class CommonAdapter<T> extends MultiItemAdapter<T> {
    private int mLayoutId;

    public CommonAdapter(Context mContext, List<T> mList,  int mLayoutId) {
        super(mContext, mList);
        this.mLayoutId = mLayoutId;
        addDelegate(new Delegate<T>() {
            @Override
            public int getLayoutId() {
                return CommonAdapter.this.mLayoutId;
            }

            @Override
            public boolean isForItemType(T item, int position) {
                //因为是单布局，所以不需要什么特殊判断条件直接返回true
                return true;
            }

            @Override
            public void convert(ViewHolder holder, T item, int position) {
                CommonAdapter.this.convert(holder,item,position);
            }
        });
    }
    //将绑定方法给客户端调用
    public abstract void convert(ViewHolder holder,T item,int position);
}
