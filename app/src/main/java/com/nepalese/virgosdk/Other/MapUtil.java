package com.nepalese.virgosdk.Other;

import java.math.BigDecimal;

/**
 * @author nepalese on 2021/1/13 17:25
 * @usage 地图工具类：计算两个经纬坐标距离，
 */
public class MapUtil {
    private static final int DEFAULT_DIV_SCALE = 6;
    private static final double EARTH_RADIUS = 6371; // 地球平均半径

    public static double getDistance(Double lat1, Double lng1, Double lat2, Double lng2) {
        double dLat = Double.parseDouble(new BigDecimal(String.valueOf((lat1 - lat2)))
                .multiply(new BigDecimal(String.valueOf(Math.PI)))
                .divide(new BigDecimal(String.valueOf(180)), DEFAULT_DIV_SCALE, BigDecimal.ROUND_HALF_EVEN).toString());

        double dLon = Double.parseDouble(new BigDecimal(String.valueOf((lng1 - lng2)))
                .multiply(new BigDecimal(String.valueOf(Math.PI)))
                .divide(new BigDecimal(String.valueOf(180)), DEFAULT_DIV_SCALE, BigDecimal.ROUND_HALF_EVEN).toString());

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1 * Math.PI / 180)
                * Math.cos(lat2 * Math.PI / 180) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        return  (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))) * EARTH_RADIUS * 1000;
    }

    public static double getDistance2(double long1, double lat1, double long2, double lat2) {
        double a, b;
        double sa2, sb2;
        lat1 = lat1 * Math.PI / 180.0;
        lat2 = lat2 * Math.PI / 180.0;
        a = lat1 - lat2;
        b = (long1 - long2) * Math.PI / 180.0;
        sa2 = Math.sin(a / 2.0);
        sb2 = Math.sin(b / 2.0);
        return  2 * EARTH_RADIUS
                * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1)
                * Math.cos(lat2) * sb2 * sb2));
    }
}
