package com.nepalese.virgosdk.Beans;

import java.util.ArrayList;
import java.util.List;

public class M3U8 extends BaseBean{
	private String keyUrl;//密钥下载连接
	private String urlHead;//链接头: xxx.xxx.com
	private String urlRefer;//m3u8链接除去名称部分
    private String saveDir;//m3u8文件存储到本地的文件夹路径
    private List<M3U8Ts> tsList = new ArrayList<>();
    private List<M3U8> m3u8List = new ArrayList<>();//内部可能包含其他m3u8链接

	public String getKeyUrl() {
		return keyUrl;
	}

	public void setKeyUrl(String keyUrl) {
		this.keyUrl = keyUrl;
	}
	
	public String getUrlHead() {
		return urlHead;
	}

	public void setUrlHead(String urlHead) {
		this.urlHead = urlHead;
	}

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

}
