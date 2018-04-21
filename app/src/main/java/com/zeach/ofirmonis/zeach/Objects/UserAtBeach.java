package com.zeach.ofirmonis.zeach.Objects;

import java.io.Serializable;

/**
 * Created by ofirmonis on 12/10/2017.
 */

public class UserAtBeach implements Serializable {
    private String mBeachName;
    private String mBeachID;
    private String mBeachListenerId;
    private String mCountry;
    private long mTimeStamp;
    private double mLatitude;
    private double mLongitude;

    public UserAtBeach() {

    }

    public UserAtBeach(String aBeachName, String aBeachID, String aBeachListenerId, long atimestamp, String aCountry, double aLongitude, double aLatitude) {
        this.mBeachName = aBeachName;
        this.mBeachID = aBeachID;
        this.mBeachListenerId = aBeachListenerId;
        this.mTimeStamp = atimestamp;
        this.mCountry = aCountry;
        mLongitude = aLongitude;
        mLatitude = aLatitude;
    }

    public String getmBeachName() {
        return mBeachName;
    }

    public void setmBeachName(String mBeachName) {
        this.mBeachName = mBeachName;
    }

    public String getmBeachID() {
        return mBeachID;
    }

    public void setmBeachID(String mBeachID) {
        this.mBeachID = mBeachID;
    }

    public long getmTimeStamp() {
        return mTimeStamp;
    }

    public String getmBeachListenerId() {
        return mBeachListenerId;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double mLongitude) {
        this.mLongitude = mLongitude;
    }

    public void setBeachListenerId(String mBeachListenerId) {
        this.mBeachListenerId = mBeachListenerId;
    }

    public String getCountry() {
        return mCountry;
    }

    public void setCountry(String aCountry) {
        mCountry = aCountry;
    }

    public void setmTimeStamp(long mTimeStamp) {
        this.mTimeStamp = mTimeStamp;
    }

    @Override
    public String toString() {
        return "UserAtBeach{" +
                "mBeachName='" + mBeachName + '\'' +
                ", mBeachID='" + mBeachID + '\'' +
                ", mBeachListenerId='" + mBeachListenerId + '\'' +
                ", mCountry='" + mCountry + '\'' +
                ", mTimeStamp=" + mTimeStamp +
                ", mLatitude=" + mLatitude +
                ", mLongitude=" + mLongitude +
                '}';
    }
}
