package com.zeach.ofirmonis.zeach.Services;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
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
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zeach.ofirmonis.zeach.Constants.IntentExtras;
import com.zeach.ofirmonis.zeach.Singletons.AppController;
import com.zeach.ofirmonis.zeach.Constants.FirebaseConstants;
import com.zeach.ofirmonis.zeach.GpsHelper.RayCast;
import com.zeach.ofirmonis.zeach.Objects.Beach;
import com.zeach.ofirmonis.zeach.Objects.FavoriteBeach;
import com.zeach.ofirmonis.zeach.Objects.Friend;
import com.zeach.ofirmonis.zeach.Objects.UserAtBeach;
import com.zeach.ofirmonis.zeach.Objects.User;
import com.zeach.ofirmonis.zeach.Singletons.MapSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TreeMap;

import static com.zeach.ofirmonis.zeach.Constants.Actions.ACTION_ADD_FAVORITE_BEACH;
import static com.zeach.ofirmonis.zeach.Constants.Actions.ACTION_ADD_FRIEND_REQUEST;
import static com.zeach.ofirmonis.zeach.Constants.Actions.ACTION_BEACHES;
import static com.zeach.ofirmonis.zeach.Constants.Actions.ACTION_CONFIRM_FRIEND;
import static com.zeach.ofirmonis.zeach.Constants.Actions.ACTION_DELETE_FRIEND;
import static com.zeach.ofirmonis.zeach.Constants.Actions.ACTION_NEAREST_BEACH;
import static com.zeach.ofirmonis.zeach.Constants.Actions.ACTION_RECEIVE_FAVORITE_BEACHES;
import static com.zeach.ofirmonis.zeach.Constants.Actions.ACTION_REMOVE_FAVORITE_BEACH;
import static com.zeach.ofirmonis.zeach.Constants.Actions.ACTION_REQUEST_BEACHES;
import static com.zeach.ofirmonis.zeach.Constants.Actions.ACTION_REQUEST_FAVORITE_BEACHES;
import static com.zeach.ofirmonis.zeach.Constants.Actions.ACTION_REQUEST_USER;
import static com.zeach.ofirmonis.zeach.Constants.Actions.ACTION_SHUT_DOWN_BACKGROUND_ACTIVITY;
import static com.zeach.ofirmonis.zeach.Constants.Actions.ACTION_STRING_ACTIVITY;
import static com.zeach.ofirmonis.zeach.Constants.Actions.ACTION_STRING_SERVICE;
import static com.zeach.ofirmonis.zeach.Constants.Actions.ACTION_UPDATE_USER_FEEDBACK;
import static com.zeach.ofirmonis.zeach.Constants.Actions.ACTION_UPDATE_USER_PREFERENCES;
import static com.zeach.ofirmonis.zeach.Constants.Actions.ACTION_UPDATE_USER_PROFILE;
import static com.zeach.ofirmonis.zeach.Constants.Actions.ACTION_USER;
import static com.zeach.ofirmonis.zeach.Constants.FirebaseConstants.ACCURATE;
import static com.zeach.ofirmonis.zeach.Constants.FirebaseConstants.AWAITING_CONFIRMATION;
import static com.zeach.ofirmonis.zeach.Constants.FirebaseConstants.BEACHES;
import static com.zeach.ofirmonis.zeach.Constants.FirebaseConstants.BEACHES_LISTENER;
import static com.zeach.ofirmonis.zeach.Constants.FirebaseConstants.BEACHES_NAME;
import static com.zeach.ofirmonis.zeach.Constants.FirebaseConstants.BEACH_ID;
import static com.zeach.ofirmonis.zeach.Constants.FirebaseConstants.BEACH_LISTENER_ID;
import static com.zeach.ofirmonis.zeach.Constants.FirebaseConstants.COORDS;
import static com.zeach.ofirmonis.zeach.Constants.FirebaseConstants.COUNTRY;
import static com.zeach.ofirmonis.zeach.Constants.FirebaseConstants.CURRENT_BEACH;
import static com.zeach.ofirmonis.zeach.Constants.FirebaseConstants.CURRENT_DEVICES;
import static com.zeach.ofirmonis.zeach.Constants.FirebaseConstants.EASY_TO_USE;
import static com.zeach.ofirmonis.zeach.Constants.FirebaseConstants.FEEDBACK;
import static com.zeach.ofirmonis.zeach.Constants.FirebaseConstants.FRIENDS_REQUESTS;
import static com.zeach.ofirmonis.zeach.Constants.FirebaseConstants.LATITUDE;
import static com.zeach.ofirmonis.zeach.Constants.FirebaseConstants.LONGITUDE;
import static com.zeach.ofirmonis.zeach.Constants.FirebaseConstants.NAME;
import static com.zeach.ofirmonis.zeach.Constants.FirebaseConstants.PEOPLE_LIST;
import static com.zeach.ofirmonis.zeach.Constants.FirebaseConstants.PHOTO_URL;
import static com.zeach.ofirmonis.zeach.Constants.FirebaseConstants.PRIVATE_PROFILE;
import static com.zeach.ofirmonis.zeach.Constants.FirebaseConstants.RATING;
import static com.zeach.ofirmonis.zeach.Constants.FirebaseConstants.USERS;
import static com.zeach.ofirmonis.zeach.Constants.FirebaseConstants.USER_ID;
import static com.zeach.ofirmonis.zeach.Constants.FirebaseConstants.USER_TIMESTAMP;
import static com.zeach.ofirmonis.zeach.Constants.IntentExtras.NEAREST_BEACH;
import static com.zeach.ofirmonis.zeach.Constants.IntentExtras.RECEIVE_FAVORITE;


/**
 * Created by ofirmonis on 27/05/2017.
 */

public class BackgroundService extends Service {

    private static final String TAG = BackgroundService.class.getSimpleName();
    public static final int ID = 0;
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000 * 15;
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
    private String mNearestBeach;
    private boolean mBackgroundActivity;


    private BroadcastReceiver serviceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Type type;
            Gson gson = new Gson();
            Friend friend;
            switch (intent.getAction()) {
                case ACTION_REQUEST_BEACHES:
                    Log.i(TAG, "Received Beaches request from Map Fragment. Sending Beaches..");
                    sendBroadcast(ACTION_BEACHES);
                    break;
                case ACTION_REQUEST_USER:
                    sendBroadcast(ACTION_USER);
                    break;
                case ACTION_DELETE_FRIEND:
                    String friendUid = intent.getStringExtra(IntentExtras.UID);
                    Log.i(TAG, String.format("Received: [%s]", friendUid));
                    deleteFriendFromList(friendUid);
                    break;

                case ACTION_CONFIRM_FRIEND:
                    String friendjson = intent.getStringExtra(IntentExtras.FRIEND);
                    type = new TypeToken<Friend>() {
                    }.getType();
                    friend = gson.fromJson(friendjson, type);
                    Log.i(TAG, String.format("Received: [%s]", friend));
                    addUserAsFriend(friend);
                    break;
                case ACTION_ADD_FAVORITE_BEACH: {
                    String favoriteBeachString = intent.getStringExtra(IntentExtras.FAVORITE_BEACH);
                    type = new TypeToken<FavoriteBeach>() {
                    }.getType();
                    FavoriteBeach favoriteBeach = gson.fromJson(favoriteBeachString, type);
                    Log.i(TAG, "Favorite beach to add was send " + favoriteBeach);
                    addFavoriteBeach(favoriteBeach);
                    break;
                }
                case ACTION_REQUEST_FAVORITE_BEACHES:
                    Log.i(TAG, "Received user favorite beaches request");
                    sendUserFavoriteBeaches();
                    break;
                case ACTION_REMOVE_FAVORITE_BEACH:
                    Log.i(TAG, "Remove favorite beach request received.");
                    String beachKey = intent.getStringExtra(IntentExtras.FAVORITE_BEACH);
                    deleteFavoriteBeach(beachKey);
                    break;
                case ACTION_ADD_FRIEND_REQUEST:
                    friend = (Friend) intent.getSerializableExtra(IntentExtras.FRIEND);
                    Log.i(TAG, String.format("Friend request message received:[%s]", friend));
                    AddFriendRequest(friend);
                    break;
                case ACTION_UPDATE_USER_PREFERENCES:
                    boolean importFacebookFriends = intent.getBooleanExtra(IntentExtras.ADD_FRIENDS_FACEBOOK, false);
                    boolean isUserPrivate = intent.getBooleanExtra(IntentExtras.PROFILE_PRIVATE, false);
                    Log.i(TAG, String.format("User Preferences Update Request Received. import from facebook:[%b], private profile:[%b]",
                            importFacebookFriends, isUserPrivate));
                    updateUserPreferences(importFacebookFriends, isUserPrivate);
                    break;
                case ACTION_UPDATE_USER_PROFILE:
                    String name = intent.getStringExtra(IntentExtras.NAME);
                    String photoUrl = intent.getStringExtra(IntentExtras.PHOTO_URL);
                    Log.i(TAG, String.format("Update User Profile message request received. " +
                            "name:[%s], photo url:[%s]", name, photoUrl));
                    updateUserProfile(name, photoUrl);
                    break;
                case ACTION_UPDATE_USER_FEEDBACK:
                    boolean accurate = intent.getBooleanExtra(IntentExtras.ACCURATE, true);
                    boolean easyToUse = intent.getBooleanExtra(IntentExtras.EASY_TO_USE, true);
                    float rating = intent.getFloatExtra(IntentExtras.RATING, 0);
                    Log.i(TAG, String.format("Feedback received. accurate:[%b], easy to use:[%b], rating:[%f]", accurate, easyToUse, rating));
                    updateUserFeedback(accurate, easyToUse, rating);

            }
            //sendBroadcast();
        }
    };

    private void updateUserFeedback(boolean aAccurate, boolean aEasyToUse, float aRating) {
        data.child(FEEDBACK).child(mUser.getUID()).child(ACCURATE).setValue(aAccurate);
        data.child(FEEDBACK).child(mUser.getUID()).child(EASY_TO_USE).setValue(aEasyToUse);
        data.child(FEEDBACK).child(mUser.getUID()).child(RATING).setValue(aRating);
    }

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            Log.i(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        public String getCountryByCoords(LatLng mUserCoords) {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(mUserCoords.latitude, mUserCoords.longitude, 1);
                Address result;

                if (addresses != null && !addresses.isEmpty()) {
                    Log.i(TAG, "country: " + addresses.get(0).getCountryName());
                    return addresses.get(0).getCountryName();
                }
                return null;
            } catch (IOException ignored) {
            }
            return null;

        }

        @Override
        public void onLocationChanged(Location location) {
            Log.i(TAG, "onLocationChanged: " + location);
            LatLng userCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());
            mLastLocation.set(location);
            mLocation = userCurrentLocation;
            //
            if (PreferenceManager.getDefaultSharedPreferences(getApplication()).getBoolean("isActive", false)) {
                Log.i(TAG, "Activity is active. sending broadcast...");
                sendBroadcast(ACTION_STRING_ACTIVITY);
            }
            //  updateUserInBeach(beaches.get(0), mFirebaseUser.getUid());
            for (int i = 0; i < beaches.size(); i++) {
                boolean isUserInBeach = RayCast.isLatLngInside(beaches.get(i).getBeachCoordinates(), userCurrentLocation);
                Log.i(TAG, "checking against:" + beaches.get(i).getBeachName());
                if (isUserInBeach) {
                    //Asign user in this beach and break loop after it
                    Log.i(TAG, "ofir is here fucking worikng !!!");
                    updateUserInBeach(beaches.get(i), mFirebaseUser.getUid(), userCurrentLocation.longitude, userCurrentLocation.latitude);
                    //break;
                } else {
                    mNearestBeach = findNearsetBeach(userCurrentLocation);
                    Log.i(TAG, String.format("Nearest beach found:[%s]", mNearestBeach));
                    sendBroadcast(ACTION_NEAREST_BEACH);
                }
            }
            if (mBackgroundActivity && beaches.size() > 0 && mUser != null) {
                Log.i(TAG, "Background Activity Started the service. Shutting Down...");
                sendBroadcast(ACTION_SHUT_DOWN_BACKGROUND_ACTIVITY);
            }

        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.i(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.i(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.i(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "Service binded");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        initializeLocationManager();
        getMultipleLocationUpdates();
        mBackgroundActivity = intent.getBooleanExtra(IntentExtras.BACKGROUND_ACTIVITY, false);
        return START_STICKY;
    }

    private void initializeLocationManager() {
        Log.i(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    private void addFavoriteBeach(FavoriteBeach aFavoriteBeach) {
        mUser.AddBeachToList(aFavoriteBeach);
        DatabaseReference ref = data.getDatabase().getReference();
        ref.child(USERS).child(mUser.getUID()).child(FirebaseConstants.FAVORITE_BEACHES).setValue(mUser.getFavoriteBeaches());
    }

    private void sendUserFavoriteBeaches() {
        // ArrayList <FavoriteBeach> favoriteBeaches = new ArrayList(mUser.getFavoriteBeaches().values());
        sendBroadcast(ACTION_RECEIVE_FAVORITE_BEACHES);
    }

    private void updateUserInBeach(final Beach beach, final String userId, double aLongitude, double aLatitude) {
        final DatabaseReference ref = data.getDatabase().getReference();
        long timeStamp = System.currentTimeMillis() / 1000;
        final UserAtBeach userAtBeach = new UserAtBeach(beach.getBeachName(), beach.getBeachKey(),
                beach.getBeachListenerID(), timeStamp, beach.getCountry(), aLongitude, aLatitude);
        final Friend user = new Friend(mUser.getName(), userId, mUser.getProfilePictureUri(), userAtBeach, mUser.isProfilePrivate());
        Log.i(TAG, "User with beach coords = " + user.toString());
        DatabaseReference userRef = ref.child(BEACHES).child(beach.getBeachKey()).child(PEOPLE_LIST);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(user.getUID())) {
                    Log.i(TAG, "User not in the beach..Updating");
                    ref.child(BEACHES_LISTENER).child(beach.getBeachListenerID()).child(CURRENT_DEVICES).setValue(beach.getCurrentDevices() + 1);
                    ref.child(BEACHES).child(beach.getBeachKey()).child(PEOPLE_LIST).child(user.getUID()).setValue(user);
                } else {
                    ref.child(BEACHES).child(beach.getBeachKey()).child(PEOPLE_LIST).child(user.getUID()).
                            child(CURRENT_BEACH).child(USER_TIMESTAMP).setValue(userAtBeach.getmTimeStamp());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //update time stamp
        ref.child(USERS).child(userId).child(CURRENT_BEACH).setValue(userAtBeach);
        ref.child(FirebaseConstants.TIMESTAMPS).child(userId).setValue(userAtBeach);
    }

    private void deleteFriendFromList(String aFriendUid) {
        DatabaseReference ref = data.getDatabase().getReference();
        ref.child(USERS).child(mUser.getUID()).child(FirebaseConstants.FRIENDS_LIST).child(aFriendUid).removeValue();
        ref.child(USERS).child(aFriendUid).child(FirebaseConstants.FRIENDS_LIST).child(mUser.getUID()).removeValue();
        Log.i(TAG, String.format("Friend: [%s] removed from friends list. refreshing beaches..", aFriendUid));
    }

    private void deleteFavoriteBeach(String aBeachId) {
        DatabaseReference ref = data.getDatabase().getReference();
        Log.i(TAG, String.format("Removing user favorite beach. Beach Id:[%s]", aBeachId));
        ref.child(USERS).child(mUser.getUID()).child(FirebaseConstants.FAVORITE_BEACHES).child(aBeachId).removeValue();
        // getUserDetailsFromServer();
    }

    private void AddFriendRequest(Friend friend) {
        DatabaseReference data = FirebaseDatabase.getInstance().getReference();
        //create awaiting confirmation on current user
        data.child(USERS).child(mUser.getUID()).child(AWAITING_CONFIRMATION).child(friend.getUID()).setValue(friend);
        //create awaiting confirmation on current user
        Friend destinationFriend = new Friend(mUser.getName(), mUser.getUID(), mUser.getProfilePictureUri());
        data.child(USERS).child(friend.getUID()).child(FRIENDS_REQUESTS).child(mUser.getUID()).setValue(destinationFriend);
    }

    private void getSingleLocationUpdate() {
        Looper looper = null;
        try {
            mLocationManager.requestSingleUpdate(
                    LocationManager.NETWORK_PROVIDER, mLocationListeners[1], looper);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.i(TAG, "network provider does not exist, " + ex.getMessage());
        }
        /*try {
            mLocationManager.requestSingleUpdate(
                    "gps", mLocationListeners[0], looper);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.i(TAG, "gps provider does not exist " + ex.getMessage());
        }*/
    }

    private void getSingleLocationUpdate2() {
        Looper looper = null;
        try {
            mLocationManager.requestSingleUpdate(
                    LocationManager.NETWORK_PROVIDER, mLocationListeners[1], looper);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.i(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestSingleUpdate(
                    "gps", mLocationListeners[0], looper);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.i(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    private void getMultipleLocationUpdates() {
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.i(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.i(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    private static boolean checkPermission(final Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void getBeachesFromFirebase() {
        beaches.clear();
        final DatabaseReference ref = data.getDatabase().getReference(BEACHES);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                beaches.clear();
                //TODO - change string to constants (beachid and...)
                for (final DataSnapshot beach : dataSnapshot.getChildren()) {
                    String mBeachKey = (String) beach.child(BEACH_ID).getValue();
                    String mBeachName = (String) beach.child(BEACHES_NAME).getValue();
                    String mBeachListenerID = (String) beach.child(BEACH_LISTENER_ID).getValue();
                    String mTraffic = (String) beach.child(FirebaseConstants.TRAFFIC).getValue();
                    String mCountry = (String) beach.child(COUNTRY).getValue();
                    int currentDevices = (int) (long) beach.child(CURRENT_DEVICES).getValue();
                    final ArrayList<Friend> friends = new ArrayList<Friend>();
                    if (PreferenceManager.getDefaultSharedPreferences(getApplication()).getBoolean("isActive", false)) {
                        //get Friends
                        if (mUser != null && !mUser.isProfilePrivate() && beach.child(PEOPLE_LIST).exists()) {
                            final DatabaseReference userRef = data.getDatabase().getReference(USERS);
                            for (final Map.Entry<String, Friend> entry : mUser.getFriendsList().entrySet()) {
                                if (beach.child(PEOPLE_LIST).hasChild(entry.getKey())) {
                                    boolean privateProfile = beach.child(PEOPLE_LIST).child(entry.getKey()).child(PRIVATE_PROFILE).getValue(boolean.class);
                                    if (!privateProfile) {
                                        Log.i(TAG, "Found Friend: " + beach.child(PEOPLE_LIST).child(entry.getKey()).getValue());
                                        String name = (String) beach.child(PEOPLE_LIST).child(entry.getKey()).child(NAME).getValue();
                                        String uid = (String) beach.child(PEOPLE_LIST).child(entry.getKey()).child(USER_ID).getValue();
                                        String photoUrl = (String) beach.child(PEOPLE_LIST).child(entry.getKey()).child(PHOTO_URL).getValue();
                                        UserAtBeach userAtBeach = beach.child(PEOPLE_LIST).child(entry.getKey()).child(CURRENT_BEACH).getValue(UserAtBeach.class);
                                        Friend friend = new Friend(name, uid, photoUrl, userAtBeach, privateProfile);
                                        friends.add(friend);
                                    }
                                }
                            }


                        }
                        Log.i(TAG, "Friends = " + friends.toString());
                    }
                    HashMap<String, HashMap<String, Double>> mBeachCoords = (HashMap<String, HashMap<String, Double>>)
                            beach.child(COORDS).getValue();
                    Map<String, HashMap<String, Double>> map = new TreeMap<String, HashMap<String, Double>>(mBeachCoords);

                    ArrayList<LatLng> beachCoords = new ArrayList<LatLng>();
                    for (Map.Entry<String, HashMap<String, Double>> entry : map.entrySet()) {
                        HashMap<String, Double> coords = entry.getValue();
                        LatLng latlng = new LatLng(coords.get(LATITUDE), coords.get(LONGITUDE));
                        beachCoords.add(latlng);
                        Log.i("Beach1", latlng.toString());
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
                    Log.i("Beach1", beach1.toString());
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

    private void updateUserPreferences(boolean aImportFriendsFromFacebook, boolean aPrivateProfile) {
        mUser.setImportFacebookFriends(aImportFriendsFromFacebook);
        mUser.setProfilePrivate(aPrivateProfile);
        DatabaseReference ref = data.getDatabase().getReference();
        ref.child(USERS).child(mUser.getUID()).setValue(mUser);
        if (mUser.getCurrentBeach().getmBeachID() != null) {
            ref.child(BEACHES).child(mUser.getCurrentBeach().getmBeachID()).
                    child(PEOPLE_LIST).child(mUser.getUID()).child(PRIVATE_PROFILE).setValue(mUser.isProfilePrivate());
        }
    }

    private void updateUserProfile(String aName, String aPhotoUrl) {
        mUser.setName(aName);
        mUser.setProfilePictureUri(aPhotoUrl);
        DatabaseReference ref = data.getDatabase().getReference();
        ref.child(USERS).child(mUser.getUID()).setValue(mUser);
    }

    private String findNearsetBeach(LatLng aUserLocation) {
        HashMap<String, LatLng> beachesCenter = new HashMap<>();
        for (int i = 0; i < beaches.size(); i++) {
            beachesCenter.put(beaches.get(i).getBeachKey(), computeCentreBeach(beaches.get(i).getBeachCoordinates()));
        }

        return computeDistance(aUserLocation, beachesCenter);
    }

    private String computeDistance(LatLng aUserLocation, HashMap<String, LatLng> beachLocation) {
        Location user = new Location("User");
        user.setLatitude(aUserLocation.latitude);
        user.setLongitude(aUserLocation.longitude);
        HashMap<String, Float> beachesDistance = new HashMap<>();
        for (Map.Entry<String, LatLng> entry : beachLocation.entrySet()) {
            Location location = new Location(entry.getKey());
            location.setLongitude(entry.getValue().longitude);
            location.setLatitude(entry.getValue().latitude);
            float distance = user.distanceTo(location);
            beachesDistance.put(entry.getKey(), distance);
        }
        float minDistance = Collections.min(beachesDistance.values());
        return getKeyFromValue(beachesDistance, minDistance);
    }

    private static String getKeyFromValue(Map hm, Object value) {
        for (Object o : hm.keySet()) {
            if (hm.get(o).equals(value)) {
                return (String) o;
            }
        }
        return null;
    }

    private LatLng computeCentreBeach(ArrayList<LatLng> points) {
        double latitude = 0;
        double longitude = 0;
        int n = points.size();

        for (LatLng point : points) {
            latitude += point.latitude;
            longitude += point.longitude;
        }

        return new LatLng(latitude / n, longitude / n);
    }

    private void getUserDetailsFromServer() {
        DatabaseReference mUserRef = data.getDatabase().getReference(String.format("%s/%s", USERS, mAuth.getCurrentUser().getUid()));
        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUser = dataSnapshot.getValue(User.class);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(IntentExtras.USER, mUser.toString());
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
        ref.child(USERS).child(mUser.getUID()).child(FirebaseConstants.FRIENDS_LIST).child(aFriend.getUID()).setValue(aFriend);
        ref.child(USERS).child(aFriend.getUID()).child(FirebaseConstants.FRIENDS_LIST).child(mUser.getUID()).setValue(user);
        Log.i(TAG, String.format("Friend: [%s] added to friends list. refreshing beaches..", aFriend));
        Log.i(TAG, String.format("Removing request after adding"));
        ref.child(USERS).child(mUser.getUID()).child(FRIENDS_REQUESTS).child(aFriend.getUID()).removeValue();
        ref.child(USERS).child(aFriend.getUID()).child(AWAITING_CONFIRMATION).child(mUser.getUID()).removeValue();
        //   getBeachesFromFirebase();
    }


    private static String tempFileImage(Context context, Bitmap bitmap, String name) {

        File outputDir = context.getCacheDir();
        File imageFile = new File(outputDir, name + ".jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.i(context.getClass().getSimpleName(), "Error writing file", e);
        }

        return imageFile.getAbsolutePath();
    }


    @Override
    public void onCreate() {

        Log.i(TAG, "onCreate");
        //
        if (serviceReceiver != null) {
            IntentFilter intentFilter = new IntentFilter(ACTION_STRING_SERVICE);
            intentFilter.addAction(ACTION_BEACHES);
            intentFilter.addAction(ACTION_USER);
            intentFilter.addAction(ACTION_DELETE_FRIEND);
            intentFilter.addAction(ACTION_CONFIRM_FRIEND);
            intentFilter.addAction(ACTION_ADD_FAVORITE_BEACH);
            intentFilter.addAction(ACTION_REQUEST_FAVORITE_BEACHES);
            intentFilter.addAction(ACTION_REMOVE_FAVORITE_BEACH);
            intentFilter.addAction(ACTION_NEAREST_BEACH);
            intentFilter.addAction(ACTION_REQUEST_USER);
            intentFilter.addAction(ACTION_ADD_FRIEND_REQUEST);
            intentFilter.addAction(ACTION_UPDATE_USER_PREFERENCES);
            intentFilter.addAction(ACTION_UPDATE_USER_PROFILE);
            intentFilter.addAction(ACTION_UPDATE_USER_FEEDBACK);
            intentFilter.addAction(ACTION_REQUEST_BEACHES);
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
                    //    initializeLocationManager();
                    //     getSingleLocationUpdate();
                    getMultipleLocationUpdates();
                }
            }
        };
        handler.postDelayed(runnable, 1000 * 5);

    }

    private void sendBroadcast(String action) {
        AppController.getInstance().setUser(mUser);
        Intent new_intent = new Intent();
        Gson gson = new Gson();
        switch (action) {
            case ACTION_BEACHES: {
                gson = new Gson();
                String arr = gson.toJson(beaches);
                new_intent.putExtra(IntentExtras.BEACHES, arr);
                new_intent.setAction(ACTION_BEACHES);
                Log.i(TAG, "Sending beach to map fragment" + arr.toString());
                break;
            }
            case ACTION_USER: {
                new_intent.putExtra(IntentExtras.USER, mUser);
                new_intent.setAction(ACTION_USER);
                Log.i(TAG, "onvcvxCreate");
                break;
            }
            case ACTION_STRING_ACTIVITY: {
                new_intent.putExtra(IntentExtras.LATITUDE, mLocation.latitude);
                new_intent.putExtra(IntentExtras.LONGITUDE, mLocation.longitude);
                new_intent.setAction(ACTION_STRING_ACTIVITY);
                break;
            }
            case ACTION_RECEIVE_FAVORITE_BEACHES: {
                Log.i(TAG, "sending favorite beaches");
                ArrayList<FavoriteBeach> favoriteBeaches = new ArrayList<>(mUser.getFavoriteBeaches().values());
                String favoriteBeachesString = gson.toJson(favoriteBeaches);
                Log.i(TAG, favoriteBeachesString);
                new_intent.putExtra(IntentExtras.FAVORITE_BEACHES, favoriteBeachesString);
                new_intent.setAction(RECEIVE_FAVORITE);
                break;
            }
            case ACTION_NEAREST_BEACH:
                Log.i(TAG, "Sending nearest beach to map..");
                new_intent.putExtra(NEAREST_BEACH, mNearestBeach);
                new_intent.setAction(action);
                break;
            case ACTION_SHUT_DOWN_BACKGROUND_ACTIVITY:
                new_intent.setAction(ACTION_SHUT_DOWN_BACKGROUND_ACTIVITY);
                break;
        }
        sendBroadcast(new_intent);


    }


    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
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
        super.onDestroy();
    }


}
