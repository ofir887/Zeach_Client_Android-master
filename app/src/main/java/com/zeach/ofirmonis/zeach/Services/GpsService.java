package com.zeach.ofirmonis.zeach.Services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

public class GpsService extends Service implements LocationListener {


    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    public static boolean checkPermission(final Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful
        /*LocationManager LM2 = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (checkPermission(getApplicationContext()))
            LM2.requestLocationUpdates("gps", 100, 0, this);*/
        return Service.START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("GpsService", "GpsService");
        LocationManager LM2 = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (checkPermission(getApplicationContext()))
            LM2.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
    }

    @Override
    public void onLocationChanged(Location location) {
// TODO Auto-generated method stub
        Log.d("GpsService", location.toString());
        onDestroy();

    }

    @Override
    public void onProviderDisabled(String provider) {
// TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
// TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
// TODO Auto-generated method stub

    }

}