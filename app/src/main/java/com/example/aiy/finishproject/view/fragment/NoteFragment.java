package com.example.aiy.finishproject.view.fragment;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.aiy.finishproject.Base.BaseFragment;
import com.example.aiy.finishproject.R;
import com.example.aiy.finishproject.adapter.my_recycler_adapter.Base.ViewHolder;
import com.example.aiy.finishproject.adapter.my_recycler_adapter.CommonAdapter;
import com.example.aiy.finishproject.db.NoteDB;
import com.example.aiy.finishproject.manager.NoteManager;
import com.example.aiy.finishproject.util.CommonUtil;
import com.example.commonlib.util.LoggerUtils;
import com.example.aiy.finishproject.view.activity.NoteAddActivity;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;
import org.litepal.crud.callback.FindMultiCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.leefeng.promptlibrary.PromptButton;
import me.leefeng.promptlibrary.PromptButtonListener;
import me.leefeng.promptlibrary.PromptDialog;

import static com.example.aiy.finishproject.view.activity.NoteAddActivity.EDIT;
import static com.example.aiy.finishproject.view.activity.NoteAddActivity.EDITCODE;
import static com.example.aiy.finishproject.view.activity.NoteAddActivity.NOTE_ITEM;

/**
 * <p>功能简述：
 * <p>Created by Aiy on 2018/4/9.
 */

public class NoteFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "NoteFragment";
    public static final int REQUESTCODE=111;
    private List<NoteDB> mNotes=new ArrayList<>();
    private RecyclerView mRecycler;
    private Button mAdd;
    private CommonAdapter<NoteDB> mAdapter;
    /**
     * 变量简述： 判断是不是长点击
     */
    boolean isLongClick;

    private Button mAllButton;
    private EditText mSearchEdit;
    private ImageButton mSearchButton;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_note_main;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(getLayoutId(),container,false);

        mAdd=view.findViewById(R.id.note_fragment_add);
        mAdd.setOnClickListener(this);

        mAllButton=view.findViewById(R.id.note_all_button);
        mAllButton.setOnClickListener(this);

        mSearchEdit=view.findViewById(R.id.note_fragment_search_edit);
        mSearchButton=view.findViewById(R.id.note_fragment_search_button);
        mSearchButton.setOnClickListener(this);

        mRecycler=view.findViewById(R.id.note_recycler);
        final SQLiteDatabase db= LitePal.getDatabase();
        mAdapter=new CommonAdapter<NoteDB>(getActivity(),mNotes,R.layout.note_recycler) {
            @Override
            public void convert(ViewHolder holder, final NoteDB item, final int position) {
                holder.setTextDrawable(R.id.note_recycler_content,item.getIntroduction());
                holder.setTextDrawable(R.id.note_recycler_title,item.getTitle());
                holder.setTextDrawable(R.id.note_recycler_time, CommonUtil.dataToTimeText(new Date(item.getTime())));
                ImageView imageView=holder.getView(R.id.note_recycler_image);
                if (!NoteManager.HTML_NO_HAS_IMG.equals(item.getImgUrl())){
                    Glide.with(getActivity())
                            .load(new File(item.getImgUrl()))
                            .into(imageView);
                }else {
                    LoggerUtils.d("不存在");
                    imageView.setImageDrawable(null);
                }
                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LoggerUtils.d("之前传的item"+item.isHasRecorder());
                        if (isLongClick){
                            isLongClick=false;
                        }else {
                            EditActivity(item);
                        }}
                });
                holder.getConvertView().setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        isLongClick=true;
                        PromptDialog dialog=new PromptDialog(getActivity());
                        dialog.showWarnAlert("是否要删除该记录？",
                                new PromptButton("取消", new PromptButtonListener() {
                                    @Override
                                    public void onClick(PromptButton promptButton) {
                                    }
                                }),new PromptButton("确定", new PromptButtonListener() {
                                    @Override
                                    public void onClick(PromptButton promptButton) {
                                        mNotes.remove(position);
                                        NoteManager.deleteData(item.getTime());
                                        notifyDataSetChanged();
                                    }
                                }));
                        return false;
                    }
                });

            }
        };
        DataSupport.findAllAsync(NoteDB.class)
                .listen(new FindMultiCallback() {
                    @Override
                    public <T> void onFinish(List<T> t) {
                        mNotes.clear();
                        mNotes.addAll((List<NoteDB>)t);
                        mAdapter.notifyDataSetChanged();
                    }
                });


        mRecycler.setAdapter(mAdapter);
        mRecycler.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        return view;
    }
    public  void EditActivity(NoteDB noteDB){
        Intent intent=new Intent(getActivity(),NoteAddActivity.class);
        Bundle bundle=new Bundle();
        bundle.putSerializable(NOTE_ITEM,noteDB);
        bundle.putInt(EDIT,EDITCODE);
        intent.putExtras(bundle);
        startActivityForResult(intent,REQUESTCODE);
    }
    @Override
    public void onResume() {
        super.onResume();
        LoggerUtils.d("修改:");
        Observable.create(new ObservableOnSubscribe<List<NoteDB>>() {
            @Override
            public void subscribe(ObservableEmitter<List<NoteDB>> emitter) throws Exception {
                List<NoteDB> list=DataSupport.findAll(NoteDB.class);
                emitter.onNext(list);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<NoteDB>>() {
                    @Override
                    public void accept(List<NoteDB> noteDBS) throws Exception {
                        mNotes.clear();
                        mNotes.addAll(noteDBS);
                        mAdapter.notifyDataSetChanged();
                    }
                });
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.note_fragment_add:
                startActivity(new Intent(getActivity(),NoteAddActivity.class));
                break;
            case R.id.note_all_button:
                onResume();
                break;
            case R.id.note_fragment_search_button:
                String s= String.valueOf(mSearchEdit.getText());
                mNotes.clear();
                mNotes.addAll(NoteManager.queryTitle(s));
                mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
    }
}
