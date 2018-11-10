package com.ijourney.conbowcontrol.application;

import android.app.Application;

import com.blankj.utilcode.util.Utils;

import org.litepal.LitePal;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
        Utils.init(this);
    }
}
