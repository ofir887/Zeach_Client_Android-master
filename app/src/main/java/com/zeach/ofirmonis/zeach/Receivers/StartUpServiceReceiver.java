package com.zeach.ofirmonis.zeach.Receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;

import com.zeach.ofirmonis.zeach.Activities.BackgroundActivity;
import com.zeach.ofirmonis.zeach.Constants.IntentExtras;
import com.zeach.ofirmonis.zeach.Services.BackgroundService;

import java.util.Calendar;

/**
 * Created by ofirmonis on 03/10/2017.
 */

public class StartUpServiceReceiver extends BroadcastReceiver {

    private static String TAG = StartUpServiceReceiver.class.getSimpleName();
    private static final int INTERVAL = 1000 * 60 * 15;
    private Context mContext;

    @Override
    public void onReceive(final Context context, Intent intent) {
        mContext = context;
        Log.i(TAG, "StartUp service receiver has catch. starting gps background service..");
        Intent activity = new Intent(context, BackgroundActivity.class);
        activity.putExtra(IntentExtras.BACKGROUND, true);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("alarm_manager_on", true).commit();
        Log.i(TAG, "Setting alarm manager flag to true");
        Calendar calendar = Calendar.getInstance();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //repeat every 15 minutes
        PendingIntent pendingIntent = PendingIntent.getActivity(context, BackgroundService.ID,
                activity, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), INTERVAL, pendingIntent);
    }
}
