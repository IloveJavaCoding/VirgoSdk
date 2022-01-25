package com.nepalese.virgosdk.Util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresPermission;
import androidx.core.content.ContextCompat;

import com.nepalese.virgosdk.Manager.RuntimeExec;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author nepalese on 2020/11/18 10:43
 * @usage 系统管理：
 * 1. 重启应用、系统；
 * 2. 打开安装app；
 * 3. 权限检测；todo
 * 4. 设置系统时间；
 *
 */
public class SystemUtil {
    /**
     * 仅activity跳转
     * @param context
     * @param c xxxActivity.class
     */
    public static void jumActivity(Context context, Class c){
        Intent intent = new Intent(context, c);
        context.startActivity(intent);
    }

    /**
     * short toast
     * @param context
     * @param msg
     */
    public static void showToast(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * long toast
     * @param context
     * @param msg
     */
    public void showLongToast(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }
    //==========================================重启================================================
    /**
     * 重启应用
     * @param context
     */
    public static void restartApp(Context context) {
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);//Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent);
    }

    /**
     * 重启系统
     */
    public static void reBoot(){
        String cmd = "reboot";
        RuntimeExec.getInstance().executeCommand(cmd);
		
		//PowerManager pManager=(PowerManager) getSystemService(Context.POWER_SERVICE);  
        //pManager.reboot("重启"); 
    }

    /**
     * 重启某个activity
     * @param activity
     */
    public static void recreate(Activity activity){
        activity.finish();
        Intent intent = new Intent(activity, activity.getClass());
        activity.startActivity(intent);
    }

    //==============================================================================================
    /**
     * 打开第三方apk
     * @param context
     * @param packagename 包名
     */
    public static void openThirdApk(Context context, String packagename){
        Intent intent2 = context.getPackageManager().getLaunchIntentForPackage(packagename);
        String classNameString = intent2.getComponent().getClassName();//得到app类名
        Intent intent  = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        intent.setComponent(new ComponentName(packagename, classNameString));
        context.startActivity(intent);
    }

    /**
     * 静默安装apk(需root权限)
     * @param filePath
     */
    public static void installApkSilence(final String filePath){
        new Thread(() -> RuntimeExec.getInstance().executeRootCommand(RuntimeExec.INSTALL + filePath)).start();
    }

    /**
     * 跳转安装apk（需手动授权）
     * @param activity
     * @param file
     * @param requestCode
     */
    public static void installApkManual(Activity activity, File file, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_VIEW);//"android.intent.action.VIEW"
        intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        activity.startActivityForResult(intent, requestCode);//处理取消安装
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 校验当前App是否在运行或前台有其他app在运行
     * @param context
     * @return
     */
    public static boolean isRuning(Context context){
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            List<ActivityManager.RunningTaskInfo> infos = manager.getRunningTasks(Integer.MAX_VALUE);
            if(infos!=null){
                int size = infos.size();
//                Log.d(TAG, "运行程序个数: " + size);
//                for(ActivityManager.RunningTaskInfo info: infos){
//                    Log.v(TAG, ": " + info.topActivity.getPackageName());
//                    //桌面：com.android.launcher
//                }
                return size>1;
            }
            return false;
        }
        return true;
    }
    //======================================permission check========================================
    /**
     * 权限检测：
     *  if(!SystemUtil.checkPermission(this, NEEDED_PERMISSIONS)){
     *      ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
     *      return;
     *   }
     *
     *   @Override
     *     public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
     *         super.onRequestPermissionsResult(requestCode, permissions, grantResults);
     *         boolean isAllGranted = true;
     *         for (int grantResult : grantResults) {
     *             isAllGranted &= (grantResult == PackageManager.PERMISSION_GRANTED);
     *         }
     *
     *         if (requestCode == ACTION_REQUEST_PERMISSIONS) {
     *             if (isAllGranted) {
     *                 //get all requested permissions
     *             } else {
     *                 showToast("Permission denied!");
     *                 finish();
     *             }
     *         }
     *     }
     * @param context
     * @param permissions
     * @return
     */
    public static boolean checkPermission(Context context, String[] permissions){
        if (permissions == null || permissions.length == 0) {
            return true;
        }
        boolean allGranted = true;
        for (String neededPermission : permissions) {
            allGranted &= ContextCompat.checkSelfPermission(context, neededPermission) == PackageManager.PERMISSION_GRANTED;
        }
        return allGranted;
    }

    //=======================================vibrator===============================================
    @RequiresPermission("android.permission.VIBRATE")
    public static void vibrator(Context context){
        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        int ringerMode = am.getRingerMode();
        if (ringerMode != 0) {
            Vibrator vv = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
            vv.vibrate(500L);
        }
    }

    //===================================设置系统时间，时区（须系统权限）==============================
    public static void setTime(long mills){
        SystemClock.setCurrentTimeMillis(mills);
    }

    @RequiresPermission("android.permission.SET_TIME_ZONE")
    public static void setTimeZone(Context context, String timeZone){
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.setTimeZone(timeZone);
    }

    /**
     * 同步系统时间
     * @param ServiceTime
     */
    public static void timeSynchronization(Date ServiceTime){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        String datetime=sdf.format(ServiceTime);

        ArrayList<String> envlist = new ArrayList<>();
        Map<String, String> env = System.getenv();
        for (String envName : env.keySet()) {
            envlist.add(envName + "=" + env.get(envName));
        }
        String[] envp = envlist.toArray(new String[0]);
        String command;
        command = "date -s\""+datetime+"\"";
        try {
            Runtime.getRuntime().exec(new String[] { "su", "-c", command }, envp);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
