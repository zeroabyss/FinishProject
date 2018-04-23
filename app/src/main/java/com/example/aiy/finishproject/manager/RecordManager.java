package com.example.aiy.finishproject.manager;

import android.media.MediaRecorder;

import java.io.IOException;

/**
 * <p>功能简述：
 * <p>Created by Aiy on 2018/4/12.
 */

public class RecordManager {
    private static volatile RecordManager INSTANCE;
    private MediaRecorder mRecord;
    private RecordManager() {

    }

    public static RecordManager newInstance(){
        if (INSTANCE==null){
            synchronized (RecordManager.class){
                if (INSTANCE==null){
                    INSTANCE=new RecordManager();
                }
            }
        }
        return INSTANCE;
    }

    public RecordManager initRecorder(){
        mRecord=new MediaRecorder();
        mRecord.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecord.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecord.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecord.setAudioEncodingBitRate(16);
        mRecord.setAudioSamplingRate(44100);
        return INSTANCE;
    }

    public RecordManager setRecorderOutFile(String url){
        if (mRecord!=null)
            mRecord.setOutputFile(url);
        return INSTANCE;
    }

    public RecordManager recordStart(){
        if (mRecord!=null){
            try {
                mRecord.prepare();
                mRecord.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return INSTANCE;
    }

    public RecordManager recordStop(){
        if (mRecord!=null){
            mRecord.stop();
        }
        return INSTANCE;
    }
    public void recordRelease(){
        if (mRecord!=null){
            mRecord.release();
        }
    }


}
