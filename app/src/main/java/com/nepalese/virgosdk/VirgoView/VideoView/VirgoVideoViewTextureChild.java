package com.nepalese.virgosdk.VirgoView.VideoView;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.List;

/**
 * @author nepalese on 2020/9/18 11:11
 * @usage
 */
public class VirgoVideoViewTextureChild extends VirgoVideoViewTexture {
    private static final String TAG = "VirgoVideoTextureChild";

    private Context context;

    private List<String> url = null;
    private int currentIndex = 0;
    private boolean hasSetUrl = false;
    private boolean hasPause = false;

    public VirgoVideoViewTextureChild(Context context) {
        this(context, null);
    }

    public VirgoVideoViewTextureChild(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public VirgoVideoViewTextureChild(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init(){
        setLooping(false);
        setCompletionListener(mediaPlayer -> {
            Log.d(TAG, "onCompletion: complete");
            load();
        });

        setErrorListener((mediaPlayer, i, i1) -> {
            load();
            return true;
        });

        //设置系统默认的视频控制器（时间条，播放、暂停，前进、后退）
//        setMediaController(new MediaController(context));
    }

    public VirgoVideoViewTextureChild setUrl(List<String> urls) {
        if (urls != null && !urls.isEmpty()) {
            url = urls;
            hasSetUrl = true;
            currentIndex = 0;
        }
        return this;
    }

    public void play() {
        if (!hasSetUrl || url == null || url.isEmpty()) {
            return;
        }

        load();
        hasPause = false;
    }

    private void load() {
        if (url == null || url.isEmpty()) return;
        if (currentIndex >= url.size()) {
            currentIndex = 0;
        }

        String path = url.get(currentIndex);
        File file = new File(path);

        if (file.exists()) {
            Log.i(TAG, "播放本地视频");
            setVideoUri(Uri.fromFile(file));
        } else {
            Log.i(TAG, "播放在线视频");
            setVideoPath(path);
        }
        start();
        currentIndex++;
    }

    //==============================================================================================
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopPlay();
        clearFocus();
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == GONE && isPlaying()) {
            hasPause = true;
            pause();
        } else {
            if (hasPause) {
                start();
            }
        }
    }
}
