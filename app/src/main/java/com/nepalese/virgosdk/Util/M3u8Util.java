package com.nepalese.virgosdk.Util;

import com.nepalese.virgosdk.Beans.M3U8;
import com.nepalese.virgosdk.Beans.M3U8Ts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * @author nepalese on 2020/11/23 13:53
 * @usage m3u8文件下载，解析，内部ts文件下载，合并
 */
public class M3u8Util {
    //使用流程
    public static void downloadM3u8(String url, String saveDir, String saveName){
        String temp = "temp";
        String tempPath = saveDir + File.separator + temp;//存放缓存文件
        File tempDir = new File(tempPath);
        if (tempDir.exists()) {
            // 清空内部文件
            FileUtil.deleteDir(tempDir.getAbsolutePath());
        }
        String name=  getM3u8Name(url);
        saveM3u8File(url, tempPath, name);

        M3U8 m3U8 = parseM3u8File(tempPath, name, url);
        downloadFragment(m3U8);
        mergeTsFile(m3U8, saveDir, saveName);
        //清除缓存
        FileUtil.deleteDir(tempDir.getAbsolutePath());
    }

    //提取文件名
    public static String getM3u8Name(String url) {
        return url.substring(url.lastIndexOf("/") + 1,
                url.toLowerCase().indexOf(".m3u8", url.lastIndexOf("/"))) + ".m3u8";
    }

    //在子线程上运行
    public static void saveM3u8File(String url, String tempDir, String m3u8Name) {
        File dir = new File(tempDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        InputStream ireader;
        try {
            ireader = new URL(url).openStream();
            FileOutputStream writer = new FileOutputStream(new File(dir, m3u8Name));
            FileUtil.readerWriterStream(ireader, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析本地m3u8文件
     * @param dir m3u8 文件存储路径
     * @param m3u8Name m3u8 文件名
     * @param url m3u8链接
     * @return M3U8对象
     */
    public static M3U8 parseM3u8File(String dir, String m3u8Name, String url) {
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
                    if (line.toLowerCase().startsWith("http://") || line.toLowerCase().startsWith("https://")) {
                        String tag = line.substring(line.lastIndexOf("/"),
                                line.toLowerCase().indexOf(".m3u8", line.lastIndexOf("/")));
                        String m3u8Name2 = tag + ".m3u8";
                        int tp = urlRefer.indexOf(line);
                        String dir2 = dir;
                        if (tp != -1) {
                            if ((line.lastIndexOf("/") + 1) > (tp + urlRefer.length())) {// 判断路径是否重复
                                line.substring(tp + urlRefer.length(), line.lastIndexOf("/") + 1);
                            } else {
                                dir2 = dir;
                            }
                        } else {
                            dir2 = dir + tag + File.separator;
                        }
                        // 获取远程文件
                        saveM3u8File(line, dir2, m3u8Name2);
                        // 进行递归获取数据
                        m3u8.addM3u8(parseM3u8File(dir2, m3u8Name2, line));
                    } else {
                        // 不是使用http协议的
                        // TODO
                    }
                } else {
                    // 1606096656-1-1593893847.hls.ts
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

    // 根据M3U8对象下载视频段
    public static void downloadFragment(M3U8 m3u8){
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
            new Thread(runnable).start();
        }
    }

    // 合并文件
    public static void mergeTsFile(M3U8 m3u8, String outDir, String saveName){
        String savePath = outDir + File.separator + saveName;
        File file = new File(savePath);

        FileOutputStream output = null;
        try {
            output = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for(int i=0; i<m3u8.getM3u8List().size(); i++){
            String head = "video_" + i + "_";
            mergeTsFile(m3u8.getM3u8List().get(i), outDir, head + saveName);
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
                    // TODO Auto-generated catch block
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
