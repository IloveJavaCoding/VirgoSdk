package com.nepalese.virgosdk.Util;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.AudioManager;
import android.os.PowerManager;
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
 * @usage 安装包信息: 包名，版本名，版本号
 */
public class SystemUtil {
    //=================================system notices=====================================
    public static void showToast(Context context, String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(Context context, String msg){
        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
    }

    //===============================reboot========================================
    //重启应用
    public static void restartApp(Context context) {
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);//Intent.FLAG_ACTIVITY_CLEAR_TASK;
        context.startActivity(intent);
    }

    //重启系统
    public static void reBoot(){
        String cmd = "reboot";
        RuntimeExec.getInstance().executeCommand(cmd);
    }

    //===================================permission check==================================
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

    //=======================================vibrator=============================
    @RequiresPermission("android.permission.VIBRATE")
    public static void vibrator(Context context){
        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        int ringerMode = am.getRingerMode();
        if (ringerMode != 0) {
            Vibrator vv = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
            vv.vibrate(500L);
        }
    }

    //=====================set app language==========================
    public static void setLanguage(Context context, int languageId){
        Resources mResources = context.getResources();
        Configuration mConfiguration = mResources.getConfiguration();
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




    //====================================设置系统时间，时区（须系统权限）============================
    public static void setTime(long mills){
        SystemClock.setCurrentTimeMillis(mills);
    }

    @RequiresPermission("android.permission.SET_TIME_ZONE")
    public static void setTimeZone(Context context, String timeZone){
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.setTimeZone(timeZone);
    }

    public static void timeSynchronization(Date ServiceTime){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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


    //=====================================================================================
    /**
     * 1.PARTIAL_WAKE_LOCK：保证CPU保持高性能运行，而屏幕和键盘背光（也可能是触摸按键的背光）关闭。一般情况下都会使用这个WakeLock。
     * 2.ACQUIRE_CAUSES_WAKEUP：这个WakeLock除了会使CPU高性能运行外还会导致屏幕亮起，即使屏幕原先处于关闭的状态下。
     * 3.ON_AFTER_RELEASE：如果释放WakeLock的时候屏幕处于亮着的状态，则在释放WakeLock之后让屏幕再保持亮一小会。如果释放WakeLock的时候屏幕本身就没亮，则不会有动作
     * @param context
     * @return
     */
    @SuppressLint("InvalidWakeLockTag")
    public static PowerManager.WakeLock getWakeLock(Context context){
        PowerManager pManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "IncallUI");
        wakeLock.acquire();

        return wakeLock;
    }

    public static void releaseWakeLock(PowerManager.WakeLock wakeLock){
        wakeLock.release();
    }
}
