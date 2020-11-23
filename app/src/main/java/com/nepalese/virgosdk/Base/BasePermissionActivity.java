package com.nepalese.virgosdk.Base;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.nepalese.virgosdk.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nepalese on 2020/11/19 12:16
 * @usage 带一些常用配置的activity
 */
public class BasePermissionActivity extends BaseActivity {
    private static final int PERMISSION_REQUEST_CODE = 0x00;
    protected String[] mNeedPermissions;
    private boolean isNeedCheck = true;

    public BasePermissionActivity() {
    }

    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        this.setNeedPermissions();
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

        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(this, perm) != 0 || ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
                needRequestPermissionList.add(perm);
            }
        }

        return needRequestPermissionList;
    }

    private boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != 0) {
                return false;
            }
        }

        return true;
    }

    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("缺少权限");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                BasePermissionActivity.this.finish();
            }
        });
        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                BasePermissionActivity.this.startAppSettings();
                BasePermissionActivity.this.isNeedCheck = true;
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

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] paramArrayOfInt) {
        if (requestCode == 0 && !this.verifyPermissions(paramArrayOfInt)) {
            this.showMissingPermissionDialog();
            this.isNeedCheck = false;
        }
    }
}
