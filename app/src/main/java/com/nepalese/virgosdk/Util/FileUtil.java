package com.nepalese.virgosdk.Util;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

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
import java.nio.charset.StandardCharsets;

/**
 * @author nepalese on 2020/10/18 10:40
 * @usage 文件，文件夹：增删改查，复制，移动，重命名， 存储路径获取， 文件编码方式判断
 */
public class FileUtil {
    private static final String TAG = "FileUtil";

    //==================================get external file path======================================
    public static String getRootPath(){
        //storage/emulated/0
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static String getAppRootPth(Context context){
        ///storage/emulated/0/Android/data/pack_name/files
        return context.getExternalFilesDir("").getAbsolutePath();
    }

    public static String getInternalPath(){
        // /data
        return Environment.getDataDirectory().getAbsolutePath();
    }

    public static String getInternalAppPath(Context context){
        // /data/user/0/packname/files
        return context.getFilesDir().getAbsolutePath();
    }

    //=====================================create file/dir==========================================
    public static boolean createFile(String path, String fileName){
        File file = new File(path+"/"+fileName);

        if(!file.exists()){
            try {
                return file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean createDirs(String folder){
        File file = new File(folder);

        if(!file.exists()) {
            return file.mkdirs();
        }
        return false;
    }


    //==========================================delete file/dir=====================================
    public static boolean deleteDir(String dir){
        File file = new File(dir);
        if(file.exists()){
            return deleteDirWithFile(file);
        }
        return false;
    }

    private static boolean deleteDirWithFile(File file) {
        if(!file.isDirectory() || !file.exists()) {
            return false;
        }
        if(file.list().length>0){
            for(File f : file.listFiles()){
                if(f.isFile()){
                    f.delete();//delete all files
                }else if(f.isDirectory()){
                    deleteDirWithFile(f);
                }
            }
        }

        return file.delete();
    }

    public static boolean deleteFile(String path){
        File file = new File(path);
        if(file.exists()&&file.isFile()){
            return file.delete();
        }

        return false;
    }

    //强制性删除
    public void deleteForce(String path) {
        RuntimeExec.getInstance().executeCommand("rm -rf " + path);
    }

    //=====================================change file/dir name=====================================
    public static boolean changeFileDirName(String path, String oldName, String newName){
        File oldFile = new File(path+"/"+oldName);
        File newFile = new File(path+"/"+newName);

        return oldFile.renameTo(newFile);
    }


    //====================================get file/dir info=========================================
    public static int getFilesNumber(String dir){
        File file = new File(dir);
        return file.listFiles().length;
    }

    public static String[] getFilesList(String dir){
        File file = new File(dir);
        return file.list();
    }


    //====================================read and write content====================================
    public static void writeToFile(String content, String path, String fileName){
        File file = new File(path+fileName);
        if(!file.exists()){
            createFile(path, fileName);
        }

        RandomAccessFile randomAccessFile;
        try {
            randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.seek(0);//rewrite
            randomAccessFile.write(content.getBytes());

            randomAccessFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //将bytes写入到文件中
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
                    outputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * 读取文件
     * @param path
     * @param format 文件编码方式（默认utf-8）
     * @return 字符串
     */
    public static String readFile2String(String path, String format){
        if(format==null){
            format = "utf-8";
        }

        File file = new File(path);
        if(!file.exists()){
            return null;
        }

        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
        }
        return builder.toString();
    }

    //读取资源文件到string
    public static String readResource2String(Context context, int id, String format){
        InputStream inputStream = context.getResources().openRawResource(id);
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
        }
        return builder.toString();
    }

    //读取文件, 返回为bytes
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
            inputStream.read(bytes);//the total number of bytes read into the buffer,

            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void readerWriterStream(InputStream input, FileOutputStream output) {
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

    //将输入流转换为字符串
    public static String parseStream2Str(InputStream inStream, String format){
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

        if(inStream!=null){
            try {
                inStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    //============================================copy/move file====================================
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

    //需root权限
    public static void copyForce(String src, String dest){
        RuntimeExec.getInstance().executeRootCommand(RuntimeExec.CP + src + " " + dest);
    }

    public static void moveFile(String src, String dest){
        //先复制文件，然后删除原文件
        copyFile(src, dest);
        deleteFile(src);
    }

    //==================================文件编码类型：gbk ? utf-8=========================
    public static Boolean isUtf8(File file) {
        boolean isUtf8 = true;
        byte[] buffer = FileUtil.readFile2Bytes(file.getPath());
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

    public static Boolean isGbk(File file) {
        boolean isGbk = true;
        byte[] buffer = FileUtil.readFile2Bytes(file.getPath());
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
