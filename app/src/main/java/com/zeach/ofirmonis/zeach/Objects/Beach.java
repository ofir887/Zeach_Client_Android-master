package com.zeach.ofirmonis.zeach.Objects;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ofirmonis on 23/06/2017.
 */

public class Beach implements Serializable{
    private String BeachName;
    private String BeachListenerID;
    private LatLng BeachCoords[];
    private int CurrentPeopleByGps;
    private HashMap<String,Integer> Hours;
    private HashMap<String,Friend> Friends;
    private int CurrentPeople;
    private int MaxCapacity;

    //Constructor for using service
    public Beach(String beachName,String beachListenerID,LatLng beachCoords[]){
        this.BeachName = beachName;
        this.BeachListenerID = beachListenerID;
        this.BeachCoords = beachCoords;
        this.Hours = new HashMap<>();
        this.Friends = new HashMap<>();
        this.CurrentPeople = Integer.parseInt(null);
        this.MaxCapacity = Integer.parseInt(null);


    }
    //Constructor for using in app
    public Beach(String beachName,String beachListenerID,LatLng beachCoords[],int currentPeople,int maxCapacity,ArrayList<Friend> friends){
        this.BeachName = beachName;
        this.BeachListenerID = beachListenerID;
        this.BeachCoords = beachCoords;
        this.CurrentPeople = currentPeople;
        this.MaxCapacity = maxCapacity;
        this.Friends = new HashMap<>();
        this.Hours = null;
    }
}
