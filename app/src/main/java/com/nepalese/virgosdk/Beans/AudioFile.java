package com.nepalese.virgosdk.Beans;

public class AudioFile extends BaseBean{
    private long sId;
    private String sName;
    private String sDName;
    private String aArtist;
    private String sPath;
    private String sAlbum;//专辑
    private long sLogo; //图片id
    private int sDuration;//duration
    private long sSize;

    public String getsDName() {
        return sDName;
    }

    public void setsDName(String sDname) {
        this.sDName = sDname;
    }

    public long getsLogo() {
        return sLogo;
    }

    public void setsLogo(long sLogo) {
        this.sLogo = sLogo;
    }

    public long getsId() {
        return sId;
    }

    public void setsId(long sId) {
        this.sId = sId;
    }

    public long getsSize() {
        return sSize;
    }

    public void setsSize(long sSize) {
        this.sSize = sSize;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public String getaArtist() {
        return aArtist;
    }

    public void setaArtist(String aArtist) {
        this.aArtist = aArtist;
    }

    public String getsPath() {
        return sPath;
    }

    public void setsPath(String sPath) {
        this.sPath = sPath;
    }

    public String getsAlbum() {
        return sAlbum;
    }

    public void setsAlbum(String sAlbum) {
        this.sAlbum = sAlbum;
    }

    public int getsDuration() {
        return sDuration;
    }

    public void setsDuration(int sDuration) {
        this.sDuration = sDuration;
    }
}
