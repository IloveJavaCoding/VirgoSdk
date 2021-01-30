package com.nepalese.virgosdk.Util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.RequiresApi;

import com.nepalese.virgosdk.Manager.RuntimeExec;

import java.io.File;
import java.lang.reflect.Method;

/**
 * @author nepalese on 2020/9/4 17:22
 * @usage 有关显示操作： 横竖屏旋转，截屏，屏幕宽高，键盘显隐，亮度调节
 */
public class ScreenUtil {
    private static final String TAG = "ScreenUtil";

    public static final int ORIENTATION_LANDSCAPE = 0;
    public static final int ORIENTATION_PORTRAIT = 1;

    //==============================================设置系统横竖屏===================================
    public static void setOrientation(int orientation){
        switch (orientation){
            case ORIENTATION_LANDSCAPE:
                RuntimeExec.getInstance().executeCommand("setprop persist.sys.screenorientation landscape");
                RuntimeExec.getInstance().executeCommand("reboot");
                break;
            case ORIENTATION_PORTRAIT:
                RuntimeExec.getInstance().executeCommand("setprop persist.sys.screenorientation portrait");
                RuntimeExec.getInstance().executeCommand("reboot");
                break;
        }
    }

    //////////////////////////////////////////////////沉浸式//////////////////////////////////////////
    public static void setImmersionLayout(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        activity.getWindow().setNavigationBarColor(Color.TRANSPARENT);
    }

    //=========================================截屏==================================================
    public static void screenCap(Activity activity, String fileName){
        View dView = activity.getWindow().getDecorView();
        dView.setDrawingCacheEnabled(true);
        dView.buildDrawingCache();
        Bitmap bmp = dView.getDrawingCache();

        if (bmp != null)
        {
            String path = FileUtil.getAppRootPth(activity);
            BitmapUtil.saveBitmap2File(bmp, path, fileName);
        }
    }

    //默认存放在APP安装文件根目录下
    public static void screenShot(Context context, String fileName){
        String path = FileUtil.getAppRootPth(context) + File.separator + fileName;
        RuntimeExec.takeScreenShot(path);
    }

    //====================================获取屏幕显示指标===========================================
    /**
     * 获取屏幕显示指标对象
     * @return DisplayMetrics //dm.widthPixels;  //dm.heightPixels;
     * WindowManager)context.getSystemService(Context.WINDOW_SERVICE).getDefaultDisplay().getMetrics(dm); Deprecated
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    public static DisplayMetrics getScreenDM(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        context.getDisplay().getRealMetrics(dm);

        return dm;
    }

    public static DisplayMetrics getScreenDMOld(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);

        return dm;
    }

    public static int getScreenWidth(Context context){
        return getScreenDMOld(context).widthPixels;
    }

    public static int getScreenHeight(Context context){
        return getScreenDMOld(context).heightPixels;
    }

    //===========================================键盘===============================================
    //启调键盘   (两种启调方式, 在不同机型存在启调差异)
    public static void showKeyBoard(EditText et){
        et.setFocusable(true);
        et.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) et.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(et,InputMethodManager.HIDE_IMPLICIT_ONLY);//InputMethodManager.RESULT_UNCHANGED_SHOWN
    }

    //启调键盘
    public static void showKeyBoard(Context mContext) {
        if (mContext == null) return;
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.RESULT_UNCHANGED_SHOWN, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    // 隐藏键盘
    public static void hideKeyBoard(EditText et){
        InputMethodManager inputMethodManager = (InputMethodManager) et.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if(inputMethodManager!=null){
            inputMethodManager.hideSoftInputFromWindow(et.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
        }
    }

    // 隐藏键盘
    public static void hintSoftInput(Activity activity) {
        if (activity == null || activity.isFinishing()) return;
        View parent = ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
        hideKeyboard(activity, parent);
    }

    // 隐藏键盘
    public static void hideKeyboard(Activity act) {
        if (act == null || act.isFinishing()) return;
        final View v = act.getWindow().peekDecorView();
        hideKeyboard(act, v);
    }

    //隐藏键盘
    public static void hideKeyboard(Context context, View view) {
        if (context == null || view == null) return;
        IBinder mWindowToken = view.getWindowToken();
        if (mWindowToken != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(mWindowToken, InputMethodManager.RESULT_UNCHANGED_SHOWN);
//                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);//解决三星不能隐藏键盘的问题, 等遇到了再处理,
//                  (注: 现在如果使用, 则存在未被开启键盘的时候, 调用该方法, 会启动键盘)
            }
        }
    }

    /**
     * 禁止EditText弹出软件盘，光标依然正常显示。
     */
    public static void prohibitShowSoftInput(EditText editText) {
        Class<EditText> cls = EditText.class;
        Method method;
        try {
            method = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
            method.setAccessible(true);
            method.invoke(editText, false);
        } catch (Exception e) {
            Log.e(TAG, "阻止软件盘弹窗异常"+e);
        }
    }

    //==========================================屏幕亮度============================================
    //设置当前屏幕亮度值 0--255，并使之生效
    public static void setScreenBrightness(Activity act, float value) {
        value = value>255.0f? 255.0f : Math.max(value, 0.0f);
        WindowManager.LayoutParams lp = act.getWindow().getAttributes();
        lp.screenBrightness = lp.screenBrightness + (value) / 255.0f;
        if (lp.screenBrightness > 1) {
            lp.screenBrightness = 1;
        } else if (lp.screenBrightness < 0.2) {
            lp.screenBrightness = (float) 0.2;
        }
        act.getWindow().setAttributes(lp);

        // 保存设置的屏幕亮度值
        Settings.System.putInt(act.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, (int) value);
    }

    public static int getSystemScreenBrightness(Activity activity) {
        try {
            return Settings.System.getInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //自动调节亮度？
    public static boolean isAutoBrightness(Activity activity) {
        try {
            int autoBrightness = Settings.System.getInt(
                    activity.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE);
            if (autoBrightness == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                return true;
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void closeAutoBrightness(Activity activity) {
        Settings.System.putInt(activity.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
    }

    public static void openAutoBrightness(Activity activity) {
        setScreenBrightness(activity, -1);
        Settings.System.putInt(activity.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
    }
}
