package com.nepalese.virgosdk.Util;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.net.Uri;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;

/**
 *  @author nepalese on 2021/06/24 17:20
 *  @usage Glide引用库常用接口打包
 *  Lib：implementation 'com.github.bumptech.glide:glide:4.9.0'
 */
public class GlideUtil {
    private static final RequestOptions requestOptions;
    private static final DrawableTransitionOptions transitionOptions;

    static {
        requestOptions = new RequestOptions().skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        transitionOptions = new DrawableTransitionOptions().dontTransition();
    }

    /**
     * 加载图片到ImageView， 支持gif
     * @param path ：图片文件地址或网络链接
     * @param imageView
     */
    public static void loagImage(String path, ImageView imageView) {
        if (imageView == null || imageView.getContext() == null || path == null) return;
        if (path.endsWith(".gif")) {
            try {
                Glide.with(imageView.getContext())
                        .asGif()
                        .load(path)
                        .transition(transitionOptions)
                        .into(imageView);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } else {
            try {
                Glide.with(imageView.getContext())
                        .load(path)
                        .apply(requestOptions)
                        .transition(transitionOptions)
                        .into(imageView);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 加载图片到ImageView， 支持gif
     * @param file 图片文件
     * @param imageView
     */
    public static void loadImage(File file, ImageView imageView) {
        if (imageView == null || file == null) return;
        if(file.exists()){
            loagImage(file.getAbsolutePath(), imageView);
        }
    }

    /**
     * 加载图片到ImageView
     * @param uri
     * @param imageView
     */
    public static void loadImage(Uri uri, ImageView imageView) {
        if (imageView == null || uri == null) return;
        try {
            Glide.with(imageView.getContext())
                    .load(uri)
                    .transition(transitionOptions)
                    .into(imageView);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 旋转 ImageView
     * @param imgView
     * @param duration 转一圈时长
     * @return
     *
     * public void startRotateAnimation() {
     *      mRotateAnimator.cancel();
     *      mRotateAnimator.start();
     * }
     *
     * public void cancelRotateAnimation() {
     *      mLastAnimationValue = 0;
     *      mRotateAnimator.cancel();
     * }
     *
     * public void pauseRotateAnimation() {
     *      mLastAnimationValue = mRotateAnimator.getCurrentPlayTime();
     *      mRotateAnimator.cancel();
     * }
     *
     * public void resumeRotateAnimation() {
     *      mRotateAnimator.start();
     *      mRotateAnimator.setCurrentPlayTime(mLastAnimationValue);
     * }
     *
     */
    public static ObjectAnimator rotateImageView(ImageView imgView, int duration){
        ObjectAnimator animator = ObjectAnimator.ofFloat(imgView, "rotation", 0f,360f);
        animator.setDuration(duration);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setRepeatCount(ValueAnimator.INFINITE);

        return animator;
    }


    @GlideModule
    public static class GeneratedAppGlideModule extends AppGlideModule {
        @Override
        public boolean isManifestParsingEnabled() {
            return false;
        }
    }
}
