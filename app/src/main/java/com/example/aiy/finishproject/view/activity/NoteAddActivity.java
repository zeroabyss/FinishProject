package com.example.aiy.finishproject.view.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.aiy.finishproject.Base.BaseActivity;
import com.example.aiy.finishproject.R;
import com.example.aiy.finishproject.bdasr.IRecogListener;
import com.example.aiy.finishproject.bdasr.MessageStatusRecogListener;
import com.example.aiy.finishproject.bdasr.MyRecognizer;
import com.example.aiy.finishproject.db.NoteDB;
import com.example.aiy.finishproject.manager.NoteManager;
import com.example.aiy.finishproject.manager.PlayerManager;
import com.example.aiy.finishproject.manager.RecordManager;
import com.example.aiy.finishproject.util.CommonUtil;
import com.example.aiy.finishproject.util.LoggerUtils;
import com.example.aiy.finishproject.util.SDCardUtil;
import com.example.aiy.finishproject.util.StringUtils;
import com.example.aiy.finishproject.view.fragment.ShowDialogFragment;
import com.scrat.app.richtext.RichEditText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;



import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.leefeng.promptlibrary.PromptButton;
import me.leefeng.promptlibrary.PromptButtonListener;
import me.leefeng.promptlibrary.PromptDialog;

import static com.example.aiy.finishproject.bdasr.IStatus.STATUS_FINISHED;
import static com.example.aiy.finishproject.util.FileUtils.readFile;
import static com.example.aiy.finishproject.util.SDCardUtil.APP_NAME;
import static com.example.aiy.finishproject.util.SDCardUtil.SDCardRoot;

public class NoteAddActivity extends BaseActivity implements OnClickListener{
    private static final String TAG = "NoteAddActivity";

    public static final int START_RECORDER=1001;
    public static final int STOP_RECORDER=1002;
    public static final int START_ASR=1003;
    public static final int STOP_ASR=1004;
    //传NoteD对象过来的key
    public static final String NOTE_ITEM="note_db";
    //编辑状态的key
    public static final String EDIT="edit";
    //编辑状态，从之前的Activity传过来的值
    public static final int EDITCODE=1;
    //新建状态
    public static final String ADD="add";
    //返回按钮
    private ImageView mBack;
    //确定保存按钮
    private ImageView mOK;
    //分别是加粗，斜体，下划线，删除线，分号，块，图片
    private ImageButton mBold;
    private ImageButton mItalic;
    private ImageButton mUnderLine;
    private ImageButton mStrikethrough;
    private ImageButton mBullet;
    private ImageButton mQuote;
    private ImageButton mPhoto;
    private Button mRecordButton;
    private Button mAsrButton;
    //录音布局
    private ImageButton mRecorderPlayButton;
    private RelativeLayout mRecoderLayout;
    private TextView mRecoderTimeText;

    private EditText mTitleEdit;
    private TextView mTitle;
    private TextView mTime;
    private static final int REQUEST_CODE_GET_CONTENT = 666;
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 444;

    RichEditText richEditText;
    private Date mDate;
    private Handler mHandler=new MyASRHandler<>(this);
    IRecogListener listener ;
    MyRecognizer myRecognizer ;
    Map<String, Object> params ;
    /**
     * 变量简述： 判断是否编辑过,true表示已经编辑过
     */
    private boolean flag;
    /**
     * 变量简述： 是否拥有录音文件
     */
    boolean hasRecorder=false;

    PromptDialog dialog;
    /**
     * 变量简述： 录音时间是否太短，如果太短的话就不执行录音
     */
    boolean recorderTouchFlag=true;
    long mRecorderFlagTime;

    /**
     * 变量简述： 是否正在播放，根据情况切换播放按钮的样式
     */
    private boolean playerFlag=false;
    /**
     * 变量简述： ASR时间太短就不执行和录音一样
     */
    boolean mAsrTouchFlag=true;
    long mAsrFlagTime;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_note_add;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initRecordAndPlay();
        int flagCode=getIntent().getIntExtra(EDIT,0);
        if (flagCode==EDITCODE){
            NoteDB noteDB= (NoteDB) getIntent().getSerializableExtra(NOTE_ITEM);
            mTitle.setText("编辑备忘");
            mDate=new Date(noteDB.getTime());
            mTime.setText(CommonUtil.dataToTimeText(mDate));
            mTitleEdit.setText(noteDB.getTitle());
            String html=readFile(CommonUtil.dataToSDcard(mDate));
            LoggerUtils.d(html+"");
            richEditText.fromHtml(html);
            hasRecorder=noteDB.isHasRecorder();
            LoggerUtils.d("数据库信息"+noteDB.isHasRecorder()+"");
            if (hasRecorder){
                mRecoderLayout.setVisibility(View.VISIBLE);
                mRecoderTimeText.setText(PlayerManager.newInstance()
                        .setPlayerData(SDCardRoot + APP_NAME + File.separator + CommonUtil.dataToSDcard(mDate) + ".3gp")
                        .playerPrepare()
                        .getDuration());
            }
        }else {
            mTitle.setText("新建备忘");
            mDate=new Date();
            mTime.setText(CommonUtil.dataToTimeText(mDate));
        }
        //等传递过来的NodeDB赋完值再设置false，不然传递值过来的时候会修改数据，所以导致flag变成true
        initAsr();
        flag=false;
    }
    private void initAsr(){
        params=new HashMap<>();
        params.put("accept-audio-data",false);
        params.put("disable-punctuation",false);
        params.put("accept-audio-volume",true);
        params.put("pid",1537);
        listener = new MessageStatusRecogListener(mHandler);
        myRecognizer = new MyRecognizer(this, listener);
    }

    private void initView(){
        richEditText=findViewById(R.id.activity_note_add_richtext);
        richEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                flag=true;
            }
        });
        mTitle=findViewById(R.id.title_has_title);

        mBack=findViewById(R.id.title_has_back);
        mBack.setOnClickListener(this);
        mOK=findViewById(R.id.title_has_ok);
        mOK.setOnClickListener(this);
        mBold=findViewById(R.id.add_bold);
        mBold.setOnClickListener(this);
        mItalic=findViewById(R.id.add_italic);
        mItalic.setOnClickListener(this);
        mUnderLine=findViewById(R.id.add_underline);
        mUnderLine.setOnClickListener(this);
        mBullet=findViewById(R.id.add_bullet);
        mBullet.setOnClickListener(this);
        mQuote=findViewById(R.id.add_quote);
        mQuote.setOnClickListener(this);
        mStrikethrough=findViewById(R.id.add_strikethrough);
        mStrikethrough.setOnClickListener(this);
        mPhoto=findViewById(R.id.add_image);
        mPhoto.setOnClickListener(this);
        mRecordButton=findViewById(R.id.add_recorder);
        mRecordButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        mRecorderFlagTime =System.currentTimeMillis();
                        recorderTouchFlag=true;
                        Message message=mHandler.obtainMessage();
                        message.arg1=START_RECORDER;
                        mHandler.sendMessageDelayed(message,300);
                        break;
                    case MotionEvent.ACTION_UP:
                        LoggerUtils.d("FLAGTIME"+ mRecorderFlagTime);
                        long nowTime=System.currentTimeMillis();
                        if (nowTime- mRecorderFlagTime <300){
                            LoggerUtils.d("FLAG进入");
                            recorderTouchFlag=false;
                        }else {
                            Message message1 = mHandler.obtainMessage();
                            message1.arg1 = STOP_RECORDER;
                            mHandler.sendMessage(message1);
                        }
                }
                return true;
            }
        });
        mAsrButton=findViewById(R.id.add_asr);
        mAsrButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        mAsrFlagTime=System.currentTimeMillis();
                        mAsrTouchFlag=true;
                        Message message=mHandler.obtainMessage();
                        message.arg1=START_ASR;
                        mHandler.sendMessageDelayed(message,300);
                        break;
                    case MotionEvent.ACTION_UP:
                        LoggerUtils.d("FLAGTIME"+ mAsrFlagTime);
                        long nowTime=System.currentTimeMillis();
                        if (nowTime- mAsrFlagTime <300){
                            LoggerUtils.d("FLAG进入");
                            mAsrTouchFlag=false;
                        }else {
                            Message message1 = mHandler.obtainMessage();
                            message1.arg1 = STOP_ASR;
                            mHandler.sendMessage(message1);
                        }
                }
                return true;
            }
        });

        mTitleEdit=findViewById(R.id.note_add_title);
        mTitleEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                flag=true;
            }
        });
        mTime=findViewById(R.id.activity_note_add_time);
        mRecoderLayout=findViewById(R.id.note_add_recorder_layout);
        mRecoderLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                dialog.showWarnAlert("是否要删除该录音？",
                        new PromptButton("取消", new PromptButtonListener() {
                    @Override
                    public void onClick(PromptButton promptButton) {
                    }
                }),new PromptButton("确定", new PromptButtonListener() {
                    @Override
                    public void onClick(PromptButton promptButton) {
                        mRecoderLayout.setVisibility(View.INVISIBLE);
                        hasRecorder=false;
                    }
                }));

                return true;
            }
        });
        mRecoderTimeText=findViewById(R.id.note_add_recorder_text);
        mRecorderPlayButton =findViewById(R.id.note_add_recorder_button);
        mRecorderPlayButton.setOnClickListener(this);
        dialog=new PromptDialog(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (data == null || data.getData() == null || requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE)
            return;

        final Uri uri = data.getData();
        final int width = richEditText.getMeasuredWidth() - richEditText.getPaddingLeft() - richEditText.getPaddingRight();
        richEditText.image(uri, width);
    }
    /**
     * 加粗
     */
    public void setBold(View v) {
        richEditText.bold(!richEditText.contains(RichEditText.FORMAT_BOLD));
    }

    /**
     * 斜体
     */
    public void setItalic(View v) {
        richEditText.italic(!richEditText.contains(RichEditText.FORMAT_ITALIC));
    }

    /**
     * 下划线
     */
    public void setUnderline(View v) {
        richEditText.underline(!richEditText.contains(RichEditText.FORMAT_UNDERLINED));
    }

    /**
     * 删除线
     */
    public void setStrikethrough(View v) {
        richEditText.strikethrough(!richEditText.contains(RichEditText.FORMAT_STRIKETHROUGH));
    }

    /**
     * 序号
     */
    public void setBullet(View v) {
        richEditText.bullet(!richEditText.contains(RichEditText.FORMAT_BULLET));
    }

    /**
     * 引用块
     */
    public void setQuote(View v) {
        richEditText.quote(!richEditText.contains(RichEditText.FORMAT_QUOTE));
    }
    public void insertImg(View v) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        }

        /*Intent intent ;
        if (Build.VERSION.SDK_INT > 19) {

            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addCategory(Intent.CATEGORY_OPENABLE);

        } else {

            intent = new Intent(Intent.ACTION_GET_CONTENT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image*//*");*/
        Intent intent =new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_GET_CONTENT);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    void saveFile(final String content, final String fileName){
        File file = SDCardUtil.createDirFile(fileName);
        FileOutputStream outputStream = null;
        if (file == null) return;
        try {
            outputStream = new FileOutputStream(file);
            byte[] buffer = content.getBytes();
            outputStream.write(buffer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null)
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    void save() {
        if(checkTitleIsNull()){
            showToast("标题不能为空");
            return;
        }
        Observable.create(new ObservableOnSubscribe<NoteDB>() {
            @Override
            public void subscribe(ObservableEmitter<NoteDB> emitter) throws Exception {
                saveFile(richEditText.toHtml(), CommonUtil.dataToSDcard(mDate) + ".txt");

                String content=SDCardRoot + APP_NAME + File.separator + CommonUtil.dataToSDcard(mDate) + ".txt";
                String title=mTitleEdit.getText() + "";
                long time=mDate.getTime();
                String imgUrl=StringUtils.getFirstImg(NoteAddActivity.this,richEditText.toHtml());
                String introduction=StringUtils.getIntroductionText(richEditText.toHtml());
                LoggerUtils.d("简介"+introduction);
                LoggerUtils.d("图片"+imgUrl);
                int id=NoteManager.queryHasData(mDate.getTime());
                switch (id){
                    case -1:
                        NoteManager.saveNewNote(content,
                                title,
                                time,
                                imgUrl,
                                introduction,
                                hasRecorder
                        );
                        break;
                    case -2:
                        break;
                    default:
                        LoggerUtils.d("是否有录音"+hasRecorder+"id"+id);
                        NoteManager.updateData(new NoteDB(content,title,time,imgUrl,introduction,hasRecorder),id);
                }
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<NoteDB>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(NoteDB noteDB) {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                        flag = false;
                        showToast("存储完毕");
                        if (!hasRecorder){
                            //todo 删除录音
                            String recordFile=SDCardRoot+APP_NAME+File.separator+CommonUtil.dataToSDcard(mDate)+".3gp";
                            File file=new File(recordFile);
                            if (file.exists())
                                file.delete();
                        }
                    }
                });
    }


    @Override
    public void onClick(View view) {
        int id=view.getId();
        switch (id){
            case R.id.add_bold:
                setBold(view);
                startASR();
                break;
            case R.id.add_italic:
                setItalic(view);
                stopASR();
                break;
            case R.id.add_quote:
                setQuote(view);
                break;
            case R.id.add_strikethrough:
                setStrikethrough(view);
                break;
            case R.id.add_bullet:
                setBullet(view);
                break;
            case R.id.add_image:
                insertImg(view);
                break;
            case R.id.add_underline:
                setUnderline(view);
                break;
            case R.id.title_has_back:
                showDialog();
                break;
            case R.id.title_has_ok:
                save();
                break;
            case R.id.note_add_recorder_button:
                //todo 点击播放按钮
                if (playerFlag){
                    mRecorderPlayButton.setActivated(false);
                    playerFlag=false;
                    stopPlay();
                }else{
                    mRecorderPlayButton.setActivated(true);
                    playerFlag=true;
                    startPlay();
                }


        }
    }
    private void showDialog(){
        if (flag){
            ShowDialogFragment dialogFragment=new ShowDialogFragment();
            dialogFragment.setmTitle("提示");
            dialogFragment.setMessage("是否不保存就退出？");
            dialogFragment.setmOkListener(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            dialogFragment.setmCancelListener(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            dialogFragment.show(getSupportFragmentManager(),"dialog");
        }else{
            finish();
        }
    }
    void startASR(){
        myRecognizer.start(params);
    }

    void stopASR(){
        myRecognizer.stop();
    }

    void initRecordAndPlay(){
        RecordManager.newInstance()
                .initRecorder();
        PlayerManager.newInstance()
                .initPlayer();
    }
     void startRecord(){
        String recordFile=SDCardRoot+APP_NAME+File.separator+CommonUtil.dataToSDcard(mDate)+".3gp";
        RecordManager.newInstance()
                .initRecorder()
                .setRecorderOutFile(recordFile)
                .recordStart();

        LoggerUtils.d("NoteAddAcitivity:startRecorder");
    }
    void stopRecord(){
        RecordManager.newInstance().recordStop();
        RecordManager.newInstance().recordRelease();
    }

    void startPlay(){
        PlayerManager.newInstance().playerRelease();
        String recordFile=SDCardRoot+APP_NAME+File.separator+CommonUtil.dataToSDcard(mDate)+".3gp";
         PlayerManager.newInstance()
                 .initPlayer()
                 .setPlayerData(recordFile)
                 .setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                     @Override
                     public void onCompletion(MediaPlayer mediaPlayer) {
                         mRecorderPlayButton.setActivated(false);
                         playerFlag=false;
                     }
                 })
                 .playerStart();
    }
    String getPlayDuration(){
        PlayerManager.newInstance().playerRelease();
        String recordFile=SDCardRoot+APP_NAME+File.separator+CommonUtil.dataToSDcard(mDate)+".3gp";
        return PlayerManager.newInstance()
                .initPlayer()
                .setPlayerData(recordFile)
                .playerPrepare()
                .getDuration();
    }
    void stopPlay(){
        PlayerManager.newInstance().playerStop();
    }

    void showRecordLayout(){
        mRecoderLayout.setVisibility(View.VISIBLE);
        mRecoderTimeText.setText(getPlayDuration());

    }



    private static class MyASRHandler<T extends NoteAddActivity> extends Handler {
        private final WeakReference<T> main;

        private MyASRHandler(T t) {
            this.main = new WeakReference<T>(t);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            LoggerUtils.d("arg1值为"+msg.arg1);
            switch (msg.arg1){
                case STATUS_FINISHED:
                    LoggerUtils.d("字符串"+msg.obj);
                    String s= (String) msg.obj;
                    if ("ERROR".equals(s.trim())){
                        main.get().showToast("未能识别,请检测是否输入语句并且重试");
                        return;
                    }
                    main.get().richEditText.getText().insert(main.get().richEditText.getSelectionStart(),s.trim());
                    break;
                case START_RECORDER:
                    LoggerUtils.d("FLAG"+main.get().recorderTouchFlag);
                    if (!main.get().recorderTouchFlag) {
                        main.get().showToast("请长按开始录音");
                        return;
                    }
                    main.get().dialog.getDefaultBuilder().stayDuration(Long.MAX_VALUE);
                    main.get().dialog.showCustom(R.drawable.ic_format_recorder,"请开始说话",true);
                    main.get().startRecord();
                    break;
                case STOP_RECORDER:
                    main.get().dialog.dismiss();
                    main.get().stopRecord();
                    main.get().hasRecorder=true;
                    main.get().showRecordLayout();
                    break;
                case START_ASR:
                    if (!main.get().mAsrTouchFlag) {
                        main.get().showToast("请长按开始语音转换文字");
                        return;
                    }else {
                        main.get().dialog.getDefaultBuilder().stayDuration(Long.MAX_VALUE);
                        main.get().dialog.showCustom(R.drawable.ic_format_asr, "请开始说话", true);
                        main.get().startASR();
                    }
                    break;
                case STOP_ASR:
                    LoggerUtils.d("结束识别");
                    main.get().dialog.dismiss();
                    main.get().stopASR();
                    break;
            }
        }


    }

    private boolean checkTitleIsNull(){
        String title=mTitleEdit.getText()+"";
        return title.equals("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放资源
        myRecognizer.release();
        RecordManager.newInstance().recordRelease();
        PlayerManager.newInstance().playerRelease();
    }
}
