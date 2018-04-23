package com.example.aiy.finishproject.db;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * <p>功能简述：
 * <p>Created by Aiy on 2018/4/9.
 */

public class NoteDB extends DataSupport implements Serializable{
    /**
     * 变量简述： 正文的txt地址
     */
    private String content;
    /**
     * 变量简述： 简介，取正文的前面一小部分
     */
    private String introduction;
    /**
     * 变量简述： 标题
     */
    private String title;
    /**
     * 变量简述： 时间
     */
    private long time;
    /**
     * 变量简述： 第一张图片的地址，没有图片返回no_image
     */
    private String imgUrl;

    private boolean hasRecorder;
    private int id;

    public int getId() {
        return id;
    }

    public NoteDB() {
    }

    public NoteDB(String content, String title, long time, String imgUrl,String introduction, boolean hasRecorder) {
        this.content = content;
        this.introduction = introduction;
        this.title = title;
        this.time = time;
        this.imgUrl = imgUrl;
        this.hasRecorder = hasRecorder;
    }

    public boolean isHasRecorder() {
        return hasRecorder;
    }

    public void setHasRecorder(boolean hasRecorder) {
        this.hasRecorder = hasRecorder;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
