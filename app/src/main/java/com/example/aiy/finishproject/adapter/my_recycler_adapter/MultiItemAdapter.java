package com.example.aiy.finishproject.adapter.my_recycler_adapter;

import android.content.Context;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.aiy.finishproject.adapter.my_recycler_adapter.Base.Delegate;
import com.example.aiy.finishproject.adapter.my_recycler_adapter.Base.DelegateManager;
import com.example.aiy.finishproject.adapter.my_recycler_adapter.Base.ViewHolder;

import java.util.List;


/**
 * <p>功能简述：通用型Adapter
 * <p>Created by developer on 2017/12/12.
 */

public class MultiItemAdapter<T> extends RecyclerView.Adapter<ViewHolder> {
    private Context mContext;
    private List<T> mList;
    private DelegateManager<T> manager;
    /**
     * 变量简述： 点击事件，如果需要有不同item那就使用delegate里面的convert设置事件
     */
    private onItemClickListener<T> listener;

    public interface onItemClickListener<T>{
        void onClick(T bean, int position);
        void onLongClick();
    }

    public void setListener(onItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (manager.getManagerSize()>0)
            return manager.getItemType(mList.get(position),position);

        return super.getItemViewType(position);
    }

    public MultiItemAdapter(Context mContext, List<T> mList) {
        this.mContext = mContext;
        this.mList = mList;
        manager=new DelegateManager<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final int layoutId=manager.getLayoutId(viewType);
        View view= LayoutInflater.from(mContext).inflate(layoutId,parent,false);
        final ViewHolder holder=ViewHolder.newInstance(mContext,view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener!=null)
                    listener.onClick(mList.get(holder.getAdapterPosition()),holder.getAdapterPosition());
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (listener!=null) listener.onLongClick();
                return false;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        manager.convert(holder,mList.get(position),position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void addDelegate(Delegate<T> delegate){
        manager.addDelegate(delegate);
    }

    public void addDelegate(int itemType,Delegate<T> delegate){
        manager.addDelegate(itemType,delegate);
    }
    public void removeDelegate(int itemType){
        manager.removeDelegate(itemType);
    }
}
