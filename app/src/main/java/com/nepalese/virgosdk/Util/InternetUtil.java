package com.nepalese.virgosdk.Util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author nepalese on 2020/11/3 14:42
 * @usage 网络连接：需在子线程运行
 */
public class InternetUtil {
    private static final String TAG = "InternetUtil";

    /**
     * 从有效图片URL连接获取数据流，解析为bitmap便于显示
     * @param imgUrl
     * @return bitmap
     */
    public static Bitmap getBitmapFromUrl(String imgUrl){
        Bitmap bitmap = null;
        try {
            URL url = new URL(imgUrl);
            InputStream inputStream = url.openStream();

            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 将图片url先解析成bitmap, 然后保存到本地
     * @param imgUrl
     * @param path
     * @param fileName
     */
    public static void downloadImgFromUrl(String imgUrl, String path, String fileName) {
        Bitmap bitmap = getBitmapFromUrl((imgUrl));
        BitmapUtil.saveBitmap2File(bitmap, path, fileName);
    }

    /**
     * 从有效资源文件url下载资源到本地（类型不限）
     * @param strUrl
     * @param path
     * @param fileName
     */
    public static void downloadFile(String strUrl, String path, String fileName) {
        File file = new File(path+File.separator+fileName);

        if (file.exists()) {
            //do nothing
        } else {
            try {
                URL url = new URL(strUrl);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();;
                http.connect();
                int code = http.getResponseCode();
                if(code!=200){
                    Log.e(TAG, "connect failed!!!");
                    return;
                }
                //int length = http.getContentLength();

                InputStream inputStream = http.getInputStream();
                byte[] buffer = new byte[1024];
                int len;
                OutputStream outputStream = new FileOutputStream(file);
                while ((len = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                }
                Log.i(TAG, "download successful!");
                outputStream.close();
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* : 检测当前url是否有效可连, 需在子线程进行
     * @param url 需校验的url
     * @param max 最大尝试次数
     * @return
     */
    public boolean validUrl(String urlStr, int max) {
        if(urlStr == null || urlStr.length()<=0) {
            return false;
        }

        int count = 1;
        int code;
        HttpURLConnection connection;

        while(count<=max) {
            try {
                URL url = new URL(urlStr);
                connection = (HttpURLConnection) url.openConnection();
                code = connection.getResponseCode();
                Log.d(TAG, "validUrl: count: " + count + "\tcode: " + code);

                if(code==200) {
                    //有效url
                    return true;
                }
                //code != 200
                count++;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                //出现异常
                count++;
            }
        }
        return false;
    }
}