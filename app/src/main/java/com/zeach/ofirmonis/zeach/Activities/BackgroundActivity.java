package com.zeach.ofirmonis.zeach.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.zeach.ofirmonis.zeach.Constants.IntentExtras;
import com.zeach.ofirmonis.zeach.R;
import com.zeach.ofirmonis.zeach.Services.BackgroundService;

import static com.zeach.ofirmonis.zeach.Constants.Actions.ACTION_SHUT_DOWN_BACKGROUND_ACTIVITY;

public class BackgroundActivity extends AppCompatActivity {

    private static final String TAG = BackgroundActivity.class.getSimpleName();
    private Intent backgroundService;
    private PowerManager.WakeLock mWakeLock;
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
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK,
                "wakeLock");
        mWakeLock.acquire();
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        moveTaskToBack(true);
        boolean isLoggedIn = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("isLoggedIn", false);
        IntentFilter intentFilter = new IntentFilter(ACTION_SHUT_DOWN_BACKGROUND_ACTIVITY);
        registerReceiver(mShutDownReceiver, intentFilter);
        if (isLoggedIn) {
            Log.i(TAG, "Starting service...");
            backgroundService = new Intent(this, BackgroundService.class);
            backgroundService.putExtra(IntentExtras.BACKGROUND_ACTIVITY, true);
            startService(backgroundService);
        } else {
            Log.i(TAG, "User not logged in. canceling...");
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mShutDownReceiver);
        mWakeLock.release();
        super.onDestroy();
    }
}
