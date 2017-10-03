package com.zeach.ofirmonis.zeach.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zeach.ofirmonis.zeach.Services.StartService;

/**
 * Created by ofirmonis on 03/10/2017.
 */

public class StartUpServiceReceiver extends BroadcastReceiver {

    private static String TAG = StartUpServiceReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.d(TAG, "StartUp service receiver has catch. starting gps background service..");
            Intent service = new Intent(context, StartService.class);
            context.startService(service);
        }
    }
}
