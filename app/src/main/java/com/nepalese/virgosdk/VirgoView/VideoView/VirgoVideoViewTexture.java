package com.nepalese.virgosdk.VirgoView.VideoView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.rtp.AudioStream;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.MediaController;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author nepalese on 2020/9/18 08:59
 * @usage 自定义视频播放器
 * ：使用texture，可旋转，移动， 显示大小根据控件大小自适应
 * ：无法播放高分辨率视频（1k以上）, 可能与设备最大支持分辨率有关
 */
public class VirgoVideoViewTexture extends TextureView implements MediaController.MediaPlayerControl {
    private static final String TAG = "VirgoVideoViewTexture";

    //播放器当前状态
    private static final int STATE_ERROR = -1;//报错
    private static final int STATE_IDLE = 0;//初始化
    private static final int STATE_PREPARING = 1;//准备
    private static final int STATE_PREPARED = 2;//已设定播放源
    private static final int STATE_PLAYING = 3;//正在播放
    private static final int STATE_PAUSED = 4;//暂停
    private static final int STATE_PLAYBACK_COMPLETED = 5;//播放完

    //监听
    private MediaPlayer.OnCompletionListener completionListener;//完成监听
    private MediaPlayer.OnPreparedListener preparedListener;//准备监听
    private MediaPlayer.OnErrorListener errorListener;//错误监听
    private MediaPlayer.OnInfoListener infoListener;//信息监听 出现了信息或者警告的时候回调;
    private MediaPlayer.OnVideoSizeChangedListener sizeChangedListener;//分辨率监听
    private SurfaceTextureListener textureListener;//回调监听
    private int curBufferPercentage;//当前视频缓冲百分比

    private Context context;
    private Uri videoUri;//视频源
    private Map<String, String> header;//用于在线播放
    private MediaPlayer mediaPlayer;
    private SurfaceTexture surfaceTexture;//画面
    private AudioManager audioManager;//音量控制服务
    //todo 控制器， 音频
    private MediaController mediaController;//系统默认视频控制器
    private int audioFocusType = AudioManager.AUDIOFOCUS_GAIN;
    /**
     * AUDIOFOCUS_GAIN:你已获得了音频焦点．
     * AUDIOFOCUS_LOSS:你已经丢失了音频焦点比较长的时间了．你必须停止所有的音频播放．因为预料到你可能很长时间也不能再获音频焦点，所以这里是清理你的资源的好地方．比如，你必须释放MediaPlayer．
     * AUDIOFOCUS_LOSS_TRANSIENT:你临时性的丢掉了音频焦点，很快就会重新获得．你必须停止所有的音频播放，但是可以保留你的资源，因为你可能很快就能重新获得焦点．
     * AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:你临时性的丢掉了音频焦点，但是你被允许继续以低音量播放，而不是完全停止．
     */

    private int videoWidth, videoHeight;//视频分辨率

    private int curPosition;//播放状态下当前位置
    private int seekWhenPrepared;//准备状态下的
    private float volume;//音量

    //有初始化变量
    private int curState = STATE_IDLE;//当前状态
    private int aimState = STATE_IDLE;//期望状态
    private boolean isLooping = false;//循环播放

    public VirgoVideoViewTexture(Context context) {
        this(context, null);
    }

    public VirgoVideoViewTexture(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VirgoVideoViewTexture(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        videoWidth = 0;
        videoHeight = 0;
        volume = -1;

        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if(audioManager!=null){
            audioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }
        setSurfaceTextureListener(onTextureListener);

        //设置焦点
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();

        curState = STATE_IDLE;
        aimState = STATE_IDLE;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(videoWidth, widthMeasureSpec);
        int height = getDefaultSize(videoHeight, heightMeasureSpec);

        if (videoWidth > 0 && videoHeight > 0) {
            int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
            int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
            int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

            if (widthSpecMode == MeasureSpec.EXACTLY && heightSpecMode == MeasureSpec.EXACTLY) {
                // the size is fixed
                width = widthSpecSize;
                height = heightSpecSize;

                // for compatibility, we adjust size based on aspect ratio
                if ( videoWidth * height  < width * videoHeight ) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * videoWidth / videoHeight;
                } else if ( videoWidth * height  > width * videoHeight ) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * videoHeight / videoWidth;
                }
            } else if (widthSpecMode == MeasureSpec.EXACTLY) {
                // only the width is fixed, adjust the height to match aspect ratio if possible
                width = widthSpecSize;
                height = width * videoHeight / videoWidth;
                if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                    // couldn't match aspect ratio within the constraints
                    height = heightSpecSize;
                }
            } else if (heightSpecMode == MeasureSpec.EXACTLY) {
                // only the height is fixed, adjust the width to match aspect ratio if possible
                height = heightSpecSize;
                width = height * videoWidth / videoHeight;
                if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                    // couldn't match aspect ratio within the constraints
                    width = widthSpecSize;
                }
            } else {
                // neither the width nor the height are fixed, try to use actual video size
                width = videoWidth;
                height = videoHeight;
                if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                    // too tall, decrease both width and height
                    height = heightSpecSize;
                    width = height * videoWidth / videoHeight;
                }
                if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                    // too wide, decrease both width and height
                    width = widthSpecSize;
                    height = width * videoHeight / videoWidth;
                }
            }
        }  // no size yet, just adopt the given spec sizes

        Log.d(TAG, "onMeasure after: width = " + width + "\theight = " + height);
        setMeasuredDimension(width, height);
    }

    //===========================================get/set============================================
    public void setCompletionListener(MediaPlayer.OnCompletionListener completionListener) {
        this.completionListener = completionListener;
    }

    public void setPreparedListener(MediaPlayer.OnPreparedListener preparedListener) {
        this.preparedListener = preparedListener;
    }

    public void setErrorListener(MediaPlayer.OnErrorListener errorListener) {
        this.errorListener = errorListener;
    }

    public void setInfoListener(MediaPlayer.OnInfoListener infoListener) {
        this.infoListener = infoListener;
    }

    public void setSizeChangedListener(MediaPlayer.OnVideoSizeChangedListener sizeChangedListener) {
        this.sizeChangedListener = sizeChangedListener;
    }

    public void setTextureListener(SurfaceTextureListener textureListener) {
        this.textureListener = textureListener;
    }

    /**
     * @param path 文件路径或url
     */
    public void setVideoPath(String path) {
        setVideoUri(Uri.parse(path));
    }

    public void setVideoFile(File file) {
        setVideoUri(Uri.fromFile(file));
    }

    public void setVideoUri(Uri videoUri) {
        setVideoUri(videoUri, (Map)null);
    }

    private void setVideoUri(Uri videoUri, Map<String, String> headers) {
        this.videoUri = videoUri;
        this.header = headers;
        seekWhenPrepared = 0;
        openVideo();
        requestLayout();
        invalidate();
    }

    public void setLooping(boolean looping) {
        isLooping = looping;
    }

    public boolean isLooping() {
        return isLooping;
    }

    public void stopPlay() {
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            curState = STATE_IDLE;
            aimState = STATE_IDLE;
            audioManager.abandonAudioFocus(null);
        }
    }

    public void continuePlay(){
        if(curState == STATE_PAUSED){
            mediaPlayer.start();
            mediaPlayer.seekTo(curPosition);
            curState = STATE_PLAYING;
        }
    }

    //系统静音
    public void setMuteAudio(boolean isOn){
        if(audioManager!=null){
            if(isOn){
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
            }else{
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
            }
        }
    }

    public void setMutePlayer(boolean isOn){
        Log.d(TAG, "setMute: " + isOn);
        if(isOn){
            setVolume(0f);
        }else{
            if(audioManager!=null){
                setVolume(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));//当前系统音量
            }
        }
    }

    //设置音量
    public void setVolume(float volume) {
        if(volume>=0){
            this.volume = volume;
        }
        if (mediaPlayer!=null) {
            Log.d(TAG, "setVolume: " + volume);
            this.mediaPlayer.setVolume(volume, volume);
        }
    }

    //设置音频焦点类型
    public void setAudioFocusRequest(int focusGain) {
        if (focusGain != AudioManager.AUDIOFOCUS_NONE
                && focusGain != AudioManager.AUDIOFOCUS_GAIN
                && focusGain != AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
                && focusGain != AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
                && focusGain != AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE) {
            throw new IllegalArgumentException("Illegal audio focus type " + focusGain);
        }
        audioFocusType = focusGain;
    }

    //设置视频控制器
    public void setMediaController(MediaController mediaController) {
        if(mediaController!=null){
            mediaController.hide();
        }
        this.mediaController = mediaController;
        attachMediaController();
    }

    //========================================================private===============================================
    //增加视频控制器
    private void attachMediaController() {
        if (mediaPlayer != null && mediaController != null) {
            mediaController.setMediaPlayer(this);
            View anchorView = this.getParent() instanceof View ?
                    (View) this.getParent() : this;
            mediaController.setAnchorView(anchorView);
            mediaController.setEnabled(isInPlaybackState());
        }
    }

    private void setControllerVisibility() {
        if (mediaController.isShowing()) {
            mediaController.hide();
        } else {
            mediaController.show();
        }
    }

    // @return 是否处于可播放状态
    private boolean isInPlaybackState() {
        return this.mediaPlayer!= null && this.curState != STATE_ERROR && this.curState != STATE_IDLE && this.curState != STATE_PREPARING;
    }

    // 初始化player, 设立监听
    private void openVideo() {
        if (videoUri == null || surfaceTexture == null) {
            // not ready for playback just yet, will try again later
            return;
        }
        release();

        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnPreparedListener(onPreparedListener);
            mediaPlayer.setOnCompletionListener(onCompletionListener);
            mediaPlayer.setOnVideoSizeChangedListener(onVideoSizeChangedListener);
            mediaPlayer.setOnErrorListener(onErrorListener);
            mediaPlayer.setOnInfoListener(onInfoListener);
            mediaPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);

            curBufferPercentage = 0;
            mediaPlayer.setDataSource(context, videoUri, header);
            mediaPlayer.setSurface(new Surface(surfaceTexture));
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setScreenOnWhilePlaying(true);
            mediaPlayer.prepareAsync();

            onInfoListener.onInfo(
                    mediaPlayer, MediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE, 0);
            curState = STATE_PREPARING;
            //增加控制器
            attachMediaController();
        }catch (IOException e) {
            e.printStackTrace();
            curState = STATE_ERROR;
            errorListener.onError(mediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
        }

        curState = STATE_PREPARING;
    }

    private void release(){
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;

            //重置状态
            curState = STATE_IDLE;
            aimState = STATE_IDLE;

            //取消音频焦点
            if (audioFocusType != AudioManager.AUDIOFOCUS_NONE) {
                audioManager.abandonAudioFocus(null);
            }
        }
    }

    //============================================override MediaPlayerControl==========================================
    @Override
    public void start() {
        if(isInPlaybackState()){
            mediaPlayer.start();
            mediaPlayer.setLooping(isLooping);
            curState = STATE_PLAYING;
        }
        aimState = STATE_PLAYING;
    }

    @Override
    public void pause() {
        if(curState==STATE_PLAYING){
            mediaPlayer.pause();
            curState = STATE_PAUSED;
            curPosition = mediaPlayer.getCurrentPosition();
        }
        aimState = STATE_PAUSED;
    }

    @Override
    public int getDuration() {
        if (isInPlaybackState()) {
            return mediaPlayer.getDuration();
        }

        return -1;
    }

    @Override
    public int getCurrentPosition() {
        if (isInPlaybackState()) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public void seekTo(int pos) {
        if (isInPlaybackState()) {
            mediaPlayer.seekTo(pos);
            seekWhenPrepared = 0;
        } else {
            seekWhenPrepared = pos;
        }
    }

    @Override
    public boolean isPlaying() {
        return isInPlaybackState() && mediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        if(mediaPlayer!=null){
            return curBufferPercentage;
        }

        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean isKeyCodeSupported = keyCode != KeyEvent.KEYCODE_BACK &&
                keyCode != KeyEvent.KEYCODE_VOLUME_UP &&
                keyCode != KeyEvent.KEYCODE_VOLUME_DOWN &&
                keyCode != KeyEvent.KEYCODE_VOLUME_MUTE &&
                keyCode != KeyEvent.KEYCODE_MENU &&
                keyCode != KeyEvent.KEYCODE_CALL &&
                keyCode != KeyEvent.KEYCODE_ENDCALL;
        if (isInPlaybackState() && isKeyCodeSupported) {
            if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK ||
                    keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
                if (mediaPlayer.isPlaying()) {
                    pause();
                    mediaController.show();
                } else {
                    start();
                    mediaController.hide();
                }
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
                if (!mediaPlayer.isPlaying()) {
                    start();
                    mediaController.hide();
                }
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP
                    || keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE) {
                if (mediaPlayer.isPlaying()) {
                    pause();
                    mediaController.show();
                }
                return true;
            } else {
                setControllerVisibility();
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    //监听点击事件控制视频控制器显隐
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN
                && isInPlaybackState() && mediaController != null) {
            setControllerVisibility();
        }
        return super.onTouchEvent(event);
    }

    //处理轨迹球的动作事件，轨迹球相对运动的最后一个事件能用MotionEvent.getX() 和MotionEvent.getY()函数获取。
    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN
                && isInPlaybackState() && mediaController != null) {
            setControllerVisibility();
        }
        return super.onTrackballEvent(ev);
    }

    //============================================default===========================================
    //下载缓冲视频流的时候回调, 用以改变视频缓冲状态;
    private MediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mediaPlayer, int percent) {
            curBufferPercentage = percent;
        }
    };

    private MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            curState = STATE_PREPARED;

            if (volume != -1) {
                setVolume(volume);
            }
            if (preparedListener != null) {
                preparedListener.onPrepared(mediaPlayer);
            }

            //todo 增加控制器
            if (mediaController != null) {
                mediaController.setEnabled(true);
            }

            videoWidth = mp.getVideoWidth();
            videoHeight = mp.getVideoHeight();

            int seekToPosition = seekWhenPrepared;  // mSeekWhenPrepared may be changed after seekTo() call
            if (seekToPosition != 0) {
                seekTo(seekToPosition);
            }

            if (videoWidth != 0 && videoHeight != 0) {
                if (aimState == STATE_PLAYING) {
                    start();
                    if (mediaController != null) {
                        mediaController.show();
                    } else if (!isPlaying() && (seekToPosition != 0 || getCurrentPosition() > 0)) {
                        if (mediaController != null) {
                            // Show the media controls when we're paused into a video and make 'em stick.
                            mediaController.show(0);
                        }
                    }
                }
            } else {
                // We don't know the video size yet, but should start anyway.
                // The video size might be reported to us later.
                if (aimState == STATE_PLAYING) {
                    start();
                }
            }
        }
    };

    private MediaPlayer.OnInfoListener onInfoListener = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mediaPlayer, int arg1, int arg2) {
            if (infoListener != null) {
                infoListener.onInfo(mediaPlayer, arg1, arg2);
            }
            return true;
        }
    };

    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            curState = STATE_PLAYBACK_COMPLETED;
            aimState = STATE_PLAYBACK_COMPLETED;

            if (mediaController != null) {
                mediaController.hide();
            }

            if (completionListener != null) {
                completionListener.onCompletion(mediaPlayer);
            }

            //取消音频焦点
            if (audioFocusType != AudioManager.AUDIOFOCUS_NONE) {
                audioManager.abandonAudioFocus(null);
            }
        }
    };

    private MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() {
        public boolean onError(MediaPlayer mp, int framework_err, int impl_err) {
            curPosition = STATE_ERROR;
            aimState = STATE_ERROR;

            if (mediaController != null) {
                mediaController.hide();
            }

            /* If an error handler has been supplied, use it and finish. */
            if (errorListener != null) {
                if (errorListener.onError(mediaPlayer, framework_err, impl_err)) {
                    return true;
                }
            }

            //todo 增加弹框
            if (getWindowToken() != null) {
                String msg;
                if (framework_err == MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK) {
                    msg = "This video isn\'t valid for streaming to this device.";
                } else {
                    msg = "Can\'t play this video.";
                }

                new AlertDialog.Builder(getContext())
                        .setMessage(msg)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (completionListener != null) {
                                    completionListener.onCompletion(mediaPlayer);
                                }
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
            return true;
        }
    };

    //视频分辨率, 两个值一样mp.getVideoWidth() = width；
    private MediaPlayer.OnVideoSizeChangedListener onVideoSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            Log.d(TAG, "onVideoSizeChanged: width = " + width + "\theight = " + height);

            videoWidth = mp.getVideoWidth();
            videoHeight = mp.getVideoHeight();

            if(sizeChangedListener!=null){
                sizeChangedListener.onVideoSizeChanged(mediaPlayer, width, height);
            }
        }
    };

    //设置surfaceTexture显示窗口大小
    //根据视频分辨率自动调整视频显示大小
    private void setFixedSize(int w, int h){
        ViewGroup.LayoutParams lp = this.getLayoutParams();

        lp.width = w;
        lp.height = h;
        this.setLayoutParams(lp);

        // 改变surface内容尺寸
        this.getSurfaceTextureListener().onSurfaceTextureSizeChanged(
                this.getSurfaceTexture(),
                this.getLayoutParams().width,
                this.getLayoutParams().height);
    }

    //仅在初始控件时调用一次；
    private SurfaceTextureListener onTextureListener = new SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(@NonNull SurfaceTexture s, int w, int h) {
            surfaceTexture = s;
            if(textureListener!=null){
                textureListener.onSurfaceTextureAvailable(s, w, h);
            }
            openVideo();
        }

        @Override
        public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int width, int height) {
            Log.d(TAG, "surfaceChanged: width = " + width + "\theight = " +  height);

            boolean isValidState = (aimState == STATE_PLAYING);
            if (mediaPlayer != null && isValidState) {
                if (seekWhenPrepared != 0) {
                    seekTo(seekWhenPrepared);
                }
                start();
            }
        }

        @Override
        public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture s) {
            surfaceTexture = null;
            if(textureListener!=null){
                textureListener.onSurfaceTextureDestroyed(s);
            }
            release();
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(@NonNull SurfaceTexture s) {

        }
    };

    //音频焦点得失监听
    private AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = focusChange -> {
        if(focusChange == AudioManager.AUDIOFOCUS_LOSS){
            //失去焦点之后的操作
            if(isPlaying()){
                pause();
            }
        }else if(focusChange == AudioManager.AUDIOFOCUS_GAIN){
            //获得焦点之后的操作
            if(curState==STATE_PAUSED){
                start();
            }
        }
    };
}
