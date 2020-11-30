package com.nepalese.virgosdk.Util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.nepalese.virgosdk.Beans.AppInfo;

/**
 * @author nepalese on 2020/11/18 10:43
 * @usage 安装包信息: 包名，版本名，版本号
 */
public class ApkInfoUtil {
    private static final String TAG = "ApkInfoUtil";

    private static volatile ApkInfoUtil instance;
    private final PackageManager packageManager;
    private Context context;

    private ApkInfoUtil(Context context) {
        this.context = context;
        this.packageManager = context.getPackageManager();
    }

    public static ApkInfoUtil getInstance(Context context) {
        if(instance==null){
            synchronized (ApkInfoUtil.class){
                if(instance==null){
                    instance = new ApkInfoUtil(context);
                }
            }
        }
        return instance;
    }

    //=============================获取当前安装包包名，版本名，版本号=================================
    public static String getSelfPackageName(Context context){
        return context.getPackageName();
    }

    public String getSelfVersionName(){
        return  getPackageInfo(getSelfPackageName(context)).versionName;
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public long  getSelfVersionCode(){
        return getPackageInfo(getSelfPackageName(context)).getLongVersionCode();
    }

    //==========================================通用信息=============================================
    //获取安装包（已安装在该系统）相关信息
    public PackageInfo getPackageInfo(String packageName){
        PackageInfo packageInfo = null;
        try {
            //flag: Additional option flags to modify the data returned.
            packageInfo = packageManager.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo;
    }

    //packagePath: apk包的绝对路径
    public PackageInfo getPackageInfo2(String packagePath){
        return packageManager.getPackageArchiveInfo(packagePath, PackageManager.GET_ACTIVITIES);
    }

    //获取APP信息类
    public ApplicationInfo getAppInfo(PackageInfo pkgInfo){
        return pkgInfo.applicationInfo;
    }

    //获取APP UID
    public int getAppUid(String packageName){
        return getPackageInfo(packageName).applicationInfo.uid;
    }

    //获取APP名称
    public String getAppName(ApplicationInfo applicationInfo){
        return packageManager.getApplicationLabel(applicationInfo).toString();
    }

    //获取APP图标
    public Drawable getAppIcon(ApplicationInfo applicationInfo){
        return packageManager.getApplicationIcon(applicationInfo);
    }

    //获取apk包的信息：版本号，名称，图标等
    //AppInfo：(自定义类）
    public AppInfo getApkInfo(PackageInfo pkgInfo){
        if (pkgInfo != null) {
            ApplicationInfo appInfo = pkgInfo.applicationInfo;
            AppInfo app = new AppInfo();
            app.setName(getAppName(appInfo));
            app.setPackageName(appInfo.packageName);
            app.setVersionName(pkgInfo.versionName);
            app.setVersionCode(pkgInfo.versionCode);
            app.setIcon(getAppIcon(appInfo));
            return app;
        }
        return null;
    }
}
