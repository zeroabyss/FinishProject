package com.example.aiy.finishproject.view.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aiy.finishproject.Base.BaseActivity;
import com.example.aiy.finishproject.R;
import com.example.aiy.finishproject.adapter.ViewPagerAdapter;
import com.example.aiy.finishproject.entity.MainTabBean;
import com.example.aiy.finishproject.util.ActivityUtils;
import com.example.aiy.finishproject.view.fragment.NoteFragment;
import com.example.aiy.finishproject.view.fragment.Work_Three;
import com.yynet.un.AccountingFragment;

public class MainActivity extends BaseActivity {
    private Fragment[] mFragments=new Fragment[]{new NoteFragment(),new AccountingFragment(),new Work_Three()};


    private FragmentManager mFragmentManager;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private MainTabBean mTabBean;

    private boolean mMenuCheck=true;
    private Menu mMenu;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_mainlayout;
    }



    private void initViewPagerAndTabLayout(){
        mTabLayout =findViewById(R.id.Main_TabLayout);
        mViewPager=findViewById(R.id.Main_ViewPager);
        mFragmentManager=getSupportFragmentManager();
        mViewPagerAdapter=new ViewPagerAdapter(mFragmentManager,mFragments);
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        mTabBean=new MainTabBean();
        setTabs(mTabLayout, this.getLayoutInflater(), mTabBean.getTAB_TITLE(), mTabBean.getTAB_IMAGE());
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition()==0){
                    mMenuCheck=true;
                    checkOptionsMenu();
                }
                if (tab.getPosition()==1){
                    mMenuCheck=false;
                    checkOptionsMenu();
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void setTabs(TabLayout tabLayout, LayoutInflater inflater, int[] titles, int[] images) {
        for (int i = 0; i < titles.length; i++) {
            TabLayout.Tab tab = tabLayout.newTab();
            View view = inflater.inflate(R.layout.image_and_text_tab_custom, null);
            tab.setCustomView(view);
            TextView textView = view.findViewById(R.id.tab_custom_tv);
            textView.setText(titles[i]);
            ImageView imageView = view.findViewById(R.id.tab_custom_iv);
            imageView.setImageResource(images[i]);
            tabLayout.addTab(tab);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewPagerAndTabLayout();

    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mMenu=menu;
        checkOptionsMenu();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.note_add_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.note_add_button){
            ActivityUtils.startActivity(this,NoteAddActivity.class);
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkOptionsMenu(){
        if(mMenu==null)
            return;
        if (mMenuCheck){
            for(int i=0;i<mMenu.size();i++){
                MenuItem item=mMenu.getItem(i);
                item.setVisible(true);
                item.setEnabled(true);
            }
        }else{
            for(int i=0;i<mMenu.size();i++){
                MenuItem item=mMenu.getItem(i);
                item.setVisible(false);
                item.setEnabled(false);
            }
        }
    }
}
