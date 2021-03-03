package com.nepalese.virgosdk.VirgoView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author nepalese on 2020/11/15 10:40
 * @usage 横向走马灯
 */
public class VirgoMarqueeHorizontal extends View{
    private static final String TAG = "VirgoMarqueeHorizontal";

    private Paint paint;
    private int viewWidth;
    private int viewHeight;

    private Rect rect;//用于获取文字宽度
    private String contents;//需显示内容

    //字体属性
    private float textSize = 20.0f;
    private int textColor = Color.YELLOW;
    private int speed = 1;//滚动速度
    private float textWidth;

    private int backgroundColor = Color.BLACK;//背景颜色
    private final int FLASH_INTERVAL = 50;//ms

    public VirgoMarqueeHorizontal(Context context) {
        this(context, null);
    }

    public VirgoMarqueeHorizontal(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VirgoMarqueeHorizontal(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //解析自定义属性...未加

        rect = new Rect();
        //初始化画笔
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(textColor);
        paint.setTextSize(sp2px(textSize));
    }

    private void startRoll() {
        stopTask();
        handler.post(rollTask);
    }

    private void stopTask(){
        handler.removeCallbacks(rollTask);
    }

    private final Runnable rollTask = new Runnable() {
        @Override
        public void run() {
            if(!TextUtils.isEmpty(contents)) {
                if(getScrollX()>viewWidth+getTextWidth(contents)){
                    scrollTo(0,0);
                }

                scrollBy(speed, 0);
                postInvalidate();

                handler.postDelayed(rollTask, FLASH_INTERVAL);
            }
        }
    };

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        viewWidth = getWidth();
        viewHeight = getHeight();

//        adjustText();//当文本过短，调整 但重绘时感觉有点穿帮
        startRoll();
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(backgroundColor);

        Paint.FontMetricsInt metricsInt = paint.getFontMetricsInt();
        float centerY = (viewHeight - metricsInt.top - metricsInt.bottom) / 2.0f;

        canvas.drawText(contents, viewWidth, centerY, paint);
    }

    private void adjustText() {
        textWidth = getTextWidth(contents);
        if(textWidth < viewWidth){
            String blank = getBlanks(contents.length());
            int times = (int) (viewWidth / textWidth) + 1;
            StringBuilder newCont = new StringBuilder();
            if(times%2==0){
                for(int i=0; i<times/2; i++){
                    newCont.append(contents).append(blank);
                }
            }else{
                for(int i=0; i<times/2; i++){
                    newCont.append(contents).append(blank);
                }
                newCont.append(contents);
            }
            contents = newCont.toString();
        }
        Log.i(TAG, "conts: " + contents);
    }

    private String getBlanks(int length) {
        String temp = "\u3000";
        StringBuilder builder = new StringBuilder();
        for(int i=0; i<length; i++){
            builder.append(temp);
        }

        return builder.toString();
    }

    private float getTextWidth(String str){
        if (str == null || str.equals("")) {
            return 0;
        }
        if (rect == null) {
            rect = new Rect();
        }
        paint.getTextBounds(str, 0, str.length(), rect);

        return rect.width();
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public void setTextSize(float textSize) {
        this.textSize = sp2px(textSize);
        paint.setTextSize(sp2px(textSize));
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        paint.setColor(textColor);
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    private int sp2px(float spValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, getResources().getDisplayMetrics());
    }

    private Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onDetachedFromWindow() {
        stopTask();
        super.onDetachedFromWindow();
    }
}