package com.nepalese.virgosdk.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author nepalese on 2020/10/10 10:41
 * @usage
 * 1. bitmap 数据类型转换：
 *      drawable <--> Bitmap;
 *      byte[] <--> Bitmap;
 *      string <--> bitmap;
 * 2. bitmap 读取(res\file)与存储：
 * 3. bitmap 调控：
 *      反转图片 :把每个像素点的每个rgb值都与255相减（alpha的值不改变）；
 *      转灰度图(灰阶图)；
 *      将图片转ASCII码字符:返回ASCII码图，字符串；
 *      设置图片效果，根据色调，饱和度，亮度来调节图片效果；
 *      合并两个Bitmap；
 * 4. bitmap 操控：
 *      质量压缩，旋转，裁剪，伸缩，高斯模糊，透明；
 *
 */
public class BitmapUtil {
    private static final String TAG = "BitmapUtil";

    public enum ScaleType {
        NORMAL, HORIZONTAL, VERTICAL
    }

    //====================================bitmap 数据类型转换========================================
    //drawable <--> Bitmap
    /**
     * bitmap 转 drawable
     * @param context
     * @param bmp
     * @return drawable
     */
    public static Drawable bitmap2Drawable(Context context, Bitmap bmp) {
        return new BitmapDrawable(context.getResources(), bmp); //BitmapDrawable(bmp) 已被弃用
    }

    /**
     * drawable 转 bitmap
     * @param drawable
     * @return bitmap
     */
    public static Bitmap drawable2Bitmap(Drawable drawable) {
        if(drawable==null){
            return null;
        }
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            return bitmapDrawable.getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                drawable.getAlpha() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    //byte[] <--> Bitmap
    /**
     * 将bitmap 转成 byte[]
     * @param bitmap
     * @return
     */
    public static byte[] bitmap2Bytes(Bitmap bitmap) {
        if (bitmap == null) return new byte[0];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();

        try {
            baos.flush();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytes;
    }

    /**
     * byte[] 转 bitmap
     * @param bytes byte[]
     * @return bitmap
     */
    public static Bitmap bytes2Bitmap(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    //string <--> bitmap
    /**
     * bitmap 转 string
     * @param bitmap
     * @return string
     */
    public static String bitmap2String(Bitmap bitmap){
        if (bitmap == null) return null;

        byte[] bytes = bitmap2Bytes(bitmap);
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    /**
     * string 转 bitmap
     * @param data Base64 string
     * @return
     */
    public static Bitmap string2Bitmap(String data){
        byte[] bytes = Base64.decode(data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0, bytes.length);
    }

    //======================================bitmap 获取=============================================
    /**
     * 从资源文件中获取bitmap
     * @param context
     * @param id
     * @return bitmap
     */
    public static Bitmap getBitmapFromRes(Context context, int id){
        return BitmapFactory.decodeResource(context.getResources(), id);
    }

    /**
     * 从资源文件中获取多个bitmap
     * @param context
     * @param ids int[] 资源文件id 数组
     * @return Bitmap[]
     */
    public static Bitmap[] getBitmapsFromRes(Context context, int[] ids){
        Bitmap[] bitmaps = new Bitmap[ids.length];
        for(int i=0; i<ids.length; i++){
            bitmaps[i] = BitmapFactory.decodeResource(context.getResources(), ids[i]);
        }
        return bitmaps;
    }

    /**
     * 从文件中读取bitmap
     * @param filePath 文件（图片）绝对路径
     * @return bitmap
     */
    public static Bitmap getBitmapFromFile(String filePath){
        if(TextUtils.isEmpty(filePath)){
            return null;
        }
        File file = new File(filePath);
        if(!file.exists()){
            return null;
        }

        FileInputStream inputStream;
        Bitmap bitmap = null;
        try {
            inputStream = new FileInputStream(filePath);
            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    //======================================bitmap 存储=============================================
    /**
     * 存储Bitmap到本地, 若文件存在则会先删除，再创建新的
     * @param bitmap
     * @param path 本地路径
     * @param fileName 存储文件名
     */
    public static void saveBitmap2File(Bitmap bitmap, String path, String fileName){
        File file = new File(path+File.separator+fileName);
        if(file.exists()){
            file.delete();
        }
        try {
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);
            //PNG is lossless, so quality is ignored.
            bitmap.compress(Bitmap.CompressFormat.PNG,100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //========================================bitmap 调控============================================
    /**
     * 反转图片 :把每个像素点的每个rgb值都与255相减（alpha的值不改变）
     * @param bitmap
     * @return
     */
    public static Bitmap convertBitmap(Bitmap bitmap){
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int[] pixels = new int[w * h];
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h);

        for(int i=0; i<pixels.length; i++){
            int a = (pixels[i] & 0xff000000)>>24;
            int r = (pixels[i] & 0x00ff0000)>>16;
            int g = (pixels[i] & 0x0000ff00)>>8;
            int b = pixels[i] & 0x000000ff;

            pixels[i] = a<<24 | (255-r)<<16 | (255-g) <<8 | (255-b);
        }

        bitmap = Bitmap.createBitmap(pixels, w, h, Bitmap.Config.ARGB_8888);
        return bitmap;
    }

    /**
     * 转灰度图:Gray Scale Image 或是Grey Scale Image，又称灰阶图。
     * 把白色与黑色之间按对数关系分为若干等级，称为灰度。
     * 灰度分为256阶。
     * 用灰度表示的图像称作灰度图。
     * 1.浮点算法：Gray=R0.3+G0.59+B*0.11
     * 2.整数方法：Gray=(R30+G59+B*11)/100
     * 3.移位方法：Gray =(R76+G151+B*28)>>8;
     * 4.平均值法：Gray=（R+G+B）/3;
     * 5.仅取绿色：Gray=G；
     * @param bitmap
     * @return
     */
    public static Bitmap grayBitmap(Bitmap bitmap){
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int[] pixels = new int[w * h];
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h);

        for(int i=0; i<pixels.length; i++){
            int a = (pixels[i] & 0xff000000)>>24;
            int r = (pixels[i] & 0x00ff0000)>>16;
            int g = (pixels[i] & 0x0000ff00)>>8;
            int b = pixels[i] & 0x000000ff;

            int avg = (r+g+b)/3;
            pixels[i] = a<<24 | (avg)<<16 | (avg) <<8 | (avg);
        }

        bitmap = Bitmap.createBitmap(pixels, w, h, Bitmap.Config.ARGB_8888);
        return bitmap;
    }

    /**
     * 将图片转ASCII码字符
     * @param bitmap 源图
     * @return 码图
     */
    public static String bitmap2AsciiStr(Bitmap bitmap){
        //把字符分成15阶，即0-14
        String[] arr = {"M","N","H","Q","$","O","C","?","7",">","!",":","–",";","."};

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int[] pixels = new int[w * h];
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h);
        StringBuilder builder = new StringBuilder();

        //将每个像素转换相应的字符
        for(int i=0; i<pixels.length; i++){
            int r = (pixels[i] & 0x00ff0000)>>16;
            int g = (pixels[i] & 0x0000ff00)>>8;
            int b = pixels[i] & 0x000000ff;

            int avg = (r+g+b)/3;
            //0-255转换成0-14阶
            int step = (int) Math.floor(avg/18f);
            if(i>0 && i%w==0){
                //换行
                builder.append("\n");
            }
            builder.append(arr[step]);
        }

        return builder.toString();
    }

    /**
     * 将图片转ASCII码字符文件
     * @param bitmap 源图
     */
    public static void bitmap2AsciiFile(Bitmap bitmap, String path, String name){
        FileUtil.writeToFile(bitmap2AsciiStr(bitmap), path, name);
    }

    /**
     * 将图片转ASCII码图
     * @param bitmap 源图
     * @return 码图
     */
    public static Bitmap bitmap2Ascii(Bitmap bitmap){
        //把字符分成15阶，即0-14
        String[] arr = {"M","N","H","Q","$","O","C","?","7",">","!",":","–",";","."};
        //一个像素转为字符后缩放比例
        int scale =7;
        int maxPix = 170000;
        float textSize = 12;

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        //缩放图片: 防溢出
        if(w*h>maxPix){
            int rate = (int) Math.round(Math.sqrt(w*h*1f/maxPix));
            Log.i(TAG, "缩放图片: " + rate);
            bitmap = BitmapUtil.scaleBitmap(bitmap, 1f/rate);

            w = bitmap.getWidth();
            h = bitmap.getHeight();
        }

        int[] pixels = new int[w * h];

        //获取像素点
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h);

        //初始化
        StringBuilder builder = new StringBuilder();
        Bitmap outBitmap = Bitmap.createBitmap(w*scale, h*scale, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(outBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTypeface(Typeface.MONOSPACE);
        paint.setTextSize(textSize);
        paint.setColor(Color.BLACK);

        //白底
        canvas.drawColor(Color.WHITE);

        //将每个像素转换相应的字符
        for(int i=0; i<pixels.length; i++){
            int r = (pixels[i] & 0x00ff0000)>>16;
            int g = (pixels[i] & 0x0000ff00)>>8;
            int b = pixels[i] & 0x000000ff;

            //灰度算法
            int avg = (r+g+b)/3;
            //0-255转换成0-14阶
            int step = (int) Math.floor(avg/18f);
            if(i>0 && i%w==0){
                //换行、绘制、重置
                builder.append("\n");
                canvas.drawText(builder.toString(), 0, (int)(i/w)*scale, paint);
                builder.delete(0, builder.capacity());
            }
            builder.append(arr[step]);
        }

        return outBitmap;
    }

    /**
     * 设置图片效果，根据色调，饱和度，亮度来调节图片效果, 会创建新图
     * @param bitmap:要处理的图像
     * @param hue:色调
     * @param saturation:饱和度
     * @param lightness:亮度
     */
    public static Bitmap bitmapEffect(Bitmap bitmap, float hue, float saturation, float lightness) {
        hue = hue < -180.0f ? -180.0f : Math.min(hue, 180.0f);
        saturation = saturation < 0.0f ? 0.0f : Math.min(saturation, 2.0f);
        lightness = lightness < 0.0f ? 0.0f : Math.min(lightness, 2.0f);

        Bitmap bmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        ColorMatrix hueMatrix = new ColorMatrix();
        hueMatrix.setRotate(0, hue);    //0代表R，红色
        hueMatrix.setRotate(1, hue);    //1代表G，绿色
        hueMatrix.setRotate(2, hue);    //2代表B，蓝色

        ColorMatrix saturationMatrix = new ColorMatrix();
        saturationMatrix.setSaturation(saturation);

        ColorMatrix lumMatrix = new ColorMatrix();
        lumMatrix.setScale(lightness, lightness, lightness, 1);

        ColorMatrix imageMatrix = new ColorMatrix();
        imageMatrix.postConcat(hueMatrix);
        imageMatrix.postConcat(saturationMatrix);
        imageMatrix.postConcat(lumMatrix);

        paint.setColorFilter(new ColorMatrixColorFilter(imageMatrix));
        canvas.drawBitmap(bitmap, 0, 0, paint);

        //回收
        bitmap.recycle();
        return bmp;
    }

    /**
     * 合并两个Bitmap
     * @param backBitmap
     * @param frontBitmap
     * @param width 合成后图的宽
     * @param height 合成后图的高
     * @return
     */
    public static Bitmap mergeBitmap(Bitmap backBitmap, Bitmap frontBitmap, int width, int height) {
        if (backBitmap == null || backBitmap.isRecycled()
                || frontBitmap == null || frontBitmap.isRecycled()) {
            Log.e(TAG, "backBitmap=" + backBitmap + ";frontBitmap=" + frontBitmap);
            return null;
        }
        Bitmap bitmap = backBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);
        Log.i(TAG, "mergeBitmap: width=" + width + " height=" + height);
        int leftGap = (width - frontBitmap.getWidth()) / 2;
        int topGap = (height - frontBitmap.getHeight()) / 2;
        Log.i(TAG, "mergeBitmap: leftGap=" + leftGap + " topGap=" + topGap);
        Log.i(TAG, "mergeBitmap: frontBitmap.getWidth()=" + frontBitmap.getWidth() + " frontBitmap.getHeight()=" + frontBitmap.getHeight());
        Rect baseRect = new Rect(0, 0, frontBitmap.getWidth(), frontBitmap.getHeight());
        Rect frontRect = new Rect(leftGap, topGap, leftGap + frontBitmap.getWidth(), topGap + frontBitmap.getHeight());
        canvas.drawBitmap(frontBitmap, baseRect, frontRect, null);
        return bitmap;
    }

    //=========================================bitmap 操控==========================================
    /**
     * 质量压缩图片方法
     * @param bitmap  原始 bitmap
     * @param limitSize 大于多少 kb 就压缩。
     * @return bitmap
     */
    public static Bitmap compressBitmap(Bitmap bitmap, int limitSize) {
        if (bitmap == null) return null;
        if (limitSize < 1) limitSize = 1;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //quality: 100为最高(不压缩)，0为最差
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;

        //循环判断如果压缩后图片是否大于limitSize,大于则继续压缩， 最多压缩80%
        while (baos.toByteArray().length / 1024 > limitSize && options > 20) {
            baos.reset();//即清空baos
            options -= 10;//每次都减少10
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        Bitmap out = BitmapFactory.decodeStream(bais, null, null);

        //关闭流
        try {
            baos.flush();
            baos.close();
            bais.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return out;
    }

    /**
     * 设置图片旋转度, 以及镜像情况,( 将图片按照某个角度进行旋转, 然后再镜像)
     * @param bitmap  需要旋转的图片
     * @param degree  旋转角度
     * @param scaleType 旋转轴
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int degree, ScaleType scaleType) {
        if (bitmap == null) return null;
        if (degree == 0 && scaleType == ScaleType.NORMAL) return bitmap;

        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        if ((degree % 360) != 0) {
            matrix.postRotate(degree % 360);
        }
        if (scaleType == ScaleType.VERTICAL) {//垂直方向翻转
            matrix.postScale(1, -1);
        } else if (scaleType == ScaleType.HORIZONTAL) { //水平方向翻转
            matrix.postScale(-1, 1);
        }

        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            Bitmap out = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            //将原bitmap回收
            bitmap.recycle();
            return out;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            gc();
            return bitmap;
        }
    }

    /**
     * 仅旋转bitmap
     * @param bitmap
     * @param degree
     * @return
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int degree){
        if(degree%360 == 0){
            return bitmap;
        }

        Matrix matrix = new Matrix();
        matrix.postRotate(degree % 360);

        Bitmap newBm = Bitmap.createBitmap(bitmap,0,0, bitmap.getWidth(), bitmap.getHeight(), matrix,true);//bg
        if(newBm!=bitmap){
            bitmap.recycle();
        }

        return newBm;
    }

    /**
     * 裁剪圆形图片
     * @param bitmap
     * @return
     */
    public static Bitmap getCircleBitmap(Bitmap bitmap){
        //先进行修剪（非正方形图片）
        bitmap = cutBitmap(bitmap);

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Bitmap aimBm = Bitmap.createBitmap(w,h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(aimBm);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0,0, w, h);//w,h
        final RectF rectF = new RectF(rect);
        float roundP = w>h ? h/2.0f : w/2.0f;

        paint.setAntiAlias(true);
        canvas.drawARGB(0,0,0,0);
        paint.setColor(Color.WHITE);
        canvas.drawRoundRect(rectF,roundP,roundP,paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        final Rect src = new Rect(0,0,w,h);//w,h
        canvas.drawBitmap(bitmap, src, rect, paint);

        return aimBm;
    }

    /**
     * 裁剪正方形图片(以长宽中小的为新边长）
     * @param bitmap
     * @return
     */
    public static Bitmap cutBitmap(Bitmap bitmap){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if(width==height){
            return bitmap;
        }

        int len;
        int offset;
        Bitmap newBm;
        if(width>height){
            len = height;
            offset= (width-len)/2;
            newBm = Bitmap.createBitmap(bitmap,offset,0,len, len, null,true);
            bitmap.recycle();
            return newBm;
        }else{
            len = width;
            offset= (height-len)/2;
            newBm = Bitmap.createBitmap(bitmap,0,offset,len, len, null,true);
            bitmap.recycle();
            return newBm;
        }
    }

    /**
     * 裁切图片：指定裁剪位置
     * @param bitmap
     * @param left
     * @param top
     * @param right
     * @param bottom
     * @return
     */
    public static Bitmap clipBitmap(Bitmap bitmap, int left, int top, int right, int bottom) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int rawArea = (right - left) * (bottom - top);
        int targetArea = width * height * 4;

        int resultArea = rawArea;

        while (resultArea > targetArea) {
            options.inSampleSize *= 2;
            resultArea = rawArea / (options.inSampleSize * options.inSampleSize);
        }

        if (options.inSampleSize > 1) {
            options.inSampleSize /= 2;
        }

        try {
            left /= options.inSampleSize;
            top /= options.inSampleSize;
            right /= options.inSampleSize;
            bottom /= options.inSampleSize;

            Bitmap croppedBitmap = Bitmap.createBitmap(bitmap, left, top, right, bottom);
            bitmap.recycle();
            return croppedBitmap;
        } catch (Throwable t) {
            return null;
        }
    }

    /**
     * 伸缩图片
     * @param bitmap
     * @param aimW 想要的宽度
     * @param amiH 想要的高度
     * @return
     */
    public static Bitmap scaleBitmap(Bitmap bitmap, int aimW, int amiH){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleW = (float)aimW/width ;
        float scaleH = (float)amiH/height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleW,scaleH);

        Bitmap newBm = Bitmap.createBitmap(bitmap,0,0,width,height,matrix,true);
        bitmap.recycle();

        return newBm;
    }

    public static Bitmap scaleBitmap2(Bitmap bitmap, int aimW, int aimH){
        if(bitmap.getWidth()==aimW && bitmap.getHeight()==aimH){
            return bitmap;
        }

        Bitmap aimBm = Bitmap.createScaledBitmap(bitmap,aimW,aimH,false);
        if(aimBm!=bitmap){
            bitmap.recycle();
        }

        return aimBm;
    }

    /**
     * 伸缩图片
     * @param bitmap
     * @param scale 缩放比例
     * @return
     */
    public static Bitmap scaleBitmap(Bitmap bitmap, float scale){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.postScale(scale,scale);

        Bitmap newBm = Bitmap.createBitmap(bitmap,0,0,width,height,matrix,true);
        bitmap.recycle();

        return newBm;
    }

    /**
     * 高斯模糊
     * @param context
     * @param bitmap
     * @param degree 25f是最大模糊度
     * @param scale  图片缩放比例: 0.4f
     * @return
     */
    public static Bitmap blurBitmap(Context context, Bitmap bitmap, float degree, float scale){
        // 计算图片缩小后的长宽
        int width = Math.round(bitmap.getWidth() * scale);
        int height = Math.round(bitmap.getHeight() * scale);

        // 将缩小后的图片做为预渲染的图片
        Bitmap inputBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        // 创建一张渲染后的输出图片
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        // 创建RenderScript内核对象
        RenderScript rs = RenderScript.create(context);
        // 创建一个模糊效果的RenderScript的工具对象
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        // 由于RenderScript并没有使用VM来分配内存,所以需要使用Allocation类来创建和分配内存空间
        // 创建Allocation对象的时候其实内存是空的,需要使用copyTo()将数据填充进去
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);

        // 设置渲染的模糊程度, 25f是最大模糊度
        blurScript.setRadius(degree);
        // 设置blurScript对象的输入内存
        blurScript.setInput(tmpIn);
        // 将输出数据保存到输出内存中
        blurScript.forEach(tmpOut);

        // 将数据填充到Allocation中
        tmpOut.copyTo(outputBitmap);

        //回收：
        bitmap.recycle();
        inputBitmap.recycle();

        return outputBitmap;
    }

    /**
     * 使用时，将图片缩小后再使用，避免oom
     * @param bitmap
     * @param radius 模糊半径：200
     * @return
     */
    public static Bitmap blurBitmap(Bitmap bitmap, int radius) {
        if (radius < 1) {
            return null;
        }

        Bitmap outBitmap = bitmap.copy(bitmap.getConfig(), true);
        bitmap.recycle();

        int w = outBitmap.getWidth();
        int h = outBitmap.getHeight();

        int[] pix = new int[w * h];
        outBitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int[] r = new int[wh];
        int[] g = new int[wh];
        int[] b = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int[] vmin = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int[] dv = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        outBitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return outBitmap;
    }

    /**
     * 设置图片透明度
     * @param bitmap
     * @param alpha 0-255（越小越透明）
     * @return
     */
    public static Bitmap setBitmapAlpha(Bitmap bitmap, int alpha){
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int[] pixels = new int[w * h];
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h);

        //alpha = alpha / 100 * 255;
        for(int i=0; i<pixels.length; i++){
            pixels[i] = (alpha<<24) | (pixels[i] & 0x00ffffff);
        }

        bitmap = Bitmap.createBitmap(pixels, w, h, Bitmap.Config.ARGB_8888);

        return bitmap;
    }

    //==========================================other===============================================
    /**
     * 读取图片文件的类型，仅图片文件！！！
     * @param file
     * @return String; null -> 未知？文件不存在
     */
    public static String readBitmapType(File file) {
        String type = null;
        if (file.exists()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;// 只读边,不读内容
            BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            type = options.outMimeType;
        }
        return type;
    }

    //回收
    private static void gc() {
        System.gc();
        // 表示java虚拟机会做一些努力运行已被丢弃对象（即没有被任何对象引用的对象）的 finalize
        // 方法，前提是这些被丢弃对象的finalize方法还没有被调用过
        System.runFinalization();
    }
}