package com.example.commonlib.util;

import android.util.Log;

/**
 * <p>功能简述：
 * <p>Created by Aiy on 2018/4/11.
 */

public class LoggerUtils {
    private static final int level=5;
    private static final String TAG = "LoggerUtils";
    private static final int VERBOSE=0;
    private static final int DEBUG=1;
    private static final int INFO=2;
    private static final int WARN=3;
    private static final int ERROR=4;
    public static void d(String message){
        if (level>=DEBUG){
            Log.d(TAG, message);
        }
    }

    public static void v(String message){
        if (level>=VERBOSE){
            Log.v(TAG,message);
        }
    }

    public static void w(String message){
        if (level>=WARN){
            Log.w(TAG, message );
        }
    }

    public static void e(String message){
        if (level>=ERROR){
            Log.e(TAG, message );
        }
    }

    public static void i(String message){
        if (level>=INFO){
            Log.i(TAG,message);
        }
    }
}
