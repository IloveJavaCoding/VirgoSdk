package com.nepalese.virgosdk.Util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import androidx.annotation.RequiresPermission;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

/**
 * @author nepalese on 2020/11/18 12:30
 * @usage 获取设备硬件信息：设备id，IP地址，
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
}
