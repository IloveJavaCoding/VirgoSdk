package com.nepalese.virgosdk.Util;

import android.text.TextUtils;
import android.util.Log;

import com.nepalese.virgosdk.Manager.Constants;
import com.nepalese.virgosdk.Util.RegexUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author nepalese on 2020/11/30 10:56
 * @usage 网页链接爬取
 */
public class CrawlerUtil {
    private static final String TAG = "CrawlerUtil";

    public static final int TYPE_LINK_ALL = 1;
    public static final int TYPE_LINK_IMG = 2;
    private static final long TIME_OUT = 60 * 1000L;

    private static final String head = null;//链接头
    private static final String scheme = null;//协议
    private static final ExecutorService executor = Executors.newFixedThreadPool(10);

    private static String html = null;//网页内容

    public static List<String> getLinks(String url, int type){
        getHtml(url);
        executor.shutdown();
        try {
            executor.awaitTermination(TIME_OUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(type==TYPE_LINK_ALL){
            return crawlWebPage(html);
        }else if (type==TYPE_LINK_IMG){
            return crawlImage2(html);
        }
        return null;
    }

    private static void getHtml(String url){
        executor.submit(() -> {
            StringBuilder buffer = new StringBuilder();
            BufferedReader bufferedReader = null;
            InputStreamReader inputStreamReader = null;
            try {
                URL url1 = new URL(url);
                URLConnection uc = url1.openConnection();
                uc.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.66 Safari/537.36)");

                inputStreamReader = new InputStreamReader(uc.getInputStream(), StandardCharsets.UTF_8);
                bufferedReader = new BufferedReader(inputStreamReader);

                String temp;
                while ((temp = bufferedReader.readLine()) != null) {
                    buffer.append(temp.trim());
                }
                html = buffer.toString();
                Log.i(TAG, "getHtml: " + html);
            }catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        inputStreamReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private static List<String> crawlWebPage(String html) {
        if(!TextUtils.isEmpty(html)) {
            //src="http://pic1.win4000.com/xxx/5fbca616736aa.jpg"
            //src="//img.tukuppt.com/xxx/5c88be672193357468.mp3"
            Pattern pattern = Pattern.compile(Constants.RE_FILTER_URL);
            Matcher matcher = pattern.matcher(html);

            List<String> list = new ArrayList<>();
            while (matcher.find()) {
                String link = matcher.group();
                if(link.contains("html") || link.contains("=")) {
                    continue;
                }
                //1. 仅缺少协议：http(s):
                if (link.startsWith("//")) {
                    link = scheme + link;
                }
                //2. 缺少整个链接头： http(s)://xxx.xxx.com
                else if (link.startsWith("/")) {
                    link = head + link;
                }
                //3. 完整的链接
                list.add(link);
            }
            return list;
        }else{
            Log.e(TAG, "crawlWebPage: 网页内容为空");
        }
        return null;
    }

    private static List<String> crawlImage(String html){
        if(!TextUtils.isEmpty(html)){
            //<img src="https://qqpublic.qpic.cn/qq_public/0/0-1505819196-A8F81F3C5693B38EECE559AB83D9E423/600?fmt=jpg&amp;h=1266&amp;ppv=1&amp;size=123&amp;w=600" alt="">
            Pattern pattern = Pattern.compile(Constants.RE_FILTER_IMG);
            Matcher matcher = pattern.matcher(html);
            List<String> list = new ArrayList<>();
            while (matcher.find()) {
                String link = matcher.group();
                if(link.contains("src")){
                    continue;
                }
                if(link.contains("?")){
                    link = link.substring(0, link.indexOf("?")) + ".jpg";
                }
                Log.i(TAG, "crawlImage: link " + link);
                //1. 仅缺少协议：http(s):
                if (link.startsWith("//")) {
                    link = scheme + link;
                }
                //2. 缺少整个链接头： http(s)://xxx.xxx.com
                else if (link.startsWith("/")) {
                    link = head + link;
                }
                //3. 完整的链接
                list.add(link);
            }
            return list;
        }
        else{
            Log.e(TAG, "crawlWebPage: 网页内容为空");
        }
        return null;
    }

    private static List<String> crawlImage2(String html){
        if(!TextUtils.isEmpty(html)){
            List<String> urls = RegexUtil.matchNumsUrl(html);
            return filterUrl(urls);
        } else{
            Log.e(TAG, "crawlWebPage: 网页内容为空");
        }
        return null;
    }

    private static List<String> filterUrl(List<String> urls) {
        List<String> out = new ArrayList<>();
        for(String str: urls) {
            if((str.startsWith("http") || str.startsWith("https"))) {
                if(str.contains("jpeg") || str.contains("png")) {
                    if(!str.contains("token")) {
                        str = str.substring(0, str.indexOf("jpeg")+4);
                    }
                    out.add(str);
                }
            }
        }

        return out;
    }
}