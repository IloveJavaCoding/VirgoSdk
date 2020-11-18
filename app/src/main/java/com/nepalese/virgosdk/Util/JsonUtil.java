package com.nepalese.virgosdk.Util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author nepalese on 2020/11/18 12:10
 * @usage
 * Lib： gson-2.7.jar
 */
public class JsonUtil {
    public static Object getObject(String json, Type class1) {
        try {
            Gson gson = (new GsonBuilder()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            Object object = gson.fromJson(json.trim(), class1);
            return object;
        } catch (Exception var4) {
            var4.printStackTrace();
            return null;
        }
    }

    public static String toJson(Object object) {
        try {
            Gson gson = (new GsonBuilder()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            return gson.toJson(object);
        } catch (Exception var2) {
            return "";
        }
    }

    public static Object getObject(String json, TypeToken token) {
        try {
            Gson gson = new Gson();
            Object object = gson.fromJson(json.trim(), token.getType());
            return object;
        } catch (Exception var4) {
            return "";
        }
    }

    public static String stringToJson(Map<?, ?> string_map) {
        String json = "";
        JSONObject object = new JSONObject(string_map);
        json = object.toString();
        return json;
    }

    public static String string2Json(Map<?, ?> string_map) {
        return (new Gson()).toJson(string_map);
    }
}
