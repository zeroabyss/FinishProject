package com.example.aiy.finishproject.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * <p>功能简述：
 * <p>Created by Aiy on 2018/4/17.
 */

public class SPUtils {
    public static boolean getBooleanSP(Context context,String key, boolean defaultValue){
        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(key,defaultValue);
    }

    public static void setBooleanSp(Context context,String key,boolean value){
        SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor=sp.edit();
        editor.putBoolean(key,value);
        editor.apply();
    }

    public static String getStringSP(Context context,String key, String defaultValue){
        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(key,defaultValue);
    }

    public static void setStringSp(Context context,String key,String value){
        SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString(key,value);
        editor.apply();
    }
}
