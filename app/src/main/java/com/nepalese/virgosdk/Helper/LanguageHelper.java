package com.nepalese.virgosdk.Helper;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

/**
 * @author nepalese on 2020/11/30 10:19
 * @usage 设置程序语言（资源文件支持）
 */
public class LanguageHelper {
    private static Resources mResources;
    private static Configuration mConfiguration;

    public static final int LANGUAGE_CHINA = 0;
    public static final int LANGUAGE_ENGLISH = 1;

    public static void init(Context context){
        mResources = context.getResources();
        mConfiguration = mResources.getConfiguration();
    }

    /**
     * 设置语言类型
     * @param languageId LANGUAGE_CHINA = 0;
     *                   LANGUAGE_ENGLISH = 1
     */
    public static void changeLanguage(int languageId){
        switch (languageId){
            case LANGUAGE_CHINA:
                mConfiguration.locale = Locale.SIMPLIFIED_CHINESE;
                mResources.updateConfiguration(mConfiguration,mResources.getDisplayMetrics());
                break;
            case LANGUAGE_ENGLISH:
                mConfiguration.locale = Locale.US;
                mResources.updateConfiguration(mConfiguration,mResources.getDisplayMetrics());
                break;
        }
    }
}