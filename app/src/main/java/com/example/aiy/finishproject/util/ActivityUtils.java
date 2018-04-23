package com.example.aiy.finishproject.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * <p>功能简述：
 * <p>Created by Aiy on 2018/4/9.
 */

public class ActivityUtils {
    public static void startActivity(Activity last, Class next){
            last.startActivity(new Intent(last,next));
    }
}
