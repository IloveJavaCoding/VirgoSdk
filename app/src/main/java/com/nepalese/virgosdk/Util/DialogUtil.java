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
 * @usage 对话框， 时间，日间选择框
 */
public class DialogUtil {
    private static final String TAG = "DialogUtil";

    /**
     * 提示弹框
     * @param context
     * @param title 标题
     * @param message 内容
     * @param listener
     * @return
     */
    public static void showMsgDialog(Context context, String title, String message, DialogInterface.OnClickListener listener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setNegativeButton("cancel",listener)
                .setPositiveButton("confirm",listener)
                .show();
    }

    /**
     * 仅带取消键的弹框
     * @param context
     * @param title 标题
     * @param message 内容
     * @param btnText 按键信息
     */
    public static void showIntroDialog(Context context,String title,String message,String btnText){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setNegativeButton(btnText,mListener)
                .show();
    }

    /**
     * 列举选择弹框
     * @param context
     * @param items 列举项
     * @param title
     * @param listener
     */
    public static void showListDialog(Context context, String[] items, String title, DialogInterface.OnClickListener listener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setItems(items, listener)
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show().setCanceledOnTouchOutside(true);
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
                .setNegativeButton("cancel",listener)
                .setPositiveButton("confirm",listener)
                .show();
    }

    public static Dialog showViewDialog2(Context context, String title, View view, DialogInterface.OnClickListener listener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        return builder.setTitle(title)
                .setView(view)
                .setNegativeButton("cancel",listener)
                .show();
    }

    private static DialogInterface.OnClickListener mListener = (dialog, which) -> {
        switch (which){
            case DialogInterface.BUTTON_NEGATIVE:
                dialog.dismiss();
        }
    };

    /**
     * 日期选择器, 直接创建一个DatePickerDialog对话框实例，并将它显示出来， 系统的
     * @param context    上下文
     * @param themeResId 弹窗风格
     * @param l          日期选择监听
     * @param calendar   日历对象, 用于赋值初始时间
     */
    public static void showDatePicker(Context context, int themeResId, DatePickerDialog.OnDateSetListener l, Calendar calendar) {
        new DatePickerDialog(context
                , themeResId //风格
                , l // 监听
                , calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))// 设置初始日期
                .show();
    }

    /**
     * 时间选择器, 创建一个TimePickerDialog实例，并把它显示出来， 系统的
     * @param context    上下文
     * @param themeResId 弹窗风格
     * @param l          时间选择监听
     * @param calendar   日历对象, 用于赋值初始时间
     */
    public static void showTimePicker(Context context, int themeResId, TimePickerDialog.OnTimeSetListener l, Calendar calendar) {
        new TimePickerDialog(context
                , themeResId // 设置风格
                , l // 绑定监听器
                , calendar.get(Calendar.HOUR_OF_DAY)
                , calendar.get(Calendar.MINUTE)
                // true表示采用24小时制
                , true)
                .show();
    }
}
