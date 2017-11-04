package com.zeach.ofirmonis.zeach.Objects;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by ofirmonis on 12/10/2017.
 */

public class UserAtBeach implements Serializable {
    private String mBeachName;
    private String mBeachID;
    private String mBeachListenerId;
    private long mTimeStamp;

    public UserAtBeach() {

    }

    public UserAtBeach(String aBeachName, String aBeachID, String aBeachListenerId, long atimestamp) {
        this.mBeachName = aBeachName;
        this.mBeachID = aBeachID;
        this.mBeachListenerId = aBeachListenerId;
        this.mTimeStamp = atimestamp;
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

    public void setmBeachListenerId(String mBeachListenerId) {
        this.mBeachListenerId = mBeachListenerId;
    }

    @Override
    public String toString() {
        return "UserAtBeach{" +
                "mBeachName='" + mBeachName + '\'' +
                ", mBeachID='" + mBeachID + '\'' +
                ", mBeachListenerId='" + mBeachListenerId + '\'' +
                ", mTimeStamp=" + mTimeStamp +
                '}';
    }
}
