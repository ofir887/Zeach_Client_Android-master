package com.zeach.ofirmonis.zeach.Objects;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ofirmonis on 23/06/2017.
 */

public class Beach implements Serializable {
    private String BeachName;
    private String BeachKey;
    private String BeachListenerID;
    private ArrayList<LatLng> BeachCoords;
    private ArrayList<Friend> mFriends;
    private long CurrentDevices;
    private String mTraffic;
    private String Country;

    //Constructor for using service
    public Beach(String beachName, String beachListenerID, ArrayList beachCoords) {
        this.BeachName = beachName;
        this.BeachListenerID = beachListenerID;
        this.BeachCoords = beachCoords;
        this.mFriends = new ArrayList<>();


    }

    public Beach(String key, String beachListenerID, ArrayList<LatLng> beachCoords, String beachName, ArrayList<Friend> friends, String aTraffic, String aCountry) {
        this.BeachKey = key;
        this.BeachListenerID = beachListenerID;
        this.BeachCoords = beachCoords;
        this.BeachName = beachName;
        this.mFriends = friends;
        mTraffic = aTraffic;
        Country = aCountry;
    }

    //Constructor for using in app
    public Beach(String beachName, String beachListenerID, ArrayList<LatLng> beachCoords, ArrayList<Friend> friends) {
        this.BeachName = beachName;
        this.BeachListenerID = beachListenerID;
        this.BeachCoords = beachCoords;
        this.mFriends = new ArrayList<>();
    }

    //for favorite beaches
    public Beach(String beachName, String beachKey, long occupation) {
        BeachName = beachName;
        BeachKey = beachKey;
        CurrentDevices = occupation;

    }

    public ArrayList<Friend> getFriends() {
        return mFriends;
    }

    public ArrayList<LatLng> getBeachCoordinates() {
        return this.BeachCoords;
    }

    public String getBeachName() {
        return BeachName;
    }

    public String getBeachKey() {
        return BeachKey;
    }

    public String getBeachListenerID() {
        return BeachListenerID;
    }

    public long getCurrentDevices() {
        return CurrentDevices;
    }

    public String getTraffic() {
        return mTraffic;
    }

    public String getCountry() {
        return Country;
    }

    @Override
    public String toString() {
        return "Beach{" +
                "BeachName='" + BeachName + '\'' +
                ", BeachKey='" + BeachKey + '\'' +
                ", BeachListenerID='" + BeachListenerID + '\'' +
                ", BeachCoords=" + BeachCoords +
                ", mFriends=" + mFriends +
                ", CurrentDevices=" + CurrentDevices +
                ", mTraffic='" + mTraffic + '\'' +
                '}';
    }
}
