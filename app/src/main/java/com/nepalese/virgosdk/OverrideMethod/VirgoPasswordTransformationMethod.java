package com.nepalese.virgosdk.OverrideMethod;

import android.text.method.PasswordTransformationMethod;
import android.view.View;

/**
 * @author nepalese on 2020/12/01 12:07
 * @usage 自定义掩码样式的密码输入方式
 */
public class VirgoPasswordTransformationMethod extends PasswordTransformationMethod {
    private static final char DEFAULT_MASK = '*';//default mask

    private final char mask;

    public VirgoPasswordTransformationMethod(char mask){
        if(mask==' '){
            this.mask = DEFAULT_MASK;
        }else{
            this.mask = mask;
        }
    }

    @Override
    public CharSequence getTransformation(CharSequence source, View view) {
        return new PasswordCharSequence(source);
    }

    private class PasswordCharSequence implements CharSequence {
        private CharSequence mSource;

        public PasswordCharSequence(CharSequence source) {
            this.mSource = source; // Store char sequence
        }

        public char charAt(int index) {
            return mask; // This is the important part
        }

        public int length() {
            return mSource.length(); // Return default
        }

        public CharSequence subSequence(int start, int end) {
            return mSource.subSequence(start, end); // Return default
        }
    }
}