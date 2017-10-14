package com.zeach.ofirmonis.zeach.Services;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.Process;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.zeach.ofirmonis.zeach.Activities.MainActivity;
import com.zeach.ofirmonis.zeach.GpsHelper.RayCast;
import com.zeach.ofirmonis.zeach.Objects.Beach;
import com.zeach.ofirmonis.zeach.Objects.Friend;
import com.zeach.ofirmonis.zeach.Objects.UserAtBeach;
import com.zeach.ofirmonis.zeach.Objects.ZeachUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;


/**
 * Created by ofirmonis on 27/05/2017.
 */

public class BackgroundService extends Service {

    private static final String TAG = BackgroundService.class.getSimpleName();
    public static final int ID = 0;
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 0;
    private Timer timer;
    private ArrayList<Beach> beaches;
    private DatabaseReference data;
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private ZeachUser mUser;
    private LatLng mLocation;

    //
    private static final String ACTION_STRING_SERVICE = "ToService";
    private static final String ACTION_STRING_ACTIVITY = "ToActivity";
    private static final String ACTION_BEACHES = "Beaches";
    private static final String ACTION_USER = "User";
    private static final String GPS = "GPS";

    private BroadcastReceiver serviceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ACTION_USER:
                    Log.d(TAG, "received gps request from receiver");
                    //getSingleLocationUpdate();
                    //   sendBroadcast(ACTION_USER);
                    break;
            }

            //sendBroadcast();
        }
    };
    //


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
            mLocation = userCurrentLocation;
            //
            if (PreferenceManager.getDefaultSharedPreferences(getApplication()).getBoolean("isActive", true)) {
                Log.d(TAG, "Activity is active. sending broadcast...");
                sendBroadcast(ACTION_STRING_ACTIVITY);
            }
            //  updateUserInBeach(beaches.get(0), mFirebaseUser.getUid());
            for (int i = 0; i < beaches.size(); i++) {
                boolean isUserInBeach = RayCast.isLatLngInside(beaches.get(i).getBeachCoordinates(), userCurrentLocation);
                Log.d(TAG, "checking against:" + beaches.get(i).getBeachName());
                if (isUserInBeach) {
                    //Asign user in this beach and break loop after it
                    Log.d(TAG, "ofir is here fucking worikng !!!");
                    updateUserInBeach(beaches.get(0), mFirebaseUser.getUid());
                    //  onDestroy();

                }
            }
            onDestroy();

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
        Log.d(TAG, "Service binded");
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
        long timeStamp = System.currentTimeMillis() / 1000;
        UserAtBeach userAtBeach = new UserAtBeach(beach.getBeachName(), beach.getBeachKey(), timeStamp);
        ref.child("BeachesListener/Country/Israel").child(beach.getBeachListenerID()).child("CurrentPeople").setValue(beach.getCurrentPeople() + 1);
        ref.child("Beaches/Country/Israel").child(beach.getBeachKey()).child("Peoplelist").child(user.getUID()).setValue(user);
        ref.child("Users").child(userId).child("CurrentBeach").setValue(userAtBeach);

    }

    public void getSingleLocationUpdate() {
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

    private ArrayList<Friend> getFriendsOnBeach(HashMap<String, HashMap<String, Friend>> friendsInBeach) {
        final ArrayList<Friend> friends = new ArrayList<Friend>();
        for (Map.Entry<String, HashMap<String, Friend>> entry : friendsInBeach.entrySet()) {
            HashMap<String, Friend> friendHashMap = entry.getValue();
            String name = String.valueOf(friendHashMap.get("name"));
            String uid = String.valueOf(friendHashMap.get("uid"));
            String photoUrl = String.valueOf(friendHashMap.get("photoUrl"));
            Friend friend = new Friend(name, uid, photoUrl);
            //  for (Map.Entry<String, Friend> userEntry : mUser.getFriendsList().entrySet()){
            //       if (friend.getUID().equals(userEntry.getValue().getUID()))
            friends.add(friend);
            //  }
            Log.d(TAG, "Friend" + friend.toString());
        }
        return friends;
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
                    long currentPeople = (long) beach.child("CurrentPeople").getValue();
                    long currentOccupationEstimation = (long) beach.child("Result").getValue();
                    long res = (long) currentOccupationEstimation;
                    // int beachMaxCapacity = (int)beach.child("Capacity").getValue();
                    //get Friends
                    HashMap<String, HashMap<String, Friend>> friendsInBeach = (HashMap<String, HashMap<String, Friend>>) beach.child("Peoplelist").getValue();
                    //
                    /*//Todo change operation to firebase cloud query
                    final ArrayList<Friend> friends = new ArrayList<Friend>();
                    for (Map.Entry<String, HashMap<String, Friend>>entry : friendsInBeach.entrySet()){
                        HashMap<String,Friend> friendHashMap = entry.getValue();
                        String name = String.valueOf(friendHashMap.get("name"));
                        String uid = String.valueOf(friendHashMap.get("uid"));
                        String photoUrl = String.valueOf(friendHashMap.get("photoUrl"));
                        Friend friend = new Friend(name,uid,photoUrl);
                      //  for (Map.Entry<String, Friend> userEntry : mUser.getFriendsList().entrySet()){
                     //       if (friend.getUID().equals(userEntry.getValue().getUID()))
                                friends.add(friend);
                      //  }
                        Log.d(TAG,"Friend"+friend.toString());
                    }*/

                    //
                    HashMap<String, HashMap<String, Double>> mBeachCoords = (HashMap<String, HashMap<String, Double>>)
                            beach.child("Coords").getValue();
                    ArrayList<LatLng> beachCoords = new ArrayList<LatLng>();
                    for (Map.Entry<String, HashMap<String, Double>> entry : mBeachCoords.entrySet()) {
                        HashMap<String, Double> coords = entry.getValue();
                        LatLng latlng = new LatLng(coords.get("lat"), coords.get("lng"));
                        beachCoords.add(latlng);
                        Log.d("Beach1", latlng.toString());
                    }
                    final Beach beach1 = new Beach(mBeachKey, mBeachListenerID, res, beachCoords, mBeachName, 500, getFriendsOnBeach(friendsInBeach));
                    beaches.add(beach1);
                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            Log.d("Beach1", beach1.toString());
                        }
                    };
                    handler.postDelayed(runnable, 1000);
                    sendBroadcast(ACTION_BEACHES);
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
                sendBroadcast(ACTION_USER);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    @Override
    public void onCreate() {

        Log.e(TAG, "onCreate");
        //
        if (serviceReceiver != null) {
            IntentFilter intentFilter = new IntentFilter(ACTION_STRING_SERVICE);
            intentFilter.addAction(ACTION_BEACHES);
            intentFilter.addAction(ACTION_USER);
            intentFilter.addAction(GPS);
            registerReceiver(serviceReceiver, intentFilter);
        }
        //
        FirebaseApp.initializeApp(this);
        this.data = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();

        getUserDetailsFromServer();
        Handler beachHandler = new Handler();
        Runnable beachRunnable = new Runnable() {
            @Override
            public void run() {
                beaches = new ArrayList<Beach>();
                getBeachesFromFirebase();
            }
        };
        beachHandler.postDelayed(beachRunnable, 1000 * 10);

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
        handler.postDelayed(runnable, 1000 * 15);


    }

    private void sendBroadcast(String action) {
        Intent new_intent = new Intent();
        switch (action) {
            case ACTION_BEACHES: {
                Gson gson = new Gson();
                String arr = gson.toJson(beaches);
                new_intent.putExtra("beaches", arr);
                new_intent.setAction(ACTION_BEACHES);
                break;
            }
            case ACTION_USER: {
                new_intent.putExtra("User", mUser);
                new_intent.setAction(ACTION_USER);
                Log.e(TAG, "onvcvxCreate");
                break;
            }
            case ACTION_STRING_ACTIVITY: {
                new_intent.putExtra("lat", mLocation.latitude);
                new_intent.putExtra("lng", mLocation.longitude);
                new_intent.setAction(ACTION_STRING_ACTIVITY);
                break;
            }
        }
        sendBroadcast(new_intent);


    }


    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        //super.onDestroy();
        if (PreferenceManager.getDefaultSharedPreferences(getApplication()).getBoolean("isActive", true))
            unregisterReceiver(serviceReceiver);
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listener, ignore", ex);
                }
            }
        }
        Process.killProcess(Process.myPid());
    }


}
