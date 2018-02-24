package com.zeach.ofirmonis.zeach.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.zeach.ofirmonis.zeach.R;
import com.zeach.ofirmonis.zeach.Services.BackgroundService;

public class BackgroundActivity extends AppCompatActivity {

    private static final String TAG = BackgroundActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background);
        Log.i(TAG, "Starting service...");
        moveTaskToBack(true);
        Intent backgroundService = new Intent(this, BackgroundService.class);
        startService(backgroundService);
    }
}
