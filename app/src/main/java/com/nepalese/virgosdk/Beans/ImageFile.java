package com.nepalese.virgosdk.Beans;

/**
 * @author nepalese on 2020/11/19 11:17
 * @usage
 */
public class ImageFile {
    private long iId;
    private String iName;
    private String iDName;
    private String iPath;
    private String iAlbum;
    private long iSize;

    public long getiId() {
        return iId;
    }

    public void setiId(long iId) {
        this.iId = iId;
    }

    public String getiName() {
        return iName;
    }

    public void setiName(String iName) {
        this.iName = iName;
    }

    public String getiDName() {
        return iDName;
    }

    public void setiDName(String iDName) {
        this.iDName = iDName;
    }

    public String getiPath() {
        return iPath;
    }

    public void setiPath(String iPath) {
        this.iPath = iPath;
    }

    public String getiAlbum() {
        return iAlbum;
    }

    public void setiAlbum(String iAlbum) {
        this.iAlbum = iAlbum;
    }

    public long getiSize() {
        return iSize;
    }

    public void setiSize(long iSize) {
        this.iSize = iSize;
    }
}
