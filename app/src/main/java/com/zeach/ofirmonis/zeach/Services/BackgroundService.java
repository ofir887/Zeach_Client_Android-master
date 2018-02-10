package com.zeach.ofirmonis.zeach.Services;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zeach.ofirmonis.zeach.AppController;
import com.zeach.ofirmonis.zeach.Constants.FirebaseConstants;
import com.zeach.ofirmonis.zeach.GpsHelper.RayCast;
import com.zeach.ofirmonis.zeach.Objects.Beach;
import com.zeach.ofirmonis.zeach.Objects.Friend;
import com.zeach.ofirmonis.zeach.Objects.UserAtBeach;
import com.zeach.ofirmonis.zeach.Objects.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TreeMap;

import com.google.gson.Gson;


/**
 * Created by ofirmonis on 27/05/2017.
 */

public class BackgroundService extends Service {

    private static final String TAG = BackgroundService.class.getSimpleName();
    public static final int ID = 0;
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000 * 30;
    private static final float LOCATION_DISTANCE = 0;
    private Timer timer;
    private ArrayList<Beach> beaches;
    private DatabaseReference data;
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private User mUser;
    private LatLng mLocation;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;

    ////
    private static final String ACTION_STRING_SERVICE = "ToService";
    private static final String ACTION_STRING_ACTIVITY = "ToActivity";
    private static final String ACTION_BEACHES = "Beaches";
    private static final String ACTION_USER = "User";
    private static final String GPS = "GPS";
    private static final String ACTION_DELETE_FRIEND = "deleteFriend";
    private static final String ACTION_CONFIRM_FRIEND = "confirmFriend";

    private BroadcastReceiver serviceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                /*case ACTION_USER:
                    Log.d(TAG, "received gps request from receiver");
                    //getSingleLocationUpdate();
                    //   sendBroadcast(ACTION_USER);
                    break;*/
                /*case ACTION_BEACHES:
                    Log.d(TAG, "Received Beaches request from Map Fragment. Sending Beaches..");
                    if (beaches.size() > 0) {
                        sendBroadcast(ACTION_BEACHES);
                    }
                    break;*/
                case ACTION_DELETE_FRIEND:
                    String friendUid = intent.getStringExtra("UID");
                    Log.d(TAG, String.format("Received: [%s]", friendUid));
                    deleteFriendFromList(friendUid);
                    break;

                case ACTION_CONFIRM_FRIEND:
                    String friendjson = intent.getStringExtra("Friend");
                    Type type = new TypeToken<Friend>() {
                    }.getType();
                    Gson gson = new Gson();
                    Friend friend = gson.fromJson(friendjson, type);
                    Log.d(TAG, String.format("Received: [%s]", friend));
                    addUserAsFriend(friend);
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

        public String getCountryByCoords(LatLng mUserCoords) {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(mUserCoords.latitude, mUserCoords.longitude, 1);
                Address result;

                if (addresses != null && !addresses.isEmpty()) {
                    Log.e(TAG, "country: " + addresses.get(0).getCountryName());
                    return addresses.get(0).getCountryName();
                }
                return null;
            } catch (IOException ignored) {
                //do something
            }
            return null;

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
                // if (isUserInBeach) {
                //Asign user in this beach and break loop after it
                Log.d(TAG, "ofir is here fucking worikng !!!");
                updateUserInBeach(beaches.get(i), mFirebaseUser.getUid(), userCurrentLocation.longitude, userCurrentLocation.latitude);
                break;
                //  onDestroy();

                //   }
            }
            // onDestroy();

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

        return START_NOT_STICKY;
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    public void updateUserInBeach(final Beach beach, final String userId, double aLongitude, double aLatitude) {
        final DatabaseReference ref = data.getDatabase().getReference();
        long timeStamp = System.currentTimeMillis() / 1000;
        final UserAtBeach userAtBeach = new UserAtBeach(beach.getBeachName(), beach.getBeachKey(),
                beach.getBeachListenerID(), timeStamp, beach.getCountry(), aLongitude, aLatitude);
        final Friend user = new Friend(mUser.getName(), userId, mUser.getProfilePictureUri(), userAtBeach, mUser.isProfilePrivate());
        Log.d(TAG, "User with beach coords = " + user.toString());
        DatabaseReference userRef = ref.child("Beaches").child(beach.getBeachKey()).child("Peoplelist");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(user.getUID())) {
                    Log.d(TAG, "User not in the beach..Updating");
                    ref.child("BeachesListener").child(beach.getBeachListenerID()).child("CurrentDevices").setValue(beach.getCurrentDevices() + 1);
                    ref.child("Beaches").child(beach.getBeachKey()).child("Peoplelist").child(user.getUID()).setValue(user);
                } else {
                    ref.child("Beaches").child(beach.getBeachKey()).child("Peoplelist").child(user.getUID()).
                            child("currentBeach").child("mTimeStamp").setValue(userAtBeach.getmTimeStamp());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //update time stamp
        ref.child("Users").child(userId).child("currentBeach").setValue(userAtBeach);
        ref.child(FirebaseConstants.TIMESTAMPS).child(userId).setValue(userAtBeach);
    }

    public void deleteFriendFromList(String aFriendUid) {
        DatabaseReference ref = data.getDatabase().getReference();
        ref.child(FirebaseConstants.USERS).child(mUser.getUID()).child(FirebaseConstants.FRIENDS_LIST).child(aFriendUid).removeValue();
        ref.child(FirebaseConstants.USERS).child(aFriendUid).child(FirebaseConstants.FRIENDS_LIST).child(mUser.getUID()).removeValue();
        Log.d(TAG, String.format("Friend: [%s] removed from friends list. refreshing beaches..", aFriendUid));
        //  getBeachesFromFirebase();
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

    public void getBeachesFromFirebase() {
        beaches.clear();
        final DatabaseReference ref = data.getDatabase().getReference("Beaches");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                beaches.clear();
                //TODO - change string to constants (beachid and...)
                for (final DataSnapshot beach : dataSnapshot.getChildren()) {
                    String mBeachKey = (String) beach.child("BeachID").getValue();
                    String mBeachName = (String) beach.child("BeachName").getValue();
                    String mBeachListenerID = (String) beach.child("BeachListenerID").getValue();
                    String mTraffic = (String) beach.child(FirebaseConstants.TRAFFIC).getValue();
                    String mCountry = (String) beach.child("Country").getValue();
                    int currentDevices = (int) (long) beach.child("CurrentDevices").getValue();
                    //get Friends
                    final ArrayList<Friend> friends = new ArrayList<Friend>();
                    if (!mUser.isProfilePrivate() && beach.child("Peoplelist").exists()) {
                        final DatabaseReference userRef = data.getDatabase().getReference("Users");
                        for (final Map.Entry<String, Friend> entry : mUser.getFriendsList().entrySet()) {
                            if (beach.child("Peoplelist").hasChild(entry.getKey())) {
                                boolean privateProfile = beach.child("Peoplelist").child(entry.getKey()).child("profilePrivate").getValue(boolean.class);
                                if (!privateProfile) {
                                    Log.d(TAG, "Found Friend: " + beach.child("Peoplelist").child(entry.getKey()).getValue());
                                    String name = (String) beach.child("Peoplelist").child(entry.getKey()).child("name").getValue();
                                    String uid = (String) beach.child("Peoplelist").child(entry.getKey()).child("uid").getValue();
                                    String photoUrl = (String) beach.child("Peoplelist").child(entry.getKey()).child("photoUrl").getValue();
                                    UserAtBeach userAtBeach = beach.child("Peoplelist").child(entry.getKey()).child("currentBeach").getValue(UserAtBeach.class);
                                    Friend friend = new Friend(name, uid, photoUrl, userAtBeach, privateProfile);
                                    friends.add(friend);
                                }
                            }
                        }

                    }
                    Log.d(TAG, "Friends = " + friends.toString());
                    HashMap<String, HashMap<String, Double>> mBeachCoords = (HashMap<String, HashMap<String, Double>>)
                            beach.child("Coords").getValue();
                    Map<String, HashMap<String, Double>> map = new TreeMap<String, HashMap<String, Double>>(mBeachCoords);

                    ArrayList<LatLng> beachCoords = new ArrayList<LatLng>();
                    for (Map.Entry<String, HashMap<String, Double>> entry : map.entrySet()) {
                        HashMap<String, Double> coords = entry.getValue();
                        LatLng latlng = new LatLng(coords.get("lat"), coords.get("lng"));
                        beachCoords.add(latlng);
                        Log.d("Beach1", latlng.toString());
                    }

                    Beach beach1 = new Beach(mBeachKey, mBeachListenerID, beachCoords, mBeachName, friends, mTraffic, mCountry, currentDevices);
                    if (beaches.contains(beach1)) {
                        beaches.set(beaches.indexOf(beach1), beach1);
                    } else {
                        beaches.add(beach1);
                    }
                    //    Handler handler = new Handler();
                    //    Runnable runnable = new Runnable() {
                    //       @Override
                    //      public void run() {
                    Log.d("Beach1", beach1.toString());
                    sendBroadcast(ACTION_BEACHES);
                    //        }
                    //      };
                    //     handler.postDelayed(runnable, 1000);
                    //sendBroadcast(ACTION_BEACHES);
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
                mUser = dataSnapshot.getValue(User.class);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("User", mUser.toString());
                    //TODO fix asyncTask
                    PreferenceManager.getDefaultSharedPreferences(getApplication()).edit().putString("user", jsonObject.toString()).apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                sendBroadcast(ACTION_USER);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void addUserAsFriend(Friend aFriend) {
        DatabaseReference ref = data.getDatabase().getReference();
        Friend user = new Friend(mUser.getName(), mUser.getUID(), mUser.getProfilePictureUri());
        ref.child(FirebaseConstants.USERS).child(mUser.getUID()).child(FirebaseConstants.FRIENDS_LIST).child(aFriend.getUID()).setValue(aFriend);
        ref.child(FirebaseConstants.USERS).child(aFriend.getUID()).child(FirebaseConstants.FRIENDS_LIST).child(mUser.getUID()).setValue(user);
        Log.d(TAG, String.format("Friend: [%s] added to friends list. refreshing beaches..", aFriend));
        Log.d(TAG, String.format("Removing request after adding"));
        ref.child(FirebaseConstants.USERS).child(mUser.getUID()).child(FirebaseConstants.FRIENDS_REQUESTS).child(aFriend.getUID()).removeValue();
        ref.child(FirebaseConstants.USERS).child(aFriend.getUID()).child(FirebaseConstants.AWAITING_CONFIRMATION).child(mUser.getUID()).removeValue();
        //   getBeachesFromFirebase();
    }


    public static String tempFileImage(Context context, Bitmap bitmap, String name) {

        File outputDir = context.getCacheDir();
        File imageFile = new File(outputDir, name + ".jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(context.getClass().getSimpleName(), "Error writing file", e);
        }

        return imageFile.getAbsolutePath();
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
            intentFilter.addAction(ACTION_DELETE_FRIEND);
            intentFilter.addAction(ACTION_CONFIRM_FRIEND);
            registerReceiver(serviceReceiver, intentFilter);
        }
        //
        FirebaseApp.initializeApp(this);
        this.data = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();
        beaches = new ArrayList<Beach>();
        getUserDetailsFromServer();

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (checkPermission(getApplicationContext())) {
                    getBeachesFromFirebase();
                    initializeLocationManager();
                    //     getSingleLocationUpdate();
                    getMultipleLocationUpdates();
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
                Log.d(TAG, "Sending beach to map fragment" + arr.toString());
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
        super.onDestroy();
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
