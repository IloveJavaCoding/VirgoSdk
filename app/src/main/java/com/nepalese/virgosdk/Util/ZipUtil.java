package com.nepalese.virgosdk.Util;

import android.text.TextUtils;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author nepalese on 2020/11/18 14:19
 * @usage 文件压缩，解压
 * Lib: zip4j-1.3.2.jar
 */
public class ZipUtil {
    //===========================zip/unzip file/dir==============================
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
            unZipFile(file, parentDir.getAbsolutePath(), password);
        }
    }

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
        File destDir = null;
        if (destParam.endsWith(File.separator)) {
            destDir = new File(destParam);
        } else {
            destDir = new File(destParam.substring(0, destParam.lastIndexOf(File.separator)));
        }
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
    }
}
