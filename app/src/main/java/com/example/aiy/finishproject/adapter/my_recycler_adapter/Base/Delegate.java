package com.example.aiy.finishproject.adapter.my_recycler_adapter.Base;

/**
 * <p>功能简述：Recycler的不同子项布局
 * <p>Created by developer on 2017/12/12.
 */

public interface Delegate<T>  {
    //返回Layout布局
    int getLayoutId();
    //相当于equals作用，判断是不是自身
    boolean isForItemType(T item, int position);
    //用于Adapter.onBindView
    void convert(ViewHolder holder, T item, int position);
}
