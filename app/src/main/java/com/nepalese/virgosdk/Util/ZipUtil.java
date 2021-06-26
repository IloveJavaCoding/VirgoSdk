package com.nepalese.virgosdk.Util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * @author nepalese on 2020/11/18 14:19
 * @usage 文件压缩，解压，图片压缩
 * Lib: zip4j-1.3.2.jar
 * Lib: lib_compress_v1.03.4.jar
 */
public class ZipUtil {
    private static final String TAG = "ZipUtil";
    //====================================zip/unzip file/dir========================================
    /**
     * 解压文件
     * @param path 压缩包文件路径
     * @param aimPath 解压到..位置
     * @param password 解压密码（与压缩对应）
     * @throws ZipException
     */
    public static void unZip(String path, String aimPath, String password) throws ZipException {
        File file = new File(path);
        if(!file.exists()){
            return;
        }

        unZipFile(file, aimPath, password);
    }

    public static void unZipFile(File file, String aimPath, String password) throws ZipException {
        ZipFile zipFile = new ZipFile(file);
        zipFile.setFileNameCharset("GBK");

        if (!zipFile.isValidZipFile()) {
            throw new ZipException("压缩文件不合法,可能被损坏.");
        }

        if(!TextUtils.isEmpty(aimPath)){
            File destDir = new File(aimPath);
            if (destDir.isDirectory() && !destDir.exists()) {
                destDir.mkdir();
            }
            if (zipFile.isEncrypted()) {
                zipFile.setPassword(password.toCharArray());
            }
            zipFile.extractAll(aimPath);
        }else{//unzip to current path
            File parentDir = file.getParentFile();
            assert parentDir != null;
            unZipFile(file, parentDir.getAbsolutePath(), password);
        }
    }

    /**
     * 压缩文件
     * @param path 需压缩文件、文件夹路径
     * @param aimPath 压缩到指定位置
     * @param password 压缩密码（null -> 不设置密码）
     * @return
     */
    public static boolean zipFile(String path, String aimPath, String password) {
        File file = new File(path);
        if (!file.exists()) {
            return false;
        }

        aimPath = buildDestinationZipFilePath(file, aimPath);
        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);            // 压缩方式
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);    // 压缩级别
        if (!TextUtils.isEmpty(password)) {
            parameters.setEncryptFiles(true);
            parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD);    // 加密方式
            parameters.setPassword(password.toCharArray());
        }
        try {
            ZipFile zipFile = new ZipFile(aimPath);
            if (file.isDirectory()) {
                // 如果不创建目录的话,将直接把给定目录下的文件压缩到压缩文件,即没有目录结构
                File[] subFiles = file.listFiles();
                ArrayList<File> temp = new ArrayList<File>();
                assert subFiles != null;
                Collections.addAll(temp, subFiles);
                zipFile.addFiles(temp, parameters);
            } else {
                zipFile.addFile(file, parameters);
            }
        } catch (ZipException e) {
            e.printStackTrace();
        }
        return true;
    }

    private static String buildDestinationZipFilePath(File srcFile, String destParam) {
        if (TextUtils.isEmpty(destParam)) {
            if (srcFile.isDirectory()) {
                destParam = srcFile.getParent() + File.separator + srcFile.getName() + ".zip";
            } else {
                String fileName = srcFile.getName().substring(0, srcFile.getName().lastIndexOf("."));
                destParam = srcFile.getParent() + File.separator + fileName + ".zip";
            }
        } else {
            createDestDirectoryIfNecessary(destParam);    // 在指定路径不存在的情况下将其创建出来
            if (destParam.endsWith(File.separator)) {
                String fileName = "";
                if (srcFile.isDirectory()) {
                    fileName = srcFile.getName();
                } else {
                    fileName = srcFile.getName().substring(0, srcFile.getName().lastIndexOf("."));
                }
                destParam += fileName + ".zip";
            }
        }
        return destParam;
    }

    private static void createDestDirectoryIfNecessary(String destParam) {
        File destDir;
        if (destParam.endsWith(File.separator)) {
            destDir = new File(destParam);
        } else {
            destDir = new File(destParam.substring(0, destParam.lastIndexOf(File.separator)));
        }
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
    }

    //======================================压缩图片大小=============================================
    /**
     * 压缩图片到指定大小
     * @param context
     * @param src 需压缩图片路径
     * @param desPath 压缩后存储路径
     * @param limitSize 不压缩的阈值，单位为KB
     * @param callback 结果回调
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
