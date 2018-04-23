package com.yynet.un.db;

import android.widget.TextView;

import org.litepal.crud.DataSupport;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Sum extends DataSupport {


    private double total = 0;
    private String name;
    private String date;
    private int id;

    private DecimalFormat decimalFormat = new DecimalFormat("0.00");
    //todo 修改日期格式
    private SimpleDateFormat formatSum  = new SimpleDateFormat("yyyy.MM");

    public void setTotal(double total) { this.total = total; }
    public void setName(String name) { this.name = name; }
    public void setDate(String date) { this.date = date; }
    public void setId(int id) { this.id = id; }

    public double getTotal() { return total; }
    public String getName() { return name; }
    public String getDate() { return date; }
    public int getId() { return id; }

    public void calculateMoney(int id, double money, int type){
        Sum sum = new Sum();
        sum = DataSupport.find(Sum.class, id);
        sum.setTotal(sum.getTotal() + money * type);
        sum.save();
    }

    public void calculateMoneyIncludeNull(int id,String name, double money, int type, String date) {
        Sum sum = new Sum();
        if (isThereASum(id)) {
            sum = DataSupport.find(Sum.class, id);
            sum.setTotal(sum.getTotal() + money * type);
            sum.save();
        } else {
            saveSum(sum, id, name, money, type, date);
        }
    }

    public boolean isThereASum(int id) {
        if (DataSupport.find(Sum.class, id) == null)
            return false;
        return true;
    }

    public void saveSum(Sum sum, int id, String name, double money, int type, String date) {
        sum.setId(id);
        sum.setName(name);
        sum.setTotal(money * type);
        setDate(date);
        sum.save();
    }

    // save without name
    public void saveSum(Sum sum, int id, double money, int type, String date) {
        sum.setId(id);
        sum.setTotal(money * type);
        setDate(date);
        sum.setName("single");
        sum.save();
    }

    public void setMoneyText(int id, TextView textView) {
        if (isThereASum(id)) {
            Sum sum =  DataSupport.find(Sum.class, id);
            if (!sum.getDate().equals(formatSum.format(new Date()))) {
                sum.saveSum(sum, id, 0.0, 1, formatSum.format(new Date()));
            }
            textView.setText(decimalFormat.format(sum.getTotal()));
        } else {
            textView.setText(decimalFormat.format(0));
        }
    }
}
