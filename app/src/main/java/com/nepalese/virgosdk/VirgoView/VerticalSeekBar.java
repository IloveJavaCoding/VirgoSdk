package com.nepalese.virgosdk.VirgoView;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * @author nepalese on 2020/11/19 10:43
 * @usage 竖向的seekbar
 */
public class VerticalSeekBar extends androidx.appcompat.widget.AppCompatSeekBar {
    private static final String TAG = "VirgoVerticalSeekBar";

    private OnVerticalSeekBarChangeListener listener;//回调监听
    private int process;//进度

    public VerticalSeekBar(Context context) {
        this(context, null);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnVerticalSeekBarChangeListener(OnVerticalSeekBarChangeListener listener){
        this.listener = listener;
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    protected void onDraw(Canvas canvas) {
        //将SeekBar转转90度
        canvas.rotate(-90);
        //将旋转后的视图移动回来
        canvas.translate(-getHeight(),0);
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onStartTrackingTouch();
            case MotionEvent.ACTION_MOVE:
                //获取滑动的距离
                process = getMax() - (int) (getMax() * event.getY() / getHeight());
                //设置进度
                setProgress(process);
                Log.d(TAG , "Process: " + getProgress());
                //每次拖动SeekBar都会调用
                onSizeChanged(getWidth(), getHeight(), 0, 0);
                onProgressChanged();
            case MotionEvent.ACTION_UP:
                onStopTrackingTouch();
                break;

            case MotionEvent.ACTION_CANCEL:
                onStopTrackingTouch();
                break;
        }
        return true;
    }

    void onProgressChanged() {
        if(listener!=null){
            listener.onProgressChanged(this,process,true);
        }
    }

    void onStartTrackingTouch() {
        if(listener!=null){
            listener.onStartTrackingTouch(this);
        }
    }

    void onStopTrackingTouch() {
        if(listener!=null){
            listener.onStopTrackingTouch(this);
        }
    }

    public interface OnVerticalSeekBarChangeListener {
        void onProgressChanged(VerticalSeekBar verticalSeekBar, int process, boolean fromUser);

        void onStartTrackingTouch(VerticalSeekBar verticalSeekBar);

        void onStopTrackingTouch(VerticalSeekBar verticalSeekBar);
    }
}