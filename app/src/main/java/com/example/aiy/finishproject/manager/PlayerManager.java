package com.example.aiy.finishproject.manager;

import android.media.MediaPlayer;
import android.media.MediaRecorder;

import com.example.aiy.finishproject.impl.PlayerInterface;
import com.example.aiy.finishproject.util.LoggerUtils;

import java.io.IOException;

/**
 * <p>功能简述：
 * <p>Created by Aiy on 2018/4/12.
 */

public class PlayerManager {
    public static final String NOTIME="no_time";
    private static volatile PlayerManager INSTANCE;
    private MediaPlayer player;
    private PlayerManager() {

    }

    public static PlayerManager newInstance(){
        if (INSTANCE==null){
            synchronized (PlayerManager.class){
                if (INSTANCE==null){
                    INSTANCE=new PlayerManager();
                }
            }
        }
        return INSTANCE;
    }

    public PlayerManager initPlayer(){
        player =new MediaPlayer();
        return INSTANCE;
    }

    public PlayerManager setPlayerData(String url){
        if (player !=null)
            try {
                LoggerUtils.d("player:"+url);
                player.setDataSource(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
        return INSTANCE;
    }
    public PlayerManager setOnCompletionListener(MediaPlayer.OnCompletionListener listener){
        if (player!=null){
            player.setOnCompletionListener(listener);
        }
        return INSTANCE;
    }
    public PlayerManager playerStart(){
        if (player !=null){
            LoggerUtils.d("player:开始");
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    player.start();
                    playerVolume(1);
                }
            });
            player.prepareAsync();

        }
        return INSTANCE;
    }

    public PlayerManager playerPrepare(){
        if (player!=null)
            try {
                player.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        return INSTANCE;
    }

    public void playerStop(){
        if (player !=null){
            player.stop();
        }

    }
    public void playerRelease(){
        if (player !=null){
            player.release();
        }
    }

    public void playerVolume(float volume){
        if (player!=null)
            player.setVolume(volume,volume);
    }
    public String getDuration(){
        if (player==null) return NOTIME;
        int sum=player.getDuration()/1000;
        LoggerUtils.d("总时长"+sum+"");
        String timeFormat;
        if (sum/60<10){
            timeFormat="0"+sum/60+":";
        }else {
            timeFormat=sum/60+":";
        }
        if (sum%60<10){
            timeFormat+="0"+sum%60;
        }else {
            timeFormat+=sum%60;
        }
        return timeFormat;
    }
}
