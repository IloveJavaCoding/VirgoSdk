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
 * 1. 字符串转数值：float, double, int, long,
 * 2. 字符串<-->十六进制；
 * 3. 进制转换：十六进制，十进制，八进制，二进制；
 * 4. array <--> list;
 * 5. 单位转换：文件，时间，显示单位，温度；
 */

//todo 进制转换：十六进制，十进制，八进制，二进制；
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
    /**
     * 字符串转换为16进制字符串
     * @param str 字符串
     * @return
     */
    public static String string2Hex(String str) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            int ch = str.charAt(i);
            String temp = Integer.toHexString(ch);
            out.append(temp);
        }

        return out.toString();
    }

    /**
     * 转换十六进制编码为字符串
     * @param hex 十六进制编码
     * @return
     */
    public static String hex2String(String hex) {
        if ("0x".equals(hex.substring(0, 2))) {
            hex = hex.substring(2);
        }
        byte[] baKeyword = new byte[hex.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(hex.substring(
                        i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            hex = new String(baKeyword, StandardCharsets.UTF_8);//UTF-16le:Not
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return hex;
    }


    //=========================================进制转换==============================================
    /**
     * 十六进制转十进制 == Integer.valueOf(hex,16);
     *
     * @param hex
     * @return
     */
    public static int hex2Decimal(String hex) {
        int decimal = 0;

        //ff -> 255
        for (int i = 0; i < hex.length(); i++) {
            decimal += char2Int(hex.charAt(i)) * (2 << (4 * (hex.length() - 1) - 1));//16^hex.length()-1
        }

        return decimal;
    }

    private static int char2Int(char c) {
        if (c > 'F') {
            return -1;
        }
        switch (c) {
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

    /**
     * 十进制转十六进制 == Integer.toHexString(decimal);
     *
     * @param decimal
     * @return
     */
    public static String decimal2Hex(int decimal) {
        StringBuilder hex = new StringBuilder();
        List<Integer> m = new ArrayList<>();

        //a -> 商; m -> 余;
        int a = decimal;
        while (a > 0) {
            m.add(decimal % 16);
            a = decimal / 16;
        }

        for (int i = m.size() - 1; i >= 0; i--) {
            hex.append(int2Str(m.get(i)));
        }

        return hex.toString();
    }

    private static String int2Str(Integer a) {//<15
        if (a > 15) {
            return "";
        }
        switch (a) {
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

    /**
     * int[] 转 List<Integer>
     *
     * @param data
     * @return
     */
    public static List<Integer> intArr2List2(int[] data) {
        List<Integer> list = new ArrayList<>();
        for (int a : data) {
            list.add(a);
        }
        return list;
    }

    /**
     * String[] 转 List<String>
     *
     * @param data
     * @return
     */
    public static List<String> strings2List(String[] data) {
        return Arrays.asList(data);
    }

    //============================================文件单位转换=======================================

    /**
     * 文件大小单位标准化：默认精度2
     *
     * @param size
     * @return
     */
    public static String formatFileSize(long size) {
        return formatFileSize(size, 2);
    }

    /**
     * 文件大小单位标准化：自定义精度
     *
     * @param size
     * @param scale 精度
     * @return
     */
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

    /**
     * 将毫秒转换为 (hh:)mm:ss 格式
     *
     * @param millSec
     * @return
     */
    public static String formatTime(long millSec) {
        int seconds = (int) millSec / 1000;
        int h, m, s;
        String sH, sM, sS;

        if (seconds > 60 * 60) {//1 hour
            h = seconds / 3600;
            m = (seconds - h * 3600) / 60;
            s = seconds - h * 3600 - m * 60;

            sH = formatDigit(h);
            sM = formatDigit(m);
            sS = formatDigit(s);

            return sH + ":" + sM + ":" + sS;
        } else {
            m = seconds / 60;
            s = seconds - m * 60;

            sM = formatDigit(m);
            sS = formatDigit(s);

            return sM + ":" + sS;
        }
    }

    /**
     * 两位补全：0-9 -> 00-09
     *
     * @param digit
     * @return
     */
    public static String formatDigit(int digit) {
        if (digit < 10) {
            return "0" + digit;
        } else {
            return String.valueOf(digit);
        }
    }

    //====================================== 显示单位转换============================================
    //dp = px / density; --> px = dp * density = dp * (dpi / 160)
    //density = dpi / 160 ;
    //dpi = px / inch;(dot per inch)

    /**
     * dp to px: px = dp * density
     * @param context
     * @param dp
     * @return
     */
    public static float dp2px(Context context, float dp) {
        final float density = context.getResources().getDisplayMetrics().density;
        return dp * density;
    }

    /**
     * dp to px: px = dp * (dpi / 160)
     * @param context
     * @param dp
     * @return
     */
    public static float dp2px2(Context context, float dp) {
        int dpi = context.getResources().getDisplayMetrics().densityDpi;
        return dp * (dpi / 160f);
    }

    public static float dp2px3(Context context, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    /**
     * px to dp: dp = px / density;
     * @param context
     * @param px
     * @return
     */
    public static float px2dp(Context context, float px) {
        final float density = context.getResources().getDisplayMetrics().density;
        return px / density;
    }

    public static float px2dp2(Context context, float px) {
        int dpi = context.getResources().getDisplayMetrics().densityDpi;
        return px / (dpi / 160f);
    }

    /**
     * sp to px : px = sp * scaledDensity;
     * @param context
     * @param sp
     * @return
     */
    public static float sp2px(Context context, float sp) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return sp * scaledDensity;
    }

    public static float sp2px2(Context context, float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    /**
     * px to sp: sp = px / scaledDensity;
     * @param context
     * @param px
     * @return
     */
    public static float px2sp(Context context, float px) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return px / scaledDensity;
    }

    public static float px2sp2(Context context, float px) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px, context.getResources().getDisplayMetrics());
    }

    //=======================温度： 华氏 ~ 摄氏度 Fahrenheit, Centigrade=============================
    /**
     * 华氏 转 摄氏度: C =（5/9）（F-32）
     * @param f
     * @return
     */
    public static float fahrenheit2Centigrade(float f) {
        return (5 / 9f) * (f - 32f);
    }

    /**
     * 摄氏 转 度华氏:F = C * 1.8f + 32;
     * @param c
     * @return
     */
    public static float centigrade2Fahrenheit(float c) {
        return c * 1.8f + 32;
    }

}
