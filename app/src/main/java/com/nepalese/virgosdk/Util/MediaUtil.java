package com.nepalese.virgosdk.Util;

import android.content.ContentResolver;
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

import com.nepalese.virgosdk.Beans.AudioFile;
import com.nepalese.virgosdk.Beans.Song;
import com.nepalese.virgosdk.Beans.VideoFile;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MediaUtil {
    private static final String TAG = "MediaUtil";
    private static final int TYPE_FILE = 1;
    private static final int TYPE_URL = 2;

    //scan audio file
    public static List<AudioFile> getSongsList(Context context, int limit){
        List<AudioFile> songs = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

        for(int i=0; i<cursor.getCount(); i++){
            cursor.moveToNext();
            AudioFile songsInfo = new AudioFile();

            long id = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
            String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
            long albumId = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
            String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            int duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            Long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
            int isMusic = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.IS_MUSIC));

            if(isMusic!=0 && size>limit*1024*1024){
                songsInfo.setsId(id);
                songsInfo.setsName(title);
                songsInfo.setsDName(name);
                songsInfo.setsSinger(artist);
                songsInfo.setsAlbum(album);
                songsInfo.setsLogo(albumId);
                songsInfo.setsPath(path);
                songsInfo.setsLength(duration);
                songsInfo.setsSize(size);

                songs.add(songsInfo);
            }
        }
        return songs;
    }

    //get a song detail info
    public static AudioFile getMusicInformation(String path){
        AudioFile songsInfo = null;
        File file = new File(path);
        if(file.exists()){
            String displayName = path.substring(path.lastIndexOf('/')+1);
            String name = displayName.substring(0, displayName.lastIndexOf('.'));
            songsInfo = new AudioFile();

            MediaPlayer mediaPlayer = new MediaPlayer();
            int duration = 0;
            try {
                mediaPlayer.setDataSource(path);
                duration = mediaPlayer.getDuration();
            } catch (IOException e) {
                e.printStackTrace();
            }

            songsInfo.setsId(0);
            songsInfo.setsName(name);
            songsInfo.setsDName(displayName);
            songsInfo.setsSinger(name);
            songsInfo.setsPath(file.getAbsolutePath());
            songsInfo.setsAlbum(name);
            songsInfo.setsLogo(-1);
            songsInfo.setsLength(duration);//duration
            songsInfo.setsSize(file.length());
        }
        return songsInfo;
    }

    public static Song getMusic4File(File file) {
        if (file.length() == 0) return null;

        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(file.getAbsolutePath());

        final int duration;

        String keyDuration = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        // ensure the duration is a digit, otherwise return null song
        if (keyDuration == null || !keyDuration.matches("\\d+")) return null;
        duration = Integer.parseInt(keyDuration);

        final String title = extractMetadata(metadataRetriever, MediaMetadataRetriever.METADATA_KEY_TITLE, file.getName());
        final String displayName = extractMetadata(metadataRetriever, MediaMetadataRetriever.METADATA_KEY_TITLE, file.getName());
        final String artist = extractMetadata(metadataRetriever, MediaMetadataRetriever.METADATA_KEY_ARTIST, "unknown");
        final String album = extractMetadata(metadataRetriever, MediaMetadataRetriever.METADATA_KEY_ALBUM, "unknown");

        final Song song = new Song();
        song.setTitle(title);
        song.setDisplayName(displayName);
        song.setArtist(artist);
        song.setPath(file.getAbsolutePath());
        song.setAlbum(album);
        song.setDuration(duration);
        return song;
    }

    public static Bitmap parseAlbum(Context context, Song song, int defaultAlbum) {
        return parseAlbum(context, new File(song.getPath()), defaultAlbum);
    }

    public static Bitmap parseAlbum(Context context, File file, int defaultAlbum) {
        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        try {
            metadataRetriever.setDataSource(file.getAbsolutePath());
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "parseAlbum: ", e);
        }
        byte[] albumData = metadataRetriever.getEmbeddedPicture();
        if (albumData != null) {
            return BitmapFactory.decodeByteArray(albumData, 0, albumData.length);
        }
        return BitmapFactory.decodeResource(context.getResources(), defaultAlbum);
    }

    private static String extractMetadata(MediaMetadataRetriever retriever, int key, String defaultValue) {
        String value = retriever.extractMetadata(key);
        if (TextUtils.isEmpty(value)) {
            value = defaultValue;
        }
        return value;
    }

    /**
     * 通过内容提供器来获取图片缩略图
     缺点:必须更新媒体库才能看到最新的缩略图
     * @param context
     * @param Imagepath
     * @return
     */
    public static Bitmap getImageThumbnail(Context context, String Imagepath) {
        ContentResolver testcr = context.getContentResolver();
        String[] projection = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID, };
        String whereClause = MediaStore.Images.Media.DATA + " = '" + Imagepath + "'";
        Cursor cursor = testcr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, whereClause,null, null);
        int _id = 0;
        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }else if (cursor.moveToFirst()) {
            int _idColumn = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            do {
                _id = cursor.getInt(_idColumn);
            } while (cursor.moveToNext());
        }
        cursor.close();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(testcr, _id, MediaStore.Images.Thumbnails.MINI_KIND,options);
        return bitmap;
    }

    //get cover of audio file
    public static Bitmap getLogoFile(Context context, long songId, long logoId, int defaultAlbum){
        Bitmap bitmap = null;
        if(songId<0 && logoId<0){
            throw new IllegalArgumentException("Must specify an album or a song id!");
        }
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            FileDescriptor descriptor = null;
            if(logoId<=0){
                //uri = Uri.parse("content://media/external/audio/media/"+songId+"/albumart");
                bitmap = BitmapFactory.decodeResource(context.getResources(), defaultAlbum);

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
                //options.outHeight =  options.outWidth;

                bitmap = BitmapFactory.decodeFileDescriptor(descriptor,null, options);
            }
            /*
            options.inSampleSize = 1;
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor(descriptor, null, options);
            */
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    //scan video file
    public static List<VideoFile> getVideoList(Context context, int limit){
        List<VideoFile> videos = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null,null,null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);

        String[] thumbColumns = new String[]{
                MediaStore.Video.Thumbnails.DATA, MediaStore.Video.Thumbnails.VIDEO_ID};

        for(int i=0; i<cursor.getCount(); i++) {
            cursor.moveToNext();
            VideoFile videoFile = new VideoFile();

            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media._ID));
            String name = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
            String display = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
            String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.ARTIST));
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
            String pixel = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.RESOLUTION));
            String album = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.ALBUM));
            String description = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DESCRIPTION));
            long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
            long date = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_TAKEN));
            long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
            String thumbpath = null;

            String selection = MediaStore.Video.Thumbnails.VIDEO_ID+"=?";
            String[] args = new String[]{id+""};
            Cursor cursor1 = context.getContentResolver().query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, thumbColumns, selection, args,null);
            if(cursor1.moveToFirst()){
                thumbpath = cursor1.getString(cursor1.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
            }

            if(duration>1000*60*limit ){//1 min && isOk(pixel)
                videoFile.setId(id);
                videoFile.setName(name);
                videoFile.setDisplay(display);
                videoFile.setArtist(artist);
                videoFile.setPath(path);
                videoFile.setPixel(pixel);
                videoFile.setAlbum(album);
                videoFile.setDescription(description);
                videoFile.setThumbPath(thumbpath);
                videoFile.setSize(size);
                videoFile.setDate(date);
                videoFile.setDuration(duration);

                videos.add(videoFile);
            }
        }
        return videos;
    }

    public static VideoFile getVideoFileInformation(String path) {
        VideoFile videoFile = null;
        File file = new File(path);
        if(file.exists()){
            String displayName = path.substring(path.lastIndexOf('/')+1);
            String name = displayName.substring(0, displayName.lastIndexOf('.'));

            videoFile = new VideoFile();

            MediaPlayer mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(file.getPath());
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            long duration = mediaPlayer.getDuration();

            videoFile.setId(0);
            videoFile.setName(name);
            videoFile.setDisplay(displayName);
            videoFile.setArtist("UnKnown");
            videoFile.setPath(path);
            videoFile.setPixel("0");
            videoFile.setAlbum(null);
            videoFile.setDescription("Null");
            videoFile.setThumbPath(path);
            videoFile.setSize(file.length());
            videoFile.setDate(System.currentTimeMillis());
            videoFile.setDuration(duration);
        }
        return videoFile;
    }

    //①raw下的资源：
    // MediaPlayer.create(this, R.raw.test);
    // ②本地文件路径：
    // mp.setDataSource("/sdcard/test.mp3");
    // ③网络URL文件：
    // mp.setDataSource("http://www.xxx.com/music/test.mp3");
    public static long getDuration(Context context, int i){
        MediaPlayer mediaPlayer = MediaPlayer.create(context, i);
        return  mediaPlayer.getDuration();
    }

    /**
     * 获取视频第一帧作为缩略图
     * @param path
     * @param type 本地、在线视频
     */
    public static Bitmap getVideoThumb(Context context, String path, int type) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        if(type==TYPE_FILE){
            retriever.setDataSource(context, Uri.fromFile(new File(path)));//本地视频
        }else if(type==TYPE_URL){
            retriever.setDataSource(path, new HashMap()); //网络视频
        }else {
            return null;
        }

        return retriever.getFrameAtTime();
    }

    /**
     * 获取视频的缩略图
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
        Bitmap bitmap;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind); //調用ThumbnailUtils類的靜態方法createVideoThumbnail獲取視頻的截圖；
        if(bitmap!= null){
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);//調用ThumbnailUtils類的靜態方法extractThumbnail將原圖片（即上方截取的圖片）轉化為指定大小；
        }
        return bitmap;
    }
}
