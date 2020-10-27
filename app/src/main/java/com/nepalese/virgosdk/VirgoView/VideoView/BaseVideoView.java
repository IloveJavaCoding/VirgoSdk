package com.nepalese.virgosdk.VirgoView.VideoView;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.VideoView;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.List;

/**
 * @author nepalese on 2020/9/24 09:46
 * @usage 继承系统的videoView 用于主屏独立播放视频
 */
public class BaseVideoView extends VideoView {
    private static final String TAG = "BaseVideoView";

    private Context mCtx;
    private List<String> mUrls = null;

    private int mCurrentIndex = 0;
    private boolean mHasSetUrl = false;
    private boolean hasPause = false;

    public BaseVideoView(Context context) {
        this(context, null);
    }

    public BaseVideoView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BaseVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mCtx = context;

        setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.i(TAG, "onComplete");
                load();
            }
        });
    }

    public BaseVideoView setUrl(List<String> urls) {
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
        load();
        mHasSetUrl = false;
    }

    private void load() {
        if (mUrls == null || mUrls.isEmpty()) return;
        Log.i(TAG, "current index: "+ mCurrentIndex + ", list size:" + mUrls.size());
        if (mCurrentIndex >= mUrls.size()) {
            mCurrentIndex = 0;
        }

        String path = mUrls.get(mCurrentIndex);
        File file = new File(path);

        mCurrentIndex++;
        if (file.exists()) {
            play(Uri.fromFile(file));
        } else {
            play(Uri.parse(path));
        }
    }

    private void play(Uri uri) {
        setVideoURI(uri);
        Log.i(TAG, "play: " + uri.getPath());
        start();
    }

    private void release() {
       stopPlayback();
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        release();
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
