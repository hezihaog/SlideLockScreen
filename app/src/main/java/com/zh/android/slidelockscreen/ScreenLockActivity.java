package com.zh.android.slidelockscreen;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.zh.android.slidelockscreen.widget.SlideLockView;

/**
 * <b>Package:</b> com.zh.android.slidelockscreen <br>
 * <b>Create Date:</b> 2019-12-24  11:09 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 锁屏页面 <br>
 */
public class ScreenLockActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_lock);
        SlideLockView slideRail = findViewById(R.id.slide_rail);
        slideRail.setCallback(new SlideLockView.Callback() {
            @Override
            public void onUnlock() {
                Intent intent = new Intent(ScreenLockActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}