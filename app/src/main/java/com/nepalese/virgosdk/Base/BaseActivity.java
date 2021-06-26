package com.nepalese.virgosdk.Base;

import android.app.ProgressDialog;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;

/**
 * @author nepalese on 2020/11/21 12:07
 * @usage 带有常用方法的基础activity
 */
public class BaseActivity extends AppCompatActivity {
    private ProgressDialog dialog;

    public BaseActivity() {

    }

    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }

    /**
     * 调用加载弹框
     * @param title
     * @param content 提示内容
     */
    public void showDialog(String title, String content){
        dialog = new ProgressDialog(this);
        dialog.setTitle(title);
        dialog.setMessage(content);
        dialog.setCancelable(false);
        dialog.show();
    }

    /**
     * 隐藏弹框
     */
    public void hideDialog(){
        if(dialog!=null){
            dialog.dismiss();
        }
    }

    /**
     * EventBus 推送消息
     * @param obj
     */
    public void postEvent(Object obj){
        EventBus.getDefault().post(obj);
    }

    public void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void showLongToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    protected void onPause() {
        super.onPause();
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.back();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    protected void back() {
        this.finish();
    }
}