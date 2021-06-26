package com.nepalese.virgosdk.Util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.telephony.TelephonyManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;

import androidx.annotation.RequiresPermission;

/**
 * @author nepalese on 2020/11/18 12:30
 * @usage 获取设备硬件信息：
 * 1. 设备id，IP地址；
 * 2, wifi 模块检测， wifi信号强度；
 * 3. 判断外置SD/TF卡是否挂载；
 * 4. 生产信息；
 */
public class HardwareUtil {
    /**
     * 获取设备唯一id
     *
     * @param context
     * @return IMEI for GSM or MEID for CDMA.
     */
    @RequiresPermission("android.permission.READ_PRIVILEGED_PHONE_STATE")
    public static String getDeviceId(Context context) {
        try {
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            //26
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                String imei = manager.getImei();
                return imei == null ? manager.getMeid() : imei;
            } else {
                return manager.getDeviceId();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取IP地址：
     *
     * @param context
     * @return
     */
    @RequiresPermission("android.permission.ACCESS_NETWORK_STATE")
    public static String getIpAddress(Context context) {
        ConnectivityManager conMann = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobileNetworkInfo = conMann.getNetworkInfo(0);
        NetworkInfo wifiNetworkInfo = conMann.getNetworkInfo(1);

        try {
            Enumeration enumerationNi = NetworkInterface.getNetworkInterfaces();

            label43:
            while (true) {
                NetworkInterface networkInterface;
                String interfaceName;
                do {
                    if (!enumerationNi.hasMoreElements()) {
                        break label43;
                    }

                    networkInterface = (NetworkInterface) enumerationNi.nextElement();
                    interfaceName = networkInterface.getDisplayName();
                } while (!interfaceName.equals("eth0"));

                Enumeration enumIpAddr = networkInterface.getInetAddresses();

                while (enumIpAddr.hasMoreElements()) {
                    InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
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
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
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
        } catch (SocketException ignored) {
        }

        return "unknown";
    }

    private static String intToIp(int ipInt) {
        return (ipInt & 255) + "." +
                (ipInt >> 8 & 255) + "." +
                (ipInt >> 16 & 255) + "." +
                (ipInt >> 24 & 255);
    }

    ///////////////////////////////////////////wifi 模块////////////////////////////////////////////

    /**
     * 检测wifi功能是否正常
     *
     * @param context
     * @return
     */
    public static boolean isWifiEnable(Context context) {
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
     *
     * @return WifiInfo
     */
    public static WifiInfo getWifiInfo(Context context) {
        // Wifi的连接速度及信号强度：
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return wifiManager.getConnectionInfo();
    }

    /////////////////////////////////////////TF 卡模块///////////////////////////////////////////////

    /**
     * 判断内存卡是否已插入 _> 内置sd卡
     * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"></uses-permission>
     *
     * @return
     */
    public static boolean hasTFCard() {
        String state = android.os.Environment.getExternalStorageState();
        return android.os.Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * 判断外置SD/TF卡是否挂载
     *
     * @param context
     * @return
     */
    public static boolean isExistTF(Context context) {
        boolean isMounted = false;
        StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);

        try {
            Method getVolumList = StorageManager.class.getMethod("getVolumeList", null);
            getVolumList.setAccessible(true);
            Object[] results = (Object[]) getVolumList.invoke(sm, null);
            if (results != null) {
                for (Object result : results) {
                    Method mRemoveable = result.getClass().getMethod("isRemovable", null);
                    Boolean isRemovable = (Boolean) mRemoveable.invoke(result, null);
                    if (isRemovable) {
                        Method getPath = result.getClass().getMethod("getPath", null);
                        String path = (String) getPath.invoke(result, null);
                        Method getState = sm.getClass().getMethod("getVolumeState", String.class);
                        String state = (String) getState.invoke(sm, path);
                        if (Environment.MEDIA_MOUNTED.equals(state)) {
                            isMounted = true;
                            break;
                        }
                    }
                }
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return isMounted;
    }

    /////////////////////////////////////////////生产信息////////////////////////////////////////////

    /**
     * 获取厂商名
     **/
    public static String getDeviceManufacturer() {
        return Build.MANUFACTURER;
    }

    /**
     * 获取产品名
     **/
    public static String getDeviceProduct() {
        return Build.PRODUCT;
    }

    /**
     * 获取手机品牌
     */
    public static String getDeviceBrand() {
        return Build.BRAND;
    }

    /**
     * 获取手机型号
     */
    public static String getDeviceModel() {
        return Build.MODEL;
    }

    /**
     * 获取手机主板名
     */
    public static String getDeviceBoard() {
        return Build.BOARD;
    }

    public static String getCpuABI() {
        return Build.CPU_ABI;
    }

    /**
     * 设备名
     **/
    public static String getDeviceDevice() {
        return Build.DEVICE;
    }

    /**
     * fingerprit 信息
     **/
    public static String getDeviceFubgerprint() {
        return Build.FINGERPRINT;
    }

    /**
     * 硬件名
     **/
    public static String getDeviceHardware() {
        return Build.HARDWARE;
    }

    /**
     * 主机
     **/
    public static String getDeviceHost() {
        return Build.HOST;
    }

    /**
     * 显示ID
     **/
    public static String getDeviceDisplay() {
        return Build.DISPLAY;
    }

    /**
     * ID
     **/
    public static String getDeviceId() {
        return Build.ID;
    }

    /**
     * 获取手机用户名
     **/
    public static String getDeviceUser() {
        return Build.USER;
    }

    /**
     * 获取手机 硬件序列号
     **/
    @SuppressLint("HardwareIds")
    public static String getDeviceSerial() {
        return Build.SERIAL;
    }

    /**
     * 获取手机Android 系统SDK
     */
    public static int getDeviceSDK() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * 获取手机Android 版本
     */
    public static String getDeviceAndroidVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取当前手机系统语言。
     */
    public static String getDeviceDefaultLanguage() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * 获取当前系统上的语言列表(Locale列表)
     */
    public static String getDeviceSupportLanguage() {
        return Arrays.toString(Locale.getAvailableLocales());
    }

    /**
     * 完整设备信息
     * @param context
     * @return
     */
    @SuppressLint("MissingPermission")
    public static String getDeviceAllInfo(Context context) {

        return "异常的设备信息： = "
                + "\n\n IMEI:\n\t\t" + getDeviceId(context)

                + "\n\n RAM 信息:\n\t\t" + StorageUtil.getRAMInfo(context)

                + "\n\n 内部存储信息\n\t\t" + StorageUtil.getStorageInfo(context, StorageUtil.STORAGE_INTERNAL)

                + "\n\n SD卡 信息:\n\t\t" + StorageUtil.getStorageInfo(context, StorageUtil.STORAGE_EXTERNAL)

                + "\n\n 是否联网:\n\t\t" + NetworkUtil.isConnected(context)

                + "\n\n 网络类型:\n\t\t" + NetworkUtil.getNetworkType(context)

                + "\n\n 系统默认语言:\n\t\t" + getDeviceDefaultLanguage()

                + "\n\n 硬件序列号(设备名):\n\t\t" + getDeviceSerial()

                + "\n\n 手机型号:\n\t\t" + getDeviceModel()

                + "\n\n 生产厂商:\n\t\t" + getDeviceManufacturer()

                + "\n\n 手机Fingerprint标识:\n\t\t" + getDeviceFubgerprint()

                + "\n\n Android 版本:\n\t\t" + getDeviceAndroidVersion()

                + "\n\n Android SDK版本:\n\t\t" + getDeviceSDK()

                + "\n\n 安全patch 时间:\n\t\t" + (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? Build.VERSION.SECURITY_PATCH : "低版本")

                + "\n\n 版本类型:\n\t\t" + Build.TYPE

                + "\n\n 用户名:\n\t\t" + Build.USER

                + "\n\n 产品名:\n\t\t" + Build.PRODUCT

                + "\n\n ID:\n\t\t" + Build.ID

                + "\n\n 显示ID:\n\t\t" + Build.DISPLAY

                + "\n\n 硬件名:\n\t\t" + Build.HARDWARE

                + "\n\n 产品名:\n\t\t" + Build.DEVICE

                + "\n\n Bootloader:\n\t\t" + Build.BOOTLOADER

                + "\n\n 主板名:\n\t\t" + getDeviceBoard()

                + "\n\n CodeName:\n\t\t" + Build.VERSION.CODENAME

                + "\n\n 语言支持:\n\t\t" + getDeviceSupportLanguage();
    }
}
