package com.example.aiy.finishproject.manager;

import com.example.aiy.finishproject.db.NoteDB;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * <p>功能简述：
 * <p>Created by Aiy on 2018/4/10.
 */

public class NoteManager {
    public static final String HTML_NO_HAS_IMG="no_image";
    public static void saveNewNote(String content, String title, long time, String imgUrl,String introduction,boolean hasRecorder){
        NoteDB db=new NoteDB(content,title,time,imgUrl,introduction,hasRecorder);
        db.save();
    }
    /**
     * 方法简述： 根据时间来当标识符寻找对应的数据。
     * @return id
     */
    public static int queryHasData(long time){
        List<NoteDB> list= DataSupport.where("time = ?",Long.toString(time)).find(NoteDB.class);
        if (list.size()==0)
            return -1;
        else if (list.size()==1)
            return list.get(0).getId();
        else
            return -2;
    }

    public static void updateData(NoteDB noteDB,int id){
        if (!noteDB.isHasRecorder())
            noteDB.setToDefault("hasRecorder");
        noteDB.update(id);
    }

    public static void deleteData(long time){
        DataSupport.deleteAll(NoteDB.class,"time = ?",time+"");
    }

    public static List<NoteDB> queryTitle(String title){
        return DataSupport.where("title = ?",title).find(NoteDB.class);
    }
}
