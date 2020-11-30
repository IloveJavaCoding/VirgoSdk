package com.nepalese.virgosdk.Util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.nepalese.virgosdk.Beans.M3U8;
import com.nepalese.virgosdk.Beans.M3U8Ts;
import com.nepalese.virgosdk.Manager.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author nepalese on 2020/11/23 13:53
 * @usage m3u8文件下载，解析，内部ts文件下载，合并
 */
public class M3u8Util {
    private static final String TAG = "M3u8Util";
    private final String downloadPath;//下载文件放置位置
    private final String tempPath;//m3u8 文件缓存位置
    private String m3u8Name;//m3u8 链接后部名称

    private Context context;
    @SuppressLint("StaticFieldLeak")
    private static volatile M3u8Util instance;
    private static ExecutorService executor = Executors.newFixedThreadPool(10);
    private static final long TIME_OUT_10S = 10*1000L;
    private static final long TIME_OUT_5M = 5*60*1000L;

    private M3u8Util(Context context){
        this.context = context;
        downloadPath = FileUtil.getAppRootPth(context) + File.separator + Constants.DIR_M3U8;
        tempPath = downloadPath + File.separator + "temp";
    }

    public static M3u8Util getInstance(Context context){
        if(instance==null){
            synchronized (M3u8Util.class){
                if(instance==null){
                    instance = new M3u8Util(context);
                }
            }
        }
        return instance;
    }

    /**
     * 唯一外部可访问接口
     * @param m3u8Url m3u8有效链接
     * @return -1：下载失败； 1：下载成功
     */
    public int downloadM3u8(String m3u8Url) {
        int out = -1;
        if(!TextUtils.isEmpty(m3u8Url) && (m3u8Url.startsWith("http") || m3u8Url.startsWith("https")) && m3u8Url.endsWith("m3u8")){
            //显示下载提示
            m3u8Name  =  getM3u8Name(m3u8Url);
            File tempDir = new File(tempPath);
            if (tempDir.exists()) {
                // 清空内部文件
                FileUtil.deleteDir(tempPath);
            }else{
                tempDir.mkdirs();
            }

            //下载m3u8文件到本地：
            cacheM3u8File(m3u8Url);
            try {
                executor.awaitTermination(TIME_OUT_10S, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return out;
            }

            //解析m3u8文件
            Log.i(TAG, "downloadM3u8: 开始解析");
            M3U8 m3U8 = parseM3u8File(tempPath, m3u8Name, m3u8Url);

            //下载片段
            Log.i(TAG, "downloadM3u8: 开始下载片段中..");
            downloadFragment(m3U8);
            executor.shutdown();
            try {
                executor.awaitTermination(TIME_OUT_5M, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return out;
            }
            Log.i(TAG, "downloadM3u8: 下载片段完成！");

            //合并ts文件为mp4
            Log.i(TAG, "downloadM3u8: 开始合并！");
            mergeTsFile(m3U8, downloadPath);
            Log.i(TAG, "downloadM3u8: 合并完成！");

            //删除缓存文件
            FileUtil.deleteDir(tempPath);
            out = 1;
            SystemUtil.showToast(context, "下载完成！");
        }else {
            SystemUtil.showToast(context, "无效链接！");
        }

        return out;
    }

    //获取m3u8链接后补带后缀名称
    private String getM3u8Name(String url) {
        return url.substring(url.lastIndexOf("/") + 1, url.toLowerCase().indexOf(".m3u8", url.lastIndexOf("/"))) + ".m3u8";
    }

    //下载m3u8文件到本地
    private void cacheM3u8File(String m3u8Url){
        Runnable rM3u8Task = () -> {
            InputStream inputStream;
            try {
                inputStream = (new URL(m3u8Url)).openStream();
                FileOutputStream outputStream = new FileOutputStream(new File(tempPath, m3u8Name));
                FileUtil.readerWriterStream(inputStream, outputStream);

                inputStream.close();
                outputStream.close();
                Log.i(TAG, "downloadM3u8: m3u8文件下载完成");
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        Log.i(TAG, "downloadM3u8: 下载m3u8文件");
        executor.submit(rM3u8Task);
    }

    /**
     * 解析本地m3u8文件
     * @param dir m3u8 文件存储路径
     * @param m3u8Name m3u8 文件名
     * @param url m3u8链接
     * @return M3U8对象
     */
    private M3U8 parseM3u8File(String dir, String m3u8Name, String url) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(new File(dir, m3u8Name))));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // 解析请求的相关路径: ts流头连接
        String urlRefer = url.substring(0, url.lastIndexOf("/") + 1);

        M3U8 m3u8 = new M3U8();
        m3u8.setUrlRefer(urlRefer);
        m3u8.setSaveDir(dir);

        String line;
        float seconds = 0;
        try {
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#")) {
                    // #EXTINF:10 片段时长
                    if (line.startsWith("#EXTINF:")) {
                        line = line.substring(8);
                        if (line.endsWith(",")) {
                            line = line.substring(0, line.length() - 1);
                        }
                        if (line.contains(",")) {
                            line = line.substring(0, line.indexOf(","));
                        }
                        // 解析每个分段的长度
                        seconds = Float.parseFloat(line);
                    }
                    // #EXT-X-PROGRAM-DATE-TIME:2020-11-23T01:57:16Z 跳过
                    continue;
                }
                // 文件包含另一个m3u8文件
                if (line.endsWith("m3u8")) {
                    String newUrl;
                    if (!(line.toLowerCase().startsWith("http://") || line.toLowerCase().startsWith("https://"))) {
                        // 不是使用http协议的
                        // /20200531/B9bXVPnl/600kb/hls/index.m3u8
                        String head = PathUtil.getUrlHead(url);
                        newUrl = head + line;
                    } else {
                        newUrl = line;
                    }
                    String m3u8Name2 = ConvertUtil.string2Hex(String.valueOf(DateUtil.getCurTime2())) + ".m3u8";
                    // 获取远程文件
                    cacheM3u8File(newUrl);
                    // 进行递归获取数据
                    m3u8.addM3u8(parseM3u8File(tempPath, m3u8Name2, newUrl));
                } else {
                    //1.  1606096656-1-1593893847.hls.ts
                    //2.  20200531/B9bXVPnl/600kb/hls/f7W7r86J.ts
                    if(line.contains("/")){
                        line  = line.substring(line.lastIndexOf("/")+1);
                    }
                    m3u8.addTs(new M3U8Ts(line, seconds));
                    seconds = 0;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return m3u8;
    }

    //根据M3U8对象下载视频段
    private void downloadFragment(M3U8 m3u8){
        File dir = new File(m3u8.getSaveDir());
        if (!dir.exists()) {
            dir.mkdirs();
        }

        for (M3U8 m : m3u8.getM3u8List()) {
            // 下载对应的m3u8
            downloadFragment(m);
        }

        for (M3U8Ts ts : m3u8.getTsList()) {
            Runnable runnable = () -> {
                FileOutputStream writer;
                try {
                    writer = new FileOutputStream(new File(dir, ts.getContent()));
                    FileUtil.readerWriterStream(new URL(m3u8.getUrlRefer() + ts.getContent()).openStream(), writer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };
            executor.submit(runnable);
        }
    }

    // 合并文件
    private void mergeTsFile(M3U8 m3u8, String outDir){
        String saveName = DateUtil.getCurTime().toString() + ".mp4";
        String savePath = outDir + File.separator + saveName;
        File file = new File(savePath);

        FileOutputStream output = null;
        try {
            output = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for(int i=0; i<m3u8.getM3u8List().size(); i++){
            mergeTsFile(m3u8.getM3u8List().get(i), outDir);
        }

        for (M3U8Ts ts : m3u8.getTsList()) {
            File fileTemp = new File(m3u8.getSaveDir(), ts.getContent());
            if (fileTemp.exists()) {
                InputStream input = null;
                byte[] buffer = new byte[1024];
                try {
                    input = new FileInputStream(fileTemp);
                    while (true) {
                        int length;
                        if ((length = input.read(buffer)) == -1) {
                            break;
                        }
                        output.write(buffer, 0, length);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                try {
                    output.flush();
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                throw new RuntimeException(ts.getContent() + "文件不存在，合成失败！");
            }
        }
        try {
            output.flush();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
