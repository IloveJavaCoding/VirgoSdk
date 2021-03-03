package com.nepalese.virgosdk.OverrideMethod;

import android.view.MotionEvent;
import android.view.View;

/**
 * @author nepalese on 2021/01/18 10:43
 * @usage 自定义双击监听
 */
public class VirgoOnDoubleClickListener implements View.OnTouchListener{
    private int count = 0;//点击次数
    private long firstClick = 0;//第一次点击时间
    private final int duration = 500;//预设两次点击最多间隔时间

    private Callback callback;

    public interface Callback{
        //具化双击后要执行的任务
        void onDoubleClick();
    }

    public VirgoOnDoubleClickListener(Callback callback) {
        this.callback = callback;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            count ++;
            if (count == 1){
                firstClick = System.currentTimeMillis();
            }else if (count == 2){
                //第二次点击时间
                long secondClick = System.currentTimeMillis();
                if (secondClick - firstClick<=duration){
                    if (callback!=null){
                        callback.onDoubleClick();//调用重写方法
                    }
                    count = 0;
                    firstClick = 0;
                    return true;
                }else {
                    //间隔时间超过设定值，重新计算
                    firstClick = secondClick;
                    count = 1;
                }
            }
        }
        //最后要返回false 否则单击事件会被屏蔽掉
        return false;
    }
}