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
public class VideoViewTexture extends TextureView {
    private static final String TAG = "VideoViewTexture";

    private static final int TYPE_FILE = 0;//本地文件
    private static final int TYPE_URL = 1;//网络视频

    private Context mCtx;
    private MediaPlayer mediaPlayer;
    private SurfaceTexture mSurface = null;
    private List<String> mUrls = null;

    private int mCurrentIndex = 0;
    private boolean mHasSetUrl = false;
    private boolean hasPause = false;

    public VideoViewTexture(Context context) {
        this(context, null);
    }

    public VideoViewTexture(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoViewTexture(Context context, AttributeSet attrs, int defStyleAttr) {
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

    public VideoViewTexture setUrl(List<String> urls) {
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
}
