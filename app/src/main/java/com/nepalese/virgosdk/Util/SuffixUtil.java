package com.nepalese.virgosdk.Util;

/*
 *  Nepalese created on 2020/11/29
 *  usage: 文件路径，网络链接处理
 */
import android.text.TextUtils;

import com.nepalese.virgosdk.Manager.Constants;

public class SuffixUtil {
    public static final int TYPE_IMAGE = 1;//图片
    public static final int TYPE_AUDIO = 2;//音频
    public static final int TYPE_VIDEO = 3;//视频
    public static final int TYPE_TEXT = 4;//文本
    public static final int TYPE_FILE = 5;//可下载文件
    public static final int TYPE_WEB = 6;//网页
    public static final int TYPE_OTHER = 8;//其他

    /**
     * 获取文件或链接的后缀名
     * @param path
     * @return (.mp3 ...)
     */
    public static String getFileSuffix(String path){
        if(TextUtils.isEmpty(path)) return null;

        if(path.contains(".")){
            return path.substring(path.lastIndexOf("."));
        }

        return null;
    }

    /**
     * 获取文件名，不带后缀名
     * @param path
     * @return
     */
    public static String getFileName(String path) {
        return TextUtils.isEmpty(path) ? null : path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
    }

    /**
     * 获取带有后缀名的文件名
     * @param path
     * @return
     */
    public static String getNameWithSuffix(String path) {
        return TextUtils.isEmpty(path) ? null : path.substring(path.lastIndexOf("/") + 1);
    }

    /**
     * 获取整个链接头：
     * @param url
     * @return 协议及主机地址 http(s)://xxx.xxx.xxx/
     */
    public static String getUrlHead(String url){
        if(TextUtils.isEmpty(url)) return null;

        if(!(url.startsWith("http")||url.startsWith("https")))return null;

        return url.substring(0, url.indexOf("/", 10));
    }

    /**
     * 获取url链接协议类型：
     * @param url
     * @return http、https:...
     */
    public static String getUrlScheme(String url){
        if(TextUtils.isEmpty(url)) return null;

        if(!(url.startsWith("http")||url.startsWith("https")))return null;

        return url.substring(0, url.indexOf("/"));
    }

    /**
     * 获取文件类型
     * @param filePath
     * @return
     */
    public static int getFileType(String filePath){
        String suffix = getFileSuffix(filePath);
        if(suffix!=null){
            suffix = suffix.substring(1);
            for(String str: Constants.SUFFIX_IMAGE){
               if(str.equals(suffix)){
                   return TYPE_IMAGE;
               }
            }

            for(String str: Constants.SUFFIX_AUDIO){
                if(str.equals(suffix)){
                    return TYPE_AUDIO;
                }
            }

            for(String str: Constants.SUFFIX_VIDEO){
                if(str.equals(suffix)){
                    return TYPE_VIDEO;
                }
            }

            for(String str: Constants.SUFFIX_TEXT_PLAIN){
                if(str.equals(suffix)){
                    return TYPE_TEXT;
                }
            }
        }

        return TYPE_OTHER;
    }

    /**
     * 判断链接类型
     * @param url
     * @return
     */
    public static int getUrlType(String url){
        if(TextUtils.isEmpty(url) || !(url.startsWith("http") || url.startsWith("https"))){
            return TYPE_OTHER;
        }

        for(String str: Constants.LINK_SUFFIX_FILE){
            if(url.toLowerCase().endsWith(str)){
                return TYPE_FILE;
            }
        }

        return TYPE_WEB;
    }

    /**
     * 跳转过滤文件类型
     * @param filePath
     * @return
     */
    public static String getIntentType(String filePath){
        int fileType = getFileType(filePath);
        switch (fileType){
            case TYPE_IMAGE:
                return Constants.INTENT_TYPE_IMAGE;
            case TYPE_AUDIO:
                return Constants.INTENT_TYPE_AUDIO;
            case TYPE_VIDEO:
                return Constants.INTENT_TYPE_VIDEO;
            case TYPE_TEXT:
                return Constants.INTENT_TYPE_TEXT;
            default:
                return Constants.INTENT_TYPE_ALL;
        }
    }
}
