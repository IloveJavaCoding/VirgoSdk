package com.nepalese.virgosdk.Util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.RequiresPermission;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

/**
 * @author nepalese on 2020/11/18 12:30
 * @usage 获取设备硬件信息：
 * 设备id，IP地址，
 * wifi 模块检测， wifi信号强度，
 * 判断外置SD/TF卡是否挂载
 */
public class HardwareUtil {
    @RequiresPermission("android.permission.READ_PRIVILEGED_PHONE_STATE")
    public static String getDeviceId(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            return telephonyManager.getDeviceId();
        } catch (Exception var2) {
            return "";
        }
    }

    //获取IP地址：
    @RequiresPermission("android.permission.ACCESS_NETWORK_STATE")
    public static String getIpAddress(Context context) {
        ConnectivityManager conMann = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobileNetworkInfo = conMann.getNetworkInfo(0);
        NetworkInfo wifiNetworkInfo = conMann.getNetworkInfo(1);

        try {
            Enumeration enumerationNi = NetworkInterface.getNetworkInterfaces();

            label43:
            while(true) {
                NetworkInterface networkInterface;
                String interfaceName;
                do {
                    if (!enumerationNi.hasMoreElements()) {
                        break label43;
                    }

                    networkInterface = (NetworkInterface)enumerationNi.nextElement();
                    interfaceName = networkInterface.getDisplayName();
                } while(!interfaceName.equals("eth0"));

                Enumeration enumIpAddr = networkInterface.getInetAddresses();

                while(enumIpAddr.hasMoreElements()) {
                    InetAddress inetAddress = (InetAddress)enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception var10) {
            var10.printStackTrace();
        }

        String ip;
        if (wifiNetworkInfo.isConnected()) {
            WifiManager wifiManager = (WifiManager)context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            ip = intToIp(ipAddress);
        } else if (mobileNetworkInfo.isConnected()) {
            ip = getLocalIpAddress();
        } else {
            ip = "unknown";
        }

        return ip;
    }

    private static String getLocalIpAddress() {
        try {
            ArrayList<NetworkInterface> nilist = Collections.list(NetworkInterface.getNetworkInterfaces());

            for (NetworkInterface ni : nilist) {
                ArrayList<InetAddress> ialist = Collections.list(ni.getInetAddresses());

                for (InetAddress address : ialist) {
                    if (!address.isLoopbackAddress() && address instanceof Inet4Address) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (SocketException ignored) {}

        return "unknown";
    }

    private static String intToIp(int ipInt) {
        return (ipInt & 255) + "." +
                (ipInt >> 8 & 255) + "." +
                (ipInt >> 16 & 255) + "." +
                (ipInt >> 24 & 255);
    }

    ////////////////////////////////wifi 模块////////////////////////////////////////////
    //检测wifi功能是否正常
    public static boolean isWifiWell(Context context) {
        WifiManager localWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!localWifiManager.isWifiEnabled()) {
            boolean bool2 = localWifiManager.setWifiEnabled(true);
            localWifiManager.setWifiEnabled(false);
            return bool2;
        }
        boolean bool = localWifiManager.setWifiEnabled(false);
        localWifiManager.setWifiEnabled(true);
        return bool;
    }

    /**
     * 链接信号强度: WifiManager.calculateSignalLevel(info.getRssi(), 5);
     * 链接速度: info.getLinkSpeed();
     * 链接速度单位: WifiInfo.LINK_SPEED_UNITS;
     * Wifi源名称: info.getSSID();
     * @return WifiInfo
     */
    public static WifiInfo getWifiInfo(Context context) {
        // Wifi的连接速度及信号强度：
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return wifiManager.getConnectionInfo();
    }

    //////////////////////////////////TF 卡模块///////////////////////////////////////////////
    //判断内存卡是否已插入 _> 内置sd卡
    //<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"></uses-permission>
    public static boolean hasTFCard() {
        String state = android.os.Environment.getExternalStorageState();
        return android.os.Environment.MEDIA_MOUNTED.equals(state);
    }

    //判断外置SD/TF卡是否挂载
    public static boolean isExistTF(Context context) {
        boolean result = false;
        StorageManager mStorageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Method getState = storageVolumeClazz.getMethod("getState");
            Object obj = null;
            try {
                obj = getVolumeList.invoke(mStorageManager);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            final int length = Array.getLength(obj);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(obj, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                String state = (String) getState.invoke(storageVolumeElement);
                if (removable) {
                    assert state != null;
                    if (state.equals(Environment.MEDIA_MOUNTED)) {
                        result = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
