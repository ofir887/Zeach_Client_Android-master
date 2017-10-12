package com.zeach.ofirmonis.zeach.Receivers;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.CalendarContract;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.zeach.ofirmonis.zeach.Services.BackgroundService;
import com.zeach.ofirmonis.zeach.Services.GpsService;
import com.zeach.ofirmonis.zeach.Services.StartService;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by ofirmonis on 03/10/2017.
 */

public class StartUpServiceReceiver extends BroadcastReceiver {

    private static String TAG = StartUpServiceReceiver.class.getSimpleName();

    @Override
    public void onReceive(final Context context, Intent intent) {
        // if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
        Log.d(TAG, "StartUp service receiver has catch. starting gps background service..");
        Intent service = new Intent(context, BackgroundService.class);
        // context.startService(service);


        /*Calendar calendar = Calendar.getInstance();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long interval = 1000 * 60*3;
        PendingIntent pendingIntent = PendingIntent.getService(context, BackgroundService.ID,
                service, PendingIntent.FLAG_CANCEL_CURRENT);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), interval, pendingIntent);*/


    }


}
