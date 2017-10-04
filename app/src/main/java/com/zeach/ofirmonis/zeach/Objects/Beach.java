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
    private HashMap<String, Friend> Friends;
    private int CurrentPeople;
    private int MaxCapacity;

    //Constructor for using service
    public Beach(String beachName, String beachListenerID, ArrayList beachCoords) {
        this.BeachName = beachName;
        this.BeachListenerID = beachListenerID;
        this.BeachCoords = beachCoords;
        this.Friends = new HashMap<>();
        this.CurrentPeople = Integer.parseInt(null);
        this.MaxCapacity = Integer.parseInt(null);


    }

    public Beach(String key, String beachListenerID, long currentPeopleEstimation, ArrayList<LatLng> beachCoords, String beachName) {
        this.BeachKey = key;
        this.BeachListenerID = beachListenerID;
        this.CurrentPeopleEstimation = currentPeopleEstimation;
        this.BeachCoords = beachCoords;
        this.BeachName = beachName;
    }

    //Constructor for using in app
    public Beach(String beachName, String beachListenerID, ArrayList<LatLng> beachCoords, int currentPeople, int maxCapacity, ArrayList<Friend> friends) {
        this.BeachName = beachName;
        this.BeachListenerID = beachListenerID;
        this.BeachCoords = beachCoords;
        this.CurrentPeople = currentPeople;
        this.MaxCapacity = maxCapacity;
        this.Friends = new HashMap<>();
    }

    @Override
    public String toString() {
        return "Beach{" +
                "BeachName='" + BeachName + '\'' +
                ", BeachKey='" + BeachKey + '\'' +
                ", BeachListenerID='" + BeachListenerID + '\'' +
                ", BeachCoords=" + BeachCoords +
                ", CurrentPeopleEstimation=" + CurrentPeopleEstimation +
                '}';
    }
}
