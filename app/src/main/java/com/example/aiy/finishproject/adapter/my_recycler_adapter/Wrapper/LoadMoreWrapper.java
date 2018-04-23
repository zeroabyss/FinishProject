package com.example.aiy.finishproject.adapter.my_recycler_adapter.Wrapper;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.example.aiy.finishproject.adapter.my_recycler_adapter.Base.ViewHolder;
import com.example.aiy.finishproject.adapter.my_recycler_adapter.util.AdapterUtils;


/**
 * <p>功能简述：加载更多内容，拉到底部的时候显示
 * <p>Created by developer on 2017/12/13.
 */

public class LoadMoreWrapper extends RecyclerView.Adapter {
    //作为viewType
    private static final int ITEM_TYPE_LOAD_MORE=Integer.MAX_VALUE-2;
    //把需要包装的adapter传进来
    private RecyclerView.Adapter mInnerAdapter;
    /**
     * 方法简述： 加载布局
     */
    private int mLayoutId;
    /**
     * 方法简述： 加载视图
     */
    private View mLoadMoreView;
    /**
     * 方法简述： 加载的回调
     */
    private OnLoadRequested onLoadRequested;
    public interface OnLoadRequested{
        void requested();
    }

    public void setOnLoadRequested(OnLoadRequested onLoadRequested) {
        this.onLoadRequested = onLoadRequested;
    }

    public LoadMoreWrapper(RecyclerView.Adapter mInnerAdapter) {
        this.mInnerAdapter = mInnerAdapter;
    }
    /**
     * 方法简述： 拥有布局或者视图其中一个
     */
    private boolean hasLoadMore(){
        return mLoadMoreView != null || mLayoutId != 0;
    }
    private boolean isShowLoadMore(int position){
        //位置要在最后
        return hasLoadMore()&&position>=mInnerAdapter.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        //同理判断是不是最后位置
        if (isShowLoadMore(position))
            return ITEM_TYPE_LOAD_MORE;
        return mInnerAdapter.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       if (viewType==ITEM_TYPE_LOAD_MORE) {
           if (mLayoutId != 0)
               return ViewHolder.newInstance(parent.getContext(), parent, mLayoutId);
           else
               return ViewHolder.newInstance(parent.getContext(), mLoadMoreView);
       }
       return mInnerAdapter.onCreateViewHolder(parent,viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (isShowLoadMore(position)){
                if (onLoadRequested!=null) {
                    onLoadRequested.requested();
                    return;
                }
            }
            mInnerAdapter.onBindViewHolder(holder,position);
    }

    @Override
    public int getItemCount() {
        //有加载布局就+1
        return mInnerAdapter.getItemCount()+(hasLoadMore()?1:0);
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        mInnerAdapter.onViewAttachedToWindow(holder);
        if (isShowLoadMore(holder.getLayoutPosition()))
            AdapterUtils.setFullSpan(holder);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        AdapterUtils.attach(mInnerAdapter, recyclerView, new AdapterUtils.UtilCallback() {
            @Override
            public int getSpanSize(GridLayoutManager layoutManager, GridLayoutManager.SpanSizeLookup lookup, int position) {
                if (isShowLoadMore(position))
                    return layoutManager.getSpanCount();
                if (lookup!=null)
                    return lookup.getSpanSize(position);
                return 1;
            }
        });
    }

    public void setmLayoutId(int mLayoutId) {
        this.mLayoutId = mLayoutId;
    }

    public void setmLoadMoreView(View mLoadMoreView) {
        this.mLoadMoreView = mLoadMoreView;
    }
}
