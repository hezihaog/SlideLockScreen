package com.zh.android.slidelockscreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * <b>Package:</b> com.zh.android.slidelockscreen <br>
 * <b>Create Date:</b> 2019-12-23  13:40 <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b>  <br>
 */
public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Button goScreenLockBtn = findViewById(R.id.go_screen_lock);
        goScreenLockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, ScreenLockActivity.class));
                finish();
            }
        });
    }
}