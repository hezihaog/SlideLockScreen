package com.zh.android.slidelockscreen.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zh.android.slidelockscreen.service.ScreenLockService;

/**
 * <b>Package:</b> com.zh.android.slidelockscreen.receiver <br>
 * <b>Create Date:</b> 2019-12-24  11:08 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 开机自启监听 <br>
 */
public class SystemBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //启动服务，监听锁屏
        context.startService(new Intent(context, ScreenLockService.class));
    }
}