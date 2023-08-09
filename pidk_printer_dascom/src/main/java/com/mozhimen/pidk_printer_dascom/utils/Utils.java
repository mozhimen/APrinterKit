package com.mozhimen.pidk_printer_dascom.utils;

import android.graphics.Bitmap;
import android.util.Log;

public class Utils {
    public static void showBitmapInfo(Bitmap bitmap){
        Log.d("rongjiajie","压缩后的图片大小："+bitmap.getByteCount()/1024/1024+"M");
        Log.d("rongjiajie","宽度："+bitmap.getWidth());
        Log.d("rongjiajie","高度："+bitmap.getHeight());
    }
}
