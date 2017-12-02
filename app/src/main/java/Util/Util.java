package Util;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import com.zeach.ofirmonis.zeach.Services.BackgroundService;
import com.zeach.ofirmonis.zeach.Services.testJobService;

/**
 * Created by ofirmonis on 02/12/2017.
 */

public class Util {
    public static void scheduleJob(Context context) {
        Log.i("SchduleJob", "starting job again ..");
        ComponentName serviceComponent = new ComponentName(context, testJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        builder.setMinimumLatency(60 * 1000 * 2); // wait at least
        builder.setOverrideDeadline(1000 * 60); // maximum delay
        //builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
        //builder.setRequiresDeviceIdle(true); // device should be idle
        //builder.setRequiresCharging(false); // we don't care if the device is charging or not
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        jobScheduler.schedule(builder.build());
    }


}
