package com.nepalese.virgosdk.Util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author nepalese on 2020/11/18 12:10
 * @usage Json 转换
 * implementation 'com.google.code.gson:gson:2.8.6'
 */
public class JsonUtil {
    /**
     * 自定义类内属性名与json字段名一致
     * @param json
     * @param class1
     * @return
     */
    public static Object getObject(String json, Type class1) {
        try {
            Gson gson = (new GsonBuilder()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            return gson.fromJson(json.trim(), class1);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param json
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> Object getObject(String json, Class<T> tClass){
        Gson gson = new Gson();
        return gson.fromJson(json, tClass);
    }

    /**
     *   将整个类转换成json
     */
    public static String toJson(Object object) {
        try {
            Gson gson = (new GsonBuilder()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            return gson.toJson(object);
        } catch (Exception e) {
            return "";
        }
    }

    public static Object getObject(String json, TypeToken token) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(json.trim(), token.getType());
        } catch (Exception var4) {
            return "";
        }
    }

    /**
     * 生成json类型数据
     * @param string_map
     * @return
     */
    public static String stringToJson(Map<?, ?> string_map) {
        String json;
        JSONObject object = new JSONObject(string_map);
        json = object.toString();
        return json;
    }

    public static String string2Json(Map<?, ?> string_map) {
        return (new Gson()).toJson(string_map);
    }

    /**
     * 获取json数据某一字段对应的值
     * @param json
     * @param key
     * @return
     */
    public static Object getResponeValue(String json, String key) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject.get(key);
        } catch (Exception e) {
            return "";
        }
    }

    public static String getResponeValue(String json, String contentKey, String key) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            String returnObject = jsonObject.getString(contentKey);
            return (new JSONObject(returnObject)).getString(key);
        } catch (Exception e) {
            return "";
        }
    }
}