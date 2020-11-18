package com.nepalese.virgosdk.Helper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.nepalese.virgosdk.Manager.RuntimeExec;

import java.io.File;

/**
 * @author nepalese on 2020/11/18 11:26
 * @usage 安装包升级
 */
public class ApkUpgradeHelper {
    //==========================静默安装apk============================================
    //需root权限
    public static void installApkSilence(final String filePath){
        new Thread(() -> RuntimeExec.getInstance().executeRootCommand(RuntimeExec.INSTALL + filePath)).start();
    }

    //需手动授权
    public static void installApkManual(Context context, File file) {
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }
}
