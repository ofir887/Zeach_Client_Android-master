package com.zeach.ofirmonis.zeach;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Ofir-M on 07/07/2017.
 */

public class CurrentBeach implements Serializable {
    private String BeachName;
    private long Longitude;
    private long Latitude;
    private Date Date;


    public CurrentBeach(String beachName, long longitude, long latitude, Date date) {
        BeachName = beachName;
        this.Longitude = longitude;
        this.Latitude = latitude;
        this.Date = date;
    }

    public CurrentBeach() {
        BeachName = "Not In any Beach";
        this.Longitude = -1;
        this.Latitude = -1;
        this.Date = null;
    }

    public String getBeachName() {
        return BeachName;
    }

    public void setBeachName(String beachName) {
        BeachName = beachName;
    }

    public long getLongitude() {
        return Longitude;
    }

    public void setLongitude(long longitude) {
        Longitude = longitude;
    }

    public long getLatitude() {
        return Latitude;
    }

    public void setLatitude(long latitude) {
        Latitude = latitude;
    }

    public java.util.Date getDate() {
        return Date;
    }

    public void setDate(java.util.Date date) {
        Date = date;
    }

    @Override
    public String toString() {
        return "CurrentBeach{" +
                "BeachName='" + BeachName + '\'' +
                ", Longitude=" + Longitude +
                ", Latitude=" + Latitude +
                ", Date=" + Date +
                '}';
    }
}
