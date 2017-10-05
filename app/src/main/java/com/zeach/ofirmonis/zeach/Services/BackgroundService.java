package com.zeach.ofirmonis.zeach.Services;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zeach.ofirmonis.zeach.Objects.Beach;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ofirmonis on 05/10/2017.
 */

public class BackgroundService extends Service {

    private static final String TAG = "ZeachGpsService";
    public static final int ID = 1;
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000 * 30;
    private static final float LOCATION_DISTANCE = 0;
    private ArrayList<Beach> beaches;
    private DatabaseReference data;
    private Location location;
    private Timer timer;


    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        public String getCountry(Location location) {
            Geocoder geocoder = new Geocoder(getApplication());
            try {
                List<Address> address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (address.size() > 0) {
                    return address.get(0).getCountryName();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            LatLng userCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());
            //this.location = location;
            //getCountry(location); not working
            /*//remove location update
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }*/


        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };


    public void getBeachesFromFirebase() {
        DatabaseReference ref = data.getDatabase().getReference("Beaches/Country/Israel");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot beach : dataSnapshot.getChildren()) {
                    String mBeachKey = (String) beach.child("BeachID").getValue();
                    String mBeachName = (String) beach.child("BeachName").getValue();
                    String mBeachListenerID = (String) beach.child("BeachListenerID").getValue();
                    long currentOccupationEstimation = (long) beach.child("Result").getValue();
                    // int beachMaxCapacity = (int)beach.child("Capacity").getValue();

                    HashMap<String, HashMap<String, Double>> mBeachCoords = (HashMap<String, HashMap<String, Double>>)
                            beach.child("Coords").getValue();
                    ArrayList<LatLng> beachCoords = new ArrayList<LatLng>();
                    for (Map.Entry<String, HashMap<String, Double>> entry : mBeachCoords.entrySet()) {
                        HashMap<String, Double> coords = entry.getValue();
                        LatLng latlng = new LatLng(coords.get("lat"), coords.get("lng"));
                        beachCoords.add(latlng);
                        Log.d("Beach1", latlng.toString());
                    }
                    final Beach beach1 = new Beach(mBeachKey, mBeachListenerID, currentOccupationEstimation, beachCoords, mBeachName);
                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            Log.d("Beach1", beach1.toString());
                        }
                    };
                    handler.postDelayed(runnable, 1000);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");

        this.mLocationListeners = mLocationListeners;
        beaches = new ArrayList<>();
        FirebaseApp.initializeApp(this);
        this.data = FirebaseDatabase.getInstance().getReference();
        getBeachesFromFirebase();

        getLocation();

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                onDestroy();
            }
        };
        handler.postDelayed(runnable, 1000 * 45);



        //


        return START_NOT_STICKY;
    }


    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    public void getLocation2() {
        initializeLocationManager();
        Looper looper = null;
        try {
            mLocationManager.requestSingleUpdate(
                    LocationManager.NETWORK_PROVIDER, mLocationListeners[1], looper);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestSingleUpdate(
                    "gps", mLocationListeners[0], looper);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    public void getLocation() {
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    "gps", LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onCreate() {
        // getLocation();
        Log.e(TAG, "onCreate");
        //initializeLocationManager();
        LocationListener[] mLocationListeners2 = new LocationListener[]{
                new LocationListener(LocationManager.GPS_PROVIDER),
                new LocationListener(LocationManager.NETWORK_PROVIDER)
        };
        this.mLocationListeners = mLocationListeners2;
        getLocation();

    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        /*f (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }*/

        Process.killProcess(Process.myPid());

        super.onDestroy();


    }
}
