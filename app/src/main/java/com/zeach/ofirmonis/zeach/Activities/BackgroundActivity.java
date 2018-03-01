package com.zeach.ofirmonis.zeach.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.zeach.ofirmonis.zeach.Constants.IntentExtras;
import com.zeach.ofirmonis.zeach.R;
import com.zeach.ofirmonis.zeach.Services.BackgroundService;

import static com.zeach.ofirmonis.zeach.Constants.Actions.ACTION_SHUT_DOWN_BACKGROUND_ACTIVITY;

public class BackgroundActivity extends AppCompatActivity {

    private static final String TAG = BackgroundActivity.class.getSimpleName();
    private Intent backgroundService;
    private BroadcastReceiver mShutDownReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "Shut down request received. shutting down...");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopService(backgroundService);
                    finish();
                }
            }, 1000 * 10);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background);
        boolean isLoggedIn = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            IntentFilter intentFilter = new IntentFilter(ACTION_SHUT_DOWN_BACKGROUND_ACTIVITY);
            registerReceiver(mShutDownReceiver, intentFilter);
            Log.i(TAG, "Starting service...");
            moveTaskToBack(true);
            backgroundService = new Intent(this, BackgroundService.class);
            backgroundService.putExtra(IntentExtras.BACKGROUND_ACTIVITY, true);
            startService(backgroundService);
        } else {
            Log.i(TAG, "User not logged in. canceling...");
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mShutDownReceiver);
        super.onDestroy();
    }
}
