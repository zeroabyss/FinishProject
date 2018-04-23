package com.example.aiy.finishproject.adapter.my_recycler_adapter.util;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.ViewGroup;

/**
 * <p>功能简述：用于包装类wrapper
 * <p>Created by developer on 2017/12/13.
 */

public class AdapterUtils {

    public interface UtilCallback{
        /**
         * 接口简述：设置新的GridLayoutManager.SpanSizeLookUp
         * @param layoutManager recyclerView的布局必须是Grid.
         * @param lookup 原本的lookup
         * @param position 位置，根据位置不同来返回不同的SpanSize值.
         */
        int getSpanSize(GridLayoutManager layoutManager, GridLayoutManager.SpanSizeLookup lookup, int position);
    }
    /**
     * 方法简述： onAttachedToRecyclerView方法里调用
     */
    public static void attach(RecyclerView.Adapter adapter, RecyclerView recyclerView, final UtilCallback callback){
        //因为包装类里面adapter是逐步增大的，所以要把内部的adapter绑定到recyclerView里面
        adapter.onAttachedToRecyclerView(recyclerView);

        RecyclerView.LayoutManager lm=recyclerView.getLayoutManager();
        //如果是Grid就设置新的SpanSizeLookUp
        if (lm instanceof GridLayoutManager){
            final GridLayoutManager gridLayoutManager= (GridLayoutManager) lm;
            final GridLayoutManager.SpanSizeLookup oldLookUp=gridLayoutManager.getSpanSizeLookup();
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return callback.getSpanSize(gridLayoutManager,oldLookUp,position);
                }
            });
            gridLayoutManager.setSpanCount(gridLayoutManager.getSpanCount());
        }
    }
    /**
     * 方法简述： onViewAttachedToWindow方法内调用，瀑布流的布局需要调用setFullSpan（true）
     */
    public static void setFullSpan(RecyclerView.ViewHolder viewHolder){
        ViewGroup.LayoutParams lp= viewHolder.itemView.getLayoutParams();
        if (lp!=null && lp instanceof StaggeredGridLayoutManager.LayoutParams){
            StaggeredGridLayoutManager.LayoutParams p= (StaggeredGridLayoutManager.LayoutParams) lp;
            p.setFullSpan(true);
        }
    }
}
