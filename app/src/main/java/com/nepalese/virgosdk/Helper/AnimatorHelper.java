package com.nepalese.virgosdk.Helper;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

/**
 * @author nepalese on 2020/11/18 15:50
 * @usage
 */
public class AnimatorHelper {
    /**
     * rotate ImageView
     * @param imgView
     * @param duration 转一圈时长
     * @return
     */
    public static ObjectAnimator rotateImageView(ImageView imgView, int duration){
        ObjectAnimator animator = ObjectAnimator.ofFloat(imgView, "rotation", 0f,360f);
        animator.setDuration(duration);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setRepeatCount(ValueAnimator.INFINITE);

        return animator;
    }

    /**
     * public void startRotateAnimation() {
     *         mRotateAnimator.cancel();
     *         mRotateAnimator.start();
     *     }
     *
     *     public void cancelRotateAnimation() {
     *         mLastAnimationValue = 0;
     *         mRotateAnimator.cancel();
     *     }
     *
     *     public void pauseRotateAnimation() {
     *         mLastAnimationValue = mRotateAnimator.getCurrentPlayTime();
     *         mRotateAnimator.cancel();
     *     }
     *
     *     public void resumeRotateAnimation() {
     *         mRotateAnimator.start();
     *         mRotateAnimator.setCurrentPlayTime(mLastAnimationValue);
     *     }
     */
}