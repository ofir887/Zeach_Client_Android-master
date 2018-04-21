package com.crackme_native.ofirmonis.locationservice.Receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.crackme_native.ofirmonis.locationservice.Services.GpsService;

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
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        if(intent.getAction() != null)
        {
            if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) ||
                    intent.getAction().equals(Intent.ACTION_USER_PRESENT) || intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) || intent.getAction().equals(Intent.ACTION_LOCKED_BOOT_COMPLETED) || intent.getAction().equals(Intent.ACTION_POWER_CONNECTED))
            {
                Log.d(TAG, "StartUp service receiver has catch. starting gps background service..");
                context.startService(new Intent(context, GpsService.class));
            }
        }
        //
        /*if (mStartUpBroadcastReceiver != null) {
            IntentFilter intentFilter = new IntentFilter(ACTION_STRING_SERVICE);
            intentFilter.addAction(GPS);
            context.registerReceiver(mStartUpBroadcastReceiver, intentFilter);
        }*/
        //
        // if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
        Log.d(TAG, "StartUp service receiver has catch. starting gps background service..");
        // TODO
        Intent service = new Intent(context, GpsService.class);
        // context.startService(service);

        Calendar calendar = Calendar.getInstance();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long interval = 1000 * 60;
        PendingIntent pendingIntent = PendingIntent.getService(context, GpsService.ID,
                service, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), interval, pendingIntent);
        //TODO
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
