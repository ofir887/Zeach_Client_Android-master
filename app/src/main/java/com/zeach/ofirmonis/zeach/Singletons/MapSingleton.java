package com.zeach.ofirmonis.zeach.Singletons;

import com.google.android.gms.maps.model.LatLng;
import com.zeach.ofirmonis.zeach.Objects.Beach;
import com.zeach.ofirmonis.zeach.Objects.User;

import java.util.ArrayList;

/**
 * Created by ofirmonis on 24/02/2018.
 */

public class MapSingleton {

    private static MapSingleton mInstance;

    private ArrayList<Beach> mBeaches;
    private LatLng mUserLocation;
    private User mUser;
    private boolean mUserDetailesReceived;
    private boolean mBeachesDetailsReceived;
    private boolean mUserLocationRecieved;


    public MapSingleton() {
        mBeaches = new ArrayList<>();
        mUser = new User();
    }

    public static void createInstance() {
        mInstance = new MapSingleton();
    }

    public static MapSingleton getInstance() {
        if (mInstance == null) {
            return null;
        }
        return mInstance;
    }

    public void updateBeaches(ArrayList<Beach> aBeaches, boolean aReceived) {
        mBeaches = aBeaches;
        mBeachesDetailsReceived = aReceived;
    }

    public void updateUser(User aUser) {
        mUser = aUser;
        ;
    }

    public void updateUser(User aUser, boolean aReceived) {
        mUser = aUser;
        mUserDetailesReceived = aReceived;
    }

    public void updateUserLocation(LatLng aUserLocation, boolean aReceived) {
        mUserLocation = aUserLocation;
        mUserLocationRecieved = aReceived;
    }

    public ArrayList<Beach> getmBeaches() {
        return mBeaches;
    }

    public LatLng getmUserLocation() {
        return mUserLocation;
    }

    public User getmUser() {
        return mUser;
    }

    public boolean ismUserDetailesReceived() {
        return mUserDetailesReceived;
    }

    public boolean ismBeachesDetailsReceived() {
        return mBeachesDetailsReceived;
    }

    public boolean ismUserLocationRecieved() {
        return mUserLocationRecieved;
    }
}
