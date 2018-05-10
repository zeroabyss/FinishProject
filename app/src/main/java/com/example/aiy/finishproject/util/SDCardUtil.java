package com.example.aiy.finishproject.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
/**
 *任务描述： 外存相关的工具类
 *创建时间： 2018/5/10 15:07
 */
public class SDCardUtil {
	public static String SDCardRoot = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
	public static String APP_NAME = "FinishProject";

	/**
	 * 检查是否存在SDCard
	 * @return
	 */
	public static boolean hasSdcard(){
		String state = Environment.getExternalStorageState();
		if(state.equals(Environment.MEDIA_MOUNTED)){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * 方法简述： 创建文件
	 */
	public static File createDirFile(String fileName){
		if (!hasSdcard()) return null;
		File file=new File(SDCardRoot+APP_NAME+File.separator);
		if (!file.exists()) file.mkdirs();
		return new File(SDCardRoot+APP_NAME,fileName);
	}
	/**
	 * 方法简述： 获取文件
	 */
	public static File getFile(String fileName){
		File file=new File(SDCardRoot+APP_NAME+File.separator+fileName);
		if (!file.exists())
			return null;
		return file;
	}

	/**
	 * 获得文章图片保存路径
	 * @return
	 */
	public static String getPictureDir(){
		String imageCacheUrl = SDCardRoot + APP_NAME + File.separator;
		File file = new File(imageCacheUrl);
		if(!file.exists())
			file.mkdir();  //如果不存在则创建
		return imageCacheUrl;
	}

	/**
	 * 图片保存到SD卡
	 * @param bitmap
	 * @return
	 */
	public static String saveToSdCard(Bitmap bitmap) {
		String imageUrl = getPictureDir() + System.currentTimeMillis() + "-";
		File file = new File(imageUrl);
		try {
			FileOutputStream out = new FileOutputStream(file);
			if (bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)) {
				out.flush();
				out.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file.getAbsolutePath();
	}


	/** 删除文件 **/
	public static boolean deleteFile(String filePath) {
		File file = new File(filePath);
		boolean isOk = false;
		if (file.isFile() && file.exists())
			isOk = file.delete(); // 删除文件
		return isOk;
	}

}
