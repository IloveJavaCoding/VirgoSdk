package com.nepalese.virgosdk.Beans;

public class AudioFile extends BaseBean{
    private long id;
    private String name;
    private String disName;
    private String artist;
    private String path;
    private String album;//专辑
    private long logo; //图片id
    private int duration;//duration
    private long size;

    public String getDisName() {
        return disName;
    }

    public void setDisName(String sDname) {
        this.disName = sDname;
    }

    public long getLogo() {
        return logo;
    }

    public void setLogo(long logo) {
        this.logo = logo;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
