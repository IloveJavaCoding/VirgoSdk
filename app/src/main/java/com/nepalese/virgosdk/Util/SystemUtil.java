package com.nepalese.virgosdk.Util;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.SystemClock;
import android.os.Vibrator;
import android.widget.Toast;

import androidx.annotation.RequiresPermission;
import androidx.core.content.ContextCompat;

import com.nepalese.virgosdk.Manager.RuntimeExec;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * @author nepalese on 2020/11/18 10:43
 * @usage 系统管理：重启应用、系统，设置系统时间
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
    //==========================================reboot==============================================
    /**
     * 重启应用
     * @param context
     */
    public static void restartApp(Context context) {
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    /**
     * 重启系统
     */
    public static void reBoot(){
        String cmd = "reboot";
        RuntimeExec.getInstance().executeCommand(cmd);
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

    ///////////////////////////////////////
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
