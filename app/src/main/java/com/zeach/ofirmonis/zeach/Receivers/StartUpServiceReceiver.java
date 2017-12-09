package com.zeach.ofirmonis.zeach.Receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zeach.ofirmonis.zeach.Services.BackgroundService;

import java.util.Calendar;

/**
 * Created by ofirmonis on 03/10/2017.
 */

public class StartUpServiceReceiver extends BroadcastReceiver {

    private static String TAG = StartUpServiceReceiver.class.getSimpleName();
    private static final String GPS = "GPS";
    private static final String ACTION_STRING_SERVICE = "ToService";
    private Context mContext;

    @Override
    public void onReceive(final Context context, Intent intent) {
        mContext = context;
        //
        /*if (mStartUpBroadcastReceiver != null) {
            IntentFilter intentFilter = new IntentFilter(ACTION_STRING_SERVICE);
            intentFilter.addAction(GPS);
            context.registerReceiver(mStartUpBroadcastReceiver, intentFilter);
        }*/
        //
        // if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
        Log.d(TAG, "StartUp service receiver has catch. starting gps background service..");
        Intent service = new Intent(context, BackgroundService.class);
        // context.startService(service);

        Calendar calendar = Calendar.getInstance();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long interval = 1000 * 60 * 2;
        PendingIntent pendingIntent = PendingIntent.getService(context, BackgroundService.ID,
                service, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), interval, pendingIntent);
        //   sendBroadcast(GPS);


    }

    private void sendBroadcast(String action) {
        Intent intent = new Intent();
        switch (action) {
            case GPS: {
                intent.setAction(GPS);
                mContext.sendBroadcast(intent);
                break;
            }
        }
    }


}
