package com.nepalese.virgosdk.Util;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.lang.reflect.Method;

/**
 * @author nepalese on 2020/11/18 09:05
 * @usage 判断有无sim卡， 数据是否打开及开关控制， 获取SIM运营商
 */
public class SIMOperatorUtil {
    private static final String TAG = "SIMOperatorUtil";

    public static final int TYPE_NO_SIM = -1; //无sim卡
    public static final int TYPE_UNKNOWN = 0;
    public static final int TYPE_CMCC = 1; //中国移动
    public static final int TYPE_CUCC = 2; //中国联通
    public static final int TYPE_CTCC = 3; //中国电信

    private static volatile SIMOperatorUtil instance;
    private final TelephonyManager telephonyManager;

    public static SIMOperatorUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (SIMOperatorUtil.class) {
                if (instance == null) {
                    instance = new SIMOperatorUtil(context);
                }
            }
        }
        return instance;
    }

    private SIMOperatorUtil(Context context) {
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    //检查手机是否有sim卡
    public boolean hasSim() {
        String operator = telephonyManager.getSimOperator();
        return TextUtils.isEmpty(operator);
    }

    //判断数据流量开关是否打开
    public boolean isMobileDataEnabled() {
        try {
            Method getDataEnabled = telephonyManager.getClass().getDeclaredMethod("getDataEnabled");
            return (Boolean) getDataEnabled.invoke(telephonyManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //手机流量开关
    public void setMobileDataEnabled(boolean enabled) {
        try {
            Method setDataEnabled = telephonyManager.getClass().getDeclaredMethod("setDataEnabled", boolean.class);
            setDataEnabled.invoke(telephonyManager, enabled);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //获取设备拨号运营商
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
}
