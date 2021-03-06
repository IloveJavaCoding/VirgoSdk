package com.nepalese.virgosdk.Util;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.File;
import java.util.Calendar;

/**
 * @author nepalese on 2020/10/11 10:33
 * @usage 跳转选择文件，打开其他APP
 * setAction require add <intent-filter> </intent-filter> into <activity> </activity>
 */
public class IntentUtil {
    //============================================分享文本===========================================
    public static void shareText( Activity activity, String title, String content) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        intent.putExtra(Intent.EXTRA_TEXT, content);
        activity.startActivity(Intent.createChooser(intent, title));
    }

    //=========================================选择文件，并返回其位置================================
    /**
     * Uri uri = data.getData();
     * String Path = uri.getPath();
     */
    private static Intent getIntent(String type){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(type);

        return intent;
    }

    /**
     * 选取图片文件
     * @param activity
     * @param requestCode
     */
    public static void pickImageFile(Activity activity, int requestCode){
        activity.startActivityForResult(getIntent("image/*"), requestCode);
    }

    /**
     * 选取音频文件
     * @param activity
     * @param requestCode
     */
    public static void pickAudioFile(Activity activity, int requestCode){
        activity.startActivityForResult(getIntent("audio/*"),requestCode);
    }

    /**
     * 选取视频文件
     * @param activity
     * @param requestCode
     */
    public static void pickVideoFile(Activity activity, int requestCode){
        activity.startActivityForResult(getIntent("video/*"),requestCode);
    }

    /**
     * 选取文本文件
     * @param activity
     * @param requestCode
     */
    public static void pickTextFile(Activity activity, int requestCode){
        activity.startActivityForResult(getIntent("text/plain"),requestCode);
    }

    /**
     * 选取任意文件
     * @param activity
     * @param requestCode
     */
    public static void pickFile(Activity activity, int requestCode){
        activity.startActivityForResult(getIntent("*/*"),requestCode);
    }

    //========================================第三方app，无返回值====================================
    /**
     * 打开浏览器
     * @param activity
     * @param url : "http://www.google.com"
     */
    public static void openBrowser(Activity activity, String url){
        Intent intent  = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(intent);
    }

    /**
     * 打开浏览器，并直接搜索输入的关键字
     * @param activity
     * @param key
     */
    public static void searchInfo(Activity activity, String key){
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.putExtra(SearchManager.QUERY, key);
        activity.startActivity(intent);
    }

    /**
     * 打开录音机
     * @param activity
     */
    public static void openRecordAudio(Activity activity){
        Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        activity.startActivity(intent);
    }

    /** 打开地图
     * @param activity
     * @param location： "38.899533, -77.036476"
     */
    public static void openMap(Activity activity, String location){
        Uri uri = Uri.parse ("geo:" + location);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        activity.startActivity(intent);
    }

    /** 打开联系人
     * @param activity
     * 需权限： <uses-permission android:name="android.permission.READ_CONTACTS"/>
     */
    public static void openContract(Activity activity){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(ContactsContract.Contacts.CONTENT_URI);//Contacts.People.CONTENT_URI
        activity.startActivity(intent);
    }

    /**
     * 进入拨号界面
     * @param activity
     * <intent-filter>
     *     <action android:name="android.intent.action.DIAL"/>
     *  </intent-filter>
     */
    public static void openDial(Activity activity){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        activity.startActivity(intent);
    }

    /**
     * 打电话
     * @param activity
     * @param number：电话号码
     * 需权限：<uses-permission android:name="android.permission.CALL_PHONE" />
     */
    public static void dialNumber(Activity activity, String number){
        Uri uri = Uri.parse("tel:" + number);
        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
        activity.startActivity(intent);
    }

    /**
     * 发送信息
     * @param activity
     * @param number 手机号码
     * @param msg 内容
     */
    public static void sendMassage(Activity activity, String number, String msg){
        Uri uri = Uri.parse("smsto:"+number);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", msg);
        activity.startActivity(intent);
    }

    /**
     * 打开邮箱
     * @param activity
     * @param mail
     */
    public static void openEmail(Activity activity, String mail){
        Uri uri = Uri.parse("mailto:" + mail);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        activity.startActivity(intent);
    }

    /**
     * 发送电子邮件（文本）
     * @param activity
     * @param mail 目标邮箱
     * @param msg  内容
     * @param title 标题
     */
    public static void sendEmail(Activity activity, String mail, String msg, String title){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, mail);
        intent.putExtra(Intent.EXTRA_TEXT, msg);
        intent.setType("text/plain");
        activity.startActivity(Intent.createChooser(intent, title));
    }

    //==================================打开文件、第三方APP，有返回值=================================
    /**
     * 打开其他APP
     * @param activity
     * @param packageName
     * @param className
     * @param requestCode
     */
    public static void openThirdApp(Activity activity, String packageName, String className, int requestCode){
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(packageName, className);
        intent.setComponent(componentName);
        intent.setAction("android.intent.action.MAIN");
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 进入联系人，并返回选择的联系人
     * @param activity
     * @param requestCode
     */
    public static void pickContract(Activity activity, int requestCode){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(ContactsContract.Contacts.CONTENT_URI);
        activity.startActivityForResult(intent, requestCode);
    }

    //=======================================拍照，视频==============================================
    /**
     * 打开相机，拍照
     * @param activity
     * @param requestCode
     * 返回：Bundle bundle = data.getExtras();
     *      bitmap = (Bitmap)bundle.get("data");
     */
    public static void openCamera4Image(Activity activity, int requestCode){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//"android.media.action.IMAGE_CAPTURE"
        activity.startActivityForResult(intent,requestCode);
    }

    /**
     * 打开相机，拍照, 并保存到本地
     * @param activity
     * @param path
     * @param requestCode
     */
    public static void openCamera4ImageAndSave(Activity activity, String path, int requestCode){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        long time = Calendar.getInstance().getTimeInMillis();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(path, time + ".jpg")));
        activity.startActivityForResult(intent,requestCode);
    }

    /** 打开相机，录视频
     * @param activity
     * @param requestCode
     * @param quality 0 means low quality, 1 means high quality
     * @param sizeLimit  the maximum allowed size.
     * @param durationLimit the maximum allowed recording duration in seconds.
     * @param path 存储路径
     */
    public static void openCamera4Video(Activity activity, int requestCode, int quality, int sizeLimit, int durationLimit, String path){
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, quality);
        intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, sizeLimit);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, durationLimit);//max seconds

        long time = Calendar.getInstance().getTimeInMillis();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(path, time + ".mp4")));
        activity.startActivityForResult(intent, requestCode);
    }

    //===============================从设备上选择APP打开目标文件======================================
    //filePath /sdcard/song.mp3"
    public static void openAudioFile(Activity activity, String filePath){
        Uri uri = Uri.parse("file://" + filePath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "audio/*"); //mp3
        activity.startActivity(intent);
    }

    //"file:///sdcard/video.mp4"
    public static void openVideoFile(Activity activity, String filePath) {
        Uri uri = Uri.parse("file://" + filePath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "video/*"); //mp4
        activity.startActivity(intent);
    }

    //"/mnt/sdcard/images/001041580.jpg"
    public static void openImageFile(Activity activity, String filePath) {
        Uri uri = Uri.parse("file://" + filePath);;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "image/*"); //image
        activity.startActivity(intent);
    }

    public static void openTextFile(Activity activity, String filePath) {
        Uri uri = Uri.fromFile(new File(filePath));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "Text/plain"); //text
        activity.startActivity(intent);
    }


    //================从intent返回的文件路径（provider）获取其在设备上的真正存储路径====================
    public static String getRealPath4Uri(Context context, Uri uri, ContentResolver contentResolver){
        String path;
        if ("file".equalsIgnoreCase(uri.getScheme())){//使用第三方应用打开
            path = uri.getPath();
        }else{//content.provider
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                path = getPath(uri, contentResolver, context);
            } else {//4.4以下下系统调用方法
                path = getRealPathFromURI(uri, contentResolver);
            }
        }

        return path;
    }

    private static String getRealPathFromURI(Uri contentUri, ContentResolver contentResolver) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = contentResolver.query(contentUri, proj, null, null, null);
        if (null != cursor && cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
            cursor.close();
        }
        return res;
    }

    private static String getPath(Uri uri, ContentResolver contentResolver, Context context) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(contentResolver, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(contentResolver, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(contentResolver, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    private static String getDataColumn(ContentResolver contentResolver, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = contentResolver.query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
