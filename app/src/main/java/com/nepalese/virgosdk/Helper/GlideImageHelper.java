package com.nepalese.virgosdk.Helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;


public class GlideImageHelper {
    private int type;
    public GlideImageHelper(int type){
        this.type = type;
    }

    public void displayImage(Context context, Object obj, ImageView imageView) {
        switch (type){
            case 1:
                //1. load web image:url
                Glide.with(context).load(obj).into(imageView);
                break;
            case 2:
                //2. load local image:bitmap
                imageView.setImageBitmap((Bitmap) obj);
                break;
            default:
                //
                break;
        }
    }
}
