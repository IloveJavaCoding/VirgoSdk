package com.nepalese.virgosdk.Helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class GlideImageHelper {
    private int type;
    public static final int TYPE_FILE = 1;
    public static final int TYPE_URL = 2;

    public GlideImageHelper(int type){
        this.type = type;
    }

    public void displayImage(Context context, Object obj, ImageView imageView) {
        switch (type){
            case TYPE_FILE:
                //load local image:bitmap
                imageView.setImageBitmap((Bitmap) obj);
                break;
            case TYPE_URL:
                //load web image:url
                Glide.with(context).load(obj).into(imageView);
                break;
            default:
                break;
        }
    }
}
