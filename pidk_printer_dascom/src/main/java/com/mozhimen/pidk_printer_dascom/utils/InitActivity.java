package com.mozhimen.pidk_printer_dascom.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.view.KeyEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import com.mozhimen.pidk_printer_dascom.R;

/*
 * Copyright (c) 2020 DASCOM All rights reserved.
 */


public class InitActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        new MyToast(this);
        ActivityUtil.getInstance().addActivity(this);
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        //Activity销毁时，提示系统回收
        //System.gc();
        //移除Activity
        ActivityUtil.getInstance().removeActivity(this);
        super.onDestroy();
    }

    /**
     * 页面跳转
     *
     * @param clz
     */
    public void startActivity(Class<?> clz) {
        startActivity(clz, null);
    }

    /**
     * 携带数据的页面跳转
     *
     * @param clz
     * @param bundle
     */
    public void startActivity(Class<?> clz, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * 含有Bundle通过Class打开编辑界面
     *
     * @param clz
     * @param bundle
     * @param requestCode
     */
    public void startActivity(Class<?> clz, Bundle bundle, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(this, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }


    public void Toast(String text) {
        runOnUiThread(() -> MyToast.makeText(text));
    }

    public void Toast(String text, int showLong) {
        runOnUiThread(() -> MyToast.makeText(text, showLong));
    }

    public static void setActivityState(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    //封装findViewById
    protected <T extends View> T findView(int id) {
        return (T) findViewById(id);
    }

    public void openDialog() {
        ProgressDialogUtil.showProgressDialog(this, "请稍后...");
    }

    public void openDialog(String content) {
        ProgressDialogUtil.showProgressDialog(this, content);
    }

    public void closeDialog() {
        ProgressDialogUtil.dismiss();
    }


}
