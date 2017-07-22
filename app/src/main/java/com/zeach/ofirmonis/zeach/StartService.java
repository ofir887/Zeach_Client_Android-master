package com.zeach.ofirmonis.zeach;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ofirmonis on 05/08/2017.
 */

public class StartService extends Service {

    private Timer timer;
    private int i =0;
    private Thread backgroundThread;
    private Runnable myTask = new Runnable() {
        public void run() {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask()
            {
                public void run()

                {
                    Log.d("service started",String.valueOf(i));  // display the data
                    //  data.child("Ofir").setValue("dfdsfd" + i);
                    i++;

                }
            }, 1000, 5000);
            // stopSelf();
        }
    };



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
       // super.onCreate();
      //  this.backgroundThread = new Thread(myTask);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       // return super.onStartCommand(intent, flags, startId);
//        this.backgroundThread.start();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask()
        {
            public void run()
            {
                Log.d("service started","service"+i);  // display the data
                i++;
                startService(new Intent(getApplicationContext(),GpsService.class));
            }
        }, 1000, 1000*30);
        return START_STICKY;
    }

}
