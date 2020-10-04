package com.nepalese.virgosdk.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.RequiresPermission;

import com.nepalese.virgosdk.Beans.NetworkType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * to connect the internet, it must be run in a background thread
 */
public class InternetUtil {
    private static final String TAG = "InternetUtil";
    //从有效图片URL连接获取数据流，解析为bitmap便于显示
    public static Bitmap getBitmapFromUrl(String imgUrl){
        Bitmap bitmap = null;
        try {
            URL url = new URL(imgUrl);
            InputStream inputStream = url.openStream();

            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    //将图片url先解析成bitmap, 然后保存到本地
    public static void downloadImgFromUrl(String imgUrl, String path, String fileName) {
        Bitmap bitmap = getBitmapFromUrl((imgUrl));
        BitmapUtil.bitmap2Local(bitmap, path, fileName);
    }

    //从有效资源文件url下载资源到本地（类型不限）
    public static void downloadFile(String strUrl, String path, String fileName) {
        File file = new File(path+File.separator+fileName);

        if (file.exists()) {
            //--------------------------
        } else {
            try {
                URL url = new URL(strUrl);

                //===============================================
                HttpURLConnection http = (HttpURLConnection) url.openConnection();;
                http.connect();
                int code = http.getResponseCode();
                if(code!=200){
                    //connect error
                    Log.d(TAG, "connect failed!!!");
                    return;
                }
                //length of file
                int length = http.getContentLength();
                //================================================

                InputStream inputStream = http.getInputStream();

                byte[] buffer = new byte[1024];
                int len;
                OutputStream outputStream = new FileOutputStream(file);
                while ((len = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                }
                Log.d(TAG, "download successful!");
                outputStream.close();
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static NetworkInfo getActiveNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    @RequiresPermission("android.permission.ACCESS_NETWORK_STATE")
    public static NetworkType getNetworkType(Context context) {
        NetworkType netType = NetworkType.NETWORK_NO;
        NetworkInfo info = getActiveNetworkInfo(context);
        if (info != null && info.isAvailable()) {
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                netType = NetworkType.NETWORK_WIFI;
            } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                switch (info.getSubtype()) {
                    case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        netType = NetworkType.NETWORK_3G;
                        break;

                    case TelephonyManager.NETWORK_TYPE_LTE:
                    case TelephonyManager.NETWORK_TYPE_IWLAN:
                        netType = NetworkType.NETWORK_4G;
                        break;

                    case TelephonyManager.NETWORK_TYPE_GSM:
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        netType = NetworkType.NETWORK_2G;
                        break;
                    default:
                        String subtypeName = info.getSubtypeName();
                        if (subtypeName.equalsIgnoreCase("TD-SCDMA")
                                || subtypeName.equalsIgnoreCase("WCDMA")
                                || subtypeName.equalsIgnoreCase("CDMA2000")) {
                            netType = NetworkType.NETWORK_3G;
                        } else {
                            netType = NetworkType.NETWORK_UNKNOWN;
                        }
                        break;
                }
            } else {
                netType = NetworkType.NETWORK_UNKNOWN;
            }
        }
        return netType;
    }

    //=======================================network type==============================
    public static String getNetWorkType(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager!=null){
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo!=null){
                int type = networkInfo.getType();
                switch (type){
                    case ConnectivityManager.TYPE_MOBILE:
                        return "\n(数据)";
                    case ConnectivityManager.TYPE_WIFI:
                        return "\n(WIFI)";
                    case ConnectivityManager.TYPE_ETHERNET:
                        return "\n(有线)";
                }
            }
        }
        return "";
    }
}
