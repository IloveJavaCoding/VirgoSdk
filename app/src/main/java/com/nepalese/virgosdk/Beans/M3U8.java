package com.nepalese.virgosdk.Beans;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nepalese on 2020/11/23 13:57
 * @usage m3u8解析对象
 */
public class M3U8 extends BaseBean{
    private String urlRefer;//m3u8链接除去名称部分
    private String saveDir;//m3u8文件存储到本地的文件夹路径
    private List<M3U8Ts> tsList = new ArrayList<M3U8Ts>();//ts 对象
    private List<M3U8> m3u8List = new ArrayList<M3U8>();//内部可能包含其他m3u8链接

    public String getUrlRefer() {
        return urlRefer;
    }

    public void setUrlRefer(String urlRefer) {
        this.urlRefer = urlRefer;
    }

    public String getSaveDir() {
        return saveDir;
    }

    public void setSaveDir(String saveDir) {
        this.saveDir = saveDir;
    }

    public List<M3U8> getM3u8List() {
        return m3u8List;
    }

    public void addM3u8(M3U8 m3u8) {
        this.m3u8List.add(m3u8) ;
    }

    public List<M3U8Ts> getTsList() {
        return tsList;
    }

    public void setTsList(List<M3U8Ts> tsList) {
        this.tsList = tsList;
    }

    public void addTs(M3U8Ts ts) {
        this.tsList.add(ts);
    }

    //获取M3U8对象时长
    public float getDuration() {
        float duration = 0;
        for (M3U8Ts ts : this.getTsList()) {
            duration += ts.getSecond();
        }
        return duration;
    }

    @Override
    public String toString() {
        return "M3U8{" +
                "urlRefer='" + urlRefer + '\'' +
                ", saveDir='" + saveDir + '\'' +
                ", tsList=" + tsList +
                ", m3u8List=" + m3u8List +
                '}';
    }
}
