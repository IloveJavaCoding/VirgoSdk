package com.nepalese.virgosdk.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.ArraySet;

import androidx.annotation.RequiresApi;

import java.util.Set;

public class SPUtil {
    private static final int MODE_PRIVATE = 0X0000;

    private static SharedPreferences getShared(Context context, String fileName){
        return context.getSharedPreferences(fileName, MODE_PRIVATE);
    }

    public static  boolean getBoolean(Context context, String fileName, String key, boolean defValue) {
        return getShared(context, fileName).getBoolean(key, defValue);
    }

    public static void setBoolean(Context context, String fileName, String key, boolean value) {
        SharedPreferences sp = getShared(context, fileName);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static String getString(Context context, String fileName, String key, String defValue){
        return getShared(context, fileName).getString(key, defValue);
    }

    public static void setString(Context context, String fileName, String key, String value) {
        SharedPreferences sp = getShared(context, fileName);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static Set<String> getStringSet(Context context, String fileName, String key){
        return getShared(context, fileName).getStringSet(key, new ArraySet<String>());
    }

    public static void setStringSet(Context context, String fileName, String key, Set<String> value) {
        SharedPreferences sp = getShared(context, fileName);
        SharedPreferences.Editor editor = sp.edit();
        editor.putStringSet(key, value);
        editor.commit();
    }

    public static int getInt(Context context, String fileName, String key, int defValue){
        return getShared(context, fileName).getInt(key, defValue);
    }

    public static void setInt(Context context, String fileName, String key, int value) {
        SharedPreferences sp = getShared(context, fileName);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static float getFloat(Context context, String fileName, String key, float defValue){
        return getShared(context, fileName).getFloat(key, defValue);
    }

    public static void setFloat(Context context, String fileName, String key, float value) {
        SharedPreferences sp = getShared(context, fileName);
        SharedPreferences.Editor editor = sp.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    public static long getLong(Context context, String fileName, String key, long defValue){
        return getShared(context, fileName).getLong(key, defValue);
    }

    public static void setLong(Context context, String fileName, String key, long value) {
        SharedPreferences sp = getShared(context, fileName);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(key, value);
        editor.commit();
    }
}
