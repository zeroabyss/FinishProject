package com.yynet.un;

import android.support.percent.PercentRelativeLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yynet.un.db.AccountDB;
import com.yynet.un.db.Sum;

import org.litepal.crud.DataSupport;

import java.text.DecimalFormat;
import java.util.List;



public class IOItemAdapter extends RecyclerView.Adapter<IOItemAdapter.ViewHolder> {
    private static final String TAG = "IOItemAdapter";
    public static final int TYPE_COST = -1;
    public static final int TYPE_EARN =  1;
    public static final int MONTHLY_COST = 2;
    public static final int MONTHLY_EARN = 3;
    public static final int SUM = 1;

    private List<AccountDB> mAccountDBList;
    private String mDate;

    public DecimalFormat decimalFormat = new DecimalFormat("0.00");

    static class ViewHolder extends RecyclerView.ViewHolder {
        PercentRelativeLayout earnLayout, costLayout;
        RelativeLayout dateBar;

        ImageView itemImageEarn, itemImageCost;
        TextView itemNameEarn, itemNameCost;
        TextView itemMoneyEarn, itemMoneyCost;
        TextView itemDspEarn, itemDspCost;
        TextView itemDate;

        public ViewHolder(View view) {
            super(view);
            earnLayout = (PercentRelativeLayout) view.findViewById(R.id.earn_left_layout);
            costLayout = (PercentRelativeLayout) view.findViewById(R.id.cost_right_layout);
            dateBar    = (RelativeLayout) view.findViewById(R.id.date_bar);

            itemImageEarn = (ImageView) view.findViewById(R.id.earn_item_img_main);
            itemImageCost = (ImageView) view.findViewById(R.id.cost_item_img_main);
            itemNameEarn  = (TextView ) view.findViewById(R.id.earn_item_name_main);
            itemNameCost  = (TextView ) view.findViewById(R.id.cost_item_name_main);
            itemMoneyEarn = (TextView ) view.findViewById(R.id.earn_item_money_main);
            itemMoneyCost = (TextView ) view.findViewById(R.id.cost_item_money_main);
            itemDspEarn   = (TextView ) view.findViewById(R.id.earn_item_decription);
            itemDspCost   = (TextView ) view.findViewById(R.id.cost_item_decription);
            itemDate      = (TextView ) view.findViewById(R.id.iotem_date);
        }
    }

    public IOItemAdapter(List<AccountDB> accountDBList) {
        mAccountDBList = accountDBList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.un_io_item, parent ,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        AccountDB accountDB = mAccountDBList.get(position);
        showItemDate(holder, accountDB.getTimeStamp());
        // 表示支出的布局
        if (accountDB.getType() == TYPE_COST) {       // -1代表支出
            holder.earnLayout.setVisibility(View.GONE);
            holder.costLayout.setVisibility(View.VISIBLE);
            holder.itemImageCost.setImageResource(accountDB.getSrcId());
            holder.itemImageCost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "点击了支出");
                }
            });
            holder.itemNameCost.setText(accountDB.getName());
            holder.itemMoneyCost.setText(decimalFormat.format(accountDB.getMoney()));
            handleDescription(accountDB, holder.itemDspCost, holder.itemNameCost, holder.itemMoneyCost);
        //表示收入的布局
        } else if (accountDB.getType() == TYPE_EARN) {
            holder.earnLayout.setVisibility(View.VISIBLE);
            holder.costLayout.setVisibility(View.GONE);
            holder.itemImageEarn.setImageResource(accountDB.getSrcId());
            holder.itemImageEarn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "收入");
                }
            });
            holder.itemNameEarn.setText(accountDB.getName());
            holder.itemMoneyEarn.setText(decimalFormat.format(accountDB.getMoney()));
            handleDescription(accountDB, holder.itemDspEarn, holder.itemNameEarn, holder.itemMoneyEarn);
        }

    }

    @Override
    public int getItemCount() {
        return mAccountDBList.size();
    }

    // 利用全局变量进行判定
    public void showItemDate(ViewHolder holder, String Date) {
        if (GlobalVariables.getmDate().equals(Date)) holder.dateBar.setVisibility(View.GONE);
        else {
            holder.dateBar.setVisibility(View.VISIBLE);
            holder.itemDate.setText(Date);
            GlobalVariables.setmDate(Date);
            Log.d(TAG, "showItemDate: "+Date);
        }
    }

    // 返回子项目时间，便于在取消删除的时候判断是否应该显示项目时间
    public String getItemDate(int position) {
        AccountDB accountDB = mAccountDBList.get(position);
        return accountDB.getTimeStamp();
    }

    public void removeItem(int position) {
        AccountDB accountDB = mAccountDBList.get(position);
        Sum sum = DataSupport.find(Sum.class, 1);  // 1 代表sum;
        Sum month;
        int type = accountDB.getType();
        sum.setTotal(sum.getTotal()- accountDB.getMoney() * type);
        sum.save();
        // 判断收支类型
        if (type < 0) month = DataSupport.find(Sum.class, 2);     // 2 代表cost
        else month = DataSupport.find(Sum.class, 3);              // 3 代表earn
        month.setTotal(month.getTotal()- accountDB.getMoney());
        month.save();
        DataSupport.delete(AccountDB.class, mAccountDBList.get(position).getId());

        mAccountDBList.remove(position);
        notifyItemRemoved(position);
    }

    public boolean isThereADescription(AccountDB accountDB) {
        return (accountDB.getDescription()!=null && !accountDB.getDescription().equals(""));
    }

    public void handleDescription(AccountDB accountDB, TextView Dsp, TextView Name, TextView Money) {
        if (isThereADescription(accountDB)) {
            RelativeLayout.LayoutParams nameParams = (RelativeLayout.LayoutParams)Name.getLayoutParams();
            nameParams.removeRule(RelativeLayout.CENTER_VERTICAL);
            RelativeLayout.LayoutParams moneyParams = (RelativeLayout.LayoutParams)Money.getLayoutParams();
            moneyParams.removeRule(RelativeLayout.CENTER_VERTICAL);
            Dsp.setText(accountDB.getDescription());
            Name.setLayoutParams(nameParams);
            Money.setLayoutParams(moneyParams);
        } else {
            Dsp.setVisibility(View.GONE);
        }
    }
}