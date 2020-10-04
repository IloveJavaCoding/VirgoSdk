package com.nepalese.virgosdk.Helper;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import com.nepalese.virgosdk.Manager.Constants;

import java.util.Locale;

public class LanguageHelper {
    private static Resources mResources;
    private static Configuration mConfiguration;

    public static void init(Context context){
        mResources = context.getResources();
        mConfiguration = mResources.getConfiguration();
    }

    public static void changeLanguage(int languageId){
        switch (languageId){
            case Constants.LANGUAGE_CHINA:
                mConfiguration.locale = Locale.SIMPLIFIED_CHINESE;
                mResources.updateConfiguration(mConfiguration,mResources.getDisplayMetrics());
                break;
            case Constants.LANGUAGE_ENGLISH:
                mConfiguration.locale = Locale.US;
                mResources.updateConfiguration(mConfiguration,mResources.getDisplayMetrics());
                break;
        }
    }
}
