package com.zh.android.slidelockscreen;

import android.app.Application;
import android.content.Intent;
import android.os.Build;

import com.zh.android.slidelockscreen.service.ScreenLockService;

/**
 * <b>Package:</b> com.zh.android.slidelockscreen <br>
 * <b>Create Date:</b> 2019-12-24  12:36 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b>  <br>
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //启动服务，监听锁屏
        Intent intent = new Intent(this.getApplicationContext(), ScreenLockService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }
}