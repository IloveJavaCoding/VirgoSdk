package com.nepalese.virgosdk.Util;

import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author nepalese on 2020/11/18 14:20
 * @usage 色彩
 */

public class ColorUtil {

    public static int[] getColors(int color, int count, int offset) {
        int[] colors = new int[count];

        int a = (color >> 24) & 0xff;
        int r = (color >> 16) & 0xff;
        int g = (color >> 8) & 0xff;
        int b = (color) & 0xff;

        int c;
        if (g > b) {
            if (r > g) {
                c = r;
            } else {
                c = g;
            }
        } else {
            if (r > b) {
                c = r;
            } else {
                c = b;
            }
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

    private static int createColor(int a, int r, int g, int b) {
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static int setColorAlpha(int color, int alpha) {
        int r = (color >> 16) & 0xff; //>>右移位16位
        int g = (color >> 8) & 0xff; //& 与
        int b = (color) & 0xff;
        return createColor(alpha, r, g, b);
    }

    //获取图片中像素颜色
    public static List<Integer> colorCapture4Bitmap(Bitmap bitmap){
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
            Log.d("bitmapUtil", "run: color:"+entry.getValue()+",count:"+entry.getKey());
        }

        return result;
    }

    public static GradientDrawable create(@ColorInt int startColor, @ColorInt int endColor, int radius,
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
}
