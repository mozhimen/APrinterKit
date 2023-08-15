package com.mozhimen.pidk_printer_dascom_test;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mozhimen.pidk_printer_dascom.R;

/*
 * Copyright (c) 2020 DASCOM All rights reserved.
 */


public class MyToast {
    private static Toast mToast;
    private static Context context;

    public MyToast(Context context){
        this.context = context;
    }

    public static void makeText(String msg){
        makeText(msg,Toast.LENGTH_SHORT);
    }


    public static void makeText(String msg,int showLong){
        //设置Toast要显示的位置，水平居中并在底部，X轴偏移0个单位，Y轴偏移70个单位，
        makeText(msg,showLong,Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM,0,170);
    }

    public static void makeText(String msg,int showLong,int gravity,int xOffset,int yOffset){
        if (context == null){
            return;
        }
        if (mToast !=null){
            hide();
        }
        //使用布局加载器，将编写的toast_layout布局加载进来
        View view = LayoutInflater.from(context).inflate(R.layout.layout_toast,null);
        //初始化控件
        TextView textView = view.findViewById(R.id.tv_toast);
        //设置显示内容
        textView.setText(msg);
        mToast = new Toast(context);
        //设置Toast要显示的位置，水平居中并在底部，X轴偏移/个单位，Y轴偏移/个单位，
        mToast.setGravity(gravity,xOffset,yOffset);
        //设置显示时长
        mToast.setDuration(showLong);
        mToast.setView(view);
        mToast.show();
    }

    public static void hide(){
        if (mToast !=null){
            mToast.cancel();
        }
    }


}
