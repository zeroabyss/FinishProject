package com.yynet.un;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.commonlib.util.LoggerUtils;
import com.merhold.extensiblepageindicator.ExtensiblePageIndicator;
import com.yynet.un.db.AccountDB;

import java.util.ArrayList;
import java.util.List;



public class CostFragment extends Fragment {
    //todo 修改此处支出类别
    private static final String SRC_NAME="src_name";
    private static final String EDIT_MODEL="edit_model";
    //1.印尼 2.英文 3.中文
   /* private String[] titles = {"Biasa", "Makan", "camilan", "taksi", "Deposito", "Perbelanjaan", "Hiburan", "Perumahan", "Minum", "Belanja online",
            "Sepatu", "Pemeliharaan", "Rias", "Film", "Pengeluaran", "Boros", "Latihan", "Dokter", "Perjalanan", "Pendidikan", "Rokok", "Anggur", "Digital", "Hadiah",
            "Keluarga", "Pet", "Pakaian", "sehari-hari", "Buah", "bayi", "Kartu kredit", "Manajemen", "Bekerja", "mebel", "Komunikasi"};*/
   /*private String[] titles = {"Ordinary","Eat","snack","taxi","Deposit","Shopping","Entertainment","Housing","Drink","Online shopping","Shoes","Maintenance","Rias","Film","Spending","Boros","Exercise","Doctor","Travel","Education","Cigarettes","Wine","Digital","Gift",
           "Family","Pet","Clothes","daily","Fruit","baby","Credit card","Management","Work","furniture","Communication"};*/
    private String[] titles = {"一般", "用餐", "零食", "交通", "充值", "购物", "娱乐", "住房", "饮料", "网购", "鞋帽", "护肤", "化妆", "电影", "转账", "浪费", "健身", "医疗", "旅游", "教育", "香烟", "酒水", "数码", "捐献", "家庭", "宠物", "服装", "日用", "水果", "母婴", "信用卡", "理财", "工作", "家具", "通信"};
    private ViewPager mPager;
    private List<View> mPagerList;
    private List<AccountDB> mDatas;
    private LayoutInflater inflater;
    private ImageView itemImage;
    private TextView itemTitle;
    private RelativeLayout itemLayout;
    private ExtensiblePageIndicator extensiblePageIndicator;

    // 总的页数
    private int pageCount;

    // 每一页显示的个数
    private int pageSize = 12;

    // 当前显示的是第几页
    private int curIndex = 0;

    private static final String TAG = "CostFragment";
    private boolean isEditModel;
    public static CostFragment newInstance(String srcName){
        CostFragment costFragment=new CostFragment();
        Bundle bundle=new Bundle();
        bundle.putString(SRC_NAME,srcName);
        bundle.putBoolean(EDIT_MODEL,true);
        costFragment.setArguments(bundle);
        return costFragment;
    }
    public static CostFragment newInstance(){
        CostFragment costFragment=new CostFragment();
        Bundle bundle=new Bundle();
        bundle.putBoolean(EDIT_MODEL,false);
        costFragment.setArguments(bundle);
        return costFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: start");
        isEditModel=getArguments().getBoolean(EDIT_MODEL,false);
        // 获得AddItemActivity对应的控件，用来提示已选择的项目类型
        getBannerId();

        View view = inflater.inflate(R.layout.un_cost_fragment, container, false);

        mPager = (ViewPager) view.findViewById(R.id.viewpager_1);
        extensiblePageIndicator = (ExtensiblePageIndicator) view.findViewById(R.id.ll_dot_1);


        int height = mPager.getHeight();
        int width = mPager.getWidth();

        // 初始化数据源
        initDatas();

        int position;
        if (isEditModel){
            position=subSrcNameToNum(getArguments().getString(SRC_NAME));
            LoggerUtils.d("banner的位置"+position);
            changeBanner(mDatas.get(position-1));
        }else {
            // 初始化上方banner
            changeBanner(mDatas.get(0));
        }


        // 总的页数=总数/每页数量，并取整
        pageCount = (int) Math.ceil(mDatas.size() * 1.0 / pageSize);
        mPagerList = new ArrayList<View>();
        for (int i = 0; i < pageCount; i++) {
            RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.un_item_recycler_grid, mPager ,false);
            MyGridLayoutManager layoutManager = new MyGridLayoutManager(getContext(), 4);
            recyclerView.setLayoutManager(layoutManager);
            GridRecyclerAdapter adaper = new GridRecyclerAdapter(mDatas, i, pageSize);
            recyclerView.setAdapter(adaper);

            mPagerList.add(recyclerView);

            adaper.setOnItemClickListener(new GridRecyclerAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    changeBanner(mDatas.get(position));
                }
            });
        }
        // 设置适配器
        mPager.setAdapter(new ViewPagerAdapter(mPagerList));
        extensiblePageIndicator.initViewPager(mPager);

        return view;
    }

    /**
     * 初始化数据源
     */
    private void initDatas() {
        mDatas = new ArrayList<AccountDB>();
        for (int i = 1; i <= titles.length; i++) {
            mDatas.add(new AccountDB("type_big_" + i, titles[i-1]));
        }
    }


    // 获得AddItemActivity对应的控件，用来提示已选择的项目类型
    public void getBannerId() {
        itemImage = (ImageView) getActivity().findViewById(R.id.chosen_image);
        itemTitle = (TextView) getActivity().findViewById(R.id.chosen_title);
        itemLayout = (RelativeLayout) getActivity().findViewById(R.id.have_chosen);
    }

    // 改变banner状态
    public void changeBanner(AccountDB tmpItem) {
        Bitmap bm = BitmapFactory.decodeResource(getResources(), tmpItem.getSrcId());
        Palette.Builder pb = new Palette.Builder(bm);
        pb.maximumColorCount(1);


        itemImage.setImageResource(tmpItem.getSrcId());
        itemTitle.setText(tmpItem.getName());
        itemImage.setTag(-1);                        // 保留图片资源属性，-1表示支出
        itemTitle.setTag(tmpItem.getSrcName());      // 保留图片资源名称作为标签，方便以后调用

        // 获取图片颜色并改变上方banner的背景色
        pb.generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                Palette.Swatch swatch = palette.getSwatches().get(0);
                if (swatch != null) {
                    itemLayout.setBackgroundColor(swatch.getRgb());
                } else {
                    Log.d(TAG, "changeBanner: ");
                }
            }
        });
    }

    private int subSrcNameToNum(String srcName){
        String subString=srcName.substring(9);
        return Integer.parseInt(subString);
    }
}