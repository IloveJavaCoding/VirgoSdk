package com.nepalese.virgosdk.Base;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.KeyEvent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.nepalese.virgosdk.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nepalese on 2020/11/19 12:16
 * @usage 带一些常用配置的activity
 */
public class BaseActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 0x00;
    protected String[] mNeedPermissions;
    private boolean isNeedCheck = true;

    public BaseActivity() {
    }

    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        this.setNeedPermissions();
    }

    protected void postEvent(Object event) {
        EventBus.getDefault().post(event);
    }

    protected void setNeedPermissions() {
        this.mNeedPermissions = new String[]{
                "android.permission.WRITE_EXTERNAL_STORAGE",
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.INTERNET",
                "android.permission.ACCESS_COARSE_LOCATION",
                "android.permission.ACCESS_FINE_LOCATION",
                "android.permission.READ_PHONE_STATE"};
    }

    private void checkPermissions(String... permissions) {
        List<String> needRequestPermissionList = this.findDeniedPermissions(permissions);
        if (needRequestPermissionList.size() > 0) {
            ActivityCompat.requestPermissions(this, needRequestPermissionList.toArray(new String[0]), PERMISSION_REQUEST_CODE);
        }
    }

    private List<String> findDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissionList = new ArrayList();
        String[] var3 = permissions;
        int var4 = permissions.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            String perm = var3[var5];
            if (ContextCompat.checkSelfPermission(this, perm) != 0 || ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
                needRequestPermissionList.add(perm);
            }
        }

        return needRequestPermissionList;
    }

    private boolean verifyPermissions(int[] grantResults) {
        int var3 = grantResults.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            int result = grantResults[var4];
            if (result != 0) {
                return false;
            }
        }

        return true;
    }

    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(this.getString(R.string.alert));
        builder.setMessage(this.getString(R.string.permission_warn));
        builder.setNegativeButton(this.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                BaseActivity.this.finish();
            }
        });
        builder.setPositiveButton(this.getString(R.string.setting), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                BaseActivity.this.startAppSettings();
                BaseActivity.this.isNeedCheck = true;
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    private void startAppSettings() {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.parse("package:" + this.getPackageName()));
        this.startActivity(intent);
    }

    protected void onResume() {
        super.onResume();
        if (this.isNeedCheck) {
            this.checkPermissions(this.mNeedPermissions);
        }
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

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] paramArrayOfInt) {
        if (requestCode == 0 && !this.verifyPermissions(paramArrayOfInt)) {
            this.showMissingPermissionDialog();
            this.isNeedCheck = false;
        }
    }
}
