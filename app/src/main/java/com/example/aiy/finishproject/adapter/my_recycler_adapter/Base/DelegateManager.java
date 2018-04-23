package com.example.aiy.finishproject.adapter.my_recycler_adapter.Base;

import android.support.v4.util.SparseArrayCompat;

/**
 * <p>功能简述：Delegate的集合管理类
 * <p>Created by developer on 2017/12/12.
 */

public class DelegateManager<T>  {
    //跟SparseArray一样，多了一个removeAt()，这是源码注释说的。
    private SparseArrayCompat<Delegate<T>> mDelegates;

    public DelegateManager() {
        mDelegates =new SparseArrayCompat<>();
    }

    /**
     * 方法简述： 添加一个delegate
     */
    public void addDelegate(Delegate<T> delegate){
        if (delegate==null) return;
        //没有指定位置就默认加到最后
        int position= mDelegates.size();
        mDelegates.put(position,delegate);
    }
    /**
     * 方法简述： 添加一个delegate
     * @param viewType 存放位置
     */
    public void addDelegate(int viewType,Delegate<T> delegate){
        //该位置已存在
        if (mDelegates.get(viewType)!=null){
            throw new  IllegalArgumentException("delegate is registered,viewType is"+viewType+"and Delegate is "+ mDelegates.get(viewType));
        }
        mDelegates.put(viewType,delegate);
    }
    /**
     * 方法简述： 删除Delegate
     */
    public void removeDelegate(Delegate<T> delegate){
        if (delegate==null) return;
        int position= mDelegates.indexOfValue(delegate);
        //不存在返回-1
        if (position>=0){
            mDelegates.removeAt(position);
        }
    }

    public void removeDelegate(int typeView){
        int position= mDelegates.indexOfKey(typeView);
        if (position>=0) mDelegates.removeAt(position);
    }

    /**
     * 方法简述： 获取对应delegate的布局id
     */
    public int getLayoutId(int itemType){
        return getDelegate(itemType).getLayoutId();
    }
    public int getManagerSize(){
        return mDelegates.size();
    }

    public Delegate<T> getDelegate(int itemType){
        int position= mDelegates.indexOfKey(itemType);
        if (position>=0) return mDelegates.get(position);
        return null;
    }
    /**
     * 方法简述： 遍历mDelegates，匹配每个delegate的判断方法isForItemType
     */
    public  int getItemType(T item,int position){
        int size= mDelegates.size();
        for (int i=size - 1;i>=0;i--){
            Delegate<T> delegate= mDelegates.valueAt(i);
            if (delegate.isForItemType(item,position)){
                return mDelegates.keyAt(i);
            }
        }
        //全部遍历都没有找到对应的delegate
        throw new IllegalArgumentException("manager的getItemType()里面没有对应的itemType"+item+"位置"+position);
    }
    /**
     * 方法简述： adapter调用管理类的convert,管理类调用子项的convert
     */
    public void convert(ViewHolder holder,T item,int position){
        for (int i = 0; i<= mDelegates.size()-1; i++){
            Delegate<T> delegate= mDelegates.valueAt(i);
            if (delegate.isForItemType(item,position)){
                delegate.convert(holder,item,position);
                return;
            }
        }
        throw new IllegalArgumentException("manager的convert()找不到对应子项可以执行 item:"+item+"位置:"+position);
    }

}
