package com.example.aiy.finishproject.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

/**
 * <p>功能简述：
 * <p>Created by developer on 2017/11/15.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private Fragment[] mFragments;
    public ViewPagerAdapter(FragmentManager fm, Fragment[] fragments) {
        super(fm);
        mFragments=fragments;
    }

    public void setmFragments(Fragment[] mFragments) {
        this.mFragments = mFragments;
    }

    public Fragment[] getmFragments() {
        return mFragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments[position];
    }

    @Override
    public int getCount() {
        return mFragments.length;
    }
    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }
}
