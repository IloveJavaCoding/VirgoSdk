package com.nepalese.virgosdk.Util;

import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;

/**
 * @author nepalese on 2020/11/18 14:20
 * @usage 色彩处理：生成颜色，设置颜色透明度， 获取图片中像素颜色，渐变资源；
 *
 */

public class ColorUtil {
    private static final String TAG = "ColorUtil";

    /**
     * todo 这个有啥用
     * @param color
     * @param count
     * @param offset
     * @return
     */
    public static int[] getColors(int color, int count, int offset) {
        int[] colors = new int[count];

        int a = (color >> 24) & 0xff;
        int r = (color >> 16) & 0xff;
        int g = (color >> 8) & 0xff;
        int b = (color) & 0xff;

        int c;
        if (g > b) {
            c = Math.max(r, g);
        } else {
            c = Math.max(r, b);
        }

        for (int i = 0; i < count; i++) {
            int temp = (c + offset * (i - count / 2)) % 0XFF;
            if (i == 1) {
                colors[i] = createColor(a, r, temp, b);
            } else if (i == 2) {
                colors[i] = createColor(a, r, g, temp);
            } else {
                colors[i] = createColor(a, temp, g, b);
            }
        }

        return colors;
    }

    /**
     * 生成颜色 == Color.argb(a, r,g,b)
     * @param a
     * @param r
     * @param g
     * @param b
     * @return
     */
    public static int createColor(int a, int r, int g, int b) {
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    /**
     * 设置颜色透明度
     * @param color
     * @param alpha：[0,255]
     * @return
     */
    public static int setColorAlpha(@ColorInt int color, int alpha) {
        int r = (color >> 16) & 0xff; //>>右移位16位
        int g = (color >> 8) & 0xff; //& 与
        int b = (color) & 0xff;
        return createColor(alpha, r, g, b);
    }

    /**
     * 获取图片中像素颜色
     * @param bitmap 需处理位图
     * @return List<Integer>
     */
    public static List<Integer> color4Bitmap(Bitmap bitmap){
        if(bitmap==null) return null;

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int[] pixels = new int[w * h];
        HashMap<Integer, Integer> colors = new HashMap<>();
        TreeMap<Integer, Integer> sortedColors = new TreeMap<>();
        List<Integer> result = new ArrayList<>();
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h);
        for (int pixel : pixels) {
            Integer num = colors.get(pixel);
            if (num == null) {
                colors.put(pixel, 1);
            } else {
                num += 1;
                colors.put(pixel, num);
            }
        }
        for (Map.Entry<Integer, Integer> entry : colors.entrySet()) {
            sortedColors.put(entry.getValue(), entry.getKey());
        }
        for (Map.Entry<Integer, Integer> entry : sortedColors.entrySet()) {
            result.add(entry.getValue());
        }

        return result;
    }

    /**
     * 创建一个渐变资源（二色）：放射渐变
     * @param startColor
     * @param endColor
     * @param radius
     * @param centerX
     * @param centerY
     * @return
     */
    public static GradientDrawable createGradientRadial(@ColorInt int startColor, @ColorInt int endColor, int radius,
                                          @FloatRange(from = 0f, to = 1f) float centerX,
                                          @FloatRange(from = 0f, to = 1f) float centerY) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColors(new int[]{
                startColor,
                endColor
        });
        gradientDrawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        gradientDrawable.setGradientRadius(radius);
        gradientDrawable.setGradientCenter(centerX, centerY);
        return gradientDrawable;
    }

    /**
     * 创建一个渐变资源（二色）：线性渐变
     * @param startColor
     * @param endColor
     * @param orientation 渐变方向
     * @return
     */
    public static GradientDrawable createGradientLinear(@ColorInt int startColor, @ColorInt int endColor, GradientDrawable.Orientation orientation) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColors(new int[]{
                startColor,
                endColor
        });
        gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        gradientDrawable.setOrientation(orientation);
        return gradientDrawable;
    }
}
