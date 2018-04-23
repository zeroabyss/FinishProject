package com.example.aiy.finishproject.adapter.my_recycler_adapter.Wrapper;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.example.aiy.finishproject.adapter.my_recycler_adapter.Base.ViewHolder;
import com.example.aiy.finishproject.adapter.my_recycler_adapter.util.AdapterUtils;


/**
 * <p>功能简述：为adapter添加头布局或者尾布局
 * <p>Created by developer on 2017/12/13.
 */

public class HeaderAndFooterWrapper<T> extends RecyclerView.Adapter {
    public static final int ITEM_TYPE_HEADER=10000;
    public static final int ITEM_TYPE_FOOTER=20000;
    private RecyclerView.Adapter adapter;
    private SparseArray<View> mHeaderViews;
    private SparseArray<View> mFooterViews;

    public HeaderAndFooterWrapper(RecyclerView.Adapter adapter) {
        this.adapter = adapter;
        mHeaderViews=new SparseArray<>();
        mFooterViews=new SparseArray<>();
    }

    @Override
    public int getItemViewType(int position) {
        //先判断位置是不是在头或者尾不然就默认adapter的位置
        if (isHeaderPosition(position))
            return mHeaderViews.keyAt(position);
        if (isFooterPosition(position))
            return mFooterViews.keyAt(position-getRealItemCount()-getHeaderCount());
        return adapter.getItemViewType(position-getHeaderCount());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderViews.get(viewType)!=null){
            return ViewHolder.newInstance(parent.getContext(),mHeaderViews.get(viewType));
        }else if (mFooterViews.get(viewType)!=null){
            return ViewHolder.newInstance(parent.getContext(),mFooterViews.get(viewType));
        }
        return adapter.onCreateViewHolder(parent,viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //这里是设置头尾没有事件，因为他们的集合是View，所以是定义好的view
        if (isHeaderPosition(position)) return;
        if (isFooterPosition(position)) return;
        adapter.onBindViewHolder(holder,position-getHeaderCount());
    }

    @Override
    public int getItemCount() {
        //总数应该是头+实际内容+尾
        return getFooterCount()+getHeaderCount()+getRealItemCount();
    }

    public int getHeaderCount(){
        return mHeaderViews.size();
    }

    public int getFooterCount(){
        return mFooterViews.size();
    }

    public int getRealItemCount(){
        return adapter.getItemCount();
    }
    /**
     * 方法简述： 判断是不是头
     */
    private boolean isHeaderPosition(int position){
        return position<getHeaderCount();
    }

    /**
     * 方法简述： 判断是不是尾
     */
    private boolean isFooterPosition(int position){
        return position>=(getHeaderCount()+getRealItemCount());
    }

    public void addHeader(View view){
        mHeaderViews.put(mHeaderViews.size()+ITEM_TYPE_HEADER,view);
    }

    public void addFooter(View view){
        mFooterViews.put(mFooterViews.size()+ITEM_TYPE_FOOTER,view);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        AdapterUtils.attach(adapter, recyclerView, new AdapterUtils.UtilCallback() {
            @Override
            public int getSpanSize(GridLayoutManager layoutManager, GridLayoutManager.SpanSizeLookup lookup, int position) {
                //先找到viewType类型，判断是不是头尾是的话占满一行
                int viewType=getItemViewType(position);
                if (mHeaderViews.get(viewType)!=null ||mFooterViews.get(viewType)!=null){
                    return layoutManager.getSpanCount();
                }
                //或者原本有定义也可以使用
                if (lookup!=null){
                    return lookup.getSpanSize(position);
                }
                //实在没有就返回默认值1
                return 1;
            }
        });
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        //adapter关联holder
        adapter.onViewAttachedToWindow(holder);
        int position=holder.getLayoutPosition();
        if (isHeaderPosition(position)||isFooterPosition(position)){
            AdapterUtils.setFullSpan(holder);
        }
    }
}
