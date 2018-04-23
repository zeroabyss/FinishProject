package com.yynet.un.db;

import android.content.res.Resources;

import com.yynet.un.AccountingFragment;

import org.litepal.crud.DataSupport;



public class AccountDB extends DataSupport {

    private int id;
    private int type;                       // 收入还支出
    private double money;
    private String name;//具体类别
    private String description;
    private String timeStamp;
    private String srcName;                 // 图标名称

    public AccountDB() {}

    public AccountDB(String srcName, String name) {
        this.srcName = srcName;
        this.name = name;
    }

    // 构造函数（无具体描述）
    public AccountDB(String srcName, int type, double money, String name) {
        this.srcName = srcName;
        this.money = money;
        this.type = type;
        this.name = name;
    }

    // 构造函数（有具体描述）
    public AccountDB(String srcName, int type, double money, String name, String description) {
        this.money = money;
        this.type = type;
        this.srcName = srcName;
        this.name = name;
        this.description = description;
    }

    public double getMoney()                       { return money; }
    public int getType()                           { return type; }
    public String getName()                        { return name; }
    public String getDescription()                 { return description; }
    public String getTimeStamp()                   { return timeStamp; }
    public String getSrcName()                     { return srcName; }
    public int getId()                             { return id; }

    // 设定属性
    public void setMoney(double money)             { this.money = money; }
    public void setType(int type)                  { this.type = type; }
    public void setName(String name)               { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setTimeStamp(String timeStamp)     { this.timeStamp = timeStamp; }
    public void setSrcName(String srcName)         { this.srcName = srcName; }

    // 返回图片资源的id
    public int getSrcId() {
        Resources resources = AccountingFragment.resources;
        return resources.getIdentifier(srcName, "drawable", AccountingFragment.PACKAGE_NAME);
    }
}
