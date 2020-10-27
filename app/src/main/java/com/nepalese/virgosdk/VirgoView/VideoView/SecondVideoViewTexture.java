package com.nepalese.virgosdk.VirgoView.VideoView;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * 副屏播放器
 * 1. 无法使用surfaceView
 * 2. 无法播放分辨率过高视频：已知4k无法播放
 */
public class SecondVideoViewTexture extends TextureView {
    private static final String TAG = "SECOND_VIDEO_VIEW";

    private static final int TYPE_FILE = 0;//本地文件
    private static final int TYPE_URL = 1;//网络视频

    private Context mCtx;
    private MediaPlayer mediaPlayer;
    private SurfaceTexture mSurface = null;
    private List<String> mUrls = null;

    private int mCurrentIndex = 0;
    private boolean mHasSetUrl = false;
    private boolean hasPause = false;

    public SecondVideoViewTexture(Context context) {
        this(context, null);
    }

    public SecondVideoViewTexture(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SecondVideoViewTexture(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mCtx = context;
        mediaPlayer = new MediaPlayer();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.i(TAG, "onComplete");
                load();
            }
        });

        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.d(TAG, "onError  " + what + " extra = " + extra);
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
                mediaPlayer = new MediaPlayer();
                return false;
            }
        });

        setSurfaceTextureListener(new SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                mSurface = surface;
                load();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {}

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });
    }

    public SecondVideoViewTexture setUrl(List<String> urls) {
        if (urls != null && !urls.isEmpty()) {
            mUrls = urls;
            mHasSetUrl = true;
            mCurrentIndex = 0;
        }
        return this;
    }

    public void play() {
        if (!mHasSetUrl || mUrls == null || mUrls.isEmpty()) {
            return;
        }
        Log.i(TAG, "play: 开始播放！");
        load();
        mHasSetUrl = false;
    }

    public boolean isPlaying(){
        if(mediaPlayer!=null){
            return mediaPlayer.isPlaying();
        }

        return false;
    }

    private void load() {
        if (mUrls == null || mUrls.isEmpty() || mSurface==null) return;
        Log.i(TAG, "current index: "+ mCurrentIndex + ", list size:" + mUrls.size());
        if (mCurrentIndex >= mUrls.size()) {
            mCurrentIndex = 0;
        }
        String path = mUrls.get(mCurrentIndex);
        File file = new File(path);

        mCurrentIndex++;
        if (file.exists()) {
            //播放本地视频
            play(file.getPath(), TYPE_FILE);
        } else {
            //本地文件不存在, 播放在线视频；增加url有效性判断
            play(path, TYPE_URL);
        }
    }

    private void play(String path, int type) {
        //先判断视频分辨率
        if(judgeVideoSize(path, type)){
            if (mediaPlayer != null && mSurface != null) {
                mediaPlayer.reset();
                try {
                    if(type==TYPE_FILE){
                        //播放本地视频文件
                        mediaPlayer.setDataSource(path);
                    }else{
                        //播放网络视频
                        mediaPlayer.setDataSource(mCtx, Uri.parse(path));
                    }
                    Log.i(TAG, "file path: " + path);//播放在线视频是为视频缓存位置
                    mediaPlayer.setSurface(new Surface(mSurface));
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    Log.e(TAG, "play: 视频解析出错，播放下一个！ ", e);
                    load();
                }
            }else {
                Log.i(TAG, "play: mediaPlayer == null || mSurface == null");
            }
        }else{
            Log.e(TAG, "play: 视频分辨率过高无法播放！");
            load();
        }
    }

    public void pause(){
        if(mediaPlayer!=null && mediaPlayer.isPlaying()){
            hasPause = true;
            mediaPlayer.pause();
        }
    }

    public void continuePlay(){
        if(mediaPlayer!=null && hasPause){
            hasPause = false;
            mediaPlayer.start();
        }
    }

    public boolean isHasPause() {
        return hasPause;
    }

    public void stop(){
        hasPause = false;
        mediaPlayer.stop();
    }

    private void release() {
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        release();
        clearFocus();
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == GONE && mediaPlayer.isPlaying()) {
            hasPause = true;
            mediaPlayer.pause();
        } else {
            if (hasPause) {
                mediaPlayer.start();
            }
        }
    }

    /**
     * 获取视频第一帧 -> bitmap; api>10,
     * 对于分辨率大于1920*1080的视频跳过
     * true: 正常， false: 超出 有问题
     * type: 0->file; 1->url
     */
    private boolean judgeVideoSize(String path, int type) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        if(type==TYPE_FILE){
            //1. 本地文件
            retriever.setDataSource(mCtx, Uri.fromFile(new File(path)));
        }else if(type==TYPE_URL){
            //2. 网络文件
            retriever.setDataSource(path, new HashMap());
        }else {
            return false;
        }

        String height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT); // 视频高度
        String width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH); // 视频宽度
        Log.i(TAG, "VideoSize: w: " + width + "\th: " + height);

        if(Integer.parseInt(width)>1921 || Integer.parseInt(height)>1081){
            return false;
        }
        return true;
    }
}
