package com.nepalese.virgosdk.VirgoView.MyView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * @author nepalese on 2021/1/13 17:40
 * @usage 内部项全部显示，用于内嵌view
 */
 public class MyListView extends ListView {
    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 设置不滚动
     */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
