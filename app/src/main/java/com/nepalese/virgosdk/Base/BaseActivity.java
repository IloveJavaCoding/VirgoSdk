package com.nepalese.virgosdk.Base;

import android.view.KeyEvent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;

/**
 * @author nepalese on 2020/11/21 12:07
 * @usage
 */
public class BaseActivity extends AppCompatActivity {
    public BaseActivity() {
    }

    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }

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
        if (keyCode == 4) {
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