package com.nepalese.virgosdk.Helper;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * @author nepalese on 2020/11/19 15:10
 * @usage 压缩图片
 * Lib: lib_compress_v1.03.4.jar
 */
public class CompressImgHelper {
    private static final String TAG = "CompressImgHelper";

    /**
     * 压缩图片到指定大小
     * @param context
     * @param src
     * @param desPath
     * @param limitSize 不压缩的阈值，单位为KB
     * @param callback
     */
    public static void compress(Context context, String src, final String desPath,
                                int limitSize, final Callback callback) {
        if (src != null) {
            //创建鲁班压缩
            Luban.with(context)
                    .ignoreBy(limitSize)
                    .filter(path -> !(TextUtils.isEmpty(path) /*|| path.toLowerCase().endsWith(".gif")*/))
                    .load(src)
                    .setTargetDir(desPath)  //缓存压缩图片路径
                    .setCompressListener(new OnCompressListener() {
                        @Override
                        public void onStart() {
                            //  压缩开始前调用，可以在方法内启动 loading UI
                            Log.v(TAG, ("onCompress start"));
                        }

                        @Override
                        public void onSuccess(File file) {
                            //  压缩成功后调用，返回压缩后的图片文件
                            if (file != null) {
                                Log.v(TAG, "压缩成功 = " + file.getAbsolutePath() + " \nsize = " + file.length());
                                if (callback != null) {
                                    callback.onCompressResult(file.getAbsolutePath());
                                }
                            } else {
                                if (callback != null) {
                                    callback.onCompressResult("");
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            //  当压缩过程出现问题时调用
                            Log.e(TAG, "压缩异常" + e);
                            if (callback != null) {
                                callback.onCompressResult("");
                            }
                        }
                    }).launch();
        }
    }

    public interface Callback{
        void onCompressResult(String path);
    }
}