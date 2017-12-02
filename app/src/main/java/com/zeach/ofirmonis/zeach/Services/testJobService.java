package com.zeach.ofirmonis.zeach.Services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

import Util.Util;

/**
 * Created by ofirmonis on 02/12/2017.
 */

public class testJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        Intent service = new Intent(getApplicationContext(), GpsService.class);
        getApplicationContext().startService(service);
        Util.scheduleJob(getApplicationContext());
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
