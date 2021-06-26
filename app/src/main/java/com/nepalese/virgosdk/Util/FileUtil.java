package com.nepalese.virgosdk.Util;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import com.nepalese.virgosdk.Manager.RuntimeExec;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;

/**
 * @author nepalese on 2020/10/18 10:40
 * @usage
 * 1. 路径：
 *      设备外部存储根目录，apk安装根目录，内部存储根目录，apk内部存储根目录；
 * 2. 文件，文件夹：
 *      增删改查，文件夹下文件个数，
 *      内容读写，重命名，复制，移动
 *      文件编码方式判断
 */
public class FileUtil {
    private static final String TAG = "FileUtil";

    //==========================================获取路径=============================================
    /**
     * 获取设备外部存储根目录：
     * @return /storage/emulated/0
     */
    public static String getRootPath(){
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /**
     * 获取apk安装根目录
     * @param context
     * @return /storage/emulated/0/Android/data/pack_name/files
     */
    public static String getAppRootPth(Context context){
        return context.getExternalFilesDir("").getAbsolutePath();
    }

    /**
     * 内部存储根目录
     * @return /data
     */
    public static String getInternalPath(){
        return Environment.getDataDirectory().getAbsolutePath();
    }

    /**
     * apk 内部存储根目录
     * @param context
     * @return /data/user/0/packname/files
     */
    public static String getInternalAppPath(Context context){
        return context.getFilesDir().getAbsolutePath();
    }

    //=========================================创建文件、夹==========================================
    /**
     * 创建文件
     * @param path 路径
     * @param fileName 文件名
     * @return 是否创建成功
     */
    public static boolean createFile(String path, String fileName){
        File file = new File(path+File.separator+fileName);

        if(!file.exists()){
            try {
                return file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 创建文件夹
     * @param folder 文件夹路径（可多层）
     * @return 是否创建成功
     */
    public static boolean createDirs(String folder){
        File file = new File(folder);

        if(!file.exists()) {
            return file.mkdirs();
        }
        return false;
    }

    //==========================================删除文件、夹=========================================
    /**
     * 删除文件或文件夹（及里面的所有内容）
     * @param path 文件、夹路径
     * @return 是否成功删除（目标不存在也相当于删除成功）
     */
    public static boolean delFileAndDir(String path) {
        File file = new File(path);
        return delFileAndDir(file);
    }

    /**
     * 删除文件或文件夹（及里面的所有内容）
     * @param file 文件、夹对象
     * @return 是否成功删除（目标不存在也相当于删除成功）
     */
    public static boolean delFileAndDir(File file) {
        if(file.exists()){
            if(file.isFile()){//文件
                return file.delete();
            }else{//文件夹
                File[] temp = file.listFiles();
                if(temp!=null && temp.length>0){
                    for(File f : temp){
                        delFileAndDir(f);
                    }
                }
                //删除空文件夹
                return file.delete();
            }
        }
        return true;
    }

    /**
     * 强制性删除文件或文件夹：命令行
     * @param path 文件路径
     */
    public static void deleteForce(String path) {
        RuntimeExec.getInstance().executeCommand("rm -rf " + path);
    }

    //======================================文件、文件夹重命名========================================
    /**
     * 文件、文件夹重命名
     * @param path 文件、文件夹父路径
     * @param oldName 原名
     * @param newName 新名
     * @return 是否重命名成功
     */
    public static boolean renameFileOrDir(String path, String oldName, String newName){
        File oldFile = new File(path+File.separator+oldName);
        File newFile = new File(path+File.separator+newName);

        return oldFile.renameTo(newFile);
    }

    //====================================获取文件夹下文件信息========================================
    /**
     * 获取文件夹下文件个数（不深究,仅第一层）
     * @param dir 文件夹路径
     * @return 文件个数
     */
    public static int getFilesNumber(String dir){
        File file = new File(dir);
        if(!file.exists()){
            return 0;
        }
        if (file.isFile()){
            return 1;
        }

        String[] list = file.list();
        if(list==null){
            return 0;
        }
        return list.length;
    }

    /**
     * 获取某个文件夹下所有文件（包括子文件夹内）个数
     * @param dir 文件夹路径
     * @return 所有文件个数
     */
    public static int getAllFileNumbers(String dir){
        File file = new File(dir);
        if(!file.exists()){
            return 0;
        }

        if (file.isFile()){
            return 1;
        }else{
            return getAllFileNum(file);
        }
    }

    private static int getAllFileNum(File file){
        if (file.isFile()){
            return 1;
        }else{
            int num = 0;
            File[] files = file.listFiles();
            if (files != null) {
                for(File f: files){
                    num += getAllFileNum(f);
                }
            }

            return num;
        }
    }

    //===========================================内容读写============================================
    /**
     * 将string写入到文件（若文件存在，则重置）
     * @param content   内容
     * @param path      存储路径
     * @param fileName  文件名
     */
    public static void writeToFile(String content, String path, String fileName){
        File file = new File(path + File.separator + fileName);
        if(!file.exists()){
            createFile(path, fileName);
        }

        RandomAccessFile randomAccessFile;
        try {
            randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.seek(0);//重置
            randomAccessFile.write(content.getBytes());

            randomAccessFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将bytes写入到文件中
     * @param bytes             字节流
     * @param outputFilePath    存储位置（包含名字）
     */
    public static void writeByte2File(byte[] bytes, String outputFilePath) {
        BufferedOutputStream outputStream = null;
        try {
            File file = new File(outputFilePath);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            outputStream = new BufferedOutputStream(fileOutputStream);
            outputStream.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * 读取文件(文本）
     * @param path      文件路径
     * @param format    文件编码方式（默认utf-8）
     * @return          字符串
     */
    public static String readTxtFile(String path, String format){
        File file = new File(path);
        if(!file.exists()){
            return null;
        }

        FileInputStream inputStream = null;
        try{
            inputStream = new FileInputStream(file);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }

        return getString(inputStream, format);
    }

    /**
     * 读取资源内的文本文件
     * @param context
     * @param id        资源id
     * @param format    文件编码方式（默认utf-8）
     * @return          字符串
     */
    public static String readTxtResource(Context context, int id, String format){
        InputStream inputStream = context.getResources().openRawResource(id);
        return getString(inputStream, format);
    }

    /**
     * 读取文件 返回文件流
     * @param path      文件路径
     * @return byte[]   字节流
     */
    public static byte[] readFile2Bytes(String path){
        File file = new File(path);
        if(!file.exists()){
            return null;
        }

        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(file);
            long size = inputStream.getChannel().size();
            if(size<=0){//空文件
                return null;
            }

            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);//return the total number of bytes read into the buffer,

            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 将输入流写入到输出流
     * @param input
     * @param output
     */
    public static void inputOutputStream(InputStream input, FileOutputStream output) {
        byte[] buffer = new byte[1024];
        try {
            while (true) {
                int length;
                if ((length = input.read(buffer)) == -1) {
                    break;
                }
                output.write(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                input.close();
                output.flush();
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将输入流转换为字符串
     * @param inputStream   输入流
     * @param format        文件编码方式（默认utf-8）
     * @return              字符串
     */
    public static String getString(InputStream inputStream, String format) {
        if(format==null){
            format = "utf-8";
        }
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(inputStream, format);//'utf-8' 'GBK'
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuilder builder = new StringBuilder();
        String line;
        try {
            while((line=reader.readLine())!=null){
                builder.append(line);
                builder.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return builder.toString();
    }

    /**
     * 将输入流转换为字符串
     * @param inStream  输入流
     * @param format    文件编码方式（默认utf-8）
     * @return          字符串
     */
    public static String getString2(InputStream inStream, String format){
        if(format==null){
            format = "utf-8";
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];

        while (true) {
            int len;
            try {
                if ((len = inStream.read(buffer)) == -1) break;
                outputStream.write(buffer, 0, len);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            inStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] data = outputStream.toByteArray();

        String out = null;
        try {
            out = new String(data, format);//StandardCharsets.UTF_8
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return out;
    }

    //============================================文件复制、移动=====================================
    /**
     * 复制文件
     * @param src   目标文件路径
     * @param dest  复制后路径
     */
    public static void copyFile(String src, String dest){
        byte[] bytes = readFile2Bytes(src);
        writeByte2File(bytes, dest);
    }

    public static boolean copyFile2(String src, String dest) {
        InputStream from = null;
        OutputStream to = null;
        try {
            from = new FileInputStream(src);
            to = new FileOutputStream(dest);
            byte[] buffer = new byte[1024 * 8];
            int length;
            while ((length = from.read(buffer)) != -1) {
                to.write(buffer, 0, length);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            if (from != null) {
                try {
                    from.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (to != null) {
                try {
                    to.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        File file = new File(dest);
        if (file.exists()) {
            return file.setLastModified(System.currentTimeMillis());
        }
        return false;
    }

    public static void copyFile3(String src, String dest){
        RuntimeExec.getInstance().executeCommand(RuntimeExec.CP + src + " " + dest);
    }

    /**
     * 需root权限
     * @param src
     * @param dest
     */
    public static void copyForce(String src, String dest){
        RuntimeExec.getInstance().executeRootCommand(RuntimeExec.CP + src + " " + dest);
    }

    /**
     * 移动文件：先复制文件，然后删除原文件
     * @param src
     * @param dest
     */
    public static void moveFile(String src, String dest){
        copyFile(src, dest);
        delFileAndDir(src);
    }

    //======================================文件编码类型：gbk ? utf-8================================
    /**
     * 判断文件编码格式是否为 utf-8
     * @param path 文件路径
     * @return
     */
    public static Boolean isUtf8(String path) {
        boolean isUtf8 = true;
        byte[] buffer = FileUtil.readFile2Bytes(path);
        assert buffer != null;
        int end = buffer.length;
        for (int i = 0; i < end; i++) {
            byte temp = buffer[i];
            if ((temp & 0x80) == 0) {// 0xxxxxxx
                continue;
            } else if ((temp & 0xC0) == 0xC0 && (temp & 0x20) == 0) {// 110xxxxx 10xxxxxx
                if (i + 1 < end && (buffer[i + 1] & 0x80) == 0x80 && (buffer[i + 1] & 0x40) == 0) {
                    i = i + 1;
                    continue;
                }
            } else if ((temp & 0xE0) == 0xE0 && (temp & 0x10) == 0) {// 1110xxxx 10xxxxxx 10xxxxxx
                if (i + 2 < end && (buffer[i + 1] & 0x80) == 0x80 && (buffer[i + 1] & 0x40) == 0
                        && (buffer[i + 2] & 0x80) == 0x80 && (buffer[i + 2] & 0x40) == 0) {
                    i = i + 2;
                    continue;
                }
            } else if ((temp & 0xF0) == 0xF0 && (temp & 0x08) == 0) {// 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
                if (i + 3 < end && (buffer[i + 1] & 0x80) == 0x80 && (buffer[i + 1] & 0x40) == 0
                        && (buffer[i + 2] & 0x80) == 0x80 && (buffer[i + 2] & 0x40) == 0
                        && (buffer[i + 3] & 0x80) == 0x80 && (buffer[i + 3] & 0x40) == 0) {
                    i = i + 3;
                    continue;
                }
            }
            isUtf8 = false;
            break;
        }
        return isUtf8;
    }

    public static Boolean isGbk(String path) {
        boolean isGbk = true;
        byte[] buffer = FileUtil.readFile2Bytes(path);
        assert buffer != null;
        int end = buffer.length;
        for (int i = 0; i < end; i++) {
            byte temp = buffer[i];
            if ((temp & 0x80) == 0) {
                continue;// B0A1-F7FE//A1A1-A9FE
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if ((Byte.toUnsignedInt(temp) < 0xAA && Byte.toUnsignedInt(temp) > 0xA0)
                        || (Byte.toUnsignedInt(temp) < 0xF8 && Byte.toUnsignedInt(temp) > 0xAF)) {
                    if (i + 1 < end) {
                        if (Byte.toUnsignedInt(buffer[i + 1]) < 0xFF && Byte.toUnsignedInt(buffer[i + 1]) > 0xA0
                                && Byte.toUnsignedInt(buffer[i + 1]) != 0x7F) {
                            i = i + 1;
                            continue;
                        }
                    } // 8140-A0FE
                } else if (Byte.toUnsignedInt(temp) < 0xA1 && Byte.toUnsignedInt(temp) > 0x80) {
                    if (i + 1 < end) {
                        if (Byte.toUnsignedInt(buffer[i + 1]) < 0xFF && Byte.toUnsignedInt(buffer[i + 1]) > 0x3F
                                && Byte.toUnsignedInt(buffer[i + 1]) != 0x7F) {
                            i = i + 1;
                            continue;
                        }
                    } // AA40-FEA0//A840-A9A0
                } else if ((Byte.toUnsignedInt(temp) < 0xFF && Byte.toUnsignedInt(temp) > 0xA9)
                        || (Byte.toUnsignedInt(temp) < 0xAA && Byte.toUnsignedInt(temp) > 0xA7)) {
                    if (i + 1 < end) {
                        if (Byte.toUnsignedInt(buffer[i + 1]) < 0xA1 && Byte.toUnsignedInt(buffer[i + 1]) > 0x3F
                                && Byte.toUnsignedInt(buffer[i + 1]) != 0x7F) {
                            i = i + 1;
                            continue;
                        }
                    }
                }
            }
            isGbk = false;
            break;
        }
        return isGbk;
    }
}
