package com.nepalese.virgosdk.Util;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.webkit.CookieManager;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @author nepalese on 2020/11/18 10:18
 * @usage 获取设备存储容量信息, 获取指定文件、文件夹大小
 */
public class StorageUtil {
    private static final String TAG = "StorageUtil";

    public static final String TYPE_TOTAL = "total";
    public static final String TYPE_FREE = "free";
    public static final String TYPE_USED = "used";

    public static final int STORAGE_INTERNAL = 0; // 内置SD卡
    public static final int STORAGE_EXTERNAL = 1; //外置SD卡：

    //==========================================设备存储信息=========================================
    /**
     * 外部存储情况(30)
     * @param type total、free、used
     * @return
     */
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

    /**
     * 外部存储情况
     * @param type total、free、used
     * @return
     */
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

    /**
     * 获取内部存储空间
     *
     * @param context 上下文
     * @return 以M, G为单位的容量
     */
    public static String getTotalInternalMemorySize(Context context) {
        File file = Environment.getDataDirectory();
        StatFs statFs = new StatFs(file.getPath());
        long blockSizeLong = 0;
        long blockCountLong = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSizeLong = statFs.getBlockSizeLong();
            blockCountLong = statFs.getBlockCountLong();
        }
        long size = blockCountLong * blockSizeLong;
        return Formatter.formatFileSize(context, size);
    }

    /**
     * 获取内部可用存储空间
     *
     * @param context 上下文
     * @return 以M, G为单位的容量
     */
    public static String getAvailableInternalMemorySize(Context context) {
        File file = Environment.getDataDirectory();
        StatFs statFs = new StatFs(file.getPath());
        long availableBlocksLong = 0;
        long blockSizeLong = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            availableBlocksLong = statFs.getAvailableBlocksLong();
            blockSizeLong = statFs.getBlockSizeLong();
        }
        return Formatter.formatFileSize(context, availableBlocksLong
                * blockSizeLong);
    }

    /**
     * 获取外部存储空间
     *
     * @param context 上下文
     * @return 以M, G为单位的容量
     */
    public static String getTotalExternalMemorySize(Context context) {
        File file = Environment.getExternalStorageDirectory();
        StatFs statFs = new StatFs(file.getPath());
        long blockSizeLong = 0;
        long blockCountLong = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSizeLong = statFs.getBlockSizeLong();
            blockCountLong = statFs.getBlockCountLong();
        }
        return Formatter
                .formatFileSize(context, blockCountLong * blockSizeLong);
    }

    /**
     * 获取外部可用存储空间
     *
     * @param context 上下文
     * @return 以M, G为单位的容量
     */
    public static String getAvailableExternalMemorySize(Context context) {
        File file = Environment.getExternalStorageDirectory();
        StatFs statFs = new StatFs(file.getPath());
        long availableBlocksLong = 0;
        long blockSizeLong = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            availableBlocksLong = statFs.getAvailableBlocksLong();
            blockSizeLong = statFs.getBlockSizeLong();
        }
        return Formatter.formatFileSize(context, availableBlocksLong
                * blockSizeLong);
    }

    /**
     * 内存空间是否过低？
     * @param context
     * @return outInfo.availMem < (homeAppMem + ((cachedAppMem-homeAppMem)/2));
     */
    public static boolean isLowMemory(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        manager.getMemoryInfo(info);
        return info.lowMemory;
    }

    //==============================================================================================
    /**
     * 获取设备 RAM 信息
     */
    public static String getRAMInfo(Context context) {
        long totalSize = 0L;
        long availableSize;

        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        if (activityManager == null) return "可用/总共：0/0B";
        activityManager.getMemoryInfo(memoryInfo);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            totalSize = memoryInfo.totalMem;
        }
        availableSize = memoryInfo.availMem;

        return "可用/总共：" + Formatter.formatFileSize(context, availableSize)
                + "/" + Formatter.formatFileSize(context, totalSize);
    }

    /**
     * 获取手机存储 ROM 信息
     * <p>
     * type： 用于区分内置存储于外置存储的方法
     * <p>
     * 内置SD卡 ：STORAGE_INTERNAL = 0;
     * <p>
     * 外置SD卡： STORAGE_EXTERNAL = 1;
     **/
    public static String getStorageInfo(Context context, int type) {

        String path = getStoragePath(context, type);

        if (TextUtils.isEmpty(path) || path == null) {
            return "无外置SD卡";
        }

        File file = new File(path);
        StatFs statFs = new StatFs(file.getPath());
        String stotageInfo;

        long blockCount = 0;
        long blockSize = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockCount = statFs.getBlockCountLong();
            blockSize = statFs.getBlockSizeLong();
        }
        long totalSpace = blockSize * blockCount;

        long availableBlocks = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            availableBlocks = statFs.getAvailableBlocksLong();
        }
        long availableSpace = availableBlocks * blockSize;

        stotageInfo = "可用/总共："
                + Formatter.formatFileSize(context, availableSpace) + "/"
                + Formatter.formatFileSize(context, totalSpace);

        return stotageInfo;

    }

    /**
     * 使用反射方法 获取存储路径
     **/
    public static String getStoragePath(Context context, int type) {

        StorageManager sm = (StorageManager) context
                .getSystemService(Context.STORAGE_SERVICE);
        if (sm == null) return null;
        try {
            Method getPathsMethod = sm.getClass().getMethod("getVolumePaths",
                    (Class<?>) null);
            String[] path = (String[]) getPathsMethod.invoke(sm, (Object[]) null);

            switch (type) {
                case STORAGE_INTERNAL:
                    return path[type];
                case STORAGE_EXTERNAL:
                    if (path.length > 1) {
                        return path[type];
                    } else {
                        return null;
                    }

                default:
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //==========================================文件、夹大小=========================================
    /**
     * 获取指定文件的大小
     * @param path
     * @return
     */
    public static long getFileSize(String path) {
        File file = new File(path);
        return getFileSize(file);
    }

    public static long getFileSize(File file) {
        long size = -1;
        if (file.exists() && file.isFile()) {
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

    /**
     * 获取指定文件夹的大小
     * @param dir
     * @return
     */
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

    //===========================================清空缓存============================================
    public static void clearCookies() {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookies(null);
        //You can pass null as the callback if you don't need to know when the operation completes
        //or whether any cookies were removed, and in this case it is safe to call the method from a thread without a Looper.
    }
}
