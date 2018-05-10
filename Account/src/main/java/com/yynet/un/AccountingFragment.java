package com.yynet.un;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yynet.un.db.AccountDB;
import com.yynet.un.db.Sum;

import org.litepal.crud.DataSupport;
import org.litepal.crud.callback.FindMultiCallback;
import org.litepal.tablemanager.Connector;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import at.markushi.ui.CircleButton;

import static android.content.Context.MODE_PRIVATE;

/**
 * <p>功能简述：
 * <p>Created by zero on 2018/1/9.
 */

public class AccountingFragment extends Fragment {
    private List<AccountDB> accountDBList = new ArrayList<>();
    private RecyclerView ioItemRecyclerView;
    private IOItemAdapter adapter;
    private Button showBtn;
    private CircleButton addBtn;
    private ImageView headerImg;
    private TextView monthlyCost, monthlyEarn;

    private Sum sum = new Sum();
    private ImageButton mFirstBeginButton;
    private RelativeLayout relativeLayout;
    public static String PACKAGE_NAME;
    public static Resources resources;
    public static final int SELECT_GALLERY_PIC = 1;
    public DecimalFormat decimalFormat = new DecimalFormat("0.00");

    private static final String TAG = "MainActivity";
    private SimpleDateFormat formatItem = new SimpleDateFormat("yyyy.MM.dd");

    // 为recyclerView设置滑动动作
    private ItemTouchHelper.Callback mCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
            // 获得滑动位置
            final int position = viewHolder.getAdapterPosition();

            if (direction == ItemTouchHelper.RIGHT) {
                // 弹窗确认
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(getString(R.string.AccountingFragment_builder_message));

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.removeItem(position);
                        // 刷新界面
                        onResume();
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LinearLayout sonView = (LinearLayout) viewHolder.itemView;
                        TextView grandsonTextView = (TextView) sonView.findViewById(R.id.iotem_date);
                        // 判断是否应该显示时间
                        if (sonView.findViewById(R.id.date_bar).getVisibility() == View.VISIBLE)
                            GlobalVariables.setmDate("");
                        else GlobalVariables.setmDate(adapter.getItemDate(position));
                        adapter.notifyItemChanged(position);
                    }
                }).show();  // 显示弹窗
            }
        }
    };
    private ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mCallback);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.un_activity_main,container,false);
        Connector.getDatabase();


        PACKAGE_NAME = getActivity().getApplicationContext().getPackageName();
        resources = getResources();

        showBtn = (Button) view.findViewById(R.id.show_money_button);
        addBtn = (CircleButton) view.findViewById(R.id.add_button);
        ioItemRecyclerView = (RecyclerView) view.findViewById(R.id.in_and_out_items);
        headerImg = (ImageView) view.findViewById(R.id.header_img);
        monthlyCost = (TextView) view.findViewById(R.id.monthly_cost_money);
        monthlyEarn = (TextView) view.findViewById(R.id.monthly_earn_money);
        mFirstBeginButton= (ImageButton) view.findViewById(R.id.main_first_begin);
        // 设置按钮监听
        showBtn.setOnClickListener(new ButtonListener());
        addBtn.setOnClickListener(new ButtonListener());
        mFirstBeginButton.setOnClickListener(new ButtonListener());
        hasFirstBegin();
        // 设置首页header图片长按以更换图片
        headerImg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                selectPictureFromGallery();
                return false;
            }
        });

        setImageForHeader();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initIoItemList(getActivity());

        showBtn.setText(getString(R.string.show_balance));

        sum.setMoneyText(IOItemAdapter.MONTHLY_COST, monthlyCost);
        sum.setMoneyText(IOItemAdapter.MONTHLY_EARN, monthlyEarn);
    }
    private void hasFirstBegin(){
        SharedPreferences sp= getActivity().getSharedPreferences("first",MODE_PRIVATE);
        boolean isFirst=sp.getBoolean("IsFirst",true);
        if (isFirst){
            mFirstBeginButton.setVisibility(View.VISIBLE);
            SharedPreferences.Editor editor=sp.edit();
            editor.putBoolean("IsFirst",false);
            editor.apply();
        }
    }
    // 各个按钮的活动
    private class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int id=view.getId();
            if (id==R.id.add_button) {
                Intent intent = new Intent(getActivity(), AddItemActivity.class);
                startActivity(intent);
            }else if(id==R.id.show_money_button) {
                if (showBtn.getText() == getString(R.string.show_balance)) {
                    sum = DataSupport.find(Sum.class, IOItemAdapter.SUM);
                    if (sum==null){
                        sum=new Sum();
                        sum.setTotal(0.00);
                        sum.save();
                    }
                    String sumString = decimalFormat.format(sum.getTotal());
                    showBtn.setText(sumString);
                } else showBtn.setText(getString(R.string.show_balance));
            }else if (id==R.id.main_first_begin) {
                mFirstBeginButton.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECT_GALLERY_PIC:
                if (data == null) return;
                // 用户从图库选择图片后会返回所选图片的Uri
                Uri uri = data.getData();
                this.headerImg.setImageURI(uri);
                saveImageUri(uri);

                // 获取永久访问图片URI的权限
                int takeFlags = data.getFlags();
                takeFlags &=(Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                getActivity().getContentResolver().takePersistableUriPermission(uri, takeFlags);
                break;
        }
    }

    public void setRecyclerView(Context context) {
        GlobalVariables.setmDate("");        // 用于存储recyclerView的日期
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setStackFromEnd(true);    // 列表从底部开始展示，反转后从上方开始展示
        layoutManager.setReverseLayout(true);   // 列表反转
        ioItemRecyclerView.setLayoutManager(layoutManager);
        adapter = new IOItemAdapter(accountDBList);
        adapter.setOnClickListener(new IAdapterClick() {
            @Override
            public void onClick(AccountDB db) {
                AddItemActivity.newInstance(getActivity(),db);
            }
        });
        ioItemRecyclerView.setAdapter(adapter);
        itemTouchHelper.attachToRecyclerView(ioItemRecyclerView);
    }
    public void initIoItemList(final Context context) {
        DataSupport.findAllAsync(AccountDB.class).listen(new FindMultiCallback() {
            @Override
            public <T> void onFinish(List<T> t) {
                accountDBList = (List<AccountDB>) t;
                setRecyclerView(context);
            }
        });
    }

    public void selectPictureFromGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        // 设置选择类型为图片类型
        intent.setType("image/*");
        // 打开图片选择
        startActivityForResult(intent, SELECT_GALLERY_PIC);

    }
    // 利用SharedPreferences保存图片uri
    public void saveImageUri(Uri uri) {
        SharedPreferences pref = getActivity().getSharedPreferences("image", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = pref.edit();
        prefEditor.putString("uri", uri.toString());
        prefEditor.apply();
    }

    public void setImageForHeader() {
        SharedPreferences pref = getActivity().getSharedPreferences("image", MODE_PRIVATE);
        String imageUri = pref.getString("uri", "");

        if (!imageUri.equals("")) {
            Uri contentUri = Uri.parse(imageUri);
            this.headerImg.setImageURI(contentUri);
        }
    }



}
