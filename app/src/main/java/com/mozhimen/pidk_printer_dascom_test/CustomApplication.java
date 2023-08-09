package com.mozhimen.pidk_printer_dascom_test;

import android.app.Application;

import androidx.multidex.MultiDex;

/**
 * Created by HYT on 2021/12/21.
 * Describe:
 */
public class CustomApplication extends Application {

    private Boolean setLog ;

    @Override
    public void onCreate()
    {
        super.onCreate();
        //默认不开启日志

        setSetLog(false);
        MultiDex.install(this);
    }

    public Boolean getSetLog() {
        return setLog;
    }

    public void setSetLog(Boolean setLog) {
        this.setLog = setLog;
    }
}
