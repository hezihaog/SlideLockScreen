package com.zh.android.slidelockscreen;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.zh.android.slidelockscreen.widget.SlideLockView;

/**
 * @author wally
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SlideLockView slideRail = findViewById(R.id.slide_rail);
        slideRail.setCallback(new SlideLockView.Callback() {
            @Override
            public void onUnlock() {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}