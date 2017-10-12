package com.zeach.ofirmonis.zeach.Services;

import android.Manifest;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zeach.ofirmonis.zeach.AppSavedObjects;
import com.zeach.ofirmonis.zeach.Constants.GpsConstants;
import com.zeach.ofirmonis.zeach.GpsHelper.RayCast;
import com.zeach.ofirmonis.zeach.Objects.Beach;
import com.zeach.ofirmonis.zeach.Objects.Friend;
import com.zeach.ofirmonis.zeach.Objects.UserAtBeach;
import com.zeach.ofirmonis.zeach.Objects.ZeachUser;
import com.zeach.ofirmonis.zeach.Singletons.Beaches;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

/**
 * Created by ofirmonis on 27/05/2017.
 */

public class BackgroundService extends Service {

    private static final String TAG = BackgroundService.class.getSimpleName();
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 0;
    private Timer timer;
    private ArrayList<Beach> beaches;
    private DatabaseReference data;
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private ZeachUser mUser;


    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            LatLng userCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());
            mLastLocation.set(location);
            updateUserInBeach(beaches.get(0), mFirebaseUser.getUid());
            for (int i = 0; i < beaches.size(); i++) {
                boolean isUserInBeach = RayCast.isLatLngInside(beaches.get(i).getBeachCoordinates(), userCurrentLocation);
                if (isUserInBeach) {
                    //Asign user in this beach and break loop after it

                }
            }
            //   Intent i = new Intent("location_update");
            //   i.putExtra("coordinates",userCurrentLocation);
            //   sendBroadcast(i);
            //   mLastLocation.set(location);
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


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");

        return START_STICKY;
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    public void updateUserInBeach(Beach beach, String userId) {
        DatabaseReference ref = data.getDatabase().getReference();
        Friend user = new Friend(mUser.getName(), userId, mUser.getProfilePictureUri());
        //TODO add support for reading beach json at user current beach
        UserAtBeach userAtBeach = new UserAtBeach(beach.getBeachName(), beach.getBeachKey());
        ref.child("BeachesListener/Country/Israel").child(beach.getBeachListenerID()).child("CurrentPeople").setValue(beach.getCurrentPeople() + 1);
        ref.child("Beaches/Country/Israel").child(beach.getBeachKey()).child("Peoplelist").child(user.getUID()).setValue(user);
        ref.child("Users").child(userId).child("currentBeach").setValue(beach.getBeachKey());

    }

    public void getSingleLocationUpdate() {
        // initializeLocationManager();
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

    public void getMultipleLocationUpdates() {
        // initializeLocationManager();
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

    public static boolean checkPermission(final Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void getBeachesFromFirebase() {
        DatabaseReference ref = data.getDatabase().getReference("Beaches/Country/Israel");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot beach : dataSnapshot.getChildren()) {
                    String mBeachKey = (String) beach.child("BeachID").getValue();
                    String mBeachName = (String) beach.child("BeachName").getValue();
                    String mBeachListenerID = (String) beach.child("BeachListenerID").getValue();
                    long currentPeople = (long) beach.child("Current People").getValue();
                    long currentOccupationEstimation = (long) beach.child("Result").getValue();
                    long res = (long) currentOccupationEstimation;
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
                    final Beach beach1 = new Beach(mBeachKey, mBeachListenerID, res, beachCoords, mBeachName, currentPeople);
                    beaches.add(beach1);
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

    public void getUserDetailsFromServer() {
        DatabaseReference mUserRef = data.getDatabase().getReference("Users/" + mAuth.getCurrentUser().getUid());
        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUser = dataSnapshot.getValue(ZeachUser.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onCreate() {

        Log.e(TAG, "onCreate");
        FirebaseApp.initializeApp(this);
        this.data = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        getUserDetailsFromServer();
        beaches = new ArrayList<Beach>();
        getBeachesFromFirebase();

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (checkPermission(getApplicationContext())) {
                    initializeLocationManager();
                    getSingleLocationUpdate();
                }
            }
        };
        handler.postDelayed(runnable, 1000 * 5);


    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();

        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }
}
