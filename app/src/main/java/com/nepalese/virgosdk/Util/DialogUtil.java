package com.nepalese.virgosdk.Util;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import java.util.Calendar;

/**
 * @author nepalese on 2020/10/15 10:22
 * @usage
 * 1. 对话框：
 *      提示弹框，仅带取消键的提示弹框，列举选择弹框，自定义view弹框；
 * 2. 时间，日间选择框；
 */
public class DialogUtil {
    private static final String TAG = "DialogUtil";
    private static final String cancel = "Cancel";
    private static final String confirm = "Confirm";

    /**
     * 提示弹框
     * @param context
     * @param title 标题
     * @param message 内容
     * @param listener 按键点击监听
     * @return
     */
    public static void showMsgDialog(Context context, String title, String message, DialogInterface.OnClickListener listener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setNegativeButton(cancel,listener)
                .setPositiveButton(confirm,listener)
                .show();
    }

    /**
     * 仅带取消键的提示弹框
     * @param context
     * @param title 标题
     * @param message 内容
     * @param btnText 按键信息
     */
    public static void showMsgDialog(Context context, String title, String message, String btnText){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setNegativeButton(btnText, mCancelListener)
                .show();
    }

    /**
     * 列举选择弹框
     * @param context
     * @param items 列举项
     * @param title 标题
     * @param listener 列举项点击监听
     */
    public static void showListDialog(Context context, String[] items, String title, DialogInterface.OnClickListener listener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setItems(items, listener)
                .setNegativeButton(cancel, mCancelListener)
                .show()
                .setCanceledOnTouchOutside(true);
    }

    /**
     * 自定义view弹框
     * @param context
     * @param title
     * @param view
     * @param listener
     * @return
     */
    public static Dialog showViewDialog(Context context, String title, View view, DialogInterface.OnClickListener listener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        return builder.setTitle(title)
                .setView(view)
                .setNegativeButton(cancel,listener)
                .setPositiveButton(confirm,listener)
                .show();
    }

    /**
     * 自定义view弹框: 仅取消按钮
     * @param context
     * @param title
     * @param view
     * @return
     */
    public static Dialog showViewDialog(Context context, String title, View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        return builder.setTitle(title)
                .setView(view)
                .setNegativeButton(cancel, mCancelListener)
                .show();
    }

    //仅监听取消按钮
    private static final DialogInterface.OnClickListener mCancelListener = (dialog, which) -> {
        if (which == DialogInterface.BUTTON_NEGATIVE) {
            dialog.dismiss();
        }
    };

    /**
     * 日期选择器, 直接创建一个DatePickerDialog对话框实例，并将它显示出来， 系统的
     * @param context    上下文
     * @param themeResId 弹窗风格
     * @param listener   日期选择监听
     * @param calendar   日历对象, 用于赋值初始时间
     */
    public static void showDatePicker(Context context, int themeResId, DatePickerDialog.OnDateSetListener listener, Calendar calendar) {
        new DatePickerDialog(context
                , themeResId //风格
                , listener // 监听
                , calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))// 设置初始日期
                .show();
    }

    /**
     * 时间选择器, 创建一个TimePickerDialog实例，并把它显示出来， 系统的
     * @param context    上下文
     * @param themeResId 弹窗风格
     * @param listener   时间选择监听
     * @param calendar   日历对象, 用于赋值初始时间
     */
    public static void showTimePicker(Context context, int themeResId, TimePickerDialog.OnTimeSetListener listener, Calendar calendar) {
        new TimePickerDialog(context
                , themeResId // 设置风格
                , listener // 绑定监听器
                , calendar.get(Calendar.HOUR_OF_DAY)
                , calendar.get(Calendar.MINUTE)
                , true)// true表示采用24小时制
                .show();
    }
}
