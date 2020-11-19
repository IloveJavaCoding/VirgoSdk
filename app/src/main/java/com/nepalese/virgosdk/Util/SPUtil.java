package com.nepalese.virgosdk.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.ArraySet;
import android.util.Base64;

import androidx.annotation.RequiresApi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.Set;

/**
 * @author nepalese on 2020/11/19 11:35
 * @usage SharedPreference 基础配置
 */
public class SPUtil {

    private static SharedPreferences getShared(Context context, String fileName){
        return context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    public static  boolean getBoolean(Context context, String fileName, String key, boolean defValue) {
        return getShared(context, fileName).getBoolean(key, defValue);
    }

    public static void setBoolean(Context context, String fileName, String key, boolean value) {
        SharedPreferences sp = getShared(context, fileName);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static String getString(Context context, String fileName, String key, String defValue){
        return getShared(context, fileName).getString(key, defValue);
    }

    public static void setString(Context context, String fileName, String key, String value) {
        SharedPreferences sp = getShared(context, fileName);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static Set<String> getStringSet(Context context, String fileName, String key){
        return getShared(context, fileName).getStringSet(key, new ArraySet<String>());
    }

    public static void setStringSet(Context context, String fileName, String key, Set<String> value) {
        SharedPreferences sp = getShared(context, fileName);
        SharedPreferences.Editor editor = sp.edit();
        editor.putStringSet(key, value);
        editor.apply();
    }

    public static int getInt(Context context, String fileName, String key, int defValue){
        return getShared(context, fileName).getInt(key, defValue);
    }

    public static void setInt(Context context, String fileName, String key, int value) {
        SharedPreferences sp = getShared(context, fileName);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static float getFloat(Context context, String fileName, String key, float defValue){
        return getShared(context, fileName).getFloat(key, defValue);
    }

    public static void setFloat(Context context, String fileName, String key, float value) {
        SharedPreferences sp = getShared(context, fileName);
        SharedPreferences.Editor editor = sp.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public static long getLong(Context context, String fileName, String key, long defValue){
        return getShared(context, fileName).getLong(key, defValue);
    }

    public static void setLong(Context context, String fileName, String key, long value) {
        SharedPreferences sp = getShared(context, fileName);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    private static void saveObject(Context context, String fileName, String key, Object objValue) {
        SharedPreferences sp = getShared(context, fileName);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(baos);
            out.writeObject(objValue);
            String objectVal = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(key, objectVal);
            editor.apply();
        } catch (IOException ignored) {
        } finally {
            try {
                baos.close();
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static <T> T getObject(Context context, String fileName, String key, Class<T> clazz) {
        SharedPreferences sp = getShared(context, fileName);
        if (sp.contains(key)) {
            String objectVal = sp.getString(key, null);
            byte[] buffer = Base64.decode(objectVal, Base64.DEFAULT);
            ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(bais);
                T t = (T) ois.readObject();
                return t;
            } catch (IOException | ClassNotFoundException ignored) {
            } finally {
                try {
                    bais.close();
                    if (ois != null) {
                        ois.close();
                    }
                } catch (IOException ignored) {
                }
            }
        }
        return null;
    }

}
