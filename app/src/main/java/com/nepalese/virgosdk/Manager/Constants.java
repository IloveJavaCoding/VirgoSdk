package com.nepalese.virgosdk.Manager;

/**
 * @author nepalese on 2020/11/30 10:39
 * @usage 常量
 */
public class Constants {
    //文件夹
    public static final String DIR_DOWNLOAD = "download";
    public static final String DIR_M3U8 = "m3u8";

    //文件拓展名
    public static final String[] SUFFIX_IMAGE = {"jpg", "jpeg", "png", "svg", "bmp", "tiff"};
    public static final String[] SUFFIX_AUDIO = {"mp3", "wav", "wma", "aac", "flac"};
    public static final String[] SUFFIX_VIDEO = {"mp4", "flv", "avi", "wmv", "mpeg", "mov", "rm", "swf"};
    public static final String[] SUFFIX_TEXT_PLAIN = {"txt", "html", "xml", "java", "py", "php"};

    //链接拓展名：
    public static final String[] LINK_SUFFIX_FILE = {"mp3", "jpg", "png", "jpeg", "mp4", "m3u8", "pdf", "txt"};
    public static final String[] LINK_SUFFIX_WEB = {"html", "net", "com", "cn"};

    //Intent 跳转文件类型
    public static final String INTENT_TYPE_IMAGE = "image/*";
    public static final String INTENT_TYPE_AUDIO = "audio/*";
    public static final String INTENT_TYPE_VIDEO = "video/*";
    public static final String INTENT_TYPE_TEXT = "text/plain";
    public static final String INTENT_TYPE_ALL = "*/*";

    //正则规则：
    public static final String RE_FILTER_URL = "(https?:)?//(?:[-\\w.]|(?:%[\\da-fA-F]{2}))+[^\\u4e00-\\u9fa5]+[\\w-_?&=#%:]{0}(jpg|png|jpeg|mp3|mp4){1}";
    public static final String RE_FILTER_IMG ="(?<=img src=\")[^\"]*(?=\")";
}
