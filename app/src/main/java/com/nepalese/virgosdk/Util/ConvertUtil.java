package com.nepalese.virgosdk.Util;

import android.content.Context;
import android.os.Build;
import android.util.TypedValue;

import androidx.annotation.RequiresApi;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author nepalese on 2020/11/05 11:23
 * @usage 数据转换：类型，单位
 */

public class ConvertUtil {
    //======================================字符串转数值=============================================
    public static float toFloat(String src) {
        float retVal = 0.0F;

        try {
            retVal = Float.parseFloat(src);
        } catch (Exception ignored) {
        }

        return retVal;
    }

    public static double toDouble(String src) {
        double retVal = 0.0D;

        try {
            retVal = Double.parseDouble(src);
        } catch (Exception ignored) {
        }

        return retVal;
    }

    public static int toInt(String src) {
        int retVal = 0;

        try {
            retVal = Integer.parseInt(src);
        } catch (Exception ignored) {
        }

        return retVal;
    }

    public static int toInt(String src, int defaultVal) {
        int retVal;
        try {
            retVal = Integer.parseInt(src);
        } catch (Exception var4) {
            retVal = defaultVal;
        }

        return retVal;
    }

    public static long toLong(String src, long defaultVal) {
        long retVal;

        try {
            retVal = Integer.parseInt(src);
        } catch (Exception var6) {
            retVal = defaultVal;
        }

        return retVal;
    }


    //=======================================字符串<-->十六进制======================================
    //字符串转换为16进制字符串
    public static String string2Hex(String str){
        StringBuilder out = new StringBuilder();
        for(int i=0;i<str.length();i++){
            int ch = str.charAt(i);
            String temp = Integer.toHexString(ch);
            out.append(temp);
        }

        return out.toString();
    }

    //转换十六进制编码为字符串
    public static String toStringHex(String str) {
        if ("0x".equals(str.substring(0, 2)))
        {
            str = str.substring(2);
        }
        byte[] baKeyword = new byte[str.length() / 2];
        for (int i = 0; i < baKeyword.length; i++)
        {
            try
            {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(str.substring(
                        i * 2, i * 2 + 2), 16));
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        try
        {
            str = new String(baKeyword, StandardCharsets.UTF_8);//UTF-16le:Not
        } catch (Exception e1)
        {
            e1.printStackTrace();
        }
        return str;
    }


    //=========================================进制转换==============================================
    //16 <--> 10
    public static int hex2Decimal(String hex){
        int d = 0;
        //1.系统方法
        //d = Integer.valueOf(hex,16);

        //2. ff -> 255
        for(int i=0; i<hex.length(); i++){
            d += char2Int(hex.charAt(i))*(2<<(4*(hex.length()-1)-1));//16^hex.length()-1
        }

        return d;
    }

    private static int char2Int(char c){
        if(c > 'F'){
            return -1;
        }
        switch (c){
            case 'F':
                return 15;
            case 'E':
                return 14;
            case 'D':
                return 13;
            case 'C':
                return 12;
            case 'B':
                return 11;
            case 'A':
                return 10;
            default:
                return c;
        }
    }

    private String decimal2Hex2(int a) {
        String out = Integer.toHexString(a);
        if(out.length()==1){
            out = "0" + out;
        }
        return out;
    }

    public static String decimal2Hex(int d){
        StringBuilder hex = new StringBuilder();
        List<Integer> m = new ArrayList<>();
        //1.系统方法
        //hex = Integer.toHexString(d);

        //2. a -> 商; m -> 余;
        int a = d;
        while(a>0){
            m.add(d%16);
            a = d/16;
        }

        for(int i=m.size()-1; i>=0; i--){
            hex.append(int2Str(m.get(i)));
        }

        return hex.toString();
    }

    private static String int2Str(Integer a) {//<15
        if(a>15){
            return "";
        }
        switch (a){
            case 15:
                return "f";
            case 14:
                return "e";
            case 13:
                return "d";
            case 12:
                return "c";
            case 11:
                return "b";
            case 10:
                return "a";
            default:
                return a.toString();
        }
    }


    //============================================array & list======================================
    // int[] 转 List<Integer>
    @RequiresApi(api = Build.VERSION_CODES.N) //24
    public static List<Object> intArr2List(int[] data) {
        return Arrays.stream(data).boxed().collect(Collectors.toList());
    }

    public static List<Integer> intArr2List2(int[] data){
        List<Integer> list = new ArrayList<>();
        for(int a : data){
            list.add(a);
        }
        return list;
    }

    // String[] 转 List<String>
    public static List<String> strings2List(String[] data){
        return Arrays.asList(data);
    }

    //============================================文件单位转换=======================================
    public static String formatFileSize(long size) {
        long kiloByte = size / 1024;
        if (kiloByte < 1) {
            return "0 B";
        }

        long megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + " KB";
        }

        long gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + " MB";
        }

        long teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + " GB";
        }

        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).
                toPlainString() + " TB";
    }

    //自定义精度
    public static String formatFileSize(long size, int scale) {
        long kiloByte = size / 1024;
        if (kiloByte < 1) {
            return "0 B";
        }

        long megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(scale, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + " KB";
        }

        long gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(scale, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + " MB";
        }

        long teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(scale, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + " GB";
        }

        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(scale, BigDecimal.ROUND_HALF_UP).
                toPlainString() + " TB";
    }

    //============================================时间单位转换=======================================
    //get the hh:mm:ss from millSecs
    public static String formatTime(long millSec){
        int seconds =(int) millSec/1000;
        int hour, sec, min;
        String hours, secs, mins;

        if(seconds>60*60){//1 hour
            hour = seconds/3600;
            min = (seconds - hour*3600)/60;
            sec = seconds - hour*3600 - min*60;

            if(hour<10){
                hours = "0" + hour;
            }else{
                hours = Integer.toString(hour);
            }
            if(min<10){
                mins = "0" + min;
            }else{
                mins = Integer.toString(min);
            }
            if(sec<10){
                secs = "0" + sec;
            }else{
                secs = Integer.toString(sec);
            }

            return hours+":"+mins+":"+secs;
        }
        else{
            min = seconds/60;
            sec = seconds - min*60;
            if(min<10){
                mins = "0" + min;
            }else{
                mins = Integer.toString(min);
            }
            if(sec<10){
                secs = "0" + sec;
            }else{
                secs = Integer.toString(sec);
            }

            return mins+":"+secs;
        }
    }


    //====================================== 显示单位转换============================================
    //dp = px / density; --> px = dp * density = dp * (dpi / 160)
    //density = dpi / 160 ;
    //dpi = px / inch;(dot per inch)

    //px = dp * density
    //dp to px
    public static float dp2px(Context context, float dp) {
        final float density = context.getResources().getDisplayMetrics().density;
        return dp * density;
    }

    //px = dp * (dpi / 160)
    public static float dp2px2(Context context, float dp) {
        int dpi = context.getResources().getDisplayMetrics().densityDpi;
        return dp * (dpi / 160f);
    }

    public static float dp2px3(Context context, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    //dp = px / density;
    //px to dp
    public static float px2dp(Context context, float px) {
        final float density = context.getResources().getDisplayMetrics().density;
        return px / density;
    }

    public static float px2dp2(Context context, float px) {
        int dpi = context.getResources().getDisplayMetrics().densityDpi;
        return px / (dpi / 160f);
    }

    //px = sp * scaledDensity;
    //sp to px
    public static float sp2px(Context context, float sp) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return sp  * scaledDensity;
    }

    public static float sp2px2(Context context, float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    //sp = px / scaledDensity;
    //px to sp
    public static float px2sp(Context context, float px) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return px / scaledDensity;
    }

    public static float px2sp2(Context context, float px) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px, context.getResources().getDisplayMetrics());
    }

    //=======================温度： 华氏 ~ 摄氏度 Fahrenheit, Centigrade=============================
    //C=（5/9）（F-32）
    public static float fahrenheit2Centigrade(float f){
        return (5/9f) * (f-32f);
    }

    public static float centigrade2Fahrenheit(float c){
        return c*1.8f + 32;
    }

}
