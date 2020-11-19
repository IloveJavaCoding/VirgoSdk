package com.nepalese.virgosdk.Beans;

public class VideoFile extends BaseBean{
    private int id;
    private String name;//video name
    private String display;//name+后缀
    private String artist;//
    private String path;//存储 path
    private String thumbPath;//缩略图 path
    private String resolution;//分辨率
    private String album;
    private String description;
    private long size;//大小
    private long date;//最后修改日期
    private long duration;//时长

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbpath) {
        this.thumbPath = thumbpath;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
