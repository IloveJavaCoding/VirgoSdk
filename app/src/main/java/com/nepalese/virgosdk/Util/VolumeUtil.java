package com.nepalese.virgosdk.Util;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

/**
 * @author nepalese on 2020/10/18 15:43
 * @usage 系统媒体音量控制： 音量获取、设置，静音开关，音量加减
 */
public class VolumeUtil {
    private static final String TAG = "VolumeUtil";

    //设置媒体音量 刻度100
    public static void setVolume(Context context, float value){
        AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (manager!=null){
            if(value>100){
                value = 100;
            }else if(value<0){
                value = 0;
            }
            int maxVolume = manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int volume = (int) (value/ 100 * maxVolume);
            manager.setStreamVolume(AudioManager.STREAM_MUSIC,volume,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        }else {
            Log.w(TAG, "AudioManager == null");
        }
    }

    //开启静音模式
    public static void muteSystem(Context context){
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager!=null){
            audioManager.setStreamMute(AudioManager.STREAM_MUSIC,true);
        }
    }

    //关闭静音模式
    public static void unMuteSystem(Context context){
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager!=null){
            audioManager.setStreamMute(AudioManager.STREAM_MUSIC,false);
        }
    }

    //获取当前媒体音量
    public static int getCurrentVolume(Context context){
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager!=null){
            return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        }
        return -1;
    }

    //音量增加
    public static void volumeUp(Context context){
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager!=null){
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_RAISE,
                    AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        }
    }

    //音量降低
    public static void volumeDown(Context context){
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager!=null){
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_LOWER,
                    AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        }
    }

    //自动调整音量
    public static void bootResetVolume(Context context) {
        AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (manager != null) {
            //当前音量
            int volume = manager.getStreamVolume(AudioManager.STREAM_MUSIC);
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //与最大音量比较
            if (volume != manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) {
                //向音量增加方向调整
                manager.adjustStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_RAISE,
                        AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                manager.adjustStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_LOWER,
                        AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            } else {
                manager.adjustStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_LOWER,
                        AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                manager.adjustStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_RAISE,
                        AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            }
        }
    }
}
