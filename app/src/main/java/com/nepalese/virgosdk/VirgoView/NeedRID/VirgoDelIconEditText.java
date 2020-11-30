package com.nepalese.virgosdk.VirgoView.NeedRID;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;

//
public class VirgoDelIconEditText extends androidx.appcompat.widget.AppCompatEditText {
    private Drawable icon;

    public VirgoDelIconEditText(Context context) {
        this(context, null);
    }

    public VirgoDelIconEditText(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public VirgoDelIconEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //set delete icon;
        Init();
    }

    private void Init() {
//        this.icon = getResources().getDrawable(R.drawable.ic_del_icon);

        //set text change listener;
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                SetDrawable();
            }
        });
    }

    private void SetDrawable() {
        if (length() < 1) {
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        } else {
            setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (icon != null && event.getAction() == MotionEvent.ACTION_UP) {
            int eventX = (int) event.getRawX();
            int eventY = (int) event.getRawY();
            Rect rect = new Rect();
            getGlobalVisibleRect(rect);
            rect.left = rect.right - 24;//图标宽度
            if (rect.contains(eventX, eventY)) {
                setText("");//clear contents
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
