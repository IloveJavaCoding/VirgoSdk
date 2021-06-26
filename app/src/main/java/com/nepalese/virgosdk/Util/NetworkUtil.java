package com.nepalese.virgosdk.Util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.nepalese.virgosdk.Beans.BaseBean;

import java.lang.reflect.Method;
import java.util.List;

import androidx.annotation.RequiresPermission;

/**
 * @author nepalese on 2020/11/18 08:49
 * @usage
 * 1. 判断有无sim卡， 获取SIM运营商；
 * 2. 数据流量是否打开及开关控制；
 * 1. 获取网络连接类型；
 * 2. 流量使用情况；
 */
public class NetworkUtil {
    private static final String TAG = "NetworkUtil";

    public static final int NETWORK_NONE = 0;      //没有网络连接
    public static final int NETWORK_INTERNET = 1;  //有线连接
    public static final int NETWORK_WIFI = 2;      //wifi连接
    //手机网络数据连接类型
    public static final int NETWORK_2G = 3;
    public static final int NETWORK_3G = 4;
    public static final int NETWORK_4G = 5;
    public static final int NETWORK_5G = 6;
    public static final int NETWORK_MOBILE = 7;

    public static final int TYPE_NO_SIM = -1; //无sim卡
    public static final int TYPE_UNKNOWN = 0; //未知运营商
    public static final int TYPE_CMCC = 1; //中国移动
    public static final int TYPE_CUCC = 2; //中国联通
    public static final int TYPE_CTCC = 3; //中国电信

    private static volatile NetworkUtil instance;
    private final TelephonyManager telephonyManager;

    public static NetworkUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (NetworkUtil.class) {
                if (instance == null) {
                    instance = new NetworkUtil(context);
                }
            }
        }
        return instance;
    }

    private NetworkUtil(Context context) {
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    //========================================getInstancc===========================================
    /**
     * 检查设备是否插入sim卡
     * @return
     */
    public boolean hasSim() {
        String operator = telephonyManager.getSimOperator();
        return TextUtils.isEmpty(operator);
    }

    /**
     * 判断数据流量开关是否打开
     * @return
     */
    public boolean isMobileDataEnabled() {
        try {
            Method getDataEnabled = telephonyManager.getClass().getDeclaredMethod("getDataEnabled");
            return (Boolean) getDataEnabled.invoke(telephonyManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 设置数据流量开关
     * @param enabled
     */
    public void setMobileDataEnabled(boolean enabled) {
        try {
            Method setDataEnabled = telephonyManager.getClass().getDeclaredMethod("setDataEnabled", boolean.class);
            setDataEnabled.invoke(telephonyManager, enabled);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取设备拨号运营商
     * @return
     */
    public int getSimOperatorType() {
        int opeType = TYPE_NO_SIM;

        // No sim
        if (hasSim()) {
            return opeType;
        }

        String operator = telephonyManager.getNetworkOperator();
        // 中国移动
        if ("46000".equals(operator) || "46002".equals(operator) || "46004".equals(operator) || "46007".equals(operator)) {
            opeType = TYPE_CMCC;
        }
        // 中国联通
        else if ("46001".equals(operator) || "46006".equals(operator) || "46009".equals(operator)) {
            opeType = TYPE_CUCC;
        }
        // 中国电信
        else if ("46003".equals(operator) || "46005".equals(operator) || "46011".equals(operator)) {
            opeType = TYPE_CTCC;
        }
        else {
            opeType = TYPE_UNKNOWN;
        }
        return opeType;
    }

    //==============================================网络连接类型=====================================
    /**
     * 获取当前网络连接类型
     * @param context
     * @return
     */
    @RequiresPermission("android.permission.ACCESS_NETWORK_STATE")
    public static int getNetworkType(Context context) {
        //获取系统的网络服务
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //如果当前没有网络
        if (null == connManager)
            return NETWORK_NONE;

        //获取当前网络类型，如果为空，返回无网络
        NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
        if (activeNetInfo == null || !activeNetInfo.isAvailable()) {
            return NETWORK_NONE;
        }

        // 判断是不是连接的是不是有线网
        if(activeNetInfo.getType()==ConnectivityManager.TYPE_ETHERNET){
            return NETWORK_INTERNET;
        }

        // 判断是不是连接的是不是wifi
        NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (null != wifiInfo) {
            NetworkInfo.State state = wifiInfo.getState();
            if (null != state)
                if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                    return NETWORK_WIFI;
                }
        }

        // 如果不是wifi，则判断当前连接的是运营商的哪种网络2g、3g、4g等
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (null != networkInfo) {
            NetworkInfo.State state = networkInfo.getState();
            String strSubTypeName = networkInfo.getSubtypeName();
            if (null != state)
                if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                    switch (activeNetInfo.getSubtype()) {
                        //如果是2g类型
                        case TelephonyManager.NETWORK_TYPE_GPRS: // 联通2g
                        case TelephonyManager.NETWORK_TYPE_CDMA: // 电信2g
                        case TelephonyManager.NETWORK_TYPE_EDGE: // 移动2g
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            return NETWORK_2G;
                        //如果是3g类型
                        case TelephonyManager.NETWORK_TYPE_EVDO_A: // 电信3g
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                            return NETWORK_3G;
                        //如果是4g类型
                        case TelephonyManager.NETWORK_TYPE_LTE:
                            return NETWORK_4G;
                        case TelephonyManager.NETWORK_TYPE_NR: //对应的20 只有依赖为android 10.0才有此属性
                            return NETWORK_5G;
                        default:
                            //中国移动 联通 电信 三种3G制式
                            if (strSubTypeName.equalsIgnoreCase("TD-SCDMA") || strSubTypeName.equalsIgnoreCase("WCDMA") || strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                                return NETWORK_3G;
                            } else {
                                return NETWORK_MOBILE;
                            }
                    }
                }
        }
        return NETWORK_NONE;
    }

    @RequiresPermission("android.permission.ACCESS_NETWORK_STATE")
    public static NetworkType getNetworkType2(Context context) {
        NetworkType netType = NetworkType.NETWORK_NO;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            if (info.getType() == ConnectivityManager.TYPE_ETHERNET) {
                netType = NetworkType.NETWORK_INTERNET;
            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                netType = NetworkType.NETWORK_WIFI;
            } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                switch (info.getSubtype()) {
                    case TelephonyManager.NETWORK_TYPE_LTE:
                    case TelephonyManager.NETWORK_TYPE_IWLAN:
                        netType = NetworkType.NETWORK_4G;
                        break;

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

    @RequiresPermission("android.permission.ACCESS_NETWORK_STATE")
    public static String getNetworkType3(Context context){
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

    /**
     * 判断设备是否联网
     * @param context
     * @return 是否联网
     */
    @RequiresPermission("android.permission.ACCESS_NETWORK_STATE")
    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = null;
        if (cm != null) {
            info = cm.getActiveNetworkInfo();
        }
        return info != null && info.isAvailable();
    }

    /**
     * 判断数据流量开关是否打开
     * @param context
     * @return
     */
    public static boolean isMobileDataEnabled(Context context) {
        try {
            Method method = ConnectivityManager.class.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true);
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return (Boolean) method.invoke(manager);
        } catch (Throwable t) {
            Log.e(TAG, "Check mobile data encountered exception");
            return false;
        }
    }

    //===========================================流量================================================
    /**
     * 获取系统所用流量数: 上传 + 下载
     * @return
     */
    private long getTotalBytes() {
        return TrafficStats.getTotalTxBytes() + TrafficStats.getTotalRxBytes();
    }

    /**
     * 获取某APP所用下载流量
     * @param uid: PackageUtil.getInstance(content).getAppUid(packageName)
     * @return
     */
    private long getAppRxByte(int uid){
        return TrafficStats.getUidRxBytes(uid)==TrafficStats.UNSUPPORTED ? 0 :(TrafficStats.getUidRxBytes(uid));
    }

    /**
     * 获取某APP所用上传流量
     * @param uid
     * @return
     */
    private long getAppTxByte(int uid){
        return TrafficStats.getUidTxBytes(uid)==TrafficStats.UNSUPPORTED ? 0 :(TrafficStats.getUidTxBytes(uid));
    }

    //=====================================获取APP当月流量消耗情况====================================
    /**
     * APP当月流量消耗情况
     * @param packageName 包名
     * @param context
     * @return
     */
    public static FlowInfo getDataFlowInfo(String packageName, Context context) {
        PackageManager pms = context.getPackageManager();
        List<PackageInfo> packinfos = pms.getInstalledPackages(PackageManager.GET_PERMISSIONS);

        FlowInfo flowInfo = new FlowInfo();
        for (PackageInfo packinfo : packinfos) {
            String packName = packinfo.packageName;
            if (!TextUtils.isEmpty(packName)) {
                if (packName.equals(packageName)) {
                    flowInfo.setPackageName(packName);
                    flowInfo.setAppName(packinfo.applicationInfo.loadLabel(pms).toString());
                    //获取到应用的uid（user id）
                    int uid = packinfo.applicationInfo.uid;
                    //TrafficStats对象通过应用的uid来获取应用的下载、上传流量信息
                    //发送的 上传的流量byte
                    flowInfo.setUpKb(TrafficStats.getUidTxBytes(uid));
                    //下载的流量 byte
                    flowInfo.setDownKb(TrafficStats.getUidRxBytes(uid));
                    break;
                }
            }
        }
        return flowInfo;
    }

    //==============================================================================================
    public enum NetworkType {
        NETWORK_INTERNET("Internet"),
        NETWORK_WIFI("WiFi"),
        NETWORK_2G("2G"),
        NETWORK_4G("4G"),
        NETWORK_3G("3G"),
        NETWORK_UNKNOWN("Unknown"),
        NETWORK_NO("No network");

        private String desc;
        NetworkType(String desc) {
            this.desc = desc;
        }

        @Override
        public String toString() {
            return desc;
        }
    }

    public static class FlowInfo extends BaseBean {
        private String packageName;
        private String appName;
        private long upKb;
        private long downKb;

        public FlowInfo() {
        }

        public FlowInfo(String packageName, String appName, long upKb, long downKb) {
            this.packageName = packageName;
            this.appName = appName;
            this.upKb = upKb;
            this.downKb = downKb;
        }

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public String getAppName() {
            return appName;
        }

        public void setAppName(String appName) {
            this.appName = appName;
        }

        public void setUpKb(long upKb) {
            this.upKb = upKb;
        }

        public void setDownKb(long downKb) {
            this.downKb = downKb;
        }

        public long getUpKb() {
            return upKb;
        }

        public long getDownKb() {
            return downKb;
        }
    }
}
