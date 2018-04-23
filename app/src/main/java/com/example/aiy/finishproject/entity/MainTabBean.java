package com.example.aiy.finishproject.entity;

import com.example.aiy.finishproject.R;

/**
 * <p>功能简述：
 * <p>Created by Aiy on 2018/4/9.
 */

public class MainTabBean {
    private final int[] TAB_IMAGE=new int[]{
            R.drawable.work1_tab_selector,
            R.drawable.work2_tab_selector,
            R.drawable.work3_tab_selector

    };
    private final int[] TAB_TITLE=new int[]{
            R.string.work1_tab_text,
            R.string.work2_tab_text,
            R.string.work3_tab_text
    };
    public int[] getTAB_IMAGE() {
        return TAB_IMAGE;
    }

    public int[] getTAB_TITLE() {
        return TAB_TITLE;
    }
}
