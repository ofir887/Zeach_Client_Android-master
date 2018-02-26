package com.zeach.ofirmonis.zeach.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zeach.ofirmonis.zeach.Activities.BackgroundActivity;

/**
 * Created by ofirmonis on 03/10/2017.
 */

public class StartUpServiceReceiver extends BroadcastReceiver {

    private static String TAG = StartUpServiceReceiver.class.getSimpleName();
    private Context mContext;

    @Override
    public void onReceive(final Context context, Intent intent) {
        mContext = context;
        Log.i(TAG, "StartUp service receiver has catch. starting gps background service..");
        Intent activity = new Intent(context, BackgroundActivity.class);
        activity.putExtra("background", true);

        /*Calendar calendar = Calendar.getInstance();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long interval = 1000 * 60 * 5;
        PendingIntent pendingIntent = PendingIntent.getActivity(context, BackgroundService.ID,
                activity, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), interval, pendingIntent);*/
    }
}
