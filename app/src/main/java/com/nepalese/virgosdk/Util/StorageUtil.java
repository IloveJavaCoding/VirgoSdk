package com.nepalese.virgosdk.Util;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.webkit.CookieManager;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author nepalese on 2020/11/18 10:18
 * @usage 获取设备存储容量信息, 获取指定文件、文件夹大小
 */
public class StorageUtil {
    private static final String TAG = "StorageUtil";

    public static final String TYPE_TOTAL = "total";
    public static final String TYPE_FREE = "free";
    public static final String TYPE_USED = "used";

    //====================================get system memory info====================================
    //外部存储情况
    @RequiresApi(api = Build.VERSION_CODES.R)
    public static long getExternalStorageSpace(String type){
        File file = Environment.getStorageDirectory();
        if (file.exists()){
            switch (type){
                case TYPE_TOTAL:
                    return file.getTotalSpace();
                case TYPE_FREE:
                    return file.getFreeSpace();
                case TYPE_USED:
                    return file.getTotalSpace()-file.getFreeSpace();
            }
        }
        return -1;
    }

    public static long getExternalStorageSpaceOld(String type){
        File file = Environment.getExternalStorageDirectory();
        if (file.exists()){
            switch (type){
                case TYPE_TOTAL:
                    return file.getTotalSpace();
                case TYPE_FREE:
                    return file.getFreeSpace();
                case TYPE_USED:
                    return file.getTotalSpace()-file.getFreeSpace();
            }
        }
        return -1;
    }

    public static long getSystemMemorySpace(Context context, String type){
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        manager.getMemoryInfo(info);

        switch (type) {
            case TYPE_TOTAL:
                return info.totalMem;
            case TYPE_FREE:
                return info.availMem;
            case TYPE_USED:
                return info.totalMem - info.availMem;
        }
        return -1;
    }

    public static boolean isLowMemory(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        manager.getMemoryInfo(info);
        return info.lowMemory;
    }

    //========================================get size of file/dir===================================
    //获取指定文件的大小
    public static long getFileSize(String path) {
        long size = -1;
        File file = new File(path);
        //文件不存在或非文件
        if(!file.exists() || !file.isFile()){
            return size;
        }

        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);//使用FileInputStream读入file的数据流
            size = inputStream.available();//文件的大小
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            try {
                assert inputStream != null;
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return size;
    }

    public static long getFileSize(File file) {
        long size = -1;
        if (file.exists()) {
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(file);//使用FileInputStream读入file的数据流
                size = inputStream.available();//文件的大小
            } catch (IOException e) {
                e.printStackTrace();
            } finally{
                try {
                    assert inputStream != null;
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Log.e(TAG, "File not exists!");
        }
        return size;
    }

    //获取指定文件夹的大小
    public static long getDirSize(String dir) {
        long size = -1;
        File file = new File(dir);
        //文件夹不存在或非文件夹
        if(!file.exists() || !file.isDirectory()){
            return size;
        }

        File[] files = file.listFiles();//文件夹目录下的所有文件
        //空文件夹
        if(files==null || files.length<1){
            return size;
        }

        for (File value : files) {
            if (value.isDirectory()) {//判断是否父目录下还有子目录
                size = size + getDirSize(value);
            } else {
                size = size + getFileSize(value);
            }
        }
        return size;
    }

    public static long getDirSize(File file) {
        long size = -1;
        File[] files = file.listFiles();//文件夹目录下的所有文件
        //空文件夹
        if(files==null || files.length<1){
            return size;
        }

        for (File value : files) {
            if (value.isDirectory()) {//判断是否父目录下还有子目录
                size = size + getDirSize(value);
            } else {
                size = size + getFileSize(value);
            }
        }
        return size;
    }

    //===========================================清空缓存======================================
    public static void clearCookies() {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookies(null);
        //You can pass null as the callback if you don't need to know when the operation completes
        //or whether any cookies were removed, and in this case it is safe to call the method from a thread without a Looper.
    }
}
