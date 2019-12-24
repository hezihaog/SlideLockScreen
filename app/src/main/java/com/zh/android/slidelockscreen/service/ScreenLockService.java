package com.zh.android.slidelockscreen.service;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.zh.android.slidelockscreen.HomeActivity;
import com.zh.android.slidelockscreen.R;
import com.zh.android.slidelockscreen.ScreenLockActivity;

/**
 * <b>Package:</b> com.zh.android.slidelockscreen.service <br>
 * <b>Create Date:</b> 2019-12-24  11:09 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 监听锁屏服务 <br>
 */
public class ScreenLockService extends Service {
    private static final String TAG = ScreenLockService.class.getSimpleName();

    private KeyguardManager.KeyguardLock mKeyguardLock;

    private BroadcastReceiver mScreenLockBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || intent.getAction() == null) {
                return;
            }
            switch (intent.getAction()) {
                //解锁
                case Intent.ACTION_SCREEN_ON:
                    break;
                //锁屏
                case Intent.ACTION_SCREEN_OFF:
                    Intent jumpIntent = new Intent(context, ScreenLockActivity.class);
                    jumpIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(jumpIntent);
                    break;
                //用户解锁
                case Intent.ACTION_USER_PRESENT:
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service => onCreate()");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(mScreenLockBroadcastReceiver, intentFilter);
        startForeground(100, createNotification());
        if (mKeyguardLock != null) {
            mKeyguardLock.disableKeyguard();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service => onDestroy()");
        if (mKeyguardLock != null) {
            mKeyguardLock.reenableKeyguard();
        }
        if (mScreenLockBroadcastReceiver != null) {
            unregisterReceiver(mScreenLockBroadcastReceiver);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        //取消系统锁屏
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        mKeyguardLock = keyguardManager.newKeyguardLock("");
        return super.onStartCommand(intent, flags, startId);
    }

    private Notification createNotification() {
        String channelId = "10086";
        String channelName = "screen_lock_notification_channel";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(notificationChannel);
            }
        }
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setTicker("New")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("屏幕锁")
                .setContentIntent(pendingIntent)
                .setContentText("正在运行")
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .build();
        notification.flags |= Notification.FLAG_NO_CLEAR;
        return notification;
    }
}