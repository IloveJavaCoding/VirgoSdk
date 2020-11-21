package com.nepalese.virgosdk.Util;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Size;

import androidx.annotation.RequiresApi;

import com.nepalese.virgosdk.Beans.AudioFile;
import com.nepalese.virgosdk.Beans.ImageFile;
import com.nepalese.virgosdk.Beans.VideoFile;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author nepalese on 2020/11/19 09:33
 * @usage 媒体：获取由content provider 整理的音频，视频；
 * 自定义获取音频，视频信息，音频配图，视频缩略图，分辨率
 * ①raw下的资源：MediaPlayer.create(this, R.raw.test);
 * ②本地文件路径：mp.setDataSource("/sdcard/test.mp3");
 * ③网络URL文件：mp.setDataSource("http://www.xxx.com/music/test.mp3");
 */
public class MediaUtil {
    private static final String TAG = "MediaUtil";

    public static final int TYPE_FILE = 1;
    public static final int TYPE_URL = 2;

    //==========================================获取本地图片=========================================
    public static List<ImageFile> getImageList(Context context){
        List<ImageFile> imgs = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,null,null,null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);

        for(int i=0; i<cursor.getCount(); i++){
            cursor.moveToNext();
            ImageFile img = new ImageFile();

            long id = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));
            String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.TITLE));
            String disName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
            String album = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.ALBUM));
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.RELATIVE_PATH));//.Media.DATA
            long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));

            img.setiId(id);
            img.setiName(name);
            img.setiDName(disName);
            img.setiAlbum(album);
            img.setiPath(path);
            img.setiSize(size);

            imgs.add(img);
        }
        return imgs;
    }



    //=========================================获取本地音频文件======================================
    /**
     * 借用content provider
     * @param context
     * @param limit 时长过滤：单位秒
     * @return
     */
    public static List<AudioFile> getAudioList(Context context, int limit){
        List<AudioFile> songs = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

        for(int i=0; i<cursor.getCount(); i++){
            cursor.moveToNext();
            AudioFile songsInfo = new AudioFile();

            long id = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
            String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String disName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
            String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
            long albumId = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
            String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.RELATIVE_PATH));//.Media.DATA  ???
            int duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
            int isMusic = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.IS_MUSIC));

            if(isMusic!=0 && duration>limit*1000){
                songsInfo.setsId(id);
                songsInfo.setsName(name);
                songsInfo.setsDName(disName);
                songsInfo.setaArtist(artist);
                songsInfo.setsAlbum(album);
                songsInfo.setsLogo(albumId);
                songsInfo.setsPath(path);
                songsInfo.setsDuration(duration);
                songsInfo.setsSize(size);

                songs.add(songsInfo);
            }
        }
        return songs;
    }

    /**
     * 获取本地音频相关信息
     * @param path
     * @return
     */
    public static AudioFile getAudioInfo(String path){
        AudioFile songsInfo = null;
        File file = new File(path);
        if(file.exists()){
            String displayName = path.substring(path.lastIndexOf('/')+1);
            String name = displayName.substring(0, displayName.lastIndexOf('.'));
            songsInfo = new AudioFile();

            songsInfo.setsId(0);
            songsInfo.setsName(name);
            songsInfo.setsDName(displayName);
            songsInfo.setaArtist(name);
            songsInfo.setsPath(file.getAbsolutePath());
            songsInfo.setsAlbum(name);
            songsInfo.setsLogo(-1);//未知
            songsInfo.setsDuration((int) getDuration(path));//duration
            songsInfo.setsSize(file.length());
        }
        return songsInfo;
    }

    public static AudioFile getAudioInfo2(String path) {
        if (path == null)
            return null;

        File file = new File(path);
        if(!file.exists())
            return null;

        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(path);

        final int duration;

        String keyDuration = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        // ensure the duration is a digit, otherwise return null song
        if (keyDuration == null || !keyDuration.matches("\\d+")) return null;
        duration = Integer.parseInt(keyDuration);

        final String title = extractMetadata(metadataRetriever, MediaMetadataRetriever.METADATA_KEY_TITLE, file.getName());
        final String displayName = extractMetadata(metadataRetriever, MediaMetadataRetriever.METADATA_KEY_TITLE, file.getName());
        final String artist = extractMetadata(metadataRetriever, MediaMetadataRetriever.METADATA_KEY_ARTIST, "unknown");
        final String album = extractMetadata(metadataRetriever, MediaMetadataRetriever.METADATA_KEY_ALBUM, "unknown");

        AudioFile songsInfo = new AudioFile();
        songsInfo.setsId(0);
        songsInfo.setsName(title);
        songsInfo.setsDName(displayName);
        songsInfo.setaArtist(artist);
        songsInfo.setsPath(path);
        songsInfo.setsAlbum(album);
        songsInfo.setsLogo(-1);//未知
        songsInfo.setsDuration(duration);//duration
        songsInfo.setsSize(file.length());

        return songsInfo;
    }

    private static String extractMetadata(MediaMetadataRetriever retriever, int key, String defaultValue) {
        String value = retriever.extractMetadata(key);
        if (TextUtils.isEmpty(value)) {
            value = defaultValue;
        }
        return value;
    }

    //===========================================获取音频文件专辑图片====================================
    /**
     * 获取音频文件相册图片
     * @param context
     * @param path 音频路径
     * @param defaultAlbumId 默认资源文件id
     * @return
     */
    public static Bitmap getAudioAlbum(Context context, String path, int defaultAlbumId) {
        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        try {
            metadataRetriever.setDataSource(path);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "parseAlbum: ", e);
        }
        byte[] albumData = metadataRetriever.getEmbeddedPicture();
        if (albumData != null) {
            return BitmapFactory.decodeByteArray(albumData, 0, albumData.length);
        }
        return BitmapFactory.decodeResource(context.getResources(), defaultAlbumId);
    }

    /**
     * 借用content provider
     * @param context
     * @param songId 音频 id
     * @param logoId logo id
     * @param defaultAlbumId 默认资源文件id
     * @return
     * uri = Uri.parse("content://media/external/audio/media/"+songId+"/albumart");
     */
    public static Bitmap getAudioLogo(Context context, long songId, long logoId, int defaultAlbumId){
        Bitmap bitmap = null;
        if(songId<0){
            throw new IllegalArgumentException("Must specify an album or a song id!");
        }
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            FileDescriptor descriptor = null;
            if(logoId<0){
                //使用默认图片
                bitmap = BitmapFactory.decodeResource(context.getResources(), defaultAlbumId);
            }else{
                Uri uri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), logoId);
                ParcelFileDescriptor parcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r");
                if(parcelFileDescriptor!=null){
                    descriptor = parcelFileDescriptor.getFileDescriptor();
                }
                options.inSampleSize = 1;//original size
                options.inJustDecodeBounds = false;
                options.inDither = true;
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;

                bitmap = BitmapFactory.decodeFileDescriptor(descriptor,null, options);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    //=========================================获取本地视频文件======================================
    /**
     * 借用content provider
     * @param context
     * @param limit  时长过滤：单位秒
     * @return
     */
    public static List<VideoFile> getVideoList(Context context, int limit){
        List<VideoFile> videos = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null,null,null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);

        String[] thumbColumns = new String[]{MediaStore.Video.Thumbnails.DATA, MediaStore.Video.Thumbnails.VIDEO_ID};

        for(int i=0; i<cursor.getCount(); i++) {
            cursor.moveToNext();
            VideoFile videoFile = new VideoFile();

            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media._ID));
            String name = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
            String display = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
            String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.ARTIST));
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.RELATIVE_PATH)); //Media.DATA
            String resolution = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.RESOLUTION));
            String album = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.ALBUM));
            String description = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DESCRIPTION));
            long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
            long date = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED));
            long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));

            String thumbnail = null;
            String selection = MediaStore.Video.Thumbnails.VIDEO_ID+"=?";
            String[] args = new String[]{id+""};
            Cursor cursor1 = context.getContentResolver().query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, thumbColumns, selection, args,null);
            if(cursor1.moveToFirst()){
                thumbnail = cursor1.getString(cursor1.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
            }

            if(duration>1000*limit ){//1 sec && isOk(pixel)
                videoFile.setId(id);
                videoFile.setName(name);
                videoFile.setDisplay(display);
                videoFile.setArtist(artist);
                videoFile.setPath(path);
                videoFile.setResolution(resolution);
                videoFile.setAlbum(album);
                videoFile.setDescription(description);
                videoFile.setThumbPath(thumbnail);
                videoFile.setSize(size);
                videoFile.setDate(date);
                videoFile.setDuration(duration);

                videos.add(videoFile);
            }
        }
        return videos;
    }

    /**
     * 获取本地视频相关信息
     * @param context
     * @param path
     * @return
     */
    public static VideoFile getVideoFileInfo(Context context, String path) {
        VideoFile videoFile = null;
        File file = new File(path);
        if(file.exists()){
            String displayName = path.substring(path.lastIndexOf('/')+1);
            String name = displayName.substring(0, displayName.lastIndexOf('.'));

            videoFile = new VideoFile();

            videoFile.setId(0);
            videoFile.setName(name);
            videoFile.setDisplay(displayName);
            videoFile.setArtist("UnKnown");
            videoFile.setPath(path);
            videoFile.setResolution(getVideoResolution(context, path, TYPE_FILE));
            videoFile.setAlbum(null);
            videoFile.setDescription(displayName);
            videoFile.setThumbPath(path);
            videoFile.setSize(file.length());
            videoFile.setDate(System.currentTimeMillis());
            videoFile.setDuration(getDuration(path));
        }
        return videoFile;
    }

    //======================================获取音频，视频播放时长====================================
    /**
     * 获取资源音频、视频时长
     * @param context
     * @param resId 资源文件id
     * @return
     */
    public static long getDuration(Context context, int resId){
        MediaPlayer mediaPlayer = MediaPlayer.create(context, resId);
        long duration = mediaPlayer.getDuration();
        releasePlayer(mediaPlayer);
        return  duration;
    }

    /**
     * 获取本地,在线 音频、视频时长
     * @param pathOrUrl 本地文件路径或url
     * @return
     */
    public static long getDuration(String pathOrUrl){
        MediaPlayer mediaPlayer = new MediaPlayer();
        long duration = -1;
        try {
            mediaPlayer.setDataSource(pathOrUrl);
            mediaPlayer.prepare();
            duration = mediaPlayer.getDuration();
        } catch (IOException e) {
            e.printStackTrace();
        }

        releasePlayer(mediaPlayer);
        return  duration;
    }

    private static void releasePlayer(MediaPlayer player){
        if(player!=null){
            //关键语句
            player.reset();
            player.release();
        }
    }

    //======================================获取缩略图===========================================
    /**
     * 获取视频第一帧作为缩略图
     * @param pathOrUrl 本地路径或url
     * @param type 1:本地视频  2:在线视频
     * 	retriever.getFrameAtTime(); //获取视频第一帧
     * 	retriever.getFrameAtTime(timeUs, option); //获取指定位置的原尺寸图片 注意这里传的timeUs是微秒
     * 	retriever.getScaledFrameAtTime(timeUs, MediaMetadataRetrieverCompat.OPTION_CLOSEST, width, height);//获取指定位置指定宽高的缩略图
     * 	retriever.getScaledFrameAtTime(timeUs, MediaMetadataRetrieverCompat.OPTION_CLOSEST, width, height, rotate);//获取指定位置指定宽高并且旋转的缩略图
     */
    public static Bitmap getVideoThumb(Context context, String pathOrUrl, int type) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        if(type==TYPE_FILE){
            retriever.setDataSource(context, Uri.fromFile(new File(pathOrUrl)));//本地视频
        }else if(type==TYPE_URL){
            retriever.setDataSource(pathOrUrl, new HashMap()); //网络视频
        }else {
            return null;
        }

        return retriever.getFrameAtTime();
    }


    /**
     * 获取视频的缩略图--可指定大小
     * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
     * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
     * @param videoPath 视频的路径
     * @param width 指定输出视频缩略图的宽度
     * @param height 指定输出视频缩略图的高度度
     * @param kind 参照MediaStore.Images(Video).Thumbnails类中的常量MINI_KIND和MICRO_KIND。
     *            其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
     * @return 指定大小的视频缩略图
     */
    public static Bitmap getVideoThumb(String videoPath, int width, int height, int kind) {
        //調用ThumbnailUtils類的靜態方法createVideoThumbnail獲取視頻的截圖；
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);

        if(bitmap!= null){
            //調用ThumbnailUtils類的靜態方法extractThumbnail將原圖片（即上方截取的圖片）轉化為指定大小；
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
        return bitmap;
    }

    public static Bitmap getVideoThumb(String videoPath, int kind) {
        //調用ThumbnailUtils類的靜態方法createVideoThumbnail獲取視頻的截圖；
        return ThumbnailUtils.createVideoThumbnail(videoPath, kind);
    }

    @RequiresApi(29)
    public static Bitmap getVideoThumb(File file, int width, int height) {
        Bitmap bitmap = null;
        try {
            bitmap = ThumbnailUtils.createVideoThumbnail(file, new Size(width, height), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @RequiresApi(29)
    public static Bitmap getImageThumb(File file, int width, int height) {
        Bitmap bitmap = null;
        try {
            bitmap = ThumbnailUtils.createImageThumbnail(file, new Size(width, height), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @RequiresApi(29)
    public static Bitmap getAudioThumb(File file, int width, int height) {
        Bitmap bitmap = null;
        try {
            bitmap = ThumbnailUtils.createAudioThumbnail(file, new Size(width, height), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    //========================================获取视频分辨率=========================================
    /**
     * 获取视频第一帧 -> bitmap; api>10,
     * @param context
     * @param path
     * @param type  1->file; 2->url
     * @return width * height
     */
    public static String getVideoResolution(Context context, String path, int type) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        if(type==TYPE_FILE){
            retriever.setDataSource(context, Uri.fromFile(new File(path)));
        }else if(type==TYPE_URL){
            retriever.setDataSource(path, new HashMap());
        }else{
            return "";
        }

        String width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH); // 视频宽度
        String height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT); // 视频高度
//        String rotation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION); // 视频旋转方向

        return width + " * " + height;
    }
}
