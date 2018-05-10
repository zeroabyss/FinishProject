package com.yynet.un;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.commonlib.util.LoggerUtils;
import com.yynet.un.db.AccountDB;
import com.yynet.un.db.Sum;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddItemActivity extends AppCompatActivity {
    private static final String TAG = "AddItemActivity";
    public static final String EDIT_DB="edit_db";
    public static final String EDIT_MODEL="edit_model";
    private FragmentManager manager;
    private FragmentTransaction transaction;

    private Button addCostBtn;
    private Button addEarnBtn;
    private Button clearBtn;
    private Button addFinishBtn;
    private ImageButton addDescription;


    private ImageView bannerImage;
    private TextView bannerText;

    private TextView moneyText;

    private TextView words;
    //todo 修改日期格式
    private SimpleDateFormat formatItem = new SimpleDateFormat("yyyy.MM.dd");
    private SimpleDateFormat formatSum  = new SimpleDateFormat("yyyy.MM");
    private DecimalFormat decimalFormat = new DecimalFormat("0.00");

    private boolean isEditModel;
    private int mType;

    private double mLastMoney;
    private int mLastType;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.un_activity_add_item);

        addCostBtn = (Button) findViewById(R.id.add_cost_button);
        addEarnBtn = (Button) findViewById(R.id.add_earn_button);
        addFinishBtn   = (Button) findViewById(R.id.add_finish);
        addDescription = (ImageButton) findViewById(R.id.add_description);
        clearBtn = (Button) findViewById(R.id.clear);
        //words = (TextView) findViewById(R.id.anime_words);
        // 设置字体颜色
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/chinese_character.ttf");
        clearBtn.setTypeface(typeface);
        //words.setTypeface(typeface);
        // 设置按钮监听
        addCostBtn.setOnClickListener(new ButtonListener());
        addEarnBtn.setOnClickListener(new ButtonListener());
        addFinishBtn.setOnClickListener(new ButtonListener());
        addDescription.setOnClickListener(new ButtonListener());
        clearBtn.setOnClickListener(new ButtonListener());


        bannerText = (TextView) findViewById(R.id.chosen_title);
        bannerImage = (ImageView) findViewById(R.id.chosen_image);

        moneyText = (TextView) findViewById(R.id.input_money_text);
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();

        isEditModel=getIntent().getBooleanExtra(EDIT_MODEL,false);
        if (isEditModel){
            initEditModel();

        }else {
            // 及时清零
            moneyText.setText("0.00");
            transaction.replace(R.id.item_fragment, CostFragment.newInstance());
        }

        transaction.commit();

    }

    private void initEditModel(){
        AccountDB db= (AccountDB) getIntent().getSerializableExtra(EDIT_DB);
        GlobalVariables.setmId(db.getId());
        String s=Double.toString(db.getMoney());
        mLastMoney=db.getMoney();
        mLastType=db.getType();
        GlobalVariables.setmInputMoney(s);
        GlobalVariables.setmDescription(db.getDescription());
        GlobalVariables.setmDate(db.getTimeStamp());
        moneyText.setText(s);
        mType=db.getType();
        if (s.contains(".")) GlobalVariables.setHasDot(true);

        if (mType==IOItemAdapter.TYPE_COST)
            transaction.replace(R.id.item_fragment, CostFragment.newInstance(db.getSrcName()));
        else{
            addEarnBtn.setTextColor(0xffff8c00); // 设置“收入“按钮为灰色
            addCostBtn.setTextColor(0xff908070); // 设置“支出”按钮为橙色
            transaction.replace(R.id.item_fragment,EarnFragment.newInstance(db.getSrcName()));
        }
    }


    public static void newInstance(Context context,AccountDB db){
        Intent intent=new Intent(context,AddItemActivity.class);
        Bundle bundle=new Bundle();
        bundle.putSerializable(EDIT_DB,db);
        bundle.putBoolean(EDIT_MODEL,true);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
    private class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            transaction = manager.beginTransaction();
            int id=view.getId();
            if (id==R.id.add_cost_button){
                addCostBtn.setTextColor(0xffff8c00); // 设置“支出“按钮为灰色
                addEarnBtn.setTextColor(0xff908070); // 设置“收入”按钮为橙色
                transaction.replace(R.id.item_fragment, CostFragment.newInstance());
                Log.d(TAG, "onClick: add_cost_button");
            }else if(id==R.id.add_earn_button) {
                 addEarnBtn.setTextColor(0xffff8c00); // 设置“收入“按钮为灰色
                 addCostBtn.setTextColor(0xff908070); // 设置“支出”按钮为橙色
                 transaction.replace(R.id.item_fragment, EarnFragment.newInstance());
                 Log.d(TAG, "onClick: add_earn_button");
             }else if(R.id.add_finish==id) {
                String moneyString = moneyText.getText().toString();
                if (moneyString.equals("0.00") || GlobalVariables.getmInputMoney().equals("")) {
                    Toast.makeText(getApplicationContext(), getString(R.string.AddItemActivity_toast_text), Toast.LENGTH_SHORT).show();
                    return;
                }
                if(isEditModel) {
                    updateData(Double.parseDouble(moneyText.getText().toString()));
                    calculatorClear();
                    finish();
                }else {
                    putItemInData(Double.parseDouble(moneyText.getText().toString()));
                    calculatorClear();
                    finish();
                }
            }else if (R.id.clear==id) {
                calculatorClear();
                moneyText.setText("0.00");
            }else if(R.id.add_description==id){
                Intent intent = new Intent(AddItemActivity.this, AddDescription.class);
                startActivity(intent);
            }
            transaction.commit();
        }
    }
    public void updateData(double money){
        AccountDB db=new AccountDB();
        db.setMoney(money);
        db.setDescription(GlobalVariables.getmDescription());
        db.setSrcName((String) bannerText.getTag());
        int tagType= (int) bannerImage.getTag();
        if (tagType<0){
            db.setType(IOItemAdapter.TYPE_COST);
        }else db.setType(IOItemAdapter.TYPE_EARN);
        db.setName(bannerText.getText().toString());
        db.update(GlobalVariables.getmId());

        Sum sumAll=DataSupport.find(Sum.class,1);
        double total=sumAll.getTotal();
        total+=-(mLastMoney*mLastType);
        total+=money*db.getType();
        sumAll.setTotal(total);
        sumAll.save();

        Sum earnSum=DataSupport.find(Sum.class,IOItemAdapter.MONTHLY_EARN);
        Sum costSum=DataSupport.find(Sum.class,IOItemAdapter.MONTHLY_COST);

        if (GlobalVariables.getmDate().substring(0,7).equals(costSum.getDate())){
            if (IOItemAdapter.TYPE_COST==mLastType)
                costSum.setTotal(costSum.getTotal()-mLastMoney);
            else
                earnSum.setTotal(earnSum.getTotal()-mLastMoney);

            if (IOItemAdapter.TYPE_COST==db.getType())
                costSum.setTotal(costSum.getTotal()+money);
            else
                earnSum.setTotal(earnSum.getTotal()+money);

            costSum.save();
            earnSum.save();

        }

    }

    public void putItemInData(double money) {
        Sum sum = new Sum();
        AccountDB accountDB = new AccountDB();
        String tagName = (String) bannerText.getTag();
        int tagType = (int) bannerImage.getTag();
        if (tagType < 0) {
            accountDB.setType(IOItemAdapter.TYPE_COST);
        } else accountDB.setType(IOItemAdapter.TYPE_EARN);
        accountDB.setName(bannerText.getText().toString());
        accountDB.setSrcName(tagName);
        accountDB.setMoney(money);
        accountDB.setTimeStamp(formatItem.format(new Date()));         // 存储记账时间
        accountDB.setDescription(GlobalVariables.getmDescription());
        accountDB.save();

        // 存储完之后及时清空备注
        GlobalVariables.setmDescription("");
        int type = accountDB.getType();
        String sumDate = formatSum.format(new Date());
        // 计算总额
        sum.calculateMoneyIncludeNull(IOItemAdapter.SUM, "All", money, type, sumDate);
        calculateMonthlyMoney(type, accountDB);
    }

    public void calculateMonthlyMoney(int type, AccountDB accountDB) {
        Sum sum = new Sum();
        Sum tmpSum = new Sum();
        String sumDate = formatSum.format(new Date());
        int id = (int)((double)type / 2 + 2.5);
        // 保证一定现有2号id，避免出现当月支出不更新的bug
        if (!tmpSum.isThereASum(IOItemAdapter.MONTHLY_COST))
            tmpSum.saveSum(tmpSum, IOItemAdapter.MONTHLY_COST, 0.0, 1, sumDate);
        if (sum.isThereASum(id)) {
            sum = DataSupport.find(Sum.class, id);
            //todo 这里数字要改 如果是yyyy.MM则是(0,8) MM.yyyy是(3,10)
            if (sum.getDate().equals(accountDB.getTimeStamp().substring(0,7))) {
                sum.calculateMoney(id, accountDB.getMoney(), type*type);
            } else {
                sum.saveSum(sum, id, accountDB.getMoney(), type*type, sumDate);
            }
        } else {
            sum.saveSum(sum, id, accountDB.getMoney(), type*type, sumDate);
        }
    }

    // 数字输入按钮
    public void calculatorNumOnclick(View v) {
        Button view = (Button) v;
        String digit = view.getText().toString();
        String money = GlobalVariables.getmInputMoney();
        if (GlobalVariables.getmHasDot() && GlobalVariables.getmInputMoney().length()>2) {
            String dot = money.substring(money.length() - 3, money.length() - 2);
            Log.d(TAG, "calculatorNumOnclick: " + dot);
            if (dot.equals(".")) {
                Toast.makeText(getApplicationContext(), getString(R.string.AddItemActivity_toast_onclick), Toast.LENGTH_SHORT).show();
                return;
            }
        }
        GlobalVariables.setmInputMoney(money+digit);
        moneyText.setText(decimalFormat.format(Double.valueOf(GlobalVariables.getmInputMoney())));
    }

    // 清零按钮
    public void calculatorClear() {
        GlobalVariables.setmInputMoney("");
        GlobalVariables.setHasDot(false);
    }

    // 小数点处理工作
    public void calculatorPushDot(View view) {
        if (GlobalVariables.getmHasDot()) {
            Toast.makeText(getApplicationContext(), getString(R.string.AddItemActivity_toast_PushDot), Toast.LENGTH_SHORT).show();
        } else {
            GlobalVariables.setmInputMoney(GlobalVariables.getmInputMoney()+".");
            GlobalVariables.setHasDot(true);
        }
    }
    public double checkTypeNum(double num,int type){
        if (type==IOItemAdapter.TYPE_EARN) return num;
        else return -num;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlobalVariables.setmInputMoney("");
        GlobalVariables.setHasDot(false);
        GlobalVariables.setmDescription("");
        GlobalVariables.setmDate("");
    }
}
