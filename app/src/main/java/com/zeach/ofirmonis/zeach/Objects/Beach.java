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
    private long CurrentPeopleEstimation;
    private ArrayList<Friend> mFriends;
    private long CurrentPeople;
    private int MaxCapacity;

    //Constructor for using service
    public Beach(String beachName, String beachListenerID, ArrayList beachCoords) {
        this.BeachName = beachName;
        this.BeachListenerID = beachListenerID;
        this.BeachCoords = beachCoords;
        this.mFriends = new ArrayList<>();
        this.CurrentPeople = Integer.parseInt(null);
        this.MaxCapacity = Integer.parseInt(null);


    }

    public Beach(String key, String beachListenerID, long currentPeopleEstimation, ArrayList<LatLng> beachCoords, String beachName, long currentPeople, ArrayList<Friend> friends) {
        this.BeachKey = key;
        this.BeachListenerID = beachListenerID;
        this.CurrentPeopleEstimation = currentPeopleEstimation;
        this.BeachCoords = beachCoords;
        this.BeachName = beachName;
        this.CurrentPeople = currentPeople;
        this.mFriends = friends;
    }

    //Constructor for using in app
    public Beach(String beachName, String beachListenerID, ArrayList<LatLng> beachCoords, int currentPeople, int maxCapacity, ArrayList<Friend> friends) {
        this.BeachName = beachName;
        this.BeachListenerID = beachListenerID;
        this.BeachCoords = beachCoords;
        this.CurrentPeople = currentPeople;
        this.MaxCapacity = maxCapacity;
        this.mFriends = new ArrayList<>();
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

    public long getCurrentPeople() {
        return CurrentPeople;
    }

    @Override
    public String toString() {
        return "Beach{" +
                "BeachName='" + BeachName + '\'' +
                ", BeachKey='" + BeachKey + '\'' +
                ", BeachListenerID='" + BeachListenerID + '\'' +
                ", BeachCoords=" + BeachCoords +
                ", CurrentPeopleEstimation=" + CurrentPeopleEstimation +
                ", mFriends=" + mFriends +
                ", CurrentPeople=" + CurrentPeople +
                ", MaxCapacity=" + MaxCapacity +
                '}';
    }
}
