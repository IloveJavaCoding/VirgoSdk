package com.nepalese.virgosdk.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author nepalese on 2020/10/13 10:22
 * @usage 时间日期：获取，格式转换，
 */
public class DateUtil {
    private static final String TAG = "DateUtil";

    public static final String DATE_FORMAT_BASE = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_12 = "yyyy-MM-dd hh:mm:ss EEEE a";
    public static final String DATE_FORMAT_24 = "yyyy-MM-dd HH:mm:ss EEEE";
    public static final String DATE_FORMAT_DATE = "yyyy-MM-dd";
    public static final String DATE_FORMAT_TIME = "HH:mm:ss";
    public static final String DATE_FORMAT_YEAR = "yyyy";

    //=============================================格式转换==========================================
    /**
     * date 转 字符形式
     * @param date
     * @param format
     * @return
     */
    public static String date2String(Date date, String format){
        return new SimpleDateFormat(format, Locale.CHINA).format(date);
    }

    /**
     * 字符 转 date
     * @param time
     * @param format
     * @return
     */
    public static Date string2Date(String time, String format){
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.CHINA);
        Date date = null;
        try {
            date = dateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 时间戳 转 字符
     * @param time
     * @param format
     * @return
     */
    public static String long2String(long time, String format){
        Date date = new Date(time);
        return date2String(date, format);
    }

    //============================================获取时间===========================================
    /**
     * 当前时间
     * @return date
     */
    public static Date getCurTime(){
        return Calendar.getInstance().getTime();
    }

    /**
     * 当前时间
     * @return 时间戳
     */
    public static long getCurTime2(){
        return System.currentTimeMillis();
    }

    /**
     * 当前时间
     * @param format
     * @return string
     */
    public static String getCurTime(String format){
        return date2String(getCurTime(), format);
    }

    //==========================================简单计算============================================
    /**
     * 基于生日日期获取年龄
     * @param birth
     * @return
     */
    public static int getAge(Date birth){
        Calendar calendar = Calendar.getInstance();
        //当前年份
        int year = calendar.get(Calendar.YEAR);
        int year1 = Integer.parseInt(date2String(birth,DATE_FORMAT_YEAR));

        return year - year1;
    }

    /**
     * 提取一个日期里的年月日，时分秒,星期
     * @param date
     * @return int[] = {年,月,日,时,分,秒,星期}
     */
    public static int[] getSeparateDate(Date date) {
        int[] arr = new int[7];
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        arr[0] = calendar.get(Calendar.YEAR);
        arr[1] = calendar.get(Calendar.MONTH)+1;
        arr[2] = calendar.get(Calendar.DAY_OF_MONTH);

        arr[3] = calendar.get(Calendar.HOUR_OF_DAY);//24h
        arr[4] = calendar.get(Calendar.MINUTE);
        arr[5] = calendar.get(Calendar.SECOND);

        arr[6] = calendar.get(Calendar.DAY_OF_WEEK);
        return arr;
    }
}