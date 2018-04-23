package com.example.aiy.finishproject.adapter.my_recycler_adapter.Base;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * <p>功能简述：通用的viewHolder
 * <p>Created by developer on 2017/12/12.
 */

public class ViewHolder extends RecyclerView.ViewHolder {
    //存放子View
    private SparseArray<View> mViews;
    private Context mContext;
    //ConvertView,主View
    private View mConvertView;

    private ViewHolder(Context context,View itemView) {
        super(itemView);
        this.mContext=context;
        this.mConvertView=itemView;
        mViews=new SparseArray<>();
    }
    /**
     * 方法简述： 静态工厂 ,通过convertView或者parent,layoutId其实原理是一样的.
     */
    public static ViewHolder newInstance(Context context,View convertView){
        return new ViewHolder(context,convertView);
    }
    public static ViewHolder newInstance(Context context, ViewGroup parent,int layoutId){
        View convertView= LayoutInflater.from(context).inflate(layoutId,parent,false);
        return new ViewHolder(context,convertView);
    }
    /**
     * 方法简述： 获取view
     * @param viewId R.id.xxx
     */
    public <T extends View> T getView(int viewId){
        Log.d("ViewHolder", "viewId:" + viewId);
        View view= mViews.get(viewId);
        if (view==null){
            view=itemView.findViewById(viewId);
            Log.d("ViewHolder", "view:" + view);
            mViews.put(viewId,view);
        }
        return (T) view;
    }

    public View getConvertView() {
        return mConvertView;
    }

    //以下都是辅助方法
    public void setConvertViewListener(View.OnClickListener listener){
        mConvertView.setOnClickListener(listener);
    }

    public ViewHolder setImageDrawable(int imageId, int drawableId){
        Log.d("ViewHolder", "set_imageId:" + imageId);
        ImageView imageView=getView(imageId);
        if (imageView!=null)
            Glide.with(mContext).load(drawableId).into(imageView);

        return this;
    }
    public ViewHolder setImageDrawable(int imageId, int drawableId, View.OnClickListener listener){
        ImageView imageView=getView(imageId);
        if (imageView!=null) {
            imageView.setImageDrawable(ContextCompat.getDrawable(mContext, drawableId));
            imageView.setOnClickListener(listener);
        }
        return this;
    }

    public ViewHolder setTextDrawable(int textViewId,CharSequence s){
        TextView textView=getView(textViewId);
        if (textView!=null)
            textView.setText(s);
        return this;
    }
}
